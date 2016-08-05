package com.project.mobilesafe.junit;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.test.AndroidTestCase;

import com.project.mobilesafe.bean.BlackNumberInfo;
import com.project.mobilesafe.db.dao.BlackNumberDao;

public class TestBlackNumberDao extends AndroidTestCase {

	public Context mContext;
	@Override
	protected void setUp() throws Exception {
		this.mContext = getContext();
		super.setUp();
	}
	//测试添加黑名单数据库
	public void testAdd(){
		BlackNumberDao dao = new BlackNumberDao(mContext);
		Random random = new Random();
		for (int i = 0; i < 200; i++) {
			Long number = 18829346020l + i;
			dao.add(number +"", String.valueOf(random.nextInt(3) + 1));
		}
	}
	//测试删除黑名单数据库
	public void testDelete(){
		BlackNumberDao dao = new BlackNumberDao(mContext);
		boolean delete = dao.delete("18829346021");
		assertEquals(true, delete);
	}
	//测试根据电话号码查找
	public void testFind(){
		BlackNumberDao dao = new BlackNumberDao(mContext);
		String mode = dao.findNumber("18829346081");
		System.out.println(mode);
	}
	//测试查找黑名单所以人
	public void testFindAll(){
		BlackNumberDao dao = new BlackNumberDao(mContext);
		List<BlackNumberInfo> blackNumberInfos = dao.findAll();
		for (BlackNumberInfo blackNumberInfo : blackNumberInfos) {
			System.out.println(blackNumberInfo.getNumber() + ";" + blackNumberInfo.getMode());
		}
		
	}
}
