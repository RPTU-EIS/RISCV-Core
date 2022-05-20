
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import GPR._
import chisel3._


class RegisterFile_tb extends AnyFlatSpec with ChiselScalatestTester {
  "GPR" should "pass" in {
    test(new registerFile).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.io.rd.poke(15.U(5.W)) // write 5 to reg 15
      dut.io.data_in.poke(5.U(32.W))
      dut.io.WE.poke(true.B)

      dut.clock.step()

      dut.io.WE.poke(false.B)  // check read port 1
      dut.io.rs1.poke(15.U(5.W))
      dut.io.data_A.expect(5.U(32.W))

      dut.clock.step()        // check write enable
      dut.io.WE.poke(false.B)
      dut.io.rd.poke(15.U(5.W)) //
      dut.io.data_in.poke(25.U(32.W))


      dut.clock.step()
      dut.io.rs2.poke(15.U(5.W))
      dut.io.data_B.expect(5.U(32.W))

      dut.clock.step()
      dut.io.rd.poke(0.U(5.W)) //    Reg 0 hardwired to 0
      dut.io.data_in.poke(5.U(32.W))
      dut.io.WE.poke(true.B)

      dut.clock.step()
      dut.io.WE.poke(false.B)
      dut.io.rs2.poke(0.U(5.W))
      dut.io.data_B.expect(0.U(32.W))

    }
  }
}
