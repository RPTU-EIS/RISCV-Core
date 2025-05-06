package DCache

import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.util.experimental._
import firrtl.annotations.MemoryLoadFileType

class Cache (CacheFile: String, read_only: Boolean = false) extends Module{
  val io = IO(new Bundle {
    val write_en = if (!read_only) Some(Input(Bool())) else None
    val read_en = Input(Bool())
    val data_addr = Input(UInt(32.W))
    val data_in = if (!read_only) Some(Input(UInt(32.W))) else None
    val data_out = Output(UInt(32.W))
    val valid = Output(Bool())
    val busy = Output(Bool())

    val mem_write_en = Output(Bool())
    val mem_read_en = Output(Bool())
    val mem_data_in = Output(UInt(32.W))
    val mem_data_addr = Output(UInt(32.W))
    val mem_data_out = Input(UInt(32.W))

    //!added for  prefetcher
    val hit = Input(Bool()) //is there a hit in a buffer
    val prefData = Input(UInt(32.W)) //output from buffer
    val miss = Output(Bool())
  })
  val scalaReadOnlyBool = if(read_only) true.B else false.B
  val write_en_reg = RegInit(false.B)
  val read_en_reg = RegInit(false.B)
  val data_addr_reg = Reg(UInt(32.W))
  val data_in_reg = if (!read_only) Some(Reg(UInt(32.W))) else None

  val cacheLines = 64.U // cache lines as a variable
  val idle :: compare :: writeback :: allocate :: prefHit:: Nil = Enum(5) //!added prefHit
  val stateReg = RegInit(idle)
  val index = Reg(UInt(6.W)) // stores the current cache index in a register to use in later states
  val data_element = Reg(UInt(58.W)) // stores the loaded cache element in a register to use in later states
  val data_element_wire = WireInit(0.asUInt(58.W)) // stores the loaded cache element in a wire to use in the same state
  val statecount = Reg(Bool()) // waiting for 1 cycle in allocate state

  val cache_data_array = Mem(64, UInt(58.W))  // give here 64 as a variable
  loadMemoryFromFileInline(cache_data_array, CacheFile, MemoryLoadFileType.Binary)

  io.data_out := 0.U
  io.valid := 0.B
  io.busy := (stateReg =/= idle)
  io.miss := false.B//!

  io.mem_write_en := 0.B
  io.mem_read_en := 0.B
  io.mem_data_in := 0.U
  io.mem_data_addr := 0.U

  switch(stateReg) {
    is(idle) {
      //printf(p"idle state\n")
      io.data_out := data_element(31, 0)
      when(io.read_en || io.write_en.getOrElse(false.B)) {
        io.miss := true.B //!
        stateReg := compare
        write_en_reg := io.write_en.getOrElse(false.B)
        read_en_reg := io.read_en
        data_addr_reg := io.data_addr
        if (!read_only) data_in_reg.foreach(_ := io.data_in.get)
        statecount := false.B
      }
    }

    is(compare) {
      //printf(p"compare state\n")
      index := (data_addr_reg / 4.U) % cacheLines
      data_element_wire := cache_data_array((data_addr_reg / 4.U) % cacheLines).asUInt
      data_element := data_element_wire

      when(data_element_wire(57) && (data_element_wire(55, 32).asUInt === data_addr_reg(31, 8).asUInt)) {
        stateReg := idle
        io.valid := true.B
        when(read_en_reg) {
          io.data_out := data_element_wire(31, 0)
        }
        if(!read_only) {
          when(write_en_reg) {
            val temp = Wire(Vec(58, Bool()))
            temp := 0.U(58.W).asBools
            temp(57) := true.B
            temp(56) := true.B // set dirty bit

            for (i <- 0 until 32) { temp(i) := data_in_reg.get(i) } // new data is stored
            for (i <- 32 until 56) { temp(i) := data_element_wire(i) } // the tag remains the same
            cache_data_array(index) := temp.asUInt
          }
        }
      }.otherwise {
        when(io.hit){
            //printf(p"cache pref hit ${io.data_addr}\n")
            io.valid := true.B
            io.data_out := io.prefData
            if(!read_only) { //TODO busy signal? // stall?? // only have  output in next state?
              stateReg := prefHit
            }
            else{
              stateReg := idle
            }
        }.otherwise{
          if(!read_only) {
            when(data_element_wire(56) && data_element_wire(57)) {
              stateReg := writeback
            }.otherwise {
              stateReg := allocate
            }
          }
          else {
            stateReg := allocate
          }
        }
      }
    }

    is(writeback) {
      //printf(p"writeback state\n")
      io.mem_write_en := true.B
      io.mem_read_en := false.B
      val temp = Wire(Vec(32, Bool()))
      temp := 0.U(32.W).asBools // the address where the dirty cache element should be stored in memory
      temp(1) := false.B
      temp(0) := false.B // first two bits 0 as byte offset
      for (i <- 2 until 8) { temp(i) := index.asBools(i - 2) } // next 6 bits from index
      for (i <- 8 until 32) { temp(i) := data_element(i + 24) } // the rest 24 bits from the tag
      io.mem_data_addr := temp.asUInt
      io.mem_data_in := data_element(31, 0) // write the data in the dirty element to the memory
      stateReg := allocate
    }

    is(allocate) {
      //printf(p"allocate state\n")
      when(statecount) {
        statecount := false.B
        io.mem_read_en := false.B
        val temp = Wire(Vec(58, Bool()))
        temp := 0.U(58.W).asBools
        for (i <- 0 until 32) { temp(i) := io.mem_data_out(i) }
        temp(56) := false.B
        temp(57) := true.B
        for (i <- 32 until 56) { temp(i) := data_addr_reg(i - 24) }
        cache_data_array(index) := temp.asUInt
        stateReg := compare
      }.otherwise {
        statecount := true.B
        io.mem_read_en := true.B
        io.mem_write_en := false.B
        io.mem_data_addr := data_addr_reg
      }
    }
    is(prefHit) {
      //printf(p"prefHit state\n")
      // Write the prefetched data into the cache
      val temp = Wire(Vec(58, Bool()))
      temp := 0.U(58.W).asBools

      // Store the 32-bit data portion
      for (i <- 0 until 32) {
        temp(i) := io.prefData(i)
      }

      // Store the tag from address register
      for (i <- 32 until 56) {
        temp(i) := data_addr_reg(i - 24)
      }

      // Set status bits
      temp(56) := false.B  // not dirty (was prefetched, not written)
      temp(57) := true.B   // valid

      // Store into cache
      cache_data_array(index) := temp.asUInt

      // Transition to the idle state
      stateReg := idle
    }

  }
  //printf(p"hit prefetcher: ${io.hit}, data prefetcher: ${io.prefData}\n")
  //printf(p"\n")
  //printf(p"\n")
}
