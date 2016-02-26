package net.bingyan.android.wificar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GetImageTask.ImageTaskListener, View.OnTouchListener {

    private static final String TAG = "MainActivity";
    private Button showImageButton;
    private ImageView imageView;
    private Button forward;
    private Button left;
    private Button right;
    private Button backward;

    private GetImageTask imageTask;
    private SocketTask socketTask;

    private Handler handler = new Handler(Looper.getMainLooper());
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private String targetWifi;
    private boolean targetConnect;
    private String preConfigSSID;
    private int targetConfigId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initView();

    }

    void init() {
        wifiReceiver = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, intentFilter);

        targetWifi = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_key_wifi_ssid), getString(R.string.pref_default_wifi_ssid));

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        } else {
            Log.d(TAG, "start scan");
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (TextUtils.equals(wifiInfo.getSSID(), targetWifi)) {
                Log.d(TAG, "wifi connected to : " + wifiInfo.getSSID());
                targetConnect = true;
                taskStart();
            }
            List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration config : wifiConfigs) {
                if (TextUtils.equals(wifiInfo.getSSID(), config.SSID)) {
                    preConfigSSID = config.SSID;
                    Log.d(TAG, "get pre wifi connected to : " + wifiInfo.getSSID());
                } else if (TextUtils.equals(targetWifi, config.SSID)) {
                    targetConfigId = config.networkId;
                }
            }
            wifiManager.startScan();
        }
    }

    void initView() {
        imageView = (ImageView) findViewById(R.id.imageView);
        showImageButton = (Button) findViewById(R.id.showImage);

        showImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (imageTask.isPause())
//                    imageTask.resume();
//                else imageTask.pause();

                imageTask = new GetImageTask("http://192.168.1.1:8080/?action=stream", MainActivity.this);
                new Thread(imageTask).start();
                socketTask = new SocketTask();
            }
        });

        forward = (Button) findViewById(R.id.moveForward);
        left = (Button) findViewById(R.id.moveLeft);
        right = (Button) findViewById(R.id.moveRight);
        backward = (Button) findViewById(R.id.moveBackward);

        forward.setOnTouchListener(this);
        left.setOnTouchListener(this);
        right.setOnTouchListener(this);
        backward.setOnTouchListener(this);
    }

    void taskStart() {
//        imageTask = new GetImageTask("http://192.168.1.1:8080/?action=stream", this);
//        new Thread(imageTask).start();
//        socketTask = new SocketTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageTask != null)
            imageTask.stop();
        if (socketTask != null)
            socketTask.stop();

        unregisterReceiver(wifiReceiver);

        wifiManager.disconnect();
        wifiManager.removeNetwork(targetConfigId);
        wifiManager.saveConfiguration();

        if (!TextUtils.isEmpty(preConfigSSID)) {
            List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration config : wifiConfigs) {
                if (TextUtils.equals(preConfigSSID, config.SSID)) {
                    wifiManager.enableNetwork(config.networkId, true);
                    break;
                }
            }
        }
        wifiManager.reconnect();
    }

    @Override
    public void getImage(final byte[] imageData) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.moveForward:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.carForward();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    socketTask.carStop();
                }
                break;
            case R.id.moveLeft:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.carLeft();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    socketTask.carStop();
                }
                break;
            case R.id.moveRight:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.carRight();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    socketTask.carStop();
                }
                break;
            case R.id.moveBackward:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.carBackward();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    socketTask.carStop();
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "receive : " + action);
            if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                    //do stuff
                    wifiManager.startScan();
                } else {
                    // wifi connection was lost
                    Toast.makeText(context, "Wifi disconnect", Toast.LENGTH_SHORT).show();
                }
            } else if (TextUtils.equals(action, WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                if (!targetConnect) {
                    List<ScanResult> wifiScanList = wifiManager.getScanResults();
                    boolean findTarget = false;
                    for (ScanResult result : wifiScanList) {
                        if (!targetConnect && TextUtils.equals(result.SSID, targetWifi)) {
                            Toast.makeText(context, "find " + targetWifi, Toast.LENGTH_SHORT).show();
                            WifiConfiguration config = new WifiConfiguration();
                            config.SSID = String.format("\"%s\"", targetWifi);
                            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                            int netId = wifiManager.addNetwork(config);
                            wifiManager.disconnect();
                            wifiManager.enableNetwork(netId, true);
                            wifiManager.reconnect();
                            targetConfigId = netId;
                            Log.d(TAG, "try wifi connect ");
                            findTarget = true;
                            break;
                        }
                    }
                    String pattern;
                    if (!findTarget) {
                        pattern = "Not found wifi [%s]";
                    } else {
                        pattern = "Found wifi [%s], connecting";
                    }
                    Toast.makeText(context, String.format(pattern, targetWifi), Toast.LENGTH_SHORT).show();
                }
            } else if (TextUtils.equals(action, WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                Log.d(TAG, "wifi connected : " + networkInfo.isConnected() + " " + networkInfo.toString());
                if (networkInfo.isConnected()) {
                    WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    if (TextUtils.equals(String.format("\"%s\"", targetWifi), wifiInfo.getSSID())) {
                        Log.d(TAG, "target  wifi connect success");
                        targetConnect = true;
                        Toast.makeText(context, "wifi connected", Toast.LENGTH_SHORT).show();
                        taskStart();
                    }
                }
            }
        }
    }
}
