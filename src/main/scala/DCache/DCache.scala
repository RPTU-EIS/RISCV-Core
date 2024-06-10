/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam, Kamal Baghirli.

*/

package DCache

import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.util.experimental._
import firrtl.annotations.MemoryLoadFileType

class DCache(CacheFile: String) extends Module {
  val io = IO(
    new Bundle {
      val write_en = Input(Bool())
      val read_en = Input(Bool())
      val data_addr = Input(UInt(32.W))
      val data_in = Input(UInt(32.W))
      val data_out = Output(UInt(32.W))
      val valid = Output(Bool())
      val busy = Output(Bool())

      val mem_write_en = Output(Bool())
      val mem_read_en = Output(Bool())
      val mem_data_in = Output(UInt(32.W))
      val mem_data_addr = Output(UInt(32.W))
      val mem_data_out = Input(UInt(32.W))
    }
  )

  val cache = Module(new Cache(CacheFile, read_only = false))

  cache.io.write_en.foreach(_ := io.write_en)
  cache.io.read_en := io.read_en
  cache.io.data_addr := io.data_addr
  cache.io.data_in.foreach(_ := io.data_in)
  io.valid := cache.io.valid
  io.data_out := cache.io.data_out
  io.busy := cache.io.busy

  // Memory interface connections
  io.mem_write_en := cache.io.mem_write_en
  io.mem_read_en := cache.io.mem_read_en
  io.mem_data_in := cache.io.mem_data_in
  io.mem_data_addr := cache.io.mem_data_addr
  cache.io.mem_data_out := io.mem_data_out
}
