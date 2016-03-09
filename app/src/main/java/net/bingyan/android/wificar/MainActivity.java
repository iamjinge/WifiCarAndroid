package net.bingyan.android.wificar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Fragment currentFragment;

    private ManualFragment manualFragment;
    private FreeFragment freeFragment;
    private WallFragment wallFragment;
    private BackToAimFragment backFragment;

    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private String targetWifi;
    private boolean targetConnect;
    private String preConfigSSID;
    private int targetConfigId;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initView();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

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
        manualFragment = new ManualFragment();
        freeFragment = new FreeFragment();
        wallFragment = new WallFragment();
        backFragment = new BackToAimFragment();

        currentFragment = manualFragment;
        navigation();
    }

    void start() {
        GetImageTask.getInstance().startTask();
        SocketTask.getInstance().start();
    }

    void navigation() {
        getSupportFragmentManager().beginTransaction().replace(R.id.containerFrameLayout, currentFragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SocketTask.getInstance().stop();
        GetImageTask.getInstance().stop();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_manual:
                currentFragment = manualFragment;
                navigation();
                break;
            case R.id.action_free:
                currentFragment = freeFragment;
                navigation();
                break;
            case R.id.action_wall:
                currentFragment = wallFragment;
                navigation();
                break;
            case R.id.action_back:
                currentFragment = backFragment;
                navigation();
                break;
            case R.id.action_choose:
                startActivity(new Intent(this, ChooseColorActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_start:
                start();
                break;
            case R.id.action_to_detect:
                startActivity(new Intent(this, ColorDetectActivity.class));
                break;
            case R.id.action_basic_control:
                startActivity(new Intent(this, BasicControlActivity.class));
                break;
            case R.id.action_to_code:
                startActivity(new Intent(this, CodeControlActivity.class));
                break;
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
                    }
                }
            }
        }
    }
}
