package com.qqonline.conmon.async.scanSDCard;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.qqonline.conmon.DATA;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.conmon.sdcardutils.SDCardFileUtil;
import com.qqonline.conmon.sdcardutils.SDCardFileUtil.PictureLoaded;
import com.qqonline.domain.ImportRecord;
import com.qqonline.domain.MpfPicture;
import com.qqonline.domain.MpfUser;
import com.qqonline.mpf.R;
import com.qqonline.Manager.db.ImportRecordService;
import com.qqonline.Manager.db.MpfPictureService;
import com.qqonline.Manager.db.MpfUserService;

import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * 根据给出的路径参数等，导入路径下的图片到数据库
 *
 * @author YE
 */
public class AsyncLoadSDCardPictureDiretory extends
        AsyncTask<String, String, Boolean> {

    private static final String TAG = "AsyncLoadSDCardPictureDiretory";

    /**
     * 暂停标志
     */
    private Boolean isStoped;
    /**
     * 数据导入进度框
     */
    private ProgressDialog builder;
    /**
     * 退出数据导入的确认框
     */
    private AlertDialog.Builder comfirmBuilder;
    private Context context;
    /**
     * 标志线程是否已处理完，主要用于Comfirm对话框当线程已完成时不返回导入进度对话框 ，而是直接显示导入已完成字样
     */
    private boolean isDoInBackgroundComplete;
    /**
     * 要导入的文件总数，主要用于时进度条;
     */
    private int allFileCount;

    private int testDownCount;
    /**
     * 图片路径的列表，用来打包成图片实体列表
     */
    private ArrayList<String> pictureList;
    private ArrayList<MpfPicture> pictureEntitiesList;
    private ArrayList<ImportRecord> importRecordList;
    private ArrayList<MpfUser> userList;
    private SDCardFileUtil fileUtil;
    private int currentDeal;
    private MpfUserService userService;
    private MpfPictureService picService;
    private ImportRecordService importService ;
    public AsyncLoadSDCardPictureDiretory(Context context) {
        isStoped = false;
        this.context = context;

        userService = new MpfUserService(context, DATA.DATAVERSION);
        picService = new MpfPictureService(context, DATA.DATAVERSION);
        importService = new ImportRecordService(context, DATA.DATAVERSION);

        MyListener listener = new MyListener();
        builder = new ProgressDialog(context);
        builder.setIcon(android.R.drawable.ic_menu_upload);
        builder.setTitle("导入图片中，请稍候");
        builder.setMessage("导入中");
        builder.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        builder.setProgress(0);
        builder.setMax(100);
        builder.setOnDismissListener(listener);
        builder.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", listener);
        builder.setCancelable(true);

        isDoInBackgroundComplete = false;

        testDownCount = 10;

        fileUtil = new SDCardFileUtil();

        pictureEntitiesList = new ArrayList<MpfPicture>();

        pictureList = new ArrayList<String>();
        userList = new ArrayList<MpfUser>();
        importRecordList = new ArrayList<ImportRecord>();
        currentDeal = 0;
    }

    @Override
    protected void onPreExecute() {
        builder.show();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Log.i(TAG, "正在导入SD卡图片");
        String[] diretorys = params[0].split(",");   //要导入的文件夹路径数组
        try {
            allFileCount = Integer.parseInt(params[1]);         //要导入的图片总数，主要用于显示进度
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         *  要导入的各个文件夹的图片数，主要用于记录在数据库，方便判断是否有新的图片放入到文件夹中
         */
        String[] diretorysFileCountString = params[2].split(",");
        int[] diretorysFileCount = new int[diretorysFileCountString.length];
        for (int i = 0; i < diretorysFileCountString.length; i++) {
            try {
                diretorysFileCount[i] = Integer.parseInt(diretorysFileCountString[i]);
            } catch (Exception e) {
                e.printStackTrace();
                diretorysFileCount[i] = 0;
            }
        }
        //搜索出所选择的目录下的所有图片路径
        for (int i = 0; i < diretorys.length; i++) {
            if (isStoped) {
                return false;
            }
            pictureList.addAll(fileUtil.getPictureListFromDiretorys(diretorys[i], AsyncScanSDCardPicture.PICTURETYPES,
                    new PictureLoaded() {
                        @Override
                        public void onOnePictureLoaded(String path) {
                            currentDeal++;
                            publishProgress(path);

                        }
                    }));
        }
        //将图片路径列表转换为图片实体列表，用于导入数据库
        for (int i = 0; i < pictureList.size(); i++) {
            if (isStoped) {
                return false;
            }
            String picturePath = pictureList.get(i);
            String[] item = picturePath.split("/");
            String diretory = item[3];  //  mnt/estsd/???   这个第2项正好是这图片所在的SD卡根目录文件夹 主要用于
            String pictureURL = item[item.length - 1];
            MpfPicture picture = new MpfPicture();
            picture.setAddTime(new Date());
            picture.setDbId(1);
            picture.setOpenId(getOpenId(picturePath));
            picture.setPicUrl(picturePath);
            pictureEntitiesList.add(picture);
        }
        /**
         * 将要导入的文件夹记录保存进列表,同时添加到用户列表
         */
        for (int i = 0; i < diretorys.length; i++) {
            ImportRecord record = new ImportRecord();
            record.setPath(diretorys[i]);
            int count = 0;
            if (i > diretorysFileCount.length - 1) {
                count = 0;
            } else {
                count = diretorysFileCount[i];
            }
            record.setCount(count);
            importRecordList.add(record);

//			int index=diretorys[i].lastIndexOf("/");
//			String name=null;
//			if (index != -1) {
//				name=diretorys[i].substring(index+1, diretorys[i].length());
//			}
//			else {
//				name=diretorys[i];
//			}
            String openId = getOpenId(diretorys[i]);
            MpfUser user = new MpfUser();
            user.setAddTime(new Date());
            user.setDbId(1);
            user.setName(openId);
            user.setOpenId(openId);
            userList.add(user);
        }

        return true;
    }

    /**
     * 截取path路径中最后一个文件夹名用作OpenId
     *
     * @param path
     * @return
     */
    private String getOpenId(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            if (fileUtil.isSDCardRoot(file.getAbsolutePath())) {
                return context.getResources().getString(R.string.sdcard_root_path);
            } else {
                String[] item = path.split("/");
                return item[3];
            }

        } else {
            File parent = file.getParentFile();
            if (fileUtil.isSDCardRoot(parent.getAbsolutePath())) {
                return context.getResources().getString(R.string.sdcard_root_path);
            } else {
                String[] item = path.split("/");
                return item[3];
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.i(TAG, "导入SD卡图片完成");

        isDoInBackgroundComplete = true;
        //关闭进度框，并取消其消失回调函数，因为正常完成退出的话不需要弹出Comfirm对话框
        builder.setOnDismissListener(null);
        builder.dismiss();

        if (isStoped || !result) {
            return;
        }
//        picService.AddMpfPicture(pictureEntitiesList, isStoped);
//        ImportRecordService importService = new ImportRecordService(context, DATA.DATAVERSION);
//        importService.addRecord(importRecordList);
//
//        userService.AddMpfUser(userList);
        addDataBaseRecord(userList,pictureEntitiesList,importRecordList);
        if (!isStoped) {
            showComplete();
        }
        super.onPostExecute(result);
    }

    private void addDataBaseRecord(ArrayList<MpfUser> users, ArrayList<MpfPicture> pictures, ArrayList<ImportRecord> records) {
        ArrayList<MpfUser> removeList = new ArrayList<>();
        ArrayList<ImportRecord> removeImportRecordList = new ArrayList<>();
        for (MpfUser user : users) {
            if (userService.UserIsHaved(user.getOpenId())) {
                //该OpenId的用户已存在，则说明之前导入过这个文件夹
                //那么应该先删除之前导入的图片的用户记录等，再导入新的
                removeList.add(user);
                picService.delMpfPicture(user.getOpenId());
            }
        }
        for (ImportRecord record : records) {
            if (importService.hasRecord(record.getPath())) {
                removeImportRecordList.add(record);
            }
        }

        if (removeList.size() > 0) {
            users.removeAll(removeList);
        }
        if (removeImportRecordList.size() > 0) {
            records.removeAll(removeImportRecordList);
        }

        userService.AddMpfUser(users);
        picService.AddMpfPicture(pictures,isStoped);
        importService.addRecord(records);
    }
    @Override
    protected void onProgressUpdate(String... values) {
        builder.setMessage(values[0]);

        int persent = (int) (((float) currentDeal) / ((float) allFileCount * 2) * 100);  //乘以2 是因为图片要先扫描一次，再加入数据库一次，要两次操作
        builder.setProgress(persent);
        super.onProgressUpdate(values);
    }

    private int getCount(String diretoryName) {
        return 0;
    }

    public void stop() {
        isStoped = true;
        Log.i(TAG, "停止导入数据库");
    }

    private AlertDialog.Builder getConfirmDialog() {
        if (comfirmBuilder == null) {
            MyListener listener = new MyListener();
            comfirmBuilder = new AlertDialog.Builder(context);
            comfirmBuilder.setIcon(android.R.drawable.ic_menu_info_details)
                    .setTitle(R.string.sure_exit)
                    .setMessage(R.string.sure_exit)
                    .setPositiveButton(R.string.OK, listener)
                    .setNegativeButton(R.string.Cancle, listener);
        }
        return comfirmBuilder;
    }

    private void showComplete() {
        Toast.makeText(context, "导入已完成", Toast.LENGTH_SHORT).show();
    }

    private final class MyListener implements DialogInterface.OnClickListener,
            DialogInterface.OnDismissListener {
        /**
         * 导入图片进度框取消回调函数
         */
        @Override
        public void onDismiss(DialogInterface dialog) {
            getConfirmDialog().show();
        }

        /**
         * 确认退出导入图片对话框的按钮点击回调
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                //确认对话框的确定按钮
                Toast.makeText(context, "确定 ", Toast.LENGTH_SHORT).show();
                fileUtil.stop();
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                if (dialog instanceof ProgressDialog) {
                    //点击取消导入图片进度框，弹出确认对话框
                    //getConfirmDialog().show();
                    Toast.makeText(context, "取消  ProgressDialog", Toast.LENGTH_SHORT).show();
                }
//				else if(dialog instanceof AlertDialog.Builder) {	
//					
//					//builder.show();
//					Toast.makeText(context, "取消  AlertDialog", Toast.LENGTH_SHORT).show();
//				}
                else {
                    //确定对话框 取消按钮
                    if (!isDoInBackgroundComplete) {
                        builder.show();
                    } else {
                        showComplete();
                    }
                    Toast.makeText(context, "取消 ", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}
