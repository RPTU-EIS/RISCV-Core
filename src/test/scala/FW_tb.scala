import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import FW._
import chisel3._

class FW_tb extends AnyFlatSpec with ChiselScalatestTester {
  "Forwarding" should "pass" in {
    test(new FW).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.io.ID_EXRs1.poke(15.U(5.W))
      dut.io.ID_EXRs2.poke(15.U(5.W))
      dut.io.EX_MEMRegWrite.poke(0.U(1.W))
      dut.io.MEM_WBRegRd.poke(15.U(5.W))

      dut.clock.step()
      dut.io.MEM_WBRegWrite.poke(1.U(1.W))
      dut.io.EX_MEMRegRd.poke(15.U(5.W))
      dut.io.Fwd_A.expect(1.U(2.W))
      dut.io.Fwd_B.expect(1.U(2.W))

      dut.clock.step()
      dut.io.EX_MEMRegWrite.poke(1.U(5.W))
      dut.io.MEM_WBRegWrite.poke(0.U(1.W))
      dut.io.ID_EXRs2.poke(1.U(5.W))
      dut.io.Fwd_A.expect(2.U(2.W))
      dut.io.Fwd_B.expect(0.U(2.W))

      dut.clock.step()
      dut.io.ID_EXRs1.poke(1.U(5.W))
      dut.io.Fwd_A.expect(0.U(2.W))
      dut.io.Fwd_B.expect(0.U(2.W))

      dut.clock.step()
      dut.io.EX_MEMRegRd.poke(1.U(5.W))
      dut.io.Fwd_A.expect(2.U(2.W))
      dut.io.Fwd_B.expect(2.U(2.W))

      dut.clock.step()
      dut.io.ID_EXRs2.poke(15.U(5.W))
      dut.io.MEM_WBRegWrite.poke(1.U(1.W))
      dut.io.Fwd_A.expect(2.U(2.W))
      dut.io.Fwd_B.expect(1.U(2.W))
    }
  }
}
