
package GPR

import chisel3._

//class R_PORT() extends Bundle
//{
//  val rs = UInt(4.W)
//
//}

class registerFile extends Module
{
  val io = IO(new Bundle {
    val WE  = Input(Bool())
    val rs1 = Input(UInt(5.W))
    val rs2 = Input(UInt(5.W))
    val rd  = Input(UInt(5.W))
    val data_A = Output(UInt(32.W))
    val data_B = Output(UInt(32.W))
    val data_in = Input(UInt(32.W))
  })

  val registerFile = Reg(Vec(32,UInt(32.W)))

  registerFile(0) := 0.U(32.W)   // register 0 hardwired to 0

  when(io.WE&(io.rd =/= 0.U(5.W))){  // if write enable write into rd register
    registerFile(io.rd) := io.data_in//Mux(io.rd =/= 0.U(5.W),io.data_in, 0.U(32.W))
  }

  io.data_A := registerFile(io.rs1)
  io.data_B := registerFile(io.rs2)

}
