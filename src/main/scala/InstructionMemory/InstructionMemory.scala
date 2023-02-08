package InstructionMemory
import chisel3._
import chisel3.util._
import config.IMEMsetupSignals
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.{Annotation, MemorySynthInit}

class InstructionMemory (I_memoryFile: String = "src/main/scala/InstructionMemory/instructions") extends Module
{
  val testHarness = IO(
    new Bundle {
      val setupSignals     = Input(new IMEMsetupSignals)
      val requestedAddress = Output(UInt())
    }
  )


  val io = IO(
    new Bundle {
      val instructionAddress = Input(UInt(32.W))
      val instruction        = Output(UInt(32.W))
    })



  annotate(new ChiselAnnotation {
    override def toFirrtl = MemorySynthInit
  })
  //SyncReadMem will output the value of the address signal set in the previous cycle.

  //val instructions = SyncReadMem(4096, UInt(32.W))
  val i_memory = Mem(4096, UInt(32.W))
  loadMemoryFromFileInline(i_memory,I_memoryFile)

  val addressSource = Wire(UInt(32.W))

  testHarness.requestedAddress := io.instructionAddress

  when(testHarness.setupSignals.setup){
    addressSource := testHarness.setupSignals.address
  }.otherwise {
    addressSource := io.instructionAddress
  }

  // For loading data
  when(testHarness.setupSignals.setup){
    i_memory(addressSource) := testHarness.setupSignals.instruction
  }

  //io.instruction := instructions(addressSource)
  io.instruction := i_memory(addressSource)
}



////////
//val io = IO(new Bundle{
//val addr  = Input(UInt(32.W))
//val instr = Output(UInt(32.W))
//})
//
//annotate(new ChiselAnnotation {
//override def toFirrtl = MemorySynthInit
//})
//
//val i_memory = Mem(100, UInt(32.W))
//
//
//loadMemoryFromFileInline(i_memory,I_memoryFile)
//
//io.instr := i_memory(io.addr(31,2))