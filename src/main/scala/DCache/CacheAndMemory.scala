package DCache

import DataMemory.DataMemory
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
      val ready = Output(Bool())
      val data_out = Output(UInt(32.W))
    }
  )
  val data_mem  = Module(new DataMemory)
  val dcache  = Module(new DCache("src/main/scala/DCache/CacheContent.bin"))

  data_mem.testHarness.setup.setup := 0.B
  data_mem.testHarness.setup.dataIn := 0.U
  data_mem.testHarness.setup.dataAddress := 0.U
  data_mem.testHarness.setup.readEnable := 0.B
  data_mem.testHarness.setup.writeEnable := 0.B

  dcache.io.data_in := io.write_data
  dcache.io.data_addr := io.address
  dcache.io.write_en := io.write_en
  dcache.io.read_en := io.read_en
  io.ready := dcache.io.ready
  io.data_out := dcache.io.data_out

  data_mem.io.writeEnable := dcache.io.mem_write_en
  data_mem.io.readEnable := dcache.io.mem_read_en
  data_mem.io.dataIn := dcache.io.mem_data_in
  data_mem.io.dataAddress := dcache.io.mem_data_addr / 4.U
  dcache.io.mem_data_out := data_mem.io.dataOut
}
