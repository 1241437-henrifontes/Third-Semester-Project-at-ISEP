.section .text
    .global str_find

str_find:
    addi sp, sp, -16
    sw   ra, 12(sp)
    sw   s0, 8(sp)

    mv s0, a0

outer_loop:
    lbu t0, 0(s0)
    beqz t0, not_found_sf

    mv t1, s0
    mv t2, a1

inner_loop:
    lbu t3, 0(t2)
    beqz t3, found_sf
    lbu t4, 0(t1)
    beqz t4, not_found_sf
    bne t3, t4, next_outer
    addi t1, t1, 1
    addi t2, t2, 1
    j    inner_loop

found_sf:
    mv a0, s0
    j  end_sf

next_outer:
    addi s0, s0, 1
    j    outer_loop

not_found_sf:
    mv a0, zero

end_sf:
    lw ra, 12(sp)
    lw s0, 8(sp)
    addi sp, sp, 16
    ret
