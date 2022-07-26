package com.linksrussia.tsup.workerapp.dto;

import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceWrapper {
    public BluetoothDevice device;
    public String name;

    public BluetoothDeviceWrapper(BluetoothDevice device, String name) {
        this.device = device;
        this.name = name;
    }

    public String getName() {
        if (null != name)
            return name;
        return "Неизвестное устройство";
    }

}
