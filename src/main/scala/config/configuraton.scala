package config

import chisel3.util.Enum

object AluOperation {  // Different Alu operations
  val add :: sll :: srl :: sra :: or :: and :: xor :: slt :: sltu :: sub :: beq :: bne :: blt :: bge :: bltu :: bgeu :: Nil = Enum(16)
}

object States {  // instruction execution stages
  val fetch :: dec :: exec :: mem :: wb :: Nil = Enum(5)
}

//object Operation {  // Different Alu operations
//  val add :: sll :: srl :: sra :: or :: and :: xor :: slt :: sltu :: sub :: beq :: bne :: blt :: bge :: bltu :: bgeu :: Nil = Enum(16)
//}

object ExtensionCases{ // for extension unit
  val id :: jal :: jalr :: auipc :: store :: i_type :: Nil = Enum(6)
}