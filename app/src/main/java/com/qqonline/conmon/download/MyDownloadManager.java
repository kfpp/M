package com.qqonline.conmon.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;

import java.io.File;

/**
 * Created by YE on 2015/8/13 0013.
 */
public class MyDownloadManager {
    private static boolean isDownloading=false;
    private String mUrl;
    private String mFileName;
    private DownloadManager downloadManager;
    private long currentDownloadId;
    private BroadcastReceiver receiver;
    private IntentFilter filter;
    public MyDownloadManager(String url,String fileName) {
        this.mFileName=fileName;
        this.mUrl=url;
        downloadManager=(DownloadManager) MPFApp.getInstance().getSystemService(MPFApp.getInstance().DOWNLOAD_SERVICE);

        filter=new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

    }
    private void installAPK(long downloadID) {
        Uri uri=downloadManager.getUriForDownloadedFile(downloadID);
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MPFApp.getInstance().getActivityList().get(0).startActivity(intent);
    }
    private void installAPK(Uri uri) {
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MPFApp.getInstance().getActivityList().get(0).startActivity(intent);
    }
    public void downloadAndOpen() {
        File dir=new File(Environment.getExternalStorageDirectory()+File.separator+"MPFCache");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file=new File(dir,mFileName);
        if (file.exists()) {
            installAPK(Uri.fromFile(file));
        }
        if (isDownloading) {
            Toast.makeText(MPFApp.getInstance().getActivityList().get(0),"正在下载中，请稍候...",Toast.LENGTH_SHORT).show();
            return;
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long recievedID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (recievedID == currentDownloadId) {
                    isDownloading = false;
                    installAPK(recievedID);
                    release();
                }
            }
        };
        MPFApp.getInstance().registerReceiver(receiver, filter);

        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(mUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setMimeType("application/vnd.Android.package-archive");
        request.setTitle("Kugo Music");
        request.setDescription("Music Player");
        request.setDestinationInExternalPublicDir("MPFCache", mFileName);
        currentDownloadId=downloadManager.enqueue(request);
        isDownloading=true;
    }
    private void release() {
        downloadManager=null;
        MPFApp.getInstance().unregisterReceiver(receiver);
    }


}
