package com.qqonline.Manager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.VideoView;

import com.qqonline.Manager.db.MpfMachineService;
import com.qqonline.Manager.db.MpfPictureService;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.ImageCompress;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.ThumbnailUtil;
import com.qqonline.conmon.UploadFileUtil;
import com.qqonline.conmon.async.AsyncDownload;
import com.qqonline.conmon.async.AsyncGet;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.mpf.R;

import java.io.File;

/**
 * Created by YE on 2015/8/28 0028.
 */
public class VideoManager {
    private VideoView videoMain;
    private Activity activity;
    public VideoManager(Activity activity) {
        this.activity=activity;
    }

    public void setVideoThumbnail(ImageView view, String uri) {
        String path = GetExtSDCardPath.getSDCardPath() + File.separator + DATA.SDCARDVIDEOPATH;
        String name = path + File.separator + uri.replace("video:", "") + DATA.VIDEOTYPE;
        Bitmap bitmap = getVideoThumbnail(name, 800, 600, MediaStore.Images.Thumbnails.MINI_KIND);
        if (bitmap == null) {
            view.setImageResource(R.drawable.ship);

        } else {
            Bitmap videoIcon = MPFApp.getInstance().getVideoIcon();
            Bitmap newBitmap = ImageCompress.CompositePictures(bitmap, videoIcon);
            view.setImageBitmap(newBitmap);
        }
    }
    public void setView(VideoView view) {
        this.videoMain=view;
    }
    public void showVideo(VideoView view,String url)
    {
        this.videoMain=view;
        final String mid=url.replace("video:", "");
        String MPFRootPath= GetExtSDCardPath.getSDCardPath();
        String diretoryName= DATA.SDCARDVIDEOPATH;
        final String path=MPFRootPath+ File.separator+diretoryName;
        File dir=new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File video=new File(path, mid+DATA.VIDEOTYPE);
        if (video.exists()) {
            videoMain.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    video.delete();
                    downloadVideo(path,mid,DATA.VIDEOTYPE);
                    return true;
                }
            });
            showLocalVideo(video.getAbsolutePath());
        }
        else {
            downloadVideo(path,mid,DATA.VIDEOTYPE);
        }
    }
    private  void downloadVideo(String path, final String mid, String type) { // mid+".mp4";
        final String videoMid=mid;
        final AsyncDownload download = new AsyncDownload(path, mid + type,
                new AsyncDownload.DoPostBack() {

                    @Override
                    public void doPostBack(String result) {
                        if(result.startsWith("error:"))
                        {
                            //下载多媒体文件失败,文件不存在或其它原因
                            MpfPictureService service=new MpfPictureService(activity, DATA.DATAVERSION);
                            service.delMpfPictureByPicUrl("video:"+mid);
//                            showNextImageOrVideo();
                        }
                        else
                        {
                            //下载完视频后，显示
                            showLocalVideo(result);
                            //下载完视频后，上传一个缩略图到服务器，以提供微信端管理界面加载显示
                            UploadVideoThumbnail(result,mid);
                        }
                    }
                });
        AsyncGet get = new AsyncGet(activity,
                new AsyncGet.MyDoPostBack() {
                    @Override
                    public void DoPostBack(String json) {
                        String accessToken = json;
                        String downUrl=DATA.VIDIODOWNLOADURL.replace("%s1", accessToken).replace("%s2", videoMid);
                        download.execute(downUrl);
                    }
                });
        get.execute(DATA.ACCESSTOKENURL);

    }
    private void showLocalVideo(String path)
    {
        videoMain.setVideoPath(path);
        videoMain.start();
    }
    /**
     * 上传视频的缩略图到服务器
     * @param videoPath
     */
    private void UploadVideoThumbnail(final String videoPath,final String videoName) {

        MpfPictureService service=new MpfPictureService(activity, DATA.DATAVERSION);
        final String openId=service.getPictureOpenIDByName("video:" + videoName);
        MpfMachineService machineService=new MpfMachineService(activity, DATA.DATAVERSION);
        final String machineId=String.valueOf(machineService.getMpfMachine().getDbId());
        service.release();
        machineService.release();
        service=null;
        machineService=null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap thumbs = ThumbnailUtil.GetVideoThumbnailByPath(videoPath,
                        DATA.VIDEO_THUMBNAILS_WIDTH_TO_SERVICE,
                        DATA.VIDEO_THUMBNAILS_HEIGHT_TO_SERVICE,
                        MediaStore.Images.Thumbnails.MINI_KIND);
                thumbs= ImageCompress.CompositePictures(thumbs, MPFApp.getInstance().getVideoIcon());
                UploadFileUtil.uploadImg(DATA.THUMBNAILS_UPLOAD_ADDRESS, thumbs, openId, machineId, videoName + DATA.PICTURETYPE);
            }
        }).start();

    }
    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        if (bitmap == null) {
            return null;
        }
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
