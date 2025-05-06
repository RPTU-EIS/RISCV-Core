package Prefetcher

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class streamBuffer (depth: Int, width: Int) extends Module{ // stream buffer module as a fifo queue
  val io = IO(new Bundle {
    val enq = Flipped(Decoupled(UInt(width.W)))   // enqueue interface
    val deq = Decoupled(UInt(width.W))            // dequeue interface
    val flush = Input(Bool())                     //input to flush the queue in case of a miss in the buffer
    val head = Output(UInt(32.W))                 //tag of head element
    val count = Output(UInt(5.W))                 //counter of elements currently in queue
  })
  //Queue module in chisel3.util standard library
  //standard: new Queue(gen: T, entries: Int, pipe: Boolean = false, flow: Boolean = false, useSyncReadMem: Boolean = false, hasFlush: Boolean = false)
  val queue = Module(new Queue(UInt(width.W), depth, pipe = false, flow = false, useSyncReadMem = false, hasFlush = true))

  //connect enqueue and dequeue
  queue.io.enq <> io.enq
  io.deq <> queue.io.deq

  //connect io to built in counter, first address in buffer and set up flush function
  io.count := queue.io.count
  io.head := queue.io.deq.bits(63,32)
  queue.io.flush.get := io.flush
}
