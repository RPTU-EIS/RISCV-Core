
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import top_MC._
import chisel3._
//import config.ALUOps._
//import config.ExtensionCases._
//import config.States._

import java.sql.Driver

class top_MC_tb extends AnyFlatSpec with ChiselScalatestTester {
  "top_MC" should "pass" in {
    test(new top_MC).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      for(i <- 0 until 200){
        dut.clock.step()
      }
    }
  }
}
