package com.qqonline.conmon.handler;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.view.fragment.ImageFragment;

import org.apache.http.Header;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/1/26 0026.
 */
public class HandlerImageFragmentToken extends TextHttpResponseHandler {
    private WeakReference<ImageView> viewWeakReference;
    private String mMediaId;
    private ImageLoadingListener listener;

    public HandlerImageFragmentToken(ImageView view,String mediaId) {
        viewWeakReference = new WeakReference<ImageView>(view);
        mMediaId = mediaId;
    }

    public void setListener(ImageLoadingListener listener) {
        this.listener = listener;
    }

    @Override
    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
        if (viewWeakReference.get() != null) {
//            fragmentWeakReference.get()
        }
//        imageFragment = ImageFragment.newInstance(bundle);
    }

    @Override
    public void onSuccess(int i, Header[] headers, String s) {
        String url = String.format(DATA.URL_WEICHAT_MEDIA_BY_ID,s,mMediaId);
        if (viewWeakReference.get() != null) {
            Log.i("HandlerImageFragmentToken","show image by network" + url);
            ImageLoader.getInstance().displayImage(mMediaId, viewWeakReference.get(), MPFApp.getInstance().getImageOptions()
                    , listener);
        }
    }
}
