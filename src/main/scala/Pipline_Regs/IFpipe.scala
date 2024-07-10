/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

*/

package Piplined_RISC_V

import chisel3._
import chisel3.util._
import config.{Instruction, Inst}

class IFpipe extends Module
{
  val io = IO(
    new Bundle {
      val inCurrentPC         = Input(UInt(32.W))
      val inInstruction       = Input(new Instruction)
      val stall               = Input(Bool())
      val flush               = Input(Bool())
      val inBTBHit            = Input(Bool())
      val inBTBPrediction     = Input(Bool())
      val inBTBTargetPredict  = Input(UInt(32.W))
      val outBTBHit           = Output(Bool())
      val outBTBPrediction    = Output(Bool())
      val outBTBTargetPredict = Output(UInt(32.W))
      val outCurrentPC        = Output(UInt(32.W))
      val outInstruction      = Output(new Instruction)
    }
  )

  val currentPCReg   = RegEnable(io.inCurrentPC, 0.U, !io.stall)  
  val instructionReg = RegEnable(io.inInstruction, Inst.NOP, !io.stall)  


  val flushDelayed = RegInit(Bool(), 0.U)

  flushDelayed := io.flush // Note: Delay flush signal because io.outInstruction is combinational (because Read iMem is synchronous)

  // Propagate BTB signals
  val btbHitReg        = RegInit(false.B)
  val btbPredictionReg = RegInit(false.B)
  val btbTargetPredict = RegInit(0.U(32.W))

  btbHitReg        := io.inBTBHit
  btbPredictionReg := io.inBTBPrediction
  btbTargetPredict := io.inBTBTargetPredict

  io.outBTBHit           := btbHitReg
  io.outBTBPrediction    := btbPredictionReg
  io.outBTBTargetPredict := btbTargetPredict

  // Flush, Stall, or Propagate Instruction
  when(flushDelayed === 1.U){
    io.outInstruction := Inst.NOP
  }
  .otherwise{
    io.outInstruction := instructionReg
  }

  // Propagate PC
  io.outCurrentPC := currentPCReg   

}
