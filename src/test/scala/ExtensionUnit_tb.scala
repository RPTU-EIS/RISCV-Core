
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import ExtenionUnit._
import config.ExtensionCases._
import chisel3._

class ExtensionUnit_tb extends AnyFlatSpec with ChiselScalatestTester {
  "ExtenionUnit" should "pass" in {
    test(new ExtenionUnit).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.io.instr.poke(0.U(32.W))            // check ID
      dut.io.ext_type(id)
      dut.io.ext_imm.expect(0.U(32.W))

      dut.clock.step()
    }
  }
}