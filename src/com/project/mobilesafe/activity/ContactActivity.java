package com.project.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.project.mobilesafe.R;

public class ContactActivity extends Activity {
	
	private ListView lvContact;
	private ArrayList<HashMap<String, String>> contact;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		
		lvContact = (ListView) findViewById(R.id.lv_contact);
		contact = getContact();
		System.out.println(contact);
		lvContact.setAdapter(new SimpleAdapter(this, contact, R.layout.contact_list_item, 
				new String[]{"name","phone"}, new int[]{R.id.tv_name,R.id.tv_phone}));
		lvContact.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
					//读取当前的电话号码
					String phone = contact.get(position).get("phone");
					Intent intent = new Intent();
					intent.putExtra("phone", phone);
					setResult(Activity.RESULT_OK, intent);//将数据放在intent中返回给上一个页面
					finish();
			}
		});
	}
	public ArrayList<HashMap<String, String>> getContact(){
		/**
		 * 首先从raw_contants中读取联系人id("contant_id")
		 * 其次,根据contant_id从data表中查询出相应的电话号码和联系人名称
		 * 根据mimetype来区分哪个是联系人，那个是电话号码
		 */
		//首先从raw_contants中读取联系人id("contant_id")
		//其次,根据contant_id从data表中查询出相应的电话号码和联系人名称
		//根据mimetype来区分哪个是联系人，那个是电话号码
		Uri rawContacts = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dataContacts = Uri.parse("content://com.android.contacts/data");
		
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		//从raw_contants中读取联系人id("contant_id")
		Cursor rawContactsCursor = getContentResolver().query(rawContacts, new String[]{"contact_id"}, null, null, null);
		if(rawContactsCursor != null){
			while(rawContactsCursor.moveToNext()){
				String contactId = rawContactsCursor.getString(0);
				//根据contant_id从data表中查询出相应的电话号码和联系人名称
				Cursor dataCursor = getContentResolver().query(dataContacts, new String[]{"data1","mimetype"}, "contact_id=?", 
						new String[]{contactId}, null);
				if(dataCursor != null){
					HashMap<String, String> map = new HashMap<String, String>();
					while(dataCursor.moveToNext()){
						String data1 = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);
						if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
							map.put("phone", data1);
						}else if("vnd.android.cursor.item/name".equals(mimetype)){
							map.put("name", data1);
						}
					}
					list.add(map);
					dataCursor.close();
				}
			}
			rawContactsCursor.close();
		}
		return list;
	}
	
}
