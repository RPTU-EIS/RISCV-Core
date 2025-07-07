package prefetchTB

import chisel3._
import ICache._
import chisel3.util._
import chiseltest._
//import DCache.Cache
import org.scalatest.flatspec.AnyFlatSpec
import scala.util.control.Breaks._
//import RISCV_TOP._
import Cache.DICachesAndMemory

class IPrefetcher_test extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "IPrefetcher"

  val expectedValues = Seq(
          "h00000093".U, "h00100093".U, "h00200093".U, "h00300093".U,
          "h00400093".U, "h00500093".U, "h00600093".U, "h00700093".U,
          "h00800093".U, "h00900093".U, "h00A00093".U, "h00B00093".U,
          "h00C00093".U, "h00D00093".U, "h00E00093".U, "h00F00093".U,
          "h01000093".U, "h01100093".U, "h01200093".U, "h01300093".U,
          "h01400093".U, "h01500093".U, "h01600093".U, "h01700093".U,
          "h01800093".U, "h01900093".U, "h01A00093".U, "h01B00093".U,
          "h01C00093".U, "h01D00093".U, "h01E00093".U, "h01F00093".U
        )

  // "fir_test no prefetcher" should "work" in {
  //   //test(new RISCV_TOP("src/test/programs/prefetch_test")).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
  //   //test(new Cache("src/test/programs/prefetch_test")).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
  //   test(new DICachesAndMemory("src/test/programs/prefetch_test")).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
  //     c.clock.setTimeout(0)
  //     var pc = 0
  //     var counter = 0
  //     var test = 0
  //     //TODO c.io.cacheOnly.poke(true.B)

  //     var instr_out = c.io.instr_out.peek().litValue

  //     breakable {
  //       for(i <- 0 until 1000) {
  //         if(c.io.ICACHEbusy.peek().litToBoolean){
  //           if(c.io.ICACHEvalid.peek().litToBoolean) {
  //             instr_out = c.io.instr_out.peek().litValue
  //             test = (pc % 128)/4
  //             assert(instr_out == expectedValues(test).litValue, f"ADR ${test} PC ${pc}  failed: Expected 0x${expectedValues(test).litValue}%08x but got 0x${instr_out}%08x")
  //             if(pc == 24 || pc == 68 || pc == 112 || pc == 156 || pc == 200 || pc == 244 ||
  //               pc == 288 || pc == 332 || pc == 376 || pc == 420 || pc == 464 ||
  //               pc == 508 || pc == 552 || pc == 596){
  //               pc += 20
  //             }
  //             else{
  //               pc += 4
  //             }
  //             counter += 1
  //           }
  //         }
  //         c.io.instr_addr.poke((pc).U)

  //         if (pc >155*4) {
  //           println(f"fir_test ohne prefetcher BREAK at i: ${i}, counter: ${counter},--------------------------\n\n\n\n")
  //           break()  // Exit the loop early
  //         }
  //         c.clock.step(1)
  //       }
  //       println(f"fir_test ohne prefetcher ENDE at i: 1000, counter: ${counter},--------------------------\n\n\n\n")
  //     }
  //   }
  // }

  "fir_test with prefetcher" should "work" in {
    //test(new RISCV_TOP("src/test/programs/prefetch_test")).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
    test(new DICachesAndMemory("src/test/programs/prefetch_test")).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
      c.clock.setTimeout(0)
      var pc = 0
      var counter = 0
      var test = 0
      //TODO c.io.cacheOnly.poke(false.B)
      c.io.read_en.poke(true.B)
      c.io.write_en.poke(false.B)

      var instr_out = c.io.instr_out.peek().litValue

      breakable {
        for(i <- 0 until 1000) {
          // println(s"\n")
          // println(s"\n")
          // println(s"\n")
          //if(c.io.ICACHEbusy.peek().litToBoolean){
            //println(s"tb ICACHEbusy\n")
            if(c.io.ICACHEvalid.peek().litToBoolean) {
              // println(s"tb ICACHEvalid")
              instr_out = c.io.instr_out.peek().litValue
              test = (pc % 128)/4

              // println(f"tb instr: 0x$instr_out%08x")

              assert(instr_out == expectedValues(test).litValue, f"ADR ${test} PC ${pc}  failed: Expected 0x${expectedValues(test).litValue}%08x but got 0x${instr_out}%08x")
              if(pc == 24 || pc == 68 || pc == 112 || pc == 156 || pc == 200 || pc == 244 ||
                pc == 288 || pc == 332 || pc == 376 || pc == 420 || pc == 464 ||
                pc == 508 || pc == 552 || pc == 596){
                pc += 20
                // println(s"pc tb + 20: ${pc}")
              }
              else{
                pc += 4
                // println(s"pc new tb: ${pc}")
              }
              counter += 1
            }
          //}
          c.io.instr_addr.poke((pc).U)
          // println(s"tb next cycle ${pc}-------------------------")

          ////println(s"pc tb: ${pc},-------------------------\n")

          if (pc >155*4) {
            println(f"fir_test mit prefetcher BREAK at i: ${i}, counter: ${counter},--------------------------\n\n\n\n")
            break()  // Exit the loop early
          }
          c.clock.step(1)
        }
        println(s"fir_test mit prefetcher ENDE at i: 1000, counter: ${counter},-------------------------\n\n\n\n")
      }
    }
  }

//   // "linear_test no prefetcher" should "work" in {
//   //   //test(new RISCV_TOP("src/test/programs/prefetch_test")).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
//   //     test(new DICachesAndMemory("src/test/programs/prefetch_test")).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
//   //     c.clock.setTimeout(0)
//   //     var pc = 0
//   //     var counter = 0
//   //     var test = 0
//   //     //TODO c.io.cacheOnly.poke(true.B)

//   //       var instr_out = c.io.instr_out.peek().litValue

//   //     breakable {
//   //       for(i <- 0 until 1000) {
//   //         if(c.io.ICACHEbusy.peek().litToBoolean){
//   //           if(c.io.ICACHEvalid.peek().litToBoolean) {
//   //             instr_out = c.io.instr_out.peek().litValue
//   //             test = (pc % 128)/4
//   //             assert(instr_out == expectedValues(test).litValue, f"ADR ${test} PC ${pc}  failed: Expected 0x${expectedValues(test).litValue}%08x but got 0x${instr_out}%08x")
//   //             pc += 4
//   //             counter += 1
//   //           }
//   //         }

//   //         c.io.instr_addr.poke((pc).U)

//   //         if (pc > 180*4) {
//   //           println(s"linear_test_no BREAK at i: $i, counter: ${counter},--------------------------------\n\n\n\n")
//   //           break()  // Exit the loop early
//   //         }
//   //         c.clock.step(1)
//   //       }
//   //       println(s"linear_test_no ENDE at i: 1000, counter: ${counter},-----------------------------\n\n\n\n")
//   //     }
//   //   }
//   // }

  "linear_test with prefetcher" should "work" in {
    //test(new RISCV_TOP("src/test/programs/prefetch_test")).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
    test(new DICachesAndMemory("src/test/programs/prefetch_test")).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
      c.clock.setTimeout(0)
      var pc = 0
      var counter = 0
      var test = 0
      //TODO c.io.cacheOnly.poke(false.B)
      c.io.read_en.poke(false.B)
      c.io.write_en.poke(false.B)
      var instr_out = c.io.instr_out.peek().litValue

      breakable {
        for(i <- 0 until 1000) {
            if(c.io.ICACHEvalid.peek().litToBoolean) {
              instr_out = c.io.instr_out.peek().litValue
              test = (pc % 128)/4
              assert(instr_out == expectedValues(test).litValue, f"ADR ${test} PC ${pc}  failed: Expected 0x${expectedValues(test).litValue}%08x but got 0x${instr_out}%08x")
              pc += 4
              counter += 1
            }

          c.io.instr_addr.poke((pc).U)


          if (pc > 180*4) {
            println(s"linear_test_pref BREAK at i: $i, counter: ${counter},--------------------------------\n\n\n\n")
            break()  // Exit the loop early
          }
          c.clock.step(1)
        }
        println(s"linear_test_pref ENDE at i: 1000, counter: ${counter},-----------------------------\n\n\n\n")
      }
    }
  }

//   // "worst_case_no" should "work" in {
//   //   //test(new RISCV_TOP("src/test/programs/prefetch_test")).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
//   //   test(new DICachesAndMemory("src/test/programs/prefetch_test")).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
//   //     c.clock.setTimeout(0)
//   //     var pc = 0
//   //     var counter = 0
//   //     var test = 0

//   //     //TODO c.io.cacheOnly.poke(true.B)

//   //     var instr_out = c.io.instr_out.peek().litValue

//   //     breakable {
//   //       for (i <- 0 until 1000) {
//   //         if (c.io.ICACHEbusy.peek().litToBoolean) {
//   //           if (c.io.ICACHEvalid.peek().litToBoolean) {
//   //             counter += 1
//   //             instr_out = c.io.instr_out.peek().litValue
//   //             test = (pc % 128) / 4
//   //             assert(instr_out == expectedValues(test).litValue, f"ADR ${test} PC ${pc}  failed: Expected 0x${expectedValues(test).litValue}%08x but got 0x${instr_out}%08x")
//   //             pc = pc + 8 //next in prefetcher would be +4
//   //           }
//   //         }
//   //         if (pc > 720) {
//   //           println(s"worst_case_no BREAK i=${i}, counter: ${counter}-----------------------------------------\n")
//   //           break() // Exit the loop early
//   //         }
//   //         c.io.instr_addr.poke((pc).U)
//   //         c.clock.step(1)
//   //       }
//   //       println(s"worst_case_no ENDE i=1000, counter: ${counter}-----------------------------------------\n")
//   //     }
//   //   }
//   // }

  "worst_case_pref" should "work" in {
    //test(new RISCV_TOP("src/test/programs/prefetch_test")).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
    test(new DICachesAndMemory("src/test/programs/prefetch_test")).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
      c.clock.setTimeout(0)
      var pc = 0
      var counter = 0
      var test = 0
      c.io.read_en.poke(false.B)
      c.io.write_en.poke(false.B)
      //TODO c.io.cacheOnly.poke(false.B)
      
      var instr_out = c.io.instr_out.peek().litValue
      breakable {
      for (i <- 0 until 1000) {
          if (c.io.ICACHEvalid.peek().litToBoolean) {
            counter += 1
            instr_out = c.io.instr_out.peek().litValue
            test = (pc % 128) / 4
            assert(instr_out == expectedValues(test).litValue, f"ADR ${test} PC ${pc}  failed: Expected 0x${expectedValues(test).litValue}%08x but got 0x${instr_out}%08x")
            pc = pc + 8 //next in prefetcher would be +4
          }
        if(pc > 720){
          println(s"worst_case_pref BREAK i=${i}, counter: ${counter}-----------------------------------------\n")
          break()  // Exit the loop early
        }
        c.io.instr_addr.poke((pc).U)
        c.clock.step(1)
      }
      println(s"worst_case_pref ENDE i=1000, counter: ${counter}-----------------------------------------\n")
      }
    }
   }
 }