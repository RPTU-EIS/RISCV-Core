
import ALU._
import GPR._
import PC._
import bool._

object VerilogGen extends App
{
  (new chisel3.stage.ChiselStage).emitVerilog(new add_subtractor())
  (new chisel3.stage.ChiselStage).emitVerilog(new ALU())
  (new chisel3.stage.ChiselStage).emitVerilog(new registerFile())
  (new chisel3.stage.ChiselStage).emitVerilog(new PC())
  (new chisel3.stage.ChiselStage).emitVerilog(new bool())
}
