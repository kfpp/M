package com.qqonline.conmon;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * �Զ���������������Ҫ�ǵ������������ص�ַ��mediaIdʱ���ᴫ��getStreamFromOtherSource��������
 * ����ƴ�ӳ�΢���ز�ժȡ�ӿ�URL���ٴ���getStream��������ȡͼƬ��
 * ʹ��΢���ز���ȡ�ӿڵ���Ҫԭ���ǣ�ͼƬ������ʱ�Դ�ͼƬ�����Ǳ�������ģ�û��EXIFͷ��Ϣ���޷�������ʾͼƬ����
 */
public class MyImageDownloader extends BaseImageDownloader {
    private BufferedInputStream bufferedInputStream;
    /**
     * �������Ĳ�������Ҫ��MediaId
     */
    private String imageUri;
    private Object extra;
    /**
     * ��΢�������زĵ�URL
     */
    private String url;
    public MyImageDownloader(Context context) {
        super(context);
    }

    @Override
    protected InputStream getStreamFromOtherSource(final String imageUri, final Object extra) throws IOException {
        this.imageUri = imageUri;
        this.extra = extra;
//        try {
//            JSONObject jsonObject = new JSONObject(imageUri);
//            int code = (int) jsonObject.get("errcode");
//            if (code == 40001) {
//                //accessToken ��Ч
//                getImage(DATA.UPDATETOKENURL);
//            }
//        } catch (Exception e) {
//        }
//        ResponseHandler handler = new ResponseHandler(this,imageUri,extra);
////        HttpUtil.get(DATA.ACCESSTOKENURL,handler);
//        SyncHttpClient client = new SyncHttpClient();
//        client.get(DATA.ACCESSTOKENURL, handler);

        getImage(DATA.ACCESSTOKENURL);
        return bufferedInputStream;
    }

    public void getImage(String accesstokenUrl) {
        ResponseHandler handler = new ResponseHandler(this,imageUri,extra);
        SyncHttpClient client = new SyncHttpClient();
        client.get(accesstokenUrl, handler);

    }
    private final class ResponseHandler extends TextHttpResponseHandler {
        private MyImageDownloader downloader;
        private String mediaId;
        private Object extra;
        public ResponseHandler(MyImageDownloader downloader, String mediaId, Object extra) {
            this.downloader = downloader;
            this.extra = extra;
            this.mediaId = mediaId;
        }

        @Override
        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            try {
                InputStream inputStream= downloader.getStream(mediaId,extra);
                bufferedInputStream = new BufferedInputStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSuccess(int i, Header[] headers, String s) {
            url = String.format(DATA.URL_WEICHAT_MEDIA_BY_ID,s,mediaId);
            HttpUtil.getSync(url, new UpdateTokenHandler(downloader));
//            try {
//                downloader.bufferedInputStream = new BufferedInputStream(downloader.getStream(downloader.url, downloader.extra));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    private static class UpdateTokenHandler extends TextHttpResponseHandler {
        private MyImageDownloader downloader;
        public UpdateTokenHandler(MyImageDownloader downloader)
        {
            this.downloader = downloader;
        }

        @Override
        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            String name = headers[0].getName();
        }

        @Override
        public void onSuccess(int i, Header[] headers, String s) {
            int index = -1;
            for (int in = 0;in<headers.length;in++) {
                if (headers[in].getName().equalsIgnoreCase("Content-Type")) {
                    index = in;
                    break;
                }
            }
            if (headers[index].getValue().equalsIgnoreCase("text/plain")) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = (int) jsonObject.get("errcode");
                    if (code == 40001) {
                        //accessToken ��Ч
                        downloader.getImage(DATA.UPDATETOKENURL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    downloader.bufferedInputStream = new BufferedInputStream(downloader.getStream(downloader.url, downloader.extra));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /*@Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            int index = -1;
            for (int in = 0;in<headers.length;in++) {
                if (headers[in].getName().equalsIgnoreCase("Content-Type")) {
                    index = in;
                    break;
                }
            }
            if (headers[index].getValue().equalsIgnoreCase("text/plain")) {
                try {
                    String json = new String(bytes, "UTF-8");
                    JSONObject jsonObject = new JSONObject(json);
                    int code = (int) jsonObject.get("errcode");
                    if (code == 40001) {
                        //accessToken ��Ч
                        downloader.getImage(DATA.UPDATETOKENURL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    downloader.bufferedInputStream = new BufferedInputStream(downloader.getStream(downloader.url, downloader.extra));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            String name = headers[0].getName();
//            Toast.makeText(MPFApp.getInstance().getApplicationContext(), bytes.toString(), Toast.LENGTH_SHORT).show();
        }*/
    }
}
