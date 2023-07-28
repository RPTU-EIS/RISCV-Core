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
      val branchType  = Input(UInt())
      val src1        = Input(UInt())
      val src2        = Input(UInt())
      val branchTaken = Output(UInt())
    }
  )

  //Branch lookup
  io.branchTaken := 0.U

  switch(io.branchType) {
    is(beq) {
      io.branchTaken := (io.src1 === io.src2)
    }
    is(neq) {
      io.branchTaken := (io.src1 =/= io.src2)
    }
    is(gte) {
      io.branchTaken := (io.src1 >= io.src2)
    }
    is(lt) {
      io.branchTaken := (io.src1 < io.src2)
    }
    is(gteu) {
      io.branchTaken := (io.src1 >= io.src2)
    }
    is(ltu) {
      io.branchTaken := (io.src1 < io.src2)
    }
    is(jump) {
      io.branchTaken := (1.U)
    }
    is(DC) {
      io.branchTaken := (0.U)
    }
  }


}
