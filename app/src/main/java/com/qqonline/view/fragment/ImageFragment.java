package com.qqonline.view.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qqonline.Manager.VideoManager;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.HttpUtil;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.getMd5;
import com.qqonline.conmon.handler.HandlerImageFragmentToken;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.mpf.PicPlayActivity2;
import com.qqonline.mpf.R;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by YE on 2015/8/14 0014.
 */
public class ImageFragment extends Fragment implements View.OnTouchListener {
    public static final String BUNDLE_KEY_URL = "BUNDLE_KEY_URL";
    public static final String BUNDLE_KEY_POSITION = "BUNDLE_KEY_POSITION";
    public static final String BUNDLE_KEY_MEDIAID = "BUNDLE_KEY_MEDIAID";
    private PicPlayActivity2 activity;
    private String mImageUrl;
    private int mPosition;
    private String mMediaId;
    protected static int mKeyPressCount;
    protected ImageView imageView;
    protected VideoView videoView;
    /**
     *
     */
    protected static Handler handler;

    public static ImageFragment newInstance(String url, String mediaId, int index) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_URL, url);
        bundle.putString(BUNDLE_KEY_MEDIAID, mediaId);
        bundle.putInt(BUNDLE_KEY_POSITION, index);
        imageFragment.setArguments(bundle);
        return imageFragment;
    }

    public static ImageFragment newInstance(Bundle bundle) {
        ImageFragment imageFragment = new ImageFragment();
        imageFragment.setArguments(bundle);
        return imageFragment;
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        try {
            this.activity = (PicPlayActivity2) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKeyPressCount = 0;
        Bundle bundle = getArguments();
        if (bundle != null) {
            mImageUrl = bundle.getString(BUNDLE_KEY_URL, "");
            mPosition = bundle.getInt(BUNDLE_KEY_POSITION, 0);
            mMediaId = bundle.getString(BUNDLE_KEY_MEDIAID, "");
        }
        if (isSDCardImage(mImageUrl)) {
            mImageUrl = "file://" + mImageUrl;
        }
        if (handler == null) {
            handler = new MyHandler();
        }
    }

    public int getPosition() {
        return this.mPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mImageUrl == null || mImageUrl.length() <= 0 || container == null) {

            return null;
        }
        if (mImageUrl.startsWith("video:")) {
            //load video
            videoView = (VideoView) inflater.inflate(R.layout.layout_video_view, container, false);
            /*videoView = new VideoView(activity);
            videoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));*/
            MediaController mediaCo = new MediaController(activity);
            mediaCo.setPrevNextListeners(activity.videoNextListener, activity.videoPreListener);
            videoView.setOnCompletionListener(activity);
            videoView.setOnTouchListener(this); //show delete dialog when double click on the video
            videoView.setMediaController(mediaCo);
            //	videoMain.setOnKeyListener(new );
            mediaCo.setMediaPlayer(videoView);
            VideoManager manager = new VideoManager(activity);
            manager.showVideo(videoView, mImageUrl);
            //remove the event that showing the next image delay
            PicPlayActivity2.handler.removeMessages(PicPlayActivity2.HANDLE_AUTO_SHOW_NEXT);
            return videoView;
        } else {
            //load image
            imageView = (ImageView) inflater.inflate(R.layout.layout_imageview, container, false);
            imageView.setOnTouchListener(this); //show delete dialog when double click on the image
            if (!mMediaId.equals("")) {
                //download or show image by mediaId
                /*File cacheDir = new File(new File(GetExtSDCardPath.getSDCardPath()), DATA.sdCardPicFile);
                File pic = new File(cacheDir, getMd5.Md5(mMediaId, 32));
                if (pic.exists()) {
                    *//**judge image is it exist here to avoid  the black screen when the bad network situation*//*
                    ImageLoader.getInstance().displayImage(mMediaId, imageView, MPFApp.getInstance().getImageOptions()
                            , new MyImageLoadingListener(imageView));
                } else {
                    *//**
                     * image file is not exist,so show(download) image by network
                     * get accesstoken first
                     *//*
                    HandlerImageFragmentToken tokenHandler = new HandlerImageFragmentToken(imageView,mMediaId);
                    tokenHandler.setListener(new MyImageLoadingListener(imageView));
                    HttpUtil.get(DATA.ACCESSTOKENURL,tokenHandler);
                }*/
                ImageLoader.getInstance().displayImage(mMediaId, imageView, MPFApp.getInstance().getImageOptions()
                        , new MyImageLoadingListener(imageView));
            } else {
                /**
                 * download or show image by imageUrl
                 * differ from media is the that the image from imageUrl has not EXIF information
                 */
                ImageLoader.getInstance().displayImage(mImageUrl, imageView, MPFApp.getInstance().getImageOptions()
                        , new MyImageLoadingListener(imageView));
            }
//        ImageLoader.getInstance().displayImage("file:///mnt/extsd/1/efcbb512215467fef9a4abd1ea39dd8a.jpg",imageView, MPFApp.getInstance().getImageOptions());
            return imageView;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

//        activity.showNext();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    private boolean isSDCardImage(String uri) {
        return uri.startsWith("/");
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mKeyPressCount++;
        handler.sendEmptyMessageDelayed(0, 350);
        if (mKeyPressCount >= 2) {
            mKeyPressCount = 0;
            showConfirm();
        }
        return false;
    }

    private void showConfirm() {
        DeleteDialogFragment fragment = DeleteDialogFragment.newInstance(null);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        fragment.show(ft, PicPlayActivity2.DELETE_DIALOG);

    }

    private static class MyHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == 0) {
                mKeyPressCount = 0;
            }
        }
    }

    private final class MyImageLoadingListener implements ImageLoadingListener {
        private ObjectAnimator animator;
        private WeakReference<ImageView> imageViewWeakReference;

        public MyImageLoadingListener(ImageView imageView) {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        public void onLoadingStarted(String s, View view) {

            if (animator == null) {
                animator = ObjectAnimator.ofFloat(view, "rotation", 0f, -360f);
                animator.setDuration(1000);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setInterpolator(new LinearInterpolator());
            }
            if (imageViewWeakReference.get() != null) {
                imageViewWeakReference.get().setScaleType(ImageView.ScaleType.CENTER);
            }
            animator.start();
        }

        @Override
        public void onLoadingFailed(String s, View view, FailReason failReason) {
            animator.end();
            if (imageViewWeakReference.get() != null) {
                imageViewWeakReference.get().setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }

        @Override
        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            animator.end();
            if (imageViewWeakReference.get() != null) {
                imageViewWeakReference.get().setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }

        @Override
        public void onLoadingCancelled(String s, View view) {
            animator.end();
            if (imageViewWeakReference.get() != null) {
                imageViewWeakReference.get().setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }
    }
}
