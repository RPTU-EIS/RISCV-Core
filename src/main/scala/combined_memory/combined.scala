/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

*/

package combined_memory

import chisel3._
import chisel3.experimental.{ChiselAnnotation, annotate}
import firrtl.annotations.MemorySynthInit
import config.DMEMsetupSignals
//import config.MemUpdates
import config.IMEMsetupSignals
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemorySynthInit

class combined_memory(I_memoryFile: String) extends Module //default constructor
{
//  val testHarness = IO(
//    new Bundle {
//      val setup = Input(new DMEMsetupSignals)
//      //val testUpdates = Output(new MemUpdates)
//      val setupSignals     = Input(new IMEMsetupSignals)
//      val requestedAddress = Output(UInt(32.W))
//    })


  val io = IO(
    new Bundle {
      val writeEnable = Input(Bool())
      val readEnable  = Input(Bool())
      val dataIn      = Input(UInt(32.W))
      val dataAddress = Input(UInt(32.W))

      val dataOut     = Output(UInt(32.W))

      val  instructionAddress= Input (UInt(32.W))
      val instruction        = Output(UInt(32.W)) 
    })

  annotate(new ChiselAnnotation {
    override def toFirrtl = MemorySynthInit
  })

  // val d_memory = SyncReadMem(4096, UInt(32.W))  //changed to 16,384
  // val d_memory = SyncReadMem(16384, UInt(32.W))  //changed to 16,384
  val d_memory = SyncReadMem(1052672, UInt(32.W))  //changed to   524288
  
  loadMemoryFromFileInline(d_memory,I_memoryFile)

  val addressSource = Wire(UInt(32.W))
  val dataSource = Wire(UInt(32.W))
  val writeEnableSource = Wire(Bool())
  val readEnableSource = Wire(Bool())

  val addressSource2 = Wire(UInt(32.W))

  // For loading data
//  when(testHarness.setup.setup){
//     addressSource2 := testHarness.setupSignals.address
//     addressSource     := testHarness.setup.dataAddress
//     dataSource        := testHarness.setup.dataIn
//     writeEnableSource := testHarness.setup.writeEnable
//     readEnableSource  := testHarness.setup.readEnable
//   }.otherwise {
    addressSource     := io.dataAddress
    dataSource        := io.dataIn
    writeEnableSource := io.writeEnable
    readEnableSource  := io.readEnable

    addressSource2 := io.instructionAddress
 // }

 
  // testHarness.testUpdates.writeEnable  := writeEnableSource
  // testHarness.testUpdates.readEnable   := readEnableSource
  // testHarness.testUpdates.writeData    := dataSource
  // testHarness.testUpdates.writeAddress := addressSource

  //  testHarness.requestedAddress := io.instructionAddress
  
  when(writeEnableSource){
    d_memory(addressSource) := dataSource
  }

//  when(testHarness.setupSignals.setup){
//     d_memory(addressSource2) := testHarness.setupSignals.instruction
//   }

  // io.dataOut := Mux(readEnableSource, d_memory(addressSource), 0.U(32.W))
  io.dataOut := d_memory(addressSource)
  io.instruction := d_memory(addressSource2(31,2))
}

