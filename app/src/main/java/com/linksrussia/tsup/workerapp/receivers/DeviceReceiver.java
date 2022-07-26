package com.linksrussia.tsup.workerapp.receivers;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.linksrussia.tsup.workerapp.dto.BluetoothDeviceWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DeviceReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION = "DeviceReceiver";

    private final Consumer<Map<String, BluetoothDeviceWrapper>> renderCallback;

    public DeviceReceiver(Consumer<Map<String, BluetoothDeviceWrapper>> renderCallback) {
        this.renderCallback = renderCallback;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final Map<String, BluetoothDeviceWrapper> bondDevices = new HashMap<>();
        for (BluetoothDevice bondedDevice : bluetoothAdapter.getBondedDevices()) {
            bondDevices.put(bondedDevice.getAddress(), new BluetoothDeviceWrapper(bondedDevice, bondedDevice.getName()));
        }

        renderCallback.accept(bondDevices);
    }
}
