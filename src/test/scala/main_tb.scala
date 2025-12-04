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
      for(i <- 0 until 300){//add 1150, beq 800, bge  800, bgeu 850, blt 720, bltu 760, bne 730
      //add pc 373->379,380.... end success, 374 fail
      //beq pc 239->245,246.... end success, 242 fail
      //bge pc 263->269,270.... end success, 264 fail
      //bgeu pc 276->282,283.... end success, 277 fail
      //blt pc 239->245,246.... end success, 240 fail
      //bltu pc 252->258,259.... end success, 253 fail
      //bne pc 240->246,247.... end success, 241 fail
        dut.clock.step()
      }

    }

  }
}