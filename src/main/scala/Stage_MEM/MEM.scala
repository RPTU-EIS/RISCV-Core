/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

*/

package Stage_MEM

import DCache.CacheAndMemory
//import DataMemory.DataMemory
import chisel3._
import chisel3.util._
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemorySynthInit
//import config.{DMEMsetupSignals, MemUpdates}
class MEM() extends Module {
//class MEM(DataFile: String) extends Module {
  // val testHarness = IO(
  //   new Bundle {
  //     val DMEMsetup      = Input(new DMEMsetupSignals)
  //     val DMEMpeek       = Output(UInt(32.W))

  //     val testUpdates    = Output(new MemUpdates)
  //   }) 

  val io = IO(
    new Bundle {
      val dataIn      = Input(UInt())
      val dataAddress = Input(UInt(32.W))
      val writeEnable = Input(Bool())
      val readEnable  = Input(Bool())
      val dataOut     = Output(UInt())
      val dataValid   = Output(Bool())
      val memBusy     = Output(Bool())

      val CM_write_data = Output(UInt(32.W))
      val CM_address = Output(UInt(32.W))
      val CM_write_en = Output(Bool())
      val CM_read_en = Output(Bool())
      val CM_data_out = Input(UInt(32.W))
      val CM_dataValid = Input(Bool())
      val CM_memBusy = Input(Bool())


    })

    val CM_write_data = Wire(UInt(32.W))
    val CM_address = Wire(UInt(32.W))
    val CM_write_en = Wire((Bool()))
    val CM_read_en = Wire(Bool())
    val CM_data_out = Wire(UInt(32.W))
    val CM_dataValid = Wire(Bool())
    val CM_memBusy = Wire(Bool())
  //val DMEM = Module(new DataMemory())
  //val DMEM = Module(new CacheAndMemory())

  //DMEM.testHarness.setup  := testHarness.DMEMsetup
  //testHarness.DMEMpeek    := DMEM.io.data_out
  //testHarness.testUpdates := 0.U.asTypeOf(new MemUpdates) //DMEM.testHarness.testUpdates

  //DMEM
  CM_write_data  := io.dataIn
  io.CM_write_data := CM_write_data

  //DMEM.io.address     := io.dataAddress
  CM_address := io.dataAddress
  io.CM_address := CM_address

  //DMEM.io.write_en    := io.writeEnable
  CM_write_en := io.writeEnable
  io.CM_write_en := CM_write_en

  //DMEM.io.read_en     := io.readEnable
  CM_read_en := io.readEnable
  io.CM_read_en := io.readEnable

  //Read data from DMEM
  //io.dataOut          := DMEM.io.data_out
  CM_data_out := io.CM_data_out
  io.dataOut := CM_data_out

  //io.dataValid        := DMEM.io.valid
  CM_dataValid := io.CM_dataValid
  io.dataValid := CM_dataValid

  //io.memBusy          := DMEM.io.busy
  CM_memBusy := io.CM_memBusy
  io.memBusy := CM_memBusy

}
