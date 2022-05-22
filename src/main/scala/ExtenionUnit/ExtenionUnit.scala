

package ExtenionUnit

import chisel3._
import chisel3.util._
import config.ExtensionCases._

class ExtenionUnit extends Module
{
  val io = IO(new Bundle{
    val ext_type   = Input(UInt(3.W))
    val instr      = Input(UInt(32.W))
    val ext_imm    = Output(UInt(32.W))
  })

  val instr = io.instr

  io.ext_imm := 0.U(32.W)

  switch(io.ext_type){
    is(id)    { io.ext_imm := Cat(Fill(18, instr(31)), instr(31), instr(7), instr(30,25), instr(11,8), 0.U(2.W))}
    is(jal)   { io.ext_imm := Cat(Fill(10, instr(31)), instr(31), instr(19,12), instr(20), instr(30,21), 0.U(2.W))}
    is(jalr)  { io.ext_imm := Cat(Fill(18, instr(31)), instr(31,20), 0.U(2.W))}
    is(auipc) { io.ext_imm := Cat(instr(31,12), 0.U(12.W))}
    is(store) { io.ext_imm := Cat(Fill(20, instr(31)), instr(31,25), instr(4,0))}
    is(i_type){ io.ext_imm := Cat(Fill(20, instr(31)), instr(31,20))}
  }
}
