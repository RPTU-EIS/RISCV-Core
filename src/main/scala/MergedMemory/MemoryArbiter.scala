package UnifiedMemory
import chisel3._
import chisel3.util._
import config.{IMEMsetupSignals, DMEMsetupSignals, MemUpdates}
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.transforms.NoDedupAnnotation

class MemArbiter(memFile: String) extends Module {

  val testHarness = IO(
    new Bundle {
      //Instruction
      val setupSignals     = Input(new IMEMsetupSignals)
      val requestedAddress = Output(UInt())
    }
  )

val io = IO(new Bundle {

  //Signals for caches
  //Instructions
  val iAddr = Input(UInt(32.W)) //req addr from I cache
  val iReq = Input(Bool())  //req asserted from I cache on miss

  //Data
  val dAddr = Input(UInt(32.W)) //req addr from D cache
  val dData = Input(UInt(32.W)) //data to write 
  val dWrite = Input(Bool())    //asserted when D cache wants to write
  val dReq = Input(Bool())  //req asserted from D cache on miss

  //Outputs
  val dataRead = Output(UInt(32.W)) //data from mem to send to caches
  val grantData = Output(Bool())        //is data for D cache from mem valid

  //!Prefetcher
  val pref_addr = Input(UInt(32.W))


  })



  io.dataRead := 0.U
  io.grantData := false.B

  val mem  = Module(new UnifiedMemory(memFile))

  //default inputs for memory module
  mem.io.addr := 0.U
  mem.io.write := false.B
  mem.io.wdata := 0.U
  mem.io.req := false.B

  
  mem.io.req := io.dReq || io.iReq //request to memory
  mem.io.addr := Mux(io.dReq, io.dAddr, io.iAddr) //set req addr according to prio
  mem.io.write := Mux(io.dReq, io.dWrite, false.B) // set write if data cache requests write
  mem.io.wdata := Mux(io.dReq, io.dData, 0.U) //set data to write

when(io.dReq){// Data first
  mem.io.req := io.dReq
  mem.io.addr := io.dAddr
  mem.io.write := io.dWrite
  mem.io.wdata := io.dData
}.otherwise{// IPref last
  mem.io.req := true.B
  mem.io.addr := io.pref_addr
  mem.io.write := false.B
  mem.io.wdata := 0.U
}

  //set outputs to caches
  io.dataRead := mem.io.dataRead
  io.grantData := io.dReq


  //test harness //TODO
  mem.testHarness.dmemSetup.setup := 0.B
  mem.testHarness.dmemSetup.dataIn := 0.U
  mem.testHarness.dmemSetup.dataAddress := 0.U
  mem.testHarness.dmemSetup.readEnable := 0.B
  mem.testHarness.dmemSetup.writeEnable := 0.B
  mem.testHarness.imemSetup := testHarness.setupSignals
  testHarness.requestedAddress := mem.testHarness.requestedAddressIMEM

  when(io.grantData){
    // printf(p"Arbiter Data:  dAddr: ${io.dAddr}, dReq: ${io.dReq}, grantData: ${io.grantData}, dataRead: 0x${Hexadecimal(io.dataRead)}, memdataRead: 0x${Hexadecimal(mem.io.dataRead)}\n")
    
  }.otherwise{
    // printf(p"Arbiter Pref: prefAddr: ${io.pref_addr}, !grantData: ${!io.grantData}, dataRead: 0x${Hexadecimal(io.dataRead)}, memdataRead: 0x${Hexadecimal(mem.io.dataRead)}\n")
   
  }
    
}