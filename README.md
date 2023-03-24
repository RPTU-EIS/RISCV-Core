# RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel, a hardware construction language embedded in Scala. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.

## Dependencies

The project requires the following dependencies to be installed:

- Scala 2.12.13
- Chisel 3.5

## Usage

To run the project, simply run the following command in your terminal:

sbt run


This will generate Verilog code for the pipelined processor and simulate it using the included testbench.

## Structure

The project is structured as follows:


src/
|-- main/
|   |-- scala/
|   |   |-- ALU/
|   |   |-- Branch_OP/
|   |   |-- config/
|   |   |-- DataMemory/
|   |   |-- Decoder/
|   |   |-- FW/
|   |   |-- GPR/
|   |   |-- InstructionMemory/
|   |   |-- Pipeline_Regs/
|   |   |-- RISCV_TOP/
|   |   |-- Stage_EX/
|   |   |-- Stage_ID/
|   |   |-- Stage_IF/
|   |   |-- Stage_MEM/
|   |   |-- top_MC/
|
|-- test/
|   |-- resources.tests/
|   |-- scala/
|   |   |-- RISC_TOP_tb/


- `RISCV_TOP.scala`: The main Chisel file containing the pipelined RISC-V processor.
- `RISC_TOP_tb.scala`: A ScalaTest spec that tests the pipelined processor.

## Future Work

Future work for this project could include adding brancg predictor instead of stalling the pipeline for a better performance of RISC-V instructions, optimizing the pipeline for better performance, or implementing additional features such as out-of-order execution. 

