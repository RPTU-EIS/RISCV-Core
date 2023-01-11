package GPR
import chisel3._
import chisel3.util._

class ByPassReg extends Module
{
  val io = IO(
    new Bundle {
      val readAddr     = Input(UInt())

      val writeAddr    = Input(UInt())
      val writeEnable  = Input(Bool())

      val registerData = Input(UInt())
      val writeData    = Input(UInt())

      val outData      = Output(UInt())

    }
  )


  when((io.readAddr =/= 0.U) & (io.readAddr === io.writeAddr) & io.writeEnable){
    io.outData := io.writeData
  }.otherwise{
    io.outData := io.registerData
  }
}
