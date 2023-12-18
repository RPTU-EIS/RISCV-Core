/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

*/

package ALU

import chisel3._
import chisel3.util._
import config.ALUOps._


class ALU extends Module {

  val io = IO(new Bundle {
    val src1   = Input(UInt(32.W))
    val src2   = Input(UInt(32.W))
    val ALUop  = Input(UInt(32.W))
    val aluRes = Output(UInt(32.W))
  })

  val ALU_SLT  = Wire(UInt(32.W))
  val ALU_SLTU = Wire(UInt(32.W))

  //SLT operation
  when (io.src1.asSInt < io.src2.asSInt){
    ALU_SLT := 1.U
  }.otherwise{
    ALU_SLT := 0.U
  }

  //SLTU operation
  when (io.src1 < io.src2){
    ALU_SLTU:= 1.U
  }.otherwise{
    ALU_SLTU:= 0.U
  }


  val shamt = io.src2(4,0)
  io.aluRes := 0.U


  switch(io.ALUop){
    is(ADD){ io.aluRes := (io.src1 + io.src2)}  // Add
    is(SUB){ io.aluRes := (io.src1 - io.src2)}  // Sub

    is(SLL){ io.aluRes := (io.src1 << shamt)}   // SLL, SLLI
    is(SRL){ io.aluRes := (io.src1 >> shamt)}   // SRL, SRLI
    is(SRA){ io.aluRes := (Fill(32,io.src1(31)) << (31.U(5.W) - (shamt - 1.U(5.W)))) | (io.src1 >> shamt)}  // SRA, SRAI

    is(OR){ io.aluRes := io.src1 | io.src2}     // OR
    is(AND){ io.aluRes := io.src1 & io.src2}    // AND
    is(XOR){ io.aluRes := io.src1 ^ io.src2}    // XOR

    is(SLT){ io.aluRes := ALU_SLT}              // SLT,  SLTI, BLT,
    is(SLTU){ io.aluRes := ALU_SLTU}            // SLTU, SLTIU,BLTU

    is(INC_4){ io.aluRes := io.src1 + 4.U}      // PC increment
    is(COPY_B){ io.aluRes := io.src2}           //Pass B
    is(DC){ io.aluRes := io.src1 - io.src2}

  }

}

