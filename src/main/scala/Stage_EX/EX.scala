/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi

*/

package Stage_EX

import chisel3._
import chisel3.util._
import ALU.ALU
import FW.FW
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
      val controlSignalsEXB  = Input(new ControlSignals)
      val controlSignalsMEMB = Input(new ControlSignals)
      val PC                 = Input(UInt())
      val branchType         = Input(UInt())
      val op1Select          = Input(UInt())
      val op2Select          = Input(UInt())
      val rs1                = Input(UInt())
      val Rs2                = Input(UInt())
      val immData            = Input(UInt())
      val ALUop              = Input(UInt())

      //Forward unit
      val rdEXB              = Input(UInt())
      val ALUresultEXB       = Input(UInt())
      val rdMEMB             = Input(UInt())
      val ALUresultMEMB      = Input(UInt())


      val ALUResult          = Output(UInt())
      val branchAddr         = Output(UInt())
      val branch             = Output(Bool())
      val insertBubble       = Output(Bool())
      val stall             = Output(Bool())
      val Rs1Forwarded       = Output(UInt())
      val Rs2Forwarded       = Output(UInt())
    }
  )

  val ALU          = Module(new ALU).io
  val Branch       = Module(new Branch_OP).io
  val Rs1FW        = Module(new FW).io
  val Rs2FW        = Module(new FW).io

  val insertBubble            = Wire(Bool())
  val alu_operand1            = Wire(UInt())
  val alu_operand_1_forwarded = Wire(UInt())
  val alu_operand2            = Wire(UInt())
  val alu_operand_2_forwarded = Wire(UInt())
  val alu_result              = Wire(UInt())
  val stall_rs1              = Wire(Bool())
  val stall_rs2              = Wire(Bool())



  Branch.branchType := io.branchType
  Branch.src1        := alu_operand_1_forwarded
  Branch.src2        := alu_operand_2_forwarded
  io.branch         := Branch.branchCondition

  Rs1FW.regAddr            := io.instruction.registerRs1
  Rs1FW.controlSignalsEXB  := io.controlSignalsEXB
  Rs1FW.controlSignalsMEMB := io.controlSignalsMEMB
  Rs1FW.regData            := io.rs1
  Rs1FW.rdEXB              := io.rdEXB
  Rs1FW.ALUresultEXB       := io.ALUresultEXB
  Rs1FW.rdMEMB             := io.rdMEMB
  Rs1FW.ALUresultMEMB      := io.ALUresultMEMB
  alu_operand_1_forwarded         := Rs1FW.operandData
  stall_rs1                      := Rs1FW.stall

  Rs2FW.regAddr            := io.instruction.registerRs2
  Rs2FW.controlSignalsEXB  := io.controlSignalsEXB
  Rs2FW.controlSignalsMEMB := io.controlSignalsMEMB
  Rs2FW.regData            := io.Rs2
  Rs2FW.rdEXB              := io.rdEXB
  Rs2FW.ALUresultEXB       := io.ALUresultEXB
  Rs2FW.rdMEMB             := io.rdMEMB
  Rs2FW.ALUresultMEMB      := io.ALUresultMEMB
  alu_operand_2_forwarded         := Rs2FW.operandData
  stall_rs2                      := Rs2FW.stall

  //stall signal to IDBarrier and EXBarrier
  io.stall := stall_rs1 | stall_rs2

  //Do not insert a bubble when stall
  when((stall_rs1 | stall_rs2) === false.B){
    insertBubble  := io.controlSignals.jump | (io.controlSignals.branch & Branch.branchCondition  === 1.U)
  }.otherwise{
    insertBubble  := false.B
  }
  io.insertBubble := insertBubble

  //Operand 1 Mux
  when(io.op1Select === op1sel.PC){
    alu_operand1    := io.PC
  }.otherwise{
    alu_operand1    := alu_operand_1_forwarded
  }

  //Operand 2 Mux
  when(io.op2Select === op2sel.rs2){
    alu_operand2    := alu_operand_2_forwarded
  }.otherwise{
    alu_operand2    := io.immData
  }

  //output forwarded operands
  io.Rs1Forwarded := alu_operand_1_forwarded
  io.Rs2Forwarded := alu_operand_2_forwarded

  //ALU
  ALU.src1           :=alu_operand1
  ALU.src2           :=alu_operand2
  ALU.ALUop         :=io.ALUop
  alu_result        := ALU.aluRes


  io.branchAddr := alu_result


  // ALU RESULT / PC + 4 MUX
  when(io.branchType === branch_types.jump){
    io.ALUResult := io.PC + 4.U
  }.otherwise{
    io.ALUResult := alu_result
  }
}

