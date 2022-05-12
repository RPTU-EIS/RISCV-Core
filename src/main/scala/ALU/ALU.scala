
package ALU

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

//  val op1       = io.src1
//  val op2       = Mux(io.sub, 1.U(32.W) + (~(io.src2)), io.src2)
//  val sum_diff  = op1 + op2
//  val Cn_1      = op1(31) ^ op2(31) ^ sum_diff(31)
//  val Cn        = (op1(31) & op2(31)) | (Cn_1 & (op1(31) ^ op2(31)))
//  val ovf       = Cn ^ Cn_1
//
//  io.res   := sum_diff
//  io.c_out := Cn
//  io.ovf   := ovf
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
  adder_sub.io.sub  := io.ALUop >= 7.U(4.W)

  val SD  = adder_sub.io.res  // Add/Subtractor result
  val EQ  = SD === 0.U(32.W)
  val LT  = adder_sub.io.ovf ^ SD(31)
  val GE  = ~LT
  val LTU = ~adder_sub.io.c_out
  val GEU = ~LTU

  val shamt = io.src2(18,0)

  io.aluRes := 0.U(32.W)
  switch(io.ALUop){
    is(0.U(4.W), 9.U(4.W)){ io.aluRes := SD}                  // Additon, Subtraction

    is(1.U(4.W)){ io.aluRes := io.src1 << shamt}  // SLL, SLLI
    is(2.U(4.W)){ io.aluRes := io.src1 >> shamt}  // SRL, SRLI
    is(3.U(4.W)){ io.aluRes := (Fill(32, io.src1(31)) << (32.U(19.W) - shamt)) | (io.src1 >> shamt)}  // SRA, SRAI

    is(4.U(4.W)){ io.aluRes := io.src1 | io.src2}  // OR
    is(5.U(4.W)){ io.aluRes := io.src1 & io.src2}  // AND
    is(6.U(4.W)){ io.aluRes := io.src1 ^ io.src2}  // XOR

    is(7.U(4.W), 12.U(4.W)){ io.aluRes := LT}   // SLT,  SLTI, BLT,
    is(8.U(4.W), 14.U(4.W)){ io.aluRes := LTU}  // SLTU, SLTIU,BLTU

    is(10.U(4.W)){ io.aluRes := EQ}   // BEQ
    is(11.U(4.W)){ io.aluRes := ~EQ}  // BNE

    is(13.U(4.W)){ io.aluRes := GE}   // BGE
    is(15.U(4.W)){ io.aluRes := GEU}   // BGEU
  }
}

object Verilogs extends App
{
  (new chisel3.stage.ChiselStage).emitVerilog(new add_subtractor())
  (new chisel3.stage.ChiselStage).emitVerilog(new ALU())
}
