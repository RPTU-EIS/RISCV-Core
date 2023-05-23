package HazardUnit
import config._
import chisel3._
import chisel3.util._

class HazardUnit extends Module
{
  val io = IO(
    new Bundle {
        val controlSignals      = Input(new ControlSignals)
        val controlSignalsEXB   = Input(new ControlSignals)
        val controlSignalsMEMB  = Input(new ControlSignals)
        val rs1AddrIFB          = Input(UInt())
        val rs1AddrIDB          = Input(UInt())
        val rs2AddrIFB          = Input(UInt())
        val rs2AddrIDB          = Input(UInt())
        val rdAddrIDB           = Input(UInt())
        val rdAddrEXB           = Input(UInt())
        val rdAddrMEMB          = Input(UInt())
        val branchTaken         = Input(Bool())
        val btbPrediction       = Input(Bool())
        val branchMispredicted  = Output(Bool())
        val stall               = Output(Bool())
        val flushE              = Output(Bool())
        val flushD              = Output(Bool())
        val rs1Select           = Output(UInt(2.W))     //Used to select input to ALU in case of forwarding -- output from FwdUint
        val rs2Select           = Output(UInt(2.W))
    }
  )

  val stall       = Wire(Bool())

// Forwarding Unit
  // Handling source register 1
  when((io.rs1AddrIDB =/= 0.U) && (io.rs1AddrIDB === io.rdAddrEXB) && io.controlSignalsEXB.regWrite){
    // Normal forward 
    io.rs1Select  := 1.asUInt(2.W)  // Forward from EX/MEM pipeline register (EX Barrier)
  }
  .elsewhen((io.rs1AddrIDB =/= 0.U) && (io.rs1AddrIDB === io.rdAddrMEMB) && io.controlSignalsMEMB.regWrite){
    io.rs1Select  := 2.asUInt(2.W)  // Forward from MEM/WB pipeline register (MEM Barrier)
  }
  .otherwise{
    io.rs1Select  := 0.asUInt(2.W)
  }

  // Handling source register 2
  when((io.rs2AddrIDB =/= 0.U) && (io.rs2AddrIDB === io.rdAddrEXB) && io.controlSignalsEXB.regWrite){
    // Normal forward 
    io.rs2Select  := 1.asUInt(2.W)  // Forward from EX/MEM pipeline register (EX Barrier)
  }
  .elsewhen((io.rs2AddrIDB =/= 0.U) && (io.rs2AddrIDB === io.rdAddrMEMB) && io.controlSignalsMEMB.regWrite){
    io.rs2Select  := 2.asUInt(2.W)  // Forward from MEM/WB pipeline register (MEM Barrier)
  }
  .otherwise{
    io.rs2Select  := 0.asUInt(2.W)
  }

// Stalling for Load
  when(  (io.rs1AddrIFB =/= 0.U || io.rs2AddrIFB =/= 0.U) 
         && (io.rs1AddrIFB === io.rdAddrIDB || io.rs2AddrIFB === io.rdAddrIDB) 
         && io.controlSignalsEXB.regWrite 
         && io.controlSignalsEXB.memToReg) {  
    stall := true.B
  }.otherwise{
    stall := false.B
  }

// Outputs: Data Hazard -> stall ID & IF stages, and Flush EX stage (Load) ___ Control Hazard -> flush ID & EX stages (Branch Taken)
  io.stall    := stall
  when(io.branchTaken =/= io.btbPrediction){
    io.branchMispredicted := 1.B
  }
  .otherwise{
    io.branchMispredicted := 0.B
  }
  io.flushD   := io.branchMispredicted
  io.flushE   := io.stall | io.branchMispredicted
}
