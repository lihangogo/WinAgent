package com;

import java.util.TimerTask;

import com.mybatis.OnlineTempService;

public class CheckTask extends TimerTask {

	@Override
	public void run() {
		OnlineTempService onlineTempService=new OnlineTempService();
		onlineTempService.deleteAllOnlineTemp();
	}
}
