.section .text
.global enqueueValue

    #int enqueueValue(int* buffer, int length, int* nelem, int* tail, int* head, int value)
    #*buffer = a0
    #length = a1
    #*nelem = a2
    #*tail = a3
    #*head = a4
    #value = a5

enqueueValue:
    beqz a1, fail

    lw t0, 0(a3) #t0 = tail
    lw t1, 0(a4) #t1 = head

    bge t0, a1, fail
    bge t1, a1, fail

    slli t2, t1, 2 #verifica se tem espaço na head
    add t2, t2, a0
    lw t3, 0(t2)
    beqz t3, insert

    addi sp, sp, -16 #adiciona espaço na stack
    sw ra, 12(sp)
    sw a0, 8(sp)
    sw a5, 4(sp)
    addi t4, sp, 0 #reserva espaço vazio

    mv a5, t4 #adiciona espaço vazio em a5

    call dequeueValue

    lw ra, 12(sp)
    lw a0, 8(sp)
    lw a5, 4(sp)
    addi sp, sp, 16

insert:
    lw t5, 0(a4) #t5 = head
    slli t6, t5, 2 #buffer[head]
    add t6, t6, a0
    sw a5, 0(t6)
    li a0, 1
    ret


fail:
    li a0, 0
    ret