package ICache

import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.util.experimental._
import firrtl.annotations.MemoryLoadFileType

  class ICache (CacheFile: String) extends Module{
  val io = IO(new Bundle {
    val read_en = Input(Bool())
    val data_addr = Input(UInt(32.W))
    val data_out = Output(UInt(32.W))
    val valid = Output(Bool())
    val busy = Output(Bool())

    val mem_read_en = Output(Bool())
    val mem_data_addr = Output(UInt(32.W))
    val mem_data_out = Input(UInt(32.W))

    //val mem_granted = Input(Bool())

    val hit = Input(Bool()) //is there a hit in a buffer
    val prefData = Input(UInt(32.W)) //output from buffer
    val miss = Output(Bool())

    //! Added for Loop_Test_0
    val pcOut = Output(UInt(32.W))
    val flushed = Input(Bool())

  })
  io.pcOut := io.data_addr
  val flushed = RegInit(false.B)
  when(io.flushed){
    flushed := true.B
  }

  val read_en_reg = RegInit(false.B)
  val data_addr_reg = Reg(UInt(32.W))

  val cacheLines = 64.U // cache lines as a variable
  val idle :: allocate :: prefHit:: Nil = Enum(3)
  val stateReg = RegInit(idle)
  val index = Reg(UInt(6.W)) // stores the current cache index in a register to use in later states
  val data_element = Reg(UInt(58.W)) // stores the loaded cache element in a register to use in later states
  val data_element_wire = WireInit(0.asUInt(58.W)) // stores the loaded cache element in a wire to use in the same state
  val statecount = Reg(Bool()) // waiting for 1 cycle in allocate state

  val cache_data_array = Mem(64, UInt(58.W))  // give here 64 as a variable
  loadMemoryFromFileInline(cache_data_array, CacheFile, MemoryLoadFileType.Binary)

  io.data_out := 0.U
  io.valid := 0.B
  io.busy := false.B//(stateReg =/= idle)
  io.miss := false.B

  io.mem_read_en := 0.B
  io.mem_data_addr := 0.U

  val compareWire = Wire(Bool())
  compareWire := false.B
  val read_en_wire = Wire(Bool())
  read_en_wire := false.B
  val data_addr_wire = Wire(UInt(32.W))
  data_addr_wire := 0.U


  val compareReg = RegInit(false.B)

// printf(p"busy: ${io.busy}, valid: ${io.valid}, data_addr: ${io.data_addr}\n")

// printf(p"cache_data_array(0): 0x${Hexadecimal(cache_data_array(0)(31,0))}, cache_data_array(1): 0x${Hexadecimal(cache_data_array(1)(31,0))}\n")
// printf(p"cache_data_array(2): 0x${Hexadecimal(cache_data_array(2)(31,0))},cache_data_array(3): 0x${Hexadecimal(cache_data_array(3)(31,0))},cache_data_array(4): 0x${Hexadecimal(cache_data_array(4)(31,0))}\n")


val prefValue = RegInit(0.U(32.W))
switch(stateReg) {
    
    
    is(idle) {
      // printf(p"CACHE idle\n")
      when(io.read_en) {
        // printf(p"idle read\n")
        io.busy := true.B
        compareWire := true.B
        io.miss := true.B 
        read_en_wire := io.read_en
        data_addr_reg := io.data_addr
        data_addr_wire := io.data_addr

        read_en_reg := read_en_wire
  
        
        statecount := false.B
      }
      when(compareReg){
        data_addr_wire := data_addr_reg
        read_en_wire := read_en_reg
      }
      printf(p"cache compareWire ${compareWire}, compareReg ${compareReg}\n")
      when(compareWire || compareReg){
        // printf(p"idle compare\n")
        io.busy := true.B
        compareReg := false.B
        index := (data_addr_wire / 4.U) % cacheLines
        //TODO
        when(compareReg){
          data_element_wire := cache_data_array(index).asUInt
        }.otherwise{
          data_element_wire := cache_data_array((data_addr_wire / 4.U) % cacheLines).asUInt
        }
        //!data_element_wire := cache_data_array((data_addr_wire / 4.U) % cacheLines).asUInt
        data_element := data_element_wire
        printf(p" data_element_wire: ${data_element_wire},index: ${index},  indexNEW: ${(data_addr_wire / 4.U) % cacheLines}\n")
        printf(p"data_element_wire(57) ${data_element_wire(57)}, data_element_wire(55, 32).asUInt ${data_element_wire(55, 32).asUInt}, data_addr_wire(31, 8).asUInt ${data_addr_wire(31, 8).asUInt}\n")
        when(data_element_wire(57) && (data_element_wire(55, 32).asUInt === data_addr_wire(31, 8).asUInt)) {
          
          stateReg := idle
           // printf(p"idle cache hit data_addr_wire ${data_addr_wire}, data_addr_reg${data_addr_reg}\n")
          io.valid := true.B
          printf(p"idle cache hit io.valid ${io.valid}\n")
          
          io.busy:= false.B
          // when(read_en_wire && compareReg) {
          //   io.data_out := data_element(31, 0)
          //    // printf(p"io.data_out 1111 0x${Hexadecimal(data_element(31, 0))}\n")
          // }.elsewhen{read_en_wire}{
          //   io.data_out := data_element_wire(31, 0)
          //    // printf(p"io.data_out 2222 0x${Hexadecimal(data_element_wire(31, 0))}\n")
          // } //TODO einkommentieren??
          when{read_en_wire}{
            io.data_out := data_element_wire(31, 0)
             // printf(p"io.data_out 2222 0x${Hexadecimal(data_element_wire(31, 0))}\n")
          }
        }.otherwise {
          printf(p"idle cache otherwise io.valid ${io.valid}\n")
          when(io.hit){
            
             // printf(p"idle pref hit\n")
              
              // io.valid := true.B
              // io.data_out := io.prefData
              // stateReg := prefHit
              // io.busy := false.B
              prefValue := io.prefData
              stateReg := prefHit //!
              io.busy := true.B
          }.otherwise{  
             // printf(p"idle allocate\n")
            stateReg := allocate
            io.busy := true.B
          }
        }
      }
    }

    is(allocate) {
       printf(p"CACHE allocate data_addr: ${io.data_addr}\n")
      io.busy := true.B
      when(io.hit) {
        //// printf(p"allocate hit\n")
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
      // printf(p"----------------------------------------------\n")
      // printf(p"----------------------------------------------\n")
      // printf(p"CACHE stored: 0x${Hexadecimal(temp.asUInt()(31, 0))} at ${index}\n")
      // printf(p"io.data_addr to be stored: ${io.data_addr}\n")
      // printf(p"----------------------------------------------\n")
      // printf(p"----------------------------------------------\n")
      // Transition to the idle state
      stateReg := idle
      printf(p" temp.asUInt: ${temp.asUInt}, index: ${index}\n")
        when(flushed){
          data_addr_reg := io.data_addr
          flushed := false.B
          data_element := cache_data_array((io.data_addr / 4.U) % cacheLines).asUInt
          // printf(p"Cache flushed allocate ${io.data_addr}xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n")
        }
      }
      compareReg := true.B //TODO remove??
    }
    is(prefHit) {
      compareReg := true.B //TODO remove??
      io.busy := true.B
       printf(p"CACHE prefHit\n")
    
      // Write the prefetched data into the cache
      val temp = Wire(Vec(58, Bool()))
      temp := 0.U(58.W).asBools

      // Store the 32-bit data portion
      for (i <- 0 until 32) {
        temp(i) := prefValue(i)//!io.prefData(i)
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
      // printf(p"----------------------------------------------\n")
      // printf(p"----------------------------------------------\n")
      // printf(p"CACHE stored from pref: 0x${Hexadecimal(temp.asUInt()(31, 0))} at ${index}\n")
      // printf(p"io.data_addr to be stored: ${io.data_addr}\n")
      // printf(p"----------------------------------------------\n")
      // printf(p"----------------------------------------------\n")
      // Transition to the idle state
      stateReg := idle

      when(flushed){
          data_addr_reg := io.data_addr
          flushed := false.B
          data_element := cache_data_array((io.data_addr / 4.U) % cacheLines).asUInt
           printf(p"Cache flushed prefHit ${io.data_addr}xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n")
        }
    }

  }

}