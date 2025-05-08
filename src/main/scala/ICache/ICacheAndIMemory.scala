// package ICache

// import DCache.Cache
// import Prefetcher.Prefetcher
// import UnifiedMemory.UnifiedMemory
// import chisel3._
// import config.IMEMsetupSignals

// class ICacheAndIMemory (I_memoryFile: String, cacheOnly : Boolean = true) extends Module {
//   val testHarness = IO(
//     new Bundle {
//       val setupSignals     = Input(new IMEMsetupSignals)
//       val requestedAddress = Output(UInt())
//     }
//   )

//   val io = IO(new Bundle {
//     val instr_addr = Input(UInt(32.W))
//     val instr_out = Output(UInt(32.W))
//     val valid = Output(Bool())
//     val busy = Output(Bool())
//   })

//   //val imem = Module(new InstructionMemory(I_memoryFile))
//   val imem = Module(new UnifiedMemory(I_memoryFile))
//   val icache = Module(new Cache("src/main/scala/ICache/ICacheContent.bin", read_only = true))
//   val pref = Module(new Prefetcher(I_memoryFile, cacheOnly))

//   //Prefetcher signals
//   pref.io.missAddress :=   io.instr_addr
//   pref.io.cacheBusy   :=   icache.io.busy
//   pref.io.miss        :=   icache.io.miss
//   icache.io.hit       :=   pref.io.hit
//   icache.io.prefData  :=   pref.io.result



//   icache.io.read_en := true.B // Always reading for instruction cache
//   icache.io.data_addr := io.instr_addr
//   io.valid := icache.io.valid
//   io.instr_out := icache.io.data_out
//   io.busy := icache.io.busy

//   imem.io.instAddr := icache.io.mem_data_addr // input to memory /4.U
//   icache.io.mem_data_out := imem.io.instOut // output from memory

//   imem.testHarness.imemSetup := testHarness.setupSignals
//   testHarness.requestedAddress := imem.testHarness.requestedAddressIMEM

//   //TODO Change these
//   imem.testHarness.dmemSetup.setup       := false.B
//   imem.testHarness.dmemSetup.dataAddress := 0.U
//   imem.testHarness.dmemSetup.dataIn      := 0.U
//   imem.testHarness.dmemSetup.writeEnable := false.B
//   imem.testHarness.dmemSetup.readEnable  := false.B

//   imem.io.dataWriteEnable := false.B
//   imem.io.dataReadEnable  := false.B
//   imem.io.dataIn          := 0.U
//   imem.io.dataAddr        := 0.U

//   imem.testHarness.testUpdatesDMEM := DontCare

//   imem.io.dataOut := DontCare
// }

//TODO just commented all