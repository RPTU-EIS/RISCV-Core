module IFpipe(
  input         clock,
  input         reset,
  input  [31:0] io_inCurrentPC,
  input  [31:0] io_inInstruction_instruction,
  input         io_stall,
  input         io_flush,
  input         io_inBTBHit,
  input         io_inBTBPrediction,
  input  [31:0] io_inBTBTargetPredict,
  output        io_outBTBHit,
  output        io_outBTBPrediction,
  output [31:0] io_outBTBTargetPredict,
  output [31:0] io_outCurrentPC,
  output [31:0] io_outInstruction_instruction
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
`endif // RANDOMIZE_REG_INIT
  wire  _currentPCReg_T = ~io_stall; // @[IFpipe.scala 36:55]
  reg [31:0] currentPCReg; // @[Reg.scala 28:20]
  reg  flushDelayed; // @[IFpipe.scala 37:29]
  reg  btbHitReg; // @[IFpipe.scala 42:33]
  reg  btbPredictionReg; // @[IFpipe.scala 43:33]
  reg [31:0] btbTargetPredict; // @[IFpipe.scala 44:33]
  assign io_outBTBHit = btbHitReg; // @[IFpipe.scala 50:26]
  assign io_outBTBPrediction = btbPredictionReg; // @[IFpipe.scala 51:26]
  assign io_outBTBTargetPredict = btbTargetPredict; // @[IFpipe.scala 52:26]
  assign io_outCurrentPC = currentPCReg; // @[IFpipe.scala 63:19]
  assign io_outInstruction_instruction = flushDelayed ? 32'h33 : io_inInstruction_instruction; // @[IFpipe.scala 55:29 56:23 59:23]
  always @(posedge clock) begin
    if (reset) begin // @[Reg.scala 28:20]
      currentPCReg <= 32'h0; // @[Reg.scala 28:20]
    end else if (_currentPCReg_T) begin // @[Reg.scala 29:18]
      currentPCReg <= io_inCurrentPC; // @[Reg.scala 29:22]
    end
    if (reset) begin // @[IFpipe.scala 37:29]
      flushDelayed <= 1'h0; // @[IFpipe.scala 37:29]
    end else begin
      flushDelayed <= io_flush; // @[IFpipe.scala 39:16]
    end
    if (reset) begin // @[IFpipe.scala 42:33]
      btbHitReg <= 1'h0; // @[IFpipe.scala 42:33]
    end else begin
      btbHitReg <= io_inBTBHit; // @[IFpipe.scala 46:20]
    end
    if (reset) begin // @[IFpipe.scala 43:33]
      btbPredictionReg <= 1'h0; // @[IFpipe.scala 43:33]
    end else begin
      btbPredictionReg <= io_inBTBPrediction; // @[IFpipe.scala 47:20]
    end
    if (reset) begin // @[IFpipe.scala 44:33]
      btbTargetPredict <= 32'h0; // @[IFpipe.scala 44:33]
    end else begin
      btbTargetPredict <= io_inBTBTargetPredict; // @[IFpipe.scala 48:20]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  currentPCReg = _RAND_0[31:0];
  _RAND_1 = {1{`RANDOM}};
  flushDelayed = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  btbHitReg = _RAND_2[0:0];
  _RAND_3 = {1{`RANDOM}};
  btbPredictionReg = _RAND_3[0:0];
  _RAND_4 = {1{`RANDOM}};
  btbTargetPredict = _RAND_4[31:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module IDpipe(
  input         clock,
  input         reset,
  input  [31:0] io_inInstruction_instruction,
  input         io_inControlSignals_memToReg,
  input         io_inControlSignals_regWrite,
  input         io_inControlSignals_memRead,
  input         io_inControlSignals_memWrite,
  input  [31:0] io_inPC,
  input  [2:0]  io_inBranchType,
  input         io_inOp1Select,
  input         io_inOp2Select,
  input  [31:0] io_inImmData,
  input  [4:0]  io_inRd,
  input  [4:0]  io_inALUop,
  output [31:0] io_outInstruction_instruction,
  output        io_outControlSignals_memToReg,
  output        io_outControlSignals_regWrite,
  output        io_outControlSignals_memRead,
  output        io_outControlSignals_memWrite,
  output [31:0] io_outPC,
  output [2:0]  io_outBranchType,
  output        io_outOp1Select,
  output        io_outOp2Select,
  output [31:0] io_outImmData,
  output [4:0]  io_outRd,
  output [4:0]  io_outALUop,
  input  [31:0] io_inReadData1,
  input  [31:0] io_inReadData2,
  input         io_flush,
  input         io_stall,
  input         io_inBTBHit,
  input         io_inBTBPrediction,
  input  [31:0] io_inBTBTargetPredict,
  output        io_outBTBHit,
  output        io_outBTBPrediction,
  output [31:0] io_outBTBTargetPredict,
  output [31:0] io_outReadData1,
  output [31:0] io_outReadData2
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
  reg [31:0] _RAND_6;
  reg [31:0] _RAND_7;
  reg [31:0] _RAND_8;
  reg [31:0] _RAND_9;
  reg [31:0] _RAND_10;
  reg [31:0] _RAND_11;
  reg [31:0] _RAND_12;
  reg [31:0] _RAND_13;
  reg [31:0] _RAND_14;
  reg [31:0] _RAND_15;
  reg [31:0] _RAND_16;
`endif // RANDOMIZE_REG_INIT
  wire  _instructionReg_T = ~io_stall; // @[IDpipe.scala 68:59]
  reg [31:0] instructionReg_instruction; // @[Reg.scala 16:16]
  reg  controlSignalsReg_memToReg; // @[Reg.scala 16:16]
  reg  controlSignalsReg_regWrite; // @[Reg.scala 16:16]
  reg  controlSignalsReg_memRead; // @[Reg.scala 16:16]
  reg  controlSignalsReg_memWrite; // @[Reg.scala 16:16]
  reg [2:0] branchTypeReg; // @[Reg.scala 28:20]
  reg [31:0] PCReg; // @[Reg.scala 28:20]
  reg  op1SelectReg; // @[Reg.scala 28:20]
  reg  op2SelectReg; // @[Reg.scala 28:20]
  reg [31:0] immDataReg; // @[Reg.scala 28:20]
  reg [4:0] rdReg; // @[Reg.scala 28:20]
  reg [4:0] ALUopReg; // @[Reg.scala 28:20]
  reg [31:0] readData1Reg; // @[Reg.scala 28:20]
  reg [31:0] readData2Reg; // @[Reg.scala 28:20]
  reg  btbHitReg; // @[IDpipe.scala 81:33]
  reg  btbPredictionReg; // @[IDpipe.scala 82:33]
  reg [31:0] btbTargetPredict; // @[IDpipe.scala 83:33]
  assign io_outInstruction_instruction = instructionReg_instruction; // @[IDpipe.scala 102:24]
  assign io_outControlSignals_memToReg = controlSignalsReg_memToReg; // @[IDpipe.scala 103:24]
  assign io_outControlSignals_regWrite = controlSignalsReg_regWrite; // @[IDpipe.scala 103:24]
  assign io_outControlSignals_memRead = controlSignalsReg_memRead; // @[IDpipe.scala 103:24]
  assign io_outControlSignals_memWrite = controlSignalsReg_memWrite; // @[IDpipe.scala 103:24]
  assign io_outPC = PCReg; // @[IDpipe.scala 105:24]
  assign io_outBranchType = branchTypeReg; // @[IDpipe.scala 104:24]
  assign io_outOp1Select = op1SelectReg; // @[IDpipe.scala 106:24]
  assign io_outOp2Select = op2SelectReg; // @[IDpipe.scala 107:24]
  assign io_outImmData = immDataReg; // @[IDpipe.scala 108:24]
  assign io_outRd = rdReg; // @[IDpipe.scala 109:24]
  assign io_outALUop = ALUopReg; // @[IDpipe.scala 110:24]
  assign io_outBTBHit = btbHitReg; // @[IDpipe.scala 98:26]
  assign io_outBTBPrediction = btbPredictionReg; // @[IDpipe.scala 99:26]
  assign io_outBTBTargetPredict = btbTargetPredict; // @[IDpipe.scala 100:26]
  assign io_outReadData1 = readData1Reg; // @[IDpipe.scala 113:24]
  assign io_outReadData2 = readData2Reg; // @[IDpipe.scala 114:24]
  always @(posedge clock) begin
    if (io_flush) begin // @[IDpipe.scala 86:25]
      instructionReg_instruction <= 32'h33; // @[IDpipe.scala 87:23]
    end else if (_instructionReg_T) begin // @[Reg.scala 17:18]
      instructionReg_instruction <= io_inInstruction_instruction; // @[Reg.scala 17:22]
    end
    if (io_flush) begin // @[IDpipe.scala 86:25]
      controlSignalsReg_memToReg <= 1'h0; // @[IDpipe.scala 88:23]
    end else if (_instructionReg_T) begin // @[Reg.scala 17:18]
      controlSignalsReg_memToReg <= io_inControlSignals_memToReg; // @[Reg.scala 17:22]
    end
    if (io_flush) begin // @[IDpipe.scala 86:25]
      controlSignalsReg_regWrite <= 1'h0; // @[IDpipe.scala 88:23]
    end else if (_instructionReg_T) begin // @[Reg.scala 17:18]
      controlSignalsReg_regWrite <= io_inControlSignals_regWrite; // @[Reg.scala 17:22]
    end
    if (io_flush) begin // @[IDpipe.scala 86:25]
      controlSignalsReg_memRead <= 1'h0; // @[IDpipe.scala 88:23]
    end else if (_instructionReg_T) begin // @[Reg.scala 17:18]
      controlSignalsReg_memRead <= io_inControlSignals_memRead; // @[Reg.scala 17:22]
    end
    if (io_flush) begin // @[IDpipe.scala 86:25]
      controlSignalsReg_memWrite <= 1'h0; // @[IDpipe.scala 88:23]
    end else if (_instructionReg_T) begin // @[Reg.scala 17:18]
      controlSignalsReg_memWrite <= io_inControlSignals_memWrite; // @[Reg.scala 17:22]
    end
    if (reset) begin // @[Reg.scala 28:20]
      branchTypeReg <= 3'h7; // @[Reg.scala 28:20]
    end else if (io_flush) begin // @[IDpipe.scala 86:25]
      branchTypeReg <= 3'h7; // @[IDpipe.scala 90:23]
    end else if (_instructionReg_T) begin // @[Reg.scala 29:18]
      branchTypeReg <= io_inBranchType; // @[Reg.scala 29:22]
    end
    if (reset) begin // @[Reg.scala 28:20]
      PCReg <= 32'h0; // @[Reg.scala 28:20]
    end else if (_instructionReg_T) begin // @[Reg.scala 29:18]
      PCReg <= io_inPC; // @[Reg.scala 29:22]
    end
    if (reset) begin // @[Reg.scala 28:20]
      op1SelectReg <= 1'h0; // @[Reg.scala 28:20]
    end else if (_instructionReg_T) begin // @[Reg.scala 29:18]
      op1SelectReg <= io_inOp1Select; // @[Reg.scala 29:22]
    end
    if (reset) begin // @[Reg.scala 28:20]
      op2SelectReg <= 1'h0; // @[Reg.scala 28:20]
    end else if (_instructionReg_T) begin // @[Reg.scala 29:18]
      op2SelectReg <= io_inOp2Select; // @[Reg.scala 29:22]
    end
    if (reset) begin // @[Reg.scala 28:20]
      immDataReg <= 32'h0; // @[Reg.scala 28:20]
    end else if (_instructionReg_T) begin // @[Reg.scala 29:18]
      immDataReg <= io_inImmData; // @[Reg.scala 29:22]
    end
    if (reset) begin // @[Reg.scala 28:20]
      rdReg <= 5'h0; // @[Reg.scala 28:20]
    end else if (io_flush) begin // @[IDpipe.scala 86:25]
      rdReg <= 5'h0; // @[IDpipe.scala 91:23]
    end else if (_instructionReg_T) begin // @[Reg.scala 29:18]
      rdReg <= io_inRd; // @[Reg.scala 29:22]
    end
    if (reset) begin // @[Reg.scala 28:20]
      ALUopReg <= 5'h0; // @[Reg.scala 28:20]
    end else if (io_flush) begin // @[IDpipe.scala 86:25]
      ALUopReg <= 5'hf; // @[IDpipe.scala 89:23]
    end else if (_instructionReg_T) begin // @[Reg.scala 29:18]
      ALUopReg <= io_inALUop; // @[Reg.scala 29:22]
    end
    if (reset) begin // @[Reg.scala 28:20]
      readData1Reg <= 32'h0; // @[Reg.scala 28:20]
    end else if (_instructionReg_T) begin // @[Reg.scala 29:18]
      readData1Reg <= io_inReadData1; // @[Reg.scala 29:22]
    end
    if (reset) begin // @[Reg.scala 28:20]
      readData2Reg <= 32'h0; // @[Reg.scala 28:20]
    end else if (_instructionReg_T) begin // @[Reg.scala 29:18]
      readData2Reg <= io_inReadData2; // @[Reg.scala 29:22]
    end
    if (reset) begin // @[IDpipe.scala 81:33]
      btbHitReg <= 1'h0; // @[IDpipe.scala 81:33]
    end else begin
      btbHitReg <= io_inBTBHit; // @[IDpipe.scala 94:20]
    end
    if (reset) begin // @[IDpipe.scala 82:33]
      btbPredictionReg <= 1'h0; // @[IDpipe.scala 82:33]
    end else begin
      btbPredictionReg <= io_inBTBPrediction; // @[IDpipe.scala 95:20]
    end
    if (reset) begin // @[IDpipe.scala 83:33]
      btbTargetPredict <= 32'h0; // @[IDpipe.scala 83:33]
    end else begin
      btbTargetPredict <= io_inBTBTargetPredict; // @[IDpipe.scala 96:20]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  instructionReg_instruction = _RAND_0[31:0];
  _RAND_1 = {1{`RANDOM}};
  controlSignalsReg_memToReg = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  controlSignalsReg_regWrite = _RAND_2[0:0];
  _RAND_3 = {1{`RANDOM}};
  controlSignalsReg_memRead = _RAND_3[0:0];
  _RAND_4 = {1{`RANDOM}};
  controlSignalsReg_memWrite = _RAND_4[0:0];
  _RAND_5 = {1{`RANDOM}};
  branchTypeReg = _RAND_5[2:0];
  _RAND_6 = {1{`RANDOM}};
  PCReg = _RAND_6[31:0];
  _RAND_7 = {1{`RANDOM}};
  op1SelectReg = _RAND_7[0:0];
  _RAND_8 = {1{`RANDOM}};
  op2SelectReg = _RAND_8[0:0];
  _RAND_9 = {1{`RANDOM}};
  immDataReg = _RAND_9[31:0];
  _RAND_10 = {1{`RANDOM}};
  rdReg = _RAND_10[4:0];
  _RAND_11 = {1{`RANDOM}};
  ALUopReg = _RAND_11[4:0];
  _RAND_12 = {1{`RANDOM}};
  readData1Reg = _RAND_12[31:0];
  _RAND_13 = {1{`RANDOM}};
  readData2Reg = _RAND_13[31:0];
  _RAND_14 = {1{`RANDOM}};
  btbHitReg = _RAND_14[0:0];
  _RAND_15 = {1{`RANDOM}};
  btbPredictionReg = _RAND_15[0:0];
  _RAND_16 = {1{`RANDOM}};
  btbTargetPredict = _RAND_16[31:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module EXpipe(
  input         clock,
  input         reset,
  input         io_inControlSignals_memToReg,
  input         io_inControlSignals_regWrite,
  input         io_inControlSignals_memRead,
  input         io_inControlSignals_memWrite,
  input  [4:0]  io_inRd,
  input  [31:0] io_inRs2,
  input  [31:0] io_inALUResult,
  input         io_stall,
  output [31:0] io_outALUResult,
  output        io_outControlSignals_memToReg,
  output        io_outControlSignals_regWrite,
  output        io_outControlSignals_memRead,
  output        io_outControlSignals_memWrite,
  output [4:0]  io_outRd,
  output [31:0] io_outRs2
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
  reg [31:0] _RAND_6;
`endif // RANDOMIZE_REG_INIT
  wire  _ALUResultReg_T = ~io_stall; // @[EXpipe.scala 35:58]
  reg [31:0] ALUResultReg; // @[Reg.scala 28:20]
  reg  controlSignalsReg_memToReg; // @[Reg.scala 16:16]
  reg  controlSignalsReg_regWrite; // @[Reg.scala 16:16]
  reg  controlSignalsReg_memRead; // @[Reg.scala 16:16]
  reg  controlSignalsReg_memWrite; // @[Reg.scala 16:16]
  reg [4:0] rdReg; // @[Reg.scala 28:20]
  reg [31:0] rs2Reg; // @[Reg.scala 28:20]
  assign io_outALUResult = ALUResultReg; // @[EXpipe.scala 50:26]
  assign io_outControlSignals_memToReg = controlSignalsReg_memToReg; // @[EXpipe.scala 41:24]
  assign io_outControlSignals_regWrite = controlSignalsReg_regWrite; // @[EXpipe.scala 41:24]
  assign io_outControlSignals_memRead = controlSignalsReg_memRead; // @[EXpipe.scala 41:24]
  assign io_outControlSignals_memWrite = controlSignalsReg_memWrite; // @[EXpipe.scala 41:24]
  assign io_outRd = rdReg; // @[EXpipe.scala 44:26]
  assign io_outRs2 = rs2Reg; // @[EXpipe.scala 47:26]
  always @(posedge clock) begin
    if (reset) begin // @[Reg.scala 28:20]
      ALUResultReg <= 32'h0; // @[Reg.scala 28:20]
    end else if (_ALUResultReg_T) begin // @[Reg.scala 29:18]
      ALUResultReg <= io_inALUResult; // @[Reg.scala 29:22]
    end
    if (_ALUResultReg_T) begin // @[Reg.scala 17:18]
      controlSignalsReg_memToReg <= io_inControlSignals_memToReg; // @[Reg.scala 17:22]
    end
    if (_ALUResultReg_T) begin // @[Reg.scala 17:18]
      controlSignalsReg_regWrite <= io_inControlSignals_regWrite; // @[Reg.scala 17:22]
    end
    if (_ALUResultReg_T) begin // @[Reg.scala 17:18]
      controlSignalsReg_memRead <= io_inControlSignals_memRead; // @[Reg.scala 17:22]
    end
    if (_ALUResultReg_T) begin // @[Reg.scala 17:18]
      controlSignalsReg_memWrite <= io_inControlSignals_memWrite; // @[Reg.scala 17:22]
    end
    if (reset) begin // @[Reg.scala 28:20]
      rdReg <= 5'h0; // @[Reg.scala 28:20]
    end else if (_ALUResultReg_T) begin // @[Reg.scala 29:18]
      rdReg <= io_inRd; // @[Reg.scala 29:22]
    end
    if (reset) begin // @[Reg.scala 28:20]
      rs2Reg <= 32'h0; // @[Reg.scala 28:20]
    end else if (_ALUResultReg_T) begin // @[Reg.scala 29:18]
      rs2Reg <= io_inRs2; // @[Reg.scala 29:22]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  ALUResultReg = _RAND_0[31:0];
  _RAND_1 = {1{`RANDOM}};
  controlSignalsReg_memToReg = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  controlSignalsReg_regWrite = _RAND_2[0:0];
  _RAND_3 = {1{`RANDOM}};
  controlSignalsReg_memRead = _RAND_3[0:0];
  _RAND_4 = {1{`RANDOM}};
  controlSignalsReg_memWrite = _RAND_4[0:0];
  _RAND_5 = {1{`RANDOM}};
  rdReg = _RAND_5[4:0];
  _RAND_6 = {1{`RANDOM}};
  rs2Reg = _RAND_6[31:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module MEMpipe(
  input         clock,
  input         reset,
  input         io_inControlSignals_memToReg,
  input         io_inControlSignals_regWrite,
  input  [4:0]  io_inRd,
  input  [31:0] io_inMEMData,
  input  [31:0] io_inALUResult,
  input         io_stall,
  output [31:0] io_outMEMData,
  output        io_outControlSignals_memToReg,
  output        io_outControlSignals_regWrite,
  output [4:0]  io_outRd,
  output [31:0] io_outALUResult
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
`endif // RANDOMIZE_REG_INIT
  wire  _ALUResultReg_T = ~io_stall; // @[MEMpipe.scala 34:58]
  reg [31:0] ALUResultReg; // @[Reg.scala 28:20]
  reg  controlSignalsReg_memToReg; // @[Reg.scala 16:16]
  reg  controlSignalsReg_regWrite; // @[Reg.scala 16:16]
  reg [4:0] rdReg; // @[Reg.scala 28:20]
  assign io_outMEMData = io_inMEMData; // @[MEMpipe.scala 45:24]
  assign io_outControlSignals_memToReg = controlSignalsReg_memToReg; // @[MEMpipe.scala 39:24]
  assign io_outControlSignals_regWrite = controlSignalsReg_regWrite; // @[MEMpipe.scala 39:24]
  assign io_outRd = rdReg; // @[MEMpipe.scala 42:24]
  assign io_outALUResult = ALUResultReg; // @[MEMpipe.scala 48:24]
  always @(posedge clock) begin
    if (reset) begin // @[Reg.scala 28:20]
      ALUResultReg <= 32'h0; // @[Reg.scala 28:20]
    end else if (_ALUResultReg_T) begin // @[Reg.scala 29:18]
      ALUResultReg <= io_inALUResult; // @[Reg.scala 29:22]
    end
    if (_ALUResultReg_T) begin // @[Reg.scala 17:18]
      controlSignalsReg_memToReg <= io_inControlSignals_memToReg; // @[Reg.scala 17:22]
    end
    if (_ALUResultReg_T) begin // @[Reg.scala 17:18]
      controlSignalsReg_regWrite <= io_inControlSignals_regWrite; // @[Reg.scala 17:22]
    end
    if (reset) begin // @[Reg.scala 28:20]
      rdReg <= 5'h0; // @[Reg.scala 28:20]
    end else if (_ALUResultReg_T) begin // @[Reg.scala 29:18]
      rdReg <= io_inRd; // @[Reg.scala 29:22]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  ALUResultReg = _RAND_0[31:0];
  _RAND_1 = {1{`RANDOM}};
  controlSignalsReg_memToReg = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  controlSignalsReg_regWrite = _RAND_2[0:0];
  _RAND_3 = {1{`RANDOM}};
  rdReg = _RAND_3[4:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module InstructionMemory(
  input         clock,
  input         testHarness_setupSignals_setup,
  input  [31:0] testHarness_setupSignals_address,
  input  [31:0] testHarness_setupSignals_instruction,
  output [31:0] testHarness_requestedAddress,
  input  [31:0] io_instructionAddress,
  output [31:0] io_instruction
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] i_memory [0:4095]; // @[InstructionMemory.scala 42:29]
  wire  i_memory_io_instruction_MPORT_en; // @[InstructionMemory.scala 42:29]
  wire [11:0] i_memory_io_instruction_MPORT_addr; // @[InstructionMemory.scala 42:29]
  wire [31:0] i_memory_io_instruction_MPORT_data; // @[InstructionMemory.scala 42:29]
  wire [31:0] i_memory_MPORT_data; // @[InstructionMemory.scala 42:29]
  wire [11:0] i_memory_MPORT_addr; // @[InstructionMemory.scala 42:29]
  wire  i_memory_MPORT_mask; // @[InstructionMemory.scala 42:29]
  wire  i_memory_MPORT_en; // @[InstructionMemory.scala 42:29]
  reg  i_memory_io_instruction_MPORT_en_pipe_0;
  reg [11:0] i_memory_io_instruction_MPORT_addr_pipe_0;
  wire [31:0] addressSource = testHarness_setupSignals_setup ? testHarness_setupSignals_address : io_instructionAddress; // @[InstructionMemory.scala 49:39 50:19 52:19]
  assign i_memory_io_instruction_MPORT_en = i_memory_io_instruction_MPORT_en_pipe_0;
  assign i_memory_io_instruction_MPORT_addr = i_memory_io_instruction_MPORT_addr_pipe_0;
  assign i_memory_io_instruction_MPORT_data = i_memory[i_memory_io_instruction_MPORT_addr]; // @[InstructionMemory.scala 42:29]
  assign i_memory_MPORT_data = testHarness_setupSignals_instruction;
  assign i_memory_MPORT_addr = addressSource[11:0];
  assign i_memory_MPORT_mask = 1'h1;
  assign i_memory_MPORT_en = testHarness_setupSignals_setup;
  assign testHarness_requestedAddress = io_instructionAddress; // @[InstructionMemory.scala 47:32]
  assign io_instruction = i_memory_io_instruction_MPORT_data; // @[InstructionMemory.scala 60:18]
  always @(posedge clock) begin
    if (i_memory_MPORT_en & i_memory_MPORT_mask) begin
      i_memory[i_memory_MPORT_addr] <= i_memory_MPORT_data; // @[InstructionMemory.scala 42:29]
    end
    i_memory_io_instruction_MPORT_en_pipe_0 <= 1'h1;
    if (1'h1) begin
      i_memory_io_instruction_MPORT_addr_pipe_0 <= addressSource[13:2];
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
  integer initvar;
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  i_memory_io_instruction_MPORT_en_pipe_0 = _RAND_0[0:0];
  _RAND_1 = {1{`RANDOM}};
  i_memory_io_instruction_MPORT_addr_pipe_0 = _RAND_1[11:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
  initial begin
    $readmemh("src/test/programs/beq_test", i_memory);
  end
endmodule
module BTB_direct(
  input         clock,
  input         reset,
  input  [31:0] io_currentPC,
  input         io_newBranch,
  input  [31:0] io_entryPC,
  input  [31:0] io_entryBrTarget,
  input         io_branchMispredicted,
  input         io_updatePrediction,
  input         io_stall,
  output        io_prediction,
  output        io_btbHit,
  output [31:0] io_targetAdr
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
`endif // RANDOMIZE_REG_INIT
  reg [54:0] btb [0:63]; // @[BTB_direct.scala 45:16]
  wire  btb_btbOut_MPORT_en; // @[BTB_direct.scala 45:16]
  wire [5:0] btb_btbOut_MPORT_addr; // @[BTB_direct.scala 45:16]
  wire [54:0] btb_btbOut_MPORT_data; // @[BTB_direct.scala 45:16]
  wire [54:0] btb_MPORT_data; // @[BTB_direct.scala 45:16]
  wire [5:0] btb_MPORT_addr; // @[BTB_direct.scala 45:16]
  wire  btb_MPORT_mask; // @[BTB_direct.scala 45:16]
  wire  btb_MPORT_en; // @[BTB_direct.scala 45:16]
  reg [1:0] predictorArray [0:63]; // @[BTB_direct.scala 51:27]
  wire  predictorArray_predictorOut_MPORT_en; // @[BTB_direct.scala 51:27]
  wire [5:0] predictorArray_predictorOut_MPORT_addr; // @[BTB_direct.scala 51:27]
  wire [1:0] predictorArray_predictorOut_MPORT_data; // @[BTB_direct.scala 51:27]
  wire [1:0] predictorArray_MPORT_1_data; // @[BTB_direct.scala 51:27]
  wire [5:0] predictorArray_MPORT_1_addr; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_1_mask; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_1_en; // @[BTB_direct.scala 51:27]
  wire [1:0] predictorArray_MPORT_2_data; // @[BTB_direct.scala 51:27]
  wire [5:0] predictorArray_MPORT_2_addr; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_2_mask; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_2_en; // @[BTB_direct.scala 51:27]
  wire [1:0] predictorArray_MPORT_3_data; // @[BTB_direct.scala 51:27]
  wire [5:0] predictorArray_MPORT_3_addr; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_3_mask; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_3_en; // @[BTB_direct.scala 51:27]
  wire [1:0] predictorArray_MPORT_4_data; // @[BTB_direct.scala 51:27]
  wire [5:0] predictorArray_MPORT_4_addr; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_4_mask; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_4_en; // @[BTB_direct.scala 51:27]
  wire [1:0] predictorArray_MPORT_5_data; // @[BTB_direct.scala 51:27]
  wire [5:0] predictorArray_MPORT_5_addr; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_5_mask; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_5_en; // @[BTB_direct.scala 51:27]
  wire [1:0] predictorArray_MPORT_6_data; // @[BTB_direct.scala 51:27]
  wire [5:0] predictorArray_MPORT_6_addr; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_6_mask; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_6_en; // @[BTB_direct.scala 51:27]
  wire [1:0] predictorArray_MPORT_7_data; // @[BTB_direct.scala 51:27]
  wire [5:0] predictorArray_MPORT_7_addr; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_7_mask; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_7_en; // @[BTB_direct.scala 51:27]
  wire [1:0] predictorArray_MPORT_8_data; // @[BTB_direct.scala 51:27]
  wire [5:0] predictorArray_MPORT_8_addr; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_8_mask; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_8_en; // @[BTB_direct.scala 51:27]
  wire [1:0] predictorArray_MPORT_9_data; // @[BTB_direct.scala 51:27]
  wire [5:0] predictorArray_MPORT_9_addr; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_9_mask; // @[BTB_direct.scala 51:27]
  wire  predictorArray_MPORT_9_en; // @[BTB_direct.scala 51:27]
  wire  _btbWriteNewEntry_T_1 = ~io_stall; // @[BTB_direct.scala 60:75]
  wire  _btbWriteNewEntry_T_2 = io_newBranch & io_branchMispredicted & ~io_stall; // @[BTB_direct.scala 60:63]
  wire  btbWriteNewEntry = reset ? 1'h0 : io_newBranch & io_branchMispredicted & ~io_stall; // @[BTB_direct.scala 57:28 58:22 60:22]
  wire [54:0] btbOut = btb_btbOut_MPORT_data;
  wire  btbOutput_valid = btbOut[54]; // @[BTB_direct.scala 75:28]
  wire [23:0] btbOutput_Tag = btbOut[53:30]; // @[BTB_direct.scala 76:26]
  wire [29:0] btbOutput_branchTarget = btbOut[29:0]; // @[BTB_direct.scala 77:35]
  wire [1:0] predictorOut = predictorArray_predictorOut_MPORT_data; // @[BTB_direct.scala 52:26 91:16]
  wire [23:0] btbInput_Tag = io_entryPC[31:8]; // @[BTB_direct.scala 105:29]
  wire [29:0] btbInput_branchTarget = io_entryBrTarget[31:2]; // @[BTB_direct.scala 106:44]
  wire [24:0] hi = {btbWriteNewEntry,btbInput_Tag}; // @[BTB_direct.scala 111:38]
  reg [1:0] prevPrediction1; // @[BTB_direct.scala 119:32]
  reg [1:0] prevPrediction2; // @[BTB_direct.scala 120:32]
  wire  _GEN_15 = io_branchMispredicted ? 1'h0 : 1'h1; // @[BTB_direct.scala 130:47 134:25 51:27]
  wire  _GEN_70 = 2'h0 == prevPrediction2 ? 1'h0 : 2'h1 == prevPrediction2 & io_branchMispredicted; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_75 = 2'h0 == prevPrediction2 ? 1'h0 : 2'h1 == prevPrediction2 & _GEN_15; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_90 = 2'h2 == prevPrediction2 ? 1'h0 : 2'h0 == prevPrediction2 & io_branchMispredicted; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_95 = 2'h2 == prevPrediction2 ? 1'h0 : 2'h0 == prevPrediction2 & _GEN_15; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_100 = 2'h2 == prevPrediction2 ? 1'h0 : _GEN_70; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_105 = 2'h2 == prevPrediction2 ? 1'h0 : _GEN_75; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_110 = 2'h3 == prevPrediction2 & io_branchMispredicted; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_115 = 2'h3 == prevPrediction2 & _GEN_15; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_120 = 2'h3 == prevPrediction2 ? 1'h0 : 2'h2 == prevPrediction2 & io_branchMispredicted; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_125 = 2'h3 == prevPrediction2 ? 1'h0 : 2'h2 == prevPrediction2 & _GEN_15; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_130 = 2'h3 == prevPrediction2 ? 1'h0 : _GEN_90; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_135 = 2'h3 == prevPrediction2 ? 1'h0 : _GEN_95; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_140 = 2'h3 == prevPrediction2 ? 1'h0 : _GEN_100; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_145 = 2'h3 == prevPrediction2 ? 1'h0 : _GEN_105; // @[BTB_direct.scala 128:30 51:27]
  wire  _GEN_150 = io_updatePrediction & _btbWriteNewEntry_T_1 & _GEN_110; // @[BTB_direct.scala 126:68 51:27]
  wire  _GEN_155 = io_updatePrediction & _btbWriteNewEntry_T_1 & _GEN_115; // @[BTB_direct.scala 126:68 51:27]
  wire  _GEN_160 = io_updatePrediction & _btbWriteNewEntry_T_1 & _GEN_120; // @[BTB_direct.scala 126:68 51:27]
  wire  _GEN_165 = io_updatePrediction & _btbWriteNewEntry_T_1 & _GEN_125; // @[BTB_direct.scala 126:68 51:27]
  wire  _GEN_170 = io_updatePrediction & _btbWriteNewEntry_T_1 & _GEN_130; // @[BTB_direct.scala 126:68 51:27]
  wire  _GEN_175 = io_updatePrediction & _btbWriteNewEntry_T_1 & _GEN_135; // @[BTB_direct.scala 126:68 51:27]
  wire  _GEN_180 = io_updatePrediction & _btbWriteNewEntry_T_1 & _GEN_140; // @[BTB_direct.scala 126:68 51:27]
  wire  _GEN_185 = io_updatePrediction & _btbWriteNewEntry_T_1 & _GEN_145; // @[BTB_direct.scala 126:68 51:27]
  assign btb_btbOut_MPORT_en = 1'h1;
  assign btb_btbOut_MPORT_addr = io_currentPC[7:2];
  assign btb_btbOut_MPORT_data = btb[btb_btbOut_MPORT_addr]; // @[BTB_direct.scala 45:16]
  assign btb_MPORT_data = {hi,btbInput_branchTarget};
  assign btb_MPORT_addr = io_entryPC[7:2];
  assign btb_MPORT_mask = 1'h1;
  assign btb_MPORT_en = reset ? 1'h0 : _btbWriteNewEntry_T_2;
  assign predictorArray_predictorOut_MPORT_en = 1'h1;
  assign predictorArray_predictorOut_MPORT_addr = io_currentPC[7:2];
  assign predictorArray_predictorOut_MPORT_data = predictorArray[predictorArray_predictorOut_MPORT_addr]; // @[BTB_direct.scala 51:27]
  assign predictorArray_MPORT_1_data = 2'h0;
  assign predictorArray_MPORT_1_addr = io_entryPC[7:2];
  assign predictorArray_MPORT_1_mask = 1'h1;
  assign predictorArray_MPORT_1_en = reset ? 1'h0 : _btbWriteNewEntry_T_2;
  assign predictorArray_MPORT_2_data = 2'h2;
  assign predictorArray_MPORT_2_addr = io_entryPC[7:2];
  assign predictorArray_MPORT_2_mask = 1'h1;
  assign predictorArray_MPORT_2_en = btbWriteNewEntry ? 1'h0 : _GEN_150;
  assign predictorArray_MPORT_3_data = 2'h3;
  assign predictorArray_MPORT_3_addr = io_entryPC[7:2];
  assign predictorArray_MPORT_3_mask = 1'h1;
  assign predictorArray_MPORT_3_en = btbWriteNewEntry ? 1'h0 : _GEN_155;
  assign predictorArray_MPORT_4_data = 2'h1;
  assign predictorArray_MPORT_4_addr = io_entryPC[7:2];
  assign predictorArray_MPORT_4_mask = 1'h1;
  assign predictorArray_MPORT_4_en = btbWriteNewEntry ? 1'h0 : _GEN_160;
  assign predictorArray_MPORT_5_data = 2'h3;
  assign predictorArray_MPORT_5_addr = io_entryPC[7:2];
  assign predictorArray_MPORT_5_mask = 1'h1;
  assign predictorArray_MPORT_5_en = btbWriteNewEntry ? 1'h0 : _GEN_165;
  assign predictorArray_MPORT_6_data = 2'h1;
  assign predictorArray_MPORT_6_addr = io_entryPC[7:2];
  assign predictorArray_MPORT_6_mask = 1'h1;
  assign predictorArray_MPORT_6_en = btbWriteNewEntry ? 1'h0 : _GEN_170;
  assign predictorArray_MPORT_7_data = 2'h0;
  assign predictorArray_MPORT_7_addr = io_entryPC[7:2];
  assign predictorArray_MPORT_7_mask = 1'h1;
  assign predictorArray_MPORT_7_en = btbWriteNewEntry ? 1'h0 : _GEN_175;
  assign predictorArray_MPORT_8_data = 2'h2;
  assign predictorArray_MPORT_8_addr = io_entryPC[7:2];
  assign predictorArray_MPORT_8_mask = 1'h1;
  assign predictorArray_MPORT_8_en = btbWriteNewEntry ? 1'h0 : _GEN_180;
  assign predictorArray_MPORT_9_data = 2'h0;
  assign predictorArray_MPORT_9_addr = io_entryPC[7:2];
  assign predictorArray_MPORT_9_mask = 1'h1;
  assign predictorArray_MPORT_9_en = btbWriteNewEntry ? 1'h0 : _GEN_185;
  assign io_prediction = io_btbHit & (predictorOut == 2'h0 | predictorOut == 2'h1); // @[BTB_direct.scala 92:19]
  assign io_btbHit = btbOutput_valid & btbOutput_Tag == io_currentPC[31:8]; // @[BTB_direct.scala 79:36]
  assign io_targetAdr = {btbOutput_branchTarget,2'h0}; // @[Cat.scala 31:58]
  always @(posedge clock) begin
    if (btb_MPORT_en & btb_MPORT_mask) begin
      btb[btb_MPORT_addr] <= btb_MPORT_data; // @[BTB_direct.scala 45:16]
    end
    if (predictorArray_MPORT_1_en & predictorArray_MPORT_1_mask) begin
      predictorArray[predictorArray_MPORT_1_addr] <= predictorArray_MPORT_1_data; // @[BTB_direct.scala 51:27]
    end
    if (predictorArray_MPORT_2_en & predictorArray_MPORT_2_mask) begin
      predictorArray[predictorArray_MPORT_2_addr] <= predictorArray_MPORT_2_data; // @[BTB_direct.scala 51:27]
    end
    if (predictorArray_MPORT_3_en & predictorArray_MPORT_3_mask) begin
      predictorArray[predictorArray_MPORT_3_addr] <= predictorArray_MPORT_3_data; // @[BTB_direct.scala 51:27]
    end
    if (predictorArray_MPORT_4_en & predictorArray_MPORT_4_mask) begin
      predictorArray[predictorArray_MPORT_4_addr] <= predictorArray_MPORT_4_data; // @[BTB_direct.scala 51:27]
    end
    if (predictorArray_MPORT_5_en & predictorArray_MPORT_5_mask) begin
      predictorArray[predictorArray_MPORT_5_addr] <= predictorArray_MPORT_5_data; // @[BTB_direct.scala 51:27]
    end
    if (predictorArray_MPORT_6_en & predictorArray_MPORT_6_mask) begin
      predictorArray[predictorArray_MPORT_6_addr] <= predictorArray_MPORT_6_data; // @[BTB_direct.scala 51:27]
    end
    if (predictorArray_MPORT_7_en & predictorArray_MPORT_7_mask) begin
      predictorArray[predictorArray_MPORT_7_addr] <= predictorArray_MPORT_7_data; // @[BTB_direct.scala 51:27]
    end
    if (predictorArray_MPORT_8_en & predictorArray_MPORT_8_mask) begin
      predictorArray[predictorArray_MPORT_8_addr] <= predictorArray_MPORT_8_data; // @[BTB_direct.scala 51:27]
    end
    if (predictorArray_MPORT_9_en & predictorArray_MPORT_9_mask) begin
      predictorArray[predictorArray_MPORT_9_addr] <= predictorArray_MPORT_9_data; // @[BTB_direct.scala 51:27]
    end
    prevPrediction1 <= predictorArray_predictorOut_MPORT_data; // @[BTB_direct.scala 52:26 91:16]
    prevPrediction2 <= prevPrediction1; // @[BTB_direct.scala 120:32]
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
  integer initvar;
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  prevPrediction1 = _RAND_0[1:0];
  _RAND_1 = {1{`RANDOM}};
  prevPrediction2 = _RAND_1[1:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
  initial begin
    $readmemh("src/main/scala/Stage_IF/BTB_Init", btb);
  end
  initial begin
    $readmemb("src/main/scala/Stage_IF/Predictor_Init", predictorArray);
  end
endmodule
module IF(
  input         clock,
  input         reset,
  input         testHarness_InstructionMemorySetup_setup,
  input  [31:0] testHarness_InstructionMemorySetup_address,
  input  [31:0] testHarness_InstructionMemorySetup_instruction,
  output [31:0] testHarness_PC,
  input  [31:0] io_branchAddr,
  input  [31:0] io_IFBarrierPC,
  input         io_stall,
  input         io_updatePrediction,
  input         io_newBranch,
  input  [31:0] io_entryPC,
  input         io_branchTaken,
  input         io_branchMispredicted,
  input  [31:0] io_PCplus4ExStage,
  output        io_btbHit,
  output        io_btbPrediction,
  output [31:0] io_btbTargetPredict,
  output [31:0] io_PC,
  output [31:0] io_instruction_instruction
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
`endif // RANDOMIZE_REG_INIT
  wire  InstructionMemory_clock; // @[IF.scala 49:33]
  wire  InstructionMemory_testHarness_setupSignals_setup; // @[IF.scala 49:33]
  wire [31:0] InstructionMemory_testHarness_setupSignals_address; // @[IF.scala 49:33]
  wire [31:0] InstructionMemory_testHarness_setupSignals_instruction; // @[IF.scala 49:33]
  wire [31:0] InstructionMemory_testHarness_requestedAddress; // @[IF.scala 49:33]
  wire [31:0] InstructionMemory_io_instructionAddress; // @[IF.scala 49:33]
  wire [31:0] InstructionMemory_io_instruction; // @[IF.scala 49:33]
  wire  BTB_clock; // @[IF.scala 50:33]
  wire  BTB_reset; // @[IF.scala 50:33]
  wire [31:0] BTB_io_currentPC; // @[IF.scala 50:33]
  wire  BTB_io_newBranch; // @[IF.scala 50:33]
  wire [31:0] BTB_io_entryPC; // @[IF.scala 50:33]
  wire [31:0] BTB_io_entryBrTarget; // @[IF.scala 50:33]
  wire  BTB_io_branchMispredicted; // @[IF.scala 50:33]
  wire  BTB_io_updatePrediction; // @[IF.scala 50:33]
  wire  BTB_io_stall; // @[IF.scala 50:33]
  wire  BTB_io_prediction; // @[IF.scala 50:33]
  wire  BTB_io_btbHit; // @[IF.scala 50:33]
  wire [31:0] BTB_io_targetAdr; // @[IF.scala 50:33]
  reg [31:0] PC; // @[IF.scala 52:34]
  wire [31:0] _instruction_WIRE_1 = InstructionMemory_io_instruction;
  wire [31:0] PCplus4 = PC + 32'h4; // @[IF.scala 64:17]
  wire [31:0] _GEN_2 = io_branchTaken ? io_branchAddr : io_PCplus4ExStage; // @[IF.scala 92:25 93:14 96:14]
  wire [31:0] _GEN_3 = BTB_io_prediction ? BTB_io_targetAdr : PCplus4; // @[IF.scala 100:28 101:14 104:14]
  wire [31:0] _GEN_4 = BTB_io_btbHit ? _GEN_3 : PCplus4; // @[IF.scala 108:12 99:27]
  InstructionMemory InstructionMemory ( // @[IF.scala 49:33]
    .clock(InstructionMemory_clock),
    .testHarness_setupSignals_setup(InstructionMemory_testHarness_setupSignals_setup),
    .testHarness_setupSignals_address(InstructionMemory_testHarness_setupSignals_address),
    .testHarness_setupSignals_instruction(InstructionMemory_testHarness_setupSignals_instruction),
    .testHarness_requestedAddress(InstructionMemory_testHarness_requestedAddress),
    .io_instructionAddress(InstructionMemory_io_instructionAddress),
    .io_instruction(InstructionMemory_io_instruction)
  );
  BTB_direct BTB ( // @[IF.scala 50:33]
    .clock(BTB_clock),
    .reset(BTB_reset),
    .io_currentPC(BTB_io_currentPC),
    .io_newBranch(BTB_io_newBranch),
    .io_entryPC(BTB_io_entryPC),
    .io_entryBrTarget(BTB_io_entryBrTarget),
    .io_branchMispredicted(BTB_io_branchMispredicted),
    .io_updatePrediction(BTB_io_updatePrediction),
    .io_stall(BTB_io_stall),
    .io_prediction(BTB_io_prediction),
    .io_btbHit(BTB_io_btbHit),
    .io_targetAdr(BTB_io_targetAdr)
  );
  assign testHarness_PC = InstructionMemory_testHarness_requestedAddress; // @[IF.scala 59:18]
  assign io_btbHit = BTB_io_btbHit; // @[IF.scala 75:13]
  assign io_btbPrediction = BTB_io_prediction; // @[IF.scala 74:20]
  assign io_btbTargetPredict = BTB_io_targetAdr; // @[IF.scala 76:23]
  assign io_PC = PC; // @[IF.scala 112:9]
  assign io_instruction_instruction = testHarness_InstructionMemorySetup_setup ? 32'h33 : _instruction_WIRE_1; // @[IF.scala 116:50 118:17 61:15]
  assign InstructionMemory_clock = clock;
  assign InstructionMemory_testHarness_setupSignals_setup = testHarness_InstructionMemorySetup_setup; // @[IF.scala 58:46]
  assign InstructionMemory_testHarness_setupSignals_address = testHarness_InstructionMemorySetup_address; // @[IF.scala 58:46]
  assign InstructionMemory_testHarness_setupSignals_instruction = testHarness_InstructionMemorySetup_instruction; // @[IF.scala 58:46]
  assign InstructionMemory_io_instructionAddress = io_stall ? io_IFBarrierPC : PC; // @[IF.scala 79:17 82:45 86:45]
  assign BTB_clock = clock;
  assign BTB_reset = reset;
  assign BTB_io_currentPC = PC; // @[IF.scala 67:20]
  assign BTB_io_newBranch = io_newBranch; // @[IF.scala 68:20]
  assign BTB_io_entryPC = io_entryPC; // @[IF.scala 70:18]
  assign BTB_io_entryBrTarget = io_branchAddr; // @[IF.scala 71:24]
  assign BTB_io_branchMispredicted = io_branchMispredicted; // @[IF.scala 72:29]
  assign BTB_io_updatePrediction = io_updatePrediction; // @[IF.scala 69:27]
  assign BTB_io_stall = io_stall; // @[IF.scala 73:16]
  always @(posedge clock) begin
    if (reset) begin // @[IF.scala 52:34]
      PC <= 32'h0; // @[IF.scala 52:34]
    end else if (testHarness_InstructionMemorySetup_setup) begin // @[IF.scala 116:50]
      PC <= 32'h0; // @[IF.scala 117:8]
    end else if (!(io_stall)) begin // @[IF.scala 79:17]
      if (io_branchMispredicted) begin // @[IF.scala 91:30]
        PC <= _GEN_2;
      end else begin
        PC <= _GEN_4;
      end
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  PC = _RAND_0[31:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module registerFile(
  input         clock,
  input         testHarness_setup_setup,
  input  [4:0]  testHarness_setup_readAddress,
  input         testHarness_setup_writeEnable,
  input  [31:0] testHarness_setup_writeData,
  output        testHarness_testUpdates_writeEnable,
  output [31:0] testHarness_testUpdates_writeData,
  output [4:0]  testHarness_testUpdates_writeAddress,
  input  [4:0]  io_readAddress1,
  input  [4:0]  io_readAddress2,
  input         io_writeEnable,
  input  [4:0]  io_writeAddress,
  input  [31:0] io_writeData,
  output [31:0] io_readData1,
  output [31:0] io_readData2
);
`ifdef RANDOMIZE_MEM_INIT
  reg [31:0] _RAND_0;
`endif // RANDOMIZE_MEM_INIT
  reg [31:0] registerFile [0:31]; // @[registerFile.scala 41:25]
  wire  registerFile_io_readData1_MPORT_en; // @[registerFile.scala 41:25]
  wire [4:0] registerFile_io_readData1_MPORT_addr; // @[registerFile.scala 41:25]
  wire [31:0] registerFile_io_readData1_MPORT_data; // @[registerFile.scala 41:25]
  wire  registerFile_io_readData2_MPORT_en; // @[registerFile.scala 41:25]
  wire [4:0] registerFile_io_readData2_MPORT_addr; // @[registerFile.scala 41:25]
  wire [31:0] registerFile_io_readData2_MPORT_data; // @[registerFile.scala 41:25]
  wire [31:0] registerFile_MPORT_data; // @[registerFile.scala 41:25]
  wire [4:0] registerFile_MPORT_addr; // @[registerFile.scala 41:25]
  wire  registerFile_MPORT_mask; // @[registerFile.scala 41:25]
  wire  registerFile_MPORT_en; // @[registerFile.scala 41:25]
  wire [4:0] readAddress1 = testHarness_setup_setup ? testHarness_setup_readAddress : io_readAddress1; // @[registerFile.scala 48:32 49:18 55:18]
  wire  writeEnable = testHarness_setup_setup ? testHarness_setup_writeEnable : io_writeEnable; // @[registerFile.scala 48:32 52:18 58:18]
  wire [4:0] writeAddress = testHarness_setup_setup ? testHarness_setup_readAddress : io_writeAddress; // @[registerFile.scala 48:32 53:18 59:18]
  wire  _T = writeAddress != 5'h0; // @[registerFile.scala 69:23]
  assign registerFile_io_readData1_MPORT_en = readAddress1 != 5'h0;
  assign registerFile_io_readData1_MPORT_addr = testHarness_setup_setup ? testHarness_setup_readAddress :
    io_readAddress1;
  assign registerFile_io_readData1_MPORT_data = registerFile[registerFile_io_readData1_MPORT_addr]; // @[registerFile.scala 41:25]
  assign registerFile_io_readData2_MPORT_en = io_readAddress2 != 5'h0;
  assign registerFile_io_readData2_MPORT_addr = io_readAddress2;
  assign registerFile_io_readData2_MPORT_data = registerFile[registerFile_io_readData2_MPORT_addr]; // @[registerFile.scala 41:25]
  assign registerFile_MPORT_data = testHarness_setup_setup ? testHarness_setup_writeData : io_writeData;
  assign registerFile_MPORT_addr = testHarness_setup_setup ? testHarness_setup_readAddress : io_writeAddress;
  assign registerFile_MPORT_mask = 1'h1;
  assign registerFile_MPORT_en = writeEnable & _T;
  assign testHarness_testUpdates_writeEnable = testHarness_setup_setup ? testHarness_setup_writeEnable : io_writeEnable; // @[registerFile.scala 48:32 52:18 58:18]
  assign testHarness_testUpdates_writeData = testHarness_setup_setup ? testHarness_setup_writeData : io_writeData; // @[registerFile.scala 48:32 51:18 57:18]
  assign testHarness_testUpdates_writeAddress = testHarness_setup_setup ? testHarness_setup_readAddress :
    io_writeAddress; // @[registerFile.scala 48:32 53:18 59:18]
  assign io_readData1 = readAddress1 != 5'h0 ? registerFile_io_readData1_MPORT_data : 32'h0; // @[registerFile.scala 75:16 77:{29,44}]
  assign io_readData2 = io_readAddress2 != 5'h0 ? registerFile_io_readData2_MPORT_data : 32'h0; // @[registerFile.scala 76:16 78:{29,44}]
  always @(posedge clock) begin
    if (registerFile_MPORT_en & registerFile_MPORT_mask) begin
      registerFile[registerFile_MPORT_addr] <= registerFile_MPORT_data; // @[registerFile.scala 41:25]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_MEM_INIT
  _RAND_0 = {1{`RANDOM}};
  for (initvar = 0; initvar < 32; initvar = initvar+1)
    registerFile[initvar] = _RAND_0[31:0];
`endif // RANDOMIZE_MEM_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module Decode(
  input  [31:0] io_instruction_instruction,
  output        io_controlSignals_memToReg,
  output        io_controlSignals_regWrite,
  output        io_controlSignals_memRead,
  output        io_controlSignals_memWrite,
  output [2:0]  io_branchType,
  output        io_op1Select,
  output        io_op2Select,
  output [2:0]  io_immType,
  output [4:0]  io_ALUop
);
  wire [31:0] _decodedControlSignals_T = io_instruction_instruction & 32'h707f; // @[Lookup.scala 31:38]
  wire  decodedControlSignals_0 = 32'h2003 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_3 = 32'h2023 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire [31:0] _decodedControlSignals_T_4 = io_instruction_instruction & 32'hfe00707f; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_5 = 32'h33 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_7 = 32'h13 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_9 = 32'h40000033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_11 = 32'h7033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_13 = 32'h7013 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_15 = 32'h6033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_17 = 32'h6013 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_19 = 32'h4033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_21 = 32'h4013 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire [31:0] _decodedControlSignals_T_22 = io_instruction_instruction & 32'h7f; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_23 = 32'h37 == _decodedControlSignals_T_22; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_25 = 32'h17 == _decodedControlSignals_T_22; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_27 = 32'h40005033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire [31:0] _decodedControlSignals_T_28 = io_instruction_instruction & 32'hfc00707f; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_29 = 32'h40005013 == _decodedControlSignals_T_28; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_31 = 32'h5033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_33 = 32'h5013 == _decodedControlSignals_T_28; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_35 = 32'h1033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_37 = 32'h1013 == _decodedControlSignals_T_28; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_39 = 32'h2033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_41 = 32'h2013 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_43 = 32'h3033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_45 = 32'h3013 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_47 = 32'h6f == _decodedControlSignals_T_22; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_49 = 32'h67 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_51 = 32'h63 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_53 = 32'h1063 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_55 = 32'h4063 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_57 = 32'h5063 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_59 = 32'h6063 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_61 = 32'h7063 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_63 = 32'h2000033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_65 = 32'h2004033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_67 = 32'h2006033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_69 = 32'h2001033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_71 = 32'h2002033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_73 = 32'h2003033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_75 = 32'h2005033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_77 = 32'h2007033 == _decodedControlSignals_T_4; // @[Lookup.scala 31:38]
  wire  _decodedControlSignals_T_124 = _decodedControlSignals_T_61 ? 1'h0 : _decodedControlSignals_T_63 | (
    _decodedControlSignals_T_65 | (_decodedControlSignals_T_67 | (_decodedControlSignals_T_69 | (
    _decodedControlSignals_T_71 | (_decodedControlSignals_T_73 | (_decodedControlSignals_T_75 |
    _decodedControlSignals_T_77)))))); // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_125 = _decodedControlSignals_T_59 ? 1'h0 : _decodedControlSignals_T_124; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_126 = _decodedControlSignals_T_57 ? 1'h0 : _decodedControlSignals_T_125; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_127 = _decodedControlSignals_T_55 ? 1'h0 : _decodedControlSignals_T_126; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_128 = _decodedControlSignals_T_53 ? 1'h0 : _decodedControlSignals_T_127; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_129 = _decodedControlSignals_T_51 ? 1'h0 : _decodedControlSignals_T_128; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_153 = _decodedControlSignals_T_3 ? 1'h0 : _decodedControlSignals_T_5 | (
    _decodedControlSignals_T_7 | (_decodedControlSignals_T_9 | (_decodedControlSignals_T_11 | (
    _decodedControlSignals_T_13 | (_decodedControlSignals_T_15 | (_decodedControlSignals_T_17 | (
    _decodedControlSignals_T_19 | (_decodedControlSignals_T_21 | (_decodedControlSignals_T_23 | (
    _decodedControlSignals_T_25 | (_decodedControlSignals_T_27 | (_decodedControlSignals_T_29 | (
    _decodedControlSignals_T_31 | (_decodedControlSignals_T_33 | (_decodedControlSignals_T_35 | (
    _decodedControlSignals_T_37 | (_decodedControlSignals_T_39 | (_decodedControlSignals_T_41 | (
    _decodedControlSignals_T_43 | (_decodedControlSignals_T_45 | (_decodedControlSignals_T_47 | (
    _decodedControlSignals_T_49 | _decodedControlSignals_T_129)))))))))))))))))))))); // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_244 = _decodedControlSignals_T_49 ? 1'h0 : _decodedControlSignals_T_51 | (
    _decodedControlSignals_T_53 | (_decodedControlSignals_T_55 | (_decodedControlSignals_T_57 | (
    _decodedControlSignals_T_59 | _decodedControlSignals_T_61)))); // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_314 = _decodedControlSignals_T_61 ? 3'h4 : 3'h7; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_315 = _decodedControlSignals_T_59 ? 3'h5 : _decodedControlSignals_T_314; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_316 = _decodedControlSignals_T_57 ? 3'h2 : _decodedControlSignals_T_315; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_317 = _decodedControlSignals_T_55 ? 3'h3 : _decodedControlSignals_T_316; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_318 = _decodedControlSignals_T_53 ? 3'h1 : _decodedControlSignals_T_317; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_319 = _decodedControlSignals_T_51 ? 3'h0 : _decodedControlSignals_T_318; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_320 = _decodedControlSignals_T_49 ? 3'h6 : _decodedControlSignals_T_319; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_321 = _decodedControlSignals_T_47 ? 3'h6 : _decodedControlSignals_T_320; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_322 = _decodedControlSignals_T_45 ? 3'h7 : _decodedControlSignals_T_321; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_323 = _decodedControlSignals_T_43 ? 3'h7 : _decodedControlSignals_T_322; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_324 = _decodedControlSignals_T_41 ? 3'h7 : _decodedControlSignals_T_323; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_325 = _decodedControlSignals_T_39 ? 3'h7 : _decodedControlSignals_T_324; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_326 = _decodedControlSignals_T_37 ? 3'h7 : _decodedControlSignals_T_325; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_327 = _decodedControlSignals_T_35 ? 3'h7 : _decodedControlSignals_T_326; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_328 = _decodedControlSignals_T_33 ? 3'h7 : _decodedControlSignals_T_327; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_329 = _decodedControlSignals_T_31 ? 3'h7 : _decodedControlSignals_T_328; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_330 = _decodedControlSignals_T_29 ? 3'h7 : _decodedControlSignals_T_329; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_331 = _decodedControlSignals_T_27 ? 3'h7 : _decodedControlSignals_T_330; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_332 = _decodedControlSignals_T_25 ? 3'h7 : _decodedControlSignals_T_331; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_333 = _decodedControlSignals_T_23 ? 3'h7 : _decodedControlSignals_T_332; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_334 = _decodedControlSignals_T_21 ? 3'h7 : _decodedControlSignals_T_333; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_335 = _decodedControlSignals_T_19 ? 3'h7 : _decodedControlSignals_T_334; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_336 = _decodedControlSignals_T_17 ? 3'h7 : _decodedControlSignals_T_335; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_337 = _decodedControlSignals_T_15 ? 3'h7 : _decodedControlSignals_T_336; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_338 = _decodedControlSignals_T_13 ? 3'h7 : _decodedControlSignals_T_337; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_339 = _decodedControlSignals_T_11 ? 3'h7 : _decodedControlSignals_T_338; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_340 = _decodedControlSignals_T_9 ? 3'h7 : _decodedControlSignals_T_339; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_341 = _decodedControlSignals_T_7 ? 3'h7 : _decodedControlSignals_T_340; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_342 = _decodedControlSignals_T_5 ? 3'h7 : _decodedControlSignals_T_341; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_343 = _decodedControlSignals_T_3 ? 3'h7 : _decodedControlSignals_T_342; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_360 = _decodedControlSignals_T_45 ? 1'h0 : _decodedControlSignals_T_47 |
    _decodedControlSignals_T_244; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_361 = _decodedControlSignals_T_43 ? 1'h0 : _decodedControlSignals_T_360; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_362 = _decodedControlSignals_T_41 ? 1'h0 : _decodedControlSignals_T_361; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_363 = _decodedControlSignals_T_39 ? 1'h0 : _decodedControlSignals_T_362; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_364 = _decodedControlSignals_T_37 ? 1'h0 : _decodedControlSignals_T_363; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_365 = _decodedControlSignals_T_35 ? 1'h0 : _decodedControlSignals_T_364; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_366 = _decodedControlSignals_T_33 ? 1'h0 : _decodedControlSignals_T_365; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_367 = _decodedControlSignals_T_31 ? 1'h0 : _decodedControlSignals_T_366; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_368 = _decodedControlSignals_T_29 ? 1'h0 : _decodedControlSignals_T_367; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_369 = _decodedControlSignals_T_27 ? 1'h0 : _decodedControlSignals_T_368; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_370 = _decodedControlSignals_T_25 ? 1'h0 : _decodedControlSignals_T_369; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_371 = _decodedControlSignals_T_23 ? 1'h0 : _decodedControlSignals_T_370; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_372 = _decodedControlSignals_T_21 ? 1'h0 : _decodedControlSignals_T_371; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_373 = _decodedControlSignals_T_19 ? 1'h0 : _decodedControlSignals_T_372; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_374 = _decodedControlSignals_T_17 ? 1'h0 : _decodedControlSignals_T_373; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_375 = _decodedControlSignals_T_15 ? 1'h0 : _decodedControlSignals_T_374; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_376 = _decodedControlSignals_T_13 ? 1'h0 : _decodedControlSignals_T_375; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_377 = _decodedControlSignals_T_11 ? 1'h0 : _decodedControlSignals_T_376; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_378 = _decodedControlSignals_T_9 ? 1'h0 : _decodedControlSignals_T_377; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_379 = _decodedControlSignals_T_7 ? 1'h0 : _decodedControlSignals_T_378; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_380 = _decodedControlSignals_T_5 ? 1'h0 : _decodedControlSignals_T_379; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_381 = _decodedControlSignals_T_3 ? 1'h0 : _decodedControlSignals_T_380; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_399 = _decodedControlSignals_T_43 ? 1'h0 : _decodedControlSignals_T_45 | (
    _decodedControlSignals_T_47 | (_decodedControlSignals_T_49 | (_decodedControlSignals_T_51 | (
    _decodedControlSignals_T_53 | (_decodedControlSignals_T_55 | (_decodedControlSignals_T_57 | (
    _decodedControlSignals_T_59 | _decodedControlSignals_T_61))))))); // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_401 = _decodedControlSignals_T_39 ? 1'h0 : _decodedControlSignals_T_41 |
    _decodedControlSignals_T_399; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_403 = _decodedControlSignals_T_35 ? 1'h0 : _decodedControlSignals_T_37 |
    _decodedControlSignals_T_401; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_405 = _decodedControlSignals_T_31 ? 1'h0 : _decodedControlSignals_T_33 |
    _decodedControlSignals_T_403; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_407 = _decodedControlSignals_T_27 ? 1'h0 : _decodedControlSignals_T_29 |
    _decodedControlSignals_T_405; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_411 = _decodedControlSignals_T_19 ? 1'h0 : _decodedControlSignals_T_21 | (
    _decodedControlSignals_T_23 | (_decodedControlSignals_T_25 | _decodedControlSignals_T_407)); // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_413 = _decodedControlSignals_T_15 ? 1'h0 : _decodedControlSignals_T_17 |
    _decodedControlSignals_T_411; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_415 = _decodedControlSignals_T_11 ? 1'h0 : _decodedControlSignals_T_13 |
    _decodedControlSignals_T_413; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_416 = _decodedControlSignals_T_9 ? 1'h0 : _decodedControlSignals_T_415; // @[Lookup.scala 34:39]
  wire  _decodedControlSignals_T_418 = _decodedControlSignals_T_5 ? 1'h0 : _decodedControlSignals_T_7 |
    _decodedControlSignals_T_416; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_428 = _decodedControlSignals_T_61 ? 3'h2 : 3'h0; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_429 = _decodedControlSignals_T_59 ? 3'h2 : _decodedControlSignals_T_428; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_430 = _decodedControlSignals_T_57 ? 3'h2 : _decodedControlSignals_T_429; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_431 = _decodedControlSignals_T_55 ? 3'h2 : _decodedControlSignals_T_430; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_432 = _decodedControlSignals_T_53 ? 3'h2 : _decodedControlSignals_T_431; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_433 = _decodedControlSignals_T_51 ? 3'h2 : _decodedControlSignals_T_432; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_434 = _decodedControlSignals_T_49 ? 3'h0 : _decodedControlSignals_T_433; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_435 = _decodedControlSignals_T_47 ? 3'h4 : _decodedControlSignals_T_434; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_436 = _decodedControlSignals_T_45 ? 3'h0 : _decodedControlSignals_T_435; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_437 = _decodedControlSignals_T_43 ? 3'h0 : _decodedControlSignals_T_436; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_438 = _decodedControlSignals_T_41 ? 3'h0 : _decodedControlSignals_T_437; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_439 = _decodedControlSignals_T_39 ? 3'h0 : _decodedControlSignals_T_438; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_440 = _decodedControlSignals_T_37 ? 3'h0 : _decodedControlSignals_T_439; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_441 = _decodedControlSignals_T_35 ? 3'h0 : _decodedControlSignals_T_440; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_442 = _decodedControlSignals_T_33 ? 3'h0 : _decodedControlSignals_T_441; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_443 = _decodedControlSignals_T_31 ? 3'h0 : _decodedControlSignals_T_442; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_444 = _decodedControlSignals_T_29 ? 3'h0 : _decodedControlSignals_T_443; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_445 = _decodedControlSignals_T_27 ? 3'h0 : _decodedControlSignals_T_444; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_446 = _decodedControlSignals_T_25 ? 3'h3 : _decodedControlSignals_T_445; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_447 = _decodedControlSignals_T_23 ? 3'h3 : _decodedControlSignals_T_446; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_448 = _decodedControlSignals_T_21 ? 3'h0 : _decodedControlSignals_T_447; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_449 = _decodedControlSignals_T_19 ? 3'h0 : _decodedControlSignals_T_448; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_450 = _decodedControlSignals_T_17 ? 3'h0 : _decodedControlSignals_T_449; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_451 = _decodedControlSignals_T_15 ? 3'h0 : _decodedControlSignals_T_450; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_452 = _decodedControlSignals_T_13 ? 3'h0 : _decodedControlSignals_T_451; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_453 = _decodedControlSignals_T_11 ? 3'h0 : _decodedControlSignals_T_452; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_454 = _decodedControlSignals_T_9 ? 3'h0 : _decodedControlSignals_T_453; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_455 = _decodedControlSignals_T_7 ? 3'h0 : _decodedControlSignals_T_454; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_456 = _decodedControlSignals_T_5 ? 3'h0 : _decodedControlSignals_T_455; // @[Lookup.scala 34:39]
  wire [2:0] _decodedControlSignals_T_457 = _decodedControlSignals_T_3 ? 3'h1 : _decodedControlSignals_T_456; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_458 = _decodedControlSignals_T_77 ? 5'h17 : 5'hf; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_459 = _decodedControlSignals_T_75 ? 5'h16 : _decodedControlSignals_T_458; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_460 = _decodedControlSignals_T_73 ? 5'h15 : _decodedControlSignals_T_459; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_461 = _decodedControlSignals_T_71 ? 5'h14 : _decodedControlSignals_T_460; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_462 = _decodedControlSignals_T_69 ? 5'h13 : _decodedControlSignals_T_461; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_463 = _decodedControlSignals_T_67 ? 5'h12 : _decodedControlSignals_T_462; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_464 = _decodedControlSignals_T_65 ? 5'h11 : _decodedControlSignals_T_463; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_465 = _decodedControlSignals_T_63 ? 5'h10 : _decodedControlSignals_T_464; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_466 = _decodedControlSignals_T_61 ? 5'h0 : _decodedControlSignals_T_465; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_467 = _decodedControlSignals_T_59 ? 5'h0 : _decodedControlSignals_T_466; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_468 = _decodedControlSignals_T_57 ? 5'h0 : _decodedControlSignals_T_467; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_469 = _decodedControlSignals_T_55 ? 5'h0 : _decodedControlSignals_T_468; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_470 = _decodedControlSignals_T_53 ? 5'h0 : _decodedControlSignals_T_469; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_471 = _decodedControlSignals_T_51 ? 5'h0 : _decodedControlSignals_T_470; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_472 = _decodedControlSignals_T_49 ? 5'h0 : _decodedControlSignals_T_471; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_473 = _decodedControlSignals_T_47 ? 5'h0 : _decodedControlSignals_T_472; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_474 = _decodedControlSignals_T_45 ? 5'h7 : _decodedControlSignals_T_473; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_475 = _decodedControlSignals_T_43 ? 5'h7 : _decodedControlSignals_T_474; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_476 = _decodedControlSignals_T_41 ? 5'h5 : _decodedControlSignals_T_475; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_477 = _decodedControlSignals_T_39 ? 5'h5 : _decodedControlSignals_T_476; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_478 = _decodedControlSignals_T_37 ? 5'h6 : _decodedControlSignals_T_477; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_479 = _decodedControlSignals_T_35 ? 5'h6 : _decodedControlSignals_T_478; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_480 = _decodedControlSignals_T_33 ? 5'h8 : _decodedControlSignals_T_479; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_481 = _decodedControlSignals_T_31 ? 5'h8 : _decodedControlSignals_T_480; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_482 = _decodedControlSignals_T_29 ? 5'h9 : _decodedControlSignals_T_481; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_483 = _decodedControlSignals_T_27 ? 5'h9 : _decodedControlSignals_T_482; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_484 = _decodedControlSignals_T_25 ? 5'h0 : _decodedControlSignals_T_483; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_485 = _decodedControlSignals_T_23 ? 5'h19 : _decodedControlSignals_T_484; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_486 = _decodedControlSignals_T_21 ? 5'h4 : _decodedControlSignals_T_485; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_487 = _decodedControlSignals_T_19 ? 5'h4 : _decodedControlSignals_T_486; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_488 = _decodedControlSignals_T_17 ? 5'h3 : _decodedControlSignals_T_487; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_489 = _decodedControlSignals_T_15 ? 5'h3 : _decodedControlSignals_T_488; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_490 = _decodedControlSignals_T_13 ? 5'h2 : _decodedControlSignals_T_489; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_491 = _decodedControlSignals_T_11 ? 5'h2 : _decodedControlSignals_T_490; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_492 = _decodedControlSignals_T_9 ? 5'h1 : _decodedControlSignals_T_491; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_493 = _decodedControlSignals_T_7 ? 5'h0 : _decodedControlSignals_T_492; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_494 = _decodedControlSignals_T_5 ? 5'h0 : _decodedControlSignals_T_493; // @[Lookup.scala 34:39]
  wire [4:0] _decodedControlSignals_T_495 = _decodedControlSignals_T_3 ? 5'h0 : _decodedControlSignals_T_494; // @[Lookup.scala 34:39]
  assign io_controlSignals_memToReg = 32'h2003 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  assign io_controlSignals_regWrite = decodedControlSignals_0 | _decodedControlSignals_T_153; // @[Lookup.scala 34:39]
  assign io_controlSignals_memRead = 32'h2003 == _decodedControlSignals_T; // @[Lookup.scala 31:38]
  assign io_controlSignals_memWrite = decodedControlSignals_0 ? 1'h0 : _decodedControlSignals_T_3; // @[Lookup.scala 34:39]
  assign io_branchType = decodedControlSignals_0 ? 3'h7 : _decodedControlSignals_T_343; // @[Lookup.scala 34:39]
  assign io_op1Select = decodedControlSignals_0 ? 1'h0 : _decodedControlSignals_T_381; // @[Lookup.scala 34:39]
  assign io_op2Select = decodedControlSignals_0 | (_decodedControlSignals_T_3 | _decodedControlSignals_T_418); // @[Lookup.scala 34:39]
  assign io_immType = decodedControlSignals_0 ? 3'h0 : _decodedControlSignals_T_457; // @[Lookup.scala 34:39]
  assign io_ALUop = decodedControlSignals_0 ? 5'h0 : _decodedControlSignals_T_495; // @[Lookup.scala 34:39]
endmodule
module ByPassReg(
  input  [31:0] io_readAddr,
  input  [31:0] io_writeAddr,
  input         io_writeEnable,
  input  [31:0] io_registerData,
  input  [31:0] io_writeData,
  output [31:0] io_outData
);
  assign io_outData = io_readAddr != 32'h0 & io_readAddr == io_writeAddr & io_writeEnable ? io_writeData :
    io_registerData; // @[ByPassReg.scala 36:80 37:16 39:16]
endmodule
module ID(
  input         clock,
  input         testHarness_registerSetup_setup,
  input  [4:0]  testHarness_registerSetup_readAddress,
  input         testHarness_registerSetup_writeEnable,
  input  [31:0] testHarness_registerSetup_writeData,
  output [31:0] testHarness_registerPeek,
  output        testHarness_testUpdates_writeEnable,
  output [31:0] testHarness_testUpdates_writeData,
  output [4:0]  testHarness_testUpdates_writeAddress,
  input  [31:0] io_instruction_instruction,
  input  [4:0]  io_registerWriteAddress,
  input  [31:0] io_registerWriteData,
  input         io_registerWriteEnable,
  output        io_controlSignals_memToReg,
  output        io_controlSignals_regWrite,
  output        io_controlSignals_memRead,
  output        io_controlSignals_memWrite,
  output [2:0]  io_branchType,
  output        io_op1Select,
  output        io_op2Select,
  output [2:0]  io_immType,
  output [31:0] io_immData,
  output [4:0]  io_ALUop,
  output [31:0] io_readData1,
  output [31:0] io_readData2
);
  wire  registers_clock; // @[ID.scala 54:25]
  wire  registers_testHarness_setup_setup; // @[ID.scala 54:25]
  wire [4:0] registers_testHarness_setup_readAddress; // @[ID.scala 54:25]
  wire  registers_testHarness_setup_writeEnable; // @[ID.scala 54:25]
  wire [31:0] registers_testHarness_setup_writeData; // @[ID.scala 54:25]
  wire  registers_testHarness_testUpdates_writeEnable; // @[ID.scala 54:25]
  wire [31:0] registers_testHarness_testUpdates_writeData; // @[ID.scala 54:25]
  wire [4:0] registers_testHarness_testUpdates_writeAddress; // @[ID.scala 54:25]
  wire [4:0] registers_io_readAddress1; // @[ID.scala 54:25]
  wire [4:0] registers_io_readAddress2; // @[ID.scala 54:25]
  wire  registers_io_writeEnable; // @[ID.scala 54:25]
  wire [4:0] registers_io_writeAddress; // @[ID.scala 54:25]
  wire [31:0] registers_io_writeData; // @[ID.scala 54:25]
  wire [31:0] registers_io_readData1; // @[ID.scala 54:25]
  wire [31:0] registers_io_readData2; // @[ID.scala 54:25]
  wire [31:0] Decode_io_instruction_instruction; // @[ID.scala 55:25]
  wire  Decode_io_controlSignals_memToReg; // @[ID.scala 55:25]
  wire  Decode_io_controlSignals_regWrite; // @[ID.scala 55:25]
  wire  Decode_io_controlSignals_memRead; // @[ID.scala 55:25]
  wire  Decode_io_controlSignals_memWrite; // @[ID.scala 55:25]
  wire [2:0] Decode_io_branchType; // @[ID.scala 55:25]
  wire  Decode_io_op1Select; // @[ID.scala 55:25]
  wire  Decode_io_op2Select; // @[ID.scala 55:25]
  wire [2:0] Decode_io_immType; // @[ID.scala 55:25]
  wire [4:0] Decode_io_ALUop; // @[ID.scala 55:25]
  wire [31:0] ByPassReg_io_readAddr; // @[ID.scala 56:25]
  wire [31:0] ByPassReg_io_writeAddr; // @[ID.scala 56:25]
  wire  ByPassReg_io_writeEnable; // @[ID.scala 56:25]
  wire [31:0] ByPassReg_io_registerData; // @[ID.scala 56:25]
  wire [31:0] ByPassReg_io_writeData; // @[ID.scala 56:25]
  wire [31:0] ByPassReg_io_outData; // @[ID.scala 56:25]
  wire [31:0] ByPassReg_1_io_readAddr; // @[ID.scala 57:25]
  wire [31:0] ByPassReg_1_io_writeAddr; // @[ID.scala 57:25]
  wire  ByPassReg_1_io_writeEnable; // @[ID.scala 57:25]
  wire [31:0] ByPassReg_1_io_registerData; // @[ID.scala 57:25]
  wire [31:0] ByPassReg_1_io_writeData; // @[ID.scala 57:25]
  wire [31:0] ByPassReg_1_io_outData; // @[ID.scala 57:25]
  wire [11:0] _T_3 = io_instruction_instruction[31:20]; // @[configuraton.scala 103:44]
  wire [11:0] _T_7 = {io_instruction_instruction[31:25],io_instruction_instruction[11:7]}; // @[configuraton.scala 104:68]
  wire [12:0] _T_13 = {io_instruction_instruction[31],io_instruction_instruction[7],io_instruction_instruction[30:25],
    io_instruction_instruction[11:8],1'h0}; // @[configuraton.scala 105:112]
  wire [31:0] _T_16 = {io_instruction_instruction[31:12],12'h0}; // @[configuraton.scala 106:60]
  wire [20:0] _T_23 = {io_instruction_instruction[31],io_instruction_instruction[19:12],io_instruction_instruction[20],
    io_instruction_instruction[30:25],io_instruction_instruction[24:21],1'h0}; // @[configuraton.scala 108:135]
  wire [5:0] _T_25 = {1'b0,$signed(io_instruction_instruction[19:15])}; // @[configuraton.scala 109:44]
  wire [31:0] _immData_T_1 = 3'h0 == io_immType ? $signed({{20{_T_3[11]}},_T_3}) : $signed(32'sh0); // @[Mux.scala 81:58]
  wire [31:0] _immData_T_3 = 3'h1 == io_immType ? $signed({{20{_T_7[11]}},_T_7}) : $signed(_immData_T_1); // @[Mux.scala 81:58]
  wire [31:0] _immData_T_5 = 3'h2 == io_immType ? $signed({{19{_T_13[12]}},_T_13}) : $signed(_immData_T_3); // @[Mux.scala 81:58]
  wire [31:0] _immData_T_7 = 3'h3 == io_immType ? $signed(_T_16) : $signed(_immData_T_5); // @[Mux.scala 81:58]
  wire [31:0] _immData_T_9 = 3'h4 == io_immType ? $signed({{11{_T_23[20]}},_T_23}) : $signed(_immData_T_7); // @[Mux.scala 81:58]
  wire [31:0] _immData_T_11 = 3'h5 == io_immType ? $signed({{26{_T_25[5]}},_T_25}) : $signed(_immData_T_9); // @[Mux.scala 81:58]
  wire [31:0] immData = 3'h0 == io_immType ? $signed({{20{_T_3[11]}},_T_3}) : $signed(_immData_T_11); // @[Mux.scala 81:58]
  wire [31:0] _io_immData_T = 3'h0 == io_immType ? $signed({{20{_T_3[11]}},_T_3}) : $signed(_immData_T_11); // @[ID.scala 131:29]
  wire [15:0] _io_immData_T_3 = immData[15] ? 16'hffff : 16'h0; // @[Bitwise.scala 74:12]
  wire [31:0] _io_immData_T_5 = {_io_immData_T_3,immData[15:0]}; // @[Cat.scala 31:58]
  registerFile registers ( // @[ID.scala 54:25]
    .clock(registers_clock),
    .testHarness_setup_setup(registers_testHarness_setup_setup),
    .testHarness_setup_readAddress(registers_testHarness_setup_readAddress),
    .testHarness_setup_writeEnable(registers_testHarness_setup_writeEnable),
    .testHarness_setup_writeData(registers_testHarness_setup_writeData),
    .testHarness_testUpdates_writeEnable(registers_testHarness_testUpdates_writeEnable),
    .testHarness_testUpdates_writeData(registers_testHarness_testUpdates_writeData),
    .testHarness_testUpdates_writeAddress(registers_testHarness_testUpdates_writeAddress),
    .io_readAddress1(registers_io_readAddress1),
    .io_readAddress2(registers_io_readAddress2),
    .io_writeEnable(registers_io_writeEnable),
    .io_writeAddress(registers_io_writeAddress),
    .io_writeData(registers_io_writeData),
    .io_readData1(registers_io_readData1),
    .io_readData2(registers_io_readData2)
  );
  Decode Decode ( // @[ID.scala 55:25]
    .io_instruction_instruction(Decode_io_instruction_instruction),
    .io_controlSignals_memToReg(Decode_io_controlSignals_memToReg),
    .io_controlSignals_regWrite(Decode_io_controlSignals_regWrite),
    .io_controlSignals_memRead(Decode_io_controlSignals_memRead),
    .io_controlSignals_memWrite(Decode_io_controlSignals_memWrite),
    .io_branchType(Decode_io_branchType),
    .io_op1Select(Decode_io_op1Select),
    .io_op2Select(Decode_io_op2Select),
    .io_immType(Decode_io_immType),
    .io_ALUop(Decode_io_ALUop)
  );
  ByPassReg ByPassReg ( // @[ID.scala 56:25]
    .io_readAddr(ByPassReg_io_readAddr),
    .io_writeAddr(ByPassReg_io_writeAddr),
    .io_writeEnable(ByPassReg_io_writeEnable),
    .io_registerData(ByPassReg_io_registerData),
    .io_writeData(ByPassReg_io_writeData),
    .io_outData(ByPassReg_io_outData)
  );
  ByPassReg ByPassReg_1 ( // @[ID.scala 57:25]
    .io_readAddr(ByPassReg_1_io_readAddr),
    .io_writeAddr(ByPassReg_1_io_writeAddr),
    .io_writeEnable(ByPassReg_1_io_writeEnable),
    .io_registerData(ByPassReg_1_io_registerData),
    .io_writeData(ByPassReg_1_io_writeData),
    .io_outData(ByPassReg_1_io_outData)
  );
  assign testHarness_registerPeek = registers_io_readData1; // @[ID.scala 62:31]
  assign testHarness_testUpdates_writeEnable = registers_testHarness_testUpdates_writeEnable; // @[ID.scala 63:31]
  assign testHarness_testUpdates_writeData = registers_testHarness_testUpdates_writeData; // @[ID.scala 63:31]
  assign testHarness_testUpdates_writeAddress = registers_testHarness_testUpdates_writeAddress; // @[ID.scala 63:31]
  assign io_controlSignals_memToReg = Decode_io_controlSignals_memToReg; // @[ID.scala 74:21]
  assign io_controlSignals_regWrite = Decode_io_controlSignals_regWrite; // @[ID.scala 74:21]
  assign io_controlSignals_memRead = Decode_io_controlSignals_memRead; // @[ID.scala 74:21]
  assign io_controlSignals_memWrite = Decode_io_controlSignals_memWrite; // @[ID.scala 74:21]
  assign io_branchType = Decode_io_branchType; // @[ID.scala 75:21]
  assign io_op1Select = Decode_io_op1Select; // @[ID.scala 76:21]
  assign io_op2Select = Decode_io_op2Select; // @[ID.scala 77:21]
  assign io_immType = Decode_io_immType; // @[ID.scala 78:21]
  assign io_immData = Decode_io_ALUop == 5'h19 ? _io_immData_T : _io_immData_T_5; // @[ID.scala 129:37 131:18 134:18]
  assign io_ALUop = Decode_io_ALUop == 5'h19 ? 5'h0 : Decode_io_ALUop; // @[ID.scala 129:37 132:18 79:21]
  assign io_readData1 = Decode_io_ALUop == 5'h19 ? 32'h0 : ByPassReg_io_outData; // @[ID.scala 129:37 130:20 98:26]
  assign io_readData2 = ByPassReg_1_io_outData; // @[ID.scala 105:26]
  assign registers_clock = clock;
  assign registers_testHarness_setup_setup = testHarness_registerSetup_setup; // @[ID.scala 61:31]
  assign registers_testHarness_setup_readAddress = testHarness_registerSetup_readAddress; // @[ID.scala 61:31]
  assign registers_testHarness_setup_writeEnable = testHarness_registerSetup_writeEnable; // @[ID.scala 61:31]
  assign registers_testHarness_setup_writeData = testHarness_registerSetup_writeData; // @[ID.scala 61:31]
  assign registers_io_readAddress1 = io_instruction_instruction[19:15]; // @[configuraton.scala 99:32]
  assign registers_io_readAddress2 = io_instruction_instruction[24:20]; // @[configuraton.scala 100:32]
  assign registers_io_writeEnable = io_registerWriteEnable; // @[ID.scala 86:29]
  assign registers_io_writeAddress = io_registerWriteAddress; // @[ID.scala 87:29]
  assign registers_io_writeData = io_registerWriteData; // @[ID.scala 88:29]
  assign Decode_io_instruction_instruction = io_instruction_instruction; // @[ID.scala 71:23]
  assign ByPassReg_io_readAddr = {{27'd0}, io_instruction_instruction[19:15]}; // @[ID.scala 93:26]
  assign ByPassReg_io_writeAddr = {{27'd0}, io_registerWriteAddress}; // @[ID.scala 94:26]
  assign ByPassReg_io_writeEnable = io_registerWriteEnable; // @[ID.scala 95:26]
  assign ByPassReg_io_registerData = registers_io_readData1; // @[ID.scala 96:26]
  assign ByPassReg_io_writeData = io_registerWriteData; // @[ID.scala 97:26]
  assign ByPassReg_1_io_readAddr = {{27'd0}, io_instruction_instruction[24:20]}; // @[ID.scala 100:26]
  assign ByPassReg_1_io_writeAddr = {{27'd0}, io_registerWriteAddress}; // @[ID.scala 101:26]
  assign ByPassReg_1_io_writeEnable = io_registerWriteEnable; // @[ID.scala 102:26]
  assign ByPassReg_1_io_registerData = registers_io_readData2; // @[ID.scala 103:26]
  assign ByPassReg_1_io_writeData = io_registerWriteData; // @[ID.scala 104:26]
endmodule
module ALU(
  input  [31:0] io_src1,
  input  [31:0] io_src2,
  input  [31:0] io_ALUop,
  output [31:0] io_aluRes
);
  wire  _T_2 = $signed(io_src1) < $signed(io_src2); // @[ALU.scala 32:24]
  wire  _T_3 = io_src1 < io_src2; // @[ALU.scala 39:17]
  wire [4:0] shamt = io_src2[4:0]; // @[ALU.scala 46:22]
  wire [31:0] _io_aluRes_T_1 = io_src1 + io_src2; // @[ALU.scala 51:36]
  wire [31:0] _io_aluRes_T_3 = io_src1 - io_src2; // @[ALU.scala 52:36]
  wire [62:0] _GEN_0 = {{31'd0}, io_src1}; // @[ALU.scala 54:36]
  wire [62:0] _io_aluRes_T_4 = _GEN_0 << shamt; // @[ALU.scala 54:36]
  wire [31:0] _io_aluRes_T_5 = io_src1 >> shamt; // @[ALU.scala 55:36]
  wire [31:0] _io_aluRes_T_8 = io_src1[31] ? 32'hffffffff : 32'h0; // @[Bitwise.scala 74:12]
  wire [4:0] _io_aluRes_T_10 = shamt - 5'h1; // @[ALU.scala 56:72]
  wire [4:0] _io_aluRes_T_12 = 5'h1f - _io_aluRes_T_10; // @[ALU.scala 56:63]
  wire [62:0] _GEN_1 = {{31'd0}, _io_aluRes_T_8}; // @[ALU.scala 56:49]
  wire [62:0] _io_aluRes_T_13 = _GEN_1 << _io_aluRes_T_12; // @[ALU.scala 56:49]
  wire [62:0] _GEN_15 = {{31'd0}, _io_aluRes_T_5}; // @[ALU.scala 56:86]
  wire [62:0] _io_aluRes_T_15 = _io_aluRes_T_13 | _GEN_15; // @[ALU.scala 56:86]
  wire [31:0] _io_aluRes_T_16 = io_src1 | io_src2; // @[ALU.scala 58:34]
  wire [31:0] _io_aluRes_T_17 = io_src1 & io_src2; // @[ALU.scala 59:35]
  wire [31:0] _io_aluRes_T_18 = io_src1 ^ io_src2; // @[ALU.scala 60:35]
  wire [31:0] _io_aluRes_T_20 = io_src1 + 32'h4; // @[ALU.scala 65:37]
  wire [31:0] _GEN_2 = 32'hf == io_ALUop ? _io_aluRes_T_3 : 32'h0; // @[ALU.scala 47:13 50:19 67:23]
  wire [31:0] _GEN_3 = 32'hb == io_ALUop ? io_src2 : _GEN_2; // @[ALU.scala 50:19 66:27]
  wire [31:0] _GEN_4 = 32'ha == io_ALUop ? _io_aluRes_T_20 : _GEN_3; // @[ALU.scala 50:19 65:26]
  wire [31:0] ALU_SLTU = {{31'd0}, _T_3}; // @[ALU.scala 29:22]
  wire [31:0] _GEN_5 = 32'h7 == io_ALUop ? ALU_SLTU : _GEN_4; // @[ALU.scala 50:19 63:25]
  wire [31:0] ALU_SLT = {{31'd0}, _T_2}; // @[ALU.scala 28:22]
  wire [31:0] _GEN_6 = 32'h5 == io_ALUop ? ALU_SLT : _GEN_5; // @[ALU.scala 50:19 62:24]
  wire [31:0] _GEN_7 = 32'h4 == io_ALUop ? _io_aluRes_T_18 : _GEN_6; // @[ALU.scala 50:19 60:24]
  wire [31:0] _GEN_8 = 32'h2 == io_ALUop ? _io_aluRes_T_17 : _GEN_7; // @[ALU.scala 50:19 59:24]
  wire [31:0] _GEN_9 = 32'h3 == io_ALUop ? _io_aluRes_T_16 : _GEN_8; // @[ALU.scala 50:19 58:23]
  wire [62:0] _GEN_10 = 32'h9 == io_ALUop ? _io_aluRes_T_15 : {{31'd0}, _GEN_9}; // @[ALU.scala 50:19 56:24]
  wire [62:0] _GEN_11 = 32'h8 == io_ALUop ? {{31'd0}, _io_aluRes_T_5} : _GEN_10; // @[ALU.scala 50:19 55:24]
  wire [62:0] _GEN_12 = 32'h6 == io_ALUop ? _io_aluRes_T_4 : _GEN_11; // @[ALU.scala 50:19 54:24]
  wire [62:0] _GEN_13 = 32'h1 == io_ALUop ? {{31'd0}, _io_aluRes_T_3} : _GEN_12; // @[ALU.scala 50:19 52:24]
  wire [62:0] _GEN_14 = 32'h0 == io_ALUop ? {{31'd0}, _io_aluRes_T_1} : _GEN_13; // @[ALU.scala 50:19 51:24]
  assign io_aluRes = _GEN_14[31:0];
endmodule
module MDU(
  input  [31:0] io_src1,
  input  [31:0] io_src2,
  input  [31:0] io_MDUop,
  output [31:0] io_MDURes,
  output        io_MDUopflag
);
  wire [63:0] _io_MDURes_T_1 = $signed(io_src1) * $signed(io_src2); // @[MDU.scala 41:30]
  wire  _T_2 = io_src2 == 32'h0; // @[MDU.scala 44:18]
  wire  _T_5 = io_src1 == 32'h80000000 & io_src2 == 32'hffffffff; // @[MDU.scala 47:42]
  wire [32:0] _io_MDURes_T_3 = $signed(io_src1) / $signed(io_src2); // @[MDU.scala 50:32]
  wire [32:0] _GEN_0 = io_src1 == 32'h80000000 & io_src2 == 32'hffffffff ? 33'h80000000 : _io_MDURes_T_3; // @[MDU.scala 47:71 48:17 50:17]
  wire [32:0] _GEN_2 = io_src2 == 32'h0 ? 33'hffffffff : _GEN_0; // @[MDU.scala 44:26 46:17]
  wire [31:0] _io_MDURes_T_4 = io_src1 / io_src2; // @[MDU.scala 58:28]
  wire [31:0] _GEN_4 = _T_2 ? 32'hfffffffe : _io_MDURes_T_4; // @[MDU.scala 54:26 56:17 58:17]
  wire [31:0] _io_MDURes_T_6 = $signed(io_src1) % $signed(io_src2); // @[MDU.scala 68:32]
  wire [31:0] _GEN_5 = _T_5 ? 32'h0 : _io_MDURes_T_6; // @[MDU.scala 65:71 66:17 68:17]
  wire [31:0] _GEN_7 = _T_2 ? io_src1 : _GEN_5; // @[MDU.scala 62:26 64:17]
  wire [31:0] _GEN_1 = io_src1 % io_src2; // @[MDU.scala 76:26]
  wire [31:0] _io_MDURes_T_7 = _GEN_1[31:0]; // @[MDU.scala 76:26]
  wire [31:0] _GEN_9 = _T_2 ? io_src1 : _io_MDURes_T_7; // @[MDU.scala 72:26 74:17 76:15]
  wire [63:0] _io_MDURes_T_10 = {{32'd0}, _io_MDURes_T_1[63:32]}; // @[MDU.scala 81:38]
  wire [63:0] _io_MDURes_T_11 = io_src1 * io_src2; // @[MDU.scala 84:28]
  wire [63:0] _io_MDURes_T_12 = {{32'd0}, _io_MDURes_T_11[63:32]}; // @[MDU.scala 84:39]
  wire  _T_17 = io_MDUop == 32'h14; // @[MDU.scala 86:23]
  wire [31:0] _temp_T = ~io_src1; // @[MDU.scala 88:20]
  wire [31:0] temp = _temp_T + 32'h1; // @[MDU.scala 88:29]
  wire [63:0] temp2 = temp * io_src2; // @[MDU.scala 89:25]
  wire [63:0] _io_MDURes_T_13 = ~temp2; // @[MDU.scala 90:23]
  wire [63:0] _io_MDURes_T_15 = _io_MDURes_T_13 + 64'h1; // @[MDU.scala 90:30]
  wire [63:0] _io_MDURes_T_16 = {{32'd0}, _io_MDURes_T_15[63:32]}; // @[MDU.scala 90:36]
  wire [63:0] _GEN_10 = io_src1[31] ? _io_MDURes_T_16 : _io_MDURes_T_12; // @[MDU.scala 87:30 90:18 93:18]
  wire [63:0] _GEN_11 = io_MDUop == 32'h14 ? _GEN_10 : 64'h0; // @[MDU.scala 31:23 86:34]
  wire [63:0] _GEN_13 = io_MDUop == 32'h15 ? _io_MDURes_T_12 : _GEN_11; // @[MDU.scala 83:33 84:15]
  wire  _GEN_14 = io_MDUop == 32'h15 | _T_17; // @[MDU.scala 32:23 83:33]
  wire [63:0] _GEN_15 = io_MDUop == 32'h13 ? _io_MDURes_T_10 : _GEN_13; // @[MDU.scala 80:32 81:15]
  wire  _GEN_16 = io_MDUop == 32'h13 | _GEN_14; // @[MDU.scala 32:23 80:32]
  wire [63:0] _GEN_18 = io_MDUop == 32'h17 ? {{32'd0}, _GEN_9} : _GEN_15; // @[MDU.scala 71:32]
  wire  _GEN_19 = io_MDUop == 32'h17 | _GEN_16; // @[MDU.scala 32:23 71:32]
  wire [63:0] _GEN_21 = io_MDUop == 32'h12 ? {{32'd0}, _GEN_7} : _GEN_18; // @[MDU.scala 61:31]
  wire  _GEN_22 = io_MDUop == 32'h12 | _GEN_19; // @[MDU.scala 32:23 61:31]
  wire [63:0] _GEN_24 = io_MDUop == 32'h16 ? {{32'd0}, _GEN_4} : _GEN_21; // @[MDU.scala 53:31]
  wire  _GEN_25 = io_MDUop == 32'h16 | _GEN_22; // @[MDU.scala 32:23 53:31]
  wire [63:0] _GEN_27 = io_MDUop == 32'h11 ? {{31'd0}, _GEN_2} : _GEN_24; // @[MDU.scala 43:31]
  wire  _GEN_28 = io_MDUop == 32'h11 | _GEN_25; // @[MDU.scala 32:23 43:31]
  wire [63:0] _GEN_29 = io_MDUop == 32'h10 ? _io_MDURes_T_1 : _GEN_27; // @[MDU.scala 40:26 41:15]
  assign io_MDURes = _GEN_29[31:0];
  assign io_MDUopflag = io_MDUop == 32'h10 | _GEN_28; // @[MDU.scala 32:23 40:26]
endmodule
module Branch_OP(
  input  [31:0] io_branchType,
  input  [31:0] io_src1,
  input  [31:0] io_src2,
  output [31:0] io_branchTaken
);
  wire  _io_branchTaken_T_2 = $signed(io_src1) >= $signed(io_src2); // @[Branch_OP.scala 39:30]
  wire  _io_branchTaken_T_3 = $signed(io_src1) < $signed(io_src2); // @[Branch_OP.scala 42:30]
  wire  _GEN_2 = 32'h5 == io_branchType ? _io_branchTaken_T_3 : 32'h6 == io_branchType; // @[Branch_OP.scala 31:25 48:22]
  wire  _GEN_3 = 32'h4 == io_branchType ? _io_branchTaken_T_2 : _GEN_2; // @[Branch_OP.scala 31:25 45:22]
  wire  _GEN_4 = 32'h3 == io_branchType ? $signed(io_src1) < $signed(io_src2) : _GEN_3; // @[Branch_OP.scala 31:25 42:22]
  wire  _GEN_5 = 32'h2 == io_branchType ? $signed(io_src1) >= $signed(io_src2) : _GEN_4; // @[Branch_OP.scala 31:25 39:22]
  wire  _GEN_6 = 32'h1 == io_branchType ? $signed(io_src1) != $signed(io_src2) : _GEN_5; // @[Branch_OP.scala 31:25 36:22]
  wire  _GEN_7 = 32'h0 == io_branchType ? $signed(io_src1) == $signed(io_src2) : _GEN_6; // @[Branch_OP.scala 31:25 33:22]
  assign io_branchTaken = {{31'd0}, _GEN_7};
endmodule
module EX(
  input  [31:0] io_PC,
  input  [2:0]  io_branchType,
  input         io_op1Select,
  input         io_op2Select,
  input  [31:0] io_rs1,
  input  [31:0] io_rs2,
  input  [31:0] io_immData,
  input  [4:0]  io_ALUop,
  input  [1:0]  io_rs1Select,
  input  [1:0]  io_rs2Select,
  input  [31:0] io_ALUresultEXB,
  input  [31:0] io_ALUresultMEMB,
  input         io_btbHit,
  input  [31:0] io_btbTargetPredict,
  output        io_newBranch,
  output        io_updatePrediction,
  output [31:0] io_outPCplus4,
  output [31:0] io_ALUResult,
  output [31:0] io_branchTarget,
  output        io_branchTaken,
  output        io_wrongAddrPred,
  output [31:0] io_Rs2Forwarded
);
  wire [31:0] ALU_io_src1; // @[EX.scala 55:35]
  wire [31:0] ALU_io_src2; // @[EX.scala 55:35]
  wire [31:0] ALU_io_ALUop; // @[EX.scala 55:35]
  wire [31:0] ALU_io_aluRes; // @[EX.scala 55:35]
  wire [31:0] MDU_io_src1; // @[EX.scala 56:35]
  wire [31:0] MDU_io_src2; // @[EX.scala 56:35]
  wire [31:0] MDU_io_MDUop; // @[EX.scala 56:35]
  wire [31:0] MDU_io_MDURes; // @[EX.scala 56:35]
  wire  MDU_io_MDUopflag; // @[EX.scala 56:35]
  wire [31:0] Branch_OP_io_branchType; // @[EX.scala 57:35]
  wire [31:0] Branch_OP_io_src1; // @[EX.scala 57:35]
  wire [31:0] Branch_OP_io_src2; // @[EX.scala 57:35]
  wire [31:0] Branch_OP_io_branchTaken; // @[EX.scala 57:35]
  wire [31:0] _GEN_0 = io_rs1Select == 2'h2 ? io_ALUresultMEMB : io_rs1; // @[EX.scala 76:44 77:26 81:26]
  wire [31:0] alu_operand1 = io_rs1Select == 2'h1 ? io_ALUresultEXB : _GEN_0; // @[EX.scala 72:39 73:26]
  wire [31:0] _GEN_2 = io_rs2Select == 2'h2 ? io_ALUresultMEMB : io_rs2; // @[EX.scala 88:44 89:26 93:26]
  wire [31:0] alu_operand2 = io_rs2Select == 2'h1 ? io_ALUresultEXB : _GEN_2; // @[EX.scala 84:39 85:26]
  wire  _io_wrongAddrPred_T_1 = io_btbHit & ALU_io_aluRes != io_btbTargetPredict; // @[EX.scala 120:33]
  wire [31:0] PCplus4 = io_PC + 32'h4; // @[EX.scala 128:20]
  wire  mdu_op_flag = MDU_io_MDUopflag; // @[EX.scala 114:22 60:37]
  wire [31:0] _io_ALUResult_T = mdu_op_flag ? MDU_io_MDURes : ALU_io_aluRes; // @[EX.scala 132:24]
  wire  _T_11 = ~io_btbHit | _io_wrongAddrPred_T_1; // @[EX.scala 137:21]
  wire  _GEN_8 = ~io_btbHit | _io_wrongAddrPred_T_1 ? 1'h0 : 1'h1; // @[EX.scala 137:76 139:27 142:27]
  ALU ALU ( // @[EX.scala 55:35]
    .io_src1(ALU_io_src1),
    .io_src2(ALU_io_src2),
    .io_ALUop(ALU_io_ALUop),
    .io_aluRes(ALU_io_aluRes)
  );
  MDU MDU ( // @[EX.scala 56:35]
    .io_src1(MDU_io_src1),
    .io_src2(MDU_io_src2),
    .io_MDUop(MDU_io_MDUop),
    .io_MDURes(MDU_io_MDURes),
    .io_MDUopflag(MDU_io_MDUopflag)
  );
  Branch_OP Branch_OP ( // @[EX.scala 57:35]
    .io_branchType(Branch_OP_io_branchType),
    .io_src1(Branch_OP_io_src1),
    .io_src2(Branch_OP_io_src2),
    .io_branchTaken(Branch_OP_io_branchTaken)
  );
  assign io_newBranch = io_branchType != 3'h7 & _T_11; // @[EX.scala 136:42 145:25]
  assign io_updatePrediction = io_branchType != 3'h7 & _GEN_8; // @[EX.scala 136:42 146:25]
  assign io_outPCplus4 = io_PC + 32'h4; // @[EX.scala 128:20]
  assign io_ALUResult = io_branchType == 3'h6 ? PCplus4 : _io_ALUResult_T; // @[EX.scala 129:44 130:18 132:18]
  assign io_branchTarget = ALU_io_aluRes; // @[EX.scala 121:20]
  assign io_branchTaken = Branch_OP_io_branchTaken[0]; // @[EX.scala 119:20]
  assign io_wrongAddrPred = io_btbHit & ALU_io_aluRes != io_btbTargetPredict; // @[EX.scala 120:33]
  assign io_Rs2Forwarded = io_rs2Select == 2'h1 ? io_ALUresultEXB : _GEN_2; // @[EX.scala 84:39 85:26]
  assign ALU_io_src1 = io_op1Select ? io_PC : alu_operand1; // @[EX.scala 100:17 97:35 98:17]
  assign ALU_io_src2 = ~io_op2Select ? alu_operand2 : io_immData; // @[EX.scala 103:36 104:17 106:17]
  assign ALU_io_ALUop = {{27'd0}, io_ALUop}; // @[EX.scala 68:28]
  assign MDU_io_src1 = io_rs1Select == 2'h1 ? io_ALUresultEXB : _GEN_0; // @[EX.scala 72:39 73:26]
  assign MDU_io_src2 = io_rs2Select == 2'h1 ? io_ALUresultEXB : _GEN_2; // @[EX.scala 84:39 85:26]
  assign MDU_io_MDUop = {{27'd0}, io_ALUop}; // @[EX.scala 113:22]
  assign Branch_OP_io_branchType = {{29'd0}, io_branchType}; // @[EX.scala 67:28]
  assign Branch_OP_io_src1 = io_rs1Select == 2'h1 ? io_ALUresultEXB : _GEN_0; // @[EX.scala 72:39 73:26]
  assign Branch_OP_io_src2 = io_rs2Select == 2'h1 ? io_ALUresultEXB : _GEN_2; // @[EX.scala 84:39 85:26]
endmodule
module DataMemory(
  input         clock,
  input         io_writeEnable,
  input  [31:0] io_dataIn,
  input  [31:0] io_dataAddress,
  output [31:0] io_dataOut
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] d_memory [0:1048575]; // @[DataMemory.scala 48:29]
  wire  d_memory_io_dataOut_MPORT_en; // @[DataMemory.scala 48:29]
  wire [19:0] d_memory_io_dataOut_MPORT_addr; // @[DataMemory.scala 48:29]
  wire [31:0] d_memory_io_dataOut_MPORT_data; // @[DataMemory.scala 48:29]
  wire [31:0] d_memory_MPORT_data; // @[DataMemory.scala 48:29]
  wire [19:0] d_memory_MPORT_addr; // @[DataMemory.scala 48:29]
  wire  d_memory_MPORT_mask; // @[DataMemory.scala 48:29]
  wire  d_memory_MPORT_en; // @[DataMemory.scala 48:29]
  reg  d_memory_io_dataOut_MPORT_en_pipe_0;
  reg [19:0] d_memory_io_dataOut_MPORT_addr_pipe_0;
  assign d_memory_io_dataOut_MPORT_en = d_memory_io_dataOut_MPORT_en_pipe_0;
  assign d_memory_io_dataOut_MPORT_addr = d_memory_io_dataOut_MPORT_addr_pipe_0;
  assign d_memory_io_dataOut_MPORT_data = d_memory[d_memory_io_dataOut_MPORT_addr]; // @[DataMemory.scala 48:29]
  assign d_memory_MPORT_data = io_dataIn;
  assign d_memory_MPORT_addr = io_dataAddress[19:0];
  assign d_memory_MPORT_mask = 1'h1;
  assign d_memory_MPORT_en = io_writeEnable;
  assign io_dataOut = d_memory_io_dataOut_MPORT_data; // @[DataMemory.scala 80:14]
  always @(posedge clock) begin
    if (d_memory_MPORT_en & d_memory_MPORT_mask) begin
      d_memory[d_memory_MPORT_addr] <= d_memory_MPORT_data; // @[DataMemory.scala 48:29]
    end
    d_memory_io_dataOut_MPORT_en_pipe_0 <= 1'h1;
    if (1'h1) begin
      d_memory_io_dataOut_MPORT_addr_pipe_0 <= io_dataAddress[19:0];
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
  integer initvar;
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  d_memory_io_dataOut_MPORT_en_pipe_0 = _RAND_0[0:0];
  _RAND_1 = {1{`RANDOM}};
  d_memory_io_dataOut_MPORT_addr_pipe_0 = _RAND_1[19:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
  initial begin
    $readmemh("src/main/scala/DataMemory/dataMemVals", d_memory);
  end
endmodule
module DCache(
  input         clock,
  input         reset,
  input         io_write_en,
  input         io_read_en,
  input  [31:0] io_data_addr,
  input  [31:0] io_data_in,
  output [31:0] io_data_out,
  output        io_busy,
  output        io_mem_write_en,
  output [31:0] io_mem_data_in,
  output [31:0] io_mem_data_addr,
  input  [31:0] io_mem_data_out
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
  reg [63:0] _RAND_6;
  reg [31:0] _RAND_7;
`endif // RANDOMIZE_REG_INIT
  reg [57:0] cache_data_array [0:63]; // @[DCache.scala 59:29]
  wire  cache_data_array_data_element_wire_MPORT_en; // @[DCache.scala 59:29]
  wire [5:0] cache_data_array_data_element_wire_MPORT_addr; // @[DCache.scala 59:29]
  wire [57:0] cache_data_array_data_element_wire_MPORT_data; // @[DCache.scala 59:29]
  wire [57:0] cache_data_array_MPORT_data; // @[DCache.scala 59:29]
  wire [5:0] cache_data_array_MPORT_addr; // @[DCache.scala 59:29]
  wire  cache_data_array_MPORT_mask; // @[DCache.scala 59:29]
  wire  cache_data_array_MPORT_en; // @[DCache.scala 59:29]
  wire [57:0] cache_data_array_MPORT_1_data; // @[DCache.scala 59:29]
  wire [5:0] cache_data_array_MPORT_1_addr; // @[DCache.scala 59:29]
  wire  cache_data_array_MPORT_1_mask; // @[DCache.scala 59:29]
  wire  cache_data_array_MPORT_1_en; // @[DCache.scala 59:29]
  reg  write_en_reg; // @[DCache.scala 39:25]
  reg  read_en_reg; // @[DCache.scala 40:24]
  reg [31:0] data_addr_reg; // @[DCache.scala 41:26]
  reg [31:0] data_in_reg; // @[DCache.scala 42:24]
  reg [1:0] stateReg; // @[DCache.scala 53:25]
  reg [5:0] index; // @[DCache.scala 54:18]
  reg [57:0] data_element; // @[DCache.scala 55:25]
  reg  statecount; // @[DCache.scala 57:23]
  wire  _T = 2'h0 == stateReg; // @[DCache.scala 71:20]
  wire  _T_2 = 2'h1 == stateReg; // @[DCache.scala 71:20]
  wire [31:0] _index_T = data_addr_reg / 3'h4; // @[DCache.scala 89:31]
  wire [31:0] _GEN_0 = _index_T % 32'h40; // @[DCache.scala 89:38]
  wire [6:0] _index_T_1 = _GEN_0[6:0]; // @[DCache.scala 89:38]
  wire [57:0] _GEN_60 = 2'h1 == stateReg ? cache_data_array_data_element_wire_MPORT_data : 58'h0; // @[DCache.scala 71:20 90:25]
  wire [57:0] data_element_wire = 2'h0 == stateReg ? 58'h0 : _GEN_60; // @[DCache.scala 71:20]
  wire  temp__0 = data_in_reg[0]; // @[DCache.scala 107:57]
  wire  temp__1 = data_in_reg[1]; // @[DCache.scala 107:57]
  wire  temp__2 = data_in_reg[2]; // @[DCache.scala 107:57]
  wire  temp__3 = data_in_reg[3]; // @[DCache.scala 107:57]
  wire  temp__4 = data_in_reg[4]; // @[DCache.scala 107:57]
  wire  temp__5 = data_in_reg[5]; // @[DCache.scala 107:57]
  wire  temp__6 = data_in_reg[6]; // @[DCache.scala 107:57]
  wire  temp__7 = data_in_reg[7]; // @[DCache.scala 107:57]
  wire  temp__8 = data_in_reg[8]; // @[DCache.scala 107:57]
  wire  temp__9 = data_in_reg[9]; // @[DCache.scala 107:57]
  wire  temp__10 = data_in_reg[10]; // @[DCache.scala 107:57]
  wire  temp__11 = data_in_reg[11]; // @[DCache.scala 107:57]
  wire  temp__12 = data_in_reg[12]; // @[DCache.scala 107:57]
  wire  temp__13 = data_in_reg[13]; // @[DCache.scala 107:57]
  wire  temp__14 = data_in_reg[14]; // @[DCache.scala 107:57]
  wire  temp__15 = data_in_reg[15]; // @[DCache.scala 107:57]
  wire  temp__16 = data_in_reg[16]; // @[DCache.scala 107:57]
  wire  temp__17 = data_in_reg[17]; // @[DCache.scala 107:57]
  wire  temp__18 = data_in_reg[18]; // @[DCache.scala 107:57]
  wire  temp__19 = data_in_reg[19]; // @[DCache.scala 107:57]
  wire  temp__20 = data_in_reg[20]; // @[DCache.scala 107:57]
  wire  temp__21 = data_in_reg[21]; // @[DCache.scala 107:57]
  wire  temp__22 = data_in_reg[22]; // @[DCache.scala 107:57]
  wire  temp__23 = data_in_reg[23]; // @[DCache.scala 107:57]
  wire  temp__24 = data_in_reg[24]; // @[DCache.scala 107:57]
  wire  temp__25 = data_in_reg[25]; // @[DCache.scala 107:57]
  wire  temp__26 = data_in_reg[26]; // @[DCache.scala 107:57]
  wire  temp__27 = data_in_reg[27]; // @[DCache.scala 107:57]
  wire  temp__28 = data_in_reg[28]; // @[DCache.scala 107:57]
  wire  temp__29 = data_in_reg[29]; // @[DCache.scala 107:57]
  wire  temp__30 = data_in_reg[30]; // @[DCache.scala 107:57]
  wire  temp__31 = data_in_reg[31]; // @[DCache.scala 107:57]
  wire  temp__32 = data_element_wire[32]; // @[DCache.scala 108:64]
  wire  temp__33 = data_element_wire[33]; // @[DCache.scala 108:64]
  wire  temp__34 = data_element_wire[34]; // @[DCache.scala 108:64]
  wire  temp__35 = data_element_wire[35]; // @[DCache.scala 108:64]
  wire  temp__36 = data_element_wire[36]; // @[DCache.scala 108:64]
  wire  temp__37 = data_element_wire[37]; // @[DCache.scala 108:64]
  wire  temp__38 = data_element_wire[38]; // @[DCache.scala 108:64]
  wire  temp__39 = data_element_wire[39]; // @[DCache.scala 108:64]
  wire  temp__40 = data_element_wire[40]; // @[DCache.scala 108:64]
  wire  temp__41 = data_element_wire[41]; // @[DCache.scala 108:64]
  wire  temp__42 = data_element_wire[42]; // @[DCache.scala 108:64]
  wire  temp__43 = data_element_wire[43]; // @[DCache.scala 108:64]
  wire  temp__44 = data_element_wire[44]; // @[DCache.scala 108:64]
  wire  temp__45 = data_element_wire[45]; // @[DCache.scala 108:64]
  wire  temp__46 = data_element_wire[46]; // @[DCache.scala 108:64]
  wire  temp__47 = data_element_wire[47]; // @[DCache.scala 108:64]
  wire  temp__48 = data_element_wire[48]; // @[DCache.scala 108:64]
  wire  temp__49 = data_element_wire[49]; // @[DCache.scala 108:64]
  wire  temp__50 = data_element_wire[50]; // @[DCache.scala 108:64]
  wire  temp__51 = data_element_wire[51]; // @[DCache.scala 108:64]
  wire  temp__52 = data_element_wire[52]; // @[DCache.scala 108:64]
  wire  temp__53 = data_element_wire[53]; // @[DCache.scala 108:64]
  wire  temp__54 = data_element_wire[54]; // @[DCache.scala 108:64]
  wire  temp__55 = data_element_wire[55]; // @[DCache.scala 108:64]
  wire [6:0] lo_lo_lo = {temp__6,temp__5,temp__4,temp__3,temp__2,temp__1,temp__0}; // @[DCache.scala 109:66]
  wire [13:0] lo_lo = {temp__13,temp__12,temp__11,temp__10,temp__9,temp__8,temp__7,lo_lo_lo}; // @[DCache.scala 109:66]
  wire [6:0] lo_hi_lo = {temp__20,temp__19,temp__18,temp__17,temp__16,temp__15,temp__14}; // @[DCache.scala 109:66]
  wire [28:0] lo = {temp__28,temp__27,temp__26,temp__25,temp__24,temp__23,temp__22,temp__21,lo_hi_lo,lo_lo}; // @[DCache.scala 109:66]
  wire [6:0] hi_lo_lo = {temp__35,temp__34,temp__33,temp__32,temp__31,temp__30,temp__29}; // @[DCache.scala 109:66]
  wire [13:0] hi_lo = {temp__42,temp__41,temp__40,temp__39,temp__38,temp__37,temp__36,hi_lo_lo}; // @[DCache.scala 109:66]
  wire [6:0] hi_hi_lo = {temp__49,temp__48,temp__47,temp__46,temp__45,temp__44,temp__43}; // @[DCache.scala 109:66]
  wire [28:0] hi = {2'h3,temp__55,temp__54,temp__53,temp__52,temp__51,temp__50,hi_hi_lo,hi_lo}; // @[DCache.scala 109:66]
  wire [31:0] _GEN_11 = read_en_reg ? data_element_wire[31:0] : 32'h0; // @[DCache.scala 101:23 62:15 99:27]
  wire  _GEN_14 = read_en_reg ? 1'h0 : write_en_reg; // @[DCache.scala 99:27 59:29]
  wire [1:0] _GEN_17 = data_element_wire[56] & data_element_wire[57] ? 2'h2 : 2'h3; // @[DCache.scala 114:62 115:20 117:20]
  wire [31:0] _GEN_20 = data_element_wire[57] & data_element_wire[55:32] == data_addr_reg[31:8] ? _GEN_11 : 32'h0; // @[DCache.scala 94:105 62:15]
  wire  _GEN_23 = data_element_wire[57] & data_element_wire[55:32] == data_addr_reg[31:8] & _GEN_14; // @[DCache.scala 94:105 59:29]
  wire  temp_1_2 = index[0]; // @[DCache.scala 130:46]
  wire  temp_1_8 = data_element[32]; // @[DCache.scala 131:53]
  wire  temp_1_9 = data_element[33]; // @[DCache.scala 131:53]
  wire  temp_1_10 = data_element[34]; // @[DCache.scala 131:53]
  wire  temp_1_11 = data_element[35]; // @[DCache.scala 131:53]
  wire  temp_1_12 = data_element[36]; // @[DCache.scala 131:53]
  wire  temp_1_13 = data_element[37]; // @[DCache.scala 131:53]
  wire  temp_1_14 = data_element[38]; // @[DCache.scala 131:53]
  wire  temp_1_15 = data_element[39]; // @[DCache.scala 131:53]
  wire  temp_1_16 = data_element[40]; // @[DCache.scala 131:53]
  wire  temp_1_17 = data_element[41]; // @[DCache.scala 131:53]
  wire  temp_1_18 = data_element[42]; // @[DCache.scala 131:53]
  wire  temp_1_19 = data_element[43]; // @[DCache.scala 131:53]
  wire  temp_1_20 = data_element[44]; // @[DCache.scala 131:53]
  wire  temp_1_21 = data_element[45]; // @[DCache.scala 131:53]
  wire  temp_1_22 = data_element[46]; // @[DCache.scala 131:53]
  wire  temp_1_23 = data_element[47]; // @[DCache.scala 131:53]
  wire  temp_1_24 = data_element[48]; // @[DCache.scala 131:53]
  wire  temp_1_25 = data_element[49]; // @[DCache.scala 131:53]
  wire  temp_1_26 = data_element[50]; // @[DCache.scala 131:53]
  wire  temp_1_27 = data_element[51]; // @[DCache.scala 131:53]
  wire  temp_1_28 = data_element[52]; // @[DCache.scala 131:53]
  wire  temp_1_29 = data_element[53]; // @[DCache.scala 131:53]
  wire  temp_1_30 = data_element[54]; // @[DCache.scala 131:53]
  wire  temp_1_31 = data_element[55]; // @[DCache.scala 131:53]
  wire [7:0] io_mem_data_addr_lo_lo = {index[5],index[4],index[3],index[2],index[1],temp_1_2,2'h0}; // @[DCache.scala 132:32]
  wire [15:0] io_mem_data_addr_lo = {temp_1_15,temp_1_14,temp_1_13,temp_1_12,temp_1_11,temp_1_10,temp_1_9,temp_1_8,
    io_mem_data_addr_lo_lo}; // @[DCache.scala 132:32]
  wire [7:0] io_mem_data_addr_hi_lo = {temp_1_23,temp_1_22,temp_1_21,temp_1_20,temp_1_19,temp_1_18,temp_1_17,temp_1_16}; // @[DCache.scala 132:32]
  wire [31:0] _io_mem_data_addr_T = {temp_1_31,temp_1_30,temp_1_29,temp_1_28,temp_1_27,temp_1_26,temp_1_25,temp_1_24,
    io_mem_data_addr_hi_lo,io_mem_data_addr_lo}; // @[DCache.scala 132:32]
  wire  temp_2_0 = io_mem_data_out[0]; // @[DCache.scala 145:58]
  wire  temp_2_1 = io_mem_data_out[1]; // @[DCache.scala 145:58]
  wire  temp_2_2 = io_mem_data_out[2]; // @[DCache.scala 145:58]
  wire  temp_2_3 = io_mem_data_out[3]; // @[DCache.scala 145:58]
  wire  temp_2_4 = io_mem_data_out[4]; // @[DCache.scala 145:58]
  wire  temp_2_5 = io_mem_data_out[5]; // @[DCache.scala 145:58]
  wire  temp_2_6 = io_mem_data_out[6]; // @[DCache.scala 145:58]
  wire  temp_2_7 = io_mem_data_out[7]; // @[DCache.scala 145:58]
  wire  temp_2_8 = io_mem_data_out[8]; // @[DCache.scala 145:58]
  wire  temp_2_9 = io_mem_data_out[9]; // @[DCache.scala 145:58]
  wire  temp_2_10 = io_mem_data_out[10]; // @[DCache.scala 145:58]
  wire  temp_2_11 = io_mem_data_out[11]; // @[DCache.scala 145:58]
  wire  temp_2_12 = io_mem_data_out[12]; // @[DCache.scala 145:58]
  wire  temp_2_13 = io_mem_data_out[13]; // @[DCache.scala 145:58]
  wire  temp_2_14 = io_mem_data_out[14]; // @[DCache.scala 145:58]
  wire  temp_2_15 = io_mem_data_out[15]; // @[DCache.scala 145:58]
  wire  temp_2_16 = io_mem_data_out[16]; // @[DCache.scala 145:58]
  wire  temp_2_17 = io_mem_data_out[17]; // @[DCache.scala 145:58]
  wire  temp_2_18 = io_mem_data_out[18]; // @[DCache.scala 145:58]
  wire  temp_2_19 = io_mem_data_out[19]; // @[DCache.scala 145:58]
  wire  temp_2_20 = io_mem_data_out[20]; // @[DCache.scala 145:58]
  wire  temp_2_21 = io_mem_data_out[21]; // @[DCache.scala 145:58]
  wire  temp_2_22 = io_mem_data_out[22]; // @[DCache.scala 145:58]
  wire  temp_2_23 = io_mem_data_out[23]; // @[DCache.scala 145:58]
  wire  temp_2_24 = io_mem_data_out[24]; // @[DCache.scala 145:58]
  wire  temp_2_25 = io_mem_data_out[25]; // @[DCache.scala 145:58]
  wire  temp_2_26 = io_mem_data_out[26]; // @[DCache.scala 145:58]
  wire  temp_2_27 = io_mem_data_out[27]; // @[DCache.scala 145:58]
  wire  temp_2_28 = io_mem_data_out[28]; // @[DCache.scala 145:58]
  wire  temp_2_29 = io_mem_data_out[29]; // @[DCache.scala 145:58]
  wire  temp_2_30 = io_mem_data_out[30]; // @[DCache.scala 145:58]
  wire  temp_2_31 = io_mem_data_out[31]; // @[DCache.scala 145:58]
  wire  temp_2_32 = data_addr_reg[8]; // @[DCache.scala 148:57]
  wire  temp_2_33 = data_addr_reg[9]; // @[DCache.scala 148:57]
  wire  temp_2_34 = data_addr_reg[10]; // @[DCache.scala 148:57]
  wire  temp_2_35 = data_addr_reg[11]; // @[DCache.scala 148:57]
  wire  temp_2_36 = data_addr_reg[12]; // @[DCache.scala 148:57]
  wire  temp_2_37 = data_addr_reg[13]; // @[DCache.scala 148:57]
  wire  temp_2_38 = data_addr_reg[14]; // @[DCache.scala 148:57]
  wire  temp_2_39 = data_addr_reg[15]; // @[DCache.scala 148:57]
  wire  temp_2_40 = data_addr_reg[16]; // @[DCache.scala 148:57]
  wire  temp_2_41 = data_addr_reg[17]; // @[DCache.scala 148:57]
  wire  temp_2_42 = data_addr_reg[18]; // @[DCache.scala 148:57]
  wire  temp_2_43 = data_addr_reg[19]; // @[DCache.scala 148:57]
  wire  temp_2_44 = data_addr_reg[20]; // @[DCache.scala 148:57]
  wire  temp_2_45 = data_addr_reg[21]; // @[DCache.scala 148:57]
  wire  temp_2_46 = data_addr_reg[22]; // @[DCache.scala 148:57]
  wire  temp_2_47 = data_addr_reg[23]; // @[DCache.scala 148:57]
  wire  temp_2_48 = data_addr_reg[24]; // @[DCache.scala 148:57]
  wire  temp_2_49 = data_addr_reg[25]; // @[DCache.scala 148:57]
  wire  temp_2_50 = data_addr_reg[26]; // @[DCache.scala 148:57]
  wire  temp_2_51 = data_addr_reg[27]; // @[DCache.scala 148:57]
  wire  temp_2_52 = data_addr_reg[28]; // @[DCache.scala 148:57]
  wire  temp_2_53 = data_addr_reg[29]; // @[DCache.scala 148:57]
  wire  temp_2_54 = data_addr_reg[30]; // @[DCache.scala 148:57]
  wire  temp_2_55 = data_addr_reg[31]; // @[DCache.scala 148:57]
  wire [6:0] lo_lo_lo_1 = {temp_2_6,temp_2_5,temp_2_4,temp_2_3,temp_2_2,temp_2_1,temp_2_0}; // @[DCache.scala 149:41]
  wire [13:0] lo_lo_1 = {temp_2_13,temp_2_12,temp_2_11,temp_2_10,temp_2_9,temp_2_8,temp_2_7,lo_lo_lo_1}; // @[DCache.scala 149:41]
  wire [6:0] lo_hi_lo_1 = {temp_2_20,temp_2_19,temp_2_18,temp_2_17,temp_2_16,temp_2_15,temp_2_14}; // @[DCache.scala 149:41]
  wire [28:0] lo_1 = {temp_2_28,temp_2_27,temp_2_26,temp_2_25,temp_2_24,temp_2_23,temp_2_22,temp_2_21,lo_hi_lo_1,lo_lo_1
    }; // @[DCache.scala 149:41]
  wire [6:0] hi_lo_lo_1 = {temp_2_35,temp_2_34,temp_2_33,temp_2_32,temp_2_31,temp_2_30,temp_2_29}; // @[DCache.scala 149:41]
  wire [13:0] hi_lo_1 = {temp_2_42,temp_2_41,temp_2_40,temp_2_39,temp_2_38,temp_2_37,temp_2_36,hi_lo_lo_1}; // @[DCache.scala 149:41]
  wire [6:0] hi_hi_lo_1 = {temp_2_49,temp_2_48,temp_2_47,temp_2_46,temp_2_45,temp_2_44,temp_2_43}; // @[DCache.scala 149:41]
  wire [28:0] hi_1 = {2'h2,temp_2_55,temp_2_54,temp_2_53,temp_2_52,temp_2_51,temp_2_50,hi_hi_lo_1,hi_lo_1}; // @[DCache.scala 149:41]
  wire  _GEN_26 = statecount ? 1'h0 : 1'h1; // @[DCache.scala 140:24 141:20 152:20]
  wire [1:0] _GEN_32 = statecount ? 2'h1 : stateReg; // @[DCache.scala 140:24 150:18 53:25]
  wire [31:0] _GEN_34 = statecount ? 32'h0 : data_addr_reg; // @[DCache.scala 140:24 69:20 155:26]
  wire [1:0] _GEN_42 = 2'h3 == stateReg ? _GEN_32 : stateReg; // @[DCache.scala 71:20 53:25]
  wire [31:0] _GEN_44 = 2'h3 == stateReg ? _GEN_34 : 32'h0; // @[DCache.scala 69:20 71:20]
  wire [31:0] _GEN_47 = 2'h2 == stateReg ? _io_mem_data_addr_T : _GEN_44; // @[DCache.scala 71:20 132:24]
  wire [31:0] _GEN_48 = 2'h2 == stateReg ? data_element[31:0] : 32'h0; // @[DCache.scala 71:20 133:22 68:18]
  wire  _GEN_53 = 2'h2 == stateReg ? 1'h0 : 2'h3 == stateReg & statecount; // @[DCache.scala 71:20 59:29]
  wire [6:0] _GEN_56 = 2'h1 == stateReg ? _index_T_1 : {{1'd0}, index}; // @[DCache.scala 71:20 89:13 54:18]
  wire [31:0] _GEN_64 = 2'h1 == stateReg ? _GEN_20 : 32'h0; // @[DCache.scala 62:15 71:20]
  wire  _GEN_67 = 2'h1 == stateReg & _GEN_23; // @[DCache.scala 71:20 59:29]
  wire  _GEN_70 = 2'h1 == stateReg ? 1'h0 : 2'h2 == stateReg; // @[DCache.scala 66:19 71:20]
  wire [31:0] _GEN_72 = 2'h1 == stateReg ? 32'h0 : _GEN_47; // @[DCache.scala 69:20 71:20]
  wire [31:0] _GEN_73 = 2'h1 == stateReg ? 32'h0 : _GEN_48; // @[DCache.scala 68:18 71:20]
  wire  _GEN_77 = 2'h1 == stateReg ? 1'h0 : _GEN_53; // @[DCache.scala 71:20 59:29]
  wire [6:0] _GEN_87 = 2'h0 == stateReg ? {{1'd0}, index} : _GEN_56; // @[DCache.scala 54:18 71:20]
  assign cache_data_array_data_element_wire_MPORT_en = _T ? 1'h0 : _T_2;
  assign cache_data_array_data_element_wire_MPORT_addr = _index_T_1[5:0];
  assign cache_data_array_data_element_wire_MPORT_data = cache_data_array[cache_data_array_data_element_wire_MPORT_addr]
    ; // @[DCache.scala 59:29]
  assign cache_data_array_MPORT_data = {hi,lo};
  assign cache_data_array_MPORT_addr = _index_T_1[5:0];
  assign cache_data_array_MPORT_mask = 1'h1;
  assign cache_data_array_MPORT_en = _T ? 1'h0 : _GEN_67;
  assign cache_data_array_MPORT_1_data = {hi_1,lo_1};
  assign cache_data_array_MPORT_1_addr = index;
  assign cache_data_array_MPORT_1_mask = 1'h1;
  assign cache_data_array_MPORT_1_en = _T ? 1'h0 : _GEN_77;
  assign io_data_out = 2'h0 == stateReg ? data_element[31:0] : _GEN_64; // @[DCache.scala 71:20 75:19]
  assign io_busy = stateReg != 2'h0; // @[DCache.scala 64:24]
  assign io_mem_write_en = 2'h0 == stateReg ? 1'h0 : _GEN_70; // @[DCache.scala 66:19 71:20]
  assign io_mem_data_in = 2'h0 == stateReg ? 32'h0 : _GEN_73; // @[DCache.scala 68:18 71:20]
  assign io_mem_data_addr = 2'h0 == stateReg ? 32'h0 : _GEN_72; // @[DCache.scala 69:20 71:20]
  always @(posedge clock) begin
    if (cache_data_array_MPORT_en & cache_data_array_MPORT_mask) begin
      cache_data_array[cache_data_array_MPORT_addr] <= cache_data_array_MPORT_data; // @[DCache.scala 59:29]
    end
    if (cache_data_array_MPORT_1_en & cache_data_array_MPORT_1_mask) begin
      cache_data_array[cache_data_array_MPORT_1_addr] <= cache_data_array_MPORT_1_data; // @[DCache.scala 59:29]
    end
    if (2'h0 == stateReg) begin // @[DCache.scala 71:20]
      if (io_write_en | io_read_en) begin // @[DCache.scala 76:39]
        write_en_reg <= io_write_en; // @[DCache.scala 79:22]
      end
    end
    if (2'h0 == stateReg) begin // @[DCache.scala 71:20]
      if (io_write_en | io_read_en) begin // @[DCache.scala 76:39]
        read_en_reg <= io_read_en; // @[DCache.scala 80:21]
      end
    end
    if (2'h0 == stateReg) begin // @[DCache.scala 71:20]
      if (io_write_en | io_read_en) begin // @[DCache.scala 76:39]
        data_addr_reg <= io_data_addr; // @[DCache.scala 81:23]
      end
    end
    if (2'h0 == stateReg) begin // @[DCache.scala 71:20]
      if (io_write_en | io_read_en) begin // @[DCache.scala 76:39]
        data_in_reg <= io_data_in; // @[DCache.scala 82:21]
      end
    end
    if (reset) begin // @[DCache.scala 53:25]
      stateReg <= 2'h0; // @[DCache.scala 53:25]
    end else if (2'h0 == stateReg) begin // @[DCache.scala 71:20]
      if (io_write_en | io_read_en) begin // @[DCache.scala 76:39]
        stateReg <= 2'h1; // @[DCache.scala 78:18]
      end
    end else if (2'h1 == stateReg) begin // @[DCache.scala 71:20]
      if (data_element_wire[57] & data_element_wire[55:32] == data_addr_reg[31:8]) begin // @[DCache.scala 94:105]
        stateReg <= 2'h0; // @[DCache.scala 97:18]
      end else begin
        stateReg <= _GEN_17;
      end
    end else if (2'h2 == stateReg) begin // @[DCache.scala 71:20]
      stateReg <= 2'h3; // @[DCache.scala 134:16]
    end else begin
      stateReg <= _GEN_42;
    end
    index <= _GEN_87[5:0];
    if (!(2'h0 == stateReg)) begin // @[DCache.scala 71:20]
      if (2'h1 == stateReg) begin // @[DCache.scala 71:20]
        if (2'h0 == stateReg) begin // @[DCache.scala 71:20]
          data_element <= 58'h0;
        end else if (2'h1 == stateReg) begin // @[DCache.scala 71:20]
          data_element <= cache_data_array_data_element_wire_MPORT_data; // @[DCache.scala 90:25]
        end else begin
          data_element <= 58'h0;
        end
      end
    end
    if (2'h0 == stateReg) begin // @[DCache.scala 71:20]
      if (io_write_en | io_read_en) begin // @[DCache.scala 76:39]
        statecount <= 1'h0; // @[DCache.scala 83:20]
      end
    end else if (!(2'h1 == stateReg)) begin // @[DCache.scala 71:20]
      if (!(2'h2 == stateReg)) begin // @[DCache.scala 71:20]
        if (2'h3 == stateReg) begin // @[DCache.scala 71:20]
          statecount <= _GEN_26;
        end
      end
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
  integer initvar;
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  write_en_reg = _RAND_0[0:0];
  _RAND_1 = {1{`RANDOM}};
  read_en_reg = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  data_addr_reg = _RAND_2[31:0];
  _RAND_3 = {1{`RANDOM}};
  data_in_reg = _RAND_3[31:0];
  _RAND_4 = {1{`RANDOM}};
  stateReg = _RAND_4[1:0];
  _RAND_5 = {1{`RANDOM}};
  index = _RAND_5[5:0];
  _RAND_6 = {2{`RANDOM}};
  data_element = _RAND_6[57:0];
  _RAND_7 = {1{`RANDOM}};
  statecount = _RAND_7[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
  initial begin
    $readmemb("src/main/scala/DCache/CacheContent.bin", cache_data_array);
  end
endmodule
module CacheAndMemory(
  input         clock,
  input         reset,
  input  [31:0] io_write_data,
  input  [31:0] io_address,
  input         io_write_en,
  input         io_read_en,
  output [31:0] io_data_out,
  output        io_busy
);
  wire  data_mem_clock; // @[CacheAndMemory.scala 34:25]
  wire  data_mem_io_writeEnable; // @[CacheAndMemory.scala 34:25]
  wire [31:0] data_mem_io_dataIn; // @[CacheAndMemory.scala 34:25]
  wire [31:0] data_mem_io_dataAddress; // @[CacheAndMemory.scala 34:25]
  wire [31:0] data_mem_io_dataOut; // @[CacheAndMemory.scala 34:25]
  wire  dcache_clock; // @[CacheAndMemory.scala 35:23]
  wire  dcache_reset; // @[CacheAndMemory.scala 35:23]
  wire  dcache_io_write_en; // @[CacheAndMemory.scala 35:23]
  wire  dcache_io_read_en; // @[CacheAndMemory.scala 35:23]
  wire [31:0] dcache_io_data_addr; // @[CacheAndMemory.scala 35:23]
  wire [31:0] dcache_io_data_in; // @[CacheAndMemory.scala 35:23]
  wire [31:0] dcache_io_data_out; // @[CacheAndMemory.scala 35:23]
  wire  dcache_io_busy; // @[CacheAndMemory.scala 35:23]
  wire  dcache_io_mem_write_en; // @[CacheAndMemory.scala 35:23]
  wire [31:0] dcache_io_mem_data_in; // @[CacheAndMemory.scala 35:23]
  wire [31:0] dcache_io_mem_data_addr; // @[CacheAndMemory.scala 35:23]
  wire [31:0] dcache_io_mem_data_out; // @[CacheAndMemory.scala 35:23]
  DataMemory data_mem ( // @[CacheAndMemory.scala 34:25]
    .clock(data_mem_clock),
    .io_writeEnable(data_mem_io_writeEnable),
    .io_dataIn(data_mem_io_dataIn),
    .io_dataAddress(data_mem_io_dataAddress),
    .io_dataOut(data_mem_io_dataOut)
  );
  DCache dcache ( // @[CacheAndMemory.scala 35:23]
    .clock(dcache_clock),
    .reset(dcache_reset),
    .io_write_en(dcache_io_write_en),
    .io_read_en(dcache_io_read_en),
    .io_data_addr(dcache_io_data_addr),
    .io_data_in(dcache_io_data_in),
    .io_data_out(dcache_io_data_out),
    .io_busy(dcache_io_busy),
    .io_mem_write_en(dcache_io_mem_write_en),
    .io_mem_data_in(dcache_io_mem_data_in),
    .io_mem_data_addr(dcache_io_mem_data_addr),
    .io_mem_data_out(dcache_io_mem_data_out)
  );
  assign io_data_out = dcache_io_data_out; // @[CacheAndMemory.scala 48:15]
  assign io_busy = dcache_io_busy; // @[CacheAndMemory.scala 49:11]
  assign data_mem_clock = clock;
  assign data_mem_io_writeEnable = dcache_io_mem_write_en; // @[CacheAndMemory.scala 51:27]
  assign data_mem_io_dataIn = dcache_io_mem_data_in; // @[CacheAndMemory.scala 53:22]
  assign data_mem_io_dataAddress = dcache_io_mem_data_addr / 3'h4; // @[CacheAndMemory.scala 54:54]
  assign dcache_clock = clock;
  assign dcache_reset = reset;
  assign dcache_io_write_en = io_write_en; // @[CacheAndMemory.scala 45:22]
  assign dcache_io_read_en = io_read_en; // @[CacheAndMemory.scala 46:21]
  assign dcache_io_data_addr = io_address; // @[CacheAndMemory.scala 44:23]
  assign dcache_io_data_in = io_write_data; // @[CacheAndMemory.scala 43:21]
  assign dcache_io_mem_data_out = data_mem_io_dataOut; // @[CacheAndMemory.scala 55:26]
endmodule
module MEM(
  input         clock,
  input         reset,
  output [31:0] testHarness_DMEMpeek,
  input  [31:0] io_dataIn,
  input  [31:0] io_dataAddress,
  input         io_writeEnable,
  input         io_readEnable,
  output [31:0] io_dataOut,
  output        io_memBusy
);
  wire  DMEM_clock; // @[MEM.scala 44:20]
  wire  DMEM_reset; // @[MEM.scala 44:20]
  wire [31:0] DMEM_io_write_data; // @[MEM.scala 44:20]
  wire [31:0] DMEM_io_address; // @[MEM.scala 44:20]
  wire  DMEM_io_write_en; // @[MEM.scala 44:20]
  wire  DMEM_io_read_en; // @[MEM.scala 44:20]
  wire [31:0] DMEM_io_data_out; // @[MEM.scala 44:20]
  wire  DMEM_io_busy; // @[MEM.scala 44:20]
  CacheAndMemory DMEM ( // @[MEM.scala 44:20]
    .clock(DMEM_clock),
    .reset(DMEM_reset),
    .io_write_data(DMEM_io_write_data),
    .io_address(DMEM_io_address),
    .io_write_en(DMEM_io_write_en),
    .io_read_en(DMEM_io_read_en),
    .io_data_out(DMEM_io_data_out),
    .io_busy(DMEM_io_busy)
  );
  assign testHarness_DMEMpeek = DMEM_io_data_out; // @[MEM.scala 47:27]
  assign io_dataOut = DMEM_io_data_out; // @[MEM.scala 56:23]
  assign io_memBusy = DMEM_io_busy; // @[MEM.scala 58:23]
  assign DMEM_clock = clock;
  assign DMEM_reset = reset;
  assign DMEM_io_write_data = io_dataIn; // @[MEM.scala 51:23]
  assign DMEM_io_address = io_dataAddress; // @[MEM.scala 52:23]
  assign DMEM_io_write_en = io_writeEnable; // @[MEM.scala 53:23]
  assign DMEM_io_read_en = io_readEnable; // @[MEM.scala 54:23]
endmodule
module HazardUnit(
  input  [2:0]  io_branchType,
  input         io_controlSignalsEXB_memToReg,
  input         io_controlSignalsEXB_regWrite,
  input         io_controlSignalsMEMB_regWrite,
  input  [31:0] io_rs1AddrIFB,
  input  [31:0] io_rs1AddrIDB,
  input  [31:0] io_rs2AddrIFB,
  input  [31:0] io_rs2AddrIDB,
  input  [31:0] io_rdAddrIDB,
  input  [31:0] io_rdAddrEXB,
  input  [31:0] io_rdAddrMEMB,
  input         io_branchTaken,
  input         io_btbPrediction,
  input         io_wrongAddrPred,
  input         io_membusy,
  output        io_branchMispredicted,
  output        io_stall,
  output        io_stall_membusy,
  output        io_flushE,
  output        io_flushD,
  output [1:0]  io_rs1Select,
  output [1:0]  io_rs2Select
);
  wire  _T = io_rs1AddrIDB != 32'h0; // @[HazardUnit.scala 50:23]
  wire [1:0] _GEN_0 = _T & io_rs1AddrIDB == io_rdAddrMEMB & io_controlSignalsMEMB_regWrite ? 2'h2 : 2'h0; // @[HazardUnit.scala 54:108 55:19 58:19]
  wire  _T_8 = io_rs2AddrIDB != 32'h0; // @[HazardUnit.scala 62:23]
  wire [1:0] _GEN_2 = _T_8 & io_rs2AddrIDB == io_rdAddrMEMB & io_controlSignalsMEMB_regWrite ? 2'h2 : 2'h0; // @[HazardUnit.scala 66:108 67:19 70:19]
  wire  _T_18 = io_rs1AddrIFB != 32'h0 | io_rs2AddrIFB != 32'h0; // @[HazardUnit.scala 74:33]
  wire  _T_22 = _T_18 & (io_rs1AddrIFB == io_rdAddrIDB | io_rs2AddrIFB == io_rdAddrIDB); // @[HazardUnit.scala 75:10]
  wire  _T_23 = _T_22 & io_controlSignalsEXB_regWrite; // @[HazardUnit.scala 76:10]
  assign io_branchMispredicted = io_branchTaken != io_btbPrediction & io_branchType != 3'h7 | io_wrongAddrPred; // @[HazardUnit.scala 91:84]
  assign io_stall = _T_23 & io_controlSignalsEXB_memToReg; // @[HazardUnit.scala 77:10]
  assign io_stall_membusy = io_membusy; // @[HazardUnit.scala 84:20]
  assign io_flushE = io_stall | io_branchMispredicted; // @[HazardUnit.scala 98:27]
  assign io_flushD = io_branchMispredicted; // @[HazardUnit.scala 97:15]
  assign io_rs1Select = io_rs1AddrIDB != 32'h0 & io_rs1AddrIDB == io_rdAddrEXB & io_controlSignalsEXB_regWrite ? 2'h1 :
    _GEN_0; // @[HazardUnit.scala 50:101 52:19]
  assign io_rs2Select = io_rs2AddrIDB != 32'h0 & io_rs2AddrIDB == io_rdAddrEXB & io_controlSignalsEXB_regWrite ? 2'h1 :
    _GEN_2; // @[HazardUnit.scala 62:101 64:19]
endmodule
module top_MC(
  input         clock,
  input         reset,
  input         testHarness_setupSignals_IMEMsignals_setup,
  input  [31:0] testHarness_setupSignals_IMEMsignals_address,
  input  [31:0] testHarness_setupSignals_IMEMsignals_instruction,
  input         testHarness_setupSignals_registerSignals_setup,
  input  [4:0]  testHarness_setupSignals_registerSignals_readAddress,
  input         testHarness_setupSignals_registerSignals_writeEnable,
  input  [31:0] testHarness_setupSignals_registerSignals_writeData,
  output [31:0] testHarness_testReadouts_registerRead,
  output [31:0] testHarness_testReadouts_DMEMread,
  output        testHarness_regUpdates_writeEnable,
  output [31:0] testHarness_regUpdates_writeData,
  output [4:0]  testHarness_regUpdates_writeAddress,
  output [31:0] testHarness_currentPC
);
  wire  IFpipe_clock; // @[top_MC.scala 37:26]
  wire  IFpipe_reset; // @[top_MC.scala 37:26]
  wire [31:0] IFpipe_io_inCurrentPC; // @[top_MC.scala 37:26]
  wire [31:0] IFpipe_io_inInstruction_instruction; // @[top_MC.scala 37:26]
  wire  IFpipe_io_stall; // @[top_MC.scala 37:26]
  wire  IFpipe_io_flush; // @[top_MC.scala 37:26]
  wire  IFpipe_io_inBTBHit; // @[top_MC.scala 37:26]
  wire  IFpipe_io_inBTBPrediction; // @[top_MC.scala 37:26]
  wire [31:0] IFpipe_io_inBTBTargetPredict; // @[top_MC.scala 37:26]
  wire  IFpipe_io_outBTBHit; // @[top_MC.scala 37:26]
  wire  IFpipe_io_outBTBPrediction; // @[top_MC.scala 37:26]
  wire [31:0] IFpipe_io_outBTBTargetPredict; // @[top_MC.scala 37:26]
  wire [31:0] IFpipe_io_outCurrentPC; // @[top_MC.scala 37:26]
  wire [31:0] IFpipe_io_outInstruction_instruction; // @[top_MC.scala 37:26]
  wire  IDpipe_clock; // @[top_MC.scala 38:26]
  wire  IDpipe_reset; // @[top_MC.scala 38:26]
  wire [31:0] IDpipe_io_inInstruction_instruction; // @[top_MC.scala 38:26]
  wire  IDpipe_io_inControlSignals_memToReg; // @[top_MC.scala 38:26]
  wire  IDpipe_io_inControlSignals_regWrite; // @[top_MC.scala 38:26]
  wire  IDpipe_io_inControlSignals_memRead; // @[top_MC.scala 38:26]
  wire  IDpipe_io_inControlSignals_memWrite; // @[top_MC.scala 38:26]
  wire [31:0] IDpipe_io_inPC; // @[top_MC.scala 38:26]
  wire [2:0] IDpipe_io_inBranchType; // @[top_MC.scala 38:26]
  wire  IDpipe_io_inOp1Select; // @[top_MC.scala 38:26]
  wire  IDpipe_io_inOp2Select; // @[top_MC.scala 38:26]
  wire [31:0] IDpipe_io_inImmData; // @[top_MC.scala 38:26]
  wire [4:0] IDpipe_io_inRd; // @[top_MC.scala 38:26]
  wire [4:0] IDpipe_io_inALUop; // @[top_MC.scala 38:26]
  wire [31:0] IDpipe_io_outInstruction_instruction; // @[top_MC.scala 38:26]
  wire  IDpipe_io_outControlSignals_memToReg; // @[top_MC.scala 38:26]
  wire  IDpipe_io_outControlSignals_regWrite; // @[top_MC.scala 38:26]
  wire  IDpipe_io_outControlSignals_memRead; // @[top_MC.scala 38:26]
  wire  IDpipe_io_outControlSignals_memWrite; // @[top_MC.scala 38:26]
  wire [31:0] IDpipe_io_outPC; // @[top_MC.scala 38:26]
  wire [2:0] IDpipe_io_outBranchType; // @[top_MC.scala 38:26]
  wire  IDpipe_io_outOp1Select; // @[top_MC.scala 38:26]
  wire  IDpipe_io_outOp2Select; // @[top_MC.scala 38:26]
  wire [31:0] IDpipe_io_outImmData; // @[top_MC.scala 38:26]
  wire [4:0] IDpipe_io_outRd; // @[top_MC.scala 38:26]
  wire [4:0] IDpipe_io_outALUop; // @[top_MC.scala 38:26]
  wire [31:0] IDpipe_io_inReadData1; // @[top_MC.scala 38:26]
  wire [31:0] IDpipe_io_inReadData2; // @[top_MC.scala 38:26]
  wire  IDpipe_io_flush; // @[top_MC.scala 38:26]
  wire  IDpipe_io_stall; // @[top_MC.scala 38:26]
  wire  IDpipe_io_inBTBHit; // @[top_MC.scala 38:26]
  wire  IDpipe_io_inBTBPrediction; // @[top_MC.scala 38:26]
  wire [31:0] IDpipe_io_inBTBTargetPredict; // @[top_MC.scala 38:26]
  wire  IDpipe_io_outBTBHit; // @[top_MC.scala 38:26]
  wire  IDpipe_io_outBTBPrediction; // @[top_MC.scala 38:26]
  wire [31:0] IDpipe_io_outBTBTargetPredict; // @[top_MC.scala 38:26]
  wire [31:0] IDpipe_io_outReadData1; // @[top_MC.scala 38:26]
  wire [31:0] IDpipe_io_outReadData2; // @[top_MC.scala 38:26]
  wire  EXpipe_clock; // @[top_MC.scala 39:26]
  wire  EXpipe_reset; // @[top_MC.scala 39:26]
  wire  EXpipe_io_inControlSignals_memToReg; // @[top_MC.scala 39:26]
  wire  EXpipe_io_inControlSignals_regWrite; // @[top_MC.scala 39:26]
  wire  EXpipe_io_inControlSignals_memRead; // @[top_MC.scala 39:26]
  wire  EXpipe_io_inControlSignals_memWrite; // @[top_MC.scala 39:26]
  wire [4:0] EXpipe_io_inRd; // @[top_MC.scala 39:26]
  wire [31:0] EXpipe_io_inRs2; // @[top_MC.scala 39:26]
  wire [31:0] EXpipe_io_inALUResult; // @[top_MC.scala 39:26]
  wire  EXpipe_io_stall; // @[top_MC.scala 39:26]
  wire [31:0] EXpipe_io_outALUResult; // @[top_MC.scala 39:26]
  wire  EXpipe_io_outControlSignals_memToReg; // @[top_MC.scala 39:26]
  wire  EXpipe_io_outControlSignals_regWrite; // @[top_MC.scala 39:26]
  wire  EXpipe_io_outControlSignals_memRead; // @[top_MC.scala 39:26]
  wire  EXpipe_io_outControlSignals_memWrite; // @[top_MC.scala 39:26]
  wire [4:0] EXpipe_io_outRd; // @[top_MC.scala 39:26]
  wire [31:0] EXpipe_io_outRs2; // @[top_MC.scala 39:26]
  wire  MEMpipe_clock; // @[top_MC.scala 40:26]
  wire  MEMpipe_reset; // @[top_MC.scala 40:26]
  wire  MEMpipe_io_inControlSignals_memToReg; // @[top_MC.scala 40:26]
  wire  MEMpipe_io_inControlSignals_regWrite; // @[top_MC.scala 40:26]
  wire [4:0] MEMpipe_io_inRd; // @[top_MC.scala 40:26]
  wire [31:0] MEMpipe_io_inMEMData; // @[top_MC.scala 40:26]
  wire [31:0] MEMpipe_io_inALUResult; // @[top_MC.scala 40:26]
  wire  MEMpipe_io_stall; // @[top_MC.scala 40:26]
  wire [31:0] MEMpipe_io_outMEMData; // @[top_MC.scala 40:26]
  wire  MEMpipe_io_outControlSignals_memToReg; // @[top_MC.scala 40:26]
  wire  MEMpipe_io_outControlSignals_regWrite; // @[top_MC.scala 40:26]
  wire [4:0] MEMpipe_io_outRd; // @[top_MC.scala 40:26]
  wire [31:0] MEMpipe_io_outALUResult; // @[top_MC.scala 40:26]
  wire  IF_clock; // @[top_MC.scala 43:19]
  wire  IF_reset; // @[top_MC.scala 43:19]
  wire  IF_testHarness_InstructionMemorySetup_setup; // @[top_MC.scala 43:19]
  wire [31:0] IF_testHarness_InstructionMemorySetup_address; // @[top_MC.scala 43:19]
  wire [31:0] IF_testHarness_InstructionMemorySetup_instruction; // @[top_MC.scala 43:19]
  wire [31:0] IF_testHarness_PC; // @[top_MC.scala 43:19]
  wire [31:0] IF_io_branchAddr; // @[top_MC.scala 43:19]
  wire [31:0] IF_io_IFBarrierPC; // @[top_MC.scala 43:19]
  wire  IF_io_stall; // @[top_MC.scala 43:19]
  wire  IF_io_updatePrediction; // @[top_MC.scala 43:19]
  wire  IF_io_newBranch; // @[top_MC.scala 43:19]
  wire [31:0] IF_io_entryPC; // @[top_MC.scala 43:19]
  wire  IF_io_branchTaken; // @[top_MC.scala 43:19]
  wire  IF_io_branchMispredicted; // @[top_MC.scala 43:19]
  wire [31:0] IF_io_PCplus4ExStage; // @[top_MC.scala 43:19]
  wire  IF_io_btbHit; // @[top_MC.scala 43:19]
  wire  IF_io_btbPrediction; // @[top_MC.scala 43:19]
  wire [31:0] IF_io_btbTargetPredict; // @[top_MC.scala 43:19]
  wire [31:0] IF_io_PC; // @[top_MC.scala 43:19]
  wire [31:0] IF_io_instruction_instruction; // @[top_MC.scala 43:19]
  wire  ID_clock; // @[top_MC.scala 44:19]
  wire  ID_testHarness_registerSetup_setup; // @[top_MC.scala 44:19]
  wire [4:0] ID_testHarness_registerSetup_readAddress; // @[top_MC.scala 44:19]
  wire  ID_testHarness_registerSetup_writeEnable; // @[top_MC.scala 44:19]
  wire [31:0] ID_testHarness_registerSetup_writeData; // @[top_MC.scala 44:19]
  wire [31:0] ID_testHarness_registerPeek; // @[top_MC.scala 44:19]
  wire  ID_testHarness_testUpdates_writeEnable; // @[top_MC.scala 44:19]
  wire [31:0] ID_testHarness_testUpdates_writeData; // @[top_MC.scala 44:19]
  wire [4:0] ID_testHarness_testUpdates_writeAddress; // @[top_MC.scala 44:19]
  wire [31:0] ID_io_instruction_instruction; // @[top_MC.scala 44:19]
  wire [4:0] ID_io_registerWriteAddress; // @[top_MC.scala 44:19]
  wire [31:0] ID_io_registerWriteData; // @[top_MC.scala 44:19]
  wire  ID_io_registerWriteEnable; // @[top_MC.scala 44:19]
  wire  ID_io_controlSignals_memToReg; // @[top_MC.scala 44:19]
  wire  ID_io_controlSignals_regWrite; // @[top_MC.scala 44:19]
  wire  ID_io_controlSignals_memRead; // @[top_MC.scala 44:19]
  wire  ID_io_controlSignals_memWrite; // @[top_MC.scala 44:19]
  wire [2:0] ID_io_branchType; // @[top_MC.scala 44:19]
  wire  ID_io_op1Select; // @[top_MC.scala 44:19]
  wire  ID_io_op2Select; // @[top_MC.scala 44:19]
  wire [2:0] ID_io_immType; // @[top_MC.scala 44:19]
  wire [31:0] ID_io_immData; // @[top_MC.scala 44:19]
  wire [4:0] ID_io_ALUop; // @[top_MC.scala 44:19]
  wire [31:0] ID_io_readData1; // @[top_MC.scala 44:19]
  wire [31:0] ID_io_readData2; // @[top_MC.scala 44:19]
  wire [31:0] EX_io_PC; // @[top_MC.scala 45:19]
  wire [2:0] EX_io_branchType; // @[top_MC.scala 45:19]
  wire  EX_io_op1Select; // @[top_MC.scala 45:19]
  wire  EX_io_op2Select; // @[top_MC.scala 45:19]
  wire [31:0] EX_io_rs1; // @[top_MC.scala 45:19]
  wire [31:0] EX_io_rs2; // @[top_MC.scala 45:19]
  wire [31:0] EX_io_immData; // @[top_MC.scala 45:19]
  wire [4:0] EX_io_ALUop; // @[top_MC.scala 45:19]
  wire [1:0] EX_io_rs1Select; // @[top_MC.scala 45:19]
  wire [1:0] EX_io_rs2Select; // @[top_MC.scala 45:19]
  wire [31:0] EX_io_ALUresultEXB; // @[top_MC.scala 45:19]
  wire [31:0] EX_io_ALUresultMEMB; // @[top_MC.scala 45:19]
  wire  EX_io_btbHit; // @[top_MC.scala 45:19]
  wire [31:0] EX_io_btbTargetPredict; // @[top_MC.scala 45:19]
  wire  EX_io_newBranch; // @[top_MC.scala 45:19]
  wire  EX_io_updatePrediction; // @[top_MC.scala 45:19]
  wire [31:0] EX_io_outPCplus4; // @[top_MC.scala 45:19]
  wire [31:0] EX_io_ALUResult; // @[top_MC.scala 45:19]
  wire [31:0] EX_io_branchTarget; // @[top_MC.scala 45:19]
  wire  EX_io_branchTaken; // @[top_MC.scala 45:19]
  wire  EX_io_wrongAddrPred; // @[top_MC.scala 45:19]
  wire [31:0] EX_io_Rs2Forwarded; // @[top_MC.scala 45:19]
  wire  MEM_clock; // @[top_MC.scala 46:19]
  wire  MEM_reset; // @[top_MC.scala 46:19]
  wire [31:0] MEM_testHarness_DMEMpeek; // @[top_MC.scala 46:19]
  wire [31:0] MEM_io_dataIn; // @[top_MC.scala 46:19]
  wire [31:0] MEM_io_dataAddress; // @[top_MC.scala 46:19]
  wire  MEM_io_writeEnable; // @[top_MC.scala 46:19]
  wire  MEM_io_readEnable; // @[top_MC.scala 46:19]
  wire [31:0] MEM_io_dataOut; // @[top_MC.scala 46:19]
  wire  MEM_io_memBusy; // @[top_MC.scala 46:19]
  wire [2:0] HzdUnit_io_branchType; // @[top_MC.scala 50:23]
  wire  HzdUnit_io_controlSignalsEXB_memToReg; // @[top_MC.scala 50:23]
  wire  HzdUnit_io_controlSignalsEXB_regWrite; // @[top_MC.scala 50:23]
  wire  HzdUnit_io_controlSignalsMEMB_regWrite; // @[top_MC.scala 50:23]
  wire [31:0] HzdUnit_io_rs1AddrIFB; // @[top_MC.scala 50:23]
  wire [31:0] HzdUnit_io_rs1AddrIDB; // @[top_MC.scala 50:23]
  wire [31:0] HzdUnit_io_rs2AddrIFB; // @[top_MC.scala 50:23]
  wire [31:0] HzdUnit_io_rs2AddrIDB; // @[top_MC.scala 50:23]
  wire [31:0] HzdUnit_io_rdAddrIDB; // @[top_MC.scala 50:23]
  wire [31:0] HzdUnit_io_rdAddrEXB; // @[top_MC.scala 50:23]
  wire [31:0] HzdUnit_io_rdAddrMEMB; // @[top_MC.scala 50:23]
  wire  HzdUnit_io_branchTaken; // @[top_MC.scala 50:23]
  wire  HzdUnit_io_btbPrediction; // @[top_MC.scala 50:23]
  wire  HzdUnit_io_wrongAddrPred; // @[top_MC.scala 50:23]
  wire  HzdUnit_io_membusy; // @[top_MC.scala 50:23]
  wire  HzdUnit_io_branchMispredicted; // @[top_MC.scala 50:23]
  wire  HzdUnit_io_stall; // @[top_MC.scala 50:23]
  wire  HzdUnit_io_stall_membusy; // @[top_MC.scala 50:23]
  wire  HzdUnit_io_flushE; // @[top_MC.scala 50:23]
  wire  HzdUnit_io_flushD; // @[top_MC.scala 50:23]
  wire [1:0] HzdUnit_io_rs1Select; // @[top_MC.scala 50:23]
  wire [1:0] HzdUnit_io_rs2Select; // @[top_MC.scala 50:23]
  IFpipe IFpipe ( // @[top_MC.scala 37:26]
    .clock(IFpipe_clock),
    .reset(IFpipe_reset),
    .io_inCurrentPC(IFpipe_io_inCurrentPC),
    .io_inInstruction_instruction(IFpipe_io_inInstruction_instruction),
    .io_stall(IFpipe_io_stall),
    .io_flush(IFpipe_io_flush),
    .io_inBTBHit(IFpipe_io_inBTBHit),
    .io_inBTBPrediction(IFpipe_io_inBTBPrediction),
    .io_inBTBTargetPredict(IFpipe_io_inBTBTargetPredict),
    .io_outBTBHit(IFpipe_io_outBTBHit),
    .io_outBTBPrediction(IFpipe_io_outBTBPrediction),
    .io_outBTBTargetPredict(IFpipe_io_outBTBTargetPredict),
    .io_outCurrentPC(IFpipe_io_outCurrentPC),
    .io_outInstruction_instruction(IFpipe_io_outInstruction_instruction)
  );
  IDpipe IDpipe ( // @[top_MC.scala 38:26]
    .clock(IDpipe_clock),
    .reset(IDpipe_reset),
    .io_inInstruction_instruction(IDpipe_io_inInstruction_instruction),
    .io_inControlSignals_memToReg(IDpipe_io_inControlSignals_memToReg),
    .io_inControlSignals_regWrite(IDpipe_io_inControlSignals_regWrite),
    .io_inControlSignals_memRead(IDpipe_io_inControlSignals_memRead),
    .io_inControlSignals_memWrite(IDpipe_io_inControlSignals_memWrite),
    .io_inPC(IDpipe_io_inPC),
    .io_inBranchType(IDpipe_io_inBranchType),
    .io_inOp1Select(IDpipe_io_inOp1Select),
    .io_inOp2Select(IDpipe_io_inOp2Select),
    .io_inImmData(IDpipe_io_inImmData),
    .io_inRd(IDpipe_io_inRd),
    .io_inALUop(IDpipe_io_inALUop),
    .io_outInstruction_instruction(IDpipe_io_outInstruction_instruction),
    .io_outControlSignals_memToReg(IDpipe_io_outControlSignals_memToReg),
    .io_outControlSignals_regWrite(IDpipe_io_outControlSignals_regWrite),
    .io_outControlSignals_memRead(IDpipe_io_outControlSignals_memRead),
    .io_outControlSignals_memWrite(IDpipe_io_outControlSignals_memWrite),
    .io_outPC(IDpipe_io_outPC),
    .io_outBranchType(IDpipe_io_outBranchType),
    .io_outOp1Select(IDpipe_io_outOp1Select),
    .io_outOp2Select(IDpipe_io_outOp2Select),
    .io_outImmData(IDpipe_io_outImmData),
    .io_outRd(IDpipe_io_outRd),
    .io_outALUop(IDpipe_io_outALUop),
    .io_inReadData1(IDpipe_io_inReadData1),
    .io_inReadData2(IDpipe_io_inReadData2),
    .io_flush(IDpipe_io_flush),
    .io_stall(IDpipe_io_stall),
    .io_inBTBHit(IDpipe_io_inBTBHit),
    .io_inBTBPrediction(IDpipe_io_inBTBPrediction),
    .io_inBTBTargetPredict(IDpipe_io_inBTBTargetPredict),
    .io_outBTBHit(IDpipe_io_outBTBHit),
    .io_outBTBPrediction(IDpipe_io_outBTBPrediction),
    .io_outBTBTargetPredict(IDpipe_io_outBTBTargetPredict),
    .io_outReadData1(IDpipe_io_outReadData1),
    .io_outReadData2(IDpipe_io_outReadData2)
  );
  EXpipe EXpipe ( // @[top_MC.scala 39:26]
    .clock(EXpipe_clock),
    .reset(EXpipe_reset),
    .io_inControlSignals_memToReg(EXpipe_io_inControlSignals_memToReg),
    .io_inControlSignals_regWrite(EXpipe_io_inControlSignals_regWrite),
    .io_inControlSignals_memRead(EXpipe_io_inControlSignals_memRead),
    .io_inControlSignals_memWrite(EXpipe_io_inControlSignals_memWrite),
    .io_inRd(EXpipe_io_inRd),
    .io_inRs2(EXpipe_io_inRs2),
    .io_inALUResult(EXpipe_io_inALUResult),
    .io_stall(EXpipe_io_stall),
    .io_outALUResult(EXpipe_io_outALUResult),
    .io_outControlSignals_memToReg(EXpipe_io_outControlSignals_memToReg),
    .io_outControlSignals_regWrite(EXpipe_io_outControlSignals_regWrite),
    .io_outControlSignals_memRead(EXpipe_io_outControlSignals_memRead),
    .io_outControlSignals_memWrite(EXpipe_io_outControlSignals_memWrite),
    .io_outRd(EXpipe_io_outRd),
    .io_outRs2(EXpipe_io_outRs2)
  );
  MEMpipe MEMpipe ( // @[top_MC.scala 40:26]
    .clock(MEMpipe_clock),
    .reset(MEMpipe_reset),
    .io_inControlSignals_memToReg(MEMpipe_io_inControlSignals_memToReg),
    .io_inControlSignals_regWrite(MEMpipe_io_inControlSignals_regWrite),
    .io_inRd(MEMpipe_io_inRd),
    .io_inMEMData(MEMpipe_io_inMEMData),
    .io_inALUResult(MEMpipe_io_inALUResult),
    .io_stall(MEMpipe_io_stall),
    .io_outMEMData(MEMpipe_io_outMEMData),
    .io_outControlSignals_memToReg(MEMpipe_io_outControlSignals_memToReg),
    .io_outControlSignals_regWrite(MEMpipe_io_outControlSignals_regWrite),
    .io_outRd(MEMpipe_io_outRd),
    .io_outALUResult(MEMpipe_io_outALUResult)
  );
  IF IF ( // @[top_MC.scala 43:19]
    .clock(IF_clock),
    .reset(IF_reset),
    .testHarness_InstructionMemorySetup_setup(IF_testHarness_InstructionMemorySetup_setup),
    .testHarness_InstructionMemorySetup_address(IF_testHarness_InstructionMemorySetup_address),
    .testHarness_InstructionMemorySetup_instruction(IF_testHarness_InstructionMemorySetup_instruction),
    .testHarness_PC(IF_testHarness_PC),
    .io_branchAddr(IF_io_branchAddr),
    .io_IFBarrierPC(IF_io_IFBarrierPC),
    .io_stall(IF_io_stall),
    .io_updatePrediction(IF_io_updatePrediction),
    .io_newBranch(IF_io_newBranch),
    .io_entryPC(IF_io_entryPC),
    .io_branchTaken(IF_io_branchTaken),
    .io_branchMispredicted(IF_io_branchMispredicted),
    .io_PCplus4ExStage(IF_io_PCplus4ExStage),
    .io_btbHit(IF_io_btbHit),
    .io_btbPrediction(IF_io_btbPrediction),
    .io_btbTargetPredict(IF_io_btbTargetPredict),
    .io_PC(IF_io_PC),
    .io_instruction_instruction(IF_io_instruction_instruction)
  );
  ID ID ( // @[top_MC.scala 44:19]
    .clock(ID_clock),
    .testHarness_registerSetup_setup(ID_testHarness_registerSetup_setup),
    .testHarness_registerSetup_readAddress(ID_testHarness_registerSetup_readAddress),
    .testHarness_registerSetup_writeEnable(ID_testHarness_registerSetup_writeEnable),
    .testHarness_registerSetup_writeData(ID_testHarness_registerSetup_writeData),
    .testHarness_registerPeek(ID_testHarness_registerPeek),
    .testHarness_testUpdates_writeEnable(ID_testHarness_testUpdates_writeEnable),
    .testHarness_testUpdates_writeData(ID_testHarness_testUpdates_writeData),
    .testHarness_testUpdates_writeAddress(ID_testHarness_testUpdates_writeAddress),
    .io_instruction_instruction(ID_io_instruction_instruction),
    .io_registerWriteAddress(ID_io_registerWriteAddress),
    .io_registerWriteData(ID_io_registerWriteData),
    .io_registerWriteEnable(ID_io_registerWriteEnable),
    .io_controlSignals_memToReg(ID_io_controlSignals_memToReg),
    .io_controlSignals_regWrite(ID_io_controlSignals_regWrite),
    .io_controlSignals_memRead(ID_io_controlSignals_memRead),
    .io_controlSignals_memWrite(ID_io_controlSignals_memWrite),
    .io_branchType(ID_io_branchType),
    .io_op1Select(ID_io_op1Select),
    .io_op2Select(ID_io_op2Select),
    .io_immType(ID_io_immType),
    .io_immData(ID_io_immData),
    .io_ALUop(ID_io_ALUop),
    .io_readData1(ID_io_readData1),
    .io_readData2(ID_io_readData2)
  );
  EX EX ( // @[top_MC.scala 45:19]
    .io_PC(EX_io_PC),
    .io_branchType(EX_io_branchType),
    .io_op1Select(EX_io_op1Select),
    .io_op2Select(EX_io_op2Select),
    .io_rs1(EX_io_rs1),
    .io_rs2(EX_io_rs2),
    .io_immData(EX_io_immData),
    .io_ALUop(EX_io_ALUop),
    .io_rs1Select(EX_io_rs1Select),
    .io_rs2Select(EX_io_rs2Select),
    .io_ALUresultEXB(EX_io_ALUresultEXB),
    .io_ALUresultMEMB(EX_io_ALUresultMEMB),
    .io_btbHit(EX_io_btbHit),
    .io_btbTargetPredict(EX_io_btbTargetPredict),
    .io_newBranch(EX_io_newBranch),
    .io_updatePrediction(EX_io_updatePrediction),
    .io_outPCplus4(EX_io_outPCplus4),
    .io_ALUResult(EX_io_ALUResult),
    .io_branchTarget(EX_io_branchTarget),
    .io_branchTaken(EX_io_branchTaken),
    .io_wrongAddrPred(EX_io_wrongAddrPred),
    .io_Rs2Forwarded(EX_io_Rs2Forwarded)
  );
  MEM MEM ( // @[top_MC.scala 46:19]
    .clock(MEM_clock),
    .reset(MEM_reset),
    .testHarness_DMEMpeek(MEM_testHarness_DMEMpeek),
    .io_dataIn(MEM_io_dataIn),
    .io_dataAddress(MEM_io_dataAddress),
    .io_writeEnable(MEM_io_writeEnable),
    .io_readEnable(MEM_io_readEnable),
    .io_dataOut(MEM_io_dataOut),
    .io_memBusy(MEM_io_memBusy)
  );
  HazardUnit HzdUnit ( // @[top_MC.scala 50:23]
    .io_branchType(HzdUnit_io_branchType),
    .io_controlSignalsEXB_memToReg(HzdUnit_io_controlSignalsEXB_memToReg),
    .io_controlSignalsEXB_regWrite(HzdUnit_io_controlSignalsEXB_regWrite),
    .io_controlSignalsMEMB_regWrite(HzdUnit_io_controlSignalsMEMB_regWrite),
    .io_rs1AddrIFB(HzdUnit_io_rs1AddrIFB),
    .io_rs1AddrIDB(HzdUnit_io_rs1AddrIDB),
    .io_rs2AddrIFB(HzdUnit_io_rs2AddrIFB),
    .io_rs2AddrIDB(HzdUnit_io_rs2AddrIDB),
    .io_rdAddrIDB(HzdUnit_io_rdAddrIDB),
    .io_rdAddrEXB(HzdUnit_io_rdAddrEXB),
    .io_rdAddrMEMB(HzdUnit_io_rdAddrMEMB),
    .io_branchTaken(HzdUnit_io_branchTaken),
    .io_btbPrediction(HzdUnit_io_btbPrediction),
    .io_wrongAddrPred(HzdUnit_io_wrongAddrPred),
    .io_membusy(HzdUnit_io_membusy),
    .io_branchMispredicted(HzdUnit_io_branchMispredicted),
    .io_stall(HzdUnit_io_stall),
    .io_stall_membusy(HzdUnit_io_stall_membusy),
    .io_flushE(HzdUnit_io_flushE),
    .io_flushD(HzdUnit_io_flushD),
    .io_rs1Select(HzdUnit_io_rs1Select),
    .io_rs2Select(HzdUnit_io_rs2Select)
  );
  assign testHarness_testReadouts_registerRead = ID_testHarness_registerPeek; // @[top_MC.scala 57:41]
  assign testHarness_testReadouts_DMEMread = MEM_testHarness_DMEMpeek; // @[top_MC.scala 58:41]
  assign testHarness_regUpdates_writeEnable = ID_testHarness_testUpdates_writeEnable; // @[top_MC.scala 61:41]
  assign testHarness_regUpdates_writeData = ID_testHarness_testUpdates_writeData; // @[top_MC.scala 61:41]
  assign testHarness_regUpdates_writeAddress = ID_testHarness_testUpdates_writeAddress; // @[top_MC.scala 61:41]
  assign testHarness_currentPC = IF_testHarness_PC; // @[top_MC.scala 63:41]
  assign IFpipe_clock = clock;
  assign IFpipe_reset = reset;
  assign IFpipe_io_inCurrentPC = IF_io_PC; // @[top_MC.scala 78:32]
  assign IFpipe_io_inInstruction_instruction = IF_io_instruction_instruction; // @[top_MC.scala 79:32]
  assign IFpipe_io_stall = HzdUnit_io_stall | HzdUnit_io_stall_membusy; // @[top_MC.scala 80:52]
  assign IFpipe_io_flush = HzdUnit_io_flushD; // @[top_MC.scala 81:32]
  assign IFpipe_io_inBTBHit = IF_io_btbHit; // @[top_MC.scala 82:32]
  assign IFpipe_io_inBTBPrediction = IF_io_btbPrediction; // @[top_MC.scala 83:32]
  assign IFpipe_io_inBTBTargetPredict = IF_io_btbTargetPredict; // @[top_MC.scala 84:32]
  assign IDpipe_clock = clock;
  assign IDpipe_reset = reset;
  assign IDpipe_io_inInstruction_instruction = ID_io_instruction_instruction; // @[top_MC.scala 92:32]
  assign IDpipe_io_inControlSignals_memToReg = ID_io_controlSignals_memToReg; // @[top_MC.scala 93:32]
  assign IDpipe_io_inControlSignals_regWrite = ID_io_controlSignals_regWrite; // @[top_MC.scala 93:32]
  assign IDpipe_io_inControlSignals_memRead = ID_io_controlSignals_memRead; // @[top_MC.scala 93:32]
  assign IDpipe_io_inControlSignals_memWrite = ID_io_controlSignals_memWrite; // @[top_MC.scala 93:32]
  assign IDpipe_io_inPC = IFpipe_io_outCurrentPC; // @[top_MC.scala 95:32]
  assign IDpipe_io_inBranchType = ID_io_branchType; // @[top_MC.scala 94:32]
  assign IDpipe_io_inOp1Select = ID_io_op1Select; // @[top_MC.scala 98:32]
  assign IDpipe_io_inOp2Select = ID_io_op2Select; // @[top_MC.scala 99:32]
  assign IDpipe_io_inImmData = ID_io_immData; // @[top_MC.scala 100:32]
  assign IDpipe_io_inRd = IFpipe_io_outInstruction_instruction[11:7]; // @[configuraton.scala 97:32]
  assign IDpipe_io_inALUop = ID_io_ALUop; // @[top_MC.scala 102:32]
  assign IDpipe_io_inReadData1 = ID_io_readData1; // @[top_MC.scala 103:32]
  assign IDpipe_io_inReadData2 = ID_io_readData2; // @[top_MC.scala 104:32]
  assign IDpipe_io_flush = HzdUnit_io_flushE; // @[top_MC.scala 96:32]
  assign IDpipe_io_stall = HzdUnit_io_stall_membusy; // @[top_MC.scala 97:32]
  assign IDpipe_io_inBTBHit = IFpipe_io_outBTBHit; // @[top_MC.scala 105:32]
  assign IDpipe_io_inBTBPrediction = IFpipe_io_outBTBPrediction; // @[top_MC.scala 106:32]
  assign IDpipe_io_inBTBTargetPredict = IFpipe_io_outBTBTargetPredict; // @[top_MC.scala 107:32]
  assign EXpipe_clock = clock;
  assign EXpipe_reset = reset;
  assign EXpipe_io_inControlSignals_memToReg = IDpipe_io_outControlSignals_memToReg; // @[top_MC.scala 145:31]
  assign EXpipe_io_inControlSignals_regWrite = IDpipe_io_outControlSignals_regWrite; // @[top_MC.scala 145:31]
  assign EXpipe_io_inControlSignals_memRead = IDpipe_io_outControlSignals_memRead; // @[top_MC.scala 145:31]
  assign EXpipe_io_inControlSignals_memWrite = IDpipe_io_outControlSignals_memWrite; // @[top_MC.scala 145:31]
  assign EXpipe_io_inRd = IDpipe_io_outRd; // @[top_MC.scala 146:31]
  assign EXpipe_io_inRs2 = EX_io_Rs2Forwarded; // @[top_MC.scala 147:31]
  assign EXpipe_io_inALUResult = EX_io_ALUResult; // @[top_MC.scala 144:31]
  assign EXpipe_io_stall = HzdUnit_io_stall_membusy; // @[top_MC.scala 148:31]
  assign MEMpipe_clock = clock;
  assign MEMpipe_reset = reset;
  assign MEMpipe_io_inControlSignals_memToReg = EXpipe_io_outControlSignals_memToReg; // @[top_MC.scala 157:31]
  assign MEMpipe_io_inControlSignals_regWrite = EXpipe_io_outControlSignals_regWrite; // @[top_MC.scala 157:31]
  assign MEMpipe_io_inRd = EXpipe_io_outRd; // @[top_MC.scala 159:31]
  assign MEMpipe_io_inMEMData = MEM_io_dataOut; // @[top_MC.scala 160:31]
  assign MEMpipe_io_inALUResult = EXpipe_io_outALUResult; // @[top_MC.scala 158:31]
  assign MEMpipe_io_stall = HzdUnit_io_stall_membusy; // @[top_MC.scala 161:31]
  assign IF_clock = clock;
  assign IF_reset = reset;
  assign IF_testHarness_InstructionMemorySetup_setup = testHarness_setupSignals_IMEMsignals_setup; // @[top_MC.scala 53:41]
  assign IF_testHarness_InstructionMemorySetup_address = testHarness_setupSignals_IMEMsignals_address; // @[top_MC.scala 53:41]
  assign IF_testHarness_InstructionMemorySetup_instruction = testHarness_setupSignals_IMEMsignals_instruction; // @[top_MC.scala 53:41]
  assign IF_io_branchAddr = EX_io_branchTarget; // @[top_MC.scala 73:28]
  assign IF_io_IFBarrierPC = IFpipe_io_outCurrentPC; // @[top_MC.scala 68:28]
  assign IF_io_stall = HzdUnit_io_stall | HzdUnit_io_stall_membusy; // @[top_MC.scala 69:48]
  assign IF_io_updatePrediction = EX_io_updatePrediction; // @[top_MC.scala 71:28]
  assign IF_io_newBranch = EX_io_newBranch; // @[top_MC.scala 70:28]
  assign IF_io_entryPC = IDpipe_io_outPC; // @[top_MC.scala 72:28]
  assign IF_io_branchTaken = EX_io_branchTaken; // @[top_MC.scala 67:28]
  assign IF_io_branchMispredicted = HzdUnit_io_branchMispredicted; // @[top_MC.scala 74:28]
  assign IF_io_PCplus4ExStage = EX_io_outPCplus4; // @[top_MC.scala 75:28]
  assign ID_clock = clock;
  assign ID_testHarness_registerSetup_setup = testHarness_setupSignals_registerSignals_setup; // @[top_MC.scala 54:41]
  assign ID_testHarness_registerSetup_readAddress = testHarness_setupSignals_registerSignals_readAddress; // @[top_MC.scala 54:41]
  assign ID_testHarness_registerSetup_writeEnable = testHarness_setupSignals_registerSignals_writeEnable; // @[top_MC.scala 54:41]
  assign ID_testHarness_registerSetup_writeData = testHarness_setupSignals_registerSignals_writeData; // @[top_MC.scala 54:41]
  assign ID_io_instruction_instruction = IFpipe_io_outInstruction_instruction; // @[top_MC.scala 87:31]
  assign ID_io_registerWriteAddress = MEMpipe_io_outRd; // @[top_MC.scala 88:31]
  assign ID_io_registerWriteData = MEMpipe_io_outControlSignals_memToReg ? MEMpipe_io_outMEMData :
    MEMpipe_io_outALUResult; // @[top_MC.scala 165:46 166:19 168:19]
  assign ID_io_registerWriteEnable = MEMpipe_io_outControlSignals_regWrite; // @[top_MC.scala 89:31]
  assign EX_io_PC = IDpipe_io_outPC; // @[top_MC.scala 112:31]
  assign EX_io_branchType = IDpipe_io_outBranchType; // @[top_MC.scala 113:31]
  assign EX_io_op1Select = IDpipe_io_outOp1Select; // @[top_MC.scala 114:31]
  assign EX_io_op2Select = IDpipe_io_outOp2Select; // @[top_MC.scala 115:31]
  assign EX_io_rs1 = IDpipe_io_outReadData1; // @[top_MC.scala 118:31]
  assign EX_io_rs2 = IDpipe_io_outReadData2; // @[top_MC.scala 119:31]
  assign EX_io_immData = IDpipe_io_outImmData; // @[top_MC.scala 120:31]
  assign EX_io_ALUop = IDpipe_io_outALUop; // @[top_MC.scala 121:31]
  assign EX_io_rs1Select = HzdUnit_io_rs1Select; // @[top_MC.scala 116:31]
  assign EX_io_rs2Select = HzdUnit_io_rs2Select; // @[top_MC.scala 117:31]
  assign EX_io_ALUresultEXB = EXpipe_io_outALUResult; // @[top_MC.scala 122:31]
  assign EX_io_ALUresultMEMB = MEMpipe_io_outControlSignals_memToReg ? MEMpipe_io_outMEMData : MEMpipe_io_outALUResult; // @[top_MC.scala 165:46 166:19 168:19]
  assign EX_io_btbHit = IDpipe_io_outBTBHit; // @[top_MC.scala 124:31]
  assign EX_io_btbTargetPredict = IDpipe_io_outBTBTargetPredict; // @[top_MC.scala 125:31]
  assign MEM_clock = clock;
  assign MEM_reset = reset;
  assign MEM_io_dataIn = EXpipe_io_outRs2; // @[top_MC.scala 151:31]
  assign MEM_io_dataAddress = EXpipe_io_outALUResult; // @[top_MC.scala 152:31]
  assign MEM_io_writeEnable = EXpipe_io_outControlSignals_memWrite; // @[top_MC.scala 153:31]
  assign MEM_io_readEnable = EXpipe_io_outControlSignals_memRead; // @[top_MC.scala 154:31]
  assign HzdUnit_io_branchType = IDpipe_io_outBranchType; // @[top_MC.scala 140:33]
  assign HzdUnit_io_controlSignalsEXB_memToReg = EXpipe_io_outControlSignals_memToReg; // @[top_MC.scala 128:33]
  assign HzdUnit_io_controlSignalsEXB_regWrite = EXpipe_io_outControlSignals_regWrite; // @[top_MC.scala 128:33]
  assign HzdUnit_io_controlSignalsMEMB_regWrite = MEMpipe_io_outControlSignals_regWrite; // @[top_MC.scala 129:33]
  assign HzdUnit_io_rs1AddrIFB = {{27'd0}, IFpipe_io_outInstruction_instruction[19:15]}; // @[top_MC.scala 130:33]
  assign HzdUnit_io_rs1AddrIDB = {{27'd0}, IDpipe_io_outInstruction_instruction[19:15]}; // @[top_MC.scala 132:33]
  assign HzdUnit_io_rs2AddrIFB = {{27'd0}, IFpipe_io_outInstruction_instruction[24:20]}; // @[top_MC.scala 131:33]
  assign HzdUnit_io_rs2AddrIDB = {{27'd0}, IDpipe_io_outInstruction_instruction[24:20]}; // @[top_MC.scala 133:33]
  assign HzdUnit_io_rdAddrIDB = {{27'd0}, IDpipe_io_outInstruction_instruction[11:7]}; // @[top_MC.scala 134:33]
  assign HzdUnit_io_rdAddrEXB = {{27'd0}, EXpipe_io_outRd}; // @[top_MC.scala 135:33]
  assign HzdUnit_io_rdAddrMEMB = {{27'd0}, MEMpipe_io_outRd}; // @[top_MC.scala 136:33]
  assign HzdUnit_io_branchTaken = EX_io_branchTaken; // @[top_MC.scala 137:33]
  assign HzdUnit_io_btbPrediction = IDpipe_io_outBTBPrediction; // @[top_MC.scala 139:33]
  assign HzdUnit_io_wrongAddrPred = EX_io_wrongAddrPred; // @[top_MC.scala 138:33]
  assign HzdUnit_io_membusy = MEM_io_memBusy; // @[top_MC.scala 141:33]
endmodule
module RISCV_TOP(
  input         clock,
  input         reset,
  output [31:0] io_PC,
  input         io_setup,
  input  [31:0] io_IMEMWriteData,
  input  [31:0] io_IMEMAddr,
  input  [31:0] io_DMEMWriteData,
  input  [31:0] io_DMEMAddr,
  input         io_DMEMWriteEnable,
  output [31:0] io_DMEMReadData,
  input         io_DMEMReadEnable,
  input  [31:0] io_regsWriteData,
  input  [4:0]  io_regsAddr,
  input         io_regsWriteEnable,
  output [31:0] io_regsReadData,
  output        io_regsDeviceWriteEnable,
  output [31:0] io_regsDeviceWriteData,
  output [4:0]  io_regsDeviceWriteAddress,
  output        io_memDeviceWriteEnable,
  output [31:0] io_memDeviceWriteData,
  output [31:0] io_memDeviceWriteAddress
);
  wire  top_MC_clock; // @[RISCV_TOP.scala 53:22]
  wire  top_MC_reset; // @[RISCV_TOP.scala 53:22]
  wire  top_MC_testHarness_setupSignals_IMEMsignals_setup; // @[RISCV_TOP.scala 53:22]
  wire [31:0] top_MC_testHarness_setupSignals_IMEMsignals_address; // @[RISCV_TOP.scala 53:22]
  wire [31:0] top_MC_testHarness_setupSignals_IMEMsignals_instruction; // @[RISCV_TOP.scala 53:22]
  wire  top_MC_testHarness_setupSignals_registerSignals_setup; // @[RISCV_TOP.scala 53:22]
  wire [4:0] top_MC_testHarness_setupSignals_registerSignals_readAddress; // @[RISCV_TOP.scala 53:22]
  wire  top_MC_testHarness_setupSignals_registerSignals_writeEnable; // @[RISCV_TOP.scala 53:22]
  wire [31:0] top_MC_testHarness_setupSignals_registerSignals_writeData; // @[RISCV_TOP.scala 53:22]
  wire [31:0] top_MC_testHarness_testReadouts_registerRead; // @[RISCV_TOP.scala 53:22]
  wire [31:0] top_MC_testHarness_testReadouts_DMEMread; // @[RISCV_TOP.scala 53:22]
  wire  top_MC_testHarness_regUpdates_writeEnable; // @[RISCV_TOP.scala 53:22]
  wire [31:0] top_MC_testHarness_regUpdates_writeData; // @[RISCV_TOP.scala 53:22]
  wire [4:0] top_MC_testHarness_regUpdates_writeAddress; // @[RISCV_TOP.scala 53:22]
  wire [31:0] top_MC_testHarness_currentPC; // @[RISCV_TOP.scala 53:22]
  top_MC top_MC ( // @[RISCV_TOP.scala 53:22]
    .clock(top_MC_clock),
    .reset(top_MC_reset),
    .testHarness_setupSignals_IMEMsignals_setup(top_MC_testHarness_setupSignals_IMEMsignals_setup),
    .testHarness_setupSignals_IMEMsignals_address(top_MC_testHarness_setupSignals_IMEMsignals_address),
    .testHarness_setupSignals_IMEMsignals_instruction(top_MC_testHarness_setupSignals_IMEMsignals_instruction),
    .testHarness_setupSignals_registerSignals_setup(top_MC_testHarness_setupSignals_registerSignals_setup),
    .testHarness_setupSignals_registerSignals_readAddress(top_MC_testHarness_setupSignals_registerSignals_readAddress),
    .testHarness_setupSignals_registerSignals_writeEnable(top_MC_testHarness_setupSignals_registerSignals_writeEnable),
    .testHarness_setupSignals_registerSignals_writeData(top_MC_testHarness_setupSignals_registerSignals_writeData),
    .testHarness_testReadouts_registerRead(top_MC_testHarness_testReadouts_registerRead),
    .testHarness_testReadouts_DMEMread(top_MC_testHarness_testReadouts_DMEMread),
    .testHarness_regUpdates_writeEnable(top_MC_testHarness_regUpdates_writeEnable),
    .testHarness_regUpdates_writeData(top_MC_testHarness_regUpdates_writeData),
    .testHarness_regUpdates_writeAddress(top_MC_testHarness_regUpdates_writeAddress),
    .testHarness_currentPC(top_MC_testHarness_currentPC)
  );
  assign io_PC = top_MC_testHarness_currentPC; // @[RISCV_TOP.scala 55:9]
  assign io_DMEMReadData = top_MC_testHarness_testReadouts_DMEMread; // @[RISCV_TOP.scala 73:19]
  assign io_regsReadData = top_MC_testHarness_testReadouts_registerRead; // @[RISCV_TOP.scala 74:19]
  assign io_regsDeviceWriteEnable = top_MC_testHarness_regUpdates_writeEnable; // @[RISCV_TOP.scala 77:29]
  assign io_regsDeviceWriteData = top_MC_testHarness_regUpdates_writeData; // @[RISCV_TOP.scala 78:29]
  assign io_regsDeviceWriteAddress = top_MC_testHarness_regUpdates_writeAddress; // @[RISCV_TOP.scala 76:29]
  assign io_memDeviceWriteEnable = 1'h0; // @[RISCV_TOP.scala 81:29]
  assign io_memDeviceWriteData = 32'h0; // @[RISCV_TOP.scala 82:29]
  assign io_memDeviceWriteAddress = 32'h0; // @[RISCV_TOP.scala 80:29]
  assign top_MC_clock = clock;
  assign top_MC_reset = reset;
  assign top_MC_testHarness_setupSignals_IMEMsignals_setup = io_setup; // @[RISCV_TOP.scala 59:47]
  assign top_MC_testHarness_setupSignals_IMEMsignals_address = io_IMEMAddr; // @[RISCV_TOP.scala 57:47]
  assign top_MC_testHarness_setupSignals_IMEMsignals_instruction = io_IMEMWriteData; // @[RISCV_TOP.scala 58:47]
  assign top_MC_testHarness_setupSignals_registerSignals_setup = io_setup; // @[RISCV_TOP.scala 71:52]
  assign top_MC_testHarness_setupSignals_registerSignals_readAddress = io_regsAddr; // @[RISCV_TOP.scala 67:52]
  assign top_MC_testHarness_setupSignals_registerSignals_writeEnable = io_regsWriteEnable; // @[RISCV_TOP.scala 68:52]
  assign top_MC_testHarness_setupSignals_registerSignals_writeData = io_regsWriteData; // @[RISCV_TOP.scala 70:52]
endmodule
