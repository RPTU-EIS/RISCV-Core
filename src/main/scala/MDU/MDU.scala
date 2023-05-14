/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava

*/



//Multiplication/Division unit for RISCV
package MDU

import chisel3._
import chisel3.util._
import config.MDUOps._


class MDU extends Module {

  val io = IO(new Bundle {
    val src1      = Input(UInt())
    val src2      = Input(UInt())
    val MDUop     = Input(UInt())
    val MDURes    = Output(UInt(32.W))
    val MDUopflag = Output(Bool())
  })

  io.MDURes    := 0.U
  io.MDUopflag := true.B

  when (io.MDUop === MUL){
    io.MDURes := io.src1 * io.src2
  }
  .elsewhen (io.MDUop === DIV){
    io.MDURes := io.src1 / io.src2
  }
  .elsewhen (io.MDUop === REM){
    io.MDURes := io.src1 % io.src2
  }
  .otherwise{
    io.MDUopflag := false.B
  }

}