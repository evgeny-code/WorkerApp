package com.linksrussia.tsup.workerapp.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.linksrussia.tsup.workerapp.App;
import com.linksrussia.tsup.workerapp.dto.DeviceData;
import com.linksrussia.tsup.workerapp.receivers.DataReceiver;
import com.linksrussia.tsup.workerapp.receivers.DeviceReceiver;
import com.linksrussia.tsup.workerapp.receivers.InfoMessageReceiver;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ES {
    public static final Gson GSON = new Gson();

    public static final DataParser DATA_PARSER = new DataParser();
    public static Future<?> DEVICE_DATA_FUTURE;
    public static Future<?> SAVE_DATA_FUTURE;

    public static void infoToast(String text) {
        Log.e("infoToast", text);
        App.context.sendBroadcast(new Intent(InfoMessageReceiver.INTENT_ACTION).putExtra(InfoMessageReceiver.TEXT_EXTRA, text));
    }

    private static ExecutorService DEVICE_EXECUTOR;

    public static void restartReceiveData() {
        if (null != DEVICE_EXECUTOR) {
            Log.d("DEVICE_EXECUTOR", "shutdown: " + DEVICE_EXECUTOR.shutdownNow());
        }

        DEVICE_EXECUTOR = Executors.newFixedThreadPool(2);

        BluetoothDevice selectedDevice = App.selectedDevice;
        ES.DEVICE_DATA_FUTURE = ES.DEVICE_EXECUTOR.submit(() -> {
            Thread currentThread = Thread.currentThread();
            while (true) {
                BluetoothSocket bluetoothSocket = DeviceSessionWorker.getConnectedSocket(selectedDevice);
                if (null == bluetoothSocket) {
                    infoToast("Не могу подключиться к прибору");
                    App.deviceConnected = false;
                    break;
                }

                App.deviceConnected = true;
                App.context.sendBroadcast(new Intent(DeviceReceiver.INTENT_ACTION));

                try {
                    DeviceSessionWorker.receiveData(bluetoothSocket, data -> ES.DATA_PARSER.putData(data.trim()));
                } catch (IOException e) {
                    Log.e("DATA", "Can't receive data");
                }

                DeviceSessionWorker.closeSocket(bluetoothSocket);

                if (currentThread.isInterrupted())
                    break;
            }
        });

        ES.SAVE_DATA_FUTURE = ES.DEVICE_EXECUTOR.submit(() -> {
            Thread currentThread = Thread.currentThread();
            while (true) {
                if (ES.DATA_PARSER.hasNewData()) {
                    List<DeviceData> data = ES.DATA_PARSER.getData();
                    Log.i("DATA", data.toString());

                    data.stream().forEach(dd -> {
                        App.context.sendBroadcast(new Intent(DataReceiver.INTENT_ACTION).putExtra(DataReceiver.DATA_EXTRA, GSON.toJson(dd)));
                    });
                }

                if (currentThread.isInterrupted())
                    break;
            }
        });
    }
}
