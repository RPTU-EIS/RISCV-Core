//import chiseltest._
//import org.scalatest.flatspec.AnyFlatSpec
//import DataMemory._
//import chisel3._
//
//class DataMemory_tb extends AnyFlatSpec with ChiselScalatestTester {
//  "DM" should "pass" in {
//    test(new DataMemory("src/main/scala/DataMemory/dataMemVals")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
//
//      dut.io.lsMux.poke(2.U(3.W)) //////////// word
//      dut.io.addr.poke(9.U(32.W))
//      dut.io.DM_WE.poke(false.B)
//      dut.io.DM_RE.poke(false.B)
//      dut.io.data_in.poke(13.U(32.W))
//
//      dut.io.data_out.expect(0.U(32.W)) // read enable is false, read 0
//      dut.clock.step()
//
//      dut.io.DM_RE.poke(true.B)
//      dut.io.data_out.expect("hfedcba98".U(32.W)) // read enable is true, lw from DM[9]
//      dut.clock.step()
//
//      dut.io.lsMux.poke(4.U(3.W)) //////////// lbu
//      dut.io.data_out.expect("h00000098".U(32.W)) // read enable is true, lbu from DM[9]
//      dut.clock.step()
//
//      dut.io.lsMux.poke(0.U(3.W)) //////////// lb
//      dut.io.data_out.expect("hffffff98".U(32.W)) // read enable is true, lb from DM[9]
//      dut.clock.step()
//
//      dut.io.lsMux.poke(5.U(3.W)) //////////// lhu
//      dut.io.data_out.expect("h0000ba98".U(32.W)) // read enable is true, lhu from DM[9]
//      dut.clock.step()
//
//      dut.io.lsMux.poke(1.U(3.W)) //////////// lh
//      dut.io.data_out.expect("hffffba98".U(32.W)) // read enable is true, lh from DM[9]
//      dut.clock.step() //
//
//      dut.io.lsMux.poke(2.U(3.W)) //////////// sw
//      dut.io.addr.poke(0.U(32.W))
//      dut.io.DM_WE.poke(true.B)
//      dut.io.DM_RE.poke(true.B)
//      dut.io.data_in.poke("h7e7c7a78".U(32.W))
//
//      dut.io.data_out.expect("h00000001".U(32.W))
//      dut.clock.step()
//      dut.io.data_out.expect("h7e7c7a78".U(32.W)) // sw DM[0]
//
//      dut.io.lsMux.poke(0.U(3.W)) //////////// sb
//      dut.io.addr.poke(1.U(32.W))
//      dut.io.DM_WE.poke(true.B)
//      dut.io.DM_RE.poke(true.B)
//      dut.io.data_in.poke("h7e7c7a78".U(32.W))
//
//      dut.io.data_out.expect("h00000000".U(32.W))
//      dut.clock.step()
//      dut.io.data_out.expect("h00000078".U(32.W)) // sb DM[1]
//
//      dut.io.lsMux.poke(1.U(3.W)) //////////// sh
//      dut.io.addr.poke(1.U(32.W))
//      dut.io.DM_WE.poke(true.B)
//      dut.io.DM_RE.poke(true.B)
//      dut.io.data_in.poke("h7e7c7a78".U(32.W))
//
//      dut.io.data_out.expect("h00000078".U(32.W))
//      dut.clock.step()
//      dut.io.data_out.expect("h00007a78".U(32.W)) // sh DM[1]
//      dut.io.DM_WE.poke(false.B)
//      dut.io.addr.poke(0.U(32.W))
//
//
//      dut.io.lsMux.poke(4.U(3.W)) //////////// lbu
//      dut.io.data_out.expect("h00000078".U(32.W)) // read enable is true, lbu from DM[9]
//      dut.clock.step()
//
//      dut.io.lsMux.poke(0.U(3.W)) //////////// lb
//      dut.io.data_out.expect("h00000078".U(32.W)) // read enable is true, lb from DM[9]
//      dut.clock.step()
//
//      dut.io.lsMux.poke(5.U(3.W)) //////////// lhu
//      dut.io.data_out.expect("h00007a78".U(32.W)) // read enable is true, lhu from DM[9]
//      dut.clock.step()
//
//      dut.io.lsMux.poke(1.U(3.W)) //////////// lh
//      dut.io.data_out.expect("h00007a78".U(32.W)) // read enable is true, lh from DM[9]
//      dut.clock.step()
//    }
//  }
//}