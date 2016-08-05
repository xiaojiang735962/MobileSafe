package com.project.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.project.mobilesafe.bean.BlackNumberInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

public class BlackNumberDao {

	private BlackNumberOpenHelper helper;

	public BlackNumberDao(Context context){
		helper = new BlackNumberOpenHelper(context);
	}
	/**
	 * @param number 黑名单号码
	 * @param mode	拦截模式
	 * @return
	 */
	public boolean add(String number,String mode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values= new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		long insert = db.insert("blacknumber", null, values);
		db.close();
		if(insert == -1){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * 通过电话号码删除
	 * @param number 黑名单电话号码
	 * @return
	 */
	public boolean delete(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
		int delete = db.delete("blacknumber", "number = ?", new String[]{number});
		db.close();
		if(delete == 0){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * 通过电话号码修改拦截模式
	 * @param number 黑名单电话号码
	 * @return
	 */
	public boolean changeNumberMode(String number,String mode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		int update = db.update("blacknumber", values, "number = ?", new String[]{number});
		db.close();
		if(update == 0){
			return false;
		}else{
			return true;
		}
	}
	//通过电话号码进行查找
	public String findNumber(String number){
		String mode = "";
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number = ?", new String[]{number}, null, null, null);
		if(cursor.moveToNext()){
			mode = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return mode;
	}
	//查询所以黑名单
	public List<BlackNumberInfo> findAll(){
		SQLiteDatabase db = helper.getReadableDatabase();
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
		Cursor cursor = db.query("blacknumber", new String[]{"number","mode"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setNumber(cursor.getString(0));
			blackNumberInfo.setMode(cursor.getString(1));
			blackNumberInfos.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		//休眠3秒加载数据
		SystemClock.sleep(2000);
		return blackNumberInfos;
	}
	/**
	 * 分页加载数据
	 * @param pageNumber 表示当前是那一页
	 * @param pageSize	表示每一页有多少条数据
	 * @return
	 * limit 表示限制当前有多少数据
	 * offset 表示跳过，从第几条开始
	 */
	public List<BlackNumberInfo> findPar(int pageNumber,int pageSize){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?", new String[]{
				String.valueOf(pageSize),String.valueOf(pageSize * pageNumber)});
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setNumber(cursor.getString(0));
			blackNumberInfo.setMode(cursor.getString(1));
			blackNumberInfos.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return blackNumberInfos;
	}
	
	/**
	 * 分批加载数据
	 * @param startIndex 开始查询的位置
	 * @param maxCount	表示每一页有多少条数据
	 */
	public List<BlackNumberInfo> findPar2(int startIndex,int maxCount){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?", new String[]{
				String.valueOf(maxCount),String.valueOf(startIndex)});
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setNumber(cursor.getString(0));
			blackNumberInfo.setMode(cursor.getString(1));
			blackNumberInfos.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return blackNumberInfos;
	}
	//获取总的记录数
	public int getTotalNumber(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		db.close();
		return count;
	}
}
