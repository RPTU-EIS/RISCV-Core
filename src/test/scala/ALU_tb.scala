
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import ALU._
import chisel3._

class ALU_tb extends AnyFlatSpec with ChiselScalatestTester
{
  "ADDER" should "pass" in {
    test(new add_subtractor).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.io.src1.poke(15.U(32.W)) // 15 + 5 = 20
      dut.io.src2.poke(5.U(32.W))
      dut.io.sub.poke(false.B)

      dut.io.res.expect(20.U(32.W))
      dut.io.ovf.expect(0.U(1.W))
      dut.io.c_out.expect(0.U(32.W))

      dut.clock.step()

      dut.io.src1.poke(15.U(32.W)) // 15 - 5 = 10
      dut.io.src2.poke(5.U(32.W))
      dut.io.sub.poke(true.B)

      dut.io.res.expect(10.U(32.W))
      dut.io.ovf.expect(0.U(1.W))
      dut.io.c_out.expect(1.U(32.W))

      dut.clock.step()

      dut.io.src1.poke("h7FFFFFFF".U(32.W)) // 15 - 5 = 10
      dut.io.src2.poke(1.U(32.W))
      dut.io.sub.poke(false.B)

      dut.io.res.expect("h80000000".U(32.W))
      dut.io.ovf.expect(1.U(1.W))
      dut.io.c_out.expect(0.U(32.W))

      dut.clock.step()
    }
  }

  "ALU" should "pass" in {
    test(new ALU).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.io.ALUop.poke(0.U(4.W))     // Addition
      dut.io.src1.poke(15.U(32.W))
      dut.io.src2.poke(5.U(32.W))
      dut.io.aluRes.expect(20.U(32.W))

      dut.clock.step()

      dut.io.ALUop.poke(9.U(4.W))     // Subtraction
      dut.io.aluRes.expect(10.U(32.W))

      dut.clock.step()

      dut.io.ALUop.poke(7.U(4.W))     // SLT, SLTI, BLT True
      dut.io.src1.poke("hffffffff".U(32.W))
      dut.io.src2.poke(15.U(32.W))
      dut.io.aluRes.expect(1.U(32.W))

      dut.clock.step()

      dut.io.ALUop.poke(7.U(4.W))     // SLT, SLTI, BLT False
      dut.io.src1.poke(15.U(32.W))
      dut.io.src2.poke(5.U(32.W))
      dut.io.aluRes.expect(0.U(32.W))

      dut.clock.step()

      dut.io.ALUop.poke(8.U(4.W))     // SLTU, SLTIU, BLTU True
      dut.io.src1.poke(5.U(32.W))
      dut.io.src2.poke(15.U(32.W))
      dut.io.aluRes.expect(1.U(32.W))

      dut.clock.step()

      dut.io.ALUop.poke(8.U(4.W))     // SLTU, SLTIU, BLTU False
      dut.io.src1.poke(15.U(32.W))
      dut.io.src2.poke(5.U(32.W))
      dut.io.aluRes.expect(0.U(32.W))

      dut.clock.step()

      dut.io.ALUop.poke(10.U(4.W))     // BEQ true
      dut.io.src1.poke(5.U(32.W))
      dut.io.src2.poke(5.U(32.W))
      dut.io.aluRes.expect(1.U(32.W))

      dut.clock.step()

      dut.io.ALUop.poke(10.U(4.W))     // BEQ false
      dut.io.src1.poke(15.U(32.W))
      dut.io.src2.poke(5.U(32.W))
      dut.io.aluRes.expect(0.U(32.W))

      dut.clock.step()

      dut.io.ALUop.poke(11.U(4.W))     // BNEQ false
      dut.io.src1.poke(5.U(32.W))
      dut.io.src2.poke(5.U(32.W))
      dut.io.aluRes.expect(0.U(32.W))

      dut.clock.step()

      dut.io.ALUop.poke(11.U(4.W))     // BNEQ true
      dut.io.src1.poke(15.U(32.W))
      dut.io.src2.poke(5.U(32.W))
      dut.io.aluRes.expect(1.U(32.W))

      dut.clock.step()

      dut.io.ALUop.poke(13.U(4.W))     // BGE true
      dut.io.src1.poke(15.U(32.W))
      dut.io.src2.poke(5.U(32.W))
      dut.io.aluRes.expect(1.U(32.W))

      dut.clock.step()

      dut.io.ALUop.poke(13.U(4.W))     // BGE false
      dut.io.src1.poke(15.U(32.W))
      dut.io.src2.poke(50.U(32.W))
      dut.io.aluRes.expect(0.U(32.W))

      dut.clock.step()

      dut.io.ALUop.poke(13.U(4.W))     // BGEU true
      dut.io.src1.poke(15.U(32.W))
      dut.io.src2.poke(5.U(32.W))
      dut.io.aluRes.expect(1.U(32.W))

      dut.clock.step()

      dut.io.ALUop.poke(13.U(4.W))     // BGEU false
      dut.io.src1.poke(15.U(32.W))
      dut.io.src2.poke(50.U(32.W))
      dut.io.aluRes.expect(0.U(32.W))

      dut.clock.step()

      dut.io.src1.poke(1.U(32.W))
      dut.io.src2.poke(2.U(32.W))

      dut.io.ALUop.poke(4.U(4.W))     // or
      dut.io.aluRes.expect(3.U(32.W))

      dut.clock.step()
      dut.io.ALUop.poke(5.U(4.W))     // and
      dut.io.aluRes.expect(0.U(32.W))

      dut.clock.step()
      dut.io.ALUop.poke(6.U(4.W))     // xor
      dut.io.aluRes.expect(3.U(32.W))

      dut.clock.step()

      dut.io.src1.poke("hffffffff".U(32.W))
      dut.io.src2.poke(4.U(32.W))

      dut.io.ALUop.poke(1.U(4.W))                   // SLL
      dut.io.aluRes.expect("hfffffff0".U(32.W))

      dut.clock.step()
      dut.io.ALUop.poke(2.U(4.W))                   // SRL
      dut.io.aluRes.expect("h0fffffff".U(32.W))

      dut.clock.step()
      dut.io.ALUop.poke(3.U(4.W))                   // SRA
      dut.io.aluRes.expect("hffffffff".U(32.W))

    }
  }
}