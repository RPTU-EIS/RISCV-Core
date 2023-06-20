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
import config.{ControlSignals}
class MEMpipe extends Module
{
  val io = IO(
    new Bundle {
      //val inPCBranch      = Input(UInt())
      val inControlSignals  = Input(new ControlSignals)
      val inRd              = Input(UInt())
      val inRs2             = Input(UInt())
      val inMEMData         = Input(UInt())
      val inALUResult       = Input(UInt())

      val outMEMData        = Output(UInt())
      val outControlSignals = Output(new ControlSignals)
      val outRd             = Output(UInt())
      val outALUResult      = Output(UInt())
    }
  )

  val ALUResultReg      = RegInit(UInt(), 0.U)
  val controlSignalsReg = Reg(new ControlSignals)
  val rdReg             = RegInit(UInt(), 0.U)

  //Control singals register
  controlSignalsReg    := io.inControlSignals
  io.outControlSignals := controlSignalsReg

  //immediate data register
  rdReg                := io.inRd
  io.outRd             := rdReg

  //MEM data
  io.outMEMData        := io.inMEMData

  //ALU result register
  ALUResultReg         := io.inALUResult
  io.outALUResult      := ALUResultReg

}
