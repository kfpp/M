package com.qqonline.conmon.adapter;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qqonline.Manager.VideoManager;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.ImageCompress;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.domain.MpfPicture;
import com.qqonline.mpf.R;

import java.io.File;
import java.util.List;

/**
 * Created by YE on 2015/8/13 0013.
 */
public class GalleryAdapter extends BaseAdapter {
    private List<MpfPicture> mImageUris;

    public GalleryAdapter(List<MpfPicture> imageUris) {

        mImageUris = imageUris;
    }

    @Override
    public int getCount() {
        return mImageUris.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageUris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            ImageView view = new ImageView(parent.getContext());
            Gallery.LayoutParams layoutParams = new Gallery.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.width = 150;
            view.setLayoutParams(layoutParams);

            convertView = view;
        }
        ImageView v = (ImageView) convertView;
        String uri = mImageUris.get(position).getPicUrl();
        String mediaId = mImageUris.get(position).getMediaId();
        if (isSDCardImage(uri)) {
            uri = "file://" + uri;
        }
        if (isVideo(uri)) {
            VideoManager manager=new VideoManager(null);
            manager.setVideoThumbnail(v,uri);
        } else {
            if (mediaId != null && !mediaId.equals("")) {
                ImageLoader.getInstance().displayImage(mediaId, v, MPFApp.getInstance().getIconOptions());
            } else {
                ImageLoader.getInstance().displayImage(uri, v, MPFApp.getInstance().getIconOptions());
            }
        }
        return v;
    }

    private boolean isVideo(String uri) {
        if (uri.startsWith("video:")) {
            return true;
        } else {
            return false;
        }
    }

    public void setPicList(List<MpfPicture> mImageUris) {
        this.mImageUris = mImageUris;
    }

    private boolean isSDCardImage(String uri) {
        if (uri.startsWith("/")) {
            return true;
        } else {
            return false;
        }
    }

}
