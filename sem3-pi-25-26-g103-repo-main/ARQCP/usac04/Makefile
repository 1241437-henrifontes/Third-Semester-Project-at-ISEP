CC = riscv32-buildroot-linux-gnu-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o asm.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o asm.o -o program.elf

main.o: main.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

asm.o: asm.s asm.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c asm.s

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