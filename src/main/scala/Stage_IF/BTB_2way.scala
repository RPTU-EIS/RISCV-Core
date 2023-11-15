/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

  // --------------------------------------------------------------
  // BTB Module Documentation
  // --------------------------------------------------------------

    Parameters Definition (btbParameters):
        numSets: Number of sets in the BTB.
        numWays: Number of ways in the BTB (associativity).
        btbEntrySize: The size of each BTB entry in bits, including the Valid bit, Tag bits, and Target bits.

    Module Definition (BTB):
        The module BTB extends Module and has IO ports defined for communication with the surrounding circuit.

    Predictor State Definition:
        An enumeration Enum(4) defines four states for the two-state predictor FSM: strongTaken, weakTaken, weakNotTaken, and strongNotTaken.

    BTB and Predictor Memory Declarations:
        btb: A two-way set-associative cache (BTB) is implemented as a memory with dimensions numSets * numWays and each entry is of type btbEntry. Each entry contains a valid bit, a tag, and a branchTarget.
        lruArray: An array that stores the Least Recently Used (LRU) information for each set in the BTB. It is used for entry replacement.
        predictorArray: A memory that stores the two-bit FSM states for each set in the BTB.

    Read from BTB:
        The setIdx is extracted from the current program counter (io.currentPC) to determine which set in the BTB to access.
        The tag is extracted from the program counter, and the valid bits, tag bits, and branch target are read from the corresponding set in the BTB based on the setIdx.
        The predictorArray is also read based on the setIdx to get the current state of the FSM prediction.
        The outputs io.btbHit and io.targetAdr are set based on whether the branch target is found in the BTB (way0Hit or way1Hit).

    Update LRU Bit:
        The LRU bit is updated based on which way (way0 or way1) was accessed during a read from the BTB. This information is stored in the lruArray.

    Write to BTB:
        The btbUpdate contains the information of the new branch target to be written to the BTB.
        The BTB is updated based on the replIdx (index for replacement), and the new entry is written to either way0 or way1 based on the LRU information.

    Update Predictor FSM:
        The two-bit FSM predictor is updated based on the current state (prevPrediction2) and whether the branch was mispredicted (io.branchMispredicted). The predictor is updated with one of the four states defined in the FSM.

*/

package Stage_IF
import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFileInline
import firrtl.annotations.MemoryLoadFileType

case class btb2WayParameters(
  numSets: Int = 32, // Number of sets in the BTB
  numWays: Int = 2,  // Number of ways in the BTB (associativity)
  btbEntrySize: Int = 55 // Layout: 1 bit (Valid) + 25 bits (Tag) + 30 bits (Target, ignoring Byte Offset)
)


class BTB_2way(parameters: btb2WayParameters = btb2WayParameters()) extends Module {

  val io = IO(new Bundle {
    val currentPC           = Input(UInt(32.W))
    val newBranch           = Input(Bool())
    val entryPC             = Input(UInt(32.W))
    val entryBrTarget       = Input(UInt(32.W))
    val branchMispredicted  = Input(Bool())       // Acts as WrEn for Predictor Array
    val updatePrediction    = Input(Bool())
    val stall               = Input(Bool())
    val prediction          = Output(Bool())      // 1 means Taken
    val btbHit              = Output(Bool())      // 1 means Hit
    val targetAdr           = Output(UInt(32.W))
  })

  val numSets = parameters.numSets
  val numWays = parameters.numWays
  val btbEntrySize = parameters.btbEntrySize

  val indexBits = log2Ceil(numSets) // number of index bits needed
  val tagBits = 30 - indexBits // number of remaining tag bits

  require(isPow2(numSets))

  // Define the btbEntry type
  class btbEntry extends Bundle {
    val valid = Bool()
    val tag = UInt((btbEntrySize - 31).W)
    val branchTarget = UInt(30.W) // Target, ignoring Byte Offset in Address (IMem is word-aligned)
  }

  // Predictor State
  val strongTaken :: weakTaken :: weakNotTaken :: strongNotTaken :: Nil = Enum(4)

  val btb        = Mem(numSets * numWays, new btbEntry) // NOTE: must use combinational read in current core design to select correct address next cycle (parallel to PC+4)
  val lruArray   = Mem(numSets, UInt(1.W))              // LRU bit for entry replacement

  val predictorArray = Mem(numSets * numWays, UInt(2.W))
  val predictorOut = Wire(UInt(2.W))

  // Avoiding a simulation artifact during reset
  val btbWriteNewEntry = Wire(Bool())
  when(Module.reset.asBool){
    btbWriteNewEntry := false.B
  }.otherwise{
    btbWriteNewEntry := io.newBranch && io.branchMispredicted && io.stall === false.B // Collecting new Branches only if mispredicted (i.e., they are taken!)
  }

  // Initialize BTB and Prediction Array
  // TODO: Not working for complex datatypes like btbEntry
  //var BTB_Init:String = "src/main/scala/Stage_IF/BTB_Init"
  var Predictor_Init:String = "src/main/scala/Stage_IF/Predictor_Init"
  var LRU_Init:String = "src/main/scala/Stage_IF/LRU_Init"
  //loadMemoryFromFileInline(btb, BTB_Init,  MemoryLoadFileType.Hex)
  loadMemoryFromFileInline(lruArray, LRU_Init, MemoryLoadFileType.Binary)
  loadMemoryFromFileInline(predictorArray, Predictor_Init, MemoryLoadFileType.Binary)


  // --------------------------------------------------------------
  // READ FROM BTB
  // --------------------------------------------------------------

  val setIdx = Wire(UInt(6.W)) // Index to access the BTB memory --> 5 Bits needes to address 32 sets
  setIdx := io.currentPC((indexBits + 1), 2)
  val tag = io.currentPC(31, (indexBits + 2)) // Tag within the BTB cache 
 
  // Read valid bits from BTB entry
  val way0Valid = btb((setIdx << 1).asUInt).valid
  val way1Valid = btb((setIdx << 1 | 1.U).asUInt).valid

  // Read tag bits from the BTB entry
  val way0Tag = btb((setIdx << 1).asUInt).tag
  val way1Tag = btb((setIdx << 1 | 1.U).asUInt).tag

  // Read data from the BTB memory based on the set index
  val way0BranchTarget = btb((setIdx << 1).asUInt).branchTarget
  val way1BranchTarget = btb((setIdx << 1 | 1.U).asUInt).branchTarget

  val way0Hit      = WireInit(false.B)
  val way1Hit      = WireInit(false.B)
  val BranchTarget = WireInit(0.U(30.W))

  val way0Valid_debug = WireInit(false.B)
  val way1Valid_debug = WireInit(false.B)

  way0Valid_debug := way0Valid
  way1Valid_debug := way1Valid

  way0Hit       := (way0Tag === tag) && way0Valid_debug
  way1Hit       := (way1Tag === tag) && way1Valid_debug
  BranchTarget  := Mux(way0Hit, way0BranchTarget, way1BranchTarget)

  val hitEntry  = Mux(way0Hit, (setIdx << 1).asUInt, (setIdx << 1 | 1.U).asUInt) 

  io.btbHit     := way0Hit || way1Hit
  io.targetAdr  := Cat(BranchTarget, 0.U(2.W)) // restore full 32-bit target address


  // --------------------------------------------------------------
  // READ PREDICTION
  // --------------------------------------------------------------

  predictorOut  := predictorArray(hitEntry)
  io.prediction := io.btbHit && (predictorOut === strongTaken || predictorOut === weakTaken)

  // update LRU Bit 
  // Don't get confused, bits are swapped. If we use way 0, way 1 is the least recently used and vice versa
  when(way0Hit){
    lruArray((setIdx << 1).asUInt) := 1.U
  } .elsewhen (way1Hit){
    lruArray((setIdx << 1).asUInt) := 0.U
  }


  // --------------------------------------------------------------
  // WRITE TO BTB
  // --------------------------------------------------------------

  val btbUpdate = Wire(new btbEntry)
  val replIdx   = io.entryPC((indexBits + 1), 2)
  val lruWay    = lruArray(replIdx)

  btbUpdate.valid        := true.B
  btbUpdate.tag          := io.entryPC(31, (indexBits + 2))
  btbUpdate.branchTarget := io.entryBrTarget(31, 2) // instr. mem is word aligned, last two bits are always zero

  when(btbWriteNewEntry) {
    when((btb((replIdx << 1).asUInt).tag === btbUpdate.tag) && btb((replIdx << 1).asUInt).valid) {

      btb((replIdx << 1).asUInt)        := btbUpdate

    }.elsewhen((btb((replIdx << 1 | 1.U).asUInt).tag === btbUpdate.tag) && btb((replIdx << 1 | 1.U).asUInt).valid) {

      btb((replIdx << 1 | 1.U).asUInt)     := btbUpdate

    }.otherwise{

      btb((replIdx << 1 | lruWay).asUInt)  := btbUpdate

    }
  }
  


  // --------------------------------------------------------------
  // UPDATE 2-BIT FSM FOR PREDICTION
  // --------------------------------------------------------------

  val prevPrediction1 = RegNext(predictorOut)
  val prevPrediction2 = RegNext(prevPrediction1)  


  when(btbWriteNewEntry) {
    predictorArray(io.entryPC((indexBits + 1), 2)) := strongTaken // TODO: what's the best initial state? Strong taken helps with Loops
  }.elsewhen(io.updatePrediction === true.B && io.stall === false.B){
    // Prediction FSMs next state logic
    switch ( prevPrediction2 ) { // Switch on the Current State
      is( strongNotTaken ) {
        when(io.branchMispredicted){
          predictorArray(io.entryPC((indexBits + 1),2)) := weakNotTaken
        }
        .otherwise{
          predictorArray(io.entryPC((indexBits + 1),2)) := strongNotTaken
        }
      }
      is( weakNotTaken ) {
        when(io.branchMispredicted){
          predictorArray(io.entryPC((indexBits + 1),2)) := weakTaken
        }
        .otherwise{
          predictorArray(io.entryPC((indexBits + 1),2)) := strongNotTaken
        }
      }
      is( strongTaken ) {
        when(io.branchMispredicted){
          predictorArray(io.entryPC((indexBits + 1),2)) := weakTaken
        }
        .otherwise{
          predictorArray(io.entryPC((indexBits + 1),2)) := strongTaken
        }
      }
      is( weakTaken ) {
        when(io.branchMispredicted){
          predictorArray(io.entryPC((indexBits + 1),2)) := weakNotTaken
        }
        .otherwise{
          predictorArray(io.entryPC((indexBits + 1),2)) := strongTaken
        }
      }
    }
  } 
  

}


