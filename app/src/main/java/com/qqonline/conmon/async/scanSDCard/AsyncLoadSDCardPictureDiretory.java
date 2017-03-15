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
 * ���ݸ�����·�������ȣ�����·���µ�ͼƬ�����ݿ�
 *
 * @author YE
 */
public class AsyncLoadSDCardPictureDiretory extends
        AsyncTask<String, String, Boolean> {

    private static final String TAG = "AsyncLoadSDCardPictureDiretory";

    /**
     * ��ͣ��־
     */
    private Boolean isStoped;
    /**
     * ���ݵ�����ȿ�
     */
    private ProgressDialog builder;
    /**
     * �˳����ݵ����ȷ�Ͽ�
     */
    private AlertDialog.Builder comfirmBuilder;
    private Context context;
    /**
     * ��־�߳��Ƿ��Ѵ����꣬��Ҫ����Comfirm�Ի����߳������ʱ�����ص�����ȶԻ��� ������ֱ����ʾ�������������
     */
    private boolean isDoInBackgroundComplete;
    /**
     * Ҫ������ļ���������Ҫ����ʱ������;
     */
    private int allFileCount;

    private int testDownCount;
    /**
     * ͼƬ·�����б����������ͼƬʵ���б�
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
        builder.setTitle("����ͼƬ�У����Ժ�");
        builder.setMessage("������");
        builder.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        builder.setProgress(0);
        builder.setMax(100);
        builder.setOnDismissListener(listener);
        builder.setButton(DialogInterface.BUTTON_NEGATIVE, "ȡ��", listener);
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
        Log.i(TAG, "���ڵ���SD��ͼƬ");
        String[] diretorys = params[0].split(",");   //Ҫ������ļ���·������
        try {
            allFileCount = Integer.parseInt(params[1]);         //Ҫ�����ͼƬ��������Ҫ������ʾ����
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         *  Ҫ����ĸ����ļ��е�ͼƬ������Ҫ���ڼ�¼�����ݿ⣬�����ж��Ƿ����µ�ͼƬ���뵽�ļ�����
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
        //��������ѡ���Ŀ¼�µ�����ͼƬ·��
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
        //��ͼƬ·���б�ת��ΪͼƬʵ���б����ڵ������ݿ�
        for (int i = 0; i < pictureList.size(); i++) {
            if (isStoped) {
                return false;
            }
            String picturePath = pictureList.get(i);
            String[] item = picturePath.split("/");
            String diretory = item[3];  //  mnt/estsd/???   �����2����������ͼƬ���ڵ�SD����Ŀ¼�ļ��� ��Ҫ����
            String pictureURL = item[item.length - 1];
            MpfPicture picture = new MpfPicture();
            picture.setAddTime(new Date());
            picture.setDbId(1);
            picture.setOpenId(getOpenId(picturePath));
            picture.setPicUrl(picturePath);
            pictureEntitiesList.add(picture);
        }
        /**
         * ��Ҫ������ļ��м�¼������б�,ͬʱ��ӵ��û��б�
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
     * ��ȡpath·�������һ���ļ���������OpenId
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
        Log.i(TAG, "����SD��ͼƬ���");

        isDoInBackgroundComplete = true;
        //�رս��ȿ򣬲�ȡ������ʧ�ص���������Ϊ��������˳��Ļ�����Ҫ����Comfirm�Ի���
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
                //��OpenId���û��Ѵ��ڣ���˵��֮ǰ���������ļ���
                //��ôӦ����ɾ��֮ǰ�����ͼƬ���û���¼�ȣ��ٵ����µ�
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

        int persent = (int) (((float) currentDeal) / ((float) allFileCount * 2) * 100);  //����2 ����ΪͼƬҪ��ɨ��һ�Σ��ټ������ݿ�һ�Σ�Ҫ���β���
        builder.setProgress(persent);
        super.onProgressUpdate(values);
    }

    private int getCount(String diretoryName) {
        return 0;
    }

    public void stop() {
        isStoped = true;
        Log.i(TAG, "ֹͣ�������ݿ�");
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
        Toast.makeText(context, "���������", Toast.LENGTH_SHORT).show();
    }

    private final class MyListener implements DialogInterface.OnClickListener,
            DialogInterface.OnDismissListener {
        /**
         * ����ͼƬ���ȿ�ȡ���ص�����
         */
        @Override
        public void onDismiss(DialogInterface dialog) {
            getConfirmDialog().show();
        }

        /**
         * ȷ���˳�����ͼƬ�Ի���İ�ť����ص�
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                //ȷ�϶Ի����ȷ����ť
                Toast.makeText(context, "ȷ�� ", Toast.LENGTH_SHORT).show();
                fileUtil.stop();
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                if (dialog instanceof ProgressDialog) {
                    //���ȡ������ͼƬ���ȿ򣬵���ȷ�϶Ի���
                    //getConfirmDialog().show();
                    Toast.makeText(context, "ȡ��  ProgressDialog", Toast.LENGTH_SHORT).show();
                }
//				else if(dialog instanceof AlertDialog.Builder) {	
//					
//					//builder.show();
//					Toast.makeText(context, "ȡ��  AlertDialog", Toast.LENGTH_SHORT).show();
//				}
                else {
                    //ȷ���Ի��� ȡ����ť
                    if (!isDoInBackgroundComplete) {
                        builder.show();
                    } else {
                        showComplete();
                    }
                    Toast.makeText(context, "ȡ�� ", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}
