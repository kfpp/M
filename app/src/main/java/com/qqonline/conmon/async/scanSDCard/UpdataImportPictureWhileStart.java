package com.qqonline.conmon.async.scanSDCard;

import java.util.ArrayList;

import com.qqonline.conmon.DATA;
import com.qqonline.conmon.sdcardutils.SDCardFileUtil;
import com.qqonline.domain.ImportRecord;
import com.qqonline.Manager.db.ImportRecordService;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class UpdataImportPictureWhileStart extends
		AsyncTask<String, String, String> {
	private final static String TAG="UpdataImportPictureWhileStart";
	private ImportRecordService importPictureService;
	private Context context;
	private ArrayList<ImportRecord> recordList;
	private SDCardFileUtil fileUtil;
	public UpdataImportPictureWhileStart(Context context) {
		this.context=context;
		importPictureService=new ImportRecordService(context, DATA.DATAVERSION);
		fileUtil=new SDCardFileUtil();
	}
	@Override
	protected void onPreExecute() {
		recordList=importPictureService.getAllRecord();		
		super.onPreExecute();
	}
	@Override
	protected String doInBackground(String... params) {
		Log.i(TAG,"doInBackground");
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < recordList.size(); i++) {
			int count=fileUtil.getDiretoryFileCounts(recordList.get(i).getPath(), new String[]{"jpg"});
			if (count == recordList.get(i).getCount()) {
				continue;
			}
			else {
				sb.append(String.valueOf(i)+",");
			}
		}
		int index=sb.lastIndexOf(",");
		if (index != -1) {
			sb.replace(index, sb.length(), "");
		}
		return sb.toString();
	}
	@Override
	protected void onPostExecute(String result) {
		Log.i(TAG,"onPostExecute");
		String [] indexs=result.split(",");
		StringBuilder currentCountString=new StringBuilder();
		int allCount=0;
		if (indexs.length < 1 || indexs[0].equals("")) {
			Log.i(TAG,"已导入图片无需更新");
			return ;
		}
		for (int i = 0; i < indexs.length; i++) {
			importPictureService.deleteRecord(recordList.get(Integer.valueOf(indexs[i])).getPath());
			int currentCount=recordList.get(Integer.valueOf(indexs[i])).getCount();
			allCount += currentCount;
			currentCountString.append(String.valueOf(currentCount)+",");
		}
		int index=currentCountString.lastIndexOf(",");
		if (index != -1) {
			currentCountString.replace(index, currentCountString.length(), "");
		}
		AsyncLoadSDCardPictureDiretory load=new AsyncLoadSDCardPictureDiretory(context);
		load.execute(result,String.valueOf(allCount),currentCountString.toString());
		super.onPostExecute(result);
	}

}
