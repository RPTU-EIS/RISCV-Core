package RISCV_TOP_tb
import RISCV_TOP.RISCV_TOP
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import top_MC._
import chisel3._
import DataTypes.Data._
import java.sql.Driver

class RISCV_TOP_tb extends AnyFlatSpec with ChiselScalatestTester {

  val initDMem = DMem(settings).repr.toList
  val initRegs = Regs(settings).repr.toList
  "RISCV_TOP" should "pass" in {
    test(new RISCV_TOP).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      for(i <- 0 until 10){
        dut.clock.step()
      }

      dut.io.setup.poke(1.B)
      setupImem(instructions)
      setupRegs(initRegs)
      setupDmem(initDMem)
      dut.clock.step()
      disableTestSignals
      for(i <- 0 until 10){
        dut.clock.step()
      }
      dut.io.setup.poke(0.B)
      def setupImem(instructions: List[Int]) = {
        (0 until instructions.length).foreach{ ii =>
          dut.io.IMEMAddress.poke((ii*4).U)
          dut.io.IMEMWriteData.poke((instructions(ii).toInt).U)
          dut.clock.step()
        }
        dut.io.IMEMAddress.poke(4092.U)
        dut.io.IMEMWriteData.poke(0.U)
      }
      def setupRegs(regs: List[(Reg, Int)]) = {
        regs.foreach{ case(reg, word) =>
          dut.io.setup.poke(1.B)
          dut.io.regsWriteEnable.poke(1.B)
          dut.io.regsWriteData.poke((BigInt(word)).U)
          dut.io.regsAddress.poke((reg.value).U)
          dut.clock.step()
        }
        dut.io.regsWriteEnable.poke(0.B)
        dut.io.regsWriteData.poke(0.U)
        dut.io.regsAddress.poke(0.U)
      }
      def setupDmem(mem: List[(Addr, Int)]) = {
        mem.foreach { case (addr, word) =>
          dut.io.setup.poke(1.B)
          dut.io.DMEMWriteEnable.poke(1.B)
          dut.io.DMEMWriteData.poke(word.U)
          dut.io.DMEMAddress.poke((addr.value).U)
          dut.clock.step()
        }
        dut.io.DMEMWriteEnable.poke(0.B)
        dut.io.DMEMWriteData.poke(0.U)
        dut.io.DMEMAddress.poke(0.U)
      }
      def disableTestSignals: Unit = {
        dut.io.setup.poke(1.B)
        dut.io.DMEMWriteData.poke(0.U)
        dut.io.DMEMAddress.poke(0.U)
        dut.io.DMEMWriteEnable.poke(0.B)
        dut.io.regsWriteData.poke(0.U)
        dut.io.regsAddress.poke(0.U)
        dut.io.regsWriteEnable.poke(0.B)
        dut.io.IMEMWriteData.poke(0.U)
        dut.io.IMEMAddress.poke(4092.U)
      }

    }

  }
}

//
//dut.io.setup.poke(1.B)
//dut.io.DMEMWriteData.poke(0.U)
//dut.io.DMEMAddress.poke(0.U)
//dut.io.DMEMWriteEnable.poke(0.B)
//dut.io.regsWriteData.poke(0.U)
//dut.io.regsAddress.poke(0.U)
//dut.io.regsWriteEnable.poke(0.B)
//dut.io.IMEMWriteData.poke(0.U)
//dut.io.IMEMAddress.poke(0.U)
//dut.clock.step()
//dut.io.setup.poke(0.B)
//dut.clock.step()
//dut.io.regsWriteEnable.poke(1.B)
//dut.io.regsWriteData.poke(2.U)
//dut.io.regsAddress.poke(1.U)
//dut.clock.step()
//dut.io.regsWriteEnable.poke(0.B)
//dut.io.regsWriteData.poke(0.U)
//dut.io.regsAddress.poke(1.U)
//for(i <- 0 until 10){
//  dut.clock.step()
//}