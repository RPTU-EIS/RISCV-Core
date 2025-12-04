package Prefetcher

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class lruModule (amount: Int, widthAmount: Int) extends Module{
  val io = IO(new Bundle {
    val flush = Input(Bool())                   //variable to show a buffer has been flushed
    val usedValid = Input(Bool())               //shows that a buffer has been used
    val used = Input(UInt(widthAmount.W))       //which buffer has been used
    val out = Output(UInt(widthAmount.W))       //the least recently used buffer
  })

  val lru = RegInit(VecInit(Seq.fill(amount)(0.U(8.W))))//lru register consisting of vector with amount elements, start values 0 and width of 8
  val lruReg = RegInit(0.U(widthAmount.W))

  when(io.usedValid === true.B){ //counts every register up except the one used
    for(i <- 0 until amount){
      when(i.asUInt =/= io.used){
        lru(i) := lru(i) + 1.U
        when(lru(i) > lru(lruReg)){
          lruReg := i.U
        }
      }
    }

    lru(io.used) := 0.U //sets register for the used buffer to 0
  }
  
  io.out := lruReg
}
