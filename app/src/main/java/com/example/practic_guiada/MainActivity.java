package com.example.practic_guiada;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Activity activity;
    // Versión de Android
    private TextView versionAndroid;
    private int versionSDK;
    // Batería
    private ProgressBar pbLevelBattery;
    private TextView tvLevelBattery;
    IntentFilter batteryFilter;
    // Cámara
    CameraManager cameraManager;
    String cameraId;
    private Button btnOnLight;
    private Button btnOffLight;
    private ImageButton btnCreateFile;
    // Archivo
    private EditText etNameFile;
    private Archivo archivo;
    // Conexión
    private TextView tvConexion;
    ConnectivityManager conexion;
    // Bluetooth
    private Button btnOnBluetooth;
    private Button btnOffBluetooth;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        begin();

        // Batería
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        // BroadcastReceiver, especie de chismoso que graba los eventos que ocurren en nuestro cel a través de IntentFilter, lo que necesitábamos escuchar
        registerReceiver(broadcastReceiver, batteryFilter);

        // Luz
        this.btnOnLight.setOnClickListener(this::onLight);
        this.btnOffLight.setOnClickListener(this::offLight);

        // Archivo
        archivo = new Archivo(context, activity);
        btnCreateFile.setOnClickListener(this::onSaveFile);

        // Bluetooth
        this.btnOnBluetooth.setOnClickListener(this::onBluetooth);
        this.btnOffBluetooth.setOnClickListener(this::offBluetooth);
    }

    private void begin() {
        this.context = getApplicationContext();
        this.activity = this;
        this.versionAndroid = findViewById(R.id.tvVersionAndroid);
        this.pbLevelBattery = findViewById(R.id.pdLevelBaterry);
        this.tvLevelBattery = findViewById(R.id.tvLevelBaterry);
        this.tvConexion = findViewById(R.id.tvConecxion);
        this.btnOffLight = findViewById(R.id.btnOff);
        this.btnOnLight = findViewById(R.id.btnOn);
        this.etNameFile = findViewById(R.id.etNameFile);
        this.btnCreateFile = findViewById(R.id.btnSaveFile);
        this.btnOffBluetooth = findViewById(R.id.btnOffBluetooth);
        this.btnOnBluetooth = findViewById(R.id.btnOnBluetooth);
    }

    // Obtener versión de SO
    @Override
    protected void onResume() {
        super.onResume();
        String versionSO = Build.VERSION.RELEASE;
        versionSDK = Build.VERSION.SDK_INT;
        versionAndroid.setText("Versión SO: " + versionSO + " / SDK" + versionSDK);
        checkConnection();
    }

    // Control de la linterna
    private void onLight(View view) {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        try {
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void offLight(View view) {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // BroadcastReceiver para el estado de la batería
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int levelBattery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            pbLevelBattery.setProgress(levelBattery);
            tvLevelBattery.setText("Nivel de la batería: " + levelBattery + " %");
        }
    };

    // Verificar conexión a Internet
    private void checkConnection() {
        conexion = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conexion.getActiveNetworkInfo();
        boolean stateNet = network != null && network.isConnectedOrConnecting();
        if (stateNet) tvConexion.setText("Sí hay conexión a Internet");
        else tvConexion.setText("No hay conexión a Internet");
    }

    // Guardar archivo
    private void onSaveFile(View view) {
        String nameFile = etNameFile.getText().toString();
        if (nameFile.isEmpty()) {
            Toast.makeText(context, "Por favor, proporciona un nombre para guardar el archivo", Toast.LENGTH_LONG).show();
        } else {
            archivo.saveFile(nameFile, " ");
        }
    }

    // Habilitar Bluetooth
    public void onBluetooth(View view) {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
        }

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }

    // Deshabilitar Bluetooth
    public void offBluetooth(View view) {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
        }

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }
}
