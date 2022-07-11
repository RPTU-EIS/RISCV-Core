package Stage_MEM

import DataMemory.DataMemory
import chisel3._
import chisel3.util._

class MEM extends Module {
  val io = IO(new Bundle {
    val MW_pip_reg_WE = Input(UInt(1.W))
    val ex_mem_reg    = Input(UInt(141.W))  // third pipeline reg(ex/mem) = (ALU_T, B_din_src2, Lui_res,  Pc+4, mem,wb)
    val mem_wb_reg    = Output(UInt(136.W))  // last pipeline reg(mem/wb) = (LUI_Res, PC+4, ALU_T, MDR, WB)
  })

  val DM = Module(new DataMemory())

  val lsMux = Wire(UInt(3.W))
  val addr  = Wire(UInt(32.W))
  val DM_WE = Wire(Bool())
  val DM_RE = Wire(Bool())
  val data_in = Wire(UInt(32.W))
  val data_out = Wire(UInt(32.W))

  //  third pipeline reg(ex/mem) = (ALU_T, B_din_src2, Lui_res,  Pc+4, mem,wb)
  val mem_cntr = io.ex_mem_reg(12,8)

  // DM_R/WE, dmMUXSel
  DM_RE := mem_cntr(4)
  DM_WE := mem_cntr(3)
  lsMux := mem_cntr(2,0)
  data_in := io.ex_mem_reg(108, 77)
  addr := io.ex_mem_reg(140, 109)

  DM.io.lsMux := lsMux
  DM.io.addr := addr
  DM.io.DM_WE := DM_WE
  DM.io.DM_RE := DM_RE
  DM.io.data_in := data_in
  data_out := DM.io.data_out

  val mem_wb_reg = RegInit(0.U(136.W))

  // third pipeline reg(ex/mem) = (ALU_T, B_din_src2, Lui_res,  Pc+4, mem,wb)
  // last pipeline reg(mem/wb) = (LUI_Res, PC+4, ALU_T, MDR, WB)
  when(io.MW_pip_reg_WE === 1.U(1.W)){
    mem_wb_reg := Cat(io.ex_mem_reg(76,45), io.ex_mem_reg(44,13), io.ex_mem_reg(140, 109), data_out,
      io.ex_mem_reg(7,0))
  }

  io.mem_wb_reg := mem_wb_reg

}
