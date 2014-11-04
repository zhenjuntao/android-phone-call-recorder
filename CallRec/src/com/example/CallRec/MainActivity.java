package com.example.CallRec;

import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.app.NotificationManager;
import android.app.PendingIntent;
import com.example.TelReceiver.TelReceiver;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.e("Juntao Zhen:", "KEYCODE_BACK");
            showNotification("Call Recorder", "I am still alive......");
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showNotification(CharSequence Title, CharSequence Text) {

        // 创建一个NotificationManager的引用
        NotificationManager notificationManager = (
                NotificationManager) this.getSystemService(
                android.content.Context.NOTIFICATION_SERVICE);
        // 定义Notification的各种属性
        Intent intent = new Intent(this, this.getClass());
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                                        .setContentTitle(Title)
                                        .setContentText(Text)
                                        .setOngoing(true)
                                        .setAutoCancel(true)
                                        .setLights(Color.BLUE,200,5000)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentIntent(contentIntent)
                                        .build();
        notificationManager.notify(0, notification);
    }
}
