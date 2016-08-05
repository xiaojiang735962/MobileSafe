package com.project.mobilesafe.adapter;

import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

public abstract class MyBaseAdapter<T> extends BaseAdapter {

	public List<T> lists;
	public Context mContext;
	
	
	public MyBaseAdapter(List<T> lists, Context mContext) {
		this.lists = lists;
		this.mContext = mContext;
	}

	public MyBaseAdapter() {
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
