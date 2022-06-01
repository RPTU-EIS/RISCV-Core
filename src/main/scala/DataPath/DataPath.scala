package DataPath

import chisel3._
import chisel3.util._

import ALU._
import bool._
import ExtenionUnit._
import GPR._
import PC._

class DataPath extends Module
{
  val io = IO(new Bundle {
    val opcode = Output(UInt(7.W))
    val funct3 = Output(UInt(3.W))
    val funct7 = Output(UInt(7.W))
    val controls = Input(UInt(13.W))
    val aluOP = Input(UInt(4.W))

    val PC_out = Output(UInt(32.W))
    val instr  = Input(UInt(32.W))

    val dm_addr  = Output(UInt(32.W))
    val dm_data  = Output(UInt(32.W))
    val data_in  = Input(UInt(32.W))
  })

  // Control signals
//  val dm_din_mux   = io.controls(14,13)
  val pc_mux       = io.controls(12)
  val is_if        = io.controls(11)
  val is_br        = io.controls(10)
  val ir_we        = io.controls(9)
  val gpr_we       = io.controls(8)
  val gpr_din_mux  = io.controls(7,6)
  val src1_alu_mux = io.controls(5)
  val src2_alu_mux = io.controls(4,3)
  val exts_type    = io.controls(2,0)



  // Module Instantiation
  val pc      = Module(new PC())
  val alu     = Module(new ALU())
  val gpr     = Module(new registerFile())
  val bool    = Module(new bool())
  val extUnit = Module(new ExtenionUnit())

  // datapath signals connecting to the IO ports of modules
    // PC
  val pc_we  = pc.io.WE
  val pc_in  = pc.io.PC_next
  val pc_out = pc.io.PC_current
  val ir_reg = RegInit(0.U(32.W))

    // ALU
  val alu_src1 = alu.io.src1
  val alu_src2 = alu.io.src2
  alu.io.ALUop := io.aluOP
  val alu_res  = alu.io.aluRes
  val alu_T_reg = RegInit(0.U(32.W))

    // GPR
  gpr.io.WE := gpr_we
  val gpr_rs1 = gpr.io.rs1
  val gpr_rs2 = gpr.io.rs2
  val gpr_rd = gpr.io.rd
  val gpr_data_a = gpr.io.data_A
  val gpr_data_b = gpr.io.data_B
  val gpr_data_in = gpr.io.data_in
  val gpr_A_reg = RegInit(0.U(32.W))
  val gpr_B_reg = RegInit(0.U(32.W))
  val lui_res = Cat(ir_reg(31,12), Fill(12,0.U(1.W)))
    // bool
  bool.io.is_IF := is_if
  val brCond_true = Wire(Bool())  // = is_br & alures(0)
  bool.io.brCond_true := brCond_true
  pc_we := bool.io.PC_WE

    // Extension Unit
  extUnit.io.instr := ir_reg
  extUnit.io.ext_type := exts_type
  val ext_imm = extUnit.io.ext_imm

    // MDR
  val MDR = RegInit(0.U(32.W))
  val dm_data = Wire(UInt(32.W))
  // MUXes
    // PC Mux
  pc_in := Mux(pc_mux, alu_T_reg, alu_res)

    // gpr_data_in_MUX gpr_din_mux
  when(gpr_din_mux === 0.U(2.W)){
    gpr_data_in := alu_T_reg
  } .elsewhen(gpr_din_mux === 1.U(2.W)){
    gpr_data_in := MDR
  }.elsewhen(gpr_din_mux === 2.U(2.W)){
    gpr_data_in := lui_res
  }.elsewhen(gpr_din_mux === 3.U(2.W)){
    gpr_data_in := pc_out
  }.otherwise{
    gpr_data_in := alu_T_reg
  }

    // alu_src1_mux
  when(src1_alu_mux === 0.U(1.W)){
    alu_src1 := pc_out
  } .elsewhen(src1_alu_mux === 1.U(1.W)){
    alu_src1 := gpr_A_reg
  }.otherwise{
    alu_src1 := gpr_A_reg
  }

    // alu_src2_mux
  when(src2_alu_mux === 0.U(2.W)){
    alu_src2 := gpr_B_reg
  } .elsewhen(src2_alu_mux === 1.U(2.W)){
    alu_src2 := 4.U(32.W)
  }.elsewhen(src2_alu_mux === 2.U(2.W)){
    alu_src2 := ext_imm
  }.otherwise{
    alu_src2 := gpr_B_reg
  }
    // dm_din_mux      TODO depends memory impl., RESOLVED implemented in memory file
//  when(dm_din_mux === 0.U(2.W)){
//    dm_data := gpr_B_reg & "h000000ff".U(32.W)
//  } .elsewhen(dm_din_mux === 1.U(2.W)){
//    dm_data := gpr_B_reg & "h0000ffff".U(32.W)
//  }.elsewhen(dm_din_mux === 2.U(2.W)){
//    dm_data := gpr_B_reg
//  }.otherwise{
//    dm_data := gpr_B_reg
//  }
  dm_data := gpr_B_reg

  // for bool
  brCond_true := is_br & alu_res(0)

  // Instruction Register
  when(ir_we === 1.U(1.W)){
    ir_reg := io.instr
  }

  // GPR port connections
  gpr_rs1 := ir_reg(19,15)
  gpr_rs2 := ir_reg(24,20)
  gpr_rd  := ir_reg(11,7)
  gpr_A_reg := gpr_data_a
  gpr_B_reg := gpr_data_b

  // ALU Target REg Connection
  alu_T_reg := alu_res

  // data_in to mdr
  MDR := io.data_in

  // Output signal
  io.dm_addr := alu_T_reg
  io.dm_data := dm_data
  io.PC_out  := pc_out

  io.opcode := ir_reg(6,0)
  io.funct3 := ir_reg(14,12)
  io.funct7 := ir_reg(31,25)
}
