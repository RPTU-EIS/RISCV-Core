
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import InstructionMemory._
import chisel3._


class InstructionMemory_tb extends AnyFlatSpec with ChiselScalatestTester {
  "InstructionMemory" should "pass" in {
    test(new InstructionMemory("../../main/scala/InstructionMemory/instructions.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

    }
  }
}

