package Control

import config.AluOperation._
import chisel3._
import chisel3.util._

class Control extends Module
{
  val io = IO(new Bundle {
    val opcode = Input(UInt(7.W))
    val funct3 = Input(UInt(3.W))
    val funct7 = Input(UInt(7.W))

    val controls = Output(UInt(13.W))
    val aluOP    = Output(UInt(4.W))

    val lsMux = Output(UInt(3.W))
    val DM_WE = Output(Bool())
    val DM_RE = Output(Bool())
  })

  val pc_mux       = Wire(UInt(1.W))
  val is_if        = Wire(UInt(1.W))
  val is_br        = Wire(UInt(1.W))
  val ir_we        = Wire(UInt(1.W))
  val gpr_we       = Wire(UInt(1.W))
  val gpr_din_mux  = Wire(UInt(2.W))
  val src1_alu_mux = Wire(UInt(1.W))
  val src2_alu_mux = Wire(UInt(2.W))
  val exts_type    = Wire(UInt(3.W))

  val aluOP        = Wire(UInt(4.W))

  val lsMux = Wire(UInt(3.W))
  val DM_WE = Wire(Bool())
  val DM_RE = Wire(Bool())

  val fetch :: dec :: exec :: mem :: wb :: Nil = Enum(5) // state values

  val stateReg  = RegInit(fetch)
  val nextState = Wire(UInt(3.W))

  nextState := fetch

  pc_mux       := 0.U(1.W) // Wire(UInt(1.W))
  is_if        := 0.U(1.W) // Wire(UInt(1.W))
  is_br        := 0.U(1.W) // Wire(UInt(1.W))
  ir_we        := 0.U(1.W) // Wire(UInt(1.W))
  gpr_we       := 0.U(1.W) // Wire(UInt(1.W))
  gpr_din_mux  := 0.U(2.W) // Wire(UInt(2.W))
  src1_alu_mux := 0.U(1.W) // Wire(UInt(1.W))
  src2_alu_mux := 0.U(2.W) // Wire(UInt(2.W))
  exts_type    := 0.U(2.W) // Wire(UInt(3.W))

  aluOP := 0.U(4.W)
  lsMux := 0.U(3.W)
  DM_WE := false.B
  DM_RE := false.B

  switch(stateReg){ // TODO complete output and next state evaluations
    is(fetch) { // TODO recheck
      is_if        := 1.U(1.W)
      is_br        := 0.U(1.W)
      ir_we        := 1.U(1.W)
      gpr_we       := 0.U(1.W)
      gpr_din_mux  := 0.U(2.W)
      src1_alu_mux := 0.U(1.W)
      src2_alu_mux := 1.U(2.W)
      DM_WE        := false.B
      DM_RE        := false.B
      exts_type    := 0.U(2.W)
      pc_mux       := 0.U(1.W)
      lsMux        := 0.U(3.W)
      aluOP        := add
      nextState    := dec
    }
    is(dec)   {} // TODO
    is(exec)  {} // TODO
    is(mem)   {} // TODO
    is(wb)    {
      is_if        := 0.U(1.W)
      is_br        := 0.U(1.W)
      ir_we        := 0.U(1.W)
      gpr_we       := 1.U(1.W)
      gpr_din_mux  := 1.U(2.W)
      src1_alu_mux := 0.U(1.W)
      src2_alu_mux := 0.U(2.W)
      DM_WE        := false.B
      DM_RE        := false.B
      exts_type    := 0.U(2.W)
      pc_mux       := 0.U(1.W)
      lsMux        := 0.U(3.W)
      aluOP        := add
      nextState    := fetch
    }
  }

  stateReg := nextState // state update

  // outputs
  io.controls(12)  := pc_mux
  io.controls(11)  := is_if
  io.controls(10)  := is_br
  io.controls(9)   := ir_we
  io.controls(8)   := gpr_we
  io.controls(7,6) := gpr_din_mux
  io.controls(5)   := src1_alu_mux
  io.controls(4,3) := src2_alu_mux
  io.controls(2,0) := exts_type

  io.aluOP := aluOP
  io.lsMux := lsMux
  io.DM_WE := DM_WE
  io.DM_RE := DM_RE
}
