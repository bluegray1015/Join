package com.join;

/** 計算錄音秒數 */


import android.util.Log;

public class Counttimes_join10 extends Thread { 
	
	private Saudioclient_join10 observe;
	private Join10 main;
	
	public void setAudiothread (Saudioclient_join10 ob){
		
		observe = ob; // 設定觀察的錄音thread
		
	}
	
	public void setMainActivity (Join10 m){
		
		main = m; // 設定要觀察的主程式
		
	}
	
	
	public void run() {
		
		try {
    		
    		Thread.sleep(5000); // 設定錄音時間(設計為3秒鐘)
    		if(observe.m_keep_running){
    			observe.free(); // 將觀察的錄音thread關閉
    			//main.dialog1.dismiss();
    			Log.d("detect","free");
    		}
        }
    	catch(Exception e) {
    		Log.d("sleep exceptions...\n","");
    	}
		
	}

}
