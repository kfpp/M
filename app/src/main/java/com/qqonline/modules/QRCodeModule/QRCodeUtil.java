package com.qqonline.modules.QRCodeModule;

import org.json.JSONException;
import org.json.JSONObject;

import com.qqonline.conmon.DATA;
import com.qqonline.conmon.async.AsyncGet;
import com.qqonline.conmon.async.AsyncGetImage;
import com.qqonline.conmon.async.AsyncTaskPost;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class QRCodeUtil {

    private static final String ACCESSTOKENURL = DATA.ACCESSTOKENURL;
    private static final String TICKETURL = DATA.TICKETURL;
    private static final String QRCODEIMAGEURL = DATA.QRCODEIMAGEURL;
//    private AsyncGet get;
//    private AsyncTaskPost post;
//    private AsyncGetImage getImage;
    private ImageView iv;
    private TextView tv;
    private Bitmap bitmap;
    private IDoPostBack iLoaded;
    private Activity activity;
    private int scene_id;

    /**
     * @param context  context
     * @param scene_id 机器ID，用于扫描二维码后，直接绑定这台机器
     * @param iLoaded  回调
     */
    public QRCodeUtil(final Activity context, final int scene_id, final IDoPostBack iLoaded) {
        iv = null;
        tv = null;
        this.activity = context;
        this.scene_id = scene_id;
        this.iLoaded = iLoaded;
    }

    private void setText(String text) {
        if (tv != null) {
            tv.setText(text);
        }
    }

    private void setImage(Bitmap bitmap) {
        if (iv != null) {
            iv.setImageBitmap(bitmap);
        }
    }

    public void setImageView(ImageView iv) {
        this.iv = iv;
    }

    public void setTextView(TextView tv) {
        this.tv = tv;
    }

    public void asyncGetBitmap() {
        getAccessToken(ACCESSTOKENURL);
    }

    private void getAccessToken(String url) {
        AsyncGet get = new AsyncGet(activity, new AsyncGet.MyDoPostBack() {
            @Override
            public void DoPostBack(String json) {
                setText("access_token:" + json);
                String params = "{\"expire_seconds\": 604800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": " + scene_id + "}}}";
                getQRcodeUrl(params,json);
            }
        });
        get.execute(url);
    }

    private void getQRcodeUrl(String params,String accessToken) {
        AsyncTaskPost post = new AsyncTaskPost(new AsyncTaskPost.DoPostBack() {
            @Override
            public void PostBack(String result) {
                setText("ticket:" + result);
                JSONObject json = null;
                try {
                    json = new JSONObject(result);
                    String ticket = json.getString("ticket");
                    showImage(ticket);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.w("QRCodeUtil", result);
                    int code = 0;
                    try {
                        code = (int) json.get("errcode");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    if (code == 40001) {
                        getAccessToken(DATA.UPDATETOKENURL);
                        return ;
                    }
                    try {
                        iLoaded.OnError(code, "加载二维码出错，出错代码：" + json.get("errcode").toString());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        });
        post.setJsonParams(params);
        post.execute(TICKETURL + accessToken);
    }

    private void showImage(String ticket) {
        AsyncGetImage getImage = new AsyncGetImage(new AsyncGetImage.MyDoPostBack() {
            @Override
            public void DoPostBack(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                setImage(bitmap);
                if (QRCodeUtil.this.iLoaded != null) {
                    QRCodeUtil.this.iLoaded.BitmapLoaded(bitmap);
                }
            }
        });
        getImage.execute(QRCODEIMAGEURL + ticket);
    }
    public interface IDoPostBack {
        void BitmapLoaded(Bitmap bitmap);

        void OnError(int errCode, String error);
    }
}
