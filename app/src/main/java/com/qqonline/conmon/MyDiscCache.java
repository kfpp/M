package com.qqonline.conmon;

import com.nostra13.universalimageloader.cache.disc.impl.BaseDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;

import java.io.File;

/**
 * Created by YE on 2015/8/27 0027.
 */
public class MyDiscCache extends BaseDiskCache {

    private FileNameGenerator fileNameGenerator;
    public MyDiscCache(File cacheDir) {
        super(cacheDir);
        this.fileNameGenerator= DefaultConfigurationFactory.createFileNameGenerator();
    }

    public MyDiscCache(File cacheDir, FileNameGenerator fileNameGenerator) {
        super(cacheDir,null, fileNameGenerator);
        this.fileNameGenerator=fileNameGenerator;
    }

    @Override
    public File get(String key) {
        if (key.equals(DATA.advertisementName)) {
            String fileName = fileNameGenerator.generate(key);
            return new File(getAdvertisementCache(), fileName);
        } else {
            return super.get(key);
        }

    }

    private File getAdvertisementCache() {
        File adCache=new File(new File(GetExtSDCardPath.getSDCardPath()),DATA.sdCardAdPicFile);
        if (!adCache.exists()) {
            adCache.mkdirs();
        }
        return adCache;
    }
    @Override
    public void clear() {
        super.clear();
    }

}
