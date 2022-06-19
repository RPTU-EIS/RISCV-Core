
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import PC._
import chisel3._


class PC_tb extends AnyFlatSpec with ChiselScalatestTester {
  "PC" should "pass" in {
    test(new PC).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.io.PC_next.poke(20.U(32.W)) // write 5 to reg 15
      dut.io.WE.poke(true.B)

      dut.clock.step()

      dut.io.WE.poke(false.B)
      dut.io.PC_current.expect(20.U(32.W))

      dut.clock.step()

      dut.io.PC_current.expect(20.U(32.W))

      dut.reset.poke(true.B)
      dut.clock.step()
      dut.io.PC_current.expect(0.U(32.W))
    }
  }
}