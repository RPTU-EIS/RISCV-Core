import os
from os import path
import sys

filetext = ["package scala_Test_tb\n","import RISCV_TOP.RISCV_TOP\n","import chiseltest._\n",
            "import org.scalatest.flatspec.AnyFlatSpec\n","import top_MC._\n","import chisel3._\n",
            "import DataTypes.Data._\n","import java.sql.Driver\n\n",
            "class scala_Test_tb extends AnyFlatSpec with ChiselScalatestTester {\n\n",
            '   "Scala_test" should "pass" in {\n']



def main(arg1):
    #create file
    file_path = path.relpath("src/test/scala/test_tb.scala")
    fp = open(file_path, 'w')
    fp.writelines(filetext)
    location = 'src/main/scala/InstructionMemory/' + arg1
    txt = '     test(new RISCV_TOP("' + location + '")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>\n'
    filemoretext = [txt, "      for(i <- 0 until 200){\n", "        dut.clock.step()\n", "      }\n",
                    "     }\n", "   }\n", "}\n"]
    fp.writelines(filemoretext)
    fp.close()
    #os.system("sbt run")
    os.system("sbt test")
    #os.remove(file_path)
    
if __name__ == '__main__':
    sys.exit(main(sys.argv[1]))