package RISCV_TOP
import chisel3._
import chisel3.util._
import top_MC.top_MC
class RISCV_TOP extends Module{
  val io = IO(
    new Bundle {
      val DMEMWriteData          = Input(UInt(32.W))
      val DMEMAddress            = Input(UInt(32.W))
      val DMEMWriteEnable        = Input(Bool())
      val DMEMReadEnable         = Input(Bool())
      val DMEMReadData           = Output(UInt(32.W))

      val regsWriteData          = Input(UInt(32.W))
      val regsAddress            = Input(UInt(5.W))
      val regsWriteEnable        = Input(Bool())
      val regsReadData           = Output(UInt(32.W))

      val regsDeviceWriteEnable  = Output(Bool())
      val regsDeviceWriteData    = Output(UInt(32.W))
      val regsDeviceWriteAddress = Output(UInt(5.W))

      val memDeviceWriteEnable   = Output(Bool())
      val memDeviceWriteData     = Output(UInt(32.W))
      val memDeviceWriteAddress  = Output(UInt(32.W))

      val IMEMWriteData          = Input(UInt(32.W))
      val IMEMAddress            = Input(UInt(32.W))

      val setup                  = Input(Bool())

      val currentPC              = Output(UInt())
    })

  val top_MC = Module(new top_MC).testHarness

  top_MC.setupSignals.IMEMsignals.address     := io.IMEMAddress
  top_MC.setupSignals.IMEMsignals.instruction := io.IMEMWriteData
  top_MC.setupSignals.IMEMsignals.setup       := io.setup

  top_MC.setupSignals.DMEMsignals.writeEnable := io.DMEMWriteEnable
  top_MC.setupSignals.DMEMsignals.readEnable  := io.DMEMReadEnable
  top_MC.setupSignals.DMEMsignals.dataAddress := io.DMEMAddress
  top_MC.setupSignals.DMEMsignals.dataIn      := io.DMEMWriteData
  top_MC.setupSignals.DMEMsignals.setup       := io.setup

  top_MC.setupSignals.registerSignals.readAddress  := io.regsAddress
  top_MC.setupSignals.registerSignals.writeEnable  := io.regsWriteEnable
  top_MC.setupSignals.registerSignals.writeAddress := io.regsAddress
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

  io.currentPC := top_MC.currentPC
}
