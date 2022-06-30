
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import ExtenionUnit._
import chisel3._
import config.ExtensionCases._

class ExtensionUnit_tb extends AnyFlatSpec with ChiselScalatestTester {
  "ExtenionUnit" should "pass" in {
    test(new ExtenionUnit).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.io.instr.poke("hf5000ac0".U(32.W))            // check ID
      dut.io.ext_type.poke(id)
      dut.io.ext_imm.expect("hFFFFFF50".U(32.W))

      dut.clock.step()

      dut.io.ext_type.poke(jal)
      dut.io.ext_imm.expect("hfff0074c".U(32.W))

      dut.clock.step()

      dut.io.ext_type.poke(jalr)
      dut.io.ext_imm.expect("hfffffe9c".U(32.W))

      dut.clock.step()

      dut.io.ext_type.poke(auipc)
      dut.io.ext_imm.expect("hf5000000".U(32.W))

      dut.clock.step()

      dut.io.ext_type.poke(store)
//      dut.io.ext_imm.expect("hffffff40".U(32.W))

      dut.clock.step()

      dut.io.ext_type.poke(i_type)
      dut.io.ext_imm.expect("hffffff50".U(32.W))

    }
  }
}