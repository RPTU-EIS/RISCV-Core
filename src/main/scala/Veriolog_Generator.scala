package main.scala

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


import RISCV_TOP._
//import top_MC._
object VerilogGen extends App
{
  emitVerilog(new RISCV_TOP("src/test/programs/beq_test"), Array("--target-dir", "generated-src"))
  // (new chisel3.stage.ChiselStage).emitVerilog(new top_MC("src/test/programs/beq_test"))
  //  (new chisel3.stage.ChiselStage).emitVerilog(new bool())
  //
  //  (new chisel3.stage.ChiselStage).emitVerilog(new Control())
  //  (new chisel3.stage.ChiselStage).emitVerilog(new DataMemory())
  //
  //  (new chisel3.stage.ChiselStage).emitVerilog(new DataPath())
  //  (new chisel3.stage.ChiselStage).emitVerilog(new ExtenionUnit())
  //  (new chisel3.stage.ChiselStage).emitVerilog(new registerFile())
  //  (new chisel3.stage.ChiselStage).emitVerilog(new InstructionMemory())
  //  (new chisel3.stage.ChiselStage).emitVerilog(new PC())
  //  (new chisel3.stage.ChiselStage).emitVerilog(new DataMemory())
  // emitVerilog(new RISCV_TOP("src/main/scala/InstructionMemory/instructions"), Array("--target-dir", "generated-src"))

}
