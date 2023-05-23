package Stage_IF
import chisel3._
import chisel3.util._
import config.btbEntry
import config.predictorState
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemoryLoadFileType

class BTB extends Module {
  
  val io = IO(new Bundle {
    val currentPC           = Input(UInt(32.W))
    val btbWriteEn          = Input(Bool())
    val entryPC             = Input(UInt(32.W))
    val entryBrTarget       = Input(UInt(32.W))
    val branchMispredicted  = Input(Bool())       // Acts as WrEn for Predictor Array
    val branchBehavior      = Input(Bool())       // 1 means Taken -- 0 means Not Taken 
    val prediction          = Output(Bool())      // 1 means Taken
    val btbHit              = Output(Bool())      // 1 means Hit
    val targetAdr           = Output(UInt(32.W))
  })

  /* BTB = direct mapped cache, where Tag is Branch Instructions (most significant 26 bits)
   and Data is Branch Target (32bit). It is indexed by currentPC[5:0]*/
  val btb = Mem(64, UInt(59.W)) //new btbEntry)   // NOTE: must use combinational read to select correct address next cycle (parallel to PC+4)
  val btbOutput = Wire(new btbEntry)
  val btbInput = Wire(new btbEntry)
  // Array of 2bit FSMs -- state bits. It is indexed by currentPC[5:0]
  val predictorArr = Mem(64, UInt(2.W)) 
  val predictorOut = Wire(UInt(2.W))
  
  // Initialize BTB and Prediction Array
  var BTB_Init:String = "src/main/scala/Stage_IF/BTB_Init"
  var Predictor_Init:String = "src/main/scala/Stage_IF/Predictor_Init"
  loadMemoryFromFileInline(btb, BTB_Init,  MemoryLoadFileType.Hex)
  loadMemoryFromFileInline(predictorArr, Predictor_Init, MemoryLoadFileType.Binary)

  // Read BTB
  val btbOut = WireInit(btb(io.currentPC(5,0)))
  btbOutput.valid := btbOut(58)
  btbOutput.Tag := btbOut(57, 32)
  btbOutput.branchTarget := btbOut(31, 0)
  io.targetAdr := btbOutput.branchTarget
  when( btbOutput.valid === 1.B && btbOutput.Tag === io.currentPC(31,6) ){
    io.btbHit := 1.B  // Hit
  }
  .otherwise{
    io.btbHit := 0.B  // Miss
  }
  // Read Predictor
  predictorOut := predictorArr(io.currentPC(5,0))
  when( io.btbHit && (predictorOut === predictorState.strongTaken || predictorOut === predictorState.weakTaken) ){
    io.prediction := 1.B  // Taken
  }
  .otherwise{
    io.prediction := 0.B  // Not Taken. It is the default when BTB misses too
  }

  // Write BTB
  btbInput.Tag := io.entryPC(31,26)
  btbInput.branchTarget := io.entryBrTarget
  btbInput.valid := 0.B // Initial value, it's a wire
  when(io.btbWriteEn === 1.B){
    btbInput.valid := 1.B
    btb(io.entryPC(5,0)) := btbInput.asUInt
  }
  // Write Predictor
  when(io.btbWriteEn === 1.B){
    predictorArr(io.entryPC(5,0)) := predictorState.strongTaken  // *Note*: what's the best initial state?
  }
  .elsewhen(io.branchMispredicted === 1.B){
    // Prediction FSMs next state logic
    switch ( predictorArr(io.entryPC(5,0)) ) { // Switch on the Current State
      is( predictorState.strongNotTaken ) {
        when( io.branchBehavior === 1.B ){
          predictorArr(io.entryPC(5,0)) := predictorState.weakNotTaken
        }
        .otherwise{
          predictorArr(io.entryPC(5,0)) := predictorState.strongNotTaken
        }
      }
      is( predictorState.weakNotTaken ) {
        when( io.branchBehavior === 1.B ){
          predictorArr(io.entryPC(5,0)) := predictorState.weakTaken
        }
        .otherwise{
          predictorArr(io.entryPC(5,0)) := predictorState.strongTaken
        }
      }
      is( predictorState.strongTaken ) {
        when( io.branchBehavior === 1.B ){
          predictorArr(io.entryPC(5,0)) := predictorState.strongTaken
        }
        .otherwise{
          predictorArr(io.entryPC(5,0)) := predictorState.weakTaken
        }
      }
      is( predictorState.weakTaken ) {
        when( io.branchBehavior === 1.B ){
          predictorArr(io.entryPC(5,0)) := predictorState.strongTaken
        }
        .otherwise{
          predictorArr(io.entryPC(5,0)) := predictorState.weakNotTaken
        }
      }
    }
  }               

  
}
