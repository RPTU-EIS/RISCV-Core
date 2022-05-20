
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import bool._
import chisel3._


class bool_tb extends AnyFlatSpec with ChiselScalatestTester {
  "bool" should "pass" in {
    test(new bool).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.io.brCond_true.poke(false.B)
      dut.io.is_IF.poke(false.B)
      dut .io.PC_WE.expect(false.B)

      dut.clock.step()

      dut.io.brCond_true.poke(false.B)
      dut.io.is_IF.poke(true.B)
      dut .io.PC_WE.expect(true.B)

      dut.clock.step()

      dut.io.brCond_true.poke(true.B)
      dut.io.is_IF.poke(true.B)
      dut .io.PC_WE.expect(true.B)

      dut.clock.step()

      dut.io.brCond_true.poke(true.B)
      dut.io.is_IF.poke(false.B)
      dut .io.PC_WE.expect(true.B)

      dut.clock.step()
    }
  }
}
