/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

*/

package Decode

import chisel3._
import chisel3.util._
import config.lookup._
import config.op1sel._
import config.op2sel._
import config.branch_types._
import config.ImmFormat._
import config.{ALUOps, ControlSignals, ImmFormat, Instruction, branch_types, MDUOps}

class Decode extends Module {

  val io = IO(new Bundle {
    val instruction    = Input(new Instruction)

    val controlSignals = Output(new ControlSignals)
    val branchType     = Output(UInt(3.W))
    val op1Select      = Output(UInt(1.W))
    val op2Select      = Output(UInt(1.W))
    val immType        = Output(UInt(3.W))
    val ALUop          = Output(UInt(5.W))
  })

  val N = 0.asUInt(1.W)
  val Y = 1.asUInt(1.W)

  val opcodeMap: Array[(BitPat, List[UInt])] = Array(
    // signal      memToReg, regWrite, memRead, memWrite, branch,  jump, branchType,    Op1Select, Op2Select, ImmSelect,    ALUOp

    // Mem instructions
    LW     -> List(Y,        Y,        Y,       N,        N,       N,    branch_types.DC, rs1,       imm,       ITYPE,        ALUOps.ADD),
    SW     -> List(N,        N,        N,       Y,        N,       N,    branch_types.DC, rs1,       imm,       STYPE,        ALUOps.ADD),


    // Arithmetic instructions
    ADD    -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.ADD),
    ADDI   -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       imm,       ITYPE,        ALUOps.ADD),
    SUB    -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.SUB),
    AND    -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.AND),
    ANDI   -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       imm,       ITYPE,        ALUOps.AND),
    OR     -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.OR ),
    ORI    -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       imm,       ITYPE,        ALUOps.OR),
    XOR    -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.XOR),
    XORI   -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       imm,       ITYPE,        ALUOps.XOR),

    // Imm instructions
    LUI    -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       imm,       UTYPE,        ALUOps.LUI),
    AUIPC  -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       imm,       UTYPE,        ALUOps.ADD),

    // Shifts
    SRA    -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.SRA),
    SRAI   -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       imm,       ITYPE,        ALUOps.SRA),
    SRL    -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.SRL),
    SRLI   -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       imm,       ITYPE,        ALUOps.SRL),
    SLL    -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.SLL),
    SLLI   -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       imm,       ITYPE,        ALUOps.SLL),

    // Set less than
    SLT    -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.SLT),
    SLTI   -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       imm,       ITYPE,        ALUOps.SLT),
    SLTU   -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, ALUOps.SLTU),
    SLTIU  -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       imm,       ITYPE,        ALUOps.SLTU),

    // Jumps
    JAL    -> List(N,        Y,        N,       N,        N,       Y,    branch_types.jump, PC,      imm,       JTYPE,        ALUOps.ADD),
    JALR   -> List(N,        Y,        N,       N,        N,       Y,    branch_types.jump, rs1,     imm,       ITYPE,        ALUOps.ADD),

    // Branchs
    BEQ    -> List(N,        N,        N,       N,        Y,       N,    branch_types.beq, PC,      imm,       BTYPE,        ALUOps.ADD),
    BNE    -> List(N,        N,        N,       N,        Y,       N,    branch_types.neq, PC,      imm,       BTYPE,        ALUOps.ADD),
    BLT    -> List(N,        N,        N,       N,        Y,       N,    branch_types.lt,  PC,      imm,       BTYPE,        ALUOps.ADD),
    BGE    -> List(N,        N,        N,       N,        Y,       N,    branch_types.gte, PC,      imm,       BTYPE,        ALUOps.ADD),
    BLTU   -> List(N,        N,        N,       N,        Y,       N,    branch_types.ltu, PC,      imm,       BTYPE,        ALUOps.ADD),
    BGEU   -> List(N,        N,        N,       N,        Y,       N,    branch_types.gteu,PC,      imm,       BTYPE,        ALUOps.ADD),

    // MDU instructions

    MUL    -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, MDUOps.MUL),
    DIV    -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, MDUOps.DIV),
    REM    -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, MDUOps.REM),

    MULH   -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, MDUOps.MULH),
    MULHSU -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, MDUOps.MULHSU),
    MULHU  -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, MDUOps.MULHU),

    DIVU   -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, MDUOps.DIVU),
    REMU   -> List(N,        Y,        N,       N,        N,       N,    branch_types.DC, rs1,       rs2,       ImmFormat.DC, MDUOps.REMU),

  )


  val NOP = List(N, N, N, N, N, N, branch_types.DC, rs1, rs2, ImmFormat.DC, ALUOps.DC)

  val decodedControlSignals = ListLookup(
    io.instruction.asUInt(),
    NOP,
    opcodeMap)

  io.controlSignals.memToReg   := decodedControlSignals(0)
  io.controlSignals.regWrite   := decodedControlSignals(1)
  io.controlSignals.memRead    := decodedControlSignals(2)
  io.controlSignals.memWrite   := decodedControlSignals(3)
  io.controlSignals.branch     := decodedControlSignals(4)
  io.controlSignals.jump       := decodedControlSignals(5)

  io.branchType := decodedControlSignals(6)
  io.op1Select  := decodedControlSignals(7)
  io.op2Select  := decodedControlSignals(8)
  io.immType    := decodedControlSignals(9)
  io.ALUop      := decodedControlSignals(10)
}
