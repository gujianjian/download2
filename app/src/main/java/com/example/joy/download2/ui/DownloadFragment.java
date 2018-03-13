package com.example.joy.download2.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.joy.download2.R;
import com.example.joy.download2.service.DownLoadService;

/**
 * Created by joy on 2018/3/12.
 */

public class DownloadFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private Button btn_start_load;
    private Button btn_stop_load;
    private Button btn_cancel_load;
    private DownLoadService mDownLoadService;

    public ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DownLoadService.DownLoadBinder binder = (DownLoadService.DownLoadBinder) iBinder;
            mDownLoadService = binder.getDownLoadService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_download, null);

        initView();
        initData();
        return mView;
    }

    private void initData() {
        Intent download = new Intent(getContext(), DownLoadService.class);
        getContext().startService(download);
        getContext().bindService(download, connection, Context.BIND_AUTO_CREATE);
        //6.0申请权限
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void initView() {
        btn_start_load = mView.findViewById(R.id.btn_start_load);
        btn_start_load.setOnClickListener(this);
        btn_stop_load = mView.findViewById(R.id.btn_stop_load);
        btn_stop_load.setOnClickListener(this);
        btn_cancel_load = mView.findViewById(R.id.btn_cancel_load);
        btn_cancel_load.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_load:
                mDownLoadService.startDownLoad();
                break;
            case R.id.btn_stop_load:
                mDownLoadService.stopDownLoad();
                break;
            case R.id.btn_cancel_load:
                mDownLoadService.cancelDownLoad();
                break;
            default:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "拒绝权限不能下载", Toast.LENGTH_SHORT).show();
                } else {
                    mDownLoadService.startDownLoad();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unbindService(connection);
    }

}
