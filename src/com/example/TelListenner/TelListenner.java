package com.example.TelListenner;

/**
 * Created by Juntao Zhen on 2014/11/2.
 */

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.example.CallRec.R;


public class TelListenner extends Service {

    private final String tag = "com.tel.listner";
    @Override
    public void onCreate() {
        /* 取得电话服务 */
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TelListner listener = new TelListner();
        // 监听电话的状态
        telManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.v("Juntao Zhen", "Call recorder service exited......");
        super.onDestroy();
    }

    class TelListner extends PhoneStateListener {
        private String number;//定义一个监听电话号码
        private boolean isRecord;//定义一个当前是否正在复制的标志
        private MediaRecorder recorder;//媒体复制类

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.v("Juntao.zhen", incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:/* 无任何状态 */
                    showNotification("Call Recorder", TelephonyManager.EXTRA_STATE_IDLE);
                    number = null;
                    if (recorder != null && isRecord) {
                        Log.e("msg", "record ok");
                        recorder.stop();//录音完成
                        recorder.reset();
                        recorder.release();
                        isRecord = false;//录音完成，改变状态标志
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:/* 接起电话 */
                    // 录制声音，这是录音的核心代码
                    showNotification("Call Recorder", "CALL_STATE_OFFHOOK");
                    Log.i("Juntao.Zhen", TelephonyManager.EXTRA_INCOMING_NUMBER + TelephonyManager.EXTRA_STATE_OFFHOOK);
                    number = incomingNumber;
                    Log.e("msg", "recording");
                    Log.e("msg", incomingNumber);
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);// 定义声音来自于通话
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//存储格式
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置编码
                    SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd-HH:mm:ss");//此处定义一个format类，方便对录音文件进行命名
                    String fileName = incomingNumber + "_" + format.format(new Date());
                    File file = new File(Environment.getExternalStorageDirectory(), "myapp");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    recorder.setOutputFile(file.getAbsolutePath() + File.separator + fileName + ".amr");
                    try {
                        recorder.prepare();
                        recorder.start(); // 开始刻录
                        isRecord = true;
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:/* 电话进来 */
                    showNotification("Call Recorder", "CALL_STATE_RINGING");
                    Log.i("Juntao.Zhen", TelephonyManager.EXTRA_INCOMING_NUMBER + TelephonyManager.EXTRA_STATE_RINGING);
                    Log.e("msg", "coming");
                    number = incomingNumber;
                    getContactPeople(incomingNumber);
                    break;

                default:
                    break;
            }
        }

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
    /**
     * 根据来电号码查找联系人
     *
     * @param incomingNumber
     */
    private void getContactPeople(String incomingNumber) {

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = null;

        //查询字段名称
        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        //根据电话号码查找该联系人
        cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[]
                        {incomingNumber}, null);

        /* 找不到联系人 */
        if (cursor.getCount() == 0) {
            Log.e("msg", "unknown Number:" + incomingNumber);
        } else if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String name = cursor.getString(1);
            Log.e("msg", name + " :" + incomingNumber);
        }
        cursor.close();
        cursor = null;
    }

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }
}
