package InstructionMemory

import chisel3._
import chisel3.util._
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.{Annotation, MemorySynthInit}


class InstructionMemory(I_memoryFile: String = "instructions.txt") extends Module
{
  val io = IO(new Bundle{
    val addr  = Input(UInt(32.W))
    val instr = Output(UInt(32.W))
  })

  annotate(new ChiselAnnotation {
    override def toFirrtl = MemorySynthInit
  })

  val i_memory = Mem(10, UInt(32.W))


  loadMemoryFromFileInline(i_memory,I_memoryFile)

  io.instr := i_memory(io.addr(31,2))
}
