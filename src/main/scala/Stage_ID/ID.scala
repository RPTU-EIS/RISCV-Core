package Stage_ID

import ExtenionUnit.ExtenionUnit
import GPR.registerFile
import cntrl_Pipl_CPU.control

import chisel3._
import chisel3.util._

class ID extends Module
{
  val io = IO(new Bundle {
    val DE_pip_reg_WE = Input(UInt(1.W))
    val mem_wb_reg     = Input(UInt(136.W))  // last pipeline reg(mem/wb) = (LUI_Res, PC+4, ALU_T, MDR, WB)
    val if_id_reg     = Input(UInt(64.W))  // first pipeline reg(IF/id) = (instr, PC+4)
    val id_ex_reg     = Output(UInt(188.W))  // second pipeline reg(Id/EX) = (A, B, LUI_res, EXT_Imm, PC_Plus_4, rd, ex,mem,wb)
  })

  val gpr     = Module(new registerFile())
  val extUnit = Module(new ExtenionUnit())
  val control = Module(new control())

  // GPR PORT
  val WE  = Wire(Bool()) // taken from pipline reg mem/wb
  val rs1 = Wire(UInt(5.W))
  val rs2 = Wire(UInt(5.W))
  val rd  = Wire(UInt(5.W)) // taken from pipline reg mem/wb
  val data_A = Wire(UInt(32.W)) // goes to pipline reg id/ex
  val data_B = Wire(UInt(32.W)) // goes to pipline reg id/ex
  val data_in = Wire(UInt(32.W)) // output of GPR_datainMUX

  val gpr_din_mux = Wire(UInt(2.W)) // taken from pipline reg mem/wb

  //(LUI_Res, PC+4, ALU_T, MDR, WB)
  val WB = Wire(UInt(8.W))
  val instr = Wire(UInt(32.W))
  val PC_Plus_4 = Wire(UInt(32.W))

  val LUI_res = Wire(UInt(32.W))

  val ext_imm = extUnit.io.ext_imm

  WB := io.mem_wb_reg(7,0)
  WE := WB(7)
  rd := WB(6,2)
  gpr_din_mux := io.mem_wb_reg(1,0)
  instr := io.if_id_reg(63,32)
  PC_Plus_4 := io.if_id_reg(31,0)
  rs1 := instr(19,15)
  rs2 := instr(24,20)

  gpr.io.WE := WE
  gpr.io.rs1 := rs1
  gpr.io.rs2 := rs2
  gpr.io.rd := rd
  data_A := gpr.io.data_A
  data_B := gpr.io.data_B
  gpr.io.data_in := data_in

  LUI_res := Cat(instr(31,12), Fill(12,0.U(1.W)))

  control.io.opcode := instr(6,0)
  control.io.funct3 := instr(14,12)
  control.io.funct7 := instr(31,25)

  extUnit.io.ext_type := control.io.ext_type
  extUnit.io.instr := instr

  // last pipeline reg(mem/wb) = (LUI_Res, PC+4, ALU_T, MDR, WB)
  when(gpr_din_mux === 0.U(2.W)){
    data_in := io.mem_wb_reg(71,40) // alu_T_reg
  } .elsewhen(gpr_din_mux === 1.U(2.W)){
    data_in := io.mem_wb_reg(39,8)// MDR
  }.elsewhen(gpr_din_mux === 2.U(2.W)){
    data_in := io.mem_wb_reg(135,104) // lui_res
  }.elsewhen(gpr_din_mux === 3.U(2.W)){
    data_in := io.mem_wb_reg(103,72) // pc_out
  }.otherwise{
    data_in := io.mem_wb_reg(71,40) // alu_T_reg
  }

  val id_ex_reg = RegInit(0.U(156.W))
  val WB_cntr = control.io.WB_cntr

  //  second pipeline reg(Id/EX) = (A, B, LUI_res, EXT_Imm, rd, ex,mem,wb)
  when(io.DE_pip_reg_WE === 1.U(1.W)){
    id_ex_reg := Cat(data_A, data_B, LUI_res, ext_imm, PC_Plus_4, instr(11,7),
      control.io.EX_cntr, control.io.MEM_cntr, WB_cntr(7), instr(11,7), WB_cntr(1,0)) //// GPR_WE, GPR_RD, GPR_dataInMuxSel
  }

  io.id_ex_reg := id_ex_reg
}
