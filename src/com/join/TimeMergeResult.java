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
	private Button audiobuttonTime; // �ŧi�y����{button �ѷ�
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
	private String results; // �ŧi�x�s���ѵ��G���r��

	// �Ȥ�ݶǰe�����B�z�����
	private Voice_TxRxThread txrx;

	public ProgressDialog dialog1;
	public ProgressDialog dialog2;

	public String savefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temprecord/test.pcm";

	// �ŧi��������reference
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
		audiobuttonTime = new Button(this); // �ظm����y����{�����s����
		deleteTime = new Button(this);

		// ���o�{�b�ɶ�
		Calendar c = Calendar.getInstance();
		Hour = c.get(Calendar.HOUR_OF_DAY);
		Minute = c.get(Calendar.MINUTE);

		// �ŧiGUI row���
		for (int i = 0; i < 30; i++) {
			row[i] = new TableRow(this);
		}

		// �ŧiGUI text
		for (int i = 0; i < 4; i++) {
			text[i] = new TextView(this);
		}

		// �ŧi�s���ܽЪ��B��
		for (int i = 0; i < 20; i++) {
			textfriend[i] = new TextView(this);
			textfriend[i].setTextColor(Color.RED);
			textfriend[i].setTextSize(20);
		}

		// �ŧi�s��M�w�a�I��ܪ��C��
		textdestination.setTextColor(Color.RED);
		textdestination.setTextSize(20);

		// �ŧi�s��ɶ���ܪ�EditText
		for (int i = 0; i < 10; i++) {

			timeEdit[i] = new EditText(this);

		}

		// ----�U�Ӫ��󪺤��e�P�]�w----
		timelayout.setOrientation(LinearLayout.VERTICAL);
		textTitle.setTextSize(25);
		textTitle.setTextColor(Color.BLACK);
		text[0].setText("�Q�ܽЪ�:");
		text[1].setText("�ت��a:");
		text[2].setText("�ɶ�:");
		text[3].setText("�Ƶ�:");

		for (int i = 0; i < text.length; i++) {
			text[i].setTextSize(20);
			text[i].setTextColor(Color.BLACK);
		}
		buttonSend.setText("�e�X");
		buttonCancel.setText("����");

		buttonTime.setText("�s�W�ɶ�");
		audiobuttonTime.setText("�y����{"); // �y����{���s
		deleteTime.setText("�R���ɶ�");

		// ----button��ť��
		buttonSend.setOnClickListener(sendButton);
		buttonCancel.setOnClickListener(cancelButton);
		buttonTime.setOnClickListener(newTime);
		audiobuttonTime.setOnClickListener(recordstart); // �y����{���s��ť��
		deleteTime.setOnClickListener(deTime);

	}

	private void BundleResults() {
		// ----Join5�ǨӤ�bundle
		Bundle bundle = this.getIntent().getExtras();
		String activity_title = bundle.getString("Key_title");
		String invited_friend = bundle.getString("Key_invitedfriend");
		String destination = bundle.getString("Key_destination");
		myID = bundle.getString("Key_myID");

		// add by Lawrence--2012/11/04
		int myChooseYear = bundle.getInt("myChooseYear");
		int myChooseMonth = bundle.getInt("myChooseMonth");
		int myChooseDay = bundle.getInt("myChooseDay");

		// --------------�P�_�n�έ��ӹ�--------------------
		if (activity_title.equals("Snack")) {
			image.setImageDrawable(getResources().getDrawable(R.drawable.snack));
		} else if (activity_title.equals("KTV")) {
			image.setImageDrawable(getResources().getDrawable(R.drawable.ktv));
		} else if (activity_title.equals("Shopping")) {
			image.setImageDrawable(getResources().getDrawable(R.drawable.shopping));
		}

		// ----------��J��T-----------
		textTitle.setText(activity_title);

		row[0].addView(image);
		row[0].addView(textTitle);
		layout.addView(row[0]);

		row[1].addView(text[0]);
		layout.addView(row[1]);

		// ----�Q�ܽФH�W�����----
		int loc0 = 0;
		invited_friend = invited_friend.substring(11);
		while (loc0 >= 0) {
			loc0 = invited_friend.indexOf(symbol, 0); // �M��@�X�{��index number
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
		myChooseDate.setText(myChooseYear + "�~" + myChooseMonth + "��" + myChooseDay + "��");
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
		// �H������ǳƦn���ɶ��}�C�A���o���ƥB�ݥѤp��j�Ƨ�
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

		// -----��J��T����-------
		// Toast.makeText(Jointest.this,"2", Toast.LENGTH_SHORT).show();
	}

	/** �s�W�ɶ����s����ť�� */
	private Button.OnClickListener newTime = new Button.OnClickListener() {

		public void onClick(View v) {
			new TimePickerDialog(TimeMergeResult.this,

			new TimePickerDialog.OnTimeSetListener() {

				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

					Hour = hourOfDay; // �N�]�w���p���x�s
					Minute = minute; // �N�]�w�������x�s

					timelayout.addView(timeEdit[timeChoice]); // �W�[EditText��layout�W
					timeEdit[timeChoice].setText(chtHour(Hour) + "�I" + chtMinute(Minute) + "��"); // �]�wEditText�����e
					timeChoice++;

				}

			} // �ظm�ɶ���ܶ�ť���� (�ǤJ���ѷӬO�ϥΪ̳]�w���ɶ�)

					, Hour, Minute, true).show(); // ��ܮɶ���ܵ���

		}

	};

	/** �R���ɶ����s����ť�� */
	private Button.OnClickListener deTime = new Button.OnClickListener() {

		public void onClick(View v) {
			if (timeChoice > 0) {

				timelayout.removeView(timeEdit[timeChoice - 1]); // �Nlayout�W���ɶ�EditText�R��
				timeChoice--;

			}

		}
	};

	/** �y����{�s�W�ɶ������s��ť�� */
	private Button.OnClickListener recordstart = new Button.OnClickListener() {
		public void onClick(View v) {

			// �ظm������������
			m_recorder = new Saudioclient();

			// �]�w�[��D�{��
			m_recorder.setMainActivity(TimeMergeResult.this);

			// ��l�ƿ�����
			m_recorder.init();

			// �}�l���������
			m_recorder.start();

			// ��ܿ��Ѫ��i�פ��
			dialog1 = new ProgressDialog(TimeMergeResult.this);
			dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog1.setTitle("�y�����Ѫ��A");
			dialog1.setMessage("������");
			dialog1.setIndeterminate(true);
			dialog1.setCancelable(true);
			dialog1.show();

		}

	};

	/** ������thread�i����إ�socket��handler(�ñN�y���ɶǨ�server) */
	public void bulidsockethandler() {
		socketHandler.sendEmptyMessage(0);
	}

	private Handler socketHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// dialog1.setMessage("���ѻy����"); // �w�s�n�y���ɡA�G�N���A����ܦ�"�y�����Ѥ�"

			dialog2 = new ProgressDialog(TimeMergeResult.this);
			dialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog2.setMessage("���ѻy����"); // �w�s�n�y���ɡA�G�N���A����ܦ�"�y�����Ѥ�"
			dialog2.show();
			dialog2.incrementProgressBy(30);

			dialog1.dismiss(); // �N���A��ܥh����

			bulidsocket(); // �إ߻P�y����Oserver���s�u�A�ëإ߶ǰe����thread

			// ���ݯu���s�n�y�����
			while (m_recorder.getrunning()) {

			}

			sendFile(savefile); // �}�l�ǰe�y���ɮ�
			m_recorder = null;

		}

	};

	/** ������thread�i������s�u��handler */
	public void overSockethandler() {
		overSocket.sendEmptyMessage(0);
	}

	private Handler overSocket = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			try {

				// ���_�s�u�������檺�ʧ@
				// out.println("BYE"); // �Vserver�n�D�n���_�s�u
				voice_client.close(); // ����Socket

			} catch (IOException e) {

			}

		}

	};

	/** ������thread�i����B�z���ѵ��G��handler */
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
				// �N�r����ΡA�s��array

				/** �b��������ܿ��ѵ��G���ʧ@ */

				// //�Ntop5���ɶ��s��U�Ԧ���椺
				// adapter = new
				// ArrayAdapter<String>(VoiceTest1.this,android.R.layout.simple_spinner_item,divi_results);
				// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// spinner.setAdapter(adapter);
				//
				// // �N�s�W�ɶ����s�]�m�������(�]���|���^�ǥ��T���G)
				// recordbutton.setClickable(false);

				// �N��{���G�s�W����ܦblayout�W
				timelayout.addView(timeEdit[timeChoice]); // �W�[EditText��layout�W
				timeEdit[timeChoice].setText(de_results); // �]�wEditText�����e
				timeChoice++;

				voice_out.println("");

				// dialog1.dismiss(); // �N���A��ܥh����
				dialog2.dismiss(); // �N���A��ܥh����

			} catch (Exception e) {

				e.printStackTrace();

			}

		}
	};

	/** �إ�Socket���ŧimethod */
	public void bulidsocket() {

		try {

			// �إ߸�Ƴs�u
			voice_client = new Socket("210.71.220.151", 8081); // �W�[���������y�����A�Ⱦ������
			voice_in = new BufferedReader(new InputStreamReader(voice_client.getInputStream())); // �إ�Client
																									// socket
																									// input
																									// stream
			voice_out = new PrintWriter(voice_client.getOutputStream(), true); // �إ�Client
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

	/** �ǰe�y���ɪ��ŧimethod */
	public void sendFile(String fileName) { // �ϥΥ��a���t�α���������ƨæs���s���

		if (fileName == null)
			return; // �W�[���y�Ψ�Ū����󤤪����

		File file = new File(fileName);

		try {

			PrintWriter out1 = new PrintWriter(voice_client.getOutputStream(), true);
			OutputStream doc = new DataOutputStream(new BufferedOutputStream(voice_client.getOutputStream())); // �W�[���Ū���w�İ�

			FileInputStream fos = new FileInputStream(file); // �W�[�����A�Ⱦ������Ȥ�ШD

			// Toast.makeText(Join6.this, "������:" + (int) file.length(),
			// Toast.LENGTH_SHORT).show();
			// Toast.makeText(Join6.this, "���W��:" + (String) file.getName(),
			// Toast.LENGTH_SHORT).show();
			// Toast.makeText(Join6.this, "check1", Toast.LENGTH_SHORT).show();

			byte[] buf = new byte[65536];
			int num = fos.read(buf);

			// Toast.makeText(VoiceTest1.this, "�ǰe���:" + (String)
			// file.getName(), Toast.LENGTH_SHORT).show();

			while (num != (-1)) { // �O�_Ū�����

				doc.write(buf, 0, num); // �����Ƽg�X�����w�İ�

				doc.flush(); // ����w�İϧ��Ƽg���Ȥ��

				num = fos.read(buf); // �~��q���Ū�����

			}

			Thread.sleep(1000);
			String strnull = "";
			out1.println(strnull);

			// Toast.makeText(Join6.this, "�ǰe��󵲧�:" + (String) file.getName(),
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

	// ---------�^��Join5-------------
	private Button.OnClickListener cancelButton = new Button.OnClickListener() {
		public void onClick(View v) {
			TimeMergeResult.this.finish();
		}
	};

	// ----�ǰe�r�굹Server�B����Join5----
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

	/** �N�p�ɾ���ܦ�����r�� */
	public String chtHour(int h) {

		int hour = h; // �ŧi�ǤJ�����ԧB�Ʀr����
		String reHour = ""; // �ŧi�̫�^�Ǫ�����r��

		if ((0 <= hour) && (hour < 10)) { // hour=0~9

			switch (hour) {

			case 0:
				reHour = "�s";
				break;
			case 1:
				reHour = "�@";
				break;
			case 2:
				reHour = "�G";
				break;
			case 3:
				reHour = "�T";
				break;
			case 4:
				reHour = "�|";
				break;
			case 5:
				reHour = "��";
				break;
			case 6:
				reHour = "��";
				break;
			case 7:
				reHour = "�C";
				break;
			case 8:
				reHour = "�K";
				break;
			case 9:
				reHour = "�E";
				break;

			}

		} else if ((10 <= hour) && (hour < 20)) { // hour=10~19

			String tens = "�Q"; // �ŧi�Q��Ƥ���r��
			int re = hour % 10; // �Nhour���H10���l��
			String units = ""; // �ŧi�Ӧ�Ƥ���r��

			switch (re) {

			case 0:
				units = "�s";
				break;
			case 1:
				units = "�@";
				break;
			case 2:
				units = "�G";
				break;
			case 3:
				units = "�T";
				break;
			case 4:
				units = "�|";
				break;
			case 5:
				units = "��";
				break;
			case 6:
				units = "��";
				break;
			case 7:
				units = "�C";
				break;
			case 8:
				units = "�K";
				break;
			case 9:
				units = "�E";
				break;

			}

			if (re != 0) {
				reHour = tens + units; // ���l�ơA�^�ǥ[�W�Ӧ�ƪ���
			} else {
				reHour = tens; // ��n�㰣�A�^��"�Q"
			}
		} else { // hour=20~24
			String twentys = "�G�Q"; // �ŧi�Q��Ƥ���r��
			int re = hour % 20; // �Nhour���H10���l��
			String units = ""; // �ŧi�Ӧ�Ƥ���r��

			switch (re) {

			case 0:
				units = "�s";
				break;
			case 1:
				units = "�@";
				break;
			case 2:
				units = "�G";
				break;
			case 3:
				units = "�T";
				break;
			case 4:
				units = "�|";
				break;

			}

			if (re != 0) {
				reHour = twentys + units; // ���l�ơA�^�ǥ[�W�Ӧ�ƪ���
			} else {
				reHour = twentys; // ��n�㰣�A�^��"�G�Q"
			}
		}

		return reHour;
	}

	/** �N��������ܦ�����r�� */
	public String chtMinute(int m) {

		int minute = m;
		String reMinute = "";

		if ((0 <= minute) && (minute < 10)) { // minute=0~9

			switch (minute) {

			case 0:
				reMinute = "�s";
				break;
			case 1:
				reMinute = "�@";
				break;
			case 2:
				reMinute = "�G";
				break;
			case 3:
				reMinute = "�T";
				break;
			case 4:
				reMinute = "�|";
				break;
			case 5:
				reMinute = "��";
				break;
			case 6:
				reMinute = "��";
				break;
			case 7:
				reMinute = "�C";
				break;
			case 8:
				reMinute = "�K";
				break;
			case 9:
				reMinute = "�E";
				break;

			}

		} else if ((10 <= minute) && (minute < 20)) { // minute=10~19

			String tens = "�Q"; // �ŧi�Q��Ƥ���r��
			int re = minute % 10; // �Nminute���H10���l��
			String units = ""; // �ŧi�Ӧ�Ƥ���r��

			switch (re) {

			case 0:
				units = "�s";
				break;
			case 1:
				units = "�@";
				break;
			case 2:
				units = "�G";
				break;
			case 3:
				units = "�T";
				break;
			case 4:
				units = "�|";
				break;
			case 5:
				units = "��";
				break;
			case 6:
				units = "��";
				break;
			case 7:
				units = "�C";
				break;
			case 8:
				units = "�K";
				break;
			case 9:
				units = "�E";
				break;

			}

			if (re != 0) {

				reMinute = tens + units; // ���l�ơA�^�ǥ[�W�Ӧ�ƪ���

			} else {

				reMinute = tens; // ��n�㰣�A�^��"�Q"
			}

		} else if ((20 <= minute) && (minute < 30)) { // minute=20~29

			String tens = "�G�Q"; // �ŧi�Q��Ƥ���r��
			int re = minute % 20; // �Nminute���H20���l��
			String units = ""; // �ŧi�Ӧ�Ƥ���r��

			switch (re) {

			case 0:
				units = "�s";
				break;
			case 1:
				units = "�@";
				break;
			case 2:
				units = "�G";
				break;
			case 3:
				units = "�T";
				break;
			case 4:
				units = "�|";
				break;
			case 5:
				units = "��";
				break;
			case 6:
				units = "��";
				break;
			case 7:
				units = "�C";
				break;
			case 8:
				units = "�K";
				break;
			case 9:
				units = "�E";
				break;

			}

			if (re != 0) {

				reMinute = tens + units; // ���l�ơA�^�ǥ[�W�Ӧ�ƪ���

			} else {

				reMinute = tens; // ��n�㰣�A�^��"�G�Q"
			}

		} else if ((30 <= minute) && (minute < 40)) { // minute=30~39

			String tens = "�T�Q"; // �ŧi�Q��Ƥ���r��
			int re = minute % 30; // �Nminute���H20���l��
			String units = ""; // �ŧi�Ӧ�Ƥ���r��

			switch (re) {

			case 0:
				units = "�s";
				break;
			case 1:
				units = "�@";
				break;
			case 2:
				units = "�G";
				break;
			case 3:
				units = "�T";
				break;
			case 4:
				units = "�|";
				break;
			case 5:
				units = "��";
				break;
			case 6:
				units = "��";
				break;
			case 7:
				units = "�C";
				break;
			case 8:
				units = "�K";
				break;
			case 9:
				units = "�E";
				break;

			}

			if (re != 0) {

				reMinute = tens + units; // ���l�ơA�^�ǥ[�W�Ӧ�ƪ���

			} else {

				reMinute = tens; // ��n�㰣�A�^��"�T�Q"
			}

		} else if ((40 <= minute) && (minute < 50)) { // minute=40~49

			String tens = "�|�Q"; // �ŧi�Q��Ƥ���r��
			int re = minute % 40; // �Nminute���H20���l��
			String units = ""; // �ŧi�Ӧ�Ƥ���r��

			switch (re) {

			case 0:
				units = "�s";
				break;
			case 1:
				units = "�@";
				break;
			case 2:
				units = "�G";
				break;
			case 3:
				units = "�T";
				break;
			case 4:
				units = "�|";
				break;
			case 5:
				units = "��";
				break;
			case 6:
				units = "��";
				break;
			case 7:
				units = "�C";
				break;
			case 8:
				units = "�K";
				break;
			case 9:
				units = "�E";
				break;

			}

			if (re != 0) {

				reMinute = tens + units; // ���l�ơA�^�ǥ[�W�Ӧ�ƪ���

			} else {

				reMinute = tens; // ��n�㰣�A�^��"�|�Q"
			}

		} else if ((50 <= minute) && (minute < 60)) { // minute=50~59

			String tens = "���Q"; // �ŧi�Q��Ƥ���r��
			int re = minute % 50; // �Nminute���H20���l��
			String units = ""; // �ŧi�Ӧ�Ƥ���r��

			switch (re) {

			case 0:
				units = "�s";
				break;
			case 1:
				units = "�@";
				break;
			case 2:
				units = "�G";
				break;
			case 3:
				units = "�T";
				break;
			case 4:
				units = "�|";
				break;
			case 5:
				units = "��";
				break;
			case 6:
				units = "��";
				break;
			case 7:
				units = "�C";
				break;
			case 8:
				units = "�K";
				break;
			case 9:
				units = "�E";
				break;

			}

			if (re != 0) {

				reMinute = tens + units; // ���l�ơA�^�ǥ[�W�Ӧ�ƪ���

			} else {

				reMinute = tens; // ��n�㰣�A�^��"���Q"
			}

		}

		return reMinute;
	}

}
