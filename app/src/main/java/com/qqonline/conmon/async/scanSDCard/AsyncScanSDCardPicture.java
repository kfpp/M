package com.qqonline.conmon.async.scanSDCard;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.conmon.sdcardutils.SDCardFileUtil;
import com.qqonline.mpf.R;

import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * ???SD??????????????
 *
 * @author YE
 */
public class AsyncScanSDCardPicture extends
        AsyncTask<String, String, String> {

    private final static String TAG = "AsyncScanSDCardPicture";
    private final static String DIRETORYNAME = "diretoryName";
    private final static String DIRETORYPATH = "diretoryPath";
    private final static String PICTURECOUNTS = "pictureCounts";
    public final static String[] PICTURETYPES = {"jpg"};

    private Context context;
    private ProgressDialog builder;
    private SDCardFileUtil sdcardFileUtil;
    /**
     * ????忌?????
     */
    private ArrayList<Map<String, String>> pictureDiretoryList;
    /**
     * ???????????? pictureDiretoryList ?忌??快?竹??
     */
    private ArrayList<Integer> selectItemNumber;
    private boolean isStoped;
    /**
     * ??????
     */
    private int pictureCount;

    public AsyncScanSDCardPicture(Context context) {
        this.context = context;
        isStoped = false;
        pictureDiretoryList = new ArrayList<Map<String, String>>();

        builder = new ProgressDialog(context);
        builder.setIcon(android.R.drawable.ic_menu_gallery);
        builder.setTitle(R.string.import_picture_result_dialog_title);
        builder.setMessage(context.getResources().getString(R.string.import_picture_result_dialog_message));
        builder.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        builder.setOnDismissListener(new MyListener());
        selectItemNumber = new ArrayList<Integer>();
        sdcardFileUtil = new SDCardFileUtil();

        pictureCount = 0;
    }

    @Override
    protected void onPreExecute() {
        builder.show();
        //Toast.makeText(context, context.getString(R.string.begin_scan_sdcard), Toast.LENGTH_SHORT).show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        Log.i(TAG, "???????SD?????" + PICTURETYPES + " ???????????");
        String[] paths = GetExtSDCardPath.pathName;
        File file = null;
        for (int i = 0; i < paths.length; i++) {
            file = new File(paths[i]);
            if (file == null) continue;
            if (file.exists() && file.list() != null && file.list().length > 0) {
                break;
            }
        }
        if (file == null || file.listFiles() == null) {
            return null;
        }
        int rootFileCount = sdcardFileUtil.getDiretoryFileCountsWithoutChild(
                file.getAbsolutePath(), PICTURETYPES);
        if (rootFileCount > 0) {
            addScanedDirectory(file.getAbsolutePath(), context.getResources().getString(R.string.sdcard_root_path), rootFileCount);
        }
        for (File tempFile : file.listFiles()) {
            if (isStoped) {
                break;
            }
            //????????????
            //int count= getDiretoryPictureCounts(tempFile.getAbsolutePath());
            int count = sdcardFileUtil.getDiretoryFileCounts(tempFile.getAbsolutePath(), PICTURETYPES);
            if (count > 0) { //?????????????????0????????????????
                addScanedDirectory(tempFile.getAbsolutePath(), count);
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        //??????焟??????????忌?
        builder.dismiss();
        if (isStoped) {
            return;
        }
        Log.i(TAG, "???SD?????");
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_picture_import_list, null);
        ListView list = (ListView) view.findViewById(R.id.lv_import_pictures);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        SimpleAdapter adapter = new SimpleAdapter(context, pictureDiretoryList, android.R.layout.simple_list_item_multiple_choice, new String[]{DIRETORYNAME}, new int[]{android.R.id.text1});
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                list_item_clicked(parent, view, position, id);
            }
        });
        new AlertDialog.Builder(context)
                .setView(view)
                .setTitle(R.string.scan_result)
                .setIcon(android.R.drawable.ic_menu_search)
                .setNegativeButton(R.string.Cancle, null)
                .setPositiveButton(R.string.import_picture, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btn_OK_clicked(dialog, which);

                    }
                })
                .show();
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(String... values) {
//		Toast.makeText(context, context.getString(R.string.end_add_picture_from_sdcard), Toast.LENGTH_SHORT).show();
        super.onProgressUpdate(values);
    }

    private void addScanedDirectory(String path, int count) {
        addScanedDirectory(path, null, count);
    }

    /**
     * ?????????????
     *
     * @param path  ?????﹞??
     * @param name  ?忌??????????????null???????????????
     * @param count ??????????????
     */
    private void addScanedDirectory(String path, String name, int count) {
        File dir = new File(path);
        String dirName = null;
        if (count > 0) { //?????????????????0????????????????
            Map<String, String> item = new HashMap<String, String>();
            if (name == null || name.trim().equals("")) {
                dirName = dir.getName();
            } else {
                dirName = name;
            }
            item.put(DIRETORYNAME, dirName + " (" + String.valueOf(count) + ")");
            item.put(PICTURECOUNTS, String.valueOf(count));
            item.put(DIRETORYPATH, path);
            pictureDiretoryList.add(item);
        }
    }

    private final class MyListener implements DialogInterface.OnDismissListener {

        @Override
        public void onDismiss(DialogInterface dialog) {
            isStoped = true;
            sdcardFileUtil.stop();
            Log.i(TAG, "?????SD??");
        }

    }

    //??????忌????????????
    private void list_item_clicked(AdapterView<?> parent, View view, int position, long id) {
        selectItemNumber.add(Integer.valueOf(position));
    }

    //??????????????????
    private void btn_OK_clicked(DialogInterface dialog, int which) {
        final StringBuilder sb = new StringBuilder();
        final StringBuilder sbCount = new StringBuilder();
        for (int i = 0; i < selectItemNumber.size(); i++) {
            int index = selectItemNumber.get(i).intValue();
            Map<String, String> item = pictureDiretoryList.get(index);
            sb.append(item.get(DIRETORYPATH) + ",");
            int count = 0;
            try {
                count = Integer.parseInt(item.get(PICTURECOUNTS));
            } catch (Exception e) {
                e.printStackTrace();
            }
            sbCount.append(String.valueOf(count) + ",");
            pictureCount += count;
        }
        sb.replace(sb.length() - 1, sb.length(), ""); //?????????????
        sbCount.replace(sbCount.length() - 1, sbCount.length(), ""); //?????????????
        AsyncLoadSDCardPictureDiretory load = new AsyncLoadSDCardPictureDiretory(context);
        load.execute(sb.toString(), String.valueOf(pictureCount), sbCount.toString()); //???快????????﹞??????快?????????????????我????????
        Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
    }

    public void stop() {
        isStoped = true;
    }

}
