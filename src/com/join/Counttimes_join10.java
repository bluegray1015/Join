package com.join;

/** �p�������� */


import android.util.Log;

public class Counttimes_join10 extends Thread { 
	
	private Saudioclient_join10 observe;
	private Join10 main;
	
	public void setAudiothread (Saudioclient_join10 ob){
		
		observe = ob; // �]�w�[�����thread
		
	}
	
	public void setMainActivity (Join10 m){
		
		main = m; // �]�w�n�[��D�{��
		
	}
	
	
	public void run() {
		
		try {
    		
    		Thread.sleep(5000); // �]�w�����ɶ�(�]�p��3����)
    		if(observe.m_keep_running){
    			observe.free(); // �N�[�����thread����
    			//main.dialog1.dismiss();
    			Log.d("detect","free");
    		}
        }
    	catch(Exception e) {
    		Log.d("sleep exceptions...\n","");
    	}
		
	}

}
