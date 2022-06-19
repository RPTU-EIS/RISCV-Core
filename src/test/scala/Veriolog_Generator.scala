
import ALU._
import bool._

import Control._
import DataMemory._
import DataPath._
import ExtenionUnit._
import GPR._
import InstructionMemory._
import PC._
import top_MC._

object VerilogGen extends App
{
//  (new chisel3.stage.ChiselStage).emitVerilog(new ALU())
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
  (new chisel3.stage.ChiselStage).emitVerilog(new top_MC())

}
