package UnifiedMemory
import chisel3._
import chisel3.util._
import config.{IMEMsetupSignals, DMEMsetupSignals, MemUpdates}
import chisel3.experimental.{ChiselAnnotation, annotate}
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.{Annotation, MemorySynthInit}
import chisel3.experimental.annotate
import firrtl.transforms.NoDedupAnnotation



class UnifiedMemory(memFile: String) extends Module {
  val testHarness = IO(new Bundle {
    val imemSetup = Input(new IMEMsetupSignals)
    val dmemSetup = Input(new DMEMsetupSignals)
    val testUpdatesDMEM = Output(new MemUpdates)
    val requestedAddressIMEM = Output(UInt(32.W))
  })

  val io = IO(new Bundle {
    // Inst fetch port (R)
    val instAddr = Input(UInt(32.W))
    val instOut  = Output(UInt(32.W))

    // Data mem port (R/W)
    val dataWriteEnable = Input(Bool())
    val dataReadEnable  = Input(Bool())
    val dataIn          = Input(UInt(32.W))
    val dataAddr        = Input(UInt(32.W))
    val dataOut         = Output(UInt(32.W))
  })

//   annotate(new ChiselAnnotation {
//     override def toFirrtl = MemorySynthInit
//   })
//annotate(new MemoryFileInlineAnnotation(memory, memFile, "Hex"))
//TODO ?????
val self = this
annotate(new ChiselAnnotation {
  override def toFirrtl = NoDedupAnnotation(self.toNamed)
})



  val memory = SyncReadMem(2097152, UInt(32.W)) // sizes merged, next power of to //?
  loadMemoryFromFileInline(memory, memFile)


    val IMEM_BASE = "h00000000".U  //not used
    val DMEM_BASE = "h00100000".U  // 1048576 offset
    //?swap? // more?
    val dataAccessAddr = (io.dataAddr - DMEM_BASE)//!(31, 2)??


    testHarness.requestedAddressIMEM := io.instAddr

  // Instruction Address Source
  val instAddrSource = Wire(UInt(32.W))
  when(testHarness.imemSetup.setup) {
    instAddrSource := testHarness.imemSetup.address
  }.otherwise {
    instAddrSource := io.instAddr
  }

  // Data Address Source
  val dataAddrSource     = Wire(UInt(32.W))
  val dataInSource       = Wire(UInt(32.W))
  val writeEnableSource  = Wire(Bool())
  val readEnableSource   = Wire(Bool())

  when(testHarness.dmemSetup.setup) {
    dataAddrSource    := testHarness.dmemSetup.dataAddress //!- DMEM_BASE //!
    dataInSource      := testHarness.dmemSetup.dataIn
    writeEnableSource := testHarness.dmemSetup.writeEnable
    readEnableSource  := testHarness.dmemSetup.readEnable
  }.otherwise {
    dataAddrSource    := io.dataAddr //dataAccessAddr //!io.dataAddr
    dataInSource      := io.dataIn
    writeEnableSource := io.dataWriteEnable
    readEnableSource  := io.dataReadEnable
  }

  // Handle testHarness output
  // Instr 
  
  // Data
  testHarness.testUpdatesDMEM.writeEnable      := writeEnableSource
  testHarness.testUpdatesDMEM.readEnable       := readEnableSource
  testHarness.testUpdatesDMEM.writeData        := dataInSource
  testHarness.testUpdatesDMEM.writeAddress     := dataAddrSource

  
  // For loading data for Instr
  when(testHarness.imemSetup.setup){
    memory(instAddrSource) := testHarness.imemSetup.instruction
  }

  io.instOut := memory(instAddrSource(31,2)) //?(31,2)
  //!instAddrSource??
  
  
  
  // Write to memory Data
  when(writeEnableSource) {
    memory(dataAddrSource) := dataInSource
  }//!dataAddrSource(31,2)??

  // Read from memory
  io.dataOut := memory(dataAddrSource)
  //!dataAddrSource(31,2)??
}
