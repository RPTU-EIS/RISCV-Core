/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

*/

package GPR

import chisel3._
import chisel3.util._
//import config.{RegisterSetupSignals, RegisterUpdates}

class registerFile extends Module
{
  // val testHarness = IO(
  //   new Bundle {
  //     val setup        = Input(new RegisterSetupSignals)
  //     val testUpdates  = Output(new RegisterUpdates)
  //   }
  // )


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

//  when(testHarness.setup.setup){
  //   readAddress1 := testHarness.setup.readAddress
  //   readAddress2 := io.readAddress2
  //   writeData    := testHarness.setup.writeData
  //   writeEnable  := testHarness.setup.writeEnable
  //   writeAddress := testHarness.setup.readAddress
  // }.otherwise{
    readAddress1 := io.readAddress1
    readAddress2 := io.readAddress2
    writeData    := io.writeData
    writeEnable  := io.writeEnable
    writeAddress := io.writeAddress
  //}


  // testHarness.testUpdates.writeData := writeData
  // testHarness.testUpdates.writeEnable := writeEnable
  // testHarness.testUpdates.writeAddress := writeAddress


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
