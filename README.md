# RISC-V Pipelined Project in Chisel

This project implements a pipelined RISC-V processor in Chisel. The pipeline includes five stages: fetch, decode, execute, memory, and writeback.

The core is part of an educational project by the [Chair of Electronic Design Automation](https://eit.rptu.de/fgs/eis/) at RPTU in Kaiserslautern, Germany.

## Dependencies

The project requires the following dependencies to be installed:

- Scala 2.12.13
- Chisel 3.5

## Usage

To run the project, simply run the following command in your terminal:

`sbt run`


This will generate Verilog code for the pipelined processor.

To run the testbench, simply run the following command in your terminal:

`sbt test`

This will simulate it using the included testbench.

Note that testHarness variables are only for testing purposes and are not necessary for the project structure.

## Tools

GTKWave can be used for debugging or to verify the results.

## Structure

The project is structured as follows:

- `RISCV_TOP.scala`: The main Chisel file containing the pipelined RISC-V processor.
- `RISC_TOP_tb.scala`: A ScalaTest spec that tests the pipelined processor.

## Participants and Collaborators

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel

Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava
