/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

*/

package Stage_MEM

import DCache.CacheAndMemory
import DataMemory.DataMemory
import chisel3._
import chisel3.util._
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemorySynthInit
import config.{DMEMsetupSignals, MemUpdates}
class MEM(DataFile: String) extends Module {
  val testHarness = IO(
    new Bundle {
      val DMEMsetup      = Input(new DMEMsetupSignals)
      val DMEMpeek       = Output(UInt(32.W))

      val testUpdates    = Output(new MemUpdates)
    })

  val io = IO(
    new Bundle {
      val dataIn      = Input(UInt())
      val dataAddress = Input(UInt(32.W))
      val writeEnable = Input(Bool())
      val readEnable  = Input(Bool())
      val dataOut     = Output(UInt())
      val dataValid   = Output(Bool())
      val memBusy     = Output(Bool())
    })


  //val DMEM = Module(new DataMemory())
  val DMEM = Module(new CacheAndMemory())

  //DMEM.testHarness.setup  := testHarness.DMEMsetup
  testHarness.DMEMpeek    := DMEM.io.data_out
  testHarness.testUpdates := 0.U.asTypeOf(new MemUpdates) //DMEM.testHarness.testUpdates

  //DMEM
  DMEM.io.write_data  := io.dataIn
  DMEM.io.address     := io.dataAddress
  DMEM.io.write_en    := io.writeEnable
  DMEM.io.read_en     := io.readEnable
  //Read data from DMEM
  io.dataOut          := DMEM.io.data_out
  io.dataValid        := DMEM.io.valid
  io.memBusy          := DMEM.io.busy

}
