    .section .text
    .global str_to_int

str_to_int:
    addi sp, sp, -8
    sw   ra, 4(sp)
    li   t0, 0

conv_loop:
    lbu  t1, 0(a0)
    beqz t1, conv_end
    li   t2, '0'
    blt  t1, t2, conv_end
    li   t2, '9'
    bgt  t1, t2, conv_end

    addi t1, t1, -48
    li   t2, 10
    mul  t0, t0, t2
    add  t0, t0, t1

    addi a0, a0, 1
    j    conv_loop

conv_end:
    mv   a0, t0
    lw   ra, 4(sp)
    addi sp, sp, 8
    ret
