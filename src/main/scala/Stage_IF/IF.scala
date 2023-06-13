/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava

*/

package Stage_IF

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
    val branchAddr     = Input(UInt())
    val controlSignals = Input(new ControlSignals)
    val branch         = Input(Bool())
    val IFBarrierPC    = Input(UInt())
    val stall         = Input(Bool())


    val PC             = Output(UInt())
    val instruction    = Output(new Instruction)
  }
  )

  val InstructionMemory        = Module(new InstructionMemory(BinaryFile))
  val nextPC      = WireInit(UInt(), 0.U)
  val PC          = RegInit(UInt(32.W), 0.U)


  val instruction = Wire(new Instruction)
  val branch      = WireInit(Bool(), false.B)


  InstructionMemory.testHarness.setupSignals := testHarness.InstructionMemorySetup
  testHarness.PC := InstructionMemory.testHarness.requestedAddress

  instruction := InstructionMemory.io.instruction.asTypeOf(new Instruction)
  //stall PC
  when(io.stall){
    PC     := PC
    io.PC  := PC

    //Incremented PC
    nextPC := PC

    //fetch instruction
    InstructionMemory.io.instructionAddress := io.IFBarrierPC

  }.otherwise{

    //Mux for controlling which address to go to next
    //Either the incremented PC or branch address in the case of a jump or branch
    when(io.controlSignals.jump | (io.controlSignals.branch & io.branch === 1.U)){
      //Branch Addr
      PC := nextPC

      //Send the branch address to the rest of the pipeline
      io.PC := io.branchAddr

      //Incremented PC
      nextPC := io.branchAddr + 4.U

      //fetch instruction
      InstructionMemory.io.instructionAddress := io.branchAddr

    }.otherwise{
      //Incremented PC
      PC := nextPC

      //Send the PC to the rest of the pipeline
      io.PC := PC

      //Incremented PC
      nextPC := PC + 4.U

      //fetch instruction
      InstructionMemory.io.instructionAddress := PC

    }
  }

  io.instruction := instruction

  when(testHarness.InstructionMemorySetup.setup) {
    PC := 0.U
    instruction := Inst.NOP
  }
}

