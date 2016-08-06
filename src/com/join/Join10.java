package com.join;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.shoushuo.android.tts.ITts;

public class Join10 extends Activity {
    /** Called when the activity is first created. */
	private TextView  invitedfri[]; // 宣告負責儲存TextView reference的一維陣列
	private RadioButton timechioce[];// 宣告負責儲存RadioButton reference的陣列(每個group項分屬的RadioButton)
	private TextView activity_destination;
	private TextView activity_name;
    private ImageView activity_image;
    private TextView activity_memo;
    private Button button_go;
    private Button button_reject;
    
	private int timenum=0;
	private String ct = null;
	private String threadnum;
	private String symbol1="@g@";
	private String symbol2="@f@";
	private String symbol3="@d@";
	private String symbol4="@t@";
	private String symbol5="@m@";
	private String symbolv="@vt@";
	private String reaction;
	private String action;
	private String user;
//	private String action="createActivity:SOGO@g@kapin@vt@1@vt@@f@rebaca@f@myself@f@Lollipops新竹SOGO百貨3F@d@11點51分@t@12點51分@t@let's go@m@";
	private String act = "vote:";
	private TableLayout layoutfri;
	private RadioGroup layouttime;
	// 語音輸出所需變數
	public static String TAG = "DEBUG";
	private TextToSpeech tts;
	/*中文語音*/
	private Thread tts1;
	private ITts ttsService; 
	private boolean ttsBound;
	private String ttsString1 = "";
	private String ttsString2 = "";
	private String ttsString3 = "";
	private String ttsString4 = "";
	private String ttsString5 = "";
	private String ttsString6 = "";
	/*模式變數*/
	private int mode = 1;
	/*錄音相關*/
	protected Saudioclient_join10 m_recorder;
	public ProgressDialog dialog1,dialog2;
	private int chosen=-1;
	private int chosen_num=0;
	int counter=0;
	/*SENSOR*/
	private SensorManager mSensorManager;
	private boolean sensor_flag=false;
	
	
	
	/*socket*/
	 public static Socket voice_client;
	 public String savefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temprecord/test.pcm";
	 private Voice_TxRxThread_join10 txrx;
	 public static BufferedReader voice_in;
	 public static PrintWriter voice_out;
	 private String results;  // 宣告儲存辨識結果的字串  
	
	    @Override
	    protected void onPause(){
	    	mSensorManager.unregisterListener(mSensorEventListener);
	    	super.onPause();
	    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_join10);
        tts = new TextToSpeech(this, ttsInitListener);
        Bundle bundleStr = this.getIntent().getExtras();
    	reaction = bundleStr.getString("Key_Str");
    	user = bundleStr.getString("Key_User");
    	try {
			action = java.net.URLDecoder.decode(reaction, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	
        findViews();
        
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE); 
        mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), 1);
        
        try {
			showlayout();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
        setListeners();
    }
	private void findViews() {
		layoutfri = (TableLayout)findViewById(R.id.join10_friendlayout);
		layouttime = (RadioGroup)findViewById(R.id.join10_timegroup);
		activity_name = (TextView) findViewById(R.id.join10_group_name);
		activity_image = (ImageView) findViewById(R.id.join10_activity_image);
		activity_destination = (TextView) findViewById(R.id.join10_destination);
		activity_memo = (TextView) findViewById(R.id.join10_memo);
		button_go = (Button) findViewById(R.id.join10_go);
		button_reject = (Button) findViewById(R.id.join10_reject);
		
		
		invitedfri = new TextView[20];
		for(int i=0;i<20;i++){  
        	
			invitedfri[i] = new TextView(this);    
			invitedfri[i].setTextColor(Color.RED);
        }
		timechioce = new RadioButton[10];
		for(int i=0;i<10;i++){  
        	
			timechioce[i] = new RadioButton(this);       
        }
		
		 
	}
	
	private void showlayout() throws RemoteException {
		
    	int loc1 = 0;               // 宣告分開符號在字串的位置變數
		int loc2 = 0;
		int loc3 = 0;
		int loc4 = 0;
		int loc5 = 0;
		int locv = 0;
		
		//----切割字串----
        action=action.substring(15); // 先將標頭去除
        loc1 = action.indexOf(symbol1,0);   // 尋找@g@出現的index number
		activity_name.setText(action.substring(0,loc1)); // 存入group type
		loc1 = loc1 + symbol1.length();
		action = action.substring(loc1);
		if(mode == 1){
			ttsString1 = "受邀的群組為 ";
			ttsString2 = activity_name.getText().toString()+"  ";
			ttsString3 ="受邀的朋友有 ";
		}
		int j=0;
		while(loc2 >= 0)
		{
			loc2 = action.indexOf(symbol2,0);   // 尋找@f@出現的index number
			if (loc2 >= 0) {
				invitedfri[j].setText(action.substring(0,loc2)); // 存入所邀請的朋友
				if(mode == 1&&j>0)
				{
					ttsString4 = ttsString4 + action.substring(0,loc2)+" ";
					counter++;
				}
				layoutfri.addView(invitedfri[j]);  // 將邀請的朋友加入layout
				loc2 = loc2 + symbol2.length();
				action = action.substring(loc2);
				
				if(j==0)
				{
					String vt = invitedfri[0].getText().toString();
					locv = vt.indexOf(symbolv,0);   // 尋找@vt@出現的index number
					invitedfri[0].setText(vt.substring(0,locv)); // 存入group type
					ttsString4 = ttsString4 + vt.substring(0,locv)+" ";
					counter++;
					locv = locv + symbolv.length();
					vt = vt.substring(locv);
					
					locv = vt.indexOf(symbolv,0);   // 尋找@vt@出現的index number
					threadnum = vt.substring(0,locv); // 將thread判斷的數字存入
				}
				j++;
			}
		} // end while
		
		loc3 = action.indexOf(symbol3,0);   // 尋找@d@出現的index number
		activity_destination.setText(action.substring(0,loc3)); // 存入destination
		loc3 = loc3 + symbol3.length();
		action = action.substring(loc3);
		if(mode == 1){
			ttsString5 = "活動地點為 ";
			ttsString5 = ttsString5 + activity_destination.getText().toString()+"  ";
			ttsString5 = ttsString5 + "可選擇參加的時間有 ";
		}
		int jj=0;
		char item = '1';
		while(loc4 >= 0)
		{
			loc4 = action.indexOf(symbol4,0);   // 尋找@t@出現的index number
			if (loc4 >= 0) {
				timechioce[jj].setText(item+"."+action.substring(0,loc4)); // 存入時間的選項
				if(mode == 1)
					ttsString5 = ttsString5 + item+" " + action.substring(0,loc4)+"  ";
				timechioce[jj].setTextColor(Color.RED);
				layouttime.addView(timechioce[jj]);  // 將時間選項加入layout
				loc4 = loc4 + symbol4.length();
				action = action.substring(loc4);
				item++;
				timenum++;
				jj++;
			}
		} // end while
		chosen_num=jj;
		loc5 = action.indexOf(symbol5,0);   // 尋找@m@出現的index number
		activity_memo.setText(action.substring(0,loc5)); // 存入destination
		ttsString6 = action.substring(0,loc5);
		loc5 = loc5 + symbol5.length();
		action = action.substring(loc5);
		
		if(activity_name.getText().equals("Snack"))
		{
			activity_image.setImageDrawable(getResources().getDrawable(R.drawable.snack));
		}else if(activity_name.getText().equals("KTV"))
		{
			activity_image.setImageDrawable(getResources().getDrawable(R.drawable.ktv));
		}else if(activity_name.getText().equals("Shopping"))
		{
			activity_image.setImageDrawable(getResources().getDrawable(R.drawable.shopping));
		}
		
		Bundle bundle=this.getIntent().getExtras();
    	mode=bundle.getInt("mode");
    	
    	if(mode==1)
		{
    		tts1=new Thread(new tts_thread());
    		tts1.start();
    		
		}
	}
	
	private void setListeners() {
		button_go.setOnClickListener(buttonGo);
		button_reject.setOnClickListener(buttonReject);
	}
	private Button.OnClickListener buttonGo = new Button.OnClickListener(){
    	public void onClick(View v){
    		//傳送act給Server(活動發起者+選擇時間)
    		
    		
    		
    		act = act + invitedfri[0].getText() + symbolv + threadnum + symbolv + symbol2;
    		act = act + user + symbol2;
    		for(int t=0;t<timenum;t++)
			{
				if(timechioce[t].isChecked()) // 因為是單選Radio button，所以只會有一個被按
				{
					
					//Toast.makeText(Join10.this,timechioce[t].getText(), Toast.LENGTH_SHORT).show();
					act = act + timechioce[t].getText().toString().substring(2) + symbol4;
					ct = timechioce[t].getText().toString();
				}
			}
    		if (ct==null){ // 沒勾時間
				
				// 顯示警告方塊  
	    		new AlertDialog.Builder(Join10.this)
	    		.setTitle("注意")
	    		.setMessage("請選擇時間!")
	    		.setPositiveButton("確認", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();
				
			}else{
    		try {
				String enact = java.net.URLEncoder.encode(act , "UTF-8");
				//Toast.makeText(Join10.this,act, Toast.LENGTH_SHORT).show();
				Main.out.println(enact);     //傳答應邀請的字串給Server
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
    		
    		new AlertDialog.Builder(Join10.this)
    		.setTitle("活動")
    		.setMessage("馬上為您回傳投票結果!!")
    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Join10.this.finish();
				}}).show();
    		}
    		}};
    	private Button.OnClickListener buttonReject = new Button.OnClickListener(){
        	public void onClick(View v){
        		act = act + invitedfri[0].getText() + symbolv + threadnum + symbolv + symbol2;
        		Main.out.println(act);     //傳拒絕邀請的字串給Server
        		
        		Join10.this.finish();
        	}
        };
        
        public void recorderhandler(){
    		recorderHandler.sendEmptyMessage(0);
    	}
        
        private Handler recorderHandler = new Handler() {
        	
        	@Override 
            public void handleMessage(Message msg) {
        		m_recorder = new Saudioclient_join10();
    			
    			// 設定觀察的主程式
    			m_recorder.setMainActivity(Join10.this);
    			
    			// 初始化錄音緒
    			m_recorder.init();
    			// 開始執行錄音緒
    			m_recorder.start();
    			
    			// 顯示辨識的進度方塊  
    			dialog1 = new ProgressDialog(Join10.this);
    			dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    			dialog1.setTitle("語音辨識狀態");
    			dialog1.setMessage("錄音中");
    			dialog1.setIndeterminate(true);
    			dialog1.setCancelable(true);
    			dialog1.show();
        	}
        	
        };
	
        
        
        public void goDataUpdatehandler(){
    		messageHandler.sendEmptyMessage(0);
    	}
    	private Handler messageHandler = new Handler() { 
            
    		@Override 
            public void handleMessage(Message msg) {
    			
    			try{    				
    				dialog2.incrementProgressBy(40);
    				results = txrx.getUserData();
    			//	dialog2.dismiss(); // 將狀態顯示去除掉
    				String de_results = java.net.URLDecoder.decode(results, "UTF-8"); 
  		
    				if(de_results.compareTo("one")==0)
    				{
    					//Toast.makeText(Join10.this,"潘兔兔", Toast.LENGTH_SHORT).show();	
    					ttsService.speak("您選擇的是一  請確認",1);
    					chosen=0;
    					messageHandler.postDelayed(new Runnable(){

							public void run() {
								// TODO Auto-generated method stub
								recorderhandler();
							}
    						
    						
    					},5000);	
    				
    					// Toast.makeText(Join10.this,"潘兔兔", Toast.LENGTH_SHORT).show();	
    				    
    					//timechioce[0].setChecked(true);
    				}
    				else if(de_results.compareTo("two")==0)
    				{
    					ttsService.speak("您選擇的是二  請確認",1);
    					chosen=1;
    					messageHandler.postDelayed(new Runnable(){

							public void run() {
								// TODO Auto-generated method stub
								recorderhandler();
							}
    						
    						
    					},5000);	
    					//timechioce[1].setChecked(true);
    				}
    				else if(de_results.compareTo("three")==0)
					{
    					ttsService.speak("您選擇的是三  請確認",1);
    					chosen=2;
    					messageHandler.postDelayed(new Runnable(){

							public void run() {
								// TODO Auto-generated method stub
								recorderhandler();
							}
    						
    						
    					},5000);	
    					//timechioce[2].setChecked(true);
					}
    				else if(de_results.compareTo("four")==0)
					{
    					ttsService.speak("您選擇的是四  請確認",1);
    					chosen=3;
    					messageHandler.postDelayed(new Runnable(){

							public void run() {
								// TODO Auto-generated method stub
								recorderhandler();
							}
    						
    						
    					},5000);	
    					//timechioce[3].setChecked(true);
					}
    				else if(de_results.compareTo("five")==0)
					{
    					ttsService.speak("您選擇的是五  請確認",1);
    					chosen=4;
    					messageHandler.postDelayed(new Runnable(){

							public void run() {
								// TODO Auto-generated method stub
								recorderhandler();
							}
    						
    						
    					},5000);	
    					//timechioce[3].setChecked(true);
					}
    				else if(de_results.compareTo("Confirm")==0)
    				{
    					if(chosen>=chosen_num)
    					{
    						ttsService.speak("無此選項  請再次選擇",1);
    						chosen=-1;
    						messageHandler.postDelayed(new Runnable(){
    							public void run() {
    								// TODO Auto-generated method stub
    								recorderhandler();
    							}
        						
        						
        					},5000);	
    						
    					}
    					else
    					{
    					if(chosen==0)
    					{
    						ttsService.speak("一  已選擇",1);
    						timechioce[chosen].setChecked(true);
        					vote();
    					}
    					if(chosen==1)
    					{
    						ttsService.speak("二  已選擇",1);			
    						timechioce[chosen].setChecked(true);
        					vote();
    					}
    					if(chosen==2)
    					{
    						ttsService.speak("三  已選擇",1);
    						timechioce[chosen].setChecked(true);
        					vote();
    					}
    					if(chosen==3)
    					{
    						ttsService.speak("四  已選擇",1);
    						timechioce[chosen].setChecked(true);
        					vote();
    					}
    					if(chosen==4)
    					{
    						ttsService.speak("五  已選擇",1);
    						timechioce[chosen].setChecked(true);
        					vote();
    					}
    					if(chosen==-1)
    					{

    						ttsService.speak("您尚未選擇時間",1);
    						messageHandler.postDelayed(new Runnable(){
    							public void run() {
    								// TODO Auto-generated method stub
    								recorderhandler();
    							}
        						
        						
        					},5000);	
    					}
    					}
    					
    					
    				}
    				else if(de_results.compareTo("Cancel")==0)
    				{
    					ttsService.speak("請選擇您有空的時間",1);
    					chosen=-1;
    					messageHandler.postDelayed(new Runnable(){

							public void run() {
								// TODO Auto-generated method stub
								recorderhandler();
							}
    						
    						
    					},5000);	
    					
    				}
    				else
    				{
    					ttsService.speak("無法辨認",1);
    					
    					messageHandler.postDelayed(new Runnable(){

							public void run() {
								// TODO Auto-generated method stub
								recorderhandler();
							}
    						
    						
    					},5000);	

    					
    				}
    					
    				    				dialog2.dismiss(); // 將狀態顯示去除掉
    				
    			}catch(Exception e) {	
    				e.printStackTrace();
    				
    			}
    			
    		}	
    	};
        
        
    	 public void bulidsockethandler(){
 			socketHandler.sendEmptyMessage(0);
 		}
 		private Handler socketHandler = new Handler() {
 			
 			@Override 
 	        public void handleMessage(Message msg) {
 				
// 				dialog1.setMessage("辨識語音中"); // 已存好語音檔，故將狀態顯示變成"語音辨識中"
 				
 				
 				dialog2 = new ProgressDialog(Join10.this);
 				dialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
 				dialog2.setMessage("辨識語音中"); // 已存好語音檔，故將狀態顯示變成"語音辨識中"			
 				dialog2.show();
 				dialog2.incrementProgressBy(30);
 				dialog1.dismiss(); // 將狀態顯示去除掉
 				
 				
 				bulidsocket(); // 建立與語音辨是server的連線，並建立傳送接收thread
 				
 				//等待真正存好語音資料
 				while(m_recorder.getrunning()){
 					
 				}
 				
 				
 				sendFile(savefile); // 開始傳送語音檔案
 				m_recorder = null;
 							
 			}
 			
 		};
 		
 		
 		 public void bulidsocket(){
 		    	
 		    	try {
 		    		
 		    		// 建立資料連線
 		    		voice_client = new Socket("140.113.144.48",8082); //增加網絡接受流接受服務器文件資料
 		  		  	voice_in = new BufferedReader (new InputStreamReader(voice_client.getInputStream()));    // 建立Client socket input stream
 			        voice_out = new PrintWriter (voice_client.getOutputStream(), true);                      // 建立Client socket output stream
 			        
 			        txrx = new Voice_TxRxThread_join10(voice_in);
 			        txrx.setMessageObserver(this);
 			        txrx.start();
 		  		  	
 		  		  	
 		    	}
 		    	catch (Exception ex) {
 					  
 					  ex.printStackTrace();
 					  
 				  }
 		    	
 		    }
 		 
 		 /** 此為用thread可控制結束連線的handler*/
			public void overSockethandler(){
				overSocket.sendEmptyMessage(0);
			}
			private Handler overSocket = new Handler() {
				
				@Override 
		        public void handleMessage(Message msg) {
					
					try {
						
						// 中斷連線必須執行的動作
//			    		out.println("BYE"); // 向server要求要中斷連線
			    		voice_client.close(); // 關閉Socket
			    		
			    		
			    	}catch(IOException e){
						
					}
					
				}
				
			};   
 			
 		    /** 傳送語音檔的宣告method*/
 		    public void sendFile(String fileName) { //使用本地文件系統接受網絡資料並存為新文件
 		    	voice_out.println(user);
 		    		
 				  if (fileName == null) return; //增加文件流用來讀取文件中的資料
 					
 				  File file = new File(fileName);
 					
 						
 				  try {
 					  
 					  PrintWriter out1 = new PrintWriter (voice_client.getOutputStream(), true);
 					  OutputStream doc = new DataOutputStream(new BufferedOutputStream(voice_client.getOutputStream())); //增加文件讀取緩衝區
 					  
 					  FileInputStream fos = new FileInputStream(file); //增加網絡服務器接受客戶請求
 					  
// 					  Toast.makeText(Join6.this, "文件長度:" + (int) file.length(), Toast.LENGTH_SHORT).show();
// 					  Toast.makeText(Join6.this, "文件名稱:" + (String) file.getName(), Toast.LENGTH_SHORT).show();		  
// 					  Toast.makeText(Join6.this, "check1", Toast.LENGTH_SHORT).show();
 					  
 					  byte[] buf = new byte[65536];
 					  int num = fos.read(buf);
 					  
// 					  Toast.makeText(VoiceTest1.this, "傳送文件中:" + (String) file.getName(), Toast.LENGTH_SHORT).show();
 					  	
 					  while (num != ( - 1)) { //是否讀完文件
 						  
 						  doc.write(buf, 0, num); //把文件資料寫出網絡緩衝區
 						  
 						  doc.flush(); //重整緩衝區把資料寫往客戶端
 						  
 						  num = fos.read(buf); //繼續從文件中讀取資料
 						  
 					  }
 					  
 					  Thread.sleep(1000);
 					  String strnull = "";
 					  out1.println(strnull);
 					  
 					  
 					  
// 					  Toast.makeText(Join6.this, "傳送文件結束:" + (String) file.getName(), Toast.LENGTH_SHORT).show();
 					  
 					  fos.close();
// 					  doc.close();
// 					  client.close();
 					      
 					  
 				  }
 						
 				  catch (Exception ex) {
 					  
 					  ex.printStackTrace();
 					  
 				  }
 						
 				  finally {
 					  
 				  }
 				  
 				  
 		    }
 		    
 		    
 		public void vote() throws UnsupportedEncodingException
 		{
 			act = act + invitedfri[0].getText() + symbolv + threadnum + symbolv + symbol2;
    		act = act + user + symbol2;
    		for(int t=0;t<timenum;t++)
			{
				if(timechioce[t].isChecked()) // 因為是單選Radio button，所以只會有一個被按
				{
					act = act + timechioce[t].getText().toString().substring(2) + symbol4;
					ct = timechioce[t].getText().toString();
				}
			}
    		
    		String enact = java.net.URLEncoder.encode(act , "UTF-8");
			Main.out.println(enact);     //傳答應邀請的字串給Server
			//ttsService.speak("馬上為您回傳投票結果",1);
			Join10.this.finish();
 			
 		}
 		    
 		   
    	
        //中文語音 聲明 ServiceConnection
        private ServiceConnection connection = new ServiceConnection() { 
    		
    		public void onServiceConnected(ComponentName className, IBinder iservice) { 
    			
    			ttsService = ITts.Stub.asInterface(iservice); 
    			ttsBound = true; 
    			

    			try { 
    				ttsService.initialize(); 
    			} 
    			catch (RemoteException e) { 
    			} 
    		} 
    		
    		public void onServiceDisconnected(ComponentName arg0) { 
    			ttsService = null; 
    			ttsBound = false; 
    		} 
    	};
    	
    	//處理bindService 及 unbindService
    	@Override 
    	protected void onStart() { 
    		super.onStart(); 
    		if (!ttsBound ) { 
    			String actionName = "com.shoushuo.android.tts.intent.action.InvokeTts"; 
    			Intent intent = new Intent(actionName); 
    			this.bindService(intent, connection, Context.BIND_AUTO_CREATE); 
    		} 
    		
    	}
    	
    	private TextToSpeech.OnInitListener ttsInitListener = new TextToSpeech.OnInitListener()
        {

        
          public void onInit(int status)
          {
            
            /* 使用美國時區目前不支援中文 */
            Locale loc = new Locale("us", "", "");
            /* 檢查是否支援輸入的時區 */
            if (tts.isLanguageAvailable(loc) == TextToSpeech.LANG_AVAILABLE)
            {
              /* 設定語言 */
              tts.setLanguage(loc);
            }
            tts.setOnUtteranceCompletedListener(ttsUtteranceCompletedListener);
          }

        };
    	private TextToSpeech.OnUtteranceCompletedListener ttsUtteranceCompletedListener = new TextToSpeech.OnUtteranceCompletedListener()
        {
          
          public void onUtteranceCompleted(String utteranceId)
          {
            
          }
        };
    	@Override
    	protected void onDestroy () { 
    		/* 釋放TextToSpeech的資源 */
    	    tts.shutdown();    	      
    	   
    		if (ttsBound ) { 
    			ttsBound = false; 
    			this.unbindService(connection); 
    		} 
    		super. onDestroy (); 
    		
    	}
    	
 /*sensor 處理區*/
    	
        private final SensorEventListener mSensorEventListener = new SensorEventListener(){
        	
    		public void onAccuracyChanged(Sensor sensor, int accuracy) {
    			// TODO Auto-generated method stub
    			
    		}
    		public void onSensorChanged(SensorEvent event) {
    			// TODO Auto-generated method stub
    			if(event.values[0] == 0&&sensor_flag==true){
   		
    				recorderhandler();
    				sensor_flag=false;
    				
    			}
    		
    				
    			
    			//Toast.makeText(pr0oSensorTest.this,"proximity sensor accu ", Toast.LENGTH_SHORT).show();
    		}
        };
    
    	
    	class tts_thread implements Runnable{
    		public void ttsspeak(String a) throws RemoteException
    		{
    			
    			ttsService.speak(a, 1);
    			
    		}
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					ttsspeak(ttsString1);
					Thread.sleep(2700);
					tts.speak(ttsString2, TextToSpeech.QUEUE_FLUSH, null);
					Thread.sleep(1500);
					ttsspeak(ttsString3);
					Thread.sleep(2000);
		
	
					
					String[] temp=ttsString4.split(" ");
					for(int i=0;i<counter;i++)
					{
					tts.speak(temp[i], TextToSpeech.QUEUE_ADD, null);
					Thread.sleep(2000);
					}
					
					ttsspeak(ttsString5);
					if(ttsString6.length()!=0){
						Thread.sleep(1000);
						ttsspeak("備住");
						Thread.sleep(3000);
						ttsspeak(ttsString6);
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ttsspeak("請選擇您有空的時間");
					sensor_flag=true;
				
					

				
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    		
    	}
    	
    	
    	
    		    
    	
}
