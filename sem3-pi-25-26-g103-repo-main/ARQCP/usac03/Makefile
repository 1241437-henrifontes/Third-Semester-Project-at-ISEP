CC = riscv32-buildroot-linux-gnu-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

CC=riscv32-linux-gcc
CFLAGS=-Wall -Wextra -fanalyzer -g
ARCHFLAGS=-march=rv32imd -mabi=ilp32d
LIBRV32=${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o asm1.o asm2.o asm3.o asm4.o asm5.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o asm1.o asm2.o asm3.o asm4.o asm5.o -o program.elf

main.o: main.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

asm1.o: asm1.s asm1.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c asm1.s

asm2.o: asm2.s asm2.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c asm2.s

asm3.o: asm3.s asm3.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c asm3.s

asm4.o: asm4.s asm4.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c asm4.s

asm5.o: asm5.s asm5.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c asm5.s

clean:
	rm -f *.o *.elf .gdbinit

run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf

debug: program.elf
	@echo "Starting QEMU in debug mode and attaching GDB..."
	qemu-riscv32 -L $(LIBRV32) -g 1234 ./program.elf & \
	sleep 1; \
		riscv32-linux-gdb program.elf \
		-ex "target remote :1234" \
		-ex "layout split" \
		-ex "set disassemble-next-line on" \

define GDBINIT
target extended-remote localhost:1234
set sysroot ~/bootlin/riscv32-buildroot-linux-gnu/sysroot
break main
continue

endef

export GDBINIT

.gdbinit:
	@echo "$$GDBINIT">.gdbinit