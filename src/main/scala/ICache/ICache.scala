package ICache

import DCache.Cache
import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.util.experimental._
import firrtl.annotations.MemoryLoadFileType

class ICache(CacheFile: String) extends Module {
  val io = IO(new Bundle {
    val instr_addr = Input(UInt(32.W))
    val instr_out = Output(UInt(32.W))
    val valid = Output(Bool())
    val busy = Output(Bool())

    val mem_read_en = Output(Bool())
    val mem_addr = Output(UInt(32.W))
    val mem_data_in = Input(UInt(32.W))
  })

  val cache = Module(new Cache(CacheFile, read_only = true))

  cache.io.read_en := true.B // Always reading for instruction cache
 // cache.io.write_en.foreach(_ := false.B) // Not applicable for ICache
  cache.io.data_addr := io.instr_addr
 // cache.io.data_in.foreach(_ := 0.U) // Not applicable for ICache
  io.valid := cache.io.valid
  io.instr_out := cache.io.data_out
  io.busy := cache.io.busy

  // Memory interface connections
  io.mem_read_en := cache.io.mem_read_en
  io.mem_addr := cache.io.mem_data_addr
  cache.io.mem_data_out := io.mem_data_in
}
