package DCache

import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.util.experimental._
import firrtl.annotations.MemoryLoadFileType

// this one does not create a bundle for cache elements, instead uses 58-bit UInt. "DCache_2.scala" attepmts uses a bundle

class DCache(CacheFile: String) extends Module {
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

  /*
  class cachecontent extends Bundle{
    val valid = Bool()
    val dirty = Bool()
    val tag = UInt(24.W)
    val data = UInt(32.W)
  }*/

  import state._

  val stateReg = RegInit(idle)
  val index = Reg(UInt(6.W)) // stores the current cache index in a register to use in later states
  val data_element = Reg(UInt(58.W)) // stores the loaded cache element in a register to use in later states
  val data_element_wire = WireInit(0.asUInt(58.W)) // stores the loaded cache element in a wire to use in the same state
  val statecount = Reg(Bool()) // waiting for 1 cycle in allocate state

  val cache_data_array = Mem(64, UInt(58.W))
  loadMemoryFromFileInline(cache_data_array,CacheFile, MemoryLoadFileType.Binary)
  io.data_out := 0.U
  io.ready := 0.B

  io.mem_write_en := 0.B
  io.mem_read_en := 0.B
  io.mem_data_in := 0.U
  io.mem_data_addr := 0.U

  switch(stateReg) {

    is(idle) {
      printf("idle state\n")
      when(io.write_en || io.read_en) { // store the inputs on registers
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
      data_element_wire := cache_data_array((data_addr_reg / 4.U) % 64.U).asUInt
      data_element := data_element_wire
      printf(p"$data_element_wire\n")

      when(data_element_wire(57) && (data_element_wire(55, 32).asUInt === data_addr_reg(31, 8).asUInt)) { // if valid is 1 and tags match
        printf("compare hit\n")
        stateReg := idle
        io.ready := 1.B
        when(read_en_reg) {
          //printf(p"io.data_out\n")
          io.data_out := data_element_wire(31, 0)
        }.elsewhen(write_en_reg) { // if write hit
          val temp = Wire(Vec(58, Bool()))
          temp := 0.U(58.W).asBools
          temp(57) := 1.B
          temp(56) := 1.B // set dirty bit
          for (i <- 0 until 32) { temp(i) := data_in_reg(i) } // new data is stored
          for (i <- 32 until 56) { temp(i) := data_element_wire(i) } // the tag remains the same
          cache_data_array((data_addr_reg / 4.U) % 64.U) := temp.asUInt
        }
      }.otherwise {
        printf("compare miss\n")
        when(data_element_wire(56) && data_element_wire(57)) { // if dirty the go to writeback state
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
      temp := 0.U(32.W).asBools // the address where the dirty cache element should be stored in memory
      temp(1) := 0.B
      temp(0) := 0.B // first two bits 0 as byte offset
      for (i <- 2 until 8){ temp(i) := index.asBools(i-2) } // next 6 bits from index
      for (i <- 8 until 32){ temp(i) := data_element(i+24) } // the rest 24 bits from the tag
      io.mem_data_addr := temp.asUInt
      io.mem_data_in := data_element(31, 0) // write the data in the dirty element to the memory
      stateReg := allocate
    }

    is(allocate) {
      printf("allocate state\n")
      when(statecount) { // 2nd cycle of allocate state: take the value returned from memory and put it on the cache element
        statecount := 0.B
        io.mem_read_en := 0.B
        val temp = Wire(Vec(58, Bool()))
        temp := 0.U(58.W).asBools
        for (i <- 0 until 32){ temp(i) := io.mem_data_out(i) }
        temp(56) := 0.B
        temp(57) := 1.B
        for (i <- 32 until 56){ temp(i) := data_addr_reg(i-24) }
        cache_data_array(index) := temp.asUInt
        stateReg := compare
      }.otherwise { // this is the first cycle of allocate state: make a read request from mem 
        statecount := 1.B
        io.mem_read_en := 1.B
        io.mem_write_en := 0.B
        io.mem_data_addr := data_addr_reg
      }
    }
  }
}
