/*
 * SINGLE LINE FOLLOWER WITH ULTRASONIC SENSOR
 * 
 * Uses 5 line sensors.
 * Best used on a white surface ground with black line as tracks.
 * Follows a single track line.
 * Uses an ultrasonic sensor to STOP the Robot when there's an object in front.
 *
 *
 */

#include "sensorsDRV.h" // These files include the mappings
#include <avr/wdt.h>
#include "TBMotor.h" // These files include the mappings
#include <Servo.h>

OseppTBMotor Motor1(12, 11);
OseppTBMotor Motor2(8, 3);


#define leftMotor Motor1
#define rightMotor Motor2

Ultrasonic ults(2, 4);

const int servoPin = 9;
const int servoBais = 110;
Servo sv;

int leftSpeed = 0;
int rightSpeed = 0;

void setup()
{
  for (int i = A0; i < A5; i++)pinMode(i, INPUT_PULLUP);
  sv.attach(servoPin);
  sv.write(servoBais);

  //Setup a watchdog
  //When the battery voltage is insufficient / program unexpected
  //Watchdog reset chip
  wdt_enable(WDTO_4S);

}

bool lineTracker[5];

void loop()
{
  const float threshold = 150;

  //If in 4 seconds,The program is not coming back here.
  //Chip will reset
  wdt_reset();
  sv.write(servoBais);
  for (int i = 0; i < 5; i++)lineTracker[i] = !digitalRead(i + A0); //delete "!" to follow a white line.


  if (ults.Detect() < threshold)
  {
    leftSpeed = 0;
    rightSpeed = 0;
    SetMotor();
    return;//to the beginning of the loop function.
  }
  int left = lineTracker[0] * 3 + lineTracker[1] * 2 + lineTracker[2]  - lineTracker[3] * 2 - lineTracker[4] * 3;
  int right = -lineTracker[0] * 3 - lineTracker[1] * 2 + lineTracker[2]  + lineTracker[3] * 2 + lineTracker[4] * 3;
  if (left > right)
  {
    leftSpeed = 255;
    rightSpeed = -255;
  } else if (left < right)
  {
    leftSpeed = -255;
    rightSpeed = 255;
  } else {
    leftSpeed = rightSpeed = 255;
  }
  SetMotor();
  //delay(25);
}

// This sets a maximum speed for the motor 
void SetMotor()
{
  if (leftSpeed > 255)leftSpeed = 150; else if (leftSpeed < -255)leftSpeed = -150;
  if (rightSpeed > 255)rightSpeed = 150; else if (rightSpeed < -255)rightSpeed = -150;

  //To switch motor directions (if wired backwards)
  leftMotor.SetSpeed(leftSpeed);
  rightMotor.SetSpeed(rightSpeed);
}





