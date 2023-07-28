package DataTypes
import chisel3.util.Enum
import chisel3._
import chisel3.util._
import chisel3.util.{ BitPat, Cat }
  object Data{
    def lookupReg(s: String): Option[Int] = {
      val regMap = Map(
        "x0"       -> 0,
        "x1"       -> 1,
        "x2"       -> 2,
        "x3"       -> 3,
        "x4"       -> 4,
        "x5"       -> 5,
        "x6"       -> 6,
        "x7"       -> 7,
        "x8"       -> 8,
        "x9"       -> 9,
        "x10"      -> 10,
        "x11"      -> 11,
        "x12"      -> 12,
        "x13"      -> 13,
        "x14"      -> 14,
        "x15"      -> 15,
        "x16"      -> 16,
        "x17"      -> 17,
        "x18"      -> 18,
        "x19"      -> 19,
        "x20"      -> 20,
        "x21"      -> 21,
        "x22"      -> 22,
        "x23"      -> 23,
        "x24"      -> 24,
        "x25"      -> 25,
        "x26"      -> 26,
        "x27"      -> 27,
        "x28"      -> 28,
        "x29"      -> 29,
        "x30"      -> 30,
        "x31"      -> 31,
        "zero"     -> 0,
        "ra"       -> 1,
        "sp"       -> 2,
        "gp"       -> 3,
        "tp"       -> 4,
        "t0"       -> 5,
        "t1"       -> 6,
        "t2"       -> 7,
        "s0"       -> 8,
        "fp"       -> 8,
        "s1"       -> 9,
        "a0"       -> 10,
        "a1"       -> 11,
        "a2"       -> 12,
        "a3"       -> 13,
        "a4"       -> 14,
        "a5"       -> 15,
        "a6"       -> 16,
        "a7"       -> 17,
        "s2"       -> 18,
        "s3"       -> 19,
        "s4"       -> 20,
        "s5"       -> 21,
        "s6"       -> 22,
        "s7"       -> 23,
        "s8"       -> 24,
        "s9"       -> 25,
        "s10"      -> 26,
        "s11"      -> 27,
        "t3"       -> 28,
        "t4"       -> 29,
        "t5"       -> 30,
        "t6"       -> 31)

      regMap.lift(s)
    }
    case class Reg(value: Int)
    object Reg{ def apply(s: String): Reg = Reg(lookupReg(s).get) }

    case class Addr(value: Int){
      def +(that: Addr) = Addr(value + that.value)
      def -(that: Addr) = Addr(value - that.value)
      def step = Addr(value + 4)
    }
    trait ExecutionEvent
    case class RegUpdate(reg: Reg, word: Int)  extends ExecutionEvent
    case class Imm(value: Int)
    sealed trait TestSetting
    case class REGSET(rd: Reg, word: Int) extends TestSetting
    case class Regs(repr: Map[Reg, Int]) {
      def +(a: (Reg, Int)): (Option[RegUpdate], Regs) =
        if(a._1.value == 0) (None, this)
        else (Some(RegUpdate(a._1, a._2)), copy(repr + a))

      def arith(rd: Reg, operand1: Reg, operand2: Reg, op: (Int, Int) => Int): (Option[RegUpdate], Regs) =
        this + (rd -> op(repr(operand1), repr(operand2)))

      def arithImm(rd: Reg, operand1: Reg, operand2: Imm, op: (Int, Int) => Int): (Option[RegUpdate], Regs) =
        this + (rd -> op(repr(operand1), operand2.value))

      def compare(operand1: Reg, operand2: Reg, comp: (Int, Int) => Boolean): Boolean =
        comp(repr(operand1), repr(operand2))

      def apply(setting: TestSetting): Regs = setting match {
        case setting: REGSET => Regs(repr + (setting.rd -> setting.word))
        case _ => this
      }
    }

    case class MemWrite(addr: Addr, word: Int) extends ExecutionEvent
    case class MemRead(addr: Addr, word: Int)  extends ExecutionEvent
    case class MEMSET(addr: Addr, word: Int) extends TestSetting

    case class DMem(repr: Map[Addr, Int]) {
      def read(addr: Addr): Either[String, (MemRead, Int)] =
        if(addr.value >= 4096)
          Left(s"attempted to read from illegal address")
        else {
          val readResult = repr.lift(addr).getOrElse(0)
          Right((MemRead(addr, readResult), readResult))
        }

      def write(addr: Addr, word: Int): Either[String, (MemWrite, DMem)] =
        if(addr.value >= 4096)
          Left(s"attempted to write to illegal address")
        else {
          Right((MemWrite(addr, word)), DMem(repr + (addr -> word)))
        }

      def apply(setting: TestSetting): DMem = setting match {
        case setting: MEMSET => {
          DMem(repr + (setting.addr -> setting.word))
        }
        case _ => this
      }
    }
    object Regs{
      def empty: Regs = Regs((0 to 31).map(x => (Reg(x) -> 0)).toMap)
      def apply(settings: List[TestSetting]): Regs = settings.foldLeft(empty){
        case(acc, setting) => acc(setting)
      }
    }
    object DMem{
      def empty: DMem = DMem(Map[Addr, Int]())
      def apply(settings: List[TestSetting]): DMem = settings.foldLeft(empty){
        case(acc, setting) => acc(setting)
      }
    }
  }
