package Stage_IF
import chisel3._
import chisel3.util._
import config.{ControlSignals, IMEMsetupSignals, Inst, Instruction}
import config.Inst._
import InstructionMemory.InstructionMemory

class IF extends Module
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
    val freeze         = Input(Bool())


    val PC             = Output(UInt())
    val instruction    = Output(new Instruction)
  }
  )

  val InstructionMemory        = Module(new InstructionMemory)
  val nextPC      = WireInit(UInt(), 0.U)
  val PC          = RegInit(UInt(32.W), 0.U)


  val instruction = Wire(new Instruction)
  val branch      = WireInit(Bool(), false.B)


  InstructionMemory.testHarness.setupSignals := testHarness.InstructionMemorySetup
  testHarness.PC := InstructionMemory.testHarness.requestedAddress
  //testHarness.PC := InstructionMemory.io.instructionAddress




  instruction := InstructionMemory.io.instruction.asTypeOf(new Instruction)


  //Freeze PC if stall
  when(io.freeze){
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

  // when(io.insertBubble){
  //   io.instruction := Instruction.NOP
  // }.otherwise{
  io.instruction := instruction
  // }




  when(testHarness.InstructionMemorySetup.setup) {
    PC := 0.U
    instruction := Inst.NOP
  }
}


///////////////////
//package Stage_IF
//
//import chisel3._
//import chisel3.util._
//
//import PC._
//import InstructionMemory._
//
//class IF extends Module
//{
//  val io = IO(new Bundle {
//    val FD_pip_reg_WE = Input(UInt(1.W))
//    val br_target     = Input(UInt(32.W))  // forwarded jump target       (forwarded from exec stage)
//    val jm_target     = Input(UInt(32.W))  // forwarded br target         (forwarded from exec stage)
//    val j_br_mux_sel  = Input(UInt(1.W))   // forwarded j_or_br mux sel   (forwarded from exec stage)
//    val is_br_j       = Input(UInt(1.W))   // forwarded is_br_j           (forwarded from exec stage)
//    val alu_res0      = Input(UInt(1.W))   // forwarded comparison result (forwarded from exec stage)
//    val if_id_reg     = Output(UInt(64.W)) // first pipeline reg = (instr, PC+4)
//  })
//
//  val pc = Module(new PC())
//  val IM = Module(new InstructionMemory())
//
//  val pc_WE      = Wire(Bool())
//  val pc_next    = Wire(UInt(32.W))
//  val pc_current = Wire(UInt(32.W))
//
//  val instr = Wire(UInt(32.W))
//
//  val pc_plus_4 = Wire(UInt(32.W)) // PC + 4
//  val br_j_mux_out   = Wire(UInt(32.W)) // branch or jump multiplexer output
//  val pc_mux_sel     = Wire(UInt(1.W))  // distinguishes between jump/branch and pc+4
//
//  pc_WE := io.FD_pip_reg_WE  // TODO stall needs implementing
//
//  pc_current    := pc.io.PC_current
//  pc.io.WE      := pc_WE
//  pc.io.PC_next := pc_next
//
//  pc_plus_4 := pc_current + 4.U(32.W) // PC + 4
//
//  br_j_mux_out := Mux(io.j_br_mux_sel === 1.U(1.W), io.br_target, io.jm_target)
//
//  pc_mux_sel := ((io.j_br_mux_sel & io.alu_res0) | (!io.j_br_mux_sel)) & io.is_br_j
//
//  pc_next := Mux(pc_mux_sel === 1.U(1.W), br_j_mux_out, pc_plus_4)
//
//  IM.io.addr := pc_current
//  instr := IM.io.instr
//
//  val if_id_reg = RegInit(0.U(64.W))
//  when(io.FD_pip_reg_WE === 1.U(1.W)){
//    if_id_reg := Cat(instr, pc_plus_4)
//  }
//
//  io.if_id_reg := if_id_reg
//}
