
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import InstructionMemory._
import chisel3._

class InstructionMemory_tb extends AnyFlatSpec with ChiselScalatestTester {
  "InstructionMemory" should "pass" in {
    test(new InstructionMemory("src/main/scala/InstructionMemory/instructions")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.io.addr.poke(0.U(32.W))
      dut.io.instr.expect(0.U(32.W))
      dut.clock.step()

      dut.io.addr.poke(4.U(32.W))
      dut.io.instr.expect(1.U(32.W))
      dut.clock.step()

      dut.io.addr.poke(8.U(32.W))
      dut.io.instr.expect(2.U(32.W))
      dut.clock.step()

      dut.io.addr.poke(12.U(32.W))
      dut.io.instr.expect(3.U(32.W))
      dut.clock.step()

      dut.io.addr.poke(16.U(32.W))
      dut.io.instr.expect(4.U(32.W))
      dut.clock.step()

      dut.io.addr.poke(20.U(32.W))
      dut.io.instr.expect(5.U(32.W))
      dut.clock.step()

      dut.io.addr.poke(24.U(32.W))
      dut.io.instr.expect(6.U(32.W))
      dut.clock.step()

      dut.io.addr.poke(28.U(32.W))
      dut.io.instr.expect(7.U(32.W))
      dut.clock.step()

      dut.io.addr.poke(32.U(32.W))
      dut.io.instr.expect(8.U(32.W))
      dut.clock.step()

      dut.io.addr.poke(36.U(32.W))
      dut.io.instr.expect(9.U(32.W))
      dut.clock.step()
    }
  }
}

