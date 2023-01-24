main:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	sw	s1,20(sp)
	addi	s0,sp,32
	li	a5,6
	sw	a5,-20(s0)
	li	a5,-2
	sw	a5,-24(s0)
	li	a5,-1
	sw	a5,-28(s0)
	li	a5,7
	sw	a5,-32(s0)
	lw	a4,-20(s0)
	lw	a5,-24(s0)
	add	a5,a4,a5
	mv	a0,a5
    nop
    nop
    done
