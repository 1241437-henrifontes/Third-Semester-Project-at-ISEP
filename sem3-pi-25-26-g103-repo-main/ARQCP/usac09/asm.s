.section .text
.global median

# int median(int* vec, int length, int *me)

median:
    ble a1, x0, fail   

    addi sp, sp, -16          
    sw ra, 12(sp)
    sw a0, 8(sp)
    sw a1, 4(sp)
    sw a2, 0(sp)

    li a2, 1                 
    call sort_array

    lw a2, 0(sp)
    lw a1, 4(sp)
    lw a0, 8(sp)
    lw ra, 12(sp)
    addi sp, sp, 16

    li t2, 0

    li t0, 2
    rem t0, a1, t0

    beqz t0, even

odd:
    li t4, 2
    div t2, a1, t4
    slli t1, t2, 2
    add t1, t1, a0
    lw t2, 0(t1)
    j done

even:
    li t4, 2
    div t2, a1, t4
    li t0, 1
    sub t3, t2, t0
    slli t1, t2, 2
    add t1, t1, a0
    lw t2, 0(t1)
    slli t1, t3, 2
    add t1, t1, a0
    lw t3, 0(t1)
    add t2, t2, t3
    div t2, t2, t4

done:
    sw t2, 0(a2)         
    li a0, 1            
    ret

fail:
    li a0, 0
    ret
