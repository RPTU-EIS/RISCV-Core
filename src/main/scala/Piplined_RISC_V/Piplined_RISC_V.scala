//package Piplined_RISC_V
//
//import Stage_EX.EX
//import Stage_ID.ID
//import Stage_IF.IF
//import Stage_MEM.MEM
//
//import chisel3._
//import chisel3.util._
//
//
//class Piplined_RISC_V {
//
//  val IF  = Module(new IF())
//  val ID  = Module(new ID())
//  val EX  = Module(new EX())
//  val MEM = Module(new MEM())
//
//  // IF
//  val FD_pip_reg_WE = WireDefault(1.U(1.W))
////  val br_target     = Wire(UInt(32.W))  // forwarded jump target       (forwarded from exec stage)
////  val jm_target     = Wire(UInt(32.W))  // forwarded br target         (forwarded from exec stage)
////  val j_br_mux_sel  = Wire(UInt(1.W))   // forwarded j_or_br mux sel   (forwarded from exec stage)
////  val is_br_j       = Wire(UInt(1.W))   // forwarded is_br_j           (forwarded from exec stage)
////  val alu_res0      = Wire(UInt(1.W))   // forwarded comparison result (forwarded from exec stage)
//  val if_id_reg     = Wire(UInt(64.W)) // first pipeline reg = (instr, PC+4) - Output
//
//  IF.io.FD_pip_reg_WE := FD_pip_reg_WE // 1.U(1.W) // TODO implement stall
//  IF.io.br_target := EX.io.br_target
//  IF.io.jm_target := EX.io.jm_target
//  IF.io.j_br_mux_sel := EX.io.j_br_mux_sel
//  IF.io.is_br_j := EX.io.is_br_j
//  IF.io.alu_res0 := EX.io.alu_res0
//  if_id_reg := IF.io.if_id_reg
//
//
//  // ID
//  val DE_pip_reg_WE = WireDefault(1.U(1.W))
////  val mem_wb_reg    = Wire(UInt(136.W))  // last pipeline reg(mem/wb) = (LUI_Res, PC+4, ALU_T, MDR, WB)
////  val if_id_reg     = Wire(UInt(64.W))  // first pipeline reg(IF/id) = (instr, PC+4)
//  val id_ex_reg     = Wire(UInt(188.W))  // second pipeline reg(Id/EX) = (A, B, LUI_res, EXT_Imm, PC_Plus_4, rd, ex,mem,wb) - output
//
//  ID.io.DE_pip_reg_WE := DE_pip_reg_WE
//  ID.io.mem_wb_reg := mem_wb_reg
//  ID.io.if_id_reg := if_id_reg
//  id_ex_reg := ID.io.id_ex_reg
//
//  // EX
//  val EM_pip_reg_WE = WireDefault(1.U(1.W))
////  val id_ex_reg     = Wire(UInt(188.W))  // second pipeline reg(Id/EX) = (A, B, LUI_res, EXT_Imm, PC+4, rd, ex,mem,wb)
//  val ex_mem_reg    = Wire(UInt(141.W))  // third pipeline reg(ex/mem) = (ALU_T, B_din_src2, Lui_res,  Pc+4, mem,wb) // Outputs
//  val br_target     = Wire(UInt(32.W))  // forwarded jump target       (forwarded to fetch stage)
//  val jm_target     = Wire(UInt(32.W))  // forwarded br target         (forwarded to fetch stage)
//  val j_br_mux_sel  = Wire(UInt(1.W))   // forwarded j_or_br mux sel   (forwarded to fetch stage)
//  val is_br_j       = Wire(UInt(1.W))   // forwarded is_br_j           (forwarded to fetch stage)
//  val alu_res0      = Wire(UInt(1.W))   // forwarded comparison result (forwarded to fetch stage)
//
//  EX.io.EM_pip_reg_WE := EM_pip_reg_WE
//  EX.io.id_ex_reg := id_ex_reg
//  ex_mem_reg := EX.io.ex_mem_reg
//  br_target := EX.io.br_target
//  jm_target := EX.io.jm_target
//  j_br_mux_sel := EX.io.j_br_mux_sel
//  is_br_j := EX.io.is_br_j
//  alu_res0 := EX.io.alu_res0
//
//  // MEM
//  val MW_pip_reg_WE = WireDefault(1.U(1.W))
////  val ex_mem_reg    = Wire(UInt(141.W))  // third pipeline reg(ex/mem) = (ALU_T, B_din_src2, Lui_res,  Pc+4, mem,wb)
//  val mem_wb_reg    = Wire(UInt(136.W))  // last pipeline reg(mem/wb) = (LUI_Res, PC+4, ALU_T, MDR, WB) // output
//
//  MEM.io.MW_pip_reg_WE := MW_pip_reg_WE
//  MEM.io.ex_mem_reg := ex_mem_reg
//  mem_wb_reg := MEM.io.mem_wb_reg
//}
