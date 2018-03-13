package com.example.joy.download2.service;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.joy.download2.utils.L;

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

        //获得文件名
        String downloadUrl = strings[0];
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        String filename = downloadUrl.substring(downloadUrl.lastIndexOf('/'));
        File downloadFile = new File(directory, filename);
        //判断文件是否存在
        if (downloadFile.exists()) {
            downLoadedLength = downloadFile.length();
        }
        contentLength = getContentLength(downloadUrl);
        //从网络中获得的文件长度为0
        if (contentLength == 0) {
            return TYPE_FAILED;
        } else if (contentLength == downLoadedLength) {
            //已经下载的文件长度和下载的长度相等
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
                //定位已经下载的长度
                raf.seek(downLoadedLength);
                //获得文件总字节数
                inputStream = response.body().byteStream();
                int len = 0;
                int total = 0;
                byte[] buff = new byte[1024];
                while ((len = inputStream.read(buff)) != -1) {
                    if (isCancel) {
                        //取消
                        return TYPE_CANCEL;
                    } else if (isStop) {
                        //暂停
                        return TYPE_STOP;
                    } else {
                        raf.write(buff, 0, len);
                        total += len;
                        int progress = (int) ((total + downLoadedLength)*100 / contentLength);
                        publishProgress(progress);
                    }


                }
                response.body().close();
                return TYPE_SUCCESS;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream = null;
            }
            if (raf != null) {
                raf = null;

            }
            if (isCancel && downloadFile != null) {
                downloadFile.delete();
            }
        }

        return TYPE_FAILED;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        L.D(progress+"");
        if (progress > lastProgress) {
            //进度有变化就更新
            Log.d("gjj", progress + "");
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

    public void stopDownload() {
        isStop = true;
    }

    public void cancelDownload() {
        isCancel = true;
    }

    /**
     * 获得文件长度size
     *
     * @param downloadUrl
     * @return
     */
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
