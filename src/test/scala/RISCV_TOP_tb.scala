package RISCV_TOP_tb
import RISCV_TOP.RISCV_TOP
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import top_MC._
import chisel3._
import DataTypes.Data._
import java.sql.Driver

class RISCV_TOP_tb extends AnyFlatSpec with ChiselScalatestTester {

  "RISCV_TOP" should "pass" in {
    test(new RISCV_TOP).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
    for(i <- 0 until 100){
    dut.clock.step()
  }

      dut.io.setup.poke(1.B)
      dut.clock.step()
      disableTestSignals
      for(i <- 0 until 10){
      dut.clock.step()
      }

      dut.io.setup.poke(0.B)

    def disableTestSignals: Unit = {
    dut.io.setup.poke(1.B)
    dut.io.DMEMWriteData.poke(0.U)
    dut.io.DMEMAddr.poke(0.U)
    dut.io.DMEMWriteEnable.poke(0.B)
    dut.io.regsWriteData.poke(0.U)
    dut.io.regsAddr.poke(0.U)
    dut.io.regsWriteEnable.poke(0.B)
    dut.io.IMEMWriteData.poke(0.U)
    dut.io.IMEMAddr.poke(4092.U)
  }

  }

  }
}
