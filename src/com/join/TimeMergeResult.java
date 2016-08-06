package com.join;

import java.io.*;
import java.net.Socket;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimeMergeResult extends Activity {

	private String symbol = "&";
	private String act = "createActivity:";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_merge_result);

		findViews();
		BundleResults();
	}

	private CheckBox timeCheckBox[];

	private TableLayout layout;
	private LinearLayout timelayout;
	private TableRow row[];
	private ImageView image;
	private TextView text[];
	private TextView textTitle;
	private TextView textfriend[];
	private TextView textdestination;
	private EditText timeEdit[];
	private EditText memoEdit;
	private Button buttonTime;
	private Button audiobuttonTime; // 宣告語音辨認button 參照
	private Button deleteTime;
	private Button buttonSend;
	private Button buttonCancel;
	private int timeChoice = 0;
	private int f = 0;
	private int Hour;
	private int Minute;
	private String myID;

	public static Socket voice_client;
	public static BufferedReader voice_in;
	public static PrintWriter voice_out;
	private String results; // 宣告儲存辨識結果的字串

	// 客戶端傳送接收處理執行緒
	private Voice_TxRxThread txrx;

	public ProgressDialog dialog1;
	public ProgressDialog dialog2;

	public String savefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temprecord/test.pcm";

	// 宣告錄音緒的reference
	protected Saudioclient m_recorder;

	private void findViews() {

		layout = (TableLayout) findViewById(R.id.CreateactiveLayout);
		timelayout = new LinearLayout(this);
		row = new TableRow[30];
		image = new ImageView(this);
		text = new TextView[4];
		textTitle = new TextView(this);
		textfriend = new TextView[20];
		textdestination = new TextView(this);
		timeEdit = new EditText[10];
		memoEdit = new EditText(this);
		buttonSend = new Button(this);
		buttonCancel = new Button(this);
		buttonTime = new Button(this);
		audiobuttonTime = new Button(this); // 建置執行語音辨認的按鈕物件
		deleteTime = new Button(this);

		// 取得現在時間
		Calendar c = Calendar.getInstance();
		Hour = c.get(Calendar.HOUR_OF_DAY);
		Minute = c.get(Calendar.MINUTE);

		// 宣告GUI row行數
		for (int i = 0; i < 30; i++) {
			row[i] = new TableRow(this);
		}

		// 宣告GUI text
		for (int i = 0; i < 4; i++) {
			text[i] = new TextView(this);
		}

		// 宣告存放邀請的朋友
		for (int i = 0; i < 20; i++) {
			textfriend[i] = new TextView(this);
			textfriend[i].setTextColor(Color.RED);
			textfriend[i].setTextSize(20);
		}

		// 宣告存放決定地點顯示的顏色
		textdestination.setTextColor(Color.RED);
		textdestination.setTextSize(20);

		// 宣告存放時間選擇的EditText
		for (int i = 0; i < 10; i++) {

			timeEdit[i] = new EditText(this);

		}

		// ----各個物件的內容與設定----
		timelayout.setOrientation(LinearLayout.VERTICAL);
		textTitle.setTextSize(25);
		textTitle.setTextColor(Color.BLACK);
		text[0].setText("被邀請者:");
		text[1].setText("目的地:");
		text[2].setText("時間:");
		text[3].setText("備註:");

		for (int i = 0; i < text.length; i++) {
			text[i].setTextSize(20);
			text[i].setTextColor(Color.BLACK);
		}
		buttonSend.setText("送出");
		buttonCancel.setText("取消");

		buttonTime.setText("新增時間");
		audiobuttonTime.setText("語音辨認"); // 語音辨認按鈕
		deleteTime.setText("刪除時間");

		// ----button頃聽者
		buttonSend.setOnClickListener(sendButton);
		buttonCancel.setOnClickListener(cancelButton);
		buttonTime.setOnClickListener(newTime);
		audiobuttonTime.setOnClickListener(recordstart); // 語音辨認按鈕傾聽者
		deleteTime.setOnClickListener(deTime);

	}

	private void BundleResults() {
		// ----Join5傳來之bundle
		Bundle bundle = this.getIntent().getExtras();
		String activity_title = bundle.getString("Key_title");
		String invited_friend = bundle.getString("Key_invitedfriend");
		String destination = bundle.getString("Key_destination");
		myID = bundle.getString("Key_myID");

		// add by Lawrence--2012/11/04
		int myChooseYear = bundle.getInt("myChooseYear");
		int myChooseMonth = bundle.getInt("myChooseMonth");
		int myChooseDay = bundle.getInt("myChooseDay");

		// --------------判斷要用哪個圖--------------------
		if (activity_title.equals("Snack")) {
			image.setImageDrawable(getResources().getDrawable(R.drawable.snack));
		} else if (activity_title.equals("KTV")) {
			image.setImageDrawable(getResources().getDrawable(R.drawable.ktv));
		} else if (activity_title.equals("Shopping")) {
			image.setImageDrawable(getResources().getDrawable(R.drawable.shopping));
		}

		// ----------填入資訊-----------
		textTitle.setText(activity_title);

		row[0].addView(image);
		row[0].addView(textTitle);
		layout.addView(row[0]);

		row[1].addView(text[0]);
		layout.addView(row[1]);

		// ----被邀請人名單切割----
		int loc0 = 0;
		invited_friend = invited_friend.substring(11);
		while (loc0 >= 0) {
			loc0 = invited_friend.indexOf(symbol, 0); // 尋找@出現的index number
			if (loc0 >= 0) {
				textfriend[f].setText(invited_friend.substring(0, loc0));
				row[2 + f].addView(textfriend[f]);
				layout.addView(row[2 + f]);
				loc0 = loc0 + symbol.length();
				invited_friend = invited_friend.substring(loc0);
				f++;
			}
		} // end while

		textdestination.setText(destination);
		row[2 + f].addView(text[1]);
		layout.addView(row[2 + f]);
		layout.addView(textdestination);

		row[3 + f].addView(text[2]);
		layout.addView(row[3 + f]);

		// add by Lawrence--2012/11/04
		TextView myChooseDate = new TextView(this);
		myChooseDate.setTextColor(Color.BLUE);
		myChooseDate.setTextSize(20);
		myChooseDate.setText(myChooseYear + "年" + myChooseMonth + "月" + myChooseDay + "日");
		row[4 + f].addView(myChooseDate);
		layout.addView(row[4 + f]);

		// modify by Lawrence--2012/10/07
		timeCheckBox = new CheckBox[20];

		for (int i = 0; i < timeCheckBox.length; i++) {
			timeCheckBox[i] = new CheckBox(this);
			timeCheckBox[i].setTextColor(Color.BLUE);
			timeCheckBox[i].setTextSize(20);
		}

		String[] timeResult = new String[22];
		int x = 0;
		for (int i = 0; i < 22; i++) {
			int time = 13 + x;
			timeResult[i] = time + ":00~" + time + ":30";
			System.out.println("index=" + i + "/timeResult=" + timeResult[i]);

			x++;
			i++;
		}
		x = 0;
		for (int i = 1; i <= 22; i++) {
			int time = 13 + x;
			int time2 = 14 + x;

			timeResult[i] = time + ":30~" + time2 + ":00";
			System.out.println("index=" + i + "/timeResult=" + timeResult[i]);

			x++;
			i++;
		}

		Random r = new Random();
		// 隨機選取準備好的時間陣列，不得重複且需由小到大排序
		Set<Integer> randomSet = new TreeSet<Integer>();

		for (int i = 0; i < 10; i++) {
			int randomInt = r.nextInt(21);
			randomSet.add(randomInt);
			System.out.println("setSize=" + randomSet.size());
		}

		Iterator<Integer> randomIterator = randomSet.iterator();
		int ii = 0;
		while (randomIterator.hasNext()) {
			timeCheckBox[ii].setText(timeResult[randomIterator.next()]);
			ii++;
		}

		for (int i = 0; i < 5; i++) {
			row[5 + f + i].addView(timeCheckBox[i]);
			layout.addView(row[5 + f + i]);
		}

		row[10 + f].addView(text[3]);
		layout.addView(row[10 + f]);

		row[11 + f].addView(memoEdit);
		layout.addView(row[11 + f]);

		row[12 + f].addView(buttonSend);
		row[13 + f].addView(buttonCancel);
		layout.addView(row[12 + f]);
		layout.addView(row[13 + f]);

		// -----填入資訊完畢-------
		// Toast.makeText(Jointest.this,"2", Toast.LENGTH_SHORT).show();
	}

	/** 新增時間按鈕的傾聽者 */
	private Button.OnClickListener newTime = new Button.OnClickListener() {

		public void onClick(View v) {
			new TimePickerDialog(TimeMergeResult.this,

			new TimePickerDialog.OnTimeSetListener() {

				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

					Hour = hourOfDay; // 將設定的小時儲存
					Minute = minute; // 將設定的分鐘儲存

					timelayout.addView(timeEdit[timeChoice]); // 增加EditText到layout上
					timeEdit[timeChoice].setText(chtHour(Hour) + "點" + chtMinute(Minute) + "分"); // 設定EditText的內容
					timeChoice++;

				}

			} // 建置時間選擇傾聽物件 (傳入的參照是使用者設定的時間)

					, Hour, Minute, true).show(); // 顯示時間選擇視窗

		}

	};

	/** 刪除時間按鈕的傾聽者 */
	private Button.OnClickListener deTime = new Button.OnClickListener() {

		public void onClick(View v) {
			if (timeChoice > 0) {

				timelayout.removeView(timeEdit[timeChoice - 1]); // 將layout上的時間EditText刪除
				timeChoice--;

			}

		}
	};

	/** 語音辨認新增時間的按鈕傾聽者 */
	private Button.OnClickListener recordstart = new Button.OnClickListener() {
		public void onClick(View v) {

			// 建置錄音緒之物件
			m_recorder = new Saudioclient();

			// 設定觀察的主程式
			m_recorder.setMainActivity(TimeMergeResult.this);

			// 初始化錄音緒
			m_recorder.init();

			// 開始執行錄音緒
			m_recorder.start();

			// 顯示辨識的進度方塊
			dialog1 = new ProgressDialog(TimeMergeResult.this);
			dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog1.setTitle("語音辨識狀態");
			dialog1.setMessage("錄音中");
			dialog1.setIndeterminate(true);
			dialog1.setCancelable(true);
			dialog1.show();

		}

	};

	/** 此為用thread可控制建立socket的handler(並將語音檔傳到server) */
	public void bulidsockethandler() {
		socketHandler.sendEmptyMessage(0);
	}

	private Handler socketHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// dialog1.setMessage("辨識語音中"); // 已存好語音檔，故將狀態顯示變成"語音辨識中"

			dialog2 = new ProgressDialog(TimeMergeResult.this);
			dialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog2.setMessage("辨識語音中"); // 已存好語音檔，故將狀態顯示變成"語音辨識中"
			dialog2.show();
			dialog2.incrementProgressBy(30);

			dialog1.dismiss(); // 將狀態顯示去除掉

			bulidsocket(); // 建立與語音辨是server的連線，並建立傳送接收thread

			// 等待真正存好語音資料
			while (m_recorder.getrunning()) {

			}

			sendFile(savefile); // 開始傳送語音檔案
			m_recorder = null;

		}

	};

	/** 此為用thread可控制結束連線的handler */
	public void overSockethandler() {
		overSocket.sendEmptyMessage(0);
	}

	private Handler overSocket = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			try {

				// 中斷連線必須執行的動作
				// out.println("BYE"); // 向server要求要中斷連線
				voice_client.close(); // 關閉Socket

			} catch (IOException e) {

			}

		}

	};

	/** 此為用thread可控制處理辨識結果的handler */
	public void goDataUpdatehandler() {
		messageHandler.sendEmptyMessage(0);
	}

	private Handler messageHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			try {

				dialog2.incrementProgressBy(40);
				results = txrx.getUserData();

				String de_results = java.net.URLDecoder.decode(results, "UTF-8");

				// String[] divi_results = de_results.split(","); //
				// 將字串切割，存到array

				/** 在此執行顯示辨識結果的動作 */

				// //將top5的時間存到下拉式選單內
				// adapter = new
				// ArrayAdapter<String>(VoiceTest1.this,android.R.layout.simple_spinner_item,divi_results);
				// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// spinner.setAdapter(adapter);
				//
				// // 將新增時間按鈕設置為不能按(因為尚未回傳正確結果)
				// recordbutton.setClickable(false);

				// 將辨認結果新增並顯示在layout上
				timelayout.addView(timeEdit[timeChoice]); // 增加EditText到layout上
				timeEdit[timeChoice].setText(de_results); // 設定EditText的內容
				timeChoice++;

				voice_out.println("");

				// dialog1.dismiss(); // 將狀態顯示去除掉
				dialog2.dismiss(); // 將狀態顯示去除掉

			} catch (Exception e) {

				e.printStackTrace();

			}

		}
	};

	/** 建立Socket的宣告method */
	public void bulidsocket() {

		try {

			// 建立資料連線
			voice_client = new Socket("210.71.220.151", 8081); // 增加網絡接受流接受服務器文件資料
			voice_in = new BufferedReader(new InputStreamReader(voice_client.getInputStream())); // 建立Client
																									// socket
																									// input
																									// stream
			voice_out = new PrintWriter(voice_client.getOutputStream(), true); // 建立Client
																				// socket
																				// output
																				// stream

			txrx = new Voice_TxRxThread(voice_in);
			txrx.setMessageObserver(this);
			txrx.start();

		} catch (Exception ex) {

			ex.printStackTrace();

		}

	}

	/** 傳送語音檔的宣告method */
	public void sendFile(String fileName) { // 使用本地文件系統接受網絡資料並存為新文件

		if (fileName == null)
			return; // 增加文件流用來讀取文件中的資料

		File file = new File(fileName);

		try {

			PrintWriter out1 = new PrintWriter(voice_client.getOutputStream(), true);
			OutputStream doc = new DataOutputStream(new BufferedOutputStream(voice_client.getOutputStream())); // 增加文件讀取緩衝區

			FileInputStream fos = new FileInputStream(file); // 增加網絡服務器接受客戶請求

			// Toast.makeText(Join6.this, "文件長度:" + (int) file.length(),
			// Toast.LENGTH_SHORT).show();
			// Toast.makeText(Join6.this, "文件名稱:" + (String) file.getName(),
			// Toast.LENGTH_SHORT).show();
			// Toast.makeText(Join6.this, "check1", Toast.LENGTH_SHORT).show();

			byte[] buf = new byte[65536];
			int num = fos.read(buf);

			// Toast.makeText(VoiceTest1.this, "傳送文件中:" + (String)
			// file.getName(), Toast.LENGTH_SHORT).show();

			while (num != (-1)) { // 是否讀完文件

				doc.write(buf, 0, num); // 把文件資料寫出網絡緩衝區

				doc.flush(); // 重整緩衝區把資料寫往客戶端

				num = fos.read(buf); // 繼續從文件中讀取資料

			}

			Thread.sleep(1000);
			String strnull = "";
			out1.println(strnull);

			// Toast.makeText(Join6.this, "傳送文件結束:" + (String) file.getName(),
			// Toast.LENGTH_SHORT).show();

			fos.close();
			// doc.close();
			// client.close();

		}

		catch (Exception ex) {

			ex.printStackTrace();

		}

		finally {

		}

	}

	// ---------回到Join5-------------
	private Button.OnClickListener cancelButton = new Button.OnClickListener() {
		public void onClick(View v) {
			TimeMergeResult.this.finish();
		}
	};

	// ----傳送字串給Server、關閉Join5----
	private Button.OnClickListener sendButton = new Button.OnClickListener() {

		public void onClick(View v) {
			act += textTitle.getText() + "@g@";
			act += myID + "@f@";
			for (int invitedf = 0; invitedf < f; invitedf++) {
				act += textfriend[invitedf].getText() + "@f@";
			}
			act += textdestination.getText() + "@d@";
			// for (int t = 0; t < timeChoice; t++) {
			// act += timeEdit[t].getText() + "@t@";
			// }

			// modify by Lawrence--2012/10/07
			for (int i = 0; i < timeCheckBox.length; i++) {
				if (timeCheckBox[i].isChecked())
					act += timeCheckBox[i].getText() + "@t@";
			}
			act += memoEdit.getText() + "@m@";

			Log.e("TimeMergeResult", "act=" + act);
			try {
				String enact = java.net.URLEncoder.encode(act, "UTF-8");
				Main.out.println(enact);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Intent intent = new Intent();
			TimeMergeResult.this.setResult(RESULT_OK, intent);
			TimeMergeResult.this.finish();
		}
	};

	/** 將小時整數變成中文字串 */
	public String chtHour(int h) {

		int hour = h; // 宣告傳入的阿拉伯數字正數
		String reHour = ""; // 宣告最後回傳的中文字串

		if ((0 <= hour) && (hour < 10)) { // hour=0~9

			switch (hour) {

			case 0:
				reHour = "零";
				break;
			case 1:
				reHour = "一";
				break;
			case 2:
				reHour = "二";
				break;
			case 3:
				reHour = "三";
				break;
			case 4:
				reHour = "四";
				break;
			case 5:
				reHour = "五";
				break;
			case 6:
				reHour = "六";
				break;
			case 7:
				reHour = "七";
				break;
			case 8:
				reHour = "八";
				break;
			case 9:
				reHour = "九";
				break;

			}

		} else if ((10 <= hour) && (hour < 20)) { // hour=10~19

			String tens = "十"; // 宣告十位數中文字串
			int re = hour % 10; // 將hour除以10取餘數
			String units = ""; // 宣告個位數中文字串

			switch (re) {

			case 0:
				units = "零";
				break;
			case 1:
				units = "一";
				break;
			case 2:
				units = "二";
				break;
			case 3:
				units = "三";
				break;
			case 4:
				units = "四";
				break;
			case 5:
				units = "五";
				break;
			case 6:
				units = "六";
				break;
			case 7:
				units = "七";
				break;
			case 8:
				units = "八";
				break;
			case 9:
				units = "九";
				break;

			}

			if (re != 0) {
				reHour = tens + units; // 有餘數，回傳加上個位數的值
			} else {
				reHour = tens; // 剛好整除，回傳"十"
			}
		} else { // hour=20~24
			String twentys = "二十"; // 宣告十位數中文字串
			int re = hour % 20; // 將hour除以10取餘數
			String units = ""; // 宣告個位數中文字串

			switch (re) {

			case 0:
				units = "零";
				break;
			case 1:
				units = "一";
				break;
			case 2:
				units = "二";
				break;
			case 3:
				units = "三";
				break;
			case 4:
				units = "四";
				break;

			}

			if (re != 0) {
				reHour = twentys + units; // 有餘數，回傳加上個位數的值
			} else {
				reHour = twentys; // 剛好整除，回傳"二十"
			}
		}

		return reHour;
	}

	/** 將分鐘整數變成中文字串 */
	public String chtMinute(int m) {

		int minute = m;
		String reMinute = "";

		if ((0 <= minute) && (minute < 10)) { // minute=0~9

			switch (minute) {

			case 0:
				reMinute = "零";
				break;
			case 1:
				reMinute = "一";
				break;
			case 2:
				reMinute = "二";
				break;
			case 3:
				reMinute = "三";
				break;
			case 4:
				reMinute = "四";
				break;
			case 5:
				reMinute = "五";
				break;
			case 6:
				reMinute = "六";
				break;
			case 7:
				reMinute = "七";
				break;
			case 8:
				reMinute = "八";
				break;
			case 9:
				reMinute = "九";
				break;

			}

		} else if ((10 <= minute) && (minute < 20)) { // minute=10~19

			String tens = "十"; // 宣告十位數中文字串
			int re = minute % 10; // 將minute除以10取餘數
			String units = ""; // 宣告個位數中文字串

			switch (re) {

			case 0:
				units = "零";
				break;
			case 1:
				units = "一";
				break;
			case 2:
				units = "二";
				break;
			case 3:
				units = "三";
				break;
			case 4:
				units = "四";
				break;
			case 5:
				units = "五";
				break;
			case 6:
				units = "六";
				break;
			case 7:
				units = "七";
				break;
			case 8:
				units = "八";
				break;
			case 9:
				units = "九";
				break;

			}

			if (re != 0) {

				reMinute = tens + units; // 有餘數，回傳加上個位數的值

			} else {

				reMinute = tens; // 剛好整除，回傳"十"
			}

		} else if ((20 <= minute) && (minute < 30)) { // minute=20~29

			String tens = "二十"; // 宣告十位數中文字串
			int re = minute % 20; // 將minute除以20取餘數
			String units = ""; // 宣告個位數中文字串

			switch (re) {

			case 0:
				units = "零";
				break;
			case 1:
				units = "一";
				break;
			case 2:
				units = "二";
				break;
			case 3:
				units = "三";
				break;
			case 4:
				units = "四";
				break;
			case 5:
				units = "五";
				break;
			case 6:
				units = "六";
				break;
			case 7:
				units = "七";
				break;
			case 8:
				units = "八";
				break;
			case 9:
				units = "九";
				break;

			}

			if (re != 0) {

				reMinute = tens + units; // 有餘數，回傳加上個位數的值

			} else {

				reMinute = tens; // 剛好整除，回傳"二十"
			}

		} else if ((30 <= minute) && (minute < 40)) { // minute=30~39

			String tens = "三十"; // 宣告十位數中文字串
			int re = minute % 30; // 將minute除以20取餘數
			String units = ""; // 宣告個位數中文字串

			switch (re) {

			case 0:
				units = "零";
				break;
			case 1:
				units = "一";
				break;
			case 2:
				units = "二";
				break;
			case 3:
				units = "三";
				break;
			case 4:
				units = "四";
				break;
			case 5:
				units = "五";
				break;
			case 6:
				units = "六";
				break;
			case 7:
				units = "七";
				break;
			case 8:
				units = "八";
				break;
			case 9:
				units = "九";
				break;

			}

			if (re != 0) {

				reMinute = tens + units; // 有餘數，回傳加上個位數的值

			} else {

				reMinute = tens; // 剛好整除，回傳"三十"
			}

		} else if ((40 <= minute) && (minute < 50)) { // minute=40~49

			String tens = "四十"; // 宣告十位數中文字串
			int re = minute % 40; // 將minute除以20取餘數
			String units = ""; // 宣告個位數中文字串

			switch (re) {

			case 0:
				units = "零";
				break;
			case 1:
				units = "一";
				break;
			case 2:
				units = "二";
				break;
			case 3:
				units = "三";
				break;
			case 4:
				units = "四";
				break;
			case 5:
				units = "五";
				break;
			case 6:
				units = "六";
				break;
			case 7:
				units = "七";
				break;
			case 8:
				units = "八";
				break;
			case 9:
				units = "九";
				break;

			}

			if (re != 0) {

				reMinute = tens + units; // 有餘數，回傳加上個位數的值

			} else {

				reMinute = tens; // 剛好整除，回傳"四十"
			}

		} else if ((50 <= minute) && (minute < 60)) { // minute=50~59

			String tens = "五十"; // 宣告十位數中文字串
			int re = minute % 50; // 將minute除以20取餘數
			String units = ""; // 宣告個位數中文字串

			switch (re) {

			case 0:
				units = "零";
				break;
			case 1:
				units = "一";
				break;
			case 2:
				units = "二";
				break;
			case 3:
				units = "三";
				break;
			case 4:
				units = "四";
				break;
			case 5:
				units = "五";
				break;
			case 6:
				units = "六";
				break;
			case 7:
				units = "七";
				break;
			case 8:
				units = "八";
				break;
			case 9:
				units = "九";
				break;

			}

			if (re != 0) {

				reMinute = tens + units; // 有餘數，回傳加上個位數的值

			} else {

				reMinute = tens; // 剛好整除，回傳"五十"
			}

		}

		return reMinute;
	}

}
