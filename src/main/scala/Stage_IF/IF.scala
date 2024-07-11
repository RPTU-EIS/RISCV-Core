/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

*/

package Stage_IF

import ICache.ICacheAndIMemory
import chisel3._
import chisel3.util._
import config.{ControlSignals, IMEMsetupSignals, Inst, Instruction}
import config.Inst._
import InstructionMemory.InstructionMemory

class IF(BinaryFile: String) extends Module
{

  val testHarness = IO(
    new Bundle {
      val InstructionMemorySetup = Input(new IMEMsetupSignals)
      val PC        = Output(UInt())
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
    val fetchBusy          = Output(Bool()) // added this signal for stall
  })

  // TODO change name for "InstructionMemory"
  val InstructionMemory = Module(new ICacheAndIMemory(BinaryFile)) // it should be changed with ICacheAndMemory class
  val BTB               = Module(new BTB_direct)
  val nextPC            = WireInit(UInt(), 0.U)
  val PC                = RegInit(UInt(32.W), 0.U)
  val PCplus4           = Wire(UInt(32.W))
  val instruction       = Wire(new Instruction)
  val branch            = WireInit(Bool(), false.B)

  // i commented those two lines and I question even if they are necessary TODO
  InstructionMemory.testHarness.setupSignals := testHarness.InstructionMemorySetup
  testHarness.PC := InstructionMemory.testHarness.requestedAddress

  instruction := InstructionMemory.io.instr_out.asTypeOf(new Instruction)
  io.fetchBusy := InstructionMemory.io.busy //InstructionMemory.io.busy
//  instruction := InstructionMemory.io.instruction.asTypeOf(new Instruction)

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
    }
      .otherwise{
        nextPC := io.PCplus4ExStage
      }
  }
    .elsewhen(BTB.io.btbHit){  // BTB hits -> Choose nextPC as per the prediction
      when(BTB.io.prediction){  // Predict taken
        nextPC := BTB.io.targetAdr
      }
        .otherwise{ // Predict not taken
          nextPC := PCplus4
        }
    }
    .otherwise{ // Normal instruction OR assume not taken (BTB miss)
      nextPC := PCplus4
    }
  // Stall PC
  when(io.stall){ // TODO here maybe stall all input signals
    when(io.branchMispredicted) {
      PC := nextPC
    }.otherwise{
      PC := PC
    }

    //Fetch prev instruction -- Stalling the part of IF Barrier that holds the instruction
    //InstructionMemory.io.instructionAddress := io.IFBarrierPC
    InstructionMemory.io.instr_addr := io.IFBarrierPC

  }.otherwise{
    //Fetch instruction
//    InstructionMemory.io.instructionAddress := PC
    InstructionMemory.io.instr_addr := PC
    // PC register gets nextPC
    PC := nextPC
  }
  //Mux for controlling which address to go to next
//  when(io.branchMispredicted){  // Case of branch mispredicted, we realize that in EX stage
//    when(io.branchTaken){  // Branch Behavior is Taken, but Predicted Not-Taken
//      nextPC := io.branchAddr
//    }
//    .otherwise{
//      nextPC := io.PCplus4ExStage
//    }
//  }
//  .elsewhen(BTB.io.btbHit){  // BTB hits -> Choose nextPC as per the prediction
//    when(BTB.io.prediction){  // Predict taken
//      nextPC := BTB.io.targetAdr
//    }
//    .otherwise{ // Predict not taken
//      nextPC := PCplus4
//    }
//  }
//  .otherwise{ // Normal instruction OR assume not taken (BTB miss)
//    nextPC := PCplus4
//  }
  
  // Send PC to the rest of the pipeline
  io.PC := PC

  io.instruction := instruction

  when(testHarness.InstructionMemorySetup.setup) {
    PC := 0.U
    instruction := Inst.NOP
  }
}
