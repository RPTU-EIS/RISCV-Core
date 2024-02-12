package DCache

import combined_memory.combined_memory
import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.util.experimental._
import firrtl.annotations.MemoryLoadFileType
class CacheAndMemory extends Module{
  val io = IO(
    new Bundle{
      val write_data = Input(UInt(32.W))
      val address = Input(UInt(32.W))
      val write_en = Input(Bool())
      val read_en = Input(Bool())
      val valid = Output(Bool())
      val data_out = Output(UInt(32.W))
      val busy = Output(Bool())

      val cache_data_in = Output(UInt(32.W))
      val cache_data_addr = Output(UInt(32.W))
      val cache_write_en = Output(Bool())
      val cache_read_en = Output(Bool())
      val cache_valid = Input(Bool())
      val cache_data_out = Input(UInt(32.W))
      val cache_busy = Input(Bool())
    }
  )

  
  //val data_mem  = Module(new combined)
 // val dcache  = Module(new DCache("src/main/scala/DCache/CacheContent.bin"))

  val cache_data_in = Wire(UInt(32.W))
  val cache_data_addr = Wire(UInt(32.W))
  val cache_write_en = Wire(Bool())
  val cache_read_en = Wire(Bool())
  val cache_valid = Wire(Bool())
  val cache_data_out = Wire(UInt(32.W))
  val cache_busy = Wire(Bool())
/*
  data_mem.testHarness.setup.setup := 0.B
  data_mem.testHarness.setup.dataIn := 0.U
  data_mem.testHarness.setup.dataAddress := 0.U
  data_mem.testHarness.setup.readEnable := 0.B
  data_mem.testHarness.setup.writeEnable := 0.B
*/
  //dcache.io.data_in := io.write_data
  cache_data_in := io.write_data
  io.cache_data_in := cache_data_in

  //dcache.io.data_addr := io.address
  cache_data_addr := io.address
  io.cache_data_addr := cache_data_addr

  //dcache.io.write_en := io.write_en
  cache_write_en := io.write_en
  io.cache_write_en := cache_write_en

  //dcache.io.read_en := io.read_en
  cache_read_en := io.read_en
  io.cache_read_en := cache_read_en

  //io.valid := dcache.io.valid
  cache_valid := io.cache_valid
  io.valid := cache_valid
  
  //io.data_out := dcache.io.data_out
  cache_data_out := io.cache_data_out
  io.data_out := cache_data_out

  //io.busy := dcache.io.busy
  cache_busy := io.cache_busy
  io.busy := cache_busy


  //data_mem.io.writeEnable := dcache.io.mem_write_en

  //data_mem.io.readEnable := dcache.io.mem_read_en

  //data_mem.io.dataIn := dcache.io.mem_data_in

  //data_mem.io.dataAddress := dcache.io.mem_data_addr / 4.U

 // dcache.io.mem_data_out := data_mem.io.dataOut
}
