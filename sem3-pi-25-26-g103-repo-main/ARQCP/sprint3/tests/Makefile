CC = gcc
CFLAGS = -Wall -Wextra -g -I.. -I../Manager -I../Sensor -I../Board -I../UI -I../LightSigns

TESTS = test_log test_config test_board test_lightsigns test_serial test_manager test_menu test_login test_setfree test_setnono test_assigntrain test_outputSerial test_orderOfDeparture test_expo test_railwayManager test_runner

.PHONY: all clean run

all: $(TESTS)

# build individual test executables and link with sources as needed

test_log: test_log.c helpers.c ../Manager/log.c unity.c
	$(CC) $(CFLAGS) -o $@ test_log.c helpers.c ../Manager/log.c unity.c

test_config: test_config.c helpers.c ../Sensor/config.c unity.c
	$(CC) $(CFLAGS) -o $@ test_config.c helpers.c ../Sensor/config.c unity.c

test_board: test_board.c ../Board/board.c helpers.c ../UI/outputSerial.c ../Manager/manager.c ../Manager/log.c unity.c
	$(CC) $(CFLAGS) -o $@ test_board.c ../Board/board.c helpers.c ../UI/outputSerial.c ../Manager/manager.c ../Manager/log.c unity.c

test_lightsigns: test_lightsigns.c unity.c
	$(CC) $(CFLAGS) -o $@ test_lightsigns.c unity.c

test_serial: test_serial.c ../UI/serial.c unity.c
	$(CC) $(CFLAGS) -o $@ test_serial.c ../UI/serial.c unity.c

test_manager: test_manager.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c unity.c
	$(CC) $(CFLAGS) -o $@ test_manager.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c unity.c

test_menu: test_menu.c ../UI/menu.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c ../UI/railwayManager.c ../Sensor/expo.c ../LightSigns/lightsigns.c ../Manager/assignTrain.c ../Manager/setNono.c ../Manager/setFree.c ../Manager/orderOfDeparture.c unity.c
	$(CC) $(CFLAGS) -DUSE_REAL_LIGHTSIGNS -o $@ test_menu.c ../UI/menu.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c ../UI/railwayManager.c ../Sensor/expo.c ../LightSigns/lightsigns.c ../Manager/assignTrain.c ../Manager/setNono.c ../Manager/setFree.c ../Manager/orderOfDeparture.c unity.c

test_login: test_login.c ../UI/login.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c unity.c
	$(CC) $(CFLAGS) -o $@ test_login.c ../UI/login.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c unity.c

test_setfree: test_setfree.c ../Manager/setFree.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c unity.c
	$(CC) $(CFLAGS) -o $@ test_setfree.c ../Manager/setFree.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c unity.c

test_setnono: test_setnono.c ../Manager/setNono.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c unity.c
	$(CC) $(CFLAGS) -o $@ test_setnono.c ../Manager/setNono.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c unity.c

test_assigntrain: test_assigntrain.c ../Manager/assignTrain.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c unity.c
	$(CC) $(CFLAGS) -o $@ test_assigntrain.c ../Manager/assignTrain.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c unity.c

test_outputSerial: test_outputSerial.c ../UI/outputSerial.c helpers.c unity.c
	$(CC) $(CFLAGS) -o $@ test_outputSerial.c ../UI/outputSerial.c helpers.c unity.c

test_orderOfDeparture: test_orderOfDeparture.c ../Manager/orderOfDeparture.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c unity.c
	$(CC) $(CFLAGS) -o $@ test_orderOfDeparture.c ../Manager/orderOfDeparture.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c unity.c

test_expo: test_expo.c helpers.c ../Manager/log.c ../Sensor/expo.c ../Manager/manager.c ../Board/board.c ../UI/outputSerial.c unity.c
	$(CC) $(CFLAGS) -o $@ test_expo.c helpers.c ../Manager/log.c ../Sensor/expo.c ../Manager/manager.c ../Board/board.c ../UI/outputSerial.c unity.c

test_railwayManager: test_railwayManager.c ../UI/railwayManager.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c ../Manager/assignTrain.c ../Manager/setNono.c ../Manager/setFree.c ../Manager/orderOfDeparture.c unity.c
	$(CC) $(CFLAGS) -o $@ test_railwayManager.c ../UI/railwayManager.c helpers.c ../Manager/manager.c ../Manager/log.c ../Board/board.c ../UI/outputSerial.c ../Manager/assignTrain.c ../Manager/setNono.c ../Manager/setFree.c ../Manager/orderOfDeparture.c unity.c

# runner executes the tests

test_runner: test_runner.c
	$(CC) $(CFLAGS) -o $@ test_runner.c

run: all
	./test_runner

clean:
	rm -f *.o $(TESTS) tmp_* *.txt
