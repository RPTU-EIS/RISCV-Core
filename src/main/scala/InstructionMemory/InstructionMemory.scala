package InstructionMemory
import chisel3._
import chisel3.util._
import config.IMEMsetupSignals
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.{Annotation, MemorySynthInit}

class InstructionMemory (I_memoryFile: String = "src/main/scala/InstructionMemory/beq_test") extends Module
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

  val i_memory = SyncReadMem(4096, UInt(32.W))
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

  io.instruction := i_memory(addressSource(31,2))
}
