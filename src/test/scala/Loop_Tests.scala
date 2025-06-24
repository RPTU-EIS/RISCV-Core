package main_tb

import RISCV_TOP.RISCV_TOP
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import top_MC._
import chisel3._
import DataTypes.Data._
import java.sql.Driver

class loop_tb extends AnyFlatSpec with ChiselScalatestTester {

  "Loop_Tests" should "pass" in {
    test(new RISCV_TOP("src/test/programs/Loop_Test_0")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      for(i <- 0 until 200){
        dut.clock.step()
      }

    }

  }
}