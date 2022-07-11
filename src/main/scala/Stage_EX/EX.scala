package Stage_EX

import ALU.ALU

import chisel3._
import chisel3.util._

class EX extends Module
{
  val io = IO(new Bundle {
    val EM_pip_reg_WE = Input(UInt(1.W))
    val id_ex_reg     = Input(UInt(188.W))  // second pipeline reg(Id/EX) = (A, B, LUI_res, EXT_Imm, PC+4, rd, ex,mem,wb)
    val ex_mem_reg    = Output(UInt(141.W))  // third pipeline reg(ex/mem) = (ALU_T, B_din_src2, Lui_res,  Pc+4, mem,wb)
    val br_target     = Output(UInt(32.W))  // forwarded jump target       (forwarded to fetch stage)
    val jm_target     = Output(UInt(32.W))  // forwarded br target         (forwarded to fetch stage)
    val j_br_mux_sel  = Output(UInt(1.W))   // forwarded j_or_br mux sel   (forwarded to fetch stage)
    val is_br_j       = Output(UInt(1.W))   // forwarded is_br_j           (forwarded to fetch stage)
    val alu_res0      = Output(UInt(1.W))   // forwarded comparison result (forwarded to fetch stage)
  })

  val ALU = Module(new ALU())

  val src1   = Wire(UInt(32.W))
  val src2   = Wire(UInt(32.W))
  val ALUop  = Wire(UInt(4.W))
  val aluRes = Wire(UInt(32.W))

  // ALUsrcMuxsels, ALUop, J_BR_MUX_sel, is_br_j,
  val EX_cntr = io.id_ex_reg(22,13) // 10 bit long

  val alu_src1_mux_sel = EX_cntr(9,8)
  val alu_src2_mux_sel = EX_cntr(7,6)

  val PC_Plus_4 = io.id_ex_reg(59,28)
  val Ext_imm = io.id_ex_reg(91,60)

  val br_target = Wire(UInt(32.W))

  ALUop := EX_cntr(5,2)
  io.j_br_mux_sel := EX_cntr(1)
  io.is_br_j := EX_cntr(0)

  //src1 := io.id_ex_reg(187, 156)
  //src2 := io.id_ex_reg(155, 124)

  ALU.io.src1 := src1
  ALU.io.src2 := src2
  ALU.io.ALUop := ALUop
  aluRes := ALU.io.aluRes

  when(alu_src1_mux_sel === 0.U(1.W)){
    src1 := PC_Plus_4
  } .elsewhen(alu_src1_mux_sel === 1.U(1.W)){
    src1 := io.id_ex_reg(187, 156) //gpr_A_reg
  }.otherwise{
    src1 := io.id_ex_reg(187, 156) //gpr_A_reg
  }

  when(alu_src2_mux_sel === 0.U(1.W)){
    src2 := io.id_ex_reg(155, 124) // grp_B_REG
  } .elsewhen(alu_src2_mux_sel === 1.U(1.W)){
    src2 := Ext_imm
  }.otherwise{
    src2 := Ext_imm
  }

  br_target := PC_Plus_4 + Ext_imm


  val ex_mem_reg = RegInit(0.U(141.W))

  // third pipeline reg(ex/mem) = (ALU_T, B_din_src2, Lui_res,  Pc+4, mem,wb)
  when(io.EM_pip_reg_WE === 1.U(1.W)){
    ex_mem_reg := Cat(aluRes, io.id_ex_reg(155, 124), io.id_ex_reg(123, 92),PC_Plus_4, io.id_ex_reg(12,0))
  }

  io.ex_mem_reg := ex_mem_reg
  io.jm_target := aluRes
  io.br_target := br_target
}
