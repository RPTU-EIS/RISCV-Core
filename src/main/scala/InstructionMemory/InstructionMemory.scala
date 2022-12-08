package InstructionMemory
import chisel3._
import chisel3.util._

class IMEMsetupSignals extends Bundle {
  val setup       = Bool()
  val address     = UInt(32.W)
  val instruction = UInt(32.W)
}
class InstructionMemory extends Module
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



  // SyncReadMem will output the value of the address signal set in the previous cycle.

  val instructions = SyncReadMem(4096, UInt(32.W))

  // The address we want to read at during operation. During setup it acts as a write address
  // leading to the somewhat uninformative name shown here.
  val addressSource = Wire(UInt(32.W))

  testHarness.requestedAddress := io.instructionAddress

  when(testHarness.setupSignals.setup){
    addressSource := testHarness.setupSignals.address
  }.otherwise {
    addressSource := io.instructionAddress
  }

  // For loading data
  when(testHarness.setupSignals.setup){
    instructions(addressSource) := testHarness.setupSignals.instruction
  }

  io.instruction := instructions(addressSource)
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