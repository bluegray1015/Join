package com.join;

/** �t�d�ǻw�P������ƪ�thread */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.view.View.OnClickListener;
import android.widget.Toast;


public class Voice_TxRxThread_join10 extends Thread{
	
//	private Socket s;        // �Ȥ�ݳs�uSocket����
	private BufferedReader recog_in;
//	private PrintWriter out;
	private Join10 main;
	
	
	private String in_recog_Str;
	
	public Voice_TxRxThread_join10(BufferedReader reader) {    // ��thread��Constructor
			
//		s = socket;
		recog_in = reader;
//		out = writer;
	}
	
	// ���w�o��Thread���T���[���
	public void setMessageObserver(Join10 activity) {
		main = activity;
	}
	
	
	
	public String getUserData() {
		return in_recog_Str;
	}
	
	public void run(){
		
		try {
			
			// �t�d��ťserver�^�Ǫ�data
			while((in_recog_Str = recog_in.readLine())!= null) {
				
	//			Toast.makeText(main, "����", Toast.LENGTH_LONG).show();	
				main.goDataUpdatehandler(); // ����server�^�Ǫ��y�����ѵ��G
				
			}
	    
			if(in_recog_Str == null){
					
				main.overSockethandler(); // ���줤�_�s�u���R�O
					
			}
			
		}catch(IOException e){
			
		}catch(Exception e){
			
		}
		
	}

}
