package Branch_OP

import chisel3._
import chisel3.util._
import config.BranchOperation._

class Branch_OP extends Module {

  val io = IO(
    new Bundle {
      val branchType         = Input(UInt(4.W))
      val src1                = Input(UInt(32.W))
      val src2                = Input(UInt(32.W))

      val branchCondition = Output(UInt(1.W))
    }
  )

  //Branch if expressions are true
  //Branch lookup

  switch(io.branchType) {
    is(beq) {
      io.branchCondition := (io.src1 === io.src2)
    }
    is(neq) {
      io.branchCondition := (io.src1 =/= io.src2)
    }
    is(gte) {
      io.branchCondition := (io.src1 >= io.src2)
    }
    is(lt) {
      io.branchCondition := (io.src1 < io.src2)
    }
    is(gteu) {
      io.branchCondition := (io.src1 >= io.src2)
    }
    is(ltu) {
      io.branchCondition := (io.src1 < io.src2)
    }
    is(jump) {
      io.branchCondition := (1.U)
    }
    is(dc) {
      io.branchCondition := (0.U)
    }
  }

}
