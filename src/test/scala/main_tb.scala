package main_tb
import RISCV_TOP.RISCV_TOP
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import top_MC._
import chisel3._
import DataTypes.Data._
import java.sql.Driver

class main_tb extends AnyFlatSpec with ChiselScalatestTester {

  "main_tb" should "pass" in {
    test(new RISCV_TOP("src/test/programs/test0")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      for(i <- 0 until 200){
        dut.clock.step()
      }

    }

  }
}
