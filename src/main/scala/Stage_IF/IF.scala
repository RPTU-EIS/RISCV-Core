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
import config.{ControlSignals, IMEMsetupSignals, Inst, Instruction}
import config.Inst._

class IF(BinaryFile: String) extends Module
{

  val testHarness = IO(
    new Bundle {
      val InstructionMemorySetup = Input(new IMEMsetupSignals)
    }
  )


  val io = IO(new Bundle {
    val branchAddr         = Input(UInt())
    val IFBarrierPC        = Input(UInt())
    val stall              = Input(Bool())
    // Inputs for BTB, will come from EX stage and Hazard Unit
    val updatePrediction   = Input(Bool())
    val newBranch          = Input(Bool())
    val entryPC            = Input(UInt(32.W))
    val branchTaken        = Input(Bool())  // 1 means Taken -- 0 means Not Taken
    val branchMispredicted = Input(Bool())
    val PCplus4ExStage     = Input(UInt(32.W))
    val btbHit             = Output(Bool())
    val btbPrediction      = Output(Bool())
    val btbTargetPredict   = Output(UInt(32.W))
    val PC                 = Output(UInt())
    val instruction        = Output(new Instruction)
    //!val fetchBusy          = Output(Bool()) // added this signal for stall

    //!from Memory
    val instructionICache        = Input(new Instruction)
    val memoryPCin                = Input(UInt(32.W))
    val ICACHEvalid        = Input(Bool())

    //! Added for Loop_Test_0
    val branchToDo      = Output(Bool())
    val instr_addr   = Output(UInt(32.W))
  })
  val oldPC = RegInit(0.U(32.W))
  val next = RegInit(false.B)
  val branchAddress = RegInit(0.U(32.W))
  val branchToDo = RegInit(false.B)

  when(io.branchTaken){
    branchToDo := true.B
    branchAddress := io.branchAddr
  }

  val bufferPC                = RegInit(UInt(32.W), 0.U)
  val firstFetch            = RegInit(true.B)
  

  val BTB               = Module(new BTB_direct)
  val nextPC            = WireInit(UInt(), 0.U)
  val PC                = RegInit(UInt(32.W), 0.U)
  val PCplus4           = Wire(UInt(32.W))
  val instruction       = Wire(new Instruction)
  val branch            = WireInit(Bool(), false.B)


  instruction := io.instructionICache.asTypeOf(new Instruction)


  // Adder to increment PC
  PCplus4 := PC + 4.U

  // BTB signals
  BTB.io.currentPC := PC
  BTB.io.newBranch := io.newBranch
  BTB.io.updatePrediction := io.updatePrediction
  BTB.io.entryPC := io.entryPC
  BTB.io.entryBrTarget := io.branchAddr
  BTB.io.branchMispredicted := io.branchMispredicted
  BTB.io.stall := io.stall
  io.btbPrediction := BTB.io.prediction
  io.btbHit := BTB.io.btbHit
  io.btbTargetPredict := BTB.io.targetAdr

  when(io.branchMispredicted){  // Case of branch mispredicted, we realize that in EX stage
    when(io.branchTaken){  // Branch Behavior is Taken, but Predicted Not-Taken
      nextPC := io.branchAddr
      // printf(p"IF mispredict branch taken nextPC:${io.branchAddr}\n")
    }
      .otherwise{
        nextPC := io.PCplus4ExStage
      }
  }
    .elsewhen(BTB.io.btbHit){  // BTB hits -> Choose nextPC as per the prediction
      when(BTB.io.prediction){  // Predict taken
        nextPC := BTB.io.targetAdr
        // printf(p"IF predict branch taken nextPC:${BTB.io.targetAdr}\n")
      }
        .otherwise{ // Predict not taken
          nextPC := PCplus4
          // printf(p"IF predict branch not taken nextPC:${PCplus4}\n")
        }
    }
    .otherwise{ // Normal instruction OR assume not taken (BTB miss)
      nextPC := PCplus4
      // printf(p"IF normal instr nextPC:${PCplus4}\n")
    }
  // Stall PC
  when(io.stall){ // TODO here maybe stall all input signals
    // printf(p"IF stalled\n")
    when(io.branchMispredicted) {
      PC := nextPC
    }.otherwise{
      PC := PC
    }

    //Fetch prev instruction -- Stalling the part of IF Barrier that holds the instruction
    io.instr_addr := io.IFBarrierPC

  }.elsewhen(firstFetch){
    io.instr_addr := PCplus4
    PC := PCplus4
    bufferPC := PCplus4
    firstFetch := false.B

  }.otherwise{
    //Fetch instruction
    io.instr_addr := PC
    // PC register gets nextPC
    //PC := nextPC
    //!bufferPC := nextPC
    PC := nextPC//!bufferPC
    // printf(p"IF nextPC after fetch\n")
  }
  

  //! Added for Loop_Test_0
  // printf(p"IF io.memoryPCin: ${io.memoryPCin}, branchAddress: ${branchAddress}, io.ICACHEvalid: ${io.ICACHEvalid}, branchToDo: ${branchToDo}\n")
  when(io.memoryPCin === branchAddress && io.ICACHEvalid && branchToDo){
    branchToDo := false.B
    // printf(p"IF branchToDo to false\n")
  }
  io.branchToDo := branchToDo
  // when(io.ICACHEvalid){
  //   next := true.B
  //   // printf(p"IF next\n")
  // }.otherwise{
  //   next := false.B
  // }
  // when(next){
  //   oldPC := PC
  //   // printf(p"IF next PC ${PC}\n")
  // }

  // Send PC to the rest of the pipeline
  // printf(p"IF io.PC:${io.PC}, nextPC:${nextPC}\n")
  io.PC := PC
  // io.PC := oldPC

  io.instruction := instruction

  when(testHarness.InstructionMemorySetup.setup) {
    PC := 0.U
    instruction := Inst.NOP
  }

    // printf(p"\n")
  // printf(p"\n")
  // printf(p"\n")
  // printf(p"\n")
  // printf(p"\n")
}
