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
	private TextView  invitedfri[]; // �ŧi�t�d�x�sTextView reference���@���}�C
	private RadioButton timechioce[];// �ŧi�t�d�x�sRadioButton reference���}�C(�C��group�����ݪ�RadioButton)
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
//	private String action="createActivity:SOGO@g@kapin@vt@1@vt@@f@rebaca@f@myself@f@Lollipops�s��SOGO�ʳf3F@d@11�I51��@t@12�I51��@t@let's go@m@";
	private String act = "vote:";
	private TableLayout layoutfri;
	private RadioGroup layouttime;
	// �y����X�һ��ܼ�
	public static String TAG = "DEBUG";
	private TextToSpeech tts;
	/*����y��*/
	private Thread tts1;
	private ITts ttsService; 
	private boolean ttsBound;
	private String ttsString1 = "";
	private String ttsString2 = "";
	private String ttsString3 = "";
	private String ttsString4 = "";
	private String ttsString5 = "";
	private String ttsString6 = "";
	/*�Ҧ��ܼ�*/
	private int mode = 1;
	/*��������*/
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
	 private String results;  // �ŧi�x�s���ѵ��G���r��  
	
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
		
    	int loc1 = 0;               // �ŧi���}�Ÿ��b�r�ꪺ��m�ܼ�
		int loc2 = 0;
		int loc3 = 0;
		int loc4 = 0;
		int loc5 = 0;
		int locv = 0;
		
		//----���Φr��----
        action=action.substring(15); // ���N���Y�h��
        loc1 = action.indexOf(symbol1,0);   // �M��@g@�X�{��index number
		activity_name.setText(action.substring(0,loc1)); // �s�Jgroup type
		loc1 = loc1 + symbol1.length();
		action = action.substring(loc1);
		if(mode == 1){
			ttsString1 = "���ܪ��s�լ� ";
			ttsString2 = activity_name.getText().toString()+"  ";
			ttsString3 ="���ܪ��B�ͦ� ";
		}
		int j=0;
		while(loc2 >= 0)
		{
			loc2 = action.indexOf(symbol2,0);   // �M��@f@�X�{��index number
			if (loc2 >= 0) {
				invitedfri[j].setText(action.substring(0,loc2)); // �s�J���ܽЪ��B��
				if(mode == 1&&j>0)
				{
					ttsString4 = ttsString4 + action.substring(0,loc2)+" ";
					counter++;
				}
				layoutfri.addView(invitedfri[j]);  // �N�ܽЪ��B�ͥ[�Jlayout
				loc2 = loc2 + symbol2.length();
				action = action.substring(loc2);
				
				if(j==0)
				{
					String vt = invitedfri[0].getText().toString();
					locv = vt.indexOf(symbolv,0);   // �M��@vt@�X�{��index number
					invitedfri[0].setText(vt.substring(0,locv)); // �s�Jgroup type
					ttsString4 = ttsString4 + vt.substring(0,locv)+" ";
					counter++;
					locv = locv + symbolv.length();
					vt = vt.substring(locv);
					
					locv = vt.indexOf(symbolv,0);   // �M��@vt@�X�{��index number
					threadnum = vt.substring(0,locv); // �Nthread�P�_���Ʀr�s�J
				}
				j++;
			}
		} // end while
		
		loc3 = action.indexOf(symbol3,0);   // �M��@d@�X�{��index number
		activity_destination.setText(action.substring(0,loc3)); // �s�Jdestination
		loc3 = loc3 + symbol3.length();
		action = action.substring(loc3);
		if(mode == 1){
			ttsString5 = "���ʦa�I�� ";
			ttsString5 = ttsString5 + activity_destination.getText().toString()+"  ";
			ttsString5 = ttsString5 + "�i��ܰѥ[���ɶ��� ";
		}
		int jj=0;
		char item = '1';
		while(loc4 >= 0)
		{
			loc4 = action.indexOf(symbol4,0);   // �M��@t@�X�{��index number
			if (loc4 >= 0) {
				timechioce[jj].setText(item+"."+action.substring(0,loc4)); // �s�J�ɶ����ﶵ
				if(mode == 1)
					ttsString5 = ttsString5 + item+" " + action.substring(0,loc4)+"  ";
				timechioce[jj].setTextColor(Color.RED);
				layouttime.addView(timechioce[jj]);  // �N�ɶ��ﶵ�[�Jlayout
				loc4 = loc4 + symbol4.length();
				action = action.substring(loc4);
				item++;
				timenum++;
				jj++;
			}
		} // end while
		chosen_num=jj;
		loc5 = action.indexOf(symbol5,0);   // �M��@m@�X�{��index number
		activity_memo.setText(action.substring(0,loc5)); // �s�Jdestination
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
    		//�ǰeact��Server(���ʵo�_��+��ܮɶ�)
    		
    		
    		
    		act = act + invitedfri[0].getText() + symbolv + threadnum + symbolv + symbol2;
    		act = act + user + symbol2;
    		for(int t=0;t<timenum;t++)
			{
				if(timechioce[t].isChecked()) // �]���O���Radio button�A�ҥH�u�|���@�ӳQ��
				{
					
					//Toast.makeText(Join10.this,timechioce[t].getText(), Toast.LENGTH_SHORT).show();
					act = act + timechioce[t].getText().toString().substring(2) + symbol4;
					ct = timechioce[t].getText().toString();
				}
			}
    		if (ct==null){ // �S�Įɶ�
				
				// ���ĵ�i���  
	    		new AlertDialog.Builder(Join10.this)
	    		.setTitle("�`�N")
	    		.setMessage("�п�ܮɶ�!")
	    		.setPositiveButton("�T�{", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();
				
			}else{
    		try {
				String enact = java.net.URLEncoder.encode(act , "UTF-8");
				//Toast.makeText(Join10.this,act, Toast.LENGTH_SHORT).show();
				Main.out.println(enact);     //�ǵ����ܽЪ��r�굹Server
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
    		
    		new AlertDialog.Builder(Join10.this)
    		.setTitle("����")
    		.setMessage("���W���z�^�ǧ벼���G!!")
    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Join10.this.finish();
				}}).show();
    		}
    		}};
    	private Button.OnClickListener buttonReject = new Button.OnClickListener(){
        	public void onClick(View v){
        		act = act + invitedfri[0].getText() + symbolv + threadnum + symbolv + symbol2;
        		Main.out.println(act);     //�ǩڵ��ܽЪ��r�굹Server
        		
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
    			
    			// �]�w�[��D�{��
    			m_recorder.setMainActivity(Join10.this);
    			
    			// ��l�ƿ�����
    			m_recorder.init();
    			// �}�l���������
    			m_recorder.start();
    			
    			// ��ܿ��Ѫ��i�פ��  
    			dialog1 = new ProgressDialog(Join10.this);
    			dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    			dialog1.setTitle("�y�����Ѫ��A");
    			dialog1.setMessage("������");
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
    			//	dialog2.dismiss(); // �N���A��ܥh����
    				String de_results = java.net.URLDecoder.decode(results, "UTF-8"); 
  		
    				if(de_results.compareTo("one")==0)
    				{
    					//Toast.makeText(Join10.this,"��ߨ�", Toast.LENGTH_SHORT).show();	
    					ttsService.speak("�z��ܪ��O�@  �нT�{",1);
    					chosen=0;
    					messageHandler.postDelayed(new Runnable(){

							public void run() {
								// TODO Auto-generated method stub
								recorderhandler();
							}
    						
    						
    					},5000);	
    				
    					// Toast.makeText(Join10.this,"��ߨ�", Toast.LENGTH_SHORT).show();	
    				    
    					//timechioce[0].setChecked(true);
    				}
    				else if(de_results.compareTo("two")==0)
    				{
    					ttsService.speak("�z��ܪ��O�G  �нT�{",1);
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
    					ttsService.speak("�z��ܪ��O�T  �нT�{",1);
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
    					ttsService.speak("�z��ܪ��O�|  �нT�{",1);
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
    					ttsService.speak("�z��ܪ��O��  �нT�{",1);
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
    						ttsService.speak("�L���ﶵ  �ЦA�����",1);
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
    						ttsService.speak("�@  �w���",1);
    						timechioce[chosen].setChecked(true);
        					vote();
    					}
    					if(chosen==1)
    					{
    						ttsService.speak("�G  �w���",1);			
    						timechioce[chosen].setChecked(true);
        					vote();
    					}
    					if(chosen==2)
    					{
    						ttsService.speak("�T  �w���",1);
    						timechioce[chosen].setChecked(true);
        					vote();
    					}
    					if(chosen==3)
    					{
    						ttsService.speak("�|  �w���",1);
    						timechioce[chosen].setChecked(true);
        					vote();
    					}
    					if(chosen==4)
    					{
    						ttsService.speak("��  �w���",1);
    						timechioce[chosen].setChecked(true);
        					vote();
    					}
    					if(chosen==-1)
    					{

    						ttsService.speak("�z�|����ܮɶ�",1);
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
    					ttsService.speak("�п�ܱz���Ū��ɶ�",1);
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
    					ttsService.speak("�L�k��{",1);
    					
    					messageHandler.postDelayed(new Runnable(){

							public void run() {
								// TODO Auto-generated method stub
								recorderhandler();
							}
    						
    						
    					},5000);	

    					
    				}
    					
    				    				dialog2.dismiss(); // �N���A��ܥh����
    				
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
 				
// 				dialog1.setMessage("���ѻy����"); // �w�s�n�y���ɡA�G�N���A����ܦ�"�y�����Ѥ�"
 				
 				
 				dialog2 = new ProgressDialog(Join10.this);
 				dialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
 				dialog2.setMessage("���ѻy����"); // �w�s�n�y���ɡA�G�N���A����ܦ�"�y�����Ѥ�"			
 				dialog2.show();
 				dialog2.incrementProgressBy(30);
 				dialog1.dismiss(); // �N���A��ܥh����
 				
 				
 				bulidsocket(); // �إ߻P�y����Oserver���s�u�A�ëإ߶ǰe����thread
 				
 				//���ݯu���s�n�y�����
 				while(m_recorder.getrunning()){
 					
 				}
 				
 				
 				sendFile(savefile); // �}�l�ǰe�y���ɮ�
 				m_recorder = null;
 							
 			}
 			
 		};
 		
 		
 		 public void bulidsocket(){
 		    	
 		    	try {
 		    		
 		    		// �إ߸�Ƴs�u
 		    		voice_client = new Socket("140.113.144.48",8082); //�W�[���������y�����A�Ⱦ������
 		  		  	voice_in = new BufferedReader (new InputStreamReader(voice_client.getInputStream()));    // �إ�Client socket input stream
 			        voice_out = new PrintWriter (voice_client.getOutputStream(), true);                      // �إ�Client socket output stream
 			        
 			        txrx = new Voice_TxRxThread_join10(voice_in);
 			        txrx.setMessageObserver(this);
 			        txrx.start();
 		  		  	
 		  		  	
 		    	}
 		    	catch (Exception ex) {
 					  
 					  ex.printStackTrace();
 					  
 				  }
 		    	
 		    }
 		 
 		 /** ������thread�i������s�u��handler*/
			public void overSockethandler(){
				overSocket.sendEmptyMessage(0);
			}
			private Handler overSocket = new Handler() {
				
				@Override 
		        public void handleMessage(Message msg) {
					
					try {
						
						// ���_�s�u�������檺�ʧ@
//			    		out.println("BYE"); // �Vserver�n�D�n���_�s�u
			    		voice_client.close(); // ����Socket
			    		
			    		
			    	}catch(IOException e){
						
					}
					
				}
				
			};   
 			
 		    /** �ǰe�y���ɪ��ŧimethod*/
 		    public void sendFile(String fileName) { //�ϥΥ��a���t�α���������ƨæs���s���
 		    	voice_out.println(user);
 		    		
 				  if (fileName == null) return; //�W�[���y�Ψ�Ū����󤤪����
 					
 				  File file = new File(fileName);
 					
 						
 				  try {
 					  
 					  PrintWriter out1 = new PrintWriter (voice_client.getOutputStream(), true);
 					  OutputStream doc = new DataOutputStream(new BufferedOutputStream(voice_client.getOutputStream())); //�W�[���Ū���w�İ�
 					  
 					  FileInputStream fos = new FileInputStream(file); //�W�[�����A�Ⱦ������Ȥ�ШD
 					  
// 					  Toast.makeText(Join6.this, "������:" + (int) file.length(), Toast.LENGTH_SHORT).show();
// 					  Toast.makeText(Join6.this, "���W��:" + (String) file.getName(), Toast.LENGTH_SHORT).show();		  
// 					  Toast.makeText(Join6.this, "check1", Toast.LENGTH_SHORT).show();
 					  
 					  byte[] buf = new byte[65536];
 					  int num = fos.read(buf);
 					  
// 					  Toast.makeText(VoiceTest1.this, "�ǰe���:" + (String) file.getName(), Toast.LENGTH_SHORT).show();
 					  	
 					  while (num != ( - 1)) { //�O�_Ū�����
 						  
 						  doc.write(buf, 0, num); //�����Ƽg�X�����w�İ�
 						  
 						  doc.flush(); //����w�İϧ��Ƽg���Ȥ��
 						  
 						  num = fos.read(buf); //�~��q���Ū�����
 						  
 					  }
 					  
 					  Thread.sleep(1000);
 					  String strnull = "";
 					  out1.println(strnull);
 					  
 					  
 					  
// 					  Toast.makeText(Join6.this, "�ǰe��󵲧�:" + (String) file.getName(), Toast.LENGTH_SHORT).show();
 					  
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
				if(timechioce[t].isChecked()) // �]���O���Radio button�A�ҥH�u�|���@�ӳQ��
				{
					act = act + timechioce[t].getText().toString().substring(2) + symbol4;
					ct = timechioce[t].getText().toString();
				}
			}
    		
    		String enact = java.net.URLEncoder.encode(act , "UTF-8");
			Main.out.println(enact);     //�ǵ����ܽЪ��r�굹Server
			//ttsService.speak("���W���z�^�ǧ벼���G",1);
			Join10.this.finish();
 			
 		}
 		    
 		   
    	
        //����y�� �n�� ServiceConnection
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
    	
    	//�B�zbindService �� unbindService
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
            
            /* �ϥά���ɰϥثe���䴩���� */
            Locale loc = new Locale("us", "", "");
            /* �ˬd�O�_�䴩��J���ɰ� */
            if (tts.isLanguageAvailable(loc) == TextToSpeech.LANG_AVAILABLE)
            {
              /* �]�w�y�� */
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
    		/* ����TextToSpeech���귽 */
    	    tts.shutdown();    	      
    	   
    		if (ttsBound ) { 
    			ttsBound = false; 
    			this.unbindService(connection); 
    		} 
    		super. onDestroy (); 
    		
    	}
    	
 /*sensor �B�z��*/
    	
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
						ttsspeak("�Ʀ�");
						Thread.sleep(3000);
						ttsspeak(ttsString6);
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ttsspeak("�п�ܱz���Ū��ɶ�");
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
