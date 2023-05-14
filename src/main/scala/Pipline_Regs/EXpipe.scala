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
import config.{ControlSignals, ControlSignalsOB}
import config.ControlSignalsOB._

class EXpipe extends Module
{
  val io = IO(
    new Bundle {
      val inBranchAddr      = Input(UInt(32.W))
      val inBranch          = Input(UInt(1.W))
      val inControlSignals  = Input(new ControlSignals)
      val inRd              = Input(UInt(5.W))
      val inRs2             = Input(UInt(32.W))
      val inALUResult       = Input(UInt(32.W))
      val inInsertBubble    = Input(Bool())

      val stall            = Input(Bool())

      val outBranchAddr     = Output(UInt())
      val outBranch         = Output(UInt())
      val outALUResult      = Output(UInt())
      val outControlSignals = Output(new ControlSignals)
      val outRd             = Output(UInt())
      val outRs2            = Output(UInt())
      val outInsertBubble   = Output(Bool())
    }
  )

  val branchAddrReg     = RegEnable(io.inBranchAddr, 0.U, !io.stall)
  val branchReg         = RegEnable(io.inBranch, 0.U, !io.stall)
  val ALUResultReg      = RegEnable(io.inALUResult, 0.U, !io.stall) // should not be frozen?
  val controlSignalsReg = RegEnable(io.inControlSignals, !io.stall)
  val rdReg             = RegEnable(io.inRd, 0.U, !io.stall)
  val rs2Reg            = RegEnable(io.inRs2, 0.U, !io.stall)
  val insertBubbleReg   = RegEnable(io.inInsertBubble, false.B, !io.stall)

  val stallReg         = RegEnable(io.stall, false.B, true.B)

  io.outBranchAddr       := branchAddrReg

  io.outBranch           := branchReg


  when(io.stall){
    controlSignalsReg := ControlSignalsOB.nop
  }

  io.outControlSignals := controlSignalsReg

  io.outInsertBubble := insertBubbleReg


  //destination register
  io.outRd               := rdReg

  //reg B register
  io.outRs2              := rs2Reg

  //ALU result register
  io.outALUResult        := ALUResultReg
}
