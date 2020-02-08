package com.connort6.bluetoothprinter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private String uuid;
    private Button bt1;
    private TextView tv;
    private TextView tv2;
    private BluetoothAdapter bluetoothAdapter;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt1 = findViewById(R.id.button);
        tv = findViewById(R.id.tv);
        tv2 = findViewById(R.id.tv2);
        editText = findViewById(R.id.editText);
    }

    public void btClick(View view) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        if (bluetoothSocket == null) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            String deviceNames = "";
            String macs = "";
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    if (deviceName.equalsIgnoreCase("mtp-3")) {
                        bluetoothDevice = device;
                        uuid = device.getUuids()[0].toString();
                        // break;
                    }
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    deviceNames += deviceName + "\n";
                    macs += deviceHardwareAddress + "\n";
                }
            }
            tv.setText(deviceNames);
            tv2.setText(macs);

            if (bluetoothDevice != null) {
                try {
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    bluetoothSocket.connect();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream = bluetoothSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        try {
            BitmapByte.setOutputStream(outputStream);
            BitmapByte.createImageFromString(editText.getText().toString());
            outputStream.write(new byte[]{10});

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Toast.makeText(this,"Bluetooth Activated",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"Sorry couldn't enable bluetooth",Toast.LENGTH_LONG).show();
        }
    }*/
}
