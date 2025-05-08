package Prefetcher

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

import UnifiedMemory._

class Prefetcher(IMemFile: String, cacheOnly: Boolean = true) extends Module {
  val io = IO(new Bundle {//TODO Add test harness
    val missAddress = Input(UInt(32.W)) //address of miss
    val cacheBusy = Input(Bool()) //is cache working on something
    val hit = Output(Bool()) //show that there was a hit in a buffer
    val result = Output(UInt(32.W)) //output of instruction at the requested address
    val miss = Input(Bool()) 
  })
  //amount and depth of buffers, adjusted according to needs or tests
  val depth = 6
  val amount = 4
  val widthAmount = log2Ceil(amount + 1)

  //setup instruction memory
  val IMem = Module(new UnifiedMemory(IMemFile))

  IMem.testHarness.dmemSetup.setup       := false.B
  IMem.testHarness.dmemSetup.dataAddress := 0.U
  IMem.testHarness.dmemSetup.dataIn      := 0.U
  IMem.testHarness.dmemSetup.writeEnable := false.B
  IMem.testHarness.dmemSetup.readEnable  := false.B

  IMem.io.write := false.B
  IMem.io.req  := false.B
  IMem.io.wdata          := 0.U
  IMem.io.addr        := io.missAddress


  IMem.testHarness.imemSetup.setup      := false.B
  IMem.testHarness.imemSetup.address    := 0.U
  IMem.testHarness.imemSetup.instruction := 0.U
  IMem.testHarness.requestedAddressIMEM := DontCare


  //setup module for least recently used buffer
  val leastU = Module(new lruModule(amount, widthAmount))
  leastU.io.flush := false.B
  leastU.io.usedValid := false.B
  leastU.io.used := 0.U

  //multi-way prefetcher with depth and amount changed in testing
  val buffer = VecInit(Seq.fill(amount)(Module(new streamBuffer(depth, 64)).io))
  for (i <- 0 until amount) {
    buffer(i).flush := false.B //set flush to false so it doesnt go undefined or wrongly flushes
    // Connecting enqueue interface
    buffer(i).enq.valid := false.B //set to true when data is available for enqueue, false otherwise
    buffer(i).enq.bits := 0.U // Data to be enqueued
    // Connecting dequeue interface
    buffer(i).deq.ready := false.B //set to true when ready to receive data, false otherwise
  }
  
  //setup outputs
  io.hit := false.B
  io.result := 0.U

  //setup finite state machine
  val waitMiss :: compare :: fetch :: flush :: Nil = Enum(4)
  val state = RegInit(waitMiss)

  //register to save the next adress to fetch
  val nextAdress = RegInit(VecInit(Seq.fill(amount)(0.U(32.W))))

  //register and wire to save which buffer to use
  val fetchBuf = RegInit(0.U(widthAmount.W))
  val fetchBufWire = Wire(UInt(widthAmount.W))
  fetchBufWire := 0.U

  //variables to save if a buffer is empty or has a hit
  val emptyCheck = RegInit(0.U(1.W))
  val hitCheck = RegInit(0.U(1.W))

  switch(state) {
    is(waitMiss) {
      when(io.miss === true.B) {//wait until miss
      //check if a buffer is empty
        for (i <- 0 until amount) {
          when(buffer(i).count === 0.U) {//if empty
            fetchBuf := i.U //save which one is empty
            emptyCheck := 1.U //variable to show a buffer is empty
          }
        }

        //check if missAddress is at top of a buffer
        for (i <- 0 until amount) {
          when(buffer(i).head === io.missAddress && buffer(i).count =/= 0.U) {//when hit and buffer not empty
            fetchBuf := i.U //save which one is a hit, can overwrite the empty one because hit is more important
            hitCheck := 1.U //condition to show a buffer is hit
          }
        }
        state := compare
      }
    }
    is(compare) {//state to wait for hit and compare missAdress to top of buffer
      emptyCheck := false.B
      hitCheck := false.B

        when(hitCheck === true.B) { //hit in a buffer
            buffer(fetchBuf).deq.ready := true.B //start dequeue
            io.result := buffer(fetchBuf).deq.bits(31, 0) //output data
            IMem.io.addr := nextAdress(fetchBuf)//set next adress to fetch
            if(!cacheOnly){
              io.hit := true.B//set hit to true to show there was a hit in a buffer
            }

            state := fetch //go to fetch state
        }.elsewhen(emptyCheck === true.B) {//no hit, but a buffer is empty, start fetching at adress after miss
          IMem.io.addr := io.missAddress + 4.U //set adress to fetch
          nextAdress(fetchBuf) := io.missAddress + 4.U//update register
          state := fetch //go to fetch state
        }.otherwise { //no hit, no buffer empty, need to flush a buffer
          leastU.io.flush := true.B //set flush signal to true to get which buffer is lru to flush
          state := flush //go to flush state
        }
    }
    is(fetch) { //prefetch state
      when(buffer(fetchBuf).count =/= depth.U && !io.miss) { 
        //update lru
        leastU.io.usedValid := true.B
        leastU.io.used := fetchBuf

        //enqueue
        buffer(fetchBuf).enq.valid := true.B
        buffer(fetchBuf).enq.bits := Cat(nextAdress(fetchBuf), IMem.io.dataRead) 

        IMem.io.addr := nextAdress(fetchBuf) + 4.U //next address to fetch
        nextAdress(fetchBuf) := nextAdress(fetchBuf) + 4.U //update register
      }.elsewhen(buffer(fetchBuf).count =/= depth.U && io.miss){ 
        //update lru
        leastU.io.usedValid := true.B
        leastU.io.used := fetchBuf

        //enqueue
        buffer(fetchBuf).enq.valid := true.B
        buffer(fetchBuf).enq.bits := Cat(nextAdress(fetchBuf), IMem.io.dataRead)

        nextAdress(fetchBuf) := nextAdress(fetchBuf) + 4.U //update register

        //analog to waitMiss state
        for (i <- 0 until amount) {
          when(buffer(i).count === 0.U) {//if empty
            fetchBuf := i.U //save which one is empty
            emptyCheck := 1.U //variable to show a buffer is empty
          }
        }

        //check if missAddress is at top of a buffer
        for (i <- 0 until amount) {
          when(buffer(i).head === io.missAddress && buffer(i).count =/= 0.U) {//when hit and buffer not empty
            fetchBuf := i.U //save which one is a hit, can overwrite the empty one because hit is more important
            hitCheck := 1.U //condition to show a buffer is hit
          }
        }
        state := compare
      }.otherwise { //if no miss occurs and the buffer is fully  fetched go to idle state and wait for miss
        state := waitMiss //idle state
      }
    }

    is(flush) { //flush state
      buffer(leastU.io.out).flush := true.B //update lru
      fetchBuf := leastU.io.out //save which buffer was flushed to fetch into it

      nextAdress(leastU.io.out) := io.missAddress + 4.U //update register
      IMem.io.addr := io.missAddress + 4.U //set adress to fetch
      state := fetch //go to fetch state
    }
  }
}