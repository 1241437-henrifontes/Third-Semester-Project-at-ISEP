    .section .text
    .globl str_len

str_len:
    addi sp, sp, -8
    sw   ra, 4(sp)
    li   t0, 0
1:  lbu  t1, 0(a0)
    beqz t1, 2f
    addi a0, a0, 1
    addi t0, t0, 1
    j    1b
2:  mv   a0, t0
    lw   ra, 4(sp)
    addi sp, sp, 8
    ret
