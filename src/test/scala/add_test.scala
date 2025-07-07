package main_tb
import RISCV_TOP.RISCV_TOP
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import top_MC._
import chisel3._
import DataTypes.Data._
import java.sql.Driver

class add_test extends AnyFlatSpec with ChiselScalatestTester {

  "add_test" should "pass" in {
    test(new RISCV_TOP("Adder_hex/rv32ui-p-add.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)
      for(i <- 0 until 300){
        dut.clock.step()
        val pcVal = dut.io.PC.peek().litValue.toLong
        val writeEnable = dut.io.DMEMWriteEnableOut.peek().litValue
        val writeData = dut.io.DMEMWriteDataOut.peek().litValue

        if (writeEnable != 0) {  // check if write enable is true (non-zero)
          pcVal match {
              case 0x80000108 => assert(writeData == 2,  "Test 2 failed")
              case 0x80000114 => assert(writeData == 3,  "Test 3 failed")
              case 0x80000130 => assert(writeData == 10, "Test 4 failed")
              case 0x8000013C => assert(writeData == 4,  "Test 5 failed")
              case 0x80000148 => assert(writeData == 6,  "Test 6 failed")
              case 0x80000154 => assert(writeData == 1,  "Test 7 failed")
              case 0x80000160 => assert(writeData == 7,  "Test 8 failed")
              case 0x8000016C => assert(writeData == 8,  "Test 9 failed")
              case 0x80000178 => assert(writeData == 9,  "Test 10 failed")
              case 0x80000184 => assert(writeData == 5,  "Test 11 failed")
              case 0x80000190 => assert(writeData == 11, "Test 12 failed")
              case 0x8000019C => assert(writeData == 12, "Test 13 failed")
              case 0x800001A8 => assert(writeData == 13, "Test 14 failed")
              case 0x800001B4 => assert(writeData == 14, "Test 15 failed")
              case 0x800001C0 => assert(writeData == 15, "Test 16 failed")
              case 0x800001CC => assert(writeData == 16, "Test 17 failed")
              case 0x800001D8 => assert(writeData == 17, "Test 18 failed")
              case 0x800001E4 => assert(writeData == 18, "Test 19 failed")
              case 0x800001F0 => assert(writeData == 19, "Test 20 failed")
              case 0x800001FC => assert(writeData == 20, "Test 21 failed")
              case 0x80000208 => assert(writeData == 21, "Test 22 failed")
              case 0x80000214 => assert(writeData == 22, "Test 23 failed")
              case 0x80000220 => assert(writeData == 23, "Test 24 failed")
              case 0x8000022C => assert(writeData == 24, "Test 25 failed")
              case 0x80000238 => assert(writeData == 25, "Test 26 failed")
              case 0x80000244 => assert(writeData == 26, "Test 27 failed")
              case 0x80000250 => assert(writeData == 27, "Test 28 failed")
              case 0x8000025C => assert(writeData == 28, "Test 29 failed")
              case 0x80000268 => assert(writeData == 29, "Test 30 failed")
              case 0x80000274 => assert(writeData == 30, "Test 31 failed")
              case 0x80000280 => assert(writeData == 31, "Test 32 failed")
              case 0x8000028C => assert(writeData == 32, "Test 33 failed")
              case 0x80000298 => assert(writeData == 33, "Test 34 failed")
              case 0x800002A4 => assert(writeData == 34, "Test 35 failed")
              case 0x800002B0 => assert(writeData == 35, "Test 36 failed")
              case 0x800002BC => assert(writeData == 36, "Test 37 failed")
              case 0x800002C8 => assert(writeData == 37, "Test 38 failed")
            case _           => // do nothing or println("PC not matched")
          }
        }
      }

    }

  }
}