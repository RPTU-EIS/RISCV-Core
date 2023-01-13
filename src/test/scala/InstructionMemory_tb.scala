//
//import chiseltest._
//import org.scalatest.flatspec.AnyFlatSpec
//import InstructionMemory._
//import chisel3._
//
//class InstructionMemory_tb extends AnyFlatSpec with ChiselScalatestTester {
//  "InstructionMemory" should "pass" in {
//    test(new InstructionMemory("src/main/scala/InstructionMemory/instructions")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
//
//      dut.io.addr.poke(0.U(32.W))
//      dut.io.instr.expect("h00a54533".U(32.W))
//      dut.clock.step()
//
//      dut.io.addr.poke(4.U(32.W))
//      dut.io.instr.expect("h00150593".U(32.W))
//      dut.clock.step()
//
//    }
//  }
//}
//
