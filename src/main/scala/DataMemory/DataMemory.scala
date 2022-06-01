package DataMemory

import chisel3._
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemorySynthInit


class DataMemory(I_memoryFile: String = "dataMemVals.txt") extends Module
{
  val io = IO(new Bundle{
    val lsMux = Input(UInt(3.W))
    val addr  = Input(UInt(32.W))
    val DM_WE = Input(Bool())
    val DM_RE = Input(Bool())
    val data_in = Input(UInt(32.W))
    val data_out = Output(UInt(32.W))
  })

  annotate(new ChiselAnnotation {
    override def toFirrtl = MemorySynthInit
  })

  val d_memory = Mem(10, UInt(32.W))
  val read_data = Wire(UInt(32.W))
  val mask = Wire(UInt(32.W))

  loadMemoryFromFileInline(d_memory,I_memoryFile)

  when(io.DM_RE | io.DM_WE)
  { // read from memory
    read_data := d_memory(io.addr)
  }.otherwise{
    read_data := 0.U(32.W)
  }

  when(io.lsMux(1,0) === 0.U(2.W)){ // lb
    io.data_out  := Mux(io.lsMux(2) === 1.U(1.W),read_data & "h000000ff".U(32.W),
                                                  Mux(read_data(7) === 1.U(1.W),  read_data | "hffffff00".U(32.W), read_data & "h000000ff".U(32.W))
                        )
    mask := "h000000ff".U(32.W)
  } .elsewhen(io.lsMux(1,0) === 1.U(2.W)){ // lh
    io.data_out  := Mux(io.lsMux(2) === 1.U(1.W),read_data & "h0000ffff".U(32.W),
                                                  Mux(read_data(15) === 1.U(1.W),  read_data | "hffff0000".U(32.W), read_data & "h0000ffff".U(32.W))
                        )
    mask := "h0000ffff".U(32.W)
  }.otherwise{ // lw
    io.data_out := read_data
    mask := "hffffffff".U(32.W)
  }


  when(io.DM_WE)
  {
    d_memory(io.addr) := (io.data_in & mask) | (read_data & (~mask))
  }

}
