package main.scala

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


import RISCV_TOP._

object VerilogGen extends App
{
  emitVerilog(new RISCV_TOP(), Array("--target-dir", "generated-src"))
}
