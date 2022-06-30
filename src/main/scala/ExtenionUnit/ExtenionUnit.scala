

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

  switch(io.ext_type){ // Branch address should be calculated from current PC not PC+4, in PC_Reg we have PC+4. That is why 4 is subtracted from branch and jump targets
    is(id)    { io.ext_imm := Cat(Fill(19, instr(31)), instr(31), instr(7), instr(30,25), instr(11,8), 0.U(1.W)) - 4.U(32.W)}    // in case of id - branch target
    is(jal)   { io.ext_imm := Cat(Fill(11, instr(31)), instr(31), instr(19,12), instr(20), instr(30,21), 0.U(1.W))  - 4.U(32.W)}  // in case of jal
    is(jalr)  { io.ext_imm := Cat(Fill(19, instr(31)), instr(31,20), 0.U(1.W))  - 4.U(32.W)}                                      // in case of jalr
    is(auipc) { io.ext_imm := Cat(instr(31,12), 0.U(12.W))}                                                          // in case of auipc
    is(store) { io.ext_imm := Cat(Fill(20, instr(31)), instr(31,25), instr(11,7))}                                    // in case store
    is(i_type){ io.ext_imm := Cat(Fill(20, instr(31)), instr(31,20))}                                                // in case of ldr or i type ALU
  }
}
