CC = riscv32-buildroot-linux-gnu-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g -I. -I./UI -I./Sensor -I./LightSigns -I./Board -I./Manager
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
OBJDIR = obj

LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf : $(OBJDIR)/main.o $(OBJDIR)/config.o $(OBJDIR)/encrypt_data.o $(OBJDIR)/decrypt_data.o $(OBJDIR)/login.o $(OBJDIR)/serial.o $(OBJDIR)/outputSerial.o $(OBJDIR)/extract_data.o $(OBJDIR)/str_len.o $(OBJDIR)/str_find.o $(OBJDIR)/str_copy_until_delim.o $(OBJDIR)/str_to_int.o $(OBJDIR)/cb_push_asm.o $(OBJDIR)/median.o $(OBJDIR)/sort_array.o $(OBJDIR)/format_command.o $(OBJDIR)/lightsigns.o $(OBJDIR)/board.o $(OBJDIR)/manager.o $(OBJDIR)/log.o $(OBJDIR)/log_belongs_to_user_asm.o $(OBJDIR)/expo.o $(OBJDIR)/menu.o $(OBJDIR)/railwayManager.o $(OBJDIR)/setNono.o $(OBJDIR)/setFree.o $(OBJDIR)/assignTrain.o $(OBJDIR)/orderOfDeparture.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(OBJDIR)/main.o $(OBJDIR)/config.o $(OBJDIR)/encrypt_data.o $(OBJDIR)/decrypt_data.o $(OBJDIR)/login.o $(OBJDIR)/serial.o $(OBJDIR)/outputSerial.o $(OBJDIR)/extract_data.o $(OBJDIR)/str_len.o $(OBJDIR)/str_find.o $(OBJDIR)/str_copy_until_delim.o $(OBJDIR)/str_to_int.o $(OBJDIR)/cb_push_asm.o $(OBJDIR)/median.o $(OBJDIR)/sort_array.o $(OBJDIR)/format_command.o $(OBJDIR)/lightsigns.o $(OBJDIR)/board.o $(OBJDIR)/manager.o $(OBJDIR)/log.o $(OBJDIR)/log_belongs_to_user_asm.o $(OBJDIR)/expo.o $(OBJDIR)/menu.o $(OBJDIR)/railwayManager.o $(OBJDIR)/setNono.o $(OBJDIR)/setFree.o $(OBJDIR)/assignTrain.o $(OBJDIR)/orderOfDeparture.o -o program.elf

$(OBJDIR)/main.o : UI/main.c Sensor/config.h funcs.h UI/serial.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c UI/main.c -o $(OBJDIR)/main.o

$(OBJDIR)/config.o : Sensor/config.c Sensor/config.h asm.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Sensor/config.c -o $(OBJDIR)/config.o

$(OBJDIR)/encrypt_data.o: Sensor/encrypt_data.s asm.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Sensor/encrypt_data.s -o $(OBJDIR)/encrypt_data.o

$(OBJDIR)/decrypt_data.o: Sensor/decrypt_data.s asm.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Sensor/decrypt_data.s -o $(OBJDIR)/decrypt_data.o

$(OBJDIR)/login.o: UI/login.c Sensor/config.h funcs.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c UI/login.c -o $(OBJDIR)/login.o

$(OBJDIR)/serial.o: UI/serial.c UI/serial.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c UI/serial.c -o $(OBJDIR)/serial.o

$(OBJDIR)/outputSerial.o: UI/outputSerial.c UI/serial.h funcs.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c UI/outputSerial.c -o $(OBJDIR)/outputSerial.o

$(OBJDIR)/extract_data.o: Sensor/extract_data.s asm.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Sensor/extract_data.s -o $(OBJDIR)/extract_data.o

$(OBJDIR)/str_len.o: Sensor/str_len.s asm.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Sensor/str_len.s -o $(OBJDIR)/str_len.o

$(OBJDIR)/str_find.o: Sensor/str_find.s asm.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Sensor/str_find.s -o $(OBJDIR)/str_find.o

$(OBJDIR)/str_copy_until_delim.o: Sensor/str_copy_until_delim.s asm.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Sensor/str_copy_until_delim.s -o $(OBJDIR)/str_copy_until_delim.o

$(OBJDIR)/str_to_int.o: Sensor/str_to_int.s asm.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Sensor/str_to_int.s -o $(OBJDIR)/str_to_int.o

$(OBJDIR)/cb_push_asm.o: Sensor/cb_push_asm.s asm.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Sensor/cb_push_asm.s -o $(OBJDIR)/cb_push_asm.o

$(OBJDIR)/median.o: Sensor/median.s asm.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Sensor/median.s -o $(OBJDIR)/median.o

$(OBJDIR)/sort_array.o: Sensor/sort_array.s asm.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Sensor/sort_array.s -o $(OBJDIR)/sort_array.o

$(OBJDIR)/format_command.o: LightSigns/format_command.s asm.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c LightSigns/format_command.s -o $(OBJDIR)/format_command.o

$(OBJDIR)/lightsigns.o: LightSigns/lightsigns.c LightSigns/lightsigns.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c LightSigns/lightsigns.c -o $(OBJDIR)/lightsigns.o

$(OBJDIR)/board.o: Board/board.c Board/board.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Board/board.c -o $(OBJDIR)/board.o

$(OBJDIR)/manager.o: Manager/manager.c Manager/manager.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Manager/manager.c -o $(OBJDIR)/manager.o

$(OBJDIR)/log.o: Manager/log.c Manager/log.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Manager/log.c -o $(OBJDIR)/log.o

$(OBJDIR)/log_belongs_to_user_asm.o: Manager/log_belongs_to_user_asm.s Manager/log.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Manager/log_belongs_to_user_asm.s -o $(OBJDIR)/log_belongs_to_user_asm.o

$(OBJDIR)/expo.o: Sensor/expo.c Sensor/expo.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c Sensor/expo.c -o $(OBJDIR)/expo.o

$(OBJDIR)/menu.o: UI/menu.c funcs.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c UI/menu.c -o $(OBJDIR)/menu.o

$(OBJDIR)/railwayManager.o: UI/railwayManager.c funcs.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c UI/railwayManager.c -o $(OBJDIR)/railwayManager.o

$(OBJDIR)/setNono.o: manager/setNono.c manager/manager.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager/setNono.c -o $(OBJDIR)/setNono.o

$(OBJDIR)/setFree.o: manager/setFree.c manager/manager.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager/setFree.c -o $(OBJDIR)/setFree.o

$(OBJDIR)/assignTrain.o: manager/assignTrain.c manager/manager.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager/assignTrain.c -o $(OBJDIR)/assignTrain.o

$(OBJDIR)/orderOfDeparture.o: manager/orderOfDeparture.c manager/manager.h Board/board.h | $(OBJDIR)
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager/orderOfDeparture.c -o $(OBJDIR)/orderOfDeparture.o

$(OBJDIR):
	mkdir -p $(OBJDIR)

run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf

# Native unit tests (run on host x86)
.PHONY: test
test:
	$(MAKE) -C tests run

clean:
	rm -f $(OBJDIR)/*.o program.elf
