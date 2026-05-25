     .section .text
    .global move_n_to_array
move_n_to_array:
    addi sp, sp, -20
    sw ra, 16(sp)
    sw s0, 12(sp)
    sw s1, 8(sp)
    sw s2, 4(sp)
    sw s3, 0(sp)

    mv s0, a0
    mv s1, a1
    mv s2, a2
    mv s3, a3
    mv t0, a4
    mv t1, a5
    mv t2, a6
    lw t3, 0(s2)
    blt t3, t1, usac07_empty

    li t4, 0       # i = 0

usac07_loop:
    bge t4, t1, usac07_end

    lw t5, 0(s3)               # tail
    slli t6, t5, 2
    add t6, s0, t6
    lw t6, 0(t6)

    slli t5, t4, 2             # reutiliza t5 para offset array[i]
    add t5, t2, t5
    sw t6, 0(t5)

    lw t5, 0(s3)               # tail novamente
    addi t5, t5, 1
    rem t5, t5, s1
    sw t5, 0(s3)

    lw t3, 0(s2)
    addi t3, t3, -1
    sw t3, 0(s2)

    addi t4, t4, 1
    j usac07_loop

usac07_end:
    li a0, 1
    lw s3, 0(sp)
    lw s2, 4(sp)
    lw s1, 8(sp)
    lw s0, 12(sp)
    lw ra, 16(sp)
    addi sp, sp, 20
    ret

usac07_empty:
    li a0, 0
    lw s3, 0(sp)
    lw s2, 4(sp)
    lw s1, 8(sp)
    lw s0, 12(sp)
    lw ra, 16(sp)
    addi sp, sp, 20
    ret
