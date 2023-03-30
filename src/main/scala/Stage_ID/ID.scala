package Stage_ID
import chisel3._
import chisel3.util._
import GPR.registerFile
import GPR.ByPassReg
import Decoder.Decoder
import config.ImmFormat._

import config.{RegisterSetupSignals, RegisterUpdates, Instruction, ControlSignals}
class ID extends Module
{
  val testHarness = IO(
    new Bundle {
      val registerSetup = Input(new RegisterSetupSignals)
      val registerPeek  = Output(UInt(32.W))

      val testUpdates   = Output(new RegisterUpdates)
    })


  val io = IO(
    new Bundle {
      val instruction          = Input(new Instruction)
      val registerWriteAddress = Input(UInt())
      val registerWriteData    = Input(UInt())
      val registerWriteEnable  = Input(Bool())

      val controlSignals       = Output(new ControlSignals)
      val branchType           = Output(UInt(3.W))
      val op1Select            = Output(UInt(1.W))
      val op2Select            = Output(UInt(1.W))
      val immType              = Output(UInt(3.W))
      val immData              = Output(UInt())
      val ALUop                = Output(UInt(4.W))

      val readData1            = Output(UInt())
      val readData2            = Output(UInt())
    }
  )

  val registers = Module(new registerFile)
  val decoder   = Module(new Decoder).io
  val bypassRs1 = Module(new ByPassReg).io
  val bypassRs2 = Module(new ByPassReg).io

  val immData   = Wire(SInt())

  registers.testHarness.setup := testHarness.registerSetup
  testHarness.registerPeek    := registers.io.readData1
  testHarness.testUpdates     := registers.testHarness.testUpdates


  //Connect instruction to decoder
  decoder.instruction := io.instruction

  //Connect decoded signals to outputs
  io.controlSignals := decoder.controlSignals
  io.branchType     := decoder.branchType
  io.op1Select      := decoder.op1Select
  io.op2Select      := decoder.op2Select
  io.immType        := decoder.immType
  io.ALUop          := decoder.ALUop

  //From instruction
  registers.io.readAddress1 := io.instruction.registerRs1
  registers.io.readAddress2 := io.instruction.registerRs2

  //From EXpipeline register
  registers.io.writeEnable  := io.registerWriteEnable
  registers.io.writeAddress := io.registerWriteAddress
  registers.io.writeData    := io.registerWriteData

  //To IDpipeline register
  // Bypass muxes, IF reading register which is currently being written
  // send the write value to IDpipeline register instead of current register value
  bypassRs1.readAddr     := io.instruction.registerRs1
  bypassRs1.writeAddr    := io.registerWriteAddress
  bypassRs1.writeEnable  := io.registerWriteEnable
  bypassRs1.registerData := registers.io.readData1
  bypassRs1.writeData    := io.registerWriteData
  io.readData1           := bypassRs1.outData

  bypassRs2.readAddr     := io.instruction.registerRs2
  bypassRs2.writeAddr    := io.registerWriteAddress
  bypassRs2.writeEnable  := io.registerWriteEnable
  bypassRs2.registerData := registers.io.readData2
  bypassRs2.writeData    := io.registerWriteData
  io.readData2           := bypassRs2.outData


  //Create alu operations map for Immediate value lookup
  val ImmOpMap = Array(
    //Key,       Value
    ITYPE -> io.instruction.immediateIType,
    STYPE -> io.instruction.immediateSType,
    BTYPE -> io.instruction.immediateBType,
    UTYPE -> io.instruction.immediateUType,
    JTYPE -> io.instruction.immediateJType,
    SHAMT -> io.instruction.immediateZType,
    DC    -> io.instruction.immediateIType
  )

  //Set immData
  immData := MuxLookup(io.immType, 0.S(32.W), ImmOpMap)

  //Sign extend immdata
  io.immData := Cat(Fill(16, immData(15)), immData(15,0)).asUInt
}
