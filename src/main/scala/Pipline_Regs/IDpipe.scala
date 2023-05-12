/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava

*/

package Piplined_RISC_V

import chisel3._
import chisel3.util._
import config.{ControlSignalsOB, Inst}
import config.Inst._
//import config.ControlSignals._
import config.{Instruction, ControlSignals, branch_types, ALUOps}
class IDpipe extends Module
{
  val io = IO(
    new Bundle {
      //Input to registes - decoder signals
      val inInstruction     = Input(new Instruction)
      val inControlSignals  = Input(new ControlSignals)
      val inPC              = Input(UInt(32.W))
      val inBranchType      = Input(UInt(3.W))
      val inOp1Select       = Input(UInt(1.W))
      val inOp2Select       = Input(UInt(1.W))
      val inImmData         = Input(UInt(32.W))
      val inRd              = Input(UInt(5.W))
      val inALUop           = Input(UInt(5.W))

      //Output from register - decoder signals
      val outInstruction    = Output(new Instruction)
      val outControlSignals = Output(new ControlSignals)
      val outPC             = Output(UInt(32.W))
      val outBranchType     = Output(UInt(3.W))
      val outOp1Select      = Output(UInt(1.W))
      val outOp2Select      = Output(UInt(1.W))
      val outImmData        = Output(UInt(32.W))
      val outRd             = Output(UInt(5.W))
      val outALUop          = Output(UInt(5.W))

      //Input to register - registers signals
      val inReadData1       = Input(UInt(32.W))
      val inReadData2       = Input(UInt(32.W))

      val flush            = Input(Bool())

      //Output from register - registers signals
      val outReadData1      = Output(UInt(32.W))
      val outReadData2      = Output(UInt(32.W))
    }
  )

  //Decoder signal registers
  val instructionReg        = RegEnable(io.inInstruction, true.B)
  val controlSignalsReg     = RegEnable(io.inControlSignals, true.B)
  val branchTypeReg         = RegEnable(io.inBranchType, 0.U, true.B)
  val PCReg                 = RegEnable(io.inPC, 0.U, true.B)
  val op1SelectReg          = RegEnable(io.inOp1Select, 0.U, true.B)
  val op2SelectReg          = RegEnable(io.inOp2Select, 0.U, true.B)
  val immDataReg            = RegEnable(io.inImmData, 0.U, true.B)
  val rdReg                 = RegEnable(io.inRd, 0.U, true.B)
  val ALUopReg              = RegEnable(io.inALUop, 0.U, true.B)
  //Register signal registers
  val readData1Reg          = RegEnable(io.inReadData1, 0.U, true.B)
  val readData2Reg          = RegEnable(io.inReadData2, 0.U, true.B)

  //Flush
  when(io.flush === 1.U){
    instructionReg    := Inst.NOP
    controlSignalsReg := ControlSignalsOB.nop
    ALUopReg          := ALUOps.DC
    branchTypeReg     := branch_types.DC
    rdReg             := 0.U
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
