package Prefetcher

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile


class Prefetcher extends Module {
  val io = IO(new Bundle {//TODO Add test harness??
    val missAddress = Input(UInt(32.W)) //address of miss
    val cacheBusy = Input(Bool()) //is cache working on something
    val hit = Output(Bool()) //show that there was a hit in a buffer
    val result = Output(UInt(32.W)) //output of instruction at the requested address
    val miss = Input(Bool()) 


    //! Memory Connections
    val mem_instr = Input(UInt(32.W))
    val grantData  = Input(Bool()) 

    val mem_addr = Output(UInt(32.W))
  })

  //! Memory Connections standard values
  io.mem_addr := io.missAddress


  //amount and depth of buffers, adjusted according to needs or tests
  val depth = 6
  val amount = 4
  val widthAmount = log2Ceil(amount + 1)



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
  val nextAddress = RegInit(VecInit(Seq.fill(amount)(0.U(32.W))))

  //register and wire to save which buffer to use
  val fetchBuf = RegInit(0.U(widthAmount.W))
  val fetchBufWire = Wire(UInt(widthAmount.W))
  fetchBufWire := 0.U

  //variables to save if a buffer is empty or has a hit
  val emptyCheck = RegInit(0.U(1.W))
  val emptyCheckWire = Wire(UInt(1.W))
  emptyCheckWire := 0.U
  val hitCheck = RegInit(0.U(1.W))
  val hitCheckWire = Wire(UInt(1.W))
  hitCheckWire := 0.U


  val missFetch = RegInit(false.B)

  // printf(p"buffer0: ${buffer(0).head}, buffer1: ${buffer(1).head}, buffer2: ${buffer(2).head}, buffer3: ${buffer(3).head}\n")
  // printf(p"nextAddr(0): ${nextAddress(0)}, nextAddr(1): ${nextAddress(1)}, nextAddr(2): ${nextAddress(2)}, nextAddr(3): ${nextAddress(3)}\n")
  
  
  switch(state) {
    is(waitMiss) {
      // printf(p"PREF waitMiss\n")
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
        missFetch := true.B
      }
    }
    is(compare) {//state to wait for hit and compare missAdress to top of buffer
    // printf(p"PREF compare missAddress${io.missAddress}\n")

    for (i <- 0 until amount) {
          when(buffer(i).count === 0.U) {//if empty
            fetchBufWire := i.U //save which one is empty
            emptyCheckWire := 1.U //variable to show a buffer is empty
          }
        }

        //check if missAddress is at top of a buffer
        for (i <- 0 until amount) {
          when(buffer(i).head === io.missAddress && buffer(i).count =/= 0.U) {//when hit and buffer not empty
            fetchBufWire := i.U //save which one is a hit, can overwrite the empty one because hit is more important
            hitCheckWire := 1.U //condition to show a buffer is hit
          }
        }
      emptyCheck := false.B
      hitCheck := false.B

        when(hitCheck === true.B || hitCheckWire === 1.U) { //hit in a buffer
          //// printf(p"PREF compare hit\n")
            buffer(fetchBufWire).deq.ready := true.B //start dequeue
            io.result := buffer(fetchBufWire).deq.bits(31, 0) //output data
            io.mem_addr := nextAddress(fetchBufWire)//set next adress to fetch
            io.hit := true.B//set hit to true to show there was a hit in a buffer

            state := fetch //go to fetch state
        }.elsewhen(emptyCheck === true.B || emptyCheckWire === 1.U) {//no hit, but a buffer is empty, start fetching at adress after miss
          //// printf(p"PREF compare empty ${fetchBufWire}\n")
          io.mem_addr := io.missAddress//! + 4.U //set adress to fetch
          nextAddress(fetchBufWire) := io.missAddress + 4.U//update register
          missFetch := true.B
          state := fetch //go to fetch state
        }.otherwise { //no hit, no buffer empty, need to flush a buffer
          //// printf(p"PREF compare flush\n")
          leastU.io.flush := true.B //set flush signal to true to get which buffer is lru to flush
          missFetch := true.B
          state := flush //go to flush state
        }
        // when(hitCheck === true.B || hitCheckWire === 1.U) { //hit in a buffer
        //   //// printf(p"PREF compare hit\n")
        //     buffer(fetchBuf).deq.ready := true.B //start dequeue
        //     io.result := buffer(fetchBuf).deq.bits(31, 0) //output data
        //     io.mem_addr := nextAddress(fetchBuf)//set next adress to fetch
        //     io.hit := true.B//set hit to true to show there was a hit in a buffer

        //     state := fetch //go to fetch state
        // }.elsewhen(emptyCheck === true.B || emptyCheckWire === 1.U) {//no hit, but a buffer is empty, start fetching at adress after miss
        //   //// printf(p"PREF compare empty ${fetchBuf}\n")
        //   io.mem_addr := io.missAddress//! + 4.U //set adress to fetch
        //   nextAddress(fetchBuf) := io.missAddress + 4.U//update register
        //   missFetch := true.B
        //   state := fetch //go to fetch state
        // }.otherwise { //no hit, no buffer empty, need to flush a buffer
        //   //// printf(p"PREF compare flush\n")
        //   leastU.io.flush := true.B //set flush signal to true to get which buffer is lru to flush
        //   missFetch := true.B
        //   state := flush //go to flush state
        // }
    }
    is(fetch) { //prefetch state
    // printf(p"PREF fetch\n")
    //!
    when(missFetch){
      // printf(p"PREF missFetch missAddress: ${io.missAddress}\n")
      io.result := Cat(io.missAddress, io.mem_instr) 
      io.mem_addr := nextAddress(fetchBuf)//set next adress to fetch
      nextAddress(fetchBuf) := io.missAddress + 4.U
      io.hit := true.B//set hit to true to show there was a hit in a buffer
      missFetch := false.B
    }.otherwise{
      when(buffer(fetchBuf).count =/= depth.U && !io.miss) { 
        // printf(p"PREF fetch no full no miss\n")
        when(!io.grantData){
        //update lru
        leastU.io.usedValid := true.B
        leastU.io.used := fetchBuf

        //enqueue
        buffer(fetchBuf).enq.valid := true.B
        buffer(fetchBuf).enq.bits := Cat(nextAddress(fetchBuf), io.mem_instr)//! - 4.U, io.mem_instr) 
        //// printf(p"PREF fetch no full no miss enq\n")
        io.mem_addr := nextAddress(fetchBuf) + 4.U//next address to fetch
        nextAddress(fetchBuf) := nextAddress(fetchBuf) + 4.U //update register
        }
      }.elsewhen(buffer(fetchBuf).count =/= depth.U && io.miss){ 
        // printf(p"PREF fetch no full miss ${io.missAddress}\n")
        when(!io.grantData){
        //update lru
        leastU.io.usedValid := true.B
        leastU.io.used := fetchBuf

        //enqueue
        buffer(fetchBuf).enq.valid := true.B
        buffer(fetchBuf).enq.bits := Cat(nextAddress(fetchBuf), io.mem_instr)//! - 4.U, io.mem_instr)
        
        nextAddress(fetchBuf) := nextAddress(fetchBuf) + 4.U //update register
        }
        //analog to waitMiss state
        for (i <- 0 until amount) {
          when(buffer(i).count === 0.U) {//if empty
            fetchBuf := i.U //save which one is empty
            emptyCheck := 1.U //variable to show a buffer is empty
          }
        }

        //check if missAddress is at top of a buffer
        for (i <- 0 until amount) {
          //// printf(p"PREF fetch count: ${buffer(i).head}, head: ${buffer(i).head}, io.missAddress: ${io.missAddress}\n")
          when(buffer(i).head === io.missAddress && buffer(i).count =/= 0.U) {//when hit and buffer not empty
            //// printf(p"PREF fetch hit\n")
            fetchBuf := i.U //save which one is a hit, can overwrite the empty one because hit is more important
            hitCheck := 1.U //condition to show a buffer is hit
          }
        }
        state := compare
        
        
      }.otherwise { //if no miss occurs and the buffer is fully  fetched go to idle state and wait for miss
        // printf(p"PREF fetch to waitMiss\n")
        state := waitMiss //idle state
      }
    }
    }

    is(flush) { //flush state
    // printf(p"PREF flush\n")
      buffer(leastU.io.out).flush := true.B //update lru
      fetchBuf := leastU.io.out //save which buffer was flushed to fetch into it


      nextAddress(leastU.io.out) := io.missAddress + 4.U //update register
      io.mem_addr := io.missAddress //!+ 4.U //set adress to fetch
      state := fetch //go to fetch state
    }
  }
  //// printf(p"PREF io.hit: ${io.hit}, io.result: 0x${Hexadecimal(io.result.asUInt)}, io.miss: ${io.miss}, io.missAddress: ${io.missAddress}, io.mem_instr: 0x${Hexadecimal(io.mem_instr.asUInt)}, io.mem_addr: ${io.mem_addr}\n")
  // printf(p"buffer enq valid:${buffer(fetchBuf).enq.valid} instr: 0x${Hexadecimal(io.mem_instr.asUInt)} at addr ${nextAddress(fetchBuf)}\n")
}