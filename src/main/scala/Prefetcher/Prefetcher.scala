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
  io.mem_addr := io.missAddress - 4.U

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
  //val fetchBuf = Wire(UInt(widthAmount.W))
  //fetchBuf := 0.U
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

  val currentEnqueue = Wire(UInt(32.W))
  currentEnqueue := 0.U


  val missPrev = RegNext(io.miss, false.B)
  val missEdge = io.miss// && !missPrev

  //printf(p"buffer0: ${(buffer(0).head / 4.U) + 1.U}, buffer1: ${(buffer(1).head/ 4.U) + 1.U}, buffer2: ${(buffer(2).head/ 4.U) + 1.U}, buffer3: ${(buffer(3).head/ 4.U) + 1.U}\n")
  //printf(p"nextAddr(0): ${(nextAddress(0) / 4.U) + 1.U}, nextAddr(1): ${(nextAddress(1) / 4.U) + 1.U}, nextAddr(2): ${(nextAddress(2) / 4.U) + 1.U}, nextAddr(3): ${(nextAddress(3) / 4.U) + 1.U}\n")

switch(state) {

    is(waitMiss) {
      when(missEdge === true.B) {//TODO io.miss === true.B) {//wait until miss


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

        io.mem_addr := io.missAddress // mem request

        //next state
        state := compare
      }
    }



    is(compare) {//state to wait for hit and compare missAdress to top of buffer

          //analog to waitMiss state
          for (i <- 0 until amount) {
            when(buffer(i).count === 0.U) {//if empty
              fetchBufWire := i.U //save which one is empty
              fetchBuf := i.U
              emptyCheckWire := 1.U //variable to show a buffer is empty
            }
          }

          //check if missAddress is at top of a buffer
          for (i <- 0 until amount) {
            when((buffer(i).head === io.missAddress || currentEnqueue === io.missAddress) && buffer(i).count =/= 0.U) {//when hit and buffer not empty
              fetchBufWire := i.U //save which one is a hit, can overwrite the empty one because hit is more important
              fetchBuf := i.U
              hitCheckWire := 1.U //condition to show a buffer is hit
            }
          }

      
      when(hitCheckWire === true.B){//hit in a buffer
        buffer(fetchBufWire).deq.ready := true.B //start dequeue
        io.result := buffer(fetchBufWire).deq.bits(31, 0) //output data

        io.mem_addr := nextAddress(fetchBufWire)//set next adress to fetch 
        // can be nextAddress because if hit we have entries in the buffer and fetch at the bottom
        io.hit := true.B//set hit to true to show there was a hit in a buffer
          
        state := fetch //go to fetch state
        
        nextAddress(fetchBufWire) := nextAddress(fetchBufWire) + 4.U

        //printf(p"------------------------------------------------------------------------------\n")
        //printf(p"Prefetcher compare hitCheck result output: 0x${Hexadecimal(buffer(fetchBufWire).deq.bits(31, 0))} at: ${(io.missAddress / 4.U) + 1.U} at buffer: ${fetchBufWire}\n")
        //printf(p"------------------------------------------------------------------------------\n")

      }.elsewhen(emptyCheckWire === true.B ){//no hit, but a buffer is empty, enqueue and start fetching at address after miss
        io.result := io.mem_instr// directly give fetched instruction to output 
        //printf(p"------------------------------------------------------------------------------\n")
        //printf(p"Prefetcher compare emptyCheck result output: 0x${Hexadecimal(io.mem_instr)} at: ${(io.missAddress / 4.U) + 1.U} at buffer: ${fetchBufWire}, io.result: 0x${Hexadecimal(io.result)}\n")
        //printf(p"------------------------------------------------------------------------------\n")
        io.hit := true.B//set hit to true to show there was a hit in a buffer

        io.mem_addr := io.missAddress + 4.U //set address to fetch

        state := fetch //go to fetch state

        nextAddress(fetchBufWire) := io.missAddress + 8.U//update register to next address after this fetch


      }.otherwise { //no hit, no buffer empty, need to flush a buffer
        leastU.io.flush := true.B //set flush signal to true to get which buffer is lru to flush
        missFetch := true.B // new buffer used

        state := flush //go to flush state

      }

      emptyCheck := false.B
      hitCheck := false.B
    }




    is(fetch) { //prefetch state
      
      when(missFetch){ // fetch on miss

        io.result := io.mem_instr
        io.hit := true.B//set hit to true to show there was a hit in a buffer

        io.mem_addr := nextAddress(fetchBuf)//set next address to fetch
        nextAddress(fetchBuf) := nextAddress(fetchBuf) + 4.U

        missFetch := false.B

    //printf(p"------------------------------------------------------------------------------\n")
    //printf(p"Prefetcher fetch result output: 0x${Hexadecimal(io.mem_instr)} at: ${((nextAddress(fetchBuf) - 4.U) / 4.U) + 1.U} at buffer: ${fetchBuf}\n")
    //printf(p"------------------------------------------------------------------------------\n")


        //stay in state to fetch next instructions

      }.otherwise{ // fetch following instructions when no current miss

        when(buffer(fetchBuf).count =/= (depth.U - 1.U) && !missEdge ) { //buffer not full and no new miss

          when(!io.grantData){ // memory not busy with data

            //update lru
            leastU.io.usedValid := true.B
            leastU.io.used := fetchBuf

            //enqueue
            buffer(fetchBuf).enq.valid := true.B
            buffer(fetchBuf).enq.bits := Cat(nextAddress(fetchBuf) - 4.U, io.mem_instr)


            io.mem_addr := nextAddress(fetchBuf)//next address to fetch
            nextAddress(fetchBuf) := nextAddress(fetchBuf) + 4.U //update register

            
        //printf(p"------------------------------------------------------------------------------\n")
        //printf(p"Prefetcher enqueued 1 instr: 0x${Hexadecimal(io.mem_instr)} at: ${((nextAddress(fetchBuf) - 4.U) / 4.U) + 1.U}, normal nextAddr: : ${nextAddress(fetchBuf) + 4.U} at buffer: ${fetchBuf}\n")
        //printf(p"------------------------------------------------------------------------------\n")

          }
          state := fetch

        }.elsewhen(buffer(fetchBuf).count =/= (depth.U - 1.U) && missEdge ) { //TODO io.miss){ 
      
          when(!io.grantData){
            //update lru
            leastU.io.usedValid := true.B
            leastU.io.used := fetchBuf

            //enqueue
            buffer(fetchBuf).enq.valid := true.B
          //printf(p"------------------------------------------------------------------------------\n")
          //printf(p"Prefetcher enqueued 2 instr: 0x${Hexadecimal(io.mem_instr)} at: ${((nextAddress(fetchBuf) - 4.U) / 4.U) + 1.U}, normal nextAddr: : ${nextAddress(fetchBuf) + 4.U} at buffer: ${fetchBuf}\n")
          //printf(p"------------------------------------------------------------------------------\n")

            buffer(fetchBuf).enq.bits := Cat(nextAddress(fetchBuf) - 4.U, io.mem_instr)

            //check if current enqueue is the new miss
            currentEnqueue := nextAddress(fetchBuf) - 4.U
          }

          //next state
          state := compare
          
        }.otherwise { //if no miss occurs and the buffer is fully  fetched go to idle state and wait for miss
          when(!io.grantData){
            //update lru
            leastU.io.usedValid := true.B
            leastU.io.used := fetchBuf

            //enqueue
            buffer(fetchBuf).enq.valid := true.B
            //printf(p"------------------------------------------------------------------------------\n")
            //printf(p"Prefetcher enqueued 3 instr: 0x${Hexadecimal(io.mem_instr)} at: ${((nextAddress(fetchBuf) - 4.U) / 4.U) + 1.U}, normal nextAddr: : ${nextAddress(fetchBuf) + 4.U} at buffer: ${fetchBuf}\n")
            //printf(p"------------------------------------------------------------------------------\n")

            buffer(fetchBuf).enq.bits := Cat(nextAddress(fetchBuf) - 4.U, io.mem_instr)

          }
          state := waitMiss //idle state
        }
      }
    }

    is(flush) { //flush state

      buffer(leastU.io.out).flush := true.B //update lru
      fetchBuf := leastU.io.out //save which buffer was flushed to fetch into it


      nextAddress(leastU.io.out) := io.missAddress + 4.U //update register
      io.mem_addr := io.missAddress //set adress to fetch

      state := fetch //go to fetch state
    }
  }

}