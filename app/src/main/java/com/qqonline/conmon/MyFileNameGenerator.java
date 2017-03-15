package com.qqonline.conmon;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;

/**
 * Created by YE on 2015/8/25 0025.
 */
public class MyFileNameGenerator implements FileNameGenerator {
    private static final String TAG="MyFileNameGenerator";
    @Override
    public String generate(String s) {
//        Log.w(TAG, s);
        if (s.startsWith("file://")) {
            return s;
        }
        if (s.startsWith("https://api.weixin.qq.com/cgi-bin/media/get?access_token")) {
            String searchText = "media_id=";
            int searchCount = searchText.length();
            int index = s.indexOf(searchText);
            String mediaId = s.substring(index+searchCount,s.length());
            return getMd5.Md5(mediaId,32);
        }
        String fileName=getMd5.Md5(s,32);

        return fileName;
    }
}
