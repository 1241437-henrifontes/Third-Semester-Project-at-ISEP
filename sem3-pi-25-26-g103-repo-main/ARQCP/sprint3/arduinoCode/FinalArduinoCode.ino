#include "DHT.h"
#include <arduino-timer.h>
#include <math.h>

#define DHTPIN 16
#define DHTTYPE DHT11

#define RED_LED_1 5
#define YELLOW_LED_1 6
#define GREEN_LED_1 7

#define RED_LED_2 21
#define YELLOW_LED_2 20
#define GREEN_LED_2 19

#define DATA_LENGTH 100
#define UNIT_NAME_LENGTH 30

char sensor_units[][UNIT_NAME_LENGTH] = { "percentage", "celsius" };
enum sensor_unit { PERCENTAGE = 0, CELSIUS };

DHT dht(DHTPIN, DHTTYPE);
auto timer = timer_create_default();

bool emergencyActive[3] = { false, false, false };
unsigned long lastBlink[3] = { 0, 0, 0 };
bool ledState[3] = { false, false, false };

bool toggle_led(void *) {
  digitalWrite(LED_BUILTIN, !digitalRead(LED_BUILTIN));
  return true;
}

static void send_gth() {
  float t = dht.readTemperature();
  float h = dht.readHumidity();

  if (isnan(t) || isnan(h)) {
    Serial.println("ERR:DHT");
    return;
  }

  int tValue = (int)lroundf(t);
  int hValue = (int)lroundf(h);

  char buffer[DATA_LENGTH];
  snprintf(buffer, sizeof(buffer),
           "TEMP&unit:%s&value:%d#HUM&unit:%s&value:%d",
           sensor_units[CELSIUS], tValue,
           sensor_units[PERCENTAGE], hValue);

  Serial.println(buffer);
}

void clearTrack(int track) {
  if (track == 1) {
    digitalWrite(RED_LED_1, LOW);
    digitalWrite(YELLOW_LED_1, LOW);
    digitalWrite(GREEN_LED_1, LOW);
  } else if (track == 2) {
    digitalWrite(RED_LED_2, LOW);
    digitalWrite(YELLOW_LED_2, LOW);
    digitalWrite(GREEN_LED_2, LOW);
  }
}

void handle_serial() {
  if (!Serial.available()) return;

  String cmd = Serial.readStringUntil('\n');
  cmd.trim();
  cmd.toUpperCase();

  if (cmd == "GTH") {
    send_gth();
    return;
  }

  
  char c1 = cmd.charAt(0);
  char c2 = cmd.charAt(1);
  int track = cmd.substring(3).toInt();

  if (c1 == 'R' && c2 == 'B') {          
    emergencyActive[track] = true;
    clearTrack(track);
  }
  else if (c1 == 'G' && c2 == 'E') {     
    emergencyActive[track] = false;
    clearTrack(track);
    if (track == 1) digitalWrite(GREEN_LED_1, HIGH);
    if (track == 2) digitalWrite(GREEN_LED_2, HIGH);
  }
  else if (c1 == 'R' && c2 == 'E') {     
    emergencyActive[track] = false;
    clearTrack(track);
    if (track == 1) digitalWrite(RED_LED_1, HIGH);
    if (track == 2) digitalWrite(RED_LED_2, HIGH);
  }
  else if (c1 == 'Y' && c2 == 'E') {     
    emergencyActive[track] = false;
    clearTrack(track);
    if (track == 1) digitalWrite(YELLOW_LED_1, HIGH);
    if (track == 2) digitalWrite(YELLOW_LED_2, HIGH);
  }
  else if (c1 == 'C' && c2 == 'D') {     
    emergencyActive[track] = false;
    clearTrack(track);
  }
  else {
    Serial.println("ERR:CMD");
  }
}

void setup() {
  Serial.begin(9600);
  dht.begin();
  pinMode(LED_BUILTIN, OUTPUT);

  pinMode(RED_LED_1, OUTPUT);
  pinMode(YELLOW_LED_1, OUTPUT);
  pinMode(GREEN_LED_1, OUTPUT);

  pinMode(RED_LED_2, OUTPUT);
  pinMode(YELLOW_LED_2, OUTPUT);
  pinMode(GREEN_LED_2, OUTPUT);

  clearTrack(1);
  clearTrack(2);

  timer.every(1000, toggle_led);
}

void loop() {
  handle_serial();

  unsigned long now = millis();

  for (int t = 1; t <= 2; t++) {
    if (emergencyActive[t] && now - lastBlink[t] >= 500) {
      lastBlink[t] = now;
      ledState[t] = !ledState[t];

      if (t == 1) digitalWrite(RED_LED_1, ledState[t]);
      if (t == 2) digitalWrite(RED_LED_2, ledState[t]);
    }
  }

  timer.tick();
}