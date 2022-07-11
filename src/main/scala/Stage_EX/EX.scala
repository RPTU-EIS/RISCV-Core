package Stage_EX

import ExtenionUnit.ExtenionUnit
import GPR.registerFile
import cntrl_Pipl_CPU.control

import chisel3._
import chisel3.util._

class EX extends Module
{
  val io = IO(new Bundle {
    val EM_pip_reg_WE = Input(UInt(1.W))
    val id_ex_reg     = Input(UInt(156.W))  // second pipeline reg(Id/EX) = (A, B, LUI_res, EXT_Imm, rd, ex,mem,wb)
    val if_id_reg     = Output(UInt(64.W))  // first pipeline reg(IF/id) = (instr, PC+4)

  })


  val id_ex_reg = RegInit(0.U(156.W))

  when(io.EM_pip_reg_WE === 1.U(1.W)){
    id_ex_reg := Cat()
  }

  io.id_ex_reg := id_ex_reg
}
