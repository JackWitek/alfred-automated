#include "SensorsDRV.h"
#include <Arduino.h>

Ultrasonic::Ultrasonic(uint8_t Trig_Pin, uint8_t Echo_Pin)
{
  _Trig_Pin = Trig_Pin;
  _Echo_Pin = Echo_Pin;
  Distance = 0;
  pinMode(_Echo_Pin, INPUT);
  pinMode(_Trig_Pin, OUTPUT);
}

float Ultrasonic::Detect()
{
  digitalWrite(_Trig_Pin, LOW); 
  delayMicroseconds(2);
  digitalWrite(_Trig_Pin, HIGH);
  delayMicroseconds(10);
  digitalWrite(_Trig_Pin, LOW);   
  Distance = pulseIn(_Echo_Pin, HIGH, 3000);
  if (Distance == 0)Distance = 5000;
  return Distance;
}



