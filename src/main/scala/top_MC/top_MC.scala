//package top_MC
//
//import chisel3._
//
//import DataMemory._
//import InstructionMemory._
//import Control._
//import DataPath._
//
//class top_MC extends Module {
//  val io = IO(new Bundle{
//    val out1 = Output(UInt(7.W))
//  })
//  val contr = Module(new Control())
//  val DM    = Module(new DataMemory())
//  val IM    = Module(new InstructionMemory())
//  val DP    = Module(new DataPath())
//
//  contr.io.opcode := DP.io.opcode
//  contr.io.funct3 := DP.io.funct3
//  contr.io.funct7 := DP.io.funct7
//
//  DP.io.controls := contr.io.controls
//  DP.io.aluOP := contr.io.aluOP
//  DP.io.instr := IM.io.instr
//  DP.io.data_in := DM.io.data_out
//
//  DM.io.lsMux := contr.io.lsMux
//  DM.io.addr := DP.io.dm_addr
//  DM.io.DM_WE := contr.io.DM_WE
//  DM.io.DM_RE := contr.io.DM_RE
//  DM.io.data_in := DP.io.dm_data
//
//  IM.io.addr := DP.io.PC_out
//
//  io.out1 := DP.io.opcode
//}
