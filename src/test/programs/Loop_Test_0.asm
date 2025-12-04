0   00000013    addi x0, x0, 0
4   00a00293    addi x5, x0, 10       // x5 = 10
8   fff28293    addi x5, x5, -1       // x5 = x5 - 1
12  fe029ee3    bne x5, x0, -4        // if (x5 != 0) jump back to PC = 8 (loop)
16  00000073    ecall                 // End of program (used to signal halt)