/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

*/

package DataMemory

import chisel3._
import chisel3.experimental.{ChiselAnnotation, annotate}
import firrtl.annotations.MemorySynthInit
import config.DMEMsetupSignals
import config.MemUpdates
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemorySynthInit

class DataMemory(I_memoryFile: String = "src/main/scala/DataMemory/dataMemVals") extends Module //default constructor
{
  val testHarness = IO(
    new Bundle {
      val setup = Input(new DMEMsetupSignals)
      val testUpdates = Output(new MemUpdates)
    })


  val io = IO(
    new Bundle {
      val writeEnable = Input(Bool())
      val readEnable  = Input(Bool())
      val dataIn      = Input(UInt(32.W))
      val dataAddress = Input(UInt(32.W))

      val dataOut     = Output(UInt(32.W))
    })

  annotate(new ChiselAnnotation {
    override def toFirrtl = MemorySynthInit
  })

  // val d_memory = SyncReadMem(4096, UInt(32.W))  //changed to 16,384
  // val d_memory = SyncReadMem(16384, UInt(32.W))  //changed to 16,384
  val d_memory = SyncReadMem(1048576, UInt(32.W))  //changed to   524288
  
  loadMemoryFromFileInline(d_memory,I_memoryFile)

  val addressSource = Wire(UInt(32.W))
  val dataSource = Wire(UInt(32.W))
  val writeEnableSource = Wire(Bool())
  val readEnableSource = Wire(Bool())

  // For loading data
  when(testHarness.setup.setup){
    addressSource     := testHarness.setup.dataAddress
    dataSource        := testHarness.setup.dataIn
    writeEnableSource := testHarness.setup.writeEnable
    readEnableSource  := testHarness.setup.readEnable
  }.otherwise {
    addressSource     := io.dataAddress
    dataSource        := io.dataIn
    writeEnableSource := io.writeEnable
    readEnableSource  := io.readEnable
  }

  testHarness.testUpdates.writeEnable  := writeEnableSource
  testHarness.testUpdates.readEnable   := readEnableSource
  testHarness.testUpdates.writeData    := dataSource
  testHarness.testUpdates.writeAddress := addressSource

  when(writeEnableSource){
    d_memory(addressSource) := dataSource
  }

  // io.dataOut := Mux(readEnableSource, d_memory(addressSource), 0.U(32.W))
  io.dataOut := d_memory(addressSource)

}

