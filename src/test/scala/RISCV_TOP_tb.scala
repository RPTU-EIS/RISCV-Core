package RISCV_TOP_tb
import RISCV_TOP.RISCV_TOP
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import top_MC._
import chisel3._

import java.sql.Driver

class RISCV_TOP_tb extends AnyFlatSpec with ChiselScalatestTester {
  "RISCV_TOP" should "pass" in {
    test(new RISCV_TOP).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      for(i <- 0 until 10){
        dut.clock.step()
      }
      dut.io.setup.poke(1.B)
      dut.io.DMEMWriteData.poke(0.U)
      dut.io.DMEMAddress.poke(0.U)
      dut.io.DMEMWriteEnable.poke(0.B)
      dut.io.regsWriteData.poke(0.U)
      dut.io.regsAddress.poke(0.U)
      dut.io.regsWriteEnable.poke(0.B)
      dut.io.IMEMWriteData.poke(0.U)
      dut.io.IMEMAddress.poke(0.U)
      dut.clock.step()
      dut.io.setup.poke(0.B)
      dut.clock.step()
      dut.io.regsWriteEnable.poke(1.B)
      dut.io.regsWriteData.poke(2.U)
      dut.io.regsAddress.poke(1.U)
      dut.clock.step()
      dut.io.regsWriteEnable.poke(0.B)
      dut.io.regsWriteData.poke(0.U)
      dut.io.regsAddress.poke(1.U)
      for(i <- 0 until 10){
        dut.clock.step()
      }

    }

  }
}