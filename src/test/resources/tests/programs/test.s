label:  
main:
    addi x5, x0, 10
    addi x6, x0, 20
mylabel:   
    addi x6, x6, -1
    beq  x5, x6, exit
    jal  x0, mylabel
exit:
    nop
    nop
    done
