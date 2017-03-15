package com.qqonline.mpf;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qqonline.Manager.ActivityManager;
import com.qqonline.broadcast.PicReceiver;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.async.AsyncImageTask;
import com.qqonline.conmon.async.AsyncImageTask.OnFinishListener;
import com.qqonline.domain.MpfPicture;
import com.qqonline.domain.MpfUser;
import com.qqonline.Manager.db.MpfPictureService;
import com.qqonline.Manager.db.MpfUserService;
import com.qqonline.mpf.baseActivity.BaseActivity;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PicActivity extends BaseActivity {
    public enum ActivityState {
        showingUsers,
        showingPictures
    }

    /**
     *
     */
    public static ActivityState state = ActivityState.showingUsers;
    public String OpenId = "";
    private PicReceiver picReceiver = null;//????????
    private MpfPictureService mps;
    private MpfUserService mus;
    public List<MpfPicture> picList;
    public List<MpfUser> userList;
    public GridView picGridView;
    public ListAdapter adapter;
    private MPFApp application;
    /**
     * ???????????????
     */
    public int itemWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		setContentView(R.layout.pic_main);

        setContentView(R.layout.pic_main2);
        activity = this;
        Bundle bundle = this.getIntent().getExtras();
        OpenId = bundle.getString("OpenId");

        //picGridView = (GridView) findViewById(R.id.PicList);
        picGridView = (GridView) findViewById(R.id.gridViewMain);
        mps = new MpfPictureService(PicActivity.this, DATA.DATAVERSION);
        mus = new MpfUserService(PicActivity.this, DATA.DATAVERSION);


        if (!OpenId.equals("")) {
            loadPic(OpenId);
        } else {
            loadUser();
        }
        application = (MPFApp) getApplication();
        application.addActivity(this);
        //???????
        picReceiver = new PicReceiver(PicActivity.this);
        IntentFilter intentFilter = new IntentFilter(DATA.BroadcastPicActionName);
        intentFilter.setPriority(800);
        registerReceiver(picReceiver, intentFilter);
    }

    private void setView(int itemSize) {
        int size = itemSize;
        if (size % 2 != 0) {
            size++;
        }
        int length = 300;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        float density = dm.density;
        LinearLayout.LayoutParams params = null;
        int gridviewWidth = (int) (size * (length + 4) * density) / 2;//??????????????????????2
        if (gridviewWidth < (int) screenWidth * density) {
            params = new LinearLayout.LayoutParams(
                    (int) (screenWidth * density), LinearLayout.LayoutParams.MATCH_PARENT);
            int padding = ((int) screenWidth - gridviewWidth) / 2;
            //picGridView.setPadding(padding, 0, padding, 0);    //?????????????????????????
            picGridView.setPadding(10, 0, 10, 0);
        } else {
            params = new LinearLayout.LayoutParams(
                    gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
            picGridView.setPadding(10, 0, 10, 0);
        }
        int itemWidth = (int) (length * density);
        this.itemWidth = itemWidth;

        picGridView.setLayoutParams(params); // ????GirdView???????,????????
        picGridView.setColumnWidth(itemWidth); // ???????????
        picGridView.setHorizontalSpacing(5); // ???????????????
        picGridView.setStretchMode(GridView.NO_STRETCH);
        picGridView.setNumColumns(size / 2); // ??????????=????????
//        GridViewAdapter adapter=new GridViewAdapter(list);
//        gridView.setAdapter(adapter);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final int key = keyCode;
        switch (key) {
            case KeyEvent.KEYCODE_BACK:
                key_back_clicked();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void key_back_clicked() {
        if (state == ActivityState.showingUsers) {
            finish();
        }
        else if (state == ActivityState.showingPictures) {
            loadUser();
            OpenId = "";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.picmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.pic_play:
                OpenPalyPhone();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ????????????????
     */
    private void OpenPalyPhone() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("OpenId", OpenId);
        bundle.putString("type", "");
        intent.putExtras(bundle);
        intent.setClassName(this, "com.qqonline.mpf.PicPlayActivity");
        startActivityForResult(intent, 100);
    }

    /**
     * ?????????????
     */
    public void loadUser() {
        userList = mus.getUserList();
        setView(userList.size());
        dealEmptyUser(userList);
//		Display display = getWindowManager().getDefaultDisplay();
//		adapter = new UserAdapter(PicActivity.this, display);
        adapter = new MyUserAdatper(this);
        picGridView.setAdapter(adapter);
        state = ActivityState.showingUsers;
        setTitle(R.string.picactivity_title);
    }

    /**
     * ?????????????
     *
     * @param userList
     */
    private void dealEmptyUser(List<MpfUser> userList) {
        MpfPictureService pictureService=new MpfPictureService(this,DATA.DATAVERSION);
        List<MpfUser> emptyUserList = new ArrayList<MpfUser>();
        /*for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getFirstPicture() == null) {
                emptyUserList.add(userList.get(i));
            }
        }*/
        for (MpfUser user : userList) {
            if (pictureService.getPictureCount(user.getOpenId()) == 0) {
                emptyUserList.add(user);
            }
        }
        userList.removeAll(emptyUserList);
        /*for (int i = 0; i < emptyUserList.size(); i++) {
            userList.remove(emptyUserList.get(i));
        }*/
    }

    /**
     * ??????????
     *
     * @param openId
     */
    public void loadPic(String openId) {
        OpenId = openId;
        picList = mps.getPicture(openId);
        setView(picList.size());
//        Display display = getWindowManager().getDefaultDisplay();
//		adapter = new PicAdapter(PicActivity.this,display);
        adapter = new MyPictureAdapter(this);
        picGridView.setAdapter(adapter);
        state = ActivityState.showingPictures;
        setTitle(R.string.picactivity_title);
    }

    public void addPic(String picUrl) {
        CharSequence text = "???????";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        loadPic(OpenId);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(picReceiver);//????
        application.removeActivity(this);
        super.onDestroy();
    }

    private static final class MyPictureAdapter extends BaseAdapter {
        private WeakReference<PicActivity> activityWeakReference;

        public MyPictureAdapter(PicActivity activity) {
            activityWeakReference = new WeakReference<PicActivity>(activity);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PicActivity activity = activityWeakReference.get();
            if(activity == null) return null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                convertView = inflater.inflate(R.layout.item_grid_view, parent, false);
                LayoutParams params = convertView.getLayoutParams();
                params.height = (dm.heightPixels - 120) / 2;
                convertView.setLayoutParams(params);
                ImageView ivMain = (ImageView) convertView.findViewById(R.id.ivMain);
                TextView tvMain = (TextView) convertView.findViewById(R.id.tvMain);
                ImageView ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);
                ImageView ivDiretory = (ImageView) convertView.findViewById(R.id.ivDiretory);
                ivDiretory.setVisibility(View.INVISIBLE);
                AdapterHolder holder = new AdapterHolder(ivMain, tvMain, params.height);
                holder.setImageDelete(ivDelete);
                convertView.setTag(holder);
                ImageDeleteListener deleteListener = new ImageDeleteListener(activity,position);
                ivDelete.setOnClickListener(deleteListener);
                ivMain.setOnClickListener(new ImageListener(activity,position));
            }
            AdapterHolder hold = (AdapterHolder) convertView.getTag();
            final ImageView imgView = hold.getImageView();

            /*String phoneImage = activity.picList.get(position).getPicUrl();
            AsyncImageTask imageTask = new AsyncImageTask(itemWidth, hold.getHeight());
            imageTask.setOnFinishListener(new OnFinishListener() {
                @Override
                public void OnFinish(Bitmap bitmap) {
                    imgView.setImageBitmap(bitmap);
                }
            });
            imageTask.execute(phoneImage);*/
            MpfPicture picture = activity.picList.get(position);
            if (picture != null) {
                if (picture.getMediaId() != null && !picture.getMediaId().equals("")) {
                    /**???MediaId ???????????????EXIF???*/
                    ImageLoader.getInstance().displayImage(picture.getMediaId(), imgView, MPFApp.getInstance().getImageOptions());
                } else {
                    /**??????????????????????EXIF???*/
                    String url = picture.getPicUrl();
                    if (url.startsWith("/")) {
                        url = "file://"+url;
                    }
                    ImageLoader.getInstance().displayImage(url, imgView, MPFApp.getInstance().getImageOptions());
                }
            }
            return convertView;
        }

        private static final class ImageListener implements View.OnClickListener {
            private int position;
            private WeakReference<PicActivity> activityWeakReference;

            public ImageListener(PicActivity activity,int position) {
                this.position = position;
                activityWeakReference = new WeakReference<PicActivity>(activity);
            }

            @Override
            public void onClick(View v) {
                PicActivity activity = activityWeakReference.get();
                if (activity == null) return ;
                String openId = activity.picList.get(position).getOpenId();
                int dbId = activity.picList.get(position).getDbId();
//                playPicure(openId, dbId);
                ActivityManager.startPicPlayActivity(activity,dbId,null,openId);
            }

            /**
             * ????????????
             */
            /*private void playPicure(String OpenId, long DbId) {
                PicActivity activity = activityWeakReference.get();
                if (activity == null) return ;
                Intent intent = new Intent(activity, PicPlayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("OpenId", OpenId);
                bundle.putString("type", "");
                bundle.putLong("DbId", DbId);
                intent.putExtras(bundle);
                activity.startActivity(intent);
                activity.finish();
            }*/
        }

        private static final class ImageDeleteListener implements View.OnClickListener {
            private int position;
            private WeakReference<PicActivity> activityWeakReference;

            public ImageDeleteListener(PicActivity activity,int position) {
                this.position = position;
                activityWeakReference = new WeakReference<PicActivity>(activity);
            }

            @Override
            public void onClick(View v) {
                final PicActivity activity = activityWeakReference.get();
                if(activity == null) return;
                new AlertDialog.Builder(activity)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(R.string.sure_delete)
                        .setMessage(R.string.delete_pic_info)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MpfPictureService service = new MpfPictureService(activity, DATA.DATAVERSION);
                                service.delMpfPictureByPicUrl(activity.picList.get(position).getPicUrl());
                                activity.loadPic(activity.OpenId);
                            }
                        })
                        .setNegativeButton(R.string.Cancle, null)
                        .show();

            }
        }

        @Override
        public int getCount() {
            PicActivity activity = activityWeakReference.get();
            if(activity == null) return 0;
            return activity.picList.size();
        }

        @Override
        public Object getItem(int position) {
            PicActivity activity = activityWeakReference.get();
            if(activity == null) return null;
            return activity.picList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    protected static class MyUserAdatper extends BaseAdapter {
        private WeakReference<PicActivity> activity;

        public MyUserAdatper(PicActivity activity) {
            this.activity = new WeakReference<PicActivity>(activity);
        }

        @Override
        public int getCount() {
            PicActivity activity = this.activity.get();
            if (activity != null) {
                return activity.userList.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            PicActivity activity = this.activity.get();
            if (activity != null) {
                return activity.userList.get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PicActivity activity = this.activity.get();
            if (convertView == null) {
                if (activity == null) return null;
                LayoutInflater inflater = LayoutInflater.from(activity);
                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                convertView = inflater.inflate(R.layout.item_grid_view, parent, false);
                LayoutParams params = convertView.getLayoutParams();
                params.height = (dm.heightPixels - 120) / 2;
                convertView.setLayoutParams(params);
                ImageView ivMain = (ImageView) convertView.findViewById(R.id.ivMain);
                TextView tvMain = (TextView) convertView.findViewById(R.id.tvMain);
                ImageView ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);
                AdapterHolder holder = new AdapterHolder(ivMain, tvMain, params.height);
                holder.setImageDelete(ivDelete);
                convertView.setTag(holder);
                userImageClick cl = new userImageClick(activity,position);
                ivMain.setOnClickListener(cl);
                ImageDeleteListener deleteListener = new ImageDeleteListener(activity);
                deleteListener.setOpenId(position);
                ivDelete.setOnClickListener(deleteListener);
            }
            AdapterHolder hold = (AdapterHolder) convertView.getTag();
            final ImageView imgView = hold.getImageView();
            TextView txtView = hold.getTextView();
            MpfPicture firstPicture = activity.userList.get(position).getFirstPicture();
            /*String userImage = null;
            if (firstPicture != null) {
                userImage = firstPicture.getPicUrl();
            }*/
            MpfUser user = activity.userList.get(position);
            String userName = user.getName();
            MpfPictureService service = new MpfPictureService(activity, DATA.DATAVERSION);
            int count = service.getPictureCount(user.getOpenId());
            userName = userName + " [ " + count + " ]";
            if (firstPicture != null) {
                if (firstPicture.getMediaId() != null && !firstPicture.getMediaId().equals("")) {
                    /**???MediaId ???????????????EXIF???*/
                    ImageLoader.getInstance().displayImage(firstPicture.getMediaId(), imgView, MPFApp.getInstance().getImageOptions());
                } else if(firstPicture.getPicUrl() != null){
                    /**??????????????????????EXIF???*/
                    String url = firstPicture.getPicUrl();
                    if (url.startsWith("/")) {
                        url = "file://"+url;
                    }
                    ImageLoader.getInstance().displayImage(url, imgView, MPFApp.getInstance().getImageOptions());
                }
                /*AsyncImageTask task = new AsyncImageTask(activity.itemWidth, hold.getHeight());
                task.setOnFinishListener(new OnFinishListener() {
                    @Override
                    public void OnFinish(Bitmap bitmap) {
                        imgView.setImageBitmap(bitmap);
                    }
                });
                task.execute(userImage);*/
            }
            txtView.setText(userName);
            return convertView;
        }

        private final static class ImageDeleteListener implements View.OnClickListener {
            private String deleteUserOpenId;
            private WeakReference<PicActivity> activityWeakReference;

            public ImageDeleteListener(PicActivity activity) {
                activityWeakReference = new WeakReference<PicActivity>(activity);
            }

            public void setOpenId(int position) {
                if (activityWeakReference.get() != null) {
                    deleteUserOpenId = activityWeakReference.get().userList.get(position).getOpenId();
                }
            }

            @Override
            public void onClick(View v) {
                final PicActivity activity = activityWeakReference.get();
                if (activity == null) return;
                new AlertDialog.Builder(activity)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(R.string.sure_delete)
                        .setMessage(R.string.delete_user_alert_text)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MpfPictureService service = new MpfPictureService(activity, DATA.DATAVERSION);
                                service.delMpfPicture(deleteUserOpenId);
                                MpfUserService userService = new MpfUserService(activity, DATA.DATAVERSION);
                                userService.deleteUser(deleteUserOpenId);
                                service.release();
                                userService.release();
                                Toast.makeText(activity, activity.getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                                activity.loadUser();
                            }
                        })
                        .setNegativeButton(R.string.Cancle, null)
                        .show();
            }
        }

        public static final class userImageClick implements OnClickListener {
            public int position;
            private WeakReference<PicActivity> activityWeakReference;

            public userImageClick(PicActivity activity,int position) {
                this.position = position;
                activityWeakReference = new WeakReference<PicActivity>(activity);
            }


            @Override
            public void onClick(View v) {
                PicActivity activity = activityWeakReference.get();
                if (activity != null) {
                    activity.loadPic(activity.userList.get(position).getOpenId());
                }
            }

        }

    }

    public static class AdapterHolder {
        private ImageView imageView, ivDelete;
        private TextView textView;
        private int height;

        public AdapterHolder(ImageView imageView, TextView textView, int height) {
            this.imageView = imageView;
            this.textView = textView;
            this.height = height;
        }

        public void setImageDelete(ImageView ivDelete) {
            this.ivDelete = ivDelete;
        }

        public ImageView getImageDeleteView() {
            return ivDelete;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getTextView() {
            return textView;
        }

        public int getHeight() {
            return height;
        }

    }
}
