package com.example.zenbook.psulogin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private int notificationID = 1;
    private IBinder mBinder;
    /** indicates whether onRebind should be used */
    private boolean mAllowRebind;
    private Notification n;
    private WifiManager wifiManager;
    private WifiConfiguration conf;

    public MyService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(0, n);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        conf = new WifiConfiguration();

        List<ScanResult> scanResults = wifiManager.getScanResults();
        String networkSSID = "wichai";
        String networkPass = "242213470";

        conf.SSID = "\"" + networkSSID + "\"";
        conf.preSharedKey = "\""+ networkPass +"\"";

        if(!wifiManager.isWifiEnabled()){
//            System.out.println("Enable");
            wifiManager.setWifiEnabled(true);
        }else{
//            System.out.println("Already Enabled");
        }

        for( ScanResult i : scanResults ) {
            System.out.println(i.SSID+" "+i.frequency+" "+i.level+" "+i.capabilities);
        }

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }

        return START_STICKY;
    }

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent){}

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        wifiManager.setWifiEnabled(false);
    }
}
