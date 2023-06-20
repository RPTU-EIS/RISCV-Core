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
import config.{Instruction, Inst}
class IFpipe extends Module
{
  val io = IO(
    new Bundle {
      val inCurrentPC     = Input(UInt(32.W))
      val inInstruction   = Input(new Instruction)
      val stall           = Input(Bool())
      val flush           = Input(Bool())
      val outCurrentPC    = Output(UInt(32.W))
      val outInstruction  = Output(new Instruction)
    }
  )

  val currentPCReg   = RegEnable(io.inCurrentPC, 0.U, !io.stall)  
  val prevPC         = WireInit(UInt(), 0.U)
  val instructionReg = RegEnable(io.inInstruction, !io.stall)

  //Flush Pipeline Register
  when(io.flush === 1.U){
    instructionReg  := Inst.NOP
    //currentPCReg    := 0.U(32.W)
  }

  //PC
  io.outCurrentPC := currentPCReg

  //Instruction
  io.outInstruction := instructionReg   

}
