/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

*/

package RISCV_TOP
import chisel3._
import chisel3.util._
import top_MC.top_MC


class RISCV_TOP(BinaryFile: String = "src/test/programs/beq_test", DataFile: String = "src/main/scala/DataMemory/dataMemVals") extends Module{

  val io = IO(
    new Bundle {

      val PC                     = Output(UInt())
      val setup                  = Input(Bool())

      val IMEMWriteData          = Input(UInt(32.W))
      val IMEMAddr               = Input(UInt(32.W))

      val DMEMWriteData          = Input(UInt(32.W))
      val DMEMAddr               = Input(UInt(32.W))
      val DMEMWriteEnable        = Input(Bool())
      val DMEMReadData           = Output(UInt(32.W))
      val DMEMReadEnable         = Input(Bool())

      val regsWriteData          = Input(UInt(32.W))
      val regsAddr               = Input(UInt(5.W))
      val regsWriteEnable        = Input(Bool())
      val regsReadData           = Output(UInt(32.W))

      val regsDeviceWriteEnable  = Output(Bool())
      val regsDeviceWriteData    = Output(UInt(32.W))
      val regsDeviceWriteAddress    = Output(UInt(5.W))

      val memDeviceWriteEnable   = Output(Bool())
      val memDeviceWriteData     = Output(UInt(32.W))
      val memDeviceWriteAddress     = Output(UInt(32.W))




    })

  val top_MC = Module(new top_MC(BinaryFile, DataFile)).testHarness

  io.PC := top_MC.currentPC

  top_MC.setupSignals.IMEMsignals.address     := io.IMEMAddr
  top_MC.setupSignals.IMEMsignals.instruction := io.IMEMWriteData
  top_MC.setupSignals.IMEMsignals.setup       := io.setup

  top_MC.setupSignals.DMEMsignals.writeEnable := io.DMEMWriteEnable
  top_MC.setupSignals.DMEMsignals.readEnable  := io.DMEMReadEnable
  top_MC.setupSignals.DMEMsignals.dataAddress := io.DMEMAddr
  top_MC.setupSignals.DMEMsignals.dataIn      := io.DMEMWriteData
  top_MC.setupSignals.DMEMsignals.setup       := io.setup

  top_MC.setupSignals.registerSignals.readAddress  := io.regsAddr
  top_MC.setupSignals.registerSignals.writeEnable  := io.regsWriteEnable
  top_MC.setupSignals.registerSignals.writeAddress := io.regsAddr
  top_MC.setupSignals.registerSignals.writeData    := io.regsWriteData
  top_MC.setupSignals.registerSignals.setup        := io.setup

  io.DMEMReadData := top_MC.testReadouts.DMEMread
  io.regsReadData := top_MC.testReadouts.registerRead

  io.regsDeviceWriteAddress := top_MC.regUpdates.writeAddress
  io.regsDeviceWriteEnable  := top_MC.regUpdates.writeEnable
  io.regsDeviceWriteData    := top_MC.regUpdates.writeData

  io.memDeviceWriteAddress  := top_MC.memUpdates.writeAddress
  io.memDeviceWriteEnable   := top_MC.memUpdates.writeEnable
  io.memDeviceWriteData     := top_MC.memUpdates.writeData

}
