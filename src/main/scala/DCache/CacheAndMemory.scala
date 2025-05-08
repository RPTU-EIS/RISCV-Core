// /*
// RISC-V Pipelined Project in Chisel

// This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
// The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

// Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
// Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam, Kamal Baghirli.

// */

// package DCache

// import UnifiedMemory.UnifiedMemory
// import chisel3._
// import chisel3.util._
// import chisel3.experimental._
// import chisel3.util.experimental._
// import firrtl.annotations.MemoryLoadFileType


// class CacheAndMemory extends Module{
//   val io = IO(
//     new Bundle{
//       val write_data = Input(UInt(32.W))
//       val address = Input(UInt(32.W))
//       val write_en = Input(Bool())
//       val read_en = Input(Bool())
//       val valid = Output(Bool())
//       val data_out = Output(UInt(32.W))
//       val busy = Output(Bool())
//     }
//   )

//   val data_mem  = Module(new UnifiedMemory("src/main/scala/DataMemory/dataMemVals"))
//   val dcache = Module(new Cache("src/main/scala/DCache/CacheContent.bin", read_only = false))

//   data_mem.testHarness.dmemSetup.setup := 0.B
//   data_mem.testHarness.dmemSetup.dataIn := 0.U
//   data_mem.testHarness.dmemSetup.dataAddress := 0.U
//   data_mem.testHarness.dmemSetup.readEnable := 0.B
//   data_mem.testHarness.dmemSetup.writeEnable := 0.B

//   dcache.io.data_in.foreach(_ := io.write_data)
//   dcache.io.data_addr := io.address
//   dcache.io.write_en.foreach(_ := io.write_en)
//   dcache.io.read_en := io.read_en
//   io.valid := dcache.io.valid
//   io.data_out := dcache.io.data_out
//   io.busy := dcache.io.busy

//   data_mem.io.dataWriteEnable := dcache.io.mem_write_en
//   data_mem.io.dataReadEnable := dcache.io.mem_read_en
//   data_mem.io.dataIn := dcache.io.mem_data_in
//   data_mem.io.dataAddr := dcache.io.mem_data_addr / 4.U
//   dcache.io.mem_data_out := data_mem.io.dataOut


//   //TODO Change these
//   data_mem.testHarness.imemSetup.setup      := false.B
//   data_mem.testHarness.imemSetup.address    := 0.U
//   data_mem.testHarness.imemSetup.instruction := 0.U

//   data_mem.io.instAddr        := 0.U

//   data_mem.testHarness.requestedAddressIMEM := DontCare

//   data_mem.io.instOut := DontCare

//   //!
//   dcache.io.miss := DontCare
//   dcache.io.hit := false.B
//   dcache.io.prefData := DontCare
// }

//TODO just commented all
