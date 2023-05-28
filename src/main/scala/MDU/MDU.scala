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
    val src1             = Input(UInt())
    val src2             = Input(UInt())
    val MDUop            = Input(UInt())
    val MDURes           = Output(UInt(32.W))
    val MDUopflag        = Output(Bool())
    val MDUexceptionflag = Output(Bool())
  })

  io.MDURes           := 0.U
  io.MDUopflag        := true.B
  io.MDUexceptionflag := false.B   //TODO: Flag that will be used in the future for exception handling 


  val lhs = io.src1.asSInt
  val rhs = io.src2.asSInt
  //If the OP is just MUL we multiply two numbers and store the lower 32 bits into the register
  when (io.MDUop === MUL){
    io.MDURes := (lhs * rhs).asUInt 
  }
  .elsewhen (io.MDUop === DIV){
    when(io.src2 === 0.U){
      io.MDUexceptionflag := true.B
      io.MDURes := "h_8000_0000".U    //return  2,147,483,648 for error
    }.otherwise{
      io.MDURes := (lhs / rhs).asUInt 
    }
  }
  .elsewhen(io.MDUop === DIVU){
    when(io.src2 === 0.U){
      io.MDUexceptionflag := true.B
      io.MDURes := "h_8000_0000".U    //return  2,147,483,648 for error
    }.otherwise{
      io.MDURes := io.src1 / io.src2
    }
  }
  .elsewhen (io.MDUop === REM){
    when(io.src2 === 0.U){
      io.MDUexceptionflag := true.B
      io.MDURes := "h_8000_0000".U    //return  2,147,483,648 for error
    }.otherwise{
      io.MDURes := (lhs % rhs).asUInt 
    }
  }
  .elsewhen (io.MDUop === REMU){
    when(io.src2 === 0.U){
      io.MDUexceptionflag := true.B
      io.MDURes := "h_8000_0000".U    //return  2,147,483,648 for error
    }.otherwise{
    io.MDURes := io.src1 % io.src2
    }
  }
  //if the OP is MULH or MULHSU or MULHU then we multiply the two numbers but we store the upper 32 bits into the register
  .elsewhen (io.MDUop === MULH){ //signed * signed
    io.MDURes := ((lhs * rhs).asUInt >> 32.U)
  } 
  .elsewhen (io.MDUop === MULHU){ //unsigned * unsigned
    io.MDURes := ((io.src1 * io.src2) >> 32.U)
  }
  .elsewhen (io.MDUop === MULHSU){ //signed * unsigned
    io.MDURes := ((lhs * io.src2).asUInt >> 32.U)
  }
  .otherwise{
    io.MDUopflag := false.B
  }
}