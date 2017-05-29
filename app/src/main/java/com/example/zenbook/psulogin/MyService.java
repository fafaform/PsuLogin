package com.example.zenbook.psulogin;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class MyService extends Service {
    private static final int WIFI_FREQUENCY_BAND_AUTO = 0;
    private static final int WIFI_FREQUENCY_BAND_5GHZ = 1;
    private static final int WIFI_FREQUENCY_BAND_2GHZ = 2;
    private int notificationID = 1;
    private IBinder mBinder;
    /**
     * indicates whether onRebind should be used
     */
    private boolean mAllowRebind;
    private Notification n;
    private WifiManager wifiManager;
    private WifiConfiguration conf;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            System.out.println("Enable");
            wifiManager.setWifiEnabled(true);
        }else{
            System.out.println("Already Enabled");
        }

        registerReceiver(new ScanReceiver(), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        ScanWifi();

        return START_STICKY;
    }
    
    private void ScanWifi(){
        conf = new WifiConfiguration();
    
        List<ScanResult> scanResults = wifiManager.getScanResults();
        //        String networkSSID = "PSU WiFi (802.1x)";
//        String networkPass = "m37mu36r9*";
        String networkSSID = "wichai.";
        String networkPass = "242213470";
    
        conf.SSID = "\"" + networkSSID + "\"";
//        conf.preSharedKey = "\""+ networkPass +"\"";
    
    
        //TODO: test connection
    
        ScanResult scanResult = wifiManager.getScanResults().get(0);
        System.out.println("--------------------------------------------------------------------------");
        for (ScanResult i : scanResults) {
            System.out.println(i.SSID+" "+i.frequency+" "+i.level+" "+i.BSSID+" "+i.capabilities);
            System.out.println(i.SSID + ":" + networkSSID);
            if (i.SSID.equals(networkSSID)) {
                scanResult = i;
                System.out.println("SELECTED");
            }
        }
            System.out.println("--------------------------------------------------------------------------");
        conf.status = WifiConfiguration.Status.ENABLED;
        conf.priority = 40;
        
            if (scanResult.capabilities.toUpperCase().contains("WEP")) {
            Log.v("rht", "Configuring WEP");
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        
            if (networkPass.matches("^[0-9a-fA-F]+$")) {
                conf.wepKeys[0] = networkPass;
            } else {
                conf.wepKeys[0] = "\"".concat(networkPass).concat("\"");
            }
        
            conf.wepTxKeyIndex = 0;
        
        } else if (scanResult.capabilities.toUpperCase().contains("WPA")) {
            Log.v("rht", "Configuring WPA");
        
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        
            conf.preSharedKey = "\"" + networkPass + "\"";
        
        } else {
            Log.v("rht", "Configuring OPEN network");
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            conf.allowedAuthAlgorithms.clear();
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }
    
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int networkId = wifiManager.addNetwork(conf);
        Log.v("rht", "Add result " + networkId);
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        System.out.println(wifiInfo.getLinkSpeed());
    }

    private class ScanReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            ScanWifi();
        }
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {
    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        wifiManager.setWifiEnabled(false);
    }
}
