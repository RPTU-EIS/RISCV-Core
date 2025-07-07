/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava

*/

package top_MC

import Prefetcher.Prefetcher

import Cache.DICachesAndMemory
import config.{ControlSignals, IMEMsetupSignals, Inst, Instruction}
import config.Inst._


import chisel3._
import chisel3.util._
import Piplined_RISC_V._
import Stage_ID.ID
import Stage_IF.IF
import Stage_EX.EX
// import Stage_MEM.MEM
import HazardUnit.HazardUnit
import config.{MemUpdates, RegisterUpdates, SetupSignals, TestReadouts}

class top_MC(BinaryFile: String, DataFile: String) extends Module {

  val testHarness = IO(
    new Bundle {
      val setupSignals = Input(new SetupSignals)
      val testReadouts = Output(new TestReadouts)
      val regUpdates   = Output(new RegisterUpdates)
      val memUpdates   = Output(new MemUpdates)
      val currentPC    = Output(UInt(32.W))
    }
  )

  // Pipeline Registers
  val IFBarrier  = Module(new IFpipe).io
  val IDBarrier  = Module(new IDpipe).io
  val EXBarrier  = Module(new EXpipe).io
  val MEMBarrier = Module(new MEMpipe).io

 // Pipeline Stages
  val IF  = Module(new IF(BinaryFile))
  val ID  = Module(new ID)
  val EX  = Module(new EX)
  // val MEM = Module(new MEM(DataFile)) //TODO change to same file as IF -> merged mem
  val writeBackData = Wire(UInt())

  //! Instantiate memory here to avoid double instantiation in IF and MEM stages respectively
  val Memory  = Module(new DICachesAndMemory(BinaryFile))

  // Hazard Unit
  val HzdUnit = Module(new HazardUnit)


  IF.testHarness.InstructionMemorySetup := testHarness.setupSignals.IMEMsignals //! still used in IF
  ID.testHarness.registerSetup          := testHarness.setupSignals.registerSignals
  //!MEM.testHarness.DMEMsetup             := testHarness.setupSignals.DMEMsignals

  testHarness.testReadouts.registerRead := ID.testHarness.registerPeek
  //!testHarness.testReadouts.DMEMread     := MEM.testHarness.DMEMpeek
  testHarness.testReadouts.DMEMread  := Memory.io.data_out

  testHarness.regUpdates                := ID.testHarness.testUpdates
  testHarness.memUpdates                := 0.U.asTypeOf(new MemUpdates) //MEM.testHarness.testUpdates
  //! not used in IF    testHarness.currentPC                 := IF.testHarness.PC 


  // Fetch Stage
  IF.io.branchTaken        := EX.io.branchTaken
  IF.io.IFBarrierPC        := IFBarrier.outCurrentPC
  IF.io.stall              := HzdUnit.io.stall | HzdUnit.io.stall_membusy     // Stall Fetch -> PC_en=0
  IF.io.newBranch          := EX.io.newBranch
  IF.io.updatePrediction   := EX.io.updatePrediction
  IF.io.entryPC            := IDBarrier.outPC
  IF.io.branchAddr         := EX.io.branchTarget
  IF.io.branchMispredicted := HzdUnit.io.branchMispredicted
  IF.io.PCplus4ExStage     := EX.io.outPCplus4

  //Signals to IFBarrier
  IFBarrier.inCurrentPC        := IF.io.PC
  IFBarrier.inInstruction      := IF.io.instruction
  IFBarrier.stall              := HzdUnit.io.stall | HzdUnit.io.stall_membusy     // Stall Decode -> IFBarrier_en=0
  IFBarrier.flush              := HzdUnit.io.flushD
  IFBarrier.inBTBHit           := IF.io.btbHit
  IFBarrier.inBTBPrediction    := IF.io.btbPrediction
  IFBarrier.inBTBTargetPredict := IF.io.btbTargetPredict

  //Decode stage
  ID.io.instruction           := IFBarrier.outInstruction
  ID.io.registerWriteAddress  := MEMBarrier.outRd
  ID.io.registerWriteEnable   := MEMBarrier.outControlSignals.regWrite

  //Signals to IDBarrier
  IDBarrier.inInstruction      := ID.io.instruction
  IDBarrier.inControlSignals   := ID.io.controlSignals
  IDBarrier.inBranchType       := ID.io.branchType
  IDBarrier.inPC               := IFBarrier.outCurrentPC
  IDBarrier.flush              := HzdUnit.io.flushE
  IDBarrier.stall              := HzdUnit.io.stall_membusy || HzdUnit.io.stall //!
  IDBarrier.inOp1Select        := ID.io.op1Select
  IDBarrier.inOp2Select        := ID.io.op2Select
  IDBarrier.inImmData          := ID.io.immData
  IDBarrier.inRd               := IFBarrier.outInstruction.registerRd
  IDBarrier.inALUop            := ID.io.ALUop
  IDBarrier.inReadData1        := ID.io.readData1
  IDBarrier.inReadData2        := ID.io.readData2
  IDBarrier.inBTBHit           := IFBarrier.outBTBHit
  IDBarrier.inBTBPrediction    := IFBarrier.outBTBPrediction
  IDBarrier.inBTBTargetPredict := IFBarrier.outBTBTargetPredict

  //Execute stage
  EX.io.instruction           := IDBarrier.outInstruction
  EX.io.controlSignals        := IDBarrier.outControlSignals
  EX.io.PC                    := IDBarrier.outPC
  EX.io.branchType            := IDBarrier.outBranchType
  EX.io.op1Select             := IDBarrier.outOp1Select
  EX.io.op2Select             := IDBarrier.outOp2Select
  EX.io.rs1Select             := HzdUnit.io.rs1Select
  EX.io.rs2Select             := HzdUnit.io.rs2Select
  EX.io.rs1                   := IDBarrier.outReadData1
  EX.io.rs2                   := IDBarrier.outReadData2
  EX.io.immData               := IDBarrier.outImmData
  EX.io.ALUop                 := IDBarrier.outALUop
  EX.io.ALUresultEXB          := EXBarrier.outALUResult
  EX.io.ALUresultMEMB         := writeBackData
  EX.io.btbHit                := IDBarrier.outBTBHit
  EX.io.btbTargetPredict      := IDBarrier.outBTBTargetPredict

  // Hazard Unit
  HzdUnit.io.controlSignalsEXB  := EXBarrier.outControlSignals
  HzdUnit.io.controlSignalsMEMB := MEMBarrier.outControlSignals
  HzdUnit.io.rs1AddrIFB         := IFBarrier.outInstruction.registerRs1
  HzdUnit.io.rs2AddrIFB         := IFBarrier.outInstruction.registerRs2
  HzdUnit.io.rs1AddrIDB         := IDBarrier.outInstruction.registerRs1
  HzdUnit.io.rs2AddrIDB         := IDBarrier.outInstruction.registerRs2
  HzdUnit.io.rdAddrIDB          := IDBarrier.outInstruction.registerRd
  HzdUnit.io.rdAddrEXB          := EXBarrier.outRd
  HzdUnit.io.rdAddrMEMB         := MEMBarrier.outRd
  HzdUnit.io.branchTaken        := EX.io.branchTaken
  HzdUnit.io.wrongAddrPred      := EX.io.wrongAddrPred
  HzdUnit.io.btbPrediction      := IDBarrier.outBTBPrediction
  HzdUnit.io.branchType         := IDBarrier.outBranchType
  HzdUnit.io.membusy            := Memory.io.DCACHEbusy || Memory.io.ICACHEbusy//!MEM.io.memBusy || IF.io.fetchBusy // TODO changed memBusy signal

  //Signals to EXBarrier
  EXBarrier.inALUResult       := EX.io.ALUResult
  EXBarrier.inControlSignals  := IDBarrier.outControlSignals
  EXBarrier.inRd              := IDBarrier.outRd
  EXBarrier.inRs2             := EX.io.Rs2Forwarded
  EXBarrier.stall             := HzdUnit.io.stall_membusy

  //MEM stage
  // MEM.io.dataIn               := EXBarrier.outRs2
  // MEM.io.dataAddress          := EXBarrier.outALUResult
  // MEM.io.writeEnable          := EXBarrier.outControlSignals.memWrite
  // MEM.io.readEnable           := EXBarrier.outControlSignals.memRead

  //MEMBarrier
  MEMBarrier.inControlSignals := EXBarrier.outControlSignals
  MEMBarrier.inALUResult      := EXBarrier.outALUResult
  MEMBarrier.inRd             := EXBarrier.outRd
  // MEMBarrier.inMEMData        := MEM.io.dataOut
  MEMBarrier.stall            := HzdUnit.io.stall_membusy

  // MEM stage
  //Mux for which data to write to register
  when(MEMBarrier.outControlSignals.memToReg){
    writeBackData := MEMBarrier.outMEMData
  }.otherwise{
    writeBackData := MEMBarrier.outALUResult
  }

  ID.io.registerWriteData := writeBackData


  //! Added for Loop_Test_0
  IFBarrier.branchAddr        := EX.io.branchTarget
  IFBarrier.branchTaken       := EX.io.branchTaken
  HzdUnit.io.branchToDo       := IF.io.branchToDo





  //IF.io.fetchBusy  := Memory.io.ICACHEbusy //!error cant drive from child module to child module
  // val icacheBusy = Memory.io.ICACHEbusy
  // IF.io.fetchBusy := icacheBusy

  //!Memory signals
  IF.io.instructionICache := Memory.io.instr_out.asTypeOf(new Instruction)
  IF.io.memoryPCin := Memory.io.pcOut
  IF.io.ICACHEvalid := Memory.io.ICACHEvalid


   //DMEM
  Memory.io.write_data  := EXBarrier.outRs2
  Memory.io.address     := EXBarrier.outALUResult
  Memory.io.write_en    := EXBarrier.outControlSignals.memWrite
  Memory.io.read_en     := EXBarrier.outControlSignals.memRead


  //!
  Memory.io.flushed := HzdUnit.io.flushD



  //Read data from DMEM
  MEMBarrier.inMEMData          := Memory.io.data_out



  Memory.io.instr_addr := IF.io.PC//instr_addr

  Memory.testHarness.setupSignals := testHarness.setupSignals.IMEMsignals
  testHarness.currentPC                 := Memory.testHarness.requestedAddress


  IFBarrier.branchMispredicted := HzdUnit.io.branchMispredicted



// when(IF.io.instruction.asUInt =/= 0.U){
  //! Added for debugging of program flow
  printf(p"\n")
  printf(p"\n")
  
    printf(p"------------------------------------------------------------------------\n")
    printf(p"PC: ${IF.io.PC}, instruction: 0x${Hexadecimal(IF.io.instruction.asUInt)}\n")
  
  


  printf(p"------------------------------------IF------------------------------------\n")
  printf(p"PC: ${IF.io.PC}, instruction: 0x${Hexadecimal(IF.io.instruction.asUInt)}, branchTaken: ${IF.io.branchTaken}, branchAddr: ${IF.io.branchAddr}\n")

  printf(p"------------------------------------IFBarrier------------------------------------\n")
  printf(p"outPC: ${IFBarrier.outCurrentPC}, instruction: 0x${Hexadecimal(IFBarrier.outInstruction.asUInt)}, stall: ${IFBarrier.stall}, flush: ${IFBarrier.flush}\n")

  printf(p"------------------------------------ID------------------------------------\n")
  printf(p"instruction: 0x${Hexadecimal(ID.io.instruction.asUInt)}\n")

  printf(p"------------------------------------IDBarrier------------------------------------\n")
  printf(p"outPC: ${IDBarrier.outPC}, instruction: 0x${Hexadecimal(IDBarrier.outInstruction.asUInt)}, stall: ${IDBarrier.stall}, flush: ${IDBarrier.flush}\n")

  printf(p"------------------------------------EX------------------------------------\n")
  printf(p"PC: ${EX.io.PC}, instruction: 0x${Hexadecimal(EX.io.instruction.asUInt)}, ALUResult: ${EX.io.ALUResult}\n")

  printf(p"------------------------------------EXBarrier------------------------------------\n")
  printf(p"stall: ${EXBarrier.stall}, outALUResult: ${EXBarrier.outALUResult}\n")

  printf(p"------------------------------------HzdUnit------------------------------------\n")
  printf(p"stall: ${HzdUnit.io.stall}, stall_membusy: ${HzdUnit.io.stall_membusy}, flushD: ${HzdUnit.io.flushD}, flushE: ${HzdUnit.io.flushE}, branchTaken: ${HzdUnit.io.branchTaken}, branchToDo: ${HzdUnit.io.branchToDo},\n")

  printf(p"------------------------------------BTB------------------------------------\n")
  printf(p"IF.io.btbHit: ${IF.io.btbHit}, IDBarrier.inBTBHit: ${IDBarrier.inBTBHit}, EX.io.btbHit: ${EX.io.btbHit}\n")
  // }

}