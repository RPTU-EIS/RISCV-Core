package top_MC
import chisel3._
import chisel3.util._
import Piplined_RISC_V._
import Stage_ID.ID
import Stage_IF.IF
import Stage_EX.EX
import Stage_MEM.MEM
import config.{MemUpdates, RegisterUpdates, SetupSignals, TestReadouts}
class top_MC extends Module {

  val testHarness = IO(
    new Bundle {
      val setupSignals = Input(new SetupSignals)
      val testReadouts = Output(new TestReadouts)
      val regUpdates   = Output(new RegisterUpdates)
      val memUpdates   = Output(new MemUpdates)
      val currentPC    = Output(UInt(32.W))
    }
  )


  val IFBarrier  = Module(new IFpipe).io
  val IDBarrier  = Module(new IDpipe).io
  val EXBarrier  = Module(new EXpipe).io
  val MEMBarrier = Module(new MEMpipe).io


  val IF  = Module(new IF)
  val ID  = Module(new ID)
  val EX  = Module(new EX)
  val MEM = Module(new MEM)

  val writeBackData = Wire(UInt())


  IF.testHarness.InstructionMemorySetup := testHarness.setupSignals.IMEMsignals
  ID.testHarness.registerSetup          := testHarness.setupSignals.registerSignals
  MEM.testHarness.DMEMsetup             := testHarness.setupSignals.DMEMsignals

  testHarness.testReadouts.registerRead := ID.testHarness.registerPeek
  testHarness.testReadouts.DMEMread     := MEM.testHarness.DMEMpeek


  testHarness.regUpdates                := ID.testHarness.testUpdates
  testHarness.memUpdates                := MEM.testHarness.testUpdates
  testHarness.currentPC                 := IF.testHarness.PC


  ///////////////////////
  // Branch addr to IF //
  ///////////////////////
  IF.io.branchAddr            := EXBarrier.outBranchAddr
  IF.io.controlSignals        := EXBarrier.outControlSignals
  IF.io.branch                := EXBarrier.outBranch
  IF.io.IFBarrierPC           := IFBarrier.outCurrentPC
  //stall
  IF.io.freeze                := EX.io.freeze

  //Signals to IFBarrier
  IFBarrier.inCurrentPC       := IF.io.PC
  IFBarrier.inInstruction     := IF.io.instruction
  IFBarrier.freeze            := EX.io.freeze

  //Decode stage
  ID.io.instruction           := IFBarrier.outInstruction
  ID.io.registerWriteAddress  := MEMBarrier.outRd
  ID.io.registerWriteEnable   := MEMBarrier.outControlSignals.regWrite

  //Signals to IDBarrier
  IDBarrier.inInstruction    := ID.io.instruction
  IDBarrier.inControlSignals := ID.io.controlSignals
  IDBarrier.inBranchType     := ID.io.branchType
  IDBarrier.inPC             := IFBarrier.outCurrentPC
  IDBarrier.inInsertBubble   := EX.io.insertBubble
  IDBarrier.inOp1Select      := ID.io.op1Select
  IDBarrier.inOp2Select      := ID.io.op2Select
  IDBarrier.inImmData        := ID.io.immData
  IDBarrier.inRd             := IFBarrier.outInstruction.registerRd
  IDBarrier.inALUop          := ID.io.ALUop
  IDBarrier.inReadData1      := ID.io.readData1
  IDBarrier.inReadData2      := ID.io.readData2
  //Stalling
  IDBarrier.freeze           := EX.io.freeze

  //Execute stage
  EX.io.instruction           := IDBarrier.outInstruction
  EX.io.controlSignals        := IDBarrier.outControlSignals
  EX.io.controlSignalsEXB     := EXBarrier.outControlSignals
  EX.io.controlSignalsMEMB    := MEMBarrier.outControlSignals
  EX.io.PC                    := IDBarrier.outPC
  EX.io.branchType            := IDBarrier.outBranchType
  EX.io.op1Select             := IDBarrier.outOp1Select
  EX.io.op2Select             := IDBarrier.outOp2Select
  EX.io.rs1                   := IDBarrier.outReadData1
  EX.io.Rs2                   := IDBarrier.outReadData2
  EX.io.immData               := IDBarrier.outImmData
  EX.io.ALUop                 := IDBarrier.outALUop
  EX.io.rdEXB                 := EXBarrier.outRd
  EX.io.ALUresultEXB          := EXBarrier.outALUResult
  EX.io.rdMEMB                := MEMBarrier.outRd
  EX.io.ALUresultMEMB         := writeBackData


  //Signals to EXBarrier
  EXBarrier.inALUResult       := EX.io.ALUResult
  EXBarrier.inBranchAddr      := EX.io.branchAddr

  EXBarrier.inControlSignals  := IDBarrier.outControlSignals
  EXBarrier.inBranch          := EX.io.branch
  EXBarrier.inRd              := IDBarrier.outRd
  EXBarrier.inRs2             := EX.io.Rs2Forwarded
  EXBarrier.inInsertBubble    := EX.io.insertBubble
  //Stalling
  EXBarrier.freeze            := EX.io.freeze

  //MEM stage
  MEM.io.dataIn               := EXBarrier.outRs2
  MEM.io.dataAddress          := EXBarrier.outALUResult
  MEM.io.writeEnable          := EXBarrier.outControlSignals.memWrite

  //MEMBarrier
  MEMBarrier.inControlSignals := EXBarrier.outControlSignals
  MEMBarrier.inALUResult      := EXBarrier.outALUResult
  MEMBarrier.inRd             := EXBarrier.outRd
  MEMBarrier.inRs2            := EXBarrier.outRs2
  MEMBarrier.inMEMData        := MEM.io.dataOut

  ///////////////
  // MEM stage //
  ///////////////
  //Mux for which data to write to register
  when(MEMBarrier.outControlSignals.memToReg){
    writeBackData := MEMBarrier.outMEMData
  }.otherwise{
    writeBackData := MEMBarrier.outALUResult
  }

  ID.io.registerWriteData := writeBackData
}
