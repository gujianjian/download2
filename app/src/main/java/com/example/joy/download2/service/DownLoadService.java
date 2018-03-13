package com.example.joy.download2.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.joy.download2.R;
import com.example.joy.download2.StaticClass.Constant;

import java.io.File;
import java.util.Locale;

/**
 * Created by joy on 2018/3/12.
 */

public class DownLoadService extends Service {

    private DownLoadTask mDownLoadTask;
    public DownLoadTask.DownloadListener downloadListener = new DownLoadTask.DownloadListener() {
        @Override
        public void onSuccess() {
            mDownLoadTask=null;
            getNotificationManager().notify(1,getNotifacation("下载完成",-1));
            stopForeground(true);
            Toast.makeText(DownLoadService.this,"下载完成",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFail() {
            mDownLoadTask=null;
            getNotificationManager().notify(1,getNotifacation("下载失败",-1));
            stopForeground(true);
            Toast.makeText(DownLoadService.this,"下载失败",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            mDownLoadTask=null;
            stopForeground(true);
            Toast.makeText(DownLoadService.this,"Cancel",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStop() {
            mDownLoadTask=null;
            getNotificationManager().notify(1,getNotifacation("暂停下载",-1));
        }

        @Override
        public void onProgress(int progress) {

            getNotificationManager().notify(1,getNotifacation("开始下载",progress));
        }
    };


    public class DownLoadBinder extends Binder {
        public DownLoadService getDownLoadService() {
            return DownLoadService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new DownLoadBinder();
    }

    public void startDownLoad() {

        if (mDownLoadTask == null) {
            mDownLoadTask = new DownLoadTask(downloadListener);
            mDownLoadTask.execute(Constant.DOWNLOAD_URL);
            startForeground(1,getNotifacation("开始下载",0) );
        }
    }


    /**
     * 暂停下载
     */
    public void stopDownLoad() {
       if(mDownLoadTask!=null){
           mDownLoadTask.stopDownload();
       }
    }

    /**
     * 取消下载
     */
    public void cancelDownLoad(){
        if(mDownLoadTask!=null){
            mDownLoadTask.cancelDownload();
        }else{
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            String filename = Constant.DOWNLOAD_URL.substring(Constant.DOWNLOAD_URL.lastIndexOf('/'));
            File file = new File(path, filename);
            if(file.exists()){
                file.delete();
            }
            getNotificationManager().cancel(1);
            stopForeground(true);
            Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show();
        }
    }

    public NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }


    public Notification getNotifacation(String title, int progress){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"update");
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle(title);
        if(progress>0){
            builder.setContentText(progress+"%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }
    @Override
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        downloadListener=null;
    }
}
