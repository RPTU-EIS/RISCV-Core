package DataMemory

import chisel3._
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemorySynthInit
import config.DMEMsetupSignals
import config.MemUpdates
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemorySynthInit

class DataMemory(I_memoryFile: String = "src/main/scala/DataMemory/dataMemVals") extends Module
{
  val testHarness = IO(
    new Bundle {
      val setup = Input(new DMEMsetupSignals)
      val testUpdates = Output(new MemUpdates)
    })


  val io = IO(
    new Bundle {
      val writeEnable = Input(Bool())
      val dataIn      = Input(UInt(32.W))
      val dataAddress = Input(UInt(12.W))

      val dataOut     = Output(UInt(32.W))
    })

  annotate(new ChiselAnnotation {
    override def toFirrtl = MemorySynthInit
  })
  //SyncReadMem will output the value of the address signal set in the previous cycle.
  //val data = SyncReadMem(4096, UInt(32.W))
  val d_memory = SyncReadMem(4096, UInt(32.W))
  loadMemoryFromFileInline(d_memory,I_memoryFile)

  val addressSource = Wire(UInt(32.W))
  val dataSource = Wire(UInt(32.W))
  val writeEnableSource = Wire(Bool())

  // For loading data
  when(testHarness.setup.setup){
    addressSource     := testHarness.setup.dataAddress
    dataSource        := testHarness.setup.dataIn
    writeEnableSource := testHarness.setup.writeEnable
  }.otherwise {
    addressSource     := io.dataAddress
    dataSource        := io.dataIn
    writeEnableSource := io.writeEnable
  }

  testHarness.testUpdates.writeEnable  := writeEnableSource
  testHarness.testUpdates.writeData    := dataSource
  testHarness.testUpdates.writeAddress := addressSource


//  io.dataOut := data(addressSource)
//  when(writeEnableSource){
//    data(addressSource) := dataSource
//  }
  io.dataOut := d_memory(addressSource)
  when(writeEnableSource){
    d_memory(addressSource) := dataSource
  }
}




//////////
//val io = IO(new Bundle{
//val lsMux = Input(UInt(3.W))
//val addr  = Input(UInt(32.W))
//val DM_WE = Input(Bool())
//val DM_RE = Input(Bool())
//val data_in = Input(UInt(32.W))
//val data_out = Output(UInt(32.W))
//})
//
//annotate(new ChiselAnnotation {
//override def toFirrtl = MemorySynthInit
//})
//
//val d_memory = Mem(10, UInt(32.W))
//val read_data = Wire(UInt(32.W))
//val mask = Wire(UInt(32.W))
//val data_out = Wire(UInt(32.W))
//
//loadMemoryFromFileInline(d_memory,I_memoryFile)
//
//when(io.DM_RE | io.DM_WE)
//{ // read from memory
//read_data := d_memory(io.addr)
//}.otherwise{
//read_data := 0.U(32.W)
//}
//
//when(io.lsMux(1,0) === 0.U(2.W)){ // lb
//data_out  := Mux(io.lsMux(2) === 1.U(1.W),read_data & "h000000ff".U(32.W),
//Mux(read_data(7) === 1.U(1.W),  read_data | "hffffff00".U(32.W), read_data & "h000000ff".U(32.W))
//)
//mask := "h000000ff".U(32.W)
//} .elsewhen(io.lsMux(1,0) === 1.U(2.W)){ // lh
//data_out  := Mux(io.lsMux(2) === 1.U(1.W),read_data & "h0000ffff".U(32.W),
//Mux(read_data(15) === 1.U(1.W),  read_data | "hffff0000".U(32.W), read_data & "h0000ffff".U(32.W))
//)
//mask := "h0000ffff".U(32.W)
//}.otherwise{ // lw
//data_out := read_data
//mask := "hffffffff".U(32.W)
//}
//
//
//when(io.DM_WE)
//{
//d_memory(io.addr) := (io.data_in & mask) | (read_data & (~mask))
//}
//
////  when(io.DM_RE)
////  {
//io.data_out := Mux(io.DM_RE, data_out, 0.U(32.W))
////  }