
package ALU

import config.AluOperation._
import chisel3._
import chisel3.util._

class add_subtractor extends Module
{
  val io = IO(new Bundle {
    val src1  = Input(UInt(32.W))
    val src2  = Input(UInt(32.W))
    val sub   = Input(Bool())
    val res   = Output(UInt(32.W))
    val c_out = Output(UInt(1.W))
    val ovf   = Output(UInt(1.W))
  })

  val op2  = Mux(io.sub, 1.U(32.W) + (~io.src2), io.src2)
  val Cn_1 = io.src1(31) ^ op2(31) ^ io.res(31)

  io.res   := io.src1 + op2
  io.c_out := (io.src1(31) & op2(31)) | (Cn_1 & (io.src1(31) ^ op2(31)))
  io.ovf   := io.c_out ^ Cn_1
}


class ALU extends Module {
  val io = IO(new Bundle {
    val src1   = Input(UInt(32.W))
    val src2   = Input(UInt(32.W))
    val ALUop  = Input(UInt(4.W))
    val aluRes = Output(UInt(32.W))
  })

  val adder_sub = Module(new add_subtractor())

  adder_sub.io.src1 := io.src1
  adder_sub.io.src2 := io.src2
  adder_sub.io.sub  := io.ALUop > 0.U(4.W)

  val SD  = adder_sub.io.res  // Add/Subtractor result
  val EQ  = SD === 0.U(32.W)
  val LT  = adder_sub.io.ovf ^ SD(31)
  val GE  = ~LT
  val LTU = ~adder_sub.io.c_out
  val GEU = ~LTU

  val shamt = io.src2(4,0)

  io.aluRes := 46.U(32.W)

//  val add :: sll :: srl :: sra :: or :: and :: xor :: slt :: sltu :: sub :: beq :: bne :: blt :: bge :: bltu :: bgeu :: Nil = Enum(16)

  switch(io.ALUop){
    is(add, sub){ io.aluRes := SD}                  // Additon, Subtraction

    is(sll){ io.aluRes := (io.src1 << shamt)}  // SLL, SLLI
    is(srl){ io.aluRes := (io.src1 >> shamt)}  // SRL, SRLI
    is(sra){ io.aluRes := (Fill(32,io.src1(31)) << (31.U(5.W) - (shamt - 1.U(5.W)))) | (io.src1 >> shamt)}  // SRA, SRAI

    is(or){ io.aluRes := io.src1 | io.src2}  // OR
    is(and){ io.aluRes := io.src1 & io.src2}  // AND
    is(xor){ io.aluRes := io.src1 ^ io.src2}  // XOR

    is(slt, blt){ io.aluRes := LT}   // SLT,  SLTI, BLT,
    is(sltu, bltu){ io.aluRes := LTU}  // SLTU, SLTIU,BLTU

    is(beq){ io.aluRes := EQ}   // BEQ
    is(bne){ io.aluRes := ~EQ}  // BNE

    is(bge){ io.aluRes := GE}   // BGE
    is(bgeu){ io.aluRes := GEU}   // BGEU
  }
}
