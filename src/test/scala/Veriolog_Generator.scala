
import ALU._
import DataMemory._
import ExtenionUnit._
import GPR._
import PC._
import bool._
import DataPath._
import InstructionMemory._

object VerilogGen extends App
{
  (new chisel3.stage.ChiselStage).emitVerilog(new add_subtractor())
  (new chisel3.stage.ChiselStage).emitVerilog(new ALU())
  (new chisel3.stage.ChiselStage).emitVerilog(new registerFile())
  (new chisel3.stage.ChiselStage).emitVerilog(new PC())
  (new chisel3.stage.ChiselStage).emitVerilog(new bool())
  (new chisel3.stage.ChiselStage).emitVerilog(new ExtenionUnit())
  (new chisel3.stage.ChiselStage).emitVerilog(new DataPath())
  (new chisel3.stage.ChiselStage).emitVerilog(new InstructionMemory())
  (new chisel3.stage.ChiselStage).emitVerilog(new DataMemory())

}
