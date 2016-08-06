package com.join;

/** 負責傳誦與接收資料的thread */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.view.View.OnClickListener;
import android.widget.Toast;


public class Voice_TxRxThread extends Thread{
	
//	private Socket s;        // 客戶端連線Socket物件
	private BufferedReader recog_in;
//	private PrintWriter out;
	private TimeMergeResult main;
	
	
	private String in_recog_Str;
	
	public Voice_TxRxThread(BufferedReader reader) {    // 此thread的Constructor
			
//		s = socket;
		recog_in = reader;
//		out = writer;
	}
	
	// 指定這個Thread的訊息觀察者
	public void setMessageObserver(TimeMergeResult activity) {
		main = activity;
	}
	
	
	
	public String getUserData() {
		return in_recog_Str;
	}
	
	public void run(){
		
		try {
			
			// 負責傾聽server回傳的data
			while((in_recog_Str = recog_in.readLine())!= null) {
				
//				Toast.makeText(main, "收到", Toast.LENGTH_LONG).show();	
				main.goDataUpdatehandler(); // 當收到server回傳的語音辨識結果
				
			}
	    
			if(in_recog_Str == null){
					
				main.overSockethandler(); // 當收到中斷連線的命令
					
			}
			
		}catch(IOException e){
			
		}catch(Exception e){
			
		}
		
	}

}
