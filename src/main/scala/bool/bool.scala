
package bool

import chisel3._

class bool extends Module
{
  val io = IO(new Bundle{
    val is_IF       = Input(Bool())
    val brCond_true = Input(Bool())
    val PC_WE       = Output(Bool())
  })
  io.PC_WE := io.is_IF | io.brCond_true
}
