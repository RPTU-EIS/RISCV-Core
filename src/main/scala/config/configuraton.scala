package config

import chisel3.util.Enum
import chisel3._
import chisel3.util._
import chisel3.util.{ BitPat, Cat }

object AluOperation {  // Different Alu operations
  val add :: sll :: slt :: sltu :: xor :: srl :: or :: and :: sub :: beq :: sra :: blt :: bltu :: inc4 :: copyb :: dc :: Nil = Enum(16)
}

object BranchOperation {  // Different Branch operations
  val beq :: neq :: gte :: lt :: gteu :: ltu :: jump :: dc :: Nil = Enum(8)
}

object States {  // instruction execution stages
  val fetch :: dec :: exec :: mem :: wb :: Nil = Enum(5)
}
/////
object branch_types {
  val beq  = 0.asUInt(3.W)
  val neq  = 1.asUInt(3.W)
  val gte  = 2.asUInt(3.W)
  val lt   = 3.asUInt(3.W)
  val gteu = 4.asUInt(3.W)
  val ltu  = 5.asUInt(3.W)
  val jump = 6.asUInt(3.W)
  val DC   = 7.asUInt(3.W)
}

object op1sel {
  val rs1 = 0.asUInt(1.W)
  val PC  = 1.asUInt(1.W)
  val DC  = 0.asUInt(1.W)
}

object op2sel {
  val rs2 = 0.asUInt(1.W)
  val imm = 1.asUInt(1.W)
  val DC  = 0.asUInt(1.W)
}
object ImmFormat {
  val ITYPE  = 0.asUInt(3.W)
  val STYPE  = 1.asUInt(3.W)
  val BTYPE  = 2.asUInt(3.W)
  val UTYPE  = 3.asUInt(3.W)
  val JTYPE  = 4.asUInt(3.W)
  val SHAMT  = 5.asUInt(3.W)
  val DC     = 0.asUInt(3.W)
}
object ALUOps {
  val ADD    = 0.U(4.W)
  val SUB    = 1.U(4.W)
  val AND    = 2.U(4.W)
  val OR     = 3.U(4.W)
  val XOR    = 4.U(4.W)
  val SLT    = 5.U(4.W)
  val SLL    = 6.U(4.W)
  val SLTU   = 7.U(4.W)
  val SRL    = 8.U(4.W)
  val SRA    = 9.U(4.W)
  val INC_4  = 10.U(4.W)
  val COPY_B = 11.U(4.W)

  val DC     = 15.U(4.W)
}

object ControlSignalsOB {
  def nop: ControlSignals = {
    val b = Wire(new ControlSignals)
    b.memToReg   := false.B
    b.regWrite   := false.B
    b.memRead    := false.B
    b.memWrite   := false.B
    b.branch     := false.B
    b.jump       := false.B
    b
  }
}


object ExtensionCases{ // for extension unit
  val id :: jal :: jalr :: auipc :: store :: i_type :: Nil = Enum(6)
}
// abstract trait hascontrolsignals extends Bundle
//{
//  val cs = new ControlSignals()
//}

//TODO: make no sense but change this???
class Instruction extends Bundle(){
  val instruction = UInt(32.W)

  def opcode      = instruction(6, 0)
  def registerRd  = instruction(11, 7)
  def funct3      = instruction(14, 12)
  def registerRs1 = instruction(19, 15)
  def registerRs2 = instruction(24, 20)
  def funct7      = instruction(31, 25)
  def funct6      = instruction(26, 31)

  def immediateIType = instruction(31, 20).asSInt
  def immediateSType = Cat(instruction(31, 25), instruction(11,7)).asSInt
  def immediateBType = Cat(instruction(31), instruction(7), instruction(30, 25), instruction(11, 8), 0.U(1.W)).asSInt
  def immediateUType = Cat(instruction(31, 12), 0.U(12.W)).asSInt
  def immediateJType = Cat(instruction(31), instruction(19, 12), instruction(20), instruction(30, 25), instruction(24, 21), 0.U(1.W)).asSInt
  def immediateZType = instruction(19, 15).zext

  def bubble(): Instruction = {
    val bubbled = Wire(new Instruction)
    bubbled.instruction := instruction
    bubbled.instruction(6, 0) := BitPat.bitPatToUInt(BitPat("b0010011"))
    bubbled
  }
}
object Inst{
  def NOP: Instruction = {
    val w = Wire(new Instruction)
    w.instruction := BitPat.bitPatToUInt(BitPat("b0000000000000000000000000110011"))
    w
  }
}
////////////

class ControlSignals extends Bundle(){
  val memToReg   = Bool()
  val regWrite   = Bool()
  val memRead    = Bool()
  val memWrite   = Bool()
  val branch     = Bool()
  val jump       = Bool()
}

//////////
class DMEMsetupSignals extends Bundle {
  val setup           = Bool()
  val writeEnable = Bool()
  val dataIn      = UInt(32.W)
  val dataAddress = UInt(32.W)
}

class MemUpdates extends Bundle {
  val writeEnable  = Bool()
  val writeData    = UInt(32.W)
  val writeAddress = UInt(32.W)
}

class IMEMsetupSignals extends Bundle {
  val setup       = Bool()
  val address     = UInt(32.W)
  val instruction = UInt(32.W)
}

class RegisterSetupSignals extends Bundle {
  val setup = Bool()
  val readAddress  = UInt(5.W)
  val writeEnable  = Bool()
  val writeAddress = UInt(5.W)
  val writeData    = UInt(32.W)
}

class RegisterUpdates extends Bundle {
  val writeEnable  = Bool()
  val writeData    = UInt(32.W)
  val writeAddress = UInt(5.W)
}


object lookup {
  def BEQ                = BitPat("b?????????????????000?????1100011")
  def BNE                = BitPat("b?????????????????001?????1100011")
  def BLT                = BitPat("b?????????????????100?????1100011")
  def BGE                = BitPat("b?????????????????101?????1100011")
  def BLTU               = BitPat("b?????????????????110?????1100011")
  def BGEU               = BitPat("b?????????????????111?????1100011")
  def JALR               = BitPat("b?????????????????000?????1100111")
  def JAL                = BitPat("b?????????????????????????1101111")
  def LUI                = BitPat("b?????????????????????????0110111")
  def AUIPC              = BitPat("b?????????????????????????0010111")
  def ADDI               = BitPat("b?????????????????000?????0010011")
  def SLLI               = BitPat("b000000???????????001?????0010011")
  def SLTI               = BitPat("b?????????????????010?????0010011")
  def SLTIU              = BitPat("b?????????????????011?????0010011")
  def XORI               = BitPat("b?????????????????100?????0010011")
  def SRLI               = BitPat("b000000???????????101?????0010011")
  def SRAI               = BitPat("b010000???????????101?????0010011")
  def ORI                = BitPat("b?????????????????110?????0010011")
  def ANDI               = BitPat("b?????????????????111?????0010011")
  def ADD                = BitPat("b0000000??????????000?????0110011")
  def SUB                = BitPat("b0100000??????????000?????0110011")
  def SLL                = BitPat("b0000000??????????001?????0110011")
  def SLT                = BitPat("b0000000??????????010?????0110011")
  def SLTU               = BitPat("b0000000??????????011?????0110011")
  def XOR                = BitPat("b0000000??????????100?????0110011")
  def SRL                = BitPat("b0000000??????????101?????0110011")
  def SRA                = BitPat("b0100000??????????101?????0110011")
  def OR                 = BitPat("b0000000??????????110?????0110011")
  def AND                = BitPat("b0000000??????????111?????0110011")
  def LW                 = BitPat("b?????????????????010?????0000011")
  def SW                 = BitPat("b?????????????????010?????0100011")
}