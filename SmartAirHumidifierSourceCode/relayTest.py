import RPi.GPIO as GPIO
import time

GPIO.setmode(GPIO.BCM)

# init list with pin numbers

# pinList = [26]

# loop through pins and set mode and state to 'low'

# for i in pinList:
#     GPIO.setup(i, GPIO.OUT)
#     GPIO.output(i, GPIO.HIGH)


# time to sleep between operations in the main loop

GPIO.setup(26, GPIO.OUT)
GPIO.output(26, GPIO.HIGH)

SleepTimeL = 10

# main loop

try:
    
    GPIO.output(26, GPIO.LOW)
    print ("ONE")
    pinList = [26, 20, 2, 0, 1, 4, 5, 6, 7]
    while True:
#         print("ON")
        time.sleep(SleepTimeL)
#     for i in pinList:
#         time.sleep(SleepTimeL)
#         if i == 1:
#             GPIO.output(26, GPIO.HIGH)
#         if i == 6:
#             GPIO.output(26, GPIO.LOW)
            
        
#     time.sleep(SleepTimeL);
#   GPIO.output(3, GPIO.LOW)
#   print ("TWO")
#   time.sleep(SleepTimeL);
#   GPIO.output(4, GPIO.LOW)
#   print ("THREE")
#   time.sleep(SleepTimeL);
#   GPIO.output(17, GPIO.LOW)
#   print ("FOUR")
#   time.sleep(SleepTimeL);
#   GPIO.output(27, GPIO.LOW)
#   print ("FIVE")
#   time.sleep(SleepTimeL);
#   GPIO.output(22, GPIO.LOW)
#   print ("SIX")
#   time.sleep(SleepTimeL);
#   GPIO.output(10, GPIO.LOW)
#   print ("SEVEN")
#   time.sleep(SleepTimeL);
#   GPIO.output(9, GPIO.LOW)
#   print ("EIGHT")
#   time.sleep(SleepTimeL);
    GPIO.cleanup()
    print ("Good bye!")

# End program cleanly with keyboard
except KeyboardInterrupt:
  print ("  Quit")

  # Reset GPIO settings
  GPIO.cleanup()