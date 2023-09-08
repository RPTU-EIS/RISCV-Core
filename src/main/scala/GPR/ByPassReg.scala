/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

*/


package GPR

import chisel3._
import chisel3.util._

class ByPassReg extends Module
{
  val io = IO(
    new Bundle {
      val readAddr     = Input(UInt())

      val writeAddr    = Input(UInt())
      val writeEnable  = Input(Bool())

      val registerData = Input(UInt())
      val writeData    = Input(UInt())

      val outData      = Output(UInt())

    }
  )


  when((io.readAddr =/= 0.U) & (io.readAddr === io.writeAddr) & io.writeEnable){
    io.outData := io.writeData
  }.otherwise{
    io.outData := io.registerData
  }
}
