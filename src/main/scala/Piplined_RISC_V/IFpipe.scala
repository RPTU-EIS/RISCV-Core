package Piplined_RISC_V
import chisel3._
import chisel3.util._
import config.{Instruction}
class IFpipe extends Module
{
  val io = IO(
    new Bundle {
      val inCurrentPC     = Input(UInt(32.W))
      val inInstruction   = Input(new Instruction)
      val freeze          = Input(Bool())

      val outCurrentPC    = Output(UInt(32.W))
      val outInstruction  = Output(new Instruction)
    }
  )

  val currentPCReg   = RegEnable(io.inCurrentPC, 0.U, !io.freeze)
  val prevPC         = WireInit(UInt(), 0.U)
  //val InstructionReg = Reg(new Instruction)

  //PC
  io.outCurrentPC := currentPCReg

  //Instruction
  io.outInstruction := io.inInstruction

}
