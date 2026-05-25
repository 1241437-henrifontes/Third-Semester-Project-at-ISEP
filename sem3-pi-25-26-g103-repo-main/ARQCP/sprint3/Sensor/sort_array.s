    .section .text
    .global sort_array
sort_array:
    addi sp, sp, -16
    sw ra, 12(sp)
    sw s0, 8(sp)
    sw s1, 4(sp)
    sw s2, 0(sp)

    blt a1, x0, empty
    beq a1, x0, empty

    mv s0, a0
    mv s1, a1
    mv s2, a2

    li t0, 0

firstloop:
    addi t1, s1, -1
    blt t0, t1, secondloop_start
    j end

secondloop_start:
    li t2, 0

secondloop:
    sub t3, t1, t0
    bge t2, t3, moove

    slli t4, t2, 2
    add t4, s0, t4

    lw t5, 0(t4)
    lw t6, 4(t4)

    beq s2, x0, descending

ascending:
    bgt t5, t6, swap
    j continueSecondLoop

descending:
    blt t5, t6, swap
    j continueSecondLoop

swap:
    sw t6, 0(t4)
    sw t5, 4(t4)
    j continueSecondLoop

continueSecondLoop:
    addi t2, t2, 1
    j secondloop

moove:
    addi t0, t0, 1
    j firstloop

end:
    lw s2, 0(sp)
    lw s1, 4(sp)
    lw s0, 8(sp)
    lw ra, 12(sp)
    mv a0, t0
    li a0, 1
    addi sp, sp, 16
    ret

empty:
    lw s2, 0(sp)
    lw s1, 4(sp)
    lw s0, 8(sp)
    lw ra, 12(sp)
    mv a0, t0
    li a0, 0
    addi sp, sp, 16
    ret
