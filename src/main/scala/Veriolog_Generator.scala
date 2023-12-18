package main.scala

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


import RISCV_TOP._

object VerilogGen extends App
{
  emitVerilog(new RISCV_TOP(), Array("--target-dir", "generated-src"))
  //emitVerilog(new top_MC.top_MC("src/test/programs/beq_test", "src/main/scala/DataMemory/dataMemVals"), Array("--target-dir", "generated-src"))
  //(new chisel3.stage.ChiselStage).emitVerilog(new RISCV_TOP)
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
  //emitVerilog(new MDU.MDU, Array("--target-dir", "generated-src"))

}
