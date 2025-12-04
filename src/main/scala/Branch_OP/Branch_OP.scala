/*
RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.
The core is part of an educational project by the Chair of Electronic Design Automation (https://eit.rptu.de/fgs/eis/) at RPTU Kaiserslautern, Germany.

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel
Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

*/

package Branch_OP
import config.branch_types._
import chisel3._
import chisel3.util._

class Branch_OP extends Module {
  val io = IO(
    new Bundle {
      val branchType  = Input(UInt(32.W))
      val src1        = Input(UInt(32.W))
      val src2        = Input(UInt(32.W))
      val branchTaken = Output(UInt(32.W))
    }
  )

  //Branch lookup
  io.branchTaken := 0.U
  val lhs = io.src1.asSInt
  val rhs = io.src2.asSInt
  switch(io.branchType) {
    is(beq) {
      io.branchTaken := (lhs === rhs)
      when(lhs === rhs){
          printf(p"BEQ TAKEN lhs=rhs, lhs: ${lhs}, rhs: ${rhs}\n")
      }.otherwise{
          printf(p"BEQ NOT TAKEN lhs!=rhs, lhs: ${lhs}, rhs: ${rhs}\n")
      }
    }
    is(neq) {
      io.branchTaken := (lhs =/= rhs)
      when(lhs =/= rhs){
          printf(p"NEQ/BNE TAKEN lhs!=rhs, lhs: ${lhs}, rhs: ${rhs}\n")
      }.otherwise{
          printf(p"NEQ/BNE NOT TAKEN lhs=rhs, lhs: ${lhs}, rhs: ${rhs}\n")
      }
    }
    is(gte) {
      io.branchTaken := (lhs >= rhs)
      when(lhs >= rhs){
          printf(p"GTE TAKEN lhs>=rhs, lhs: ${lhs}, rhs: ${rhs}\n")
      }.otherwise{
          printf(p"GTE NOT TAKEN lhs<rhs, lhs: ${lhs}, rhs: ${rhs}\n")
      }
    }
    is(lt) {
      io.branchTaken := (lhs < rhs)
      when(lhs < rhs){
          printf(p"LT TAKEN lhs<rhs, lhs: ${lhs}, rhs: ${rhs}\n")
      }.otherwise{
          printf(p"LT NOT TAKEN lhs>=rhs, lhs: ${lhs}, rhs: ${rhs}\n")
      }
    }
    is(gteu) {
      io.branchTaken := (lhs.asUInt >= rhs.asUInt)
      when(lhs.asUInt >= rhs.asUInt){
          printf(p"GTEU TAKEN lhs>=rhs, lhs: ${lhs.asUInt}, rhs: ${rhs.asUInt}\n")
      }.otherwise{
          printf(p"GTEU NOT TAKEN lhs<hs, lhs: ${lhs.asUInt}, rhs: ${rhs.asUInt}\n")
      }
    }
    is(ltu) {
      io.branchTaken := (lhs.asUInt < rhs.asUInt)
      when(lhs.asUInt < rhs.asUInt){
          printf(p"LTU TAKEN lhs<rhs, lhs: ${lhs.asUInt}, rhs: ${rhs.asUInt}\n")
      }.otherwise{
          printf(p"LTU NOT TAKEN lhs>=rhs, lhs: ${lhs.asUInt}, rhs: ${rhs.asUInt}\n")
      }
    }
    is(jump) {
      io.branchTaken := (1.U)
        printf(p"JUMP\n")
    }
    is(DC) {
      io.branchTaken := (0.U)
        printf(p"DC\n")
    }
  }


}
