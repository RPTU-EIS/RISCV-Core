package cntrl_Pipl_CPU

import chisel3._
import chisel3.util._


class control extends Module
{
  val io = IO(new Bundle {
    val opcode = Input(UInt(6.W))
    val funct3 = Input(UInt(3.W))
    val funct7 = Input(UInt(7.W))
    val EX_cntr   = Output(UInt(10.W)) // ALUsrcMuxsels, ALUop, J_BR_MUX_sel, is_br_j,
    val MEM_cntr  = Output(UInt(5.W)) // DM_R/WE, dmMUXSel
    val WB_cntr   = Output(UInt(8.W)) // GPR_WE, GPR_RD, GPR_dataInMuxSel
    val ext_type  = Output(UInt(3.W)) //

  })
}
