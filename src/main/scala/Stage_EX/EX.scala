package Stage_EX
import chisel3._
import chisel3.util._
import ALU.ALU
import FW.FW
import Branch_OP.Branch_OP
import config.branch_types._
import config.op1sel._
import config.op2sel._
import config.{ControlSignals, Instruction, branch_types, op1sel, op2sel}

class EX extends Module {

  val io = IO(
    new Bundle {
      val instruction        = Input(new Instruction)
      val controlSignals     = Input(new ControlSignals)
      val controlSignalsEXB  = Input(new ControlSignals)
      val controlSignalsMEMB = Input(new ControlSignals)
      val PC                 = Input(UInt())
      val branchType         = Input(UInt())
      val op1Select          = Input(UInt())
      val op2Select          = Input(UInt())
      val rs1                = Input(UInt())
      val Rs2                = Input(UInt())
      val immData            = Input(UInt())
      val ALUop              = Input(UInt())

      //Forward unit
      val rdEXB              = Input(UInt())
      val ALUresultEXB       = Input(UInt())
      val rdMEMB             = Input(UInt())
      val ALUresultMEMB      = Input(UInt())


      val ALUResult          = Output(UInt())
      val branchAddr         = Output(UInt())
      val branch             = Output(Bool())
      val insertBubble       = Output(Bool())
      val freeze             = Output(Bool())
      val Rs1Forwarded       = Output(UInt())
      val Rs2Forwarded       = Output(UInt())
    }
  )

  val ALU          = Module(new ALU).io
  val Branch       = Module(new Branch_OP).io
  val Rs1FW        = Module(new FW).io
  val Rs2FW        = Module(new FW).io

  val insertBubble            = Wire(Bool())
  val alu_operand1            = Wire(UInt())
  val alu_operand_1_forwarded = Wire(UInt())
  val alu_operand2            = Wire(UInt())
  val alu_operand_2_forwarded = Wire(UInt())
  val alu_result              = Wire(UInt())
  val freeze_rs1              = Wire(Bool())
  val freeze_rs2              = Wire(Bool())


  //////////////////////
  // Branch condition //
  //////////////////////

  Branch.branchType := io.branchType
  Branch.src1        := alu_operand_1_forwarded
  Branch.src2        := alu_operand_2_forwarded
  io.branch         := Branch.branchCondition


  ////////////////
  // Forwarders //
  ////////////////
  Rs1FW.regAddr            := io.instruction.registerRs1
  Rs1FW.controlSignalsEXB  := io.controlSignalsEXB
  Rs1FW.controlSignalsMEMB := io.controlSignalsMEMB
  Rs1FW.regData            := io.rs1
  Rs1FW.rdEXB              := io.rdEXB
  Rs1FW.ALUresultEXB       := io.ALUresultEXB
  Rs1FW.rdMEMB             := io.rdMEMB
  Rs1FW.ALUresultMEMB      := io.ALUresultMEMB
  alu_operand_1_forwarded         := Rs1FW.operandData
  freeze_rs1                      := Rs1FW.freeze

  Rs2FW.regAddr            := io.instruction.registerRs2
  Rs2FW.controlSignalsEXB  := io.controlSignalsEXB
  Rs2FW.controlSignalsMEMB := io.controlSignalsMEMB
  Rs2FW.regData            := io.Rs2
  Rs2FW.rdEXB              := io.rdEXB
  Rs2FW.ALUresultEXB       := io.ALUresultEXB
  Rs2FW.rdMEMB             := io.rdMEMB
  Rs2FW.ALUresultMEMB      := io.ALUresultMEMB
  alu_operand_2_forwarded         := Rs2FW.operandData
  freeze_rs2                      := Rs2FW.freeze

  //stall signal to IDBarrier and EXBarrier
  io.freeze := freeze_rs1 | freeze_rs2

  //Do not insert a bubble when freezing
  when((freeze_rs1 | freeze_rs2) === false.B){
    insertBubble  := io.controlSignals.jump | (io.controlSignals.branch & Branch.branchCondition  === 1.U)
  }.otherwise{
    insertBubble  := false.B
  }
  io.insertBubble := insertBubble


  /////////
  // ALU //
  /////////

  //Operand 1 Mux
  when(io.op1Select === op1sel.PC){
    alu_operand1    := io.PC
  }.otherwise{
    alu_operand1    := alu_operand_1_forwarded
  }

  //Operand 2 Mux
  when(io.op2Select === op2sel.rs2){
    alu_operand2    := alu_operand_2_forwarded
  }.otherwise{
    alu_operand2    := io.immData
  }

  //output forwarded operands
  io.Rs1Forwarded := alu_operand_1_forwarded
  io.Rs2Forwarded := alu_operand_2_forwarded

  //ALU
  ALU.src1           :=alu_operand1
  ALU.src2           :=alu_operand2
  ALU.ALUop         :=io.ALUop
  alu_result        := ALU.aluRes


  /////////////////
  // BRANCH ADDR //
  /////////////////
  io.branchAddr := alu_result


  /////////////////////////////
  // ALU RESULT / PC + 4 MUX //
  /////////////////////////////
  when(io.branchType === branch_types.jump){
    io.ALUResult := io.PC + 4.U
  }.otherwise{
    io.ALUResult := alu_result
  }
}





////////////////////////
//package Stage_EX
//
//import ALU.ALU
//
//import chisel3._
//import chisel3.util._
//
//class EX extends Module
//{
//  val io = IO(new Bundle {
//    val EM_pip_reg_WE = Input(UInt(1.W))
//    val id_ex_reg     = Input(UInt(188.W))  // second pipeline reg(Id/EX) = (A, B, LUI_res, EXT_Imm, PC+4, rd, ex,mem,wb)
//    val ex_mem_reg    = Output(UInt(141.W))  // third pipeline reg(ex/mem) = (ALU_T, B_din_src2, Lui_res,  Pc+4, mem,wb)
//    val br_target     = Output(UInt(32.W))  // forwarded jump target       (forwarded to fetch stage)
//    val jm_target     = Output(UInt(32.W))  // forwarded br target         (forwarded to fetch stage)
//    val j_br_mux_sel  = Output(UInt(1.W))   // forwarded j_or_br mux sel   (forwarded to fetch stage)
//    val is_br_j       = Output(UInt(1.W))   // forwarded is_br_j           (forwarded to fetch stage)
//    val alu_res0      = Output(UInt(1.W))   // forwarded comparison result (forwarded to fetch stage)
//  })
//
//  val ALU = Module(new ALU())
//
//  val src1   = Wire(UInt(32.W))
//  val src2   = Wire(UInt(32.W))
//  val ALUop  = Wire(UInt(4.W))
//  val aluRes = Wire(UInt(32.W))
//
//  // ALUsrcMuxsels, ALUop, J_BR_MUX_sel, is_br_j,
//  val EX_cntr = io.id_ex_reg(22,13) // 10 bit long
//
//  val alu_src1_mux_sel = EX_cntr(9,8)
//  val alu_src2_mux_sel = EX_cntr(7,6)
//
//  val PC_Plus_4 = io.id_ex_reg(59,28)
//  val Ext_imm = io.id_ex_reg(91,60)
//
//  val br_target = Wire(UInt(32.W))
//
//  ALUop := EX_cntr(5,2)
//  io.j_br_mux_sel := EX_cntr(1)
//  io.is_br_j := EX_cntr(0)
//  io.alu_res0 := aluRes(0)
//
//  //src1 := io.id_ex_reg(187, 156)
//  //src2 := io.id_ex_reg(155, 124)
//
//  ALU.io.src1 := src1
//  ALU.io.src2 := src2
//  ALU.io.ALUop := ALUop
//  aluRes := ALU.io.aluRes
//
//  when(alu_src1_mux_sel === 0.U(2.W)){
//    src1 := PC_Plus_4
//  } .elsewhen(alu_src1_mux_sel === 1.U(2.W)){
//    src1 := io.id_ex_reg(187, 156) //gpr_A_reg
//  }.otherwise{
//    src1 := io.id_ex_reg(187, 156) //gpr_A_reg
//  }
//
//  when(alu_src2_mux_sel === 0.U(2.W)){
//    src2 := io.id_ex_reg(155, 124) // grp_B_REG
//  } .elsewhen(alu_src2_mux_sel === 1.U(2.W)){
//    src2 := Ext_imm
//  }.otherwise{
//    src2 := Ext_imm
//  }
///////////////////////////
//  br_target := PC_Plus_4 + Ext_imm
//
//
//  val ex_mem_reg = RegInit(0.U(141.W))
//
//  // third pipeline reg(ex/mem) = (ALU_T, B_din_src2, Lui_res,  Pc+4, mem,wb)
//  when(io.EM_pip_reg_WE === 1.U(1.W)){
//    ex_mem_reg := Cat(aluRes, io.id_ex_reg(155, 124), io.id_ex_reg(123, 92),PC_Plus_4, io.id_ex_reg(12,0))
//  }
//
//  io.ex_mem_reg := ex_mem_reg
//  io.jm_target := aluRes
//  io.br_target := br_target
//}







