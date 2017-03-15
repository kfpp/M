package com.qqonline.conmon;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/2/26.
 */
public class MyLruDiskCache extends LruDiskCache {
    public MyLruDiskCache(File cacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize) throws IOException {
        super(cacheDir, fileNameGenerator, cacheMaxSize);
    }

    public MyLruDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize, int cacheMaxFileCount) throws IOException {
        super(cacheDir, reserveCacheDir, fileNameGenerator, cacheMaxSize, cacheMaxFileCount);
    }

    protected String getKey(String imageUri) {

        return getMd5.Md5(imageUri, 32);
    }
    @Override
    public File get(String imageUri) {
        return super.get(getKey(imageUri));
    }

    @Override
    public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException {

        return super.save(getKey(imageUri), imageStream, listener);
    }

    @Override
    public boolean save(String imageUri, Bitmap bitmap) throws IOException {
        return super.save(getKey(imageUri), bitmap);
    }

    @Override
    public boolean remove(String imageUri) {
        return super.remove(getKey(imageUri));
    }
}
