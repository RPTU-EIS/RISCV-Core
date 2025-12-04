package CacheAndMemory_test

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
//import DCache.CacheAndMemory
import Cache.DICachesAndMemory

class CacheAndMemory_test extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "DCache and Memory"
  println("\n\n\n\n CacheAndMemory_test doesnt work because only cache is tested and mem was merged")
  println("->only works when connected to Arbiter etc")
  println("currently commented out\n\n\n\n")
  // "read miss dirty" should "pass" in {
  //   test(new DICachesAndMemory("src/test/programs/prefetch_test")) { c =>//CacheAndMemory) { c =>
  //     // println("\n ---------------- test 1: read miss dirty ------------------\n")
  //     c.io.write_en.poke(0.B)
  //     c.io.read_en.poke(1.B)
  //     c.io.address.poke(0.U)
  //     c.clock.step()
  //     c.io.read_en.poke(0.B)
  //     c.clock.step()
  //     c.clock.step()
  //     c.clock.step()
  //     //!c.clock.step()
  //     c.io.DCACHEvalid.expect(1.B)
  //     c.io.data_out.expect(10.U)
  //     c.clock.step()
  //     c.clock.step()
  //   }
  // }

  // "read miss clean" should "pass" in {
  //   test(new DICachesAndMemory("src/test/programs/prefetch_test")) { c =>//CacheAndMemory) { c =>
  //     //println("\n ---------------- test 2: read miss clean ------------------\n")
  //     c.io.write_en.poke(0.B)
  //     c.io.read_en.poke(1.B)
  //     c.io.address.poke(8.U)
  //     c.clock.step()
  //     c.io.read_en.poke(0.B)
  //     c.clock.step()
  //     c.clock.step()
  //     //!c.clock.step()
  //     c.io.DCACHEvalid.expect(1.B)
  //     c.io.data_out.expect(12.U)
  //     c.clock.step()
  //     c.clock.step()
  //   }
  // }
  // "read hit" should "pass" in {
  //   test(new DICachesAndMemory("src/test/programs/prefetch_test")) { c =>//CacheAndMemory) { c =>
  //     // println("\n ---------------- test 3: read hit ------------------\n")

  //     c.io.write_en.poke(0.B)
  //     c.io.read_en.poke(1.B)
  //     c.io.address.poke(2273941156L.U)
  //     //!c.clock.step()

  //     //!c.io.read_en.poke(0.B)

  //     c.io.DCACHEvalid.expect(1.B)
  //     c.io.data_out.expect(2284362812L.U)
  //     c.io.read_en.poke(0.B)//!
  //     c.clock.step()
  //     c.clock.step()
  //   }
  // }

  // "write miss" should "pass" in {
  //   test(new DICachesAndMemory("src/test/programs/prefetch_test")) { c =>//CacheAndMemory) { c =>
  //     //println("\n ---------------- test 4: write miss ------------------\n")
  //     // reading address 12
  //     /*c.io.write_en.poke(0.B)
  //     c.io.read_en.poke(1.B)
  //     c.io.address.poke(12.U)
  //     c.clock.step()
  //     c.io.read_en.poke(0.B)
  //     c.clock.step()
  //     c.clock.step()
  //     c.clock.step()
  //     c.clock.step()
  //     c.clock.step()
  //     c.clock.step()*/

  //     // writing address 12
  //     c.io.write_en.poke(1.B)
  //     c.io.read_en.poke(0.B)
  //     c.io.address.poke(12.U)
  //     c.io.write_data.poke(89.U)
  //     c.clock.step()
  //     c.io.write_en.poke(0.B)
  //     c.clock.step()
  //     c.clock.step()
  //     c.clock.step()
  //     c.clock.step()

  //     // reading address 12
  //     c.io.write_en.poke(0.B)
  //     c.io.read_en.poke(1.B)
  //     c.io.address.poke(12.U)
  //     c.clock.step()
  //     c.io.read_en.poke(0.B)
  //     c.clock.step()
  //     c.clock.step()

  //     // reading address 268 (same index 12)
  //     c.io.write_en.poke(0.B)
  //     c.io.read_en.poke(1.B)
  //     c.io.address.poke(268.U)
  //     c.clock.step()
  //     c.io.read_en.poke(0.B)
  //     c.clock.step()
  //     c.clock.step()
  //     c.clock.step()
  //     c.clock.step()
  //     c.clock.step()
  //     c.clock.step()

  //     // reading address 12
  //     c.io.write_en.poke(0.B)
  //     c.io.read_en.poke(1.B)
  //     c.io.address.poke(12.U)
  //     c.clock.step()
  //     c.io.read_en.poke(0.B)
  //     c.clock.step()
  //     c.clock.step()
  //     c.clock.step()
  //     c.clock.step()

  //     //c.io.DCACHEvalid.expect(1.B)
  //     //c.io.data_out.expect(2284362812L.U)

  //     c.clock.step()
  //     c.clock.step()
  //   }
  // }
}