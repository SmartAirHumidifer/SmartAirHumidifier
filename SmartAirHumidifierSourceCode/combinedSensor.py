# SPDX-FileCopyrightText: 2021 ladyada for Adafruit Industries
# SPDX-License-Identifier: MIT

import pyrebase
import time
from time import sleep
import board
import adafruit_dht
#!/usr/bin/env python3
import RPi.GPIO as GPIO  # import GPIO
from hx711 import HX711  # import the class HX711




# Initial the dht device, with data pin connected to:
dhtDevice = adafruit_dht.DHT22(board.D4)

# you can pass DHT22 use_pulseio=False if you wouldn't like to use pulseio.
# This may be necessary on a Linux single board computer like the Raspberry Pi,
# but it will not work in CircuitPython.
# dhtDevice = adafruit_dht.DHT22(board.D18, use_pulseio=False)


    
    

config = {
  # You can get all these info from the firebase website. It's associated with your account.
  "apiKey": "apiKey",
  "authDomain": "authDomain.firebaseapp.com",
  "databaseURL": "databaseURL.firebaseio.com/",
  "storageBucket": "storageBucke.appspot.com"
}
user = None

def GetAuthorized(firebase):
    global user
    auth = firebase.auth()  # Get a reference to the auth service
    # authenticate a user
    try:
        user = auth.sign_in_with_email_and_password("username",
                                                    "password")  # username and password of your account for database
        print(user)  # display the user information, if successful
    except:
        print("Not authorized")
        user = None

# The function to initialize the database.
# ====================================================================================================
def dbInitialization():
    firebase = pyrebase.initialize_app(config)  # has to initialize the database
    GetAuthorized(firebase)  # get authorized to operate on the database.
    return firebase

# The function to get the data from firebase database.
# ====================================================================================================
def GetDatafromFirebase(db):
    results = db.child("data").get(user["idToken"]).val();  # needs the authorization to get the data.
    print("These are the records from the Database")
    print(results)
    return;

# The function to send the data to firebase database.
# ====================================================================================================
def sendtoFirebase(db, sensordata):
    result = db.child("data").push(sensordata, user["idToken"])  # needs the authorization to save the data
    print(result)
    return;


# The function to send the data to firebase database's user authorized section.
# Each user has a separate record tree, and it is only accessible for the authorized users.
# ====================================================================================================
def sendtoUserFirebase(db, sensordata):
    userid = user["localId"] # this will guarantee the data is stored into the user directory.
    result = db.child("userdata").child(userid).push(sensordata, user["idToken"])  # needs the authorization to save the data
    print(result)
    return;

# The function to set up the record structure to be written to the database.
# ====================================================================================================
def setupData(temp, humidity, timestamp, weight):
    sensor = {"temperature": temp,
              "humidity": humidity,
              "weight": weight,
              "timestamp": timestamp}  # always post the timestamps in epoch with the data to track the timing.
    # Store the data as the dictionary format in python  # refer to here:
    # https://www.w3schools.com/python/python_dictionaries.asp
    return sensor

# The function to retrieve data on the relay board from the database.
# ====================================================================================================
def getRelayData(db):
    relayResult = db.child("Relay").child("state").get(user["idToken"]).val(); #get state of gpio pin
    return relayResult;
    
# The function to set the state of the relay depending on the data from the database.
# ====================================================================================================   
def relay(db):
    
    pinOut = getRelayData(db)
    
    relayS = str(board.D26)
    i = int(relayS)

    #set mode and state to 'low'

    GPIO.setup(i, GPIO.OUT)
    GPIO.output(i, GPIO.HIGH)

    # time to sleep between operations in the main loop

    SleepTimeL = 10

    # main loop

    if pinOut == 1:        
      GPIO.output(26, GPIO.HIGH)
      print ("Relay OFF")
    if pinOut == 0:        
      GPIO.output(26, GPIO.LOW)
      print ("Relay ON")
    return;
    
     
# The function to initialize the load cell object.
# ====================================================================================================    
def loadIt():
#     GPIO.setmode(GPIO.BCM)  # set GPIO pin mode to BCM numbering
    # Create an object hx which represents your real hx711 chip
    # Required input parameters are only 'dout_pin' and 'pd_sck_pin'
    doutS = str(board.D14)
    pdS = str(board.D15)
    dout = int(doutS)
    pd = int(pdS)
    hx = HX711(dout_pin=dout, pd_sck_pin=pd)
    # measure tare and save the value as offset for current channel
    # and gain selected. That means channel A and gain 128

    err = hx.zero()
    print(err)
    # check if successful
    if err:
        raise ValueError('Tare is unsuccessful.')

    reading = hx.get_raw_data_mean()
    if reading:  # always check if you get correct value or only False
        # now the value is close to 0
        print('Data subtracted by offset but still not converted to units:',
              reading)
    else:
        print('invalid data', reading)
    
    return hx

# The Main control function.
# ====================================================================================================
def main():
    try:
        count = 0
        firebase = dbInitialization()
        hx = loadIt()
        
        ratio = 299.939
        hx.set_scale_ratio(ratio)  # set ratio for current channel
        print('Ratio is set.', ratio)
        while True:
            try:
                
                relay(firebase.database())
                # Print the values to the serial port
                temperature_c = dhtDevice.temperature
                temperature_f = temperature_c * (9 / 5) + 32
                humidity = dhtDevice.humidity
                
                
                print('Current weight on the scale in grams is: ')
            
                weight = hx.get_weight_mean()
                hx.reset()
                print(weight)
                
                
                load = weight
                print(
                    "Temp: {:.1f} F / {:.1f} C    Humidity: {}%    Weight: {} ".format(
                        temperature_f, temperature_c, humidity, load
                    )
                )
                
                temp = str(temperature_c) + " C"
                humid = str(humidity) + "%"
                percent = (load / 1800) * 100
                x = round(percent, 2) 
                loadPercent = str(x) + "%"
                print(loadPercent)
                sensordata = setupData(temp,
                                   humid,
                                   int(time.time()),
                                    loadPercent)
                sendtoFirebase(firebase.database(), sensordata)  # save to the public access data tree
                sendtoUserFirebase(firebase.database(), sensordata) # save to the user specific userdata tree   
                count += 1
                sleep(3)
                print ("Analog Signal Generated from D/A Output")
                if (count == 15):    # exit the program after 10 readings. 
                 dhtDevice.exit()
                 break;
            except RuntimeError as error:
                # Errors happen fairly often, DHT's are hard to read, just keep going
                print(error.args[0])
                time.sleep(2.0)
                continue
            except Exception as error:
                dhtDevice.exit()
                raise error
        GetDatafromFirebase(firebase.database())  # this statement is outside the while loop  
    except (KeyboardInterrupt, SystemExit):
        print('Bye :)')

    finally:
        GPIO.cleanup()
        time.sleep(2.0)
    
if __name__=="__main__":
   main()
