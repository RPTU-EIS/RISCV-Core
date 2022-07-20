package cntrl_Pipl_CPU

import chisel3._
import chisel3.util._
import config.AluOperation._
import config.ExtensionCases._
import config.States._

class control extends Module
{
  val io = IO(new Bundle {
    val opcode = Input(UInt(6.W))
    val funct3 = Input(UInt(3.W))
    val funct7 = Input(UInt(7.W))
    val gpr_rd = Input(UInt(5.W))
    val EX_cntr   = Output(UInt(10.W)) // ALUsrcMuxsels, ALUop, J_BR_MUX_sel, is_br_j,
    val MEM_cntr  = Output(UInt(5.W)) // DM_R/WE, lsMux
    val WB_cntr   = Output(UInt(8.W)) // GPR_WE, GPR_RD, GPR_dataInMuxSel
    val ext_type  = Output(UInt(3.W)) //
  })

  val opcode = io.opcode
  val funct3 = io.funct3
  val funct7 = io.funct7

  // for EX_cntr
  val src1_alu_mux = Wire(UInt(1.W))
  val src2_alu_mux = Wire(UInt(1.W))
  val aluOP        = Wire(UInt(4.W))

  val J_BR_MUX_sel = Wire(UInt(1.W))

  val is_br_j      = Wire(UInt(1.W))

  // for MEM_cntr

  val DM_WE = Wire(Bool())
  val DM_RE = Wire(Bool())

  val lsMux = Wire(UInt(3.W))

  // for WB_cntr

  val gpr_we       = Wire(UInt(1.W))
  val gpr_din_mux  = Wire(UInt(2.W))
  val gpr_rd       = Wire(UInt(5.W))

//  io.opcode === "b1101111".U(7.W) | io.opcode === "b1100111".U(7.W)){ // jal or jalr
//  io.ext_type := Mux(io.opcode === "b1101111".U(7.W), jal, jalr) // jal ? jal : jalr (extension types)
//  io.opcode === "b0010111".U(7.W))// auipc
//  io.ext_type    := Mux(io.opcode === "b0000011".U(7.W), i_type, store) // load ? i_type : store
// io.opcode === "b0000011".U(7.W) | io.opcode === "b0100011".U(7.W)){ // load or store
    when(opcode === "b1101111".U(7.W)) // jal
    {
      io.ext_type := jal
    }
    .elsewhen(opcode === "b1100111".U(7.W)) // jalr
    {
      io.ext_type := jalr
    }
    .elsewhen(opcode === "b0010111".U(7.W)) // auipc
    {
      io.ext_type := auipc
    }
    .elsewhen(opcode === "b0100011".U(7.W)) // store
    {
      io.ext_type := store
    }
    .elsewhen(io.opcode === "b1100011".U(7.W))// branch
    {
      io.ext_type := id
    }
    .otherwise
    {
      io.ext_type := i_type
    }

  // EX_cntrl
// ALUsrcMuxsels, ALUop, J_BR_MUX_sel, is_br_j,

    when(io.opcode === "b1100011".U(7.W)) // br
    {
      J_BR_MUX_sel := 1.U(1.W)
      is_br_j := 1.U(1.W)
    }
    .elsewhen(io.opcode === "b1101111".U(7.W)) // jal
    {
      J_BR_MUX_sel := 0.U(1.W)
      is_br_j := 1.U(1.W)
    }
   .elsewhen(io.opcode === "b1100111".U(7.W)) // jalr
    {
      J_BR_MUX_sel := 0.U(1.W)
      is_br_j := 1.U(1.W)
    }
      .otherwise
      {
        J_BR_MUX_sel := 1.U(1.W)
        is_br_j := 0.U(1.W)
      }

     when(io.opcode === "b0110011".U(7.W)){ // R type
      src1_alu_mux := 1.U(2.W)
      src2_alu_mux := 0.U(2.W)
      when(io.funct3 === 5.U(3.W)){
        aluOP := Mux(io.funct7(5) === 1.U(1.W), sra, srl)
      }
        .otherwise{
          aluOP := Cat(io.funct7(5), io.funct3)
        }
  }
    .elsewhen(io.opcode === "b0010011".U(7.W)){ // i type
      src1_alu_mux := 1.U(2.W)
      src2_alu_mux := 1.U(2.W)
      when(io.funct3 === 5.U(3.W)){
        aluOP := Mux(io.funct7(5) === 1.U(1.W), sra, srl)
      } .otherwise{
        aluOP := Cat(0.U(1.W), io.funct3)
      }
    }
    .elsewhen(io.opcode === "b0000011".U(7.W) | io.opcode === "b0100011".U(7.W)){ // load or store
      src1_alu_mux := 1.U(2.W)
      src2_alu_mux := 1.U(2.W)
      aluOP        := add
    }
    .elsewhen(io.opcode === "b0010111".U(7.W)){ // auipc
      src1_alu_mux := 0.U(2.W)
      src2_alu_mux := 1.U(2.W)
      aluOP        := add
    }
    .elsewhen(io.opcode === "b1101111".U(7.W) | io.opcode === "b1100111".U(7.W)){ // jal or jalr
      src1_alu_mux := Mux(io.opcode === "b1101111".U(7.W), 0.U(2.W), 1.U(2.W)) // jal ? PC : register (alu 1st operand)
      src2_alu_mux := 1.U(2.W)
      aluOP        := add
    }
    .otherwise{ // nop
      src1_alu_mux := 1.U(2.W)
      src2_alu_mux := 0.U(2.W)
      aluOP        := add //
    }

  io.EX_cntr := Cat(src1_alu_mux, src2_alu_mux, aluOP, J_BR_MUX_sel, is_br_j)


  // MEM_cntrl := // DM_R/WE, lsMux

  DM_WE := Mux((io.opcode === "b0100011".U(7.W)), true.B, false.B) // store ? 1 : 0;
  DM_RE := Mux((io.opcode === "b0000011".U(7.W)), true.B, false.B) // load  ? 1 : 0;

  when((io.opcode === "b0000011".U(7.W)) | (io.opcode === "b0100011".U(7.W))){ // ls
    lsMux := io.funct3
  }
  .otherwise{
    lsMux := 0.U(3.W)
  }

  io.MEM_cntr := Cat(DM_RE, DM_WE, lsMux)

  // WB_cntrl := // GPR_WE, GPR_RD, GPR_dataInMuxSel

  gpr_rd := io.gpr_rd

  when(io.opcode === "b0000011".U(7.W)) // load
  {
    gpr_din_mux := 1.U(2.W)
    gpr_we := 1.U(1.W)
  }
  .elsewhen(io.opcode === "b0110111".U(7.W)) // LUI
  {
    gpr_din_mux := 2.U(2.W)
    gpr_we := 1.U(1.W)
  }
  .elsewhen(io.opcode === "b1101111".U(7.W) | io.opcode === "b1100111".U(7.W)) // jal, jalr
  {
    gpr_din_mux := 3.U(2.W)
    gpr_we := 1.U(1.W)
  }
  .elsewhen((io.opcode =/= "b0100011".U(7.W)) & (io.opcode =/= "b1100011".U(7.W))) // ALU which means !br and ! store
  {
    gpr_din_mux := 0.U(2.W)
    gpr_we := 1.U(1.W)
  }
  .otherwise
  {
    gpr_din_mux := 0.U(2.W)
    gpr_we := 0.U(1.W)
  }

  // GPR_WE, GPR_RD, GPR_dataInMuxSel
  io.WB_cntr := Cat(gpr_we, gpr_rd, gpr_din_mux)

}

