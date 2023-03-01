package DataMemory

import chisel3._
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemorySynthInit
import config.DMEMsetupSignals
import config.MemUpdates
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemorySynthInit

class DataMemory(I_memoryFile: String = "src/main/scala/DataMemory/dataMemVals") extends Module
{
  val testHarness = IO(
    new Bundle {
      val setup = Input(new DMEMsetupSignals)
      val testUpdates = Output(new MemUpdates)
    })


  val io = IO(
    new Bundle {
      val writeEnable = Input(Bool())
      val dataIn      = Input(UInt(32.W))
      val dataAddress = Input(UInt(12.W))

      val dataOut     = Output(UInt(32.W))
    })

  annotate(new ChiselAnnotation {
    override def toFirrtl = MemorySynthInit
  })

  val d_memory = SyncReadMem(4096, UInt(32.W))
  loadMemoryFromFileInline(d_memory,I_memoryFile)

  val addressSource = Wire(UInt(32.W))
  val dataSource = Wire(UInt(32.W))
  val writeEnableSource = Wire(Bool())

  // For loading data
  when(testHarness.setup.setup){
    addressSource     := testHarness.setup.dataAddress
    dataSource        := testHarness.setup.dataIn
    writeEnableSource := testHarness.setup.writeEnable
  }.otherwise {
    addressSource     := io.dataAddress
    dataSource        := io.dataIn
    writeEnableSource := io.writeEnable
  }

  testHarness.testUpdates.writeEnable  := writeEnableSource
  testHarness.testUpdates.writeData    := dataSource
  testHarness.testUpdates.writeAddress := addressSource


  io.dataOut := d_memory(addressSource)
  when(writeEnableSource){
    d_memory(addressSource) := dataSource
  }
}

