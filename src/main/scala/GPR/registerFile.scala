
package GPR
import chisel3._
import chisel3.util._
import config.{RegisterSetupSignals, RegisterUpdates}

class registerFile extends Module
{
  val testHarness = IO(
    new Bundle {
      val setup        = Input(new RegisterSetupSignals)
      val testUpdates  = Output(new RegisterUpdates)
    }
  )


  val io = IO(
    new Bundle {
      val readAddress1 = Input(UInt(5.W))
      val readAddress2 = Input(UInt(5.W))
      val writeEnable  = Input(Bool())
      val writeAddress = Input(UInt(5.W))
      val writeData    = Input(UInt(32.W))

      val readData1    = Output(UInt(32.W))
      val readData2    = Output(UInt(32.W))
    })


  val registerFile = Mem(32, UInt(32.W))
  val readAddress1 = Wire(UInt(5.W))
  val readAddress2 = Wire(UInt(5.W))
  val writeAddress = Wire(UInt(5.W))
  val writeData    = Wire(UInt(32.W))
  val writeEnable  = Wire(Bool())

  when(testHarness.setup.setup){
    readAddress1 := testHarness.setup.readAddress
    readAddress2 := io.readAddress2
    writeData    := testHarness.setup.writeData
    writeEnable  := testHarness.setup.writeEnable
    writeAddress := testHarness.setup.readAddress
  }.otherwise{
    readAddress1 := io.readAddress1
    readAddress2 := io.readAddress2
    writeData    := io.writeData
    writeEnable  := io.writeEnable
    writeAddress := io.writeAddress
  }


  testHarness.testUpdates.writeData := writeData
  testHarness.testUpdates.writeEnable := writeEnable
  testHarness.testUpdates.writeAddress := writeAddress


  when(writeEnable){
    when(writeAddress =/= 0.U){
      registerFile(writeAddress) := writeData
    }
  }


  io.readData1 := 0.U
  io.readData2 := 0.U
  when(readAddress1 =/= 0.U){ io.readData1 := registerFile(readAddress1) }
  when(readAddress2 =/= 0.U){ io.readData2 := registerFile(readAddress2) }

}




//
//  val io = IO(new Bundle {
//    val WE  = Input(Bool())
//    val rs1 = Input(UInt(5.W))
//    val rs2 = Input(UInt(5.W))
//    val rd  = Input(UInt(5.W))
//    val data_A = Output(UInt(32.W))
//    val data_B = Output(UInt(32.W))
//    val data_in = Input(UInt(32.W))
//  })
//
//  val registerFile = Reg(Vec(32,UInt(32.W)))
//
//  registerFile(0) := 0.U(32.W)   // register 0 hardwired to 0
//
//  when(io.WE&(io.rd =/= 0.U(5.W))){  // if write enable write into rd register
//    registerFile(io.rd) := io.data_in//Mux(io.rd =/= 0.U(5.W),io.data_in, 0.U(32.W))
//  }
//
//  io.data_A := registerFile(io.rs1)
//  io.data_B := registerFile(io.rs2)