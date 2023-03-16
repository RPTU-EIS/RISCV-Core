package FW

import config._
import chisel3._
import chisel3.util._

class FW extends Module
{
  val io = IO(
    new Bundle {
      val regAddr            = Input(UInt())
      val controlSignalsEXB  = Input(new ControlSignals)
      val controlSignalsMEMB = Input(new ControlSignals)
      val regData            = Input(UInt())
      val rdEXB              = Input(UInt())
      val ALUresultEXB       = Input(UInt())
      val rdMEMB             = Input(UInt())
      val ALUresultMEMB      = Input(UInt())

      val operandData        = Output(UInt())
      val stall             = Output(Bool())
    }
  )

  val forward             = Wire(Bool())
  val forwardSelect       = Wire(UInt())
  val stallReg           = RegInit(Bool(), false.B)
  val ALUresultFW      = RegInit(UInt(), 0.U)
  val rdFW             = RegInit(UInt(), 0.U)
  val controlSignalsFW = Reg(new ControlSignals)


  ALUresultFW := io.ALUresultMEMB
  rdFW := io.rdMEMB
  controlSignalsFW := io.controlSignalsMEMB


  when((io.regAddr =/= 0.U) & (io.regAddr === io.rdEXB) & io.controlSignalsEXB.regWrite){
    //Stall and forward
    when(io.controlSignalsEXB.memToReg){
      io.stall     := true.B

      forward       := true.B
      forwardSelect := 0.asUInt(2.W)

      //normal forward
    }.otherwise{
      io.stall     := false.B
      forward       := true.B
      forwardSelect := 0.asUInt(2.W)

    }
  }.elsewhen((io.regAddr =/= 0.U) & (io.regAddr === io.rdMEMB) & io.controlSignalsMEMB.regWrite){
    io.stall       := false.B

    forward         := true.B
    forwardSelect   := 1.asUInt(2.W)

  }.elsewhen((io.regAddr =/= 0.U) & (io.regAddr === rdFW) & controlSignalsFW.regWrite){
    io.stall       := false.B

    forward         := true.B
    forwardSelect   := 3.asUInt(2.W)

  }
    .otherwise{
      io.stall     := false.B
      forward       := false.B
      forwardSelect := 0.asUInt(2.W)
    }

  when(forward){
    //send correct forward data to operand
    when(forwardSelect === 0.asUInt(2.W)){
      io.operandData := io.ALUresultEXB
    }.elsewhen(forwardSelect === 3.asUInt(2.W)){
      io.operandData := ALUresultFW
    }.otherwise{
      io.operandData := io.ALUresultMEMB
    }

  }.otherwise{
    //send regdata to operand
    io.operandData := io.regData
  }
}


