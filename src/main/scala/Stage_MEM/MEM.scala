package Stage_MEM

import DataMemory.DataMemory
import chisel3._
import chisel3.util._
import DataMemory.DataMemory
import config.{DMEMsetupSignals, MemUpdates}
class MEM extends Module {
  val testHarness = IO(
    new Bundle {
      val DMEMsetup      = Input(new DMEMsetupSignals)
      val DMEMpeek       = Output(UInt(32.W))

      val testUpdates    = Output(new MemUpdates)
    })

  val io = IO(
    new Bundle {
      val dataIn      = Input(UInt())
      val dataAddress = Input(UInt())
      val writeEnable = Input(Bool())

      val dataOut     = Output(UInt())
    })


  val DMEM = Module(new DataMemory)

  DMEM.testHarness.setup  := testHarness.DMEMsetup
  testHarness.DMEMpeek    := DMEM.io.dataOut
  testHarness.testUpdates := DMEM.testHarness.testUpdates

  //DMEM
  DMEM.io.dataIn      := io.dataIn
  DMEM.io.dataAddress := io.dataAddress
  DMEM.io.writeEnable := io.writeEnable

  //Read data from DMEM
  io.dataOut          := DMEM.io.dataOut

}
