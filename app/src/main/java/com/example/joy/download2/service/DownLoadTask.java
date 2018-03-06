package com.example.joy.download2.service;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by joy on 2018/3/6.
 */

public class DownLoadTask extends AsyncTask<String, Integer, Integer> {

    private static final int TYPE_FAILED = 0;
    private static final int TYPE_SUCCESS = 1;
    private static final int TYPE_STOP = 2;
    private static final int TYPE_CANCEL = 3;
    private DownloadListener listener;
    private boolean isCancel = false;
    private boolean isStop = false;
    private int lastProgress = 0;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public DownLoadTask(DownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        long downLoadedLength = 0;
        long contentLength = 0;
        InputStream inputStream = null;
        RandomAccessFile raf = null;

        String downloadUrl = strings[0];
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        String filename = downloadUrl.substring(downloadUrl.lastIndexOf('/'));
        File downloadFile = new File(directory, filename);
        if (downloadFile.exists()) {
            downLoadedLength = downloadFile.length();
        }
        contentLength = getContentLength(downloadUrl);
        if (contentLength == 0) {
            return TYPE_FAILED;
        } else if (contentLength == downLoadedLength) {
            return TYPE_SUCCESS;
        }


        try {
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downLoadedLength + "-")
                    .url(downloadUrl).build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = okHttpClient.newCall(request).execute();
            if (response != null) {
                raf = new RandomAccessFile(downloadFile, "rw");
                raf.seek(downLoadedLength);

                inputStream = response.body().byteStream();
                int len = 0;
                int total = 0;
                byte[] buff = new byte[1024];
                while ((len = inputStream.read(buff)) != -1) {
                    if (isCancel) {
                        return TYPE_CANCEL;
                    } else if (isStop) {
                        return TYPE_STOP;
                    } else {
                        raf.write(buff, 0, len);
                        total += len;
                        int progress = (int) ((total + contentLength) * 100 / contentLength);
                        publishProgress(progress);
                    }


                }
                response.body().close();
                return TYPE_SUCCESS;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return TYPE_FAILED;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case TYPE_CANCEL:
                listener.onCancel();
                break;
            case TYPE_FAILED:
                listener.onFail();
                break;
            case TYPE_STOP:
                listener.onStop();
                break;
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            default:
                break;
        }
    }

    public void stopDownload(){
        isStop=true;
    }

    public void cancelDownload(){
        isCancel=true;
    }

    private long getContentLength(String downloadUrl) {
        try {
            Request request = new Request.Builder().url(downloadUrl).build();
            Response client = new OkHttpClient().newCall(request).execute();
            long contentLength = client.body().contentLength();
            client.body().close();
            return contentLength;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public interface DownloadListener {
        void onSuccess();

        void onFail();

        void onCancel();

        void onStop();

        void onProgress(int progress);
    }


}
