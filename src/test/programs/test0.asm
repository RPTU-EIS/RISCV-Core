addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x0, x0, 0
addi x1, x0, 9
addi x2, x0, 3
addi x3, x0, 17
blt x1, x2, 16
addi x1, x1, -5
blt x1, x2, 16
addi x3, x3, -2
mul x2, x3, x1
jal x0, -12
slt x14, x2, x3
addi x0, x0, 0