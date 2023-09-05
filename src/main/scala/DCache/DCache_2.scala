
package DCache

import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.util.experimental._

// does the same thing as DCache but tries to use bundle for cache contents. Loading from file did not work.

class DCache_2(CacheFile: String ) extends Module {
  val io = IO(
    new Bundle {
      val write_en = Input(Bool())
      val read_en = Input(Bool())
      val data_addr = Input(UInt(32.W))
      val data_in = Input(UInt(32.W))
      val data_out = Output(UInt(32.W))
      val ready = Output(Bool())

      val mem_write_en = Output(Bool())
      val mem_read_en = Output(Bool())
      val mem_data_in = Output(UInt(32.W))
      val mem_data_addr = Output(UInt(32.W))
      val mem_data_out = Input(UInt(32.W))
    }
  )

  val write_en_reg = Reg(Bool())
  val read_en_reg = Reg(Bool())
  val data_addr_reg = Reg(UInt(32.W))
  val data_in_reg = Reg(UInt(32.W))

  object state extends ChiselEnum {
    val idle, compare, writeback, allocate = Value
  }


  class cachecontent extends Bundle{
    val valid = Bool()
    val dirty = Bool()
    val tag = UInt(24.W)
    val data = UInt(32.W)
  }

  import state._

  val stateReg = RegInit(idle)
  val index = Reg(UInt(6.W))
  val data_element = RegInit(0.U.asTypeOf(new cachecontent))
  val data_element_wire = WireInit(0.U.asTypeOf(new cachecontent))
  val statecount = Reg(Bool())

  val temp_mem = Mem(64, UInt(58.W)) // temporary mem to load binary numbers from file
  val cache_data_array = Mem(64, new cachecontent) // bundle memory to assign specific bits from temp_mem to bundles
  loadMemoryFromFileInline(temp_mem,CacheFile)

  val cacheloaded = RegInit(3.U)
  
  when (cacheloaded === 3.U) { // wait for 1 cycle in the beginning
    cacheloaded := 2.U
  }
  when (cacheloaded === 2.U) { // initialize cache_data_array as all 0 at first
    for (i <- 0 until 64) {
      val tempcache = WireInit(1.U.asTypeOf(new cachecontent))
      cache_data_array(i) := tempcache
    }
    cacheloaded := 1.U
  }

  when(cacheloaded === 1.U){ // load bits from temp_mem to the corresponding fields in bundle mem
    for (i <- 0 until 64) {
      val tempcache = WireInit(0.U.asTypeOf(new cachecontent))
      val tempbin = WireInit(temp_mem(i))
      printf(p"$tempbin\n")
      tempcache.data := tempbin(31, 0)
      tempcache.tag := tempbin(55, 32)
      tempcache.dirty := tempbin(56)
      tempcache.valid := tempbin(57)
      cache_data_array(i) := tempcache
      printf(p"$tempcache\n")
    }
    cacheloaded := 0.U
  }

  // the rest is the same as "DCache.scala"

  io.data_out := 0.U
  io.ready := 0.B

  io.mem_write_en := 0.B
  io.mem_read_en := 0.B
  io.mem_data_in := 0.U
  io.mem_data_addr := 0.U

  switch(stateReg) {

    is(idle) {
      printf("idle state\n")
      when(io.write_en || io.read_en) {
        stateReg := compare
        write_en_reg := io.write_en
        read_en_reg := io.read_en
        data_addr_reg := io.data_addr
        data_in_reg := io.data_in
        statecount := 0.B
      }
    }

    is(compare) {
      printf("compare state\n")
      index := (data_addr_reg / 4.U) % 64.U
      data_element_wire := cache_data_array((data_addr_reg / 4.U) % 64.U)
      data_element := data_element_wire
      printf(p"$data_element_wire\n")

      when(data_element_wire.valid && (data_element_wire.tag === data_addr_reg(31, 8).asUInt)) {
        printf("compare hit\n")
        stateReg := idle
        io.ready := 1.B
        when(read_en_reg) {
          io.data_out := data_element_wire.data
        }.elsewhen(write_en_reg) {
          val temp = Wire(new cachecontent)
          temp.valid := 1.B
          temp.dirty := 1.B
          temp.data := data_in_reg
          temp.tag := data_element_wire.tag
          cache_data_array((data_addr_reg / 4.U) % 64.U) := temp
        }
      }.otherwise {
        printf("compare miss\n")
        when(data_element_wire.dirty && data_element_wire.valid) {
          stateReg := writeback
        }.otherwise {
          stateReg := allocate
        }
      }
    }
    is(writeback) {
      printf("writeback state\n")
      io.mem_write_en := 1.U
      io.mem_read_en := 0.U
      val temp = Wire(Vec(32, Bool()))
      temp := 0.U(32.W).asBools
      temp(1) := 0.B
      temp(0) := 0.B
      for (i <- 2 until 8){ temp(i) := index.asBools(i-2) }
      for (i <- 8 until 32){ temp(i) := data_element.tag(i-8) }
      io.mem_data_addr := temp.asUInt
      io.mem_data_in := data_element.data
      stateReg := allocate
    }

    is(allocate) {
      printf("allocate state\n")
      when(statecount) {
        statecount := 0.B
        io.mem_read_en := 0.B
        val temp = WireInit(0.U.asTypeOf(new cachecontent))
        temp.data := io.mem_data_out
        temp.dirty := 0.B
        temp.valid := 1.B
        temp.tag := data_addr_reg(31, 8)
        cache_data_array(index) := temp
        stateReg := compare
      }.otherwise {
        statecount := 1.B
        io.mem_read_en := 1.B
        io.mem_write_en := 0.B
        io.mem_data_addr := data_addr_reg
      }
    }
  }
}
