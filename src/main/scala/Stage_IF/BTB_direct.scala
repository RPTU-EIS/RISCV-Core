/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

*/

package Stage_IF
import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemoryLoadFileType

class BTB_direct extends Module {
  
  val io = IO(new Bundle {
    val currentPC           = Input(UInt(32.W))
    val newBranch          = Input(Bool())
    val entryPC             = Input(UInt(32.W))
    val entryBrTarget       = Input(UInt(32.W))
    val branchMispredicted  = Input(Bool())       // Acts as WrEn for Predictor Array
    val branchBehavior      = Input(Bool())       // 1 means Taken -- 0 means Not Taken 
    val updatePrediction    = Input(Bool())
    val prediction          = Output(Bool())      // 1 means Taken
    val btbHit              = Output(Bool())      // 1 means Hit
    val targetAdr           = Output(UInt(32.W))
  })


  // Define the btbEntry type
  class btbEntry extends Bundle(){
    val valid = UInt(1.W)
    val Tag = UInt(24.W)
    val branchTarget =  UInt(30.W)
  }

  // Predictor State
  val strongTaken :: weakTaken :: weakNotTaken :: strongNotTaken :: Nil = Enum(4)

  // BTB: 64 entry Cache
  val btb = Mem(64, UInt(55.W))   // NOTE: must use combinational read to select correct address next cycle (parallel to PC+4)
                                  // Layout: 1 bit (Valid) + 24 bits (Tag) + 30 bits (Target, ignoring Byte Offset)
  val btbOutput = Wire(new btbEntry)
  val btbInput = Wire(new btbEntry)

  // Array of 2bit FSMs -- state bits. It is indexed by currentPC[7:2]
  val predictorArray = Mem(64, UInt(2.W)) 
  val predictorOut = Wire(UInt(2.W))
  
  // Avoiding a simulation artifact during reset
  val btbWrEn = Wire(Bool())
  when(Module.reset.asBool){
    btbWrEn := 0.B
  }.otherwise{
    btbWrEn := io.newBranch && io.branchMispredicted  // Collecting new Branches only if mispredicted (i.e., they are taken!)
  }
  
  // Initialize BTB and Prediction Array
  var BTB_Init:String = "src/main/scala/Stage_IF/BTB_Init"
  var Predictor_Init:String = "src/main/scala/Stage_IF/Predictor_Init"
  loadMemoryFromFileInline(btb, BTB_Init,  MemoryLoadFileType.Hex)
  loadMemoryFromFileInline(predictorArray, Predictor_Init, MemoryLoadFileType.Binary)

  // Read BTB
  val btbOut = WireInit(btb(io.currentPC(7,2)))
  btbOutput.valid := btbOut(54)
  btbOutput.Tag := btbOut(53, 30)
  btbOutput.branchTarget := btbOut(29, 0)
  io.targetAdr := Cat(btbOutput.branchTarget, 0.U(2.W))
  when( btbOutput.valid === 1.B && btbOutput.Tag === io.currentPC(31,8) ){
    io.btbHit := 1.B  // Hit
  }
  .otherwise{
    io.btbHit := 0.B  // Miss
  }
  // Read Predictor
  predictorOut := predictorArray(io.currentPC(7,2))
  when( io.btbHit && (predictorOut === strongTaken || predictorOut === weakTaken) ){
    io.prediction := 1.B  // Taken
  }
  .otherwise{
    io.prediction := 0.B  // Not Taken. It is the default when BTB misses too
  }

  // Write BTB
  btbInput.Tag := io.entryPC(31,8)
  btbInput.branchTarget := io.entryBrTarget(31,2)
  btbInput.valid := 0.B // Default value, it's a wire
  when(btbWrEn === 1.B){
    btbInput.valid := 1.B
    btb(io.entryPC(7,2)) := btbInput.asUInt
  }
  // Write Predictor
  val prevPrediction1 = RegNext(predictorOut)
  val prevPrediction2 = RegNext(prevPrediction1)  // This register holds the prediction from 2 cycles before. It is used for updating Prediction after EX stage calculates branch behavior
  when(btbWrEn === 1.B){
    predictorArray(io.entryPC(7,2)) := strongTaken  // *Note*: what's the best initial state? Strong taken helps with Loops
  }
  .elsewhen(io.updatePrediction === 1.B){
    // Prediction FSMs next state logic
    switch ( prevPrediction2 ) { // Switch on the Current State
      is(strongNotTaken) {
        when(io.branchMispredicted === 1.B){
          predictorArray(io.entryPC(7,2)) := weakNotTaken
        }
        .otherwise{
          predictorArray(io.entryPC(7,2)) := strongNotTaken
        }
      }
      is(weakNotTaken) {
        when( io.branchMispredicted === 1.B ){
          predictorArray(io.entryPC(7,2)) := weakTaken
        }
        .otherwise{
          predictorArray(io.entryPC(7,2)) := strongNotTaken
        }
      }
      is(strongTaken) {
        when( io.branchMispredicted === 1.B ){
          predictorArray(io.entryPC(7,2)) := weakTaken
        }
        .otherwise{
          predictorArray(io.entryPC(7,2)) := strongTaken
        }
      }
      is(weakTaken) {
        when( io.branchMispredicted === 1.B ){
          predictorArray(io.entryPC(7,2)) := weakNotTaken
        }
        .otherwise{
          predictorArray(io.entryPC(7,2)) := strongTaken
        }
      }
    }
  }               

  
}
