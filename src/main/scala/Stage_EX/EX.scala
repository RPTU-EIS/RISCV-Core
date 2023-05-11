/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava

*/

package Stage_EX

import chisel3._
import chisel3.util._
import ALU.ALU
import MDU.MDU
import Branch_OP.Branch_OP
import config.branch_types._
import config.op1sel._
import config.op2sel._
import config.{ControlSignals, Instruction, branch_types, op1sel, op2sel}

class EX extends Module {

  val io = IO(
    new Bundle {
      val instruction        = Input(new Instruction)
      val controlSignals     = Input(new ControlSignals)
      val PC                 = Input(UInt(32.W))
      val branchType         = Input(UInt())
      val op1Select          = Input(UInt())
      val op2Select          = Input(UInt())
      val rs1                = Input(UInt())
      val rs2                = Input(UInt())
      val immData            = Input(UInt())
      val ALUop              = Input(UInt())
      val rs1Select          = Input(UInt(2.W))     //Used to select input to ALU in case of forwarding
      val rs2Select          = Input(UInt(2.W))
      val ALUresultEXB       = Input(UInt(32.W))
      val ALUresultMEMB      = Input(UInt(32.W))
      val ALUResult          = Output(UInt(32.W))
      val branchAddr         = Output(UInt(32.W))
      val branchCond         = Output(Bool())
      val Rs2Forwarded       = Output(UInt(32.W))
    }
  )

  val ALU          = Module(new ALU).io
  val MDU                     = Module(new MDU).io
  val Branch       = Module(new Branch_OP).io


  val mdu_result              = Wire(UInt())
  val mdu_op_flag             = Wire(Bool())
  val mdu_exception_flag      = Wire(Bool())

  val alu_operand1            = Wire(UInt())
  val alu_operand2            = Wire(UInt())

  // Control signals to ALU and Branch
  Branch.branchType := io.branchType
  ALU.ALUop         :=io.ALUop



  Branch.branchType           := io.branchType
  ALU.ALUop         :=io.ALUop

  // Choosing ALU and Branch_Op Inputs  -- 2 consecutive MUXes
    //Forwarded operands -- 1st MUX
  when(io.rs1Select === 1.asUInt(2.W)){
    alu_operand1  := io.ALUresultEXB
    Branch.src1   := io.ALUresultEXB
  }
  .elsewhen(io.rs1Select === 2.asUInt(2.W)){
    alu_operand1  := io.ALUresultMEMB
    Branch.src1   := io.ALUresultMEMB
  }
  .otherwise{
    alu_operand1  := rs1
    Branch.src1   := rs1
  }
  when(io.rs2Select === 1.asUInt(2.W)){
    alu_operand2  := io.ALUresultEXB
    Branch.src2   := io.ALUresultEXB
  }
  .elsewhen(io.rs2Select === 2.asUInt(2.W)){
    alu_operand2  := io.ALUresultMEMB
    Branch.src2   := io.ALUresultMEMB
  }
  .otherwise{
    alu_operand2  := rs2
    Branch.src2   := rs2
  }
    //Operand 1, 2nd Mux
  when(io.op1Select === op1sel.PC){
    ALU.src1    := io.PC
  }.otherwise{
    ALU.src1    := alu_operand1
  }
    //Operand 2, 2nd Mux
  when(io.op2Select === op2sel.rs2){
    ALU.src2    := alu_operand2
  }.otherwise{
    ALU.src2    := io.immData
  }


  //MDU
  MDU.src1           := alu_operand1
  MDU.src2           := alu_operand2
  MDU.MDUop          := io.ALUop
  mdu_op_flag        := MDU.MDUopflag
  mdu_exception_flag := MDU.MDUexceptionflag   //TODO: Flag that will be used in the future for exception handling


  // EX stage outputs
  io.branchCond   := Branch.branchCondition
  io.branchAddr   := ALU.aluRes
  io.Rs2Forwarded := alu_operand2
    // ALU RESULT / PC + 4 MUX
  when(io.branchType === branch_types.jump){
    io.ALUResult := io.PC + 4.U
  }.otherwise{
    io.ALUResult := Mux(mdu_op_flag, MDU.MDURes, ALU.aluRes) //MUX to choose the value either from ALU or MDU
  }
}

