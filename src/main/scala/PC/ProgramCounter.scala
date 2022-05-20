
package PC
import chisel3._

class PC extends Module
{
  val io = IO(new Bundle{
    val WE         = Input(Bool())
    val PC_next    = Input(UInt(32.W))
    val PC_current = Output(UInt(32.W))
  })

  val PC = RegInit(0.U(32.W))

  when(io.WE)
  {
    PC := io.PC_next
  }

  io.PC_current := PC
}
