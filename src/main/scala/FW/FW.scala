package FW

import config.ControlSignals._
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
      val freeze             = Output(Bool())
    }
  )

  val forward             = Wire(Bool())
  val forwardSelect       = Wire(UInt())
  val freezeReg           = RegInit(Bool(), false.B)
  val ALUresultBonus      = RegInit(UInt(), 0.U)
  val rdBonus             = RegInit(UInt(), 0.U)
  val controlSignalsBONUS = Reg(new ControlSignals)




  ALUresultBonus := io.ALUresultMEMB
  rdBonus := io.rdMEMB
  controlSignalsBONUS := io.controlSignalsMEMB


  when((io.regAddr =/= 0.U) & (io.regAddr === io.rdEXB) & io.controlSignalsEXB.regWrite){
    //Freeze and forward
    when(io.controlSignalsEXB.memToReg){
      io.freeze     := true.B

      forward       := true.B
      forwardSelect := 0.asUInt(2.W)

      //normal forward
    }.otherwise{
      io.freeze     := false.B
      forward       := true.B
      forwardSelect := 0.asUInt(2.W)

    }
  }.elsewhen((io.regAddr =/= 0.U) & (io.regAddr === io.rdMEMB) & io.controlSignalsMEMB.regWrite){
    io.freeze       := false.B

    forward         := true.B
    forwardSelect   := 1.asUInt(2.W)

  }.elsewhen((io.regAddr =/= 0.U) & (io.regAddr === rdBonus) & controlSignalsBONUS.regWrite){
    io.freeze       := false.B

    forward         := true.B
    forwardSelect   := 3.asUInt(2.W)

  }
    .otherwise{
      io.freeze     := false.B
      forward       := false.B
      forwardSelect := 0.asUInt(2.W)
    }





  when(forward){
    //if forwarding, send correct forward data to operand
    when(forwardSelect === 0.asUInt(2.W)){
      io.operandData := io.ALUresultEXB
    }.elsewhen(forwardSelect === 3.asUInt(2.W)){
      io.operandData := ALUresultBonus
    }.otherwise{
      io.operandData := io.ALUresultMEMB
    }

  }.otherwise{
    //if not forwarding, send regdata to operand
    io.operandData := io.regData
  }
}




/////////////////
//val io = IO(new Bundle {
//  val ID_EXRs1          = Input(UInt(5.W))
//  val ID_EXRs2  	      = Input(UInt(5.W))
//  val EX_MEMRegWrite    = Input(UInt(1.W))
//  val EX_MEMRegRd       = Input(UInt(5.W))
//  val MEM_WBRegWrite    = Input(UInt(1.W))
//  val MEM_WBRegRd       = Input(UInt(5.W))
//  val Fwd_A             = Output(UInt(2.W))
//  val Fwd_B             = Output(UInt(2.W))
//})
//
//  when ((io.EX_MEMRegWrite === 1.U(1.W)) &&(io.EX_MEMRegRd =/= 0.U)&&(io.EX_MEMRegRd === io.ID_EXRs1)) {
//  io.Fwd_A := "b10".U(2.W);
//} .elsewhen ((io.MEM_WBRegWrite === 1.U(1.W)) &&(io.MEM_WBRegRd =/= 0.U)&&(io.MEM_WBRegRd === io.ID_EXRs1)) {
//  io.Fwd_A := "b01".U(2.W);
//} .otherwise {
//  io.Fwd_A := "b00".U(2.W);
//}
//
//  when ((io.EX_MEMRegWrite === 1.U(1.W)) &&(io.EX_MEMRegRd =/= 0.U)&&(io.EX_MEMRegRd === io.ID_EXRs2)) {
//  io.Fwd_B := "b10".U(2.W);
//} .elsewhen ((io.MEM_WBRegWrite === 1.U(1.W)) &&(io.MEM_WBRegRd =/= 0.U)&&(io.MEM_WBRegRd === io.ID_EXRs2)) {
//  io.Fwd_B := "b01".U(2.W);
//} .otherwise {
//  io.Fwd_B := "b00".U(2.W);
//}