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
import config.{ControlSignals}
class MEMpipe extends Module
{
  val io = IO(
    new Bundle {
      //val inPCBranch      = Input(UInt())
      val inControlSignals  = Input(new ControlSignals)
      val inRd              = Input(UInt())
      val inMEMData         = Input(UInt())
      val inALUResult       = Input(UInt())
      val stall             = Input(Bool())
      val outMEMData        = Output(UInt())
      val outControlSignals = Output(new ControlSignals)
      val outRd             = Output(UInt())
      val outALUResult      = Output(UInt())
    }
  )

  val ALUResultReg      = RegEnable(io.inALUResult, 0.U, !io.stall) //RegInit(UInt(), 0.U)
  val controlSignalsReg = RegEnable(io.inControlSignals, !io.stall) //Reg(new ControlSignals)
  val rdReg             = RegEnable(io.inRd, 0.U, !io.stall) //RegInit(UInt(), 0.U)

  //Control singals register
  io.outControlSignals := controlSignalsReg

  //immediate data register
  io.outRd             := rdReg

  //MEM data
  io.outMEMData        := io.inMEMData

  //ALU result register
  io.outALUResult      := ALUResultReg

}