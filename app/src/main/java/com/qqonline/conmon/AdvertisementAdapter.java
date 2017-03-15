package com.qqonline.conmon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class AdvertisementAdapter extends BaseAdapter {

	private Context _context;
	private int[] _imageList;
	public AdvertisementAdapter(Context context,int[] imageList) {
		_context=context;
		_imageList=imageList;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return _imageList.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return _imageList[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView tempView=new ImageView(_context);
		tempView.setImageResource(_imageList[position]);
		tempView.setScaleType(ScaleType.FIT_XY);
		tempView.setAdjustViewBounds(true);
//		tempView.setBackground();
		return tempView;
	}

}
