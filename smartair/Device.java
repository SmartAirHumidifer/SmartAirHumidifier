package com.example.smartair;

public class Device {
    String deviceName;
    String deviceModelNo;

    public Device(){

    }

    public Device(String deviceName, String deviceModelNo) {
        this.deviceName = deviceName;
        this.deviceModelNo = deviceModelNo;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceModelNo() {
        return deviceModelNo;
    }
}
