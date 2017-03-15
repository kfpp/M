package com.qqonline.conmon;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

/**
 * Created by Administrator on 2016/1/22 0022.
 */
public final class MyBitmapDisplay implements BitmapDisplayer{
    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        imageAware.setImageBitmap(bitmap);
        

    }
}
