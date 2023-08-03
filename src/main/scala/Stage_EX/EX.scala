/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

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
import config.ImmFormat._
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
      val btbHit             = Input(Bool())
      val newBranch          = Output(Bool())
      val updatePrediction   = Output(Bool())
      val outPCplus4         = Output(UInt(32.W))
      val ALUResult          = Output(UInt(32.W))
      val branchTarget       = Output(UInt(32.W))
      val branchCond         = Output(Bool())
      val Rs2Forwarded       = Output(UInt(32.W))

      val immType            = Input(UInt(3.W))

    }
  )

  val ALU          = Module(new ALU).io
  val MDU          = Module(new MDU).io
  val Branch       = Module(new Branch_OP).io


  val mdu_op_flag             = Wire(Bool())
  val mdu_exception_flag      = Wire(Bool())

  val alu_operand1            = Wire(UInt())
  val alu_operand2            = Wire(UInt())
  val PCplus4 = Wire(UInt(32.W))
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
    alu_operand1  := io.rs1
    Branch.src1   := io.rs1
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
    alu_operand2  := io.rs2
    Branch.src2   := io.rs2
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

  // when(io.immType === UTYPE){
  //     ALU.src1    := io.immData
  //     ALU.src2    := 12.asUInt(32.W)
  // }
  //MDU
  MDU.src1           := alu_operand1
  MDU.src2           := alu_operand2
  MDU.MDUop          := io.ALUop
  mdu_op_flag        := MDU.MDUopflag
  mdu_exception_flag := MDU.MDUexceptionflag   //TODO: Flag that will be used in the future for exception handling


  // EX stage outputs
  io.branchCond   := Branch.branchCondition
  io.branchTarget := ALU.aluRes
  io.Rs2Forwarded := alu_operand2
  // ALU RESULT / PC + 4 MUX
  PCplus4 := io.PC + 4.U
  when(io.branchType === branch_types.jump){  // This is for jal, we need to place PC+4 into ra register -- Alternatively, H&H propagates PC+4 from IF stage
    io.ALUResult := PCplus4
  }.otherwise{
    io.ALUResult := Mux(mdu_op_flag, MDU.MDURes, ALU.aluRes) //MUX to choose the value either from ALU or MDU
  }

  // BTB-related: Finding new Branch Instructions and Updating Existing Prediction
  when(io.branchType =/= branch_types.DC){ // In case instruction is a valid branch (valid means not flushed)
    when(!io.btbHit){ // In case of BTB miss, send this as new BTB entry to IF stage
      io.newBranch := 1.B  // Update BTB! -> Tells IF to take io.branchTarget as entryBrTarget AND take IDBarrier.io.outPC as entryPC
      io.updatePrediction := 0.B
    }.otherwise{ // In case of BTB hit (we already know this branch), tell IF to change prediction FSM
      io.newBranch := 0.B
      io.updatePrediction := 1.B
    }
  }.otherwise{
    io.newBranch := 0.B
    io.updatePrediction := 0.B
  }
  io.outPCplus4 := PCplus4
}

