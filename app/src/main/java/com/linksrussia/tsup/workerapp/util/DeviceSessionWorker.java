package com.linksrussia.tsup.workerapp.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;

public class DeviceSessionWorker {

    public static BluetoothSocket createBluetoothSocket(BluetoothDevice bluetoothDevice) {
        try {
            Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            return (BluetoothSocket) m.invoke(bluetoothDevice, 1);
        } catch (InvocationTargetException e) {
            Log.e("BluetoothSocket", "InvocationTargetException", e);
        } catch (NoSuchMethodException e) {
            Log.e("BluetoothSocket", "NoSuchMethodException", e);
        } catch (IllegalAccessException e) {
            Log.e("BluetoothSocket", "IllegalAccessException", e);
        }

        Log.e("ERROR_MESSAGE", "Не могу создать Bluetooth Socket");

        return null;
    }

    @SuppressLint("MissingPermission")
    public static BluetoothSocket getConnectedSocket(BluetoothDevice bluetoothDevice) {
        Thread currentThread = Thread.currentThread();
        int connectFailureCounter = 0;
        while (true) {
            BluetoothSocket bluetoothSocket = createBluetoothSocket(bluetoothDevice);
            try {
                bluetoothSocket.connect();
                Log.i("BluetoothSocket", "Connected!!!");
                ES.infoToast("Прибор подключен!!!");
                return bluetoothSocket;
            } catch (Exception e) {
                Log.e("BluetoothSocket", "Cant connect");
                ES.infoToast("Не могу подключиться к прибору, попробую снова через 3сек");
                closeSocket(bluetoothSocket);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Log.e("DATA", "Cant sleep", ie);
                    currentThread.interrupt();
                }

                if (10 < ++connectFailureCounter) {
                    break;
                }
            }

            if (currentThread.isInterrupted())
                break;
        }

        return null;
    }

    public static void closeSocket(BluetoothSocket bluetoothSocket) {
        if (null != bluetoothSocket && bluetoothSocket.isConnected()) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e("DATA", "Can't close socket", e);
            }
        }
    }


    public static void receiveData(BluetoothSocket bluetoothSocket, Consumer<String> dataConsumer) throws IOException {
        if (null == bluetoothSocket)
            return;

        Thread currentThread = Thread.currentThread();
        byte[] buff = new byte[128];
        try (DataInputStream dis = new DataInputStream(bluetoothSocket.getInputStream())) {
            while (true) {
                int length = dis.read(buff);
                String data = new String(Arrays.copyOfRange(buff, 0, length));
                dataConsumer.accept(data);

                if (currentThread.isInterrupted())
                    break;
            }
        }
    }
}
