package com.linksrussia.tsup.workerapp.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
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
import com.linksrussia.tsup.workerapp.receivers.InfoMessageReceiver;
import com.linksrussia.tsup.workerapp.util.BleUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchDeviceActivity extends AppCompatActivity {

    public static final int ACCESS_COARSE_LOCATION_CODE = 44;
    public static final int ACCESS_FINE_LOCATION_CODE = 55;

    private final InfoMessageReceiver infoMessageReceiver = new InfoMessageReceiver();

    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();


    /*
    private final Cache<String, BluetoothDeviceWrapper> bluetoothDevices = CacheBuilder.newBuilder()
            .expireAfterWrite(100, TimeUnit.SECONDS)
            .build();
    */


    private final Map<String, BluetoothDeviceWrapper> findDevices = new HashMap<>();

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String name = device.getName();
            if (null == name) {
                name = BleUtil.parseAdertisedData(scanRecord).getName();
            }
            findDevices.put(device.getAddress(), new BluetoothDeviceWrapper(device, name));
            Log.i("onLeScan device", name + ":  " + device.getAddress());

            renderDevices(findViewById(R.id.findDevicesLayout), findDevices);
        }
    };

    private ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            switch (callbackType) {
                case ScanSettings.CALLBACK_TYPE_ALL_MATCHES:
                    Log.d("callbackType", "Scanner: ALL_MATCHES");
                    break;
                case ScanSettings.CALLBACK_TYPE_FIRST_MATCH:
                    Log.d("callbackType", "Scanner: FIRST_MATCH:");
                    break;
                case ScanSettings.CALLBACK_TYPE_MATCH_LOST:
                    Log.d("callbackType", "Scanner: MATCH_LOST :");
                    break;

                default:
                    Log.e("callbackType", "Scanner: Unknown callback type!");
                    break;
            }


            BluetoothDevice device = result.getDevice();

            String name = device.getName();
            if (null == name) {
                name = result.getScanRecord().getDeviceName();
            }

            findDevices.put(device.getAddress(), new BluetoothDeviceWrapper(device, name));
            Log.i("onScanResult device", name + ":  " + device.getAddress());

            renderDevices(findViewById(R.id.findDevicesLayout), findDevices);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult scanResult : results) {
                onScanResult(0, scanResult);
            }

        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);

        registerReceiver(infoMessageReceiver, new IntentFilter(InfoMessageReceiver.INTENT_ACTION));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<ScanFilter> scanFilters = new ArrayList<>();

            ScanSettings scanSettings = new ScanSettings.Builder()
                    .setReportDelay(0)
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            bluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback);
        } else {
            bluetoothAdapter.startLeScan(leScanCallback);
        }
    }

    @SuppressLint({"MissingPermission"})
    private void renderDevices(GridLayout layout, Map<String, BluetoothDeviceWrapper> deviceMap) {
        LayoutInflater layoutInflater = getLayoutInflater();
        layout.removeAllViews();

        BluetoothDevice selectedDevice = App.selectedDevice;
        deviceMap.forEach((name, bluetoothDeviceWrapper) -> {
            View inflate = layoutInflater.inflate(R.layout.item_device, layout, false);
            ((TextView) inflate.findViewById(R.id.deviceName)).setText(bluetoothDeviceWrapper.getName());
            ((TextView) inflate.findViewById(R.id.deviceAddress)).setText(bluetoothDeviceWrapper.device.getAddress());
            inflate.setOnClickListener(v -> {
                bluetoothDeviceWrapper.device.setPin("1234".getBytes(StandardCharsets.UTF_8));
                boolean bond = bluetoothDeviceWrapper.device.createBond();
                Log.i("bond", "bond result = " + bond);
            });

            if (App.isDeviceConnected()
                    && bluetoothDeviceWrapper.device.getAddress().equals(selectedDevice.getAddress()))
                ((CheckBox) inflate.findViewById(R.id.checkBox)).setChecked(true);

            layout.addView(inflate);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("onRequestPermissionsResult", requestCode + ":  " + Arrays.toString(permissions) + ": " + Arrays.toString(grantResults));
        if (ACCESS_COARSE_LOCATION_CODE == requestCode || ACCESS_FINE_LOCATION_CODE == requestCode) {
            if (0 != grantResults[0])
                Toast.makeText(this, "Все необходимые разрешения у приложения есть", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(infoMessageReceiver);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothLeScanner.stopScan(scanCallback);
        } else {
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    private boolean checkOrRequest(String permission, int requestCode) {
        if (ActivityCompat.checkSelfPermission(SearchDeviceActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SearchDeviceActivity.this, new String[]{permission}, requestCode);
            return false;
        }

        return true;
    }


}