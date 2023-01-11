package Piplined_RISC_V
import chisel3._
import chisel3.util._
import config.{Instruction, ControlSignals}
class IDpipe extends Module
{
  val io = IO(
    new Bundle {
      //Input to registes - decoder signals
      val inInstruction     = Input(new Instruction)
      val inControlSignals  = Input(new ControlSignals)
      val inPC              = Input(UInt(32.W))
      val inBranchType      = Input(UInt(3.W))
      val inInsertBubble    = Input(UInt())
      val inOp1Select       = Input(UInt(1.W))
      val inOp2Select       = Input(UInt(1.W))
      val inImmData         = Input(UInt(32.W))
      val inRd              = Input(UInt(5.W))
      val inALUop           = Input(UInt(4.W))

      //Output from register - decoder signals
      val outInstruction    = Output(new Instruction)
      val outControlSignals = Output(new ControlSignals)
      val outPC             = Output(UInt(32.W))
      val outBranchType     = Output(UInt(3.W))
      val outOp1Select      = Output(UInt(1.W))
      val outOp2Select      = Output(UInt(1.W))
      val outImmData        = Output(UInt(32.W))
      val outRd             = Output(UInt(5.W))
      val outALUop          = Output(UInt(4.W))

      //Input to register - registers signals
      val inReadData1       = Input(UInt(32.W))
      val inReadData2       = Input(UInt(32.W))

      val freeze            = Input(Bool())

      //Output from register - registers signals
      val outReadData1      = Output(UInt(32.W))
      val outReadData2      = Output(UInt(32.W))
    }
  )

  //Decoder signal registers
  val instructionReg        = RegEnable(io.inInstruction, !io.freeze)
  val controlSignalsReg     = RegEnable(io.inControlSignals, !io.freeze)
  val branchTypeReg         = RegEnable(io.inBranchType, 0.U, !io.freeze)
  val PCReg                 = RegEnable(io.inPC, 0.U, !io.freeze)
  val op1SelectReg          = RegEnable(io.inOp1Select, 0.U, !io.freeze)
  val op2SelectReg          = RegEnable(io.inOp2Select, 0.U, !io.freeze)
  val immDataReg            = RegEnable(io.inImmData, 0.U, !io.freeze)
  val rdReg                 = RegEnable(io.inRd, 0.U, !io.freeze)
  val ALUopReg              = RegEnable(io.inALUop, 0.U, !io.freeze)
  //Register signal registers
  val readData1Reg          = RegEnable(io.inReadData1, 0.U, !io.freeze)
  val readData2Reg          = RegEnable(io.inReadData2, 0.U, !io.freeze)

  val insertBubbleReg       = RegEnable(io.inInsertBubble, 0.U, !io.freeze)

  //Bubble instruction for two cycles
  when(io.inInsertBubble === 1.U | insertBubbleReg === 1.U){
    instructionReg    := Instruction.NOP
  }

  //Bubble control signals for two cycles
  when(io.inInsertBubble === 1.U | insertBubbleReg === 1.U){
    controlSignalsReg := ControlSignals.nop
  }

  io.outInstruction    := instructionReg

  io.outControlSignals := controlSignalsReg

  io.outBranchType     := branchTypeReg

  io.outPC             := PCReg

  io.outOp1Select      := op1SelectReg

  io.outOp2Select      := op2SelectReg

  io.outImmData        := immDataReg

  io.outRd             := rdReg

  io.outALUop          := ALUopReg


  //Register signals registers
  io.outReadData1      := readData1Reg

  io.outReadData2      := readData2Reg
}
