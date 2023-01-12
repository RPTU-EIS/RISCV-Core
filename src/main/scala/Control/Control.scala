//package Control
//
//import config.ALUOps._
//import config.ExtensionCases._
//import config.States._
//
//
//import chisel3._
//import chisel3.util._
//
//class Control extends Module
//{
//  val io = IO(new Bundle {
//    val opcode = Input(UInt(7.W))
//    val funct3 = Input(UInt(3.W))
//    val funct7 = Input(UInt(7.W))
//
//    val controls = Output(UInt(13.W))
//    val aluOP    = Output(UInt(4.W))
//
//    val lsMux = Output(UInt(3.W))
//    val DM_WE = Output(Bool())
//    val DM_RE = Output(Bool())
//  })
//
//  val pc_mux       = Wire(UInt(1.W))
//  val is_if        = Wire(UInt(1.W))
//  val is_br        = Wire(UInt(1.W))
//  val ir_we        = Wire(UInt(1.W))
//  val gpr_we       = Wire(UInt(1.W))
//  val gpr_din_mux  = Wire(UInt(2.W))
//  val src1_alu_mux = Wire(UInt(1.W))
//  val src2_alu_mux = Wire(UInt(2.W))
//  val exts_type    = Wire(UInt(3.W))
//
//  val aluOP        = Wire(UInt(4.W))
//
//  val lsMux = Wire(UInt(3.W))
//  val DM_WE = Wire(Bool())
//  val DM_RE = Wire(Bool())
//
////  val fetch :: dec :: exec :: mem :: wb :: Nil = Enum(5) // state values
//
//  val stateReg  = RegInit(fetch)
//  val nextState = Wire(UInt(3.W))
//
//  nextState := fetch
//
//  pc_mux       := 0.U(1.W) // Wire(UInt(1.W))
//  is_if        := 0.U(1.W) // Wire(UInt(1.W))
//  is_br        := 0.U(1.W) // Wire(UInt(1.W))
//  ir_we        := 0.U(1.W) // Wire(UInt(1.W))
//  gpr_we       := 0.U(1.W) // Wire(UInt(1.W))
//  gpr_din_mux  := 0.U(2.W) // Wire(UInt(2.W))
//  src1_alu_mux := 0.U(1.W) // Wire(UInt(1.W))
//  src2_alu_mux := 0.U(2.W) // Wire(UInt(2.W))
//  exts_type    := 0.U(2.W) // Wire(UInt(3.W))
//
//  aluOP := 0.U(4.W)
//  lsMux := 0.U(3.W)
//  DM_WE := false.B
//  DM_RE := false.B
//
//  switch(stateReg){
//    is(fetch) { //
//      is_if        := 1.U(1.W)
//      is_br        := 0.U(1.W)
//      ir_we        := 1.U(1.W)
//      gpr_we       := 0.U(1.W)
//      gpr_din_mux  := 0.U(2.W)
//      src1_alu_mux := 0.U(1.W)
//      src2_alu_mux := 1.U(2.W)
//      DM_WE        := false.B
//      DM_RE        := false.B
//      exts_type    := 0.U(2.W)
//      pc_mux       := 0.U(1.W)
//      lsMux        := 0.U(3.W)
//      aluOP        := add
//      nextState    := dec
//    }
//    is(dec)   {
//      is_if        := 0.U(1.W)
//      is_br        := 0.U(1.W)
//      ir_we        := 0.U(1.W)
//      src1_alu_mux := 0.U(1.W)
//      DM_WE        := false.B
//      DM_RE        := false.B
//      exts_type    := id
//      pc_mux       := 0.U(1.W)
//      lsMux        := 0.U(3.W)
//      aluOP        := add
//      when(io.opcode === "b0110111".U(7.W)){ // LUI
//        gpr_we       := 1.U(1.W)
//        gpr_din_mux  := 2.U(2.W)
//        src2_alu_mux := 0.U(2.W)
//        nextState    := fetch
//      }.otherwise{
//        gpr_we       := 0.U(1.W)
//        gpr_din_mux  := 0.U(2.W)
//        src2_alu_mux := 2.U(2.W)
//        nextState    := exec
//      }
//    }
//    is(exec)  {
//      is_if        := 0.U(1.W)
//      is_br        := Mux(io.opcode === "b1100011".U(7.W), 1.U(1.W), 0.U(1.W)) // br ? 1 : 0
//      ir_we        := 0.U(1.W)
//      gpr_we       := 0.U(1.W)
//      gpr_din_mux  := 0.U(2.W)
//      DM_WE        := false.B
//      DM_RE        := false.B
//      pc_mux       := Mux(io.opcode === "b1100011".U(7.W), 1.U(1.W), 0.U(1.W)) //  br ? 1 : 0
//      lsMux        := 0.U(3.W)
//      nextState    := Mux(io.opcode === "b1100011".U(7.W), fetch, mem) //  br ? fetch : mem
//      when(io.opcode === "b0110011".U(7.W)){ // R type
//        src1_alu_mux := 1.U(1.W)
//        src2_alu_mux := 0.U(2.W)
//        exts_type    := 0.U(2.W)
//        when(io.funct3 === 5.U(3.W)){
//          aluOP := Mux(io.funct7(5) === 1.U(1.W), sra, srl)
//        }
//          .otherwise{
//          aluOP := Cat(io.funct7(5), io.funct3)
//        }
//      }
//        .elsewhen(io.opcode === "b0010011".U(7.W)){ // i type
//        src1_alu_mux := 1.U(1.W)
//        src2_alu_mux := 2.U(2.W)
//        exts_type    := i_type //0.U(2.W)
//        when(io.funct3 === 5.U(3.W)){
//          aluOP := Mux(io.funct7(5) === 1.U(1.W), sra, srl)
//        } .otherwise{
//          aluOP := Cat(0.U(1.W), io.funct3)
//        }
//      }
//        .elsewhen(io.opcode === "b0000011".U(7.W) | io.opcode === "b0100011".U(7.W)){ // load or store
//        src1_alu_mux := 1.U(1.W)
//        src2_alu_mux := 2.U(2.W)
//        exts_type    := Mux(io.opcode === "b0000011".U(7.W), i_type, store) // load ? i_type : store
//        aluOP        := add
//      }
//        .elsewhen(io.opcode === "b0010111".U(7.W)){ // auipc
//        src1_alu_mux := 0.U(1.W)
//        src2_alu_mux := 2.U(2.W)
//        exts_type    := auipc
//        aluOP        := add
//      }
//        .elsewhen(io.opcode === "b1100011".U(7.W)){ // branch
//        src1_alu_mux := 1.U(1.W)
//        src2_alu_mux := 0.U(2.W)
//        exts_type    := id// todo set to 0
//        aluOP        := Mux(io.funct3 === 0.U(3.W), beq, Cat(1.U(1.W), io.funct3))
//      }
//        .elsewhen(io.opcode === "b1101111".U(7.W) | io.opcode === "b1100111".U(7.W)){ // jal or jalr
//          src1_alu_mux := Mux(io.opcode === "b1101111".U(7.W), 0.U(1.W), 1.U(1.W)) // jal ? PC : register (alu 1st operand)
//          src2_alu_mux := 2.U(2.W)
//          exts_type    := Mux(io.opcode === "b1101111".U(7.W), jal, jalr) // jal ? jal : jalr (extension types)
//          aluOP        := add
//        }
//        .otherwise{ // nop
//          src1_alu_mux := 1.U(1.W)
//          src2_alu_mux := 2.U(2.W)
//          exts_type    := jalr // todo set to 0
//          aluOP        := add //
//        }
//    }
//    is(mem)   {
//      is_br        := 0.U(1.W)
//      ir_we        := 0.U(1.W)
//      gpr_we       := Mux((io.opcode === "b0000011".U(7.W)) | (io.opcode === "b0100011".U(7.W)), 0.U(1.W), 1.U(1.W)) // ls ? 0 : 1;
//      DM_WE        := Mux((io.opcode === "b0100011".U(7.W)), true.B, false.B) // store ? 1 : 0;
//      DM_RE        := Mux((io.opcode === "b0000011".U(7.W)), true.B, false.B) // load  ? 1 : 0;
//      src1_alu_mux := 1.U(1.W)
//      src2_alu_mux := 2.U(2.W)
//      exts_type    := jalr
//      aluOP        := add
//      nextState    := Mux(io.opcode === "b0000011".U(7.W), wb, fetch)  // load ? writeback : fetch
//        when(io.opcode === "b0110011".U(7.W) | io.opcode === "b0010011".U(7.W) | io.opcode === "b0010111".U(7.W)){ // Arith/Logic or AUIPC
//          is_if        := 0.U(1.W)
//          gpr_din_mux  := 0.U(2.W)
//          pc_mux       := 0.U(1.W)
//          lsMux        := 0.U(3.W)
//      }
//      .elsewhen(io.opcode === "b1100111".U(7.W) | io.opcode === "b1101111".U(7.W)){ // jal or jalr
//          is_if        := 1.U(1.W)
//          gpr_din_mux  := 3.U(2.W)
//          pc_mux       := 1.U(1.W)
//          lsMux        := 0.U(3.W)
//      }
//      .elsewhen((io.opcode === "b0000011".U(7.W)) | (io.opcode === "b0100011".U(7.W))){ // ls
//        is_if        := 0.U(1.W)
//        gpr_din_mux  := 0.U(2.W)
//        pc_mux       := 0.U(1.W)
//        lsMux        := io.funct3
//      }
//      .otherwise{
//        is_if        := 0.U(1.W)
//        gpr_din_mux  := 0.U(2.W)
//        pc_mux       := 0.U(1.W)
//        lsMux        := 0.U(3.W)
//      }
//    }
//    is(wb)    {
//      is_if        := 0.U(1.W)
//      is_br        := 0.U(1.W)
//      ir_we        := 0.U(1.W)
//      gpr_we       := 1.U(1.W)
//      gpr_din_mux  := 1.U(2.W)
//      src1_alu_mux := 0.U(1.W)
//      src2_alu_mux := 0.U(2.W)
//      DM_WE        := false.B
//      DM_RE        := false.B
//      exts_type    := 0.U(2.W)
//      pc_mux       := 0.U(1.W)
//      lsMux        := 0.U(3.W)
//      aluOP        := add
//      nextState    := fetch
//    }
//  }
//
//  stateReg := nextState // state update
//
//  // outputs
//  io.controls := Cat(pc_mux, is_if, is_br, ir_we, gpr_we, gpr_din_mux, src1_alu_mux, src2_alu_mux, exts_type)
//
//  io.aluOP := aluOP
//  io.lsMux := lsMux
//  io.DM_WE := DM_WE
//  io.DM_RE := DM_RE
//}
