package com.join;

/** �p�������� */


import android.util.Log;

public class Counttimes extends Thread { 
	
	private Saudioclient observe;
	private TimeMergeResult main;
	
	public void setAudiothread (Saudioclient ob){
		
		observe = ob; // �]�w�[�����thread
		
	}
	
	public void setMainActivity (TimeMergeResult m){
		
		main = m; // �]�w�n�[��D�{��
		
	}
	
	
	public void run() {
		
		try {
    		
    		Thread.sleep(5000); // �]�w�����ɶ�(�]�p��3����)
    		if(observe.m_keep_running){
    			observe.free(); // �N�[�����thread����
    			Log.d("detect","free");
    		}
        }
    	catch(Exception e) {
    		Log.d("sleep exceptions...\n","");
    	}
		
	}

}
