package com.join;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.join.Join10.tts_thread;
import com.shoushuo.android.tts.ITts;

public class Result extends Activity {
    /** Called when the activity is first created. */
	private TextView  joinfri[]; // �ŧi�t�d�x�sTextView reference���@���}�C
	private TextView timechoice[];
	private TextView timeresult[];
	private TextView tool[];
	private TextView activity_destination;
	private TextView activity_type;
    private ImageView activity_image;
    private Button button_ok;
    
	private int timenum=0;
	private int t;
	private int tt;
	private int votenum[];
	private int maxt=0;
	private int finalmax=0;
	private String threadnum;
	private String symbol1="@g@";
	private String symbol2="@f@";
	private String symbol3="@d@";
	private String symbol4="@t@";
	private String symbol5="@v@";
	private String reaction;
	private String action;
//	private String action="ActivityResult:SOGO@g@kapin@f@rebaca@f@myself@f@Lollipops�s��SOGO�ʳf3F@d@11�I51��@t@12�I51��@t@1@v@2@v@";
	private String finaltime;
	private TableLayout layoutfri;
	private TableLayout layouttime; 
	private TableRow[] row; // �ظm�϶������C��
	
	/*����y��*/
	private Thread tts1;
	private ITts ttsService; 
	private boolean ttsBound;
	private String ttsString1 = "";
	private String ttsString2 = "";
	private String ttsString3 = "";
	private String ttsString4 = "";
	private String ttsString5 = "";
	/*�Ҧ��ܼ�*/
	private int mode = 0;
	private AlertDialog diolog;
	
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        
        Bundle bundleStr = this.getIntent().getExtras();
    	reaction = bundleStr.getString("Key_Str");
    	mode=bundleStr.getInt("mode");
    	try {
			action = java.net.URLDecoder.decode(reaction, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        findViews();
        showlayout();
        setListeners();
        
    	
        
       	
		if(mode==1)
		{
    		tts1=new Thread(new tts_thread());
    		tts1.start();

		}
    }



	private void findViews() {
		layoutfri = (TableLayout)findViewById(R.id.Result_frilayout);
		layouttime = (TableLayout)findViewById(R.id.Result_timelayout);
		activity_type = (TextView) findViewById(R.id.Result_group_type);
		activity_image = (ImageView) findViewById(R.id.Result_activity_image);
		activity_destination = (TextView) findViewById(R.id.Result_destination);
		button_ok = (Button) findViewById(R.id.joinr_ok);
		
		
		joinfri = new TextView[20];
		for(int i=0;i<20;i++){  
        	
			joinfri[i] = new TextView(this);    
			joinfri[i].setTextColor(Color.RED);
        }
		row = new TableRow[10];
		tool = new TextView[10];
		timechoice = new TextView[10];
		timeresult = new TextView[10];
		votenum = new int[10];
		for(int i=0;i<10;i++){  
			timechoice[i] = new TextView(this);   
			timeresult[i] = new TextView(this); 
			tool[i] = new TextView(this); 
			timechoice[i].setTextColor(Color.RED);
			timeresult[i].setTextColor(Color.BLUE);
			row[i] = new TableRow(this);    
			votenum[i] = 0;
		}
		
		 
	}
	
	private void showlayout() {
		
    	int loc1 = 0;               // �ŧi���}�Ÿ��b�r�ꪺ��m�ܼ�
		int loc2 = 0;
		int loc3 = 0;
		int loc4 = 0;
		int loc5 = 0;
		
		//----���Φr��----
        action=action.substring(15); // ���N���Y�h��
        loc1 = action.indexOf(symbol1,0);   // �M��@g@�X�{��index number
		activity_type.setText(action.substring(0,loc1)); // �s�Jgroup type
		loc1 = loc1 + symbol1.length();
		action = action.substring(loc1);
		
		int j=0;
		while(loc2 >= 0)
		{
			loc2 = action.indexOf(symbol2,0);   // �M��@f@�X�{��index number
			if (loc2 >= 0) {
				joinfri[j].setText(action.substring(0,loc2)); // �s�J���ܽЪ��B��
				layoutfri.addView(joinfri[j]);  // �N�ܽЪ��B�ͥ[�Jlayout
				loc2 = loc2 + symbol2.length();
				action = action.substring(loc2);
				j++;
			}
		} // end while
		
		loc3 = action.indexOf(symbol3,0);   // �M��@d@�X�{��index number
		activity_destination.setText(action.substring(0,loc3)); // �s�Jdestination
		loc3 = loc3 + symbol3.length();
		action = action.substring(loc3);
		
		t=0;
		while(loc4 >= 0)
		{
			loc4 = action.indexOf(symbol4,0);   // �M��@t@�X�{��index number
			if (loc4 >= 0) {
				timechoice[t].setText(action.substring(0,loc4)); // �s�J�ɶ��ﶵ
				loc4 = loc4 + symbol4.length();
				action = action.substring(loc4);
				timenum++;
				t++;
			}
		} // end while
		tt=0;
		while(loc5 >= 0)
		{
			loc5 = action.indexOf(symbol5,0);   // �M��@v@�X�{��index number
			if (loc5 >= 0) {
				timeresult[tt].setText(action.substring(0,loc5)+"��"); // �s�J�ɶ�����
				votenum[tt]= Integer.parseInt(action.substring(0,loc5));
				tool[tt].setText("\t");
				loc5 = loc5 + symbol5.length();
				action = action.substring(loc5);
				tt++;
			}
		} // end while
		
		for(int tl = 0;tl<timenum;tl++)
		{
			row[tl].addView(timechoice[tl]);
			row[tl].addView(tool[tl]);
			row[tl].addView(timeresult[tl]);
			layouttime.addView(row[tl]);
		}
		
		if(activity_type.getText().equals("Snack"))
		{
			activity_image.setImageDrawable(getResources().getDrawable(R.drawable.snack));
		}else if(activity_type.getText().equals("KTV"))
		{
			activity_image.setImageDrawable(getResources().getDrawable(R.drawable.ktv));
		}else if(activity_type.getText().equals("Shopping"))
		{
			activity_image.setImageDrawable(getResources().getDrawable(R.drawable.shopping));
		}
		
		if (tt > 0)   
		{   
			for (int i = 0; i < tt; i++)   
			{   
				if (votenum[i] > maxt)
			    {
			      	 maxt = votenum[i];  
			       	 finalmax = i;
 			    }
			}  
		}   
		
	
		
	}
	
	private void setListeners() {
		button_ok.setOnClickListener(buttonOK);
		
	}
	private Button.OnClickListener buttonOK = new Button.OnClickListener(){
    	public void onClick(View v){
    		new AlertDialog.Builder(Result.this)
    		.setTitle("����")
    		.setMessage("���ڭ̴N"+ timechoice[finalmax].getText() +"�b"+ activity_destination.getText() +"���o!" )
    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Result.this.finish();
				}}).show();
    		
    	}};
    	
    	public void voteresulthandler(){
    		voteresultHandler.sendEmptyMessage(0);
    	}
    	
    	private Handler voteresultHandler = new Handler() {
    		
    		@Override 
            public void handleMessage(Message msg) {
    			try {
					ttsService.speak("���ڭ̴N"+ timechoice[finalmax].getText() +"�b"+ activity_destination.getText() +"���o!" , 1);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			  diolog=	new AlertDialog.Builder(Result.this)
        		.setTitle("����")
        		.setMessage("���ڭ̴N"+ timechoice[finalmax].getText() +"�b"+ activity_destination.getText() +"���o!" )
        		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int which) {
    					Result.this.finish();
    				}}).show();
    			
    			 voteresultHandler.postDelayed(new Runnable(){

					public void run() {
						// TODO Auto-generated method stub
						diolog.dismiss();
						Result.this.finish();
						
					}
					
					
				},3000);
    			
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
					ttsService.speak("�벼���G��", 0);
					for(int i = 0; i < t ;i++){
						ttsspeak(timechoice[i].getText().toString());
						Thread.sleep(3000);
						ttsspeak(timeresult[i].getText().toString());
						Thread.sleep(3000);				
					}
					voteresulthandler();
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	}
    	protected void onDestroy () { 
    		
    		if (ttsBound ) { 
    			ttsBound = false; 
    			this.unbindService(connection); 
    		} 
    		super. onDestroy (); 
    		
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
    	
}
