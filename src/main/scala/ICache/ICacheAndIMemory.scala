package ICache

import DCache.Cache
import InstructionMemory.InstructionMemory
import chisel3._
import config.IMEMsetupSignals

class ICacheAndIMemory (I_memoryFile: String) extends Module {
  val testHarness = IO(
    new Bundle {
      val setupSignals     = Input(new IMEMsetupSignals)
      val requestedAddress = Output(UInt())
    }
  )

  val io = IO(new Bundle {
    val instr_addr = Input(UInt(32.W))
    val instr_out = Output(UInt(32.W))
    val valid = Output(Bool())
    val busy = Output(Bool())
  })

  val imem = Module(new InstructionMemory(I_memoryFile))
  val icache = Module(new Cache("src/main/scala/ICache/ICacheContent.bin", read_only = true))

  icache.io.read_en := true.B // Always reading for instruction cache
  icache.io.data_addr := io.instr_addr
  io.valid := icache.io.valid
  io.instr_out := icache.io.data_out
  io.busy := icache.io.busy

  imem.io.instructionAddress := icache.io.mem_data_addr // input to memory /4.U
  icache.io.mem_data_out := imem.io.instruction // output from memory

  imem.testHarness.setupSignals := testHarness.setupSignals
  testHarness.requestedAddress := imem.testHarness.requestedAddress
}
