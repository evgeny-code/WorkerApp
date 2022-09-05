package com.linksrussia.tsup.workerapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.linksrussia.tsup.workerapp.App;
import com.linksrussia.tsup.workerapp.R;
import com.linksrussia.tsup.workerapp.dto.BluetoothDeviceWrapper;
import com.linksrussia.tsup.workerapp.receivers.DeviceReceiver;
import com.linksrussia.tsup.workerapp.receivers.InfoMessageReceiver;
import com.linksrussia.tsup.workerapp.util.DialogUtil;
import com.linksrussia.tsup.workerapp.util.ES;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SelectDeviceActivity extends AppCompatActivity {
    public static final int ACCESS_COARSE_LOCATION_CODE = 44;
    public static final int ACCESS_FINE_LOCATION_CODE = 55;

    public static final DialogUtil DIALOG_UTIL = new DialogUtil();

    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Map<String, BluetoothDeviceWrapper> bondDevices = new HashMap<>();

    private Consumer<Map<String, BluetoothDeviceWrapper>> renderCallback = new Consumer<Map<String, BluetoothDeviceWrapper>>() {
        @Override
        public void accept(Map<String, BluetoothDeviceWrapper> deviceMap) {
            GridLayout layout = findViewById(R.id.bondedDevicesLayout);
            LayoutInflater layoutInflater = getLayoutInflater();
            layout.removeAllViews();

            if (deviceMap.isEmpty())
                DIALOG_UTIL.infoDialog(SelectDeviceActivity.this, "У вас нет привязанных Bluetooth приборов").show();

            BluetoothDevice selectedDevice = App.selectedDevice;
            deviceMap.forEach((name, bluetoothDeviceWrapper) -> {
                View inflate = layoutInflater.inflate(R.layout.item_device, layout, false);
                ((TextView) inflate.findViewById(R.id.deviceName)).setText(bluetoothDeviceWrapper.getName());
                ((TextView) inflate.findViewById(R.id.deviceAddress)).setText(bluetoothDeviceWrapper.device.getAddress());
                inflate.setOnClickListener(v -> {
                    App.selectedDevice = bluetoothDeviceWrapper.device;
                    Toast.makeText(SelectDeviceActivity.this, "Пытаемся подключиться к прибору", Toast.LENGTH_SHORT).show();
                    ES.restartReceiveData();
                    this.accept(deviceMap);
                });

                if (App.isDeviceConnected()
                        && bluetoothDeviceWrapper.device.getAddress().equals(selectedDevice.getAddress()))
                    ((CheckBox) inflate.findViewById(R.id.checkBox)).setChecked(true);

                layout.addView(inflate);
            });
        }
    };

    private final InfoMessageReceiver infoMessageReceiver = new InfoMessageReceiver();
    private final DeviceReceiver deviceReceiver = new DeviceReceiver(renderCallback);


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);

        registerReceiver(infoMessageReceiver, new IntentFilter(InfoMessageReceiver.INTENT_ACTION));
        registerReceiver(deviceReceiver, new IntentFilter(DeviceReceiver.INTENT_ACTION));

        findViewById(R.id.checkButton).setOnClickListener(v -> {
            if (!SelectDeviceActivity.this.checkOrRequest(Manifest.permission.ACCESS_COARSE_LOCATION, ACCESS_COARSE_LOCATION_CODE)) {
                return;
            }

            if (!SelectDeviceActivity.this.checkOrRequest(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_CODE)) {
                return;
            }

            DIALOG_UTIL.infoDialog(this, "Все необходимые разрешения у приложения есть").show();
        });

        findViewById(R.id.btSettingButton).setOnClickListener(v -> {
            startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
        });

        /*
        findViewById(R.id.searchButton).setOnClickListener(v -> {
            startActivity(new Intent(this, SearchDeviceActivity.class));
        });
        */

        for (BluetoothDevice bondedDevice : bluetoothAdapter.getBondedDevices()) {
            bondDevices.put(bondedDevice.getAddress(), new BluetoothDeviceWrapper(bondedDevice, bondedDevice.getName()));
        }

        renderCallback.accept(bondDevices);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("onRequestPermissionsResult", requestCode + ":  " + Arrays.toString(permissions) + ": " + Arrays.toString(grantResults));
        if (ACCESS_COARSE_LOCATION_CODE == requestCode || ACCESS_FINE_LOCATION_CODE == requestCode) {
            if (0 != grantResults[0])
                DIALOG_UTIL.infoDialog(this, "Все необходимые разрешения у приложения есть").show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(infoMessageReceiver);
        unregisterReceiver(deviceReceiver);
    }

    private boolean checkOrRequest(String permission, int requestCode) {
        if (ActivityCompat.checkSelfPermission(SelectDeviceActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SelectDeviceActivity.this, new String[]{permission}, requestCode);
            return false;
        }

        return true;
    }


}