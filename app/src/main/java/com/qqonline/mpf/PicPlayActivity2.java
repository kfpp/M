package com.qqonline.mpf;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qqonline.Manager.db.MpfPictureService;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.adapter.GalleryAdapter;
import com.qqonline.domain.MpfPicture;
import com.qqonline.modules.QRCodeModule.QRCodeAPI;
import com.qqonline.mpf.baseActivity.BaseActivity;
import com.qqonline.view.fragment.ImageFragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;


public class PicPlayActivity2 extends BaseActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, DialogInterface.OnClickListener,
        GestureDetector.OnGestureListener{

    public static final int HANDLER_DISPLAY_BOTTOM_LAYOUT = 0;
    /**
     * ��ʾ�����ѡ��Gallery�е�ͼƬ
     */
    public static final int HANDLER_SHOW_SELECTED_IMAGE = 1;
    /**
     * ��ʾͼƬ����HANDLE_AUTO_SHOW_NEXT��ͬ���������־�����õ�ǰͼƬ�±�
     */
    public static final int HANDLER_SHOW_IMAGE = 2;
    /**
     * ��ʾ��һ��ͼƬ
     */
    public static final int HANDLE_AUTO_SHOW_NEXT = 3;
    public static final int DELETE_PICTURE_WITH_SOURCE = AlertDialog.BUTTON_POSITIVE + 1000;
    public static final int DELETE_PICTURE_WITHOUT_SOURCE = AlertDialog.BUTTON_POSITIVE + 1000 +1 ;
    /**
     * �ֲ�����
     */
    public static final long SHOW_NEXT_FRAG_TIME = 10000;
    /**
     * ����ú��Զ����صײ�Gallery
     */
    private static final long HIDE_BOTTOM_TIME = 5000;
    public static final String DELETE_DIALOG = "DELETE_DIALOG";
    /**
     * handler ��ʾ��ά��
     */
    private static final int QRCODE = 4;
    public static final String[] INTENT_PARAMS = {
            "OpenId",
            "DbId",
            "acti"
    };
    /**
     * �ײ�����
     */
    public Gallery gallery;
    private GalleryAdapter adapter;
    public LinearLayout lyBottom;
    public static Handler handler;
    /**
     * ��ǰͼƬ�±�
     */
    public int currentImagePosition;
    private MpfPictureService mps;
    /**
     * ͼƬ�б�
     */
    public List<MpfPicture> picList;

    /**
     * ???OpenId
     */
    public String checkOpenId;
    /**
     * ��ʼͼƬ��DBID
     */
    public long startPictureDbId = 0;
    /**
     * �Ƿ񼤻���ת
     */
    private boolean isActi;
    /**
     * �Ƿ��ֲ�
     */
    private boolean isAutoShowNext;
    /**
     *
     */
    public VideoNextListener videoNextListener;
    /**
     *
     */
    public VideoPreListener videoPreListener;

    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TAG = "PicPlayActivity2";
        noNavigationBar = true;
        noTitleBar = true;
        noStateBar = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_play_activity2);

        initBundleParams();
        initData();
        initCtrols();
        initJumpEvent();

        checkUpdate(this);

    }


    private void initJumpEvent() {
        if (isActi) {
            handler.sendEmptyMessageDelayed(QRCODE, 3000);
        }
    }

    private void initBundleParams() {
        try {
            Bundle bundle = this.getIntent().getExtras();
            checkOpenId = bundle.getString(INTENT_PARAMS[0],"");
            startPictureDbId = bundle.getLong(INTENT_PARAMS[1],0);
            isActi = bundle.getBoolean(INTENT_PARAMS[2], false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCtrols() {
        gallery = (Gallery) findViewById(R.id.gallery);
        adapter = new GalleryAdapter(picList);
        gallery.setAdapter(adapter);
        gallery.setOnItemSelectedListener(this);
        lyBottom = (LinearLayout) findViewById(R.id.lyBottom);
    }

    private void initData() {

        isAutoShowNext = true;
        app = MPFApp.getInstance();
        activity = this;
        handler = new PicHandler(this);
        mps = new MpfPictureService(PicPlayActivity2.this, DATA.DATAVERSION);
        picList = mps.getPicture(checkOpenId);
        handler.sendEmptyMessageDelayed(HANDLER_DISPLAY_BOTTOM_LAYOUT, HIDE_BOTTOM_TIME);
        videoNextListener = new VideoNextListener();
        videoPreListener = new VideoPreListener();
        gestureDetector = new GestureDetector(this,this);
    }

    public void refreshPictureList() {
        handler.removeMessages(HANDLER_SHOW_IMAGE);
        picList = mps.getPicture(checkOpenId);
        showImage(0);
        adapter = new GalleryAdapter(picList);
        gallery.setAdapter(adapter);
        gallery.setSelection(0);
    }

    public void showImage(int index) {
        if (this == null) {
            Log.d(TAG, "PicPlayActivity2 has been destroy when showImage");
            return;
        }
        if (index >= picList.size() || index < 0) {
            index = 0;
        }
        ImageFragment imageFragment = null;
        if (true) {//imageFragment == null || imageFragment.getPosition() != index
            MpfPicture picture = picList.get(index);
            String url = picture.getPicUrl();
            String mediaId = picture.getMediaId();
//        if (url.startsWith("http:") || url.equals(DATA.advertisementName)) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (mediaId == null) mediaId = "";
            Bundle bundle = new Bundle();
            bundle.putString(ImageFragment.BUNDLE_KEY_MEDIAID, mediaId);
            bundle.putString(ImageFragment.BUNDLE_KEY_URL, url);
            bundle.putInt(ImageFragment.BUNDLE_KEY_POSITION, index);
            imageFragment = ImageFragment.newInstance(bundle);

            if (!url.startsWith("video:")) {
//                ft.setCustomAnimations(R.animator.rotate_new_in, R.animator.rotate_new_out);
                setAnimator(ft);
            }
            ft.replace(R.id.imageFragment, imageFragment);
            try {
                ft.commit();
                currentImagePosition = index;
            } catch (Exception e) {
                e.printStackTrace();
            }
//        }
        }
        autoShowNextHandler(isAutoShowNext);
    }

    /**
     * ����7����Ч֮һ����
     * @param fragmentTransaction
     */
    private void setAnimator(FragmentTransaction fragmentTransaction) {
        final int rand=new Random().nextInt(7);
        switch (rand) {
            case 0:
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                break;
            case 1:
                fragmentTransaction.setCustomAnimations(R.animator.translate_in,R.animator.translate_out);
                break;
            case 2:
                fragmentTransaction.setCustomAnimations(R.animator.scale_in,R.animator.scale_out);
                break;
            case 3:
                fragmentTransaction.setCustomAnimations(R.animator.rotate_new_in, R.animator.rotate_new_out);
                break;
            case 4:
                fragmentTransaction.setCustomAnimations(R.animator.translate_y_down_in, R.animator.translate_y_down_out);
                break;
            case 5:
                fragmentTransaction.setCustomAnimations(R.animator.translate_y_up_in, R.animator.translate_y_up_out);
                break;
            case 6:
                fragmentTransaction.setCustomAnimations(R.animator.translate_x_left_in, R.animator.translate_x_left_out);
                break;
            default:break;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        autoShowNextHandler(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(QRCODE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pic_play_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        handler.removeMessages(HANDLER_SHOW_IMAGE);
        currentImagePosition = i;

        Message message = new Message();
        message.what = HANDLER_SHOW_SELECTED_IMAGE;
        message.arg1 = i;
        handler.sendMessageDelayed(message, 300);

        handler.removeMessages(HANDLER_DISPLAY_BOTTOM_LAYOUT);
        handler.sendEmptyMessageDelayed(HANDLER_DISPLAY_BOTTOM_LAYOUT, HIDE_BOTTOM_TIME);
    }

    //    public void showNext() {
//        Message message=new Message();
//        message.what=HANDLER_SHOW_IMAGE;
//        message.arg1=++currentImagePosition;
//        if (currentImagePosition >= picList.size()) {
//            currentImagePosition=0;
//        }
//        handler.sendMessageDelayed(message, PicPlayActivity2.SHOW_NEXT_FRAG_TIME);
//    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
    private void showBottomGallery() {
        if (lyBottom.getVisibility() == View.GONE || lyBottom.getVisibility() == View.INVISIBLE) {
            lyBottom.setVisibility(View.VISIBLE);
        }

    }
    private void delayHideBottomGallery()
    {
        handler.removeMessages(HANDLER_DISPLAY_BOTTOM_LAYOUT);
        handler.sendEmptyMessageDelayed(HANDLER_DISPLAY_BOTTOM_LAYOUT, HIDE_BOTTOM_TIME);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        showBottomGallery();
        delayHideBottomGallery();
        switch (keyCode) {
            case KeyEvent.KEYCODE_C:
            case KeyEvent.KEYCODE_F9:
                //?????????
                if (app.getVersionType() == DATA.VERSIONTYPE.SmartHome) {
                    finish();
                    return true;
                }
            case KeyEvent.KEYCODE_BACK:
                if (app.getVersionType() == DATA.VERSIONTYPE.SmartHome) {

                    app.returnHome();
                } else {
                    finish();

                }
                return true;
            case KeyEvent.KEYCODE_S:
            case KeyEvent.KEYCODE_F10:
                //?????????
                isAutoShowNext = !isAutoShowNext;
                int textID = 0;
                if (isAutoShowNext) {
                    textID = R.string.start_play;
                } else {
                    textID = R.string.pause_play;
                }
                Toast.makeText(this, textID, Toast.LENGTH_SHORT).show();
                autoShowNextHandler(isAutoShowNext);
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(HANDLER_SHOW_IMAGE);
    }

    private void autoShowNextHandler(Boolean flag) {
        handler.removeMessages(HANDLE_AUTO_SHOW_NEXT);

        if (flag) {
            handler.sendEmptyMessageDelayed(HANDLE_AUTO_SHOW_NEXT, SHOW_NEXT_FRAG_TIME);
        }
    }

    public int getNextPosition() {
        currentImagePosition++;
        if (currentImagePosition >= picList.size()) {
            currentImagePosition = 0;
        }
        return currentImagePosition;
    }

    private int getPrePosition() {
        currentImagePosition--;
        if (currentImagePosition < 0) {
            currentImagePosition = picList.size() - 1;
        }
        return currentImagePosition;
    }

    private void showSelectedBottomImage(int position) {
        gallery.setSelection(position);
    }
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        showImage(getNextPosition());
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        showImage(getNextPosition());
        return false;
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.btnDelete:
                if(view.getTag()!= null && (boolean)view.getTag())delete(true);
                else delete(false);
                break;
            default:
                break;
        }
    }

    private void delete(boolean deleteSource) {
//        if (deleteSource) {
//            MpfPicture picture = picList.get(currentImagePosition);
//            String imageUri = null;
//            if (picture.getMediaId() != null) {
//                imageUri = picture.getMediaId();
//            } else {
//                imageUri = picture.getPicUrl();
//            }
//            File image = ImageLoader.getInstance().getDiskCache().get(imageUri);
//            if (image.exists()) {
//                Toast.makeText(PicPlayActivity2.this, image.getName(), Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(PicPlayActivity2.this, "false", Toast.LENGTH_SHORT).show();
//        }
        if (currentImagePosition == picList.size() - 1) {
            Toast.makeText(this, R.string.picture_can_not_delete, Toast.LENGTH_SHORT).show();
            return;
        }
        int id = picList.get(currentImagePosition).getDbId();
        MpfPictureService service = new MpfPictureService(this, DATA.DATAVERSION);
        service.delMpfPicture(id);

        if (deleteSource) {
            /**
             * 删除源文件
             */
            MpfPicture picture = picList.get(currentImagePosition);
            String imageUri = picture.getMediaId() != null ? picture.getMediaId() : picture.getPicUrl();
            ImageLoader.getInstance().getDiskCache().remove(imageUri);
            Log.i(TAG,"remove source file:"+imageUri);
        }
        picList.remove(currentImagePosition);
        adapter.notifyDataSetChanged();
        showImage(currentImagePosition);

    }

    //deleteDialogFragment ???????,OnCreateDialog???
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case PicPlayActivity2.DELETE_PICTURE_WITH_SOURCE:
                delete(true);
                break;
            case PicPlayActivity2.DELETE_PICTURE_WITHOUT_SOURCE:
                delete(false);break;
            default:break;
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        showBottomGallery();
        delayHideBottomGallery();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        float minMove = 120;         //��С��������
        float minVelocity = 0;      //��С�����ٶ�
        float beginX = e1.getX();
        float endX = e2.getX();
        float beginY = e1.getY();
        float endY = e2.getY();

        if(beginX-endX>minMove&&Math.abs(velocityX)>minVelocity){   //��
            int prePosition = getPrePosition();
//            showImage(prePosition);
            currentImagePosition = prePosition;
            showSelectedBottomImage(prePosition);
        }else if(endX-beginX>minMove&&Math.abs(velocityX)>minVelocity){   //�һ�
            int nextPosition = getNextPosition();
//            showImage(nextPosition);
            currentImagePosition = nextPosition;
            showSelectedBottomImage(nextPosition);
        }else if(beginY-endY>minMove&&Math.abs(velocityY)>minVelocity){   //�ϻ�
//            Toast.makeText(this,velocityX+"�ϻ�",Toast.LENGTH_SHORT).show();
        }else if(endY-beginY>minMove&&Math.abs(velocityY)>minVelocity){   //�»�
//            Toast.makeText(this,velocityX+"�»�",Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private final class VideoPreListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showImage(getPrePosition());
        }
    }

    private final class VideoNextListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showImage(getNextPosition());
        }
    }


    private static class PicHandler extends Handler {
        private WeakReference<PicPlayActivity2> activityWeakReference;

        public PicHandler(PicPlayActivity2 activity) {
            activityWeakReference = new WeakReference<PicPlayActivity2>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (activityWeakReference.get() == null) {
                return;
            }
            if (msg.what == HANDLER_DISPLAY_BOTTOM_LAYOUT) {
                activityWeakReference.get().lyBottom.setVisibility(View.GONE);
                activityWeakReference.get().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            } else if (msg.what == HANDLER_SHOW_SELECTED_IMAGE) {
                int position = msg.arg1;
                if (activityWeakReference.get().currentImagePosition == position) {
                    activityWeakReference.get().showImage(position);
                    activityWeakReference.get().gallery.setSelection(position);
                }
            } else if (msg.what == QRCODE) {

                if (activityWeakReference.get() != null) {

                    QRCodeAPI.show(activityWeakReference.get());
                }
            } else if (msg.what == HANDLER_SHOW_IMAGE) {
                final int position = msg.arg1;
                activityWeakReference.get().showImage(position);
                activityWeakReference.get().gallery.setSelection(position);
            } else if (msg.what == HANDLE_AUTO_SHOW_NEXT) {
                activityWeakReference.get().showImage(activityWeakReference.get().getNextPosition());
                activityWeakReference.get().gallery.setSelection(activityWeakReference.get().currentImagePosition);
            }
            super.handleMessage(msg);
        }
    }
}
