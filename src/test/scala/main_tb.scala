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
    //test(new RISCV_TOP("src/test/programs/test0")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
    test(new RISCV_TOP("src/main/resources/mem.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)
      for(i <- 0 until 1130){
        dut.clock.step()
      }

    }

  }
}
