package com.join;

/** 錄音執行緒 thread*/

import java.io.*;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class Saudioclient_join10 extends Thread { 
	
    protected AudioRecord m_in_rec; // 宣告錄音物件
    protected int         m_in_buf_size; // 宣告buffer size值
    
    protected short []      buffer; // 宣告buffer物件
    
    protected boolean     m_keep_running; // 宣告是否錄音的指標物件
    protected boolean     thread_running;
    //=======================================
    protected boolean     voice_active;
    protected boolean     voice_S1;
    protected double [] E_max = new double[500];
    protected double [] E_min = new double[500];
    protected double [] E_now = new double[500];
    protected double [] E_thr = new double[500];
    int Nf=0;
    //=======================================
    int sampleRateInHz = 16000; //8000 44100, 22050 and 11025  宣告建置取樣頻率值       
    int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; // 宣告建置channel configuration值         
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT; // 宣告建置encoding值
    
	private File SDCardpath; // 宣告SD card目錄物件
	private File myRecAudioFile; // 宣告檔案路徑物件
	private File myRecAudioDir; // 宣告檔案所屬目錄物件
	
	private boolean sdCardExit;
	
	private Join10 main;
	private Counttimes_join10 time1;
	
	
	public void setMainActivity (Join10 m){
		
		main = m; // 設定要觀察的主程式
		
	}
    
    // 開始執行thread前的初始化method
    public void init() {
    	
    	// 建置錄音物件的buffer size
    	m_in_buf_size =  AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    	
    	// 建置錄音物件
    	m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRateInHz, channelConfig, audioFormat, m_in_buf_size);
    	
    	
    	// 建置每個buffer element的reference array，每個element type 為 short
    	buffer = new short [m_in_buf_size];
    	
    	
    	// 判斷SD Card是否插入
	    sdCardExit = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	    if (sdCardExit){
	    	
	    	SDCardpath = Environment.getExternalStorageDirectory();
	    	myRecAudioDir = new File( SDCardpath.getAbsolutePath() + "/temprecord" );
            
	    	// 若沒有此目錄
            if( !myRecAudioDir.exists() ) {
            	
            	myRecAudioDir.mkdirs();
            	
            }
	    
	    }
    	
    	
    }
    
    // 執行thread的method
    public void run() {
    	
    	thread_running = true;
    	
    	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
    	
  	  	myRecAudioFile = new File (SDCardpath.getAbsolutePath() + "/temprecord/test.pcm");
  	  	
  	  	try {
  	  		
  	  		//如果文件已經存在，先刪除(覆蓋原本的檔案)
  	        if (myRecAudioFile.exists()) myRecAudioFile.delete();
  				
  	        // 建立file
  	        myRecAudioFile.createNewFile();
  	        
  	        
  	        // 建立檔案輸入stream物件
  	        FileOutputStream fisWriter = new FileOutputStream (myRecAudioFile);
    		BufferedOutputStream bos = new BufferedOutputStream(fisWriter);
 	  		DataOutputStream dos = new DataOutputStream(bos);
 	  		
  	  		// 開始錄音
  	  		m_in_rec.startRecording();
  	  		
  	  		// 開始計時
  	  		time1 = new Counttimes_join10();
  	  		time1.setMainActivity(main);
  	  		time1.setAudiothread(Saudioclient_join10.this);
  	  		time1.start();
  	  		
  	  		// 將可開始錄音的指標設為true
  	    	m_keep_running = true;
  	  		
  	    	// VAD初始化
  	    	voice_active = false;
  	    	voice_S1=true;
  	    	int fc=0;//fc => 抓取音框到512點(32ms)
  	    	short[] frame = new short [512];
  	    	E_min[0]=30;
  	    	E_max[0]=0;
  	    	int EPC=0,IR=0;
  	    	Nf=0;
  	    	
  	  		// 將錄音資料寫入檔案
  	  		while(m_keep_running) {

  	  			int len = m_in_rec.read(buffer, 0, m_in_buf_size);
  	  			int i=0;
  	  			
  	  			while(i<len){//buffer while
  	  				
  	  				if(fc<512){//不足一個frame的時候繼續抓取資料
  	  					
  	  					frame[fc] = buffer[i];
  	  					fc++;
  	  				}
	  				else{
	  					//之後要把FFT功能寫在這裡//
	  					
	  					//frame[511] = buffer[i];
	  					/* 抓滿一個frame, 丟進state machine檢查
	  					 * S0: 更新globle E_max/min
	  					 * S1: 偵測語音起始點
	  					 * S2: 端點標記
	  					 * S3: 等待下一段語音
	  					 * S4: 雜音判定
	  					 * S5: 端點判決
	  					 */
	  					Nf++;
	  					E_now[Nf]=E_cal(frame);
	  					E_refresh(E_cal(frame));
	  					E_thr[Nf] = 0.8*E_min[Nf]+(0.2)*(E_max[Nf]-E_min[Nf]);
	  					/* State 0: 
	  					 * 更新globle E_max/min
	  					 * 動態調整threshold
	  					 * Nf = Nf+1;
	  					*/

	  					if(voice_S1){
	  						/* 判斷是否有語音輸入
	  						 * 因為voice_S1=true, 無論如何一開始會先進這裡
	  						 */

	  						// Case.1  開始為背景雜訊
	  						if((E_max[Nf]>5*E_min[Nf])&&(Nf>5)){
	  							voice_S1=false;
	  							for(int j=(Nf-5);j<Nf;j++){
	  								if (E_max[j]<E_max[j+1]){
	  									;
	  									//如果連續5個frame成長, 跳到下一個state
	  								}
	  								else{
	  									//只要不是連續5個frame都成長
	  									voice_S1=true;
	  								}
	  							}
	  						}
	  						else{
	  						// Case.2  開始為語音訊號
	  						}
	  					}
	  					else{
	  						/* voice_S1=false
	  						 * State 2: 表示語音開始進入, 接下來要找到結束端點
	  						 */
	  						if(E_now[Nf]<E_thr[Nf]){
	  							//語音停止進入, 進入State 3: 判斷是否有下一段語音
	  							Log.d("detect", "E_now["+Nf+"]="+E_now[Nf]);
	  							Log.d("detect", "E_thr["+Nf+"]="+E_thr[Nf]);
	  							EPC++;
	  							IR=0;
	  							if(EPC>50){
	  								//找到結束端點
	  								m_keep_running=false;
	  								time1.interrupt();
	  								Log.d("detect", "Nf="+Nf);
	  								Log.d("detect", "EPC="+EPC);
	  							}
	  						}
	  						else{
	  							//E_now>E_thr, 有語音繼續進入
	  							//State 4: 判斷是否為雜訊干擾
	  							if(IR>5){
	  								EPC=0;
	  								IR=0;
	  							}
	  							else{
	  								EPC++;
	  								IR++;
	  							}
	  						}
	  					}
	  					/* 繼續抓取下一個frame
	  					 * frame shift = 10ms => 跳過160點
	  					 * 複製512-160=352點
	  					 */
	  					
	  					fc=352; 
	  					for(int j=0;j<352;j++){
	  						frame[j]=frame[160+j];
	  					}

	  				}//if-else
  	  				
  	  				
	  				Log.d("message", "Number "+i+": "+String.valueOf(buffer[i]));
	  				// 寫入, len似乎為512
	  				dos.writeShort(buffer[i]);
	  				i++;
	  			}
  	  		}//end of while
  	  		
  	  		for(int ii=0;ii<Nf;ii++){
  	  			Log.d("E_now", String.valueOf(E_now[ii]));
  	  			Log.d("E_max", String.valueOf(E_max[ii]));
  	  			Log.d("E_min", String.valueOf(E_min[ii]));
  	  			Log.d("E_thr", String.valueOf(E_thr[ii]));
  	  		}
  	  			  		
  	  		// 關閉錄音物件
  	  		m_in_rec.stop();
  	  		main.bulidsockethandler();
  	  		// 關閉寫入資料的物件
  	  		dos.flush();
  	  		dos.close();
  	  		//main.dialog1.dismiss();
  	  		thread_running = false;

  	  	}
  	  	catch (FileNotFoundException e1) {
  	  		e1.printStackTrace();
  	  	} 
  	  	catch (IOException e) {
  	  		e.printStackTrace();
  	  	}
  
    }
    
    // 結束執行thread的method
    public void free() {
    	m_keep_running = false;
    }
    
    public boolean getrunning(){
    	
    	return thread_running;
    }
    
    public File getfile(){
    	
    	return myRecAudioFile;
    }
    
    public double E_cal(short[] frame){
    	double E=0;
    	for(int i=0;i<512;i++){
    		E = E+((double)frame[i])*((double)frame[i]);
    	}
    	return E/100000000;    	
    }
    
    public void E_refresh(double E){
    	if (E<E_min[Nf-1]){
    		E_min[Nf]=0.5*(E+E_min[Nf-1]);
    	}
    	else{
    		E_min[Nf]=E_min[Nf-1];
    	}
    	if (E>E_max[Nf-1]){
    		E_max[Nf]=0.5*(E+E_max[Nf-1]);
    	}
    	else{
    		E_max[Nf]=E_max[Nf-1];
    	}
    }
    
}  
