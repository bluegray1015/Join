package com.join;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shoushuo.android.tts.ITts;

public class Main extends Activity implements LocationListener {
	/** �ŧi�ܼư� */
	// �ŧiGUI�ǤJ�����ܼ�
	private TextView view_name;
	private TextView view_sex;
	private TextView view_birthday;
	private TextView view_country;
	private TextView view_city;
	private Vector<String> receiveinvite = new Vector<String>();
	private ImageButton goexecute;

	public AlertDialog dialog, invite_message;
	public ProgressDialog dialog1;

	// Socket�s�u�ܼ�
	public static Socket s;
	public static BufferedReader in;
	public static PrintWriter out;
	private String inStr;
	private String[] datas = new String[10];
	private String a;
	private int recount = 0;
	private int comcount = 0;
	// ����birthday�һ��ܼ�
	String bir[] = new String[3];
	String bir_year;
	String bir_month;
	String bir_date;

	private String TransUsername = "";
	private String TransUserpwd = "";
	private String TransSex = "";
	private String TransBirthday = "";
	private String TransCountry = "";
	private String TransCity = "";
	private String Useract = "";
	private String TransServer = "";

	private String loginflag = "";

	// �Ȥ�ݶǰe�����B�z�����
	private TxRxThread txrx;

	// �y����X�һ��ܼ�
	public static String TAG = "DEBUG";
	private TextToSpeech tts;

	// �W��location�һ��ܼ�
	private LocationManager mgr;
	private String best;
	private String userlocation;
	private String executegroup;

	/* ����y�� */
	private ITts ttsService;
	private boolean ttsBound;

	/* �Ҧ��ܼ� */
	private int mode = 0;
	private int type = 0;
	/* sensor */
	private SensorManager mSensorManager;
	private boolean sensorFlag = false;
	/* �q�� */
	private NotificationManager mNotiManager;

	private AlertDialog voteresult_dialog;

	private int myChooseYear;
	private int myChooseMonth;
	private int myChooseDay;

	/** �{���}�l����� */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		showlogin(); // �]�w�n��activity��GUI����
		createsocket(); // �إ�socket

		// �ظm�@�ӻy������
		tts = new TextToSpeech(this, ttsInitListener);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// ���Progress��ܶi�פ��
		dialog1 = new ProgressDialog(Main.this);
		dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog1.setTitle(R.string.processdialogtitle);
		dialog1.setMessage(getResources().getString(R.string.processdialogcontext));
		dialog1.setIndeterminate(true);
		dialog1.setCancelable(true);
		dialog1.show();

		txrx = new TxRxThread(s, in, out);
		txrx.setMessageObserver(this);

		mNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// �ѥ��ǭȪ��ܼơA�h�P�_�O���ӭ������L��
		if (Useract.equals("login"))// �P�_��login��������L��
		{
			txrx.setTransUserDatafor_login(TransUsername, TransUserpwd, Useract);
		} else if (Useract.equals("register"))// �P�_��register��������L��
		{

			txrx.setTransUserDatafor_register(TransUsername, TransUserpwd, TransSex, TransBirthday, TransCountry,
					TransCity, Useract);
		} else if (Useract.equals("modify")) {
			txrx.setTransUserDatafor_modify(TransUsername, TransUserpwd, TransSex, TransBirthday, TransCountry,
					TransCity, Useract);
		}

		txrx.start();

	}

	public void showlogin() {
		setContentView(R.layout.main);

		view_name = (TextView) findViewById(R.id.nameText);
		view_sex = (TextView) findViewById(R.id.sexText);
		// view_bdyear = (TextView)findViewById(R.id.bdyearText);
		// view_bdmonth = (TextView)findViewById(R.id.bdmonthText);
		view_birthday = (TextView) findViewById(R.id.birthdayText);
		view_country = (TextView) findViewById(R.id.coutryText);
		view_city = (TextView) findViewById(R.id.cityText);

		Bundle bundle = this.getIntent().getExtras();
		TransUsername = bundle.getString("Key_username"); // �^���W�@��activity�����
		TransUserpwd = bundle.getString("Key_password");
		TransSex = bundle.getString("Key_radiostate");
		TransBirthday = bundle.getString("Key_birthday");
		TransCountry = bundle.getString("Key_country");
		TransCity = bundle.getString("Key_city");
		Useract = bundle.getString("Key_act");
		TransServer = bundle.getString("Key_server");

		goexecute = (ImageButton) findViewById(R.id.join3execute);
		goexecute.setOnClickListener(execute);

	}

	public void createsocket() {

		// �إ�client socket
		try {
			s = new Socket(TransServer, 8188);
			in = new BufferedReader(new InputStreamReader(s.getInputStream())); // �إ�Client
																				// socket
																				// input
																				// stream
			out = new PrintWriter(s.getOutputStream(), true); // �إ�Client socket
																// output stream

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void overActivityhandler() {
		overHandler.sendEmptyMessage(0);
	}

	private Handler overHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Main.this.finish();
		}

	};

	public void goDataUpdatehandler() {
		messageHandler.sendEmptyMessage(0);
	}

	private Handler messageHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			inStr = txrx.getUserData();

			if (inStr.startsWith("userdata:")) { // Server�^�ǡA�ϥΪ̱b��data

				// ���쪺data�ܼ�
				String Tusername;
				String Tbirthday;
				String Tsex;
				String Tcountry;
				String Tcity;

				String str2 = inStr.substring(9);
				String modifysymbol = "";

				if (str2.startsWith("toModify")) {

					modifysymbol = str2.substring(0, 8);
					str2 = str2.substring(8);
				}

				// �Nserver�ǨӪ��r�����
				int loc1 = 0; // �ŧi���}�Ÿ��b�r�ꪺ��m�ܼ�
				int i = 0;
				String symbol1 = "@"; // �ŧi�n�䪺�Ÿ�
				while (loc1 >= 0) {

					loc1 = str2.indexOf(symbol1, 0); // �M��@�X�{��index number
					if (loc1 >= 0) {

						datas[i] = str2.substring(0, loc1);
						// textArea1.append(str2.substring(0,loc1) + "\n"); //
						// �N�r��(1~loc1)���r��L�X
						loc1 = loc1 + symbol1.length();
						str2 = str2.substring(loc1);
						i++;
					}
				} // end while

				Tusername = datas[0];
				if (datas[2].equals("Male"))
					Tsex = "�k��";
				else
					Tsex = "�k��";
				// Tsex = datas[2];
				Tbirthday = datas[3];
				Tcountry = datas[5];
				Tcity = datas[6];
				// ���ܥͤ����(�r��B�z) added by kiwi @ 2011/7/5
				// �N�ͤ�����~���
				int birth_len = Tbirthday.length();
				String birthday = Tbirthday.replace(".", "/").substring(0, birth_len - 1);

				view_name.setText(Tusername);
				view_sex.setText(Tsex);
				view_birthday.setText(birthday);
				view_country.setText(Tcountry + getString(R.string.comma));
				view_city.setText(Tcity);

				if (modifysymbol.equals("")) { // ���O�ץ�����T�A�~����H�U�ʧ@

					openDialog2(TransUsername + " " + Useract);

					// �y����X����
					if (Tusername.toString().length() > 0) {

						/* �ǤJ�n�����r�� */
						tts.speak("Welcome" + Tusername.toString(), TextToSpeech.QUEUE_FLUSH, null);
						tts.speak("Welcome" + Tusername.toString(), TextToSpeech.QUEUE_FLUSH, null);
					} else {

						/* �L��J�r��� */
						tts.speak("You have no name", TextToSpeech.QUEUE_FLUSH, null);
						tts.speak("You have no name", TextToSpeech.QUEUE_FLUSH, null);
					}
				} else {

					openDialog2(view_name.getText().toString() + " have modified data");

				}

			} else if (inStr.equals("NoThisUser")) { // Server�^�ǡA�n�JID�αK�X�����T�Ұ��檺�ʧ@

				// ���ĵ�i���
				new AlertDialog.Builder(Main.this).setTitle(R.string.dialogtitle_3_1)
						.setMessage(R.string.dialogcontext_3_1)
						.setPositiveButton(R.string.dialogbutton_3_1, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

								loginflag = "No";
								Main.this.finish();

							}
						}).show();
			} else if (inStr.equals("registerReject")) { // Server�^�ǡA���UID�w�s�b�Ұ��檺�ʧ@

				// ���ĵ�i���
				new AlertDialog.Builder(Main.this).setTitle(R.string.dialogtitle_3_2)
						.setMessage(R.string.dialogcontext_3_2)
						.setPositiveButton(R.string.dialogbutton_3_2, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

								loginflag = "No";
								Main.this.finish();

							}
						}).show();

			} else if (inStr.startsWith("groupinformation:")) { // �Y�ǨӨ̦�m���s��T
				// ��ܴ��ܤ��
				new AlertDialog.Builder(Main.this).setTitle(R.string.dialogtitle_3_3)
						.setMessage(R.string.dialogcontext_3_3)
						.setPositiveButton(R.string.dialogbutton_3_3_1, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

								try {
									inStr = java.net.URLDecoder.decode(inStr, "UTF-8");
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
								Bundle bundleStr = new Bundle();
								bundleStr.putString("Key_Str", inStr);
								bundleStr.putString("Key_user", TransUsername);

								// add by Lawrence--2012/11/04
								bundleStr.putInt("myChooseYear", myChooseYear);
								bundleStr.putInt("myChooseMonth", myChooseMonth);
								bundleStr.putInt("myChooseDay", myChooseDay);

								Intent intent = new Intent();
								intent.setClass(Main.this, FriendAndLocationList.class);
								intent.putExtras(bundleStr);
								startActivity(intent);
							}
						}).setNegativeButton(R.string.dialogbutton_3_3_2, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();

			} else if (inStr.startsWith("createActivity")) { // �Y�ǨӬ����ܽаT��

				setNoti();
				// �ѽX
				try {
					inStr = java.net.URLDecoder.decode(inStr, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				// �N���쪺�r��[��vector��
				receiveinvite.add(inStr);
				try {
					if (mode == 1)
						ttsService.speak("���H�ܽбz�[�J����", 1);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				// ��ܴ��ܤ��
				invite_message = new AlertDialog.Builder(Main.this).setTitle(R.string.dialogtitle_3_4)
						.setMessage(R.string.dialogcontext_3_4)
						.setPositiveButton(R.string.dialogbutton_3_4, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

								comcount = 0;
								for (Enumeration e = receiveinvite.elements(); e.hasMoreElements();) {
									a = (String) e.nextElement();
									if (recount == comcount) { // �P�_�ثe���F�X��see
																// detail�A�M�w�n��ܪ��r��
										Bundle bundleStr = new Bundle();

										bundleStr.putInt("mode", mode);
										bundleStr.putString("Key_Str", a);
										bundleStr.putString("Key_User", TransUsername);
										Intent intent = new Intent();
										intent.setClass(Main.this, Join10.class);
										intent.putExtras(bundleStr);
										startActivity(intent);

									}
									comcount++;
								}
								recount++; // ���Ubutton recount �[1
							} // end onClick

						}).show();

				if (mode == 1) {
					type = 0;
					sensorFlag = true;

				}

			} else if (inStr.startsWith("ActivityResult")) { // �Y�ǨӬ��ʧ벼���G
				// ��ܴ��ܤ��
				if (mode == 1)
					try {
						ttsService.speak("����벼���G", 1);
						type = 1;
						sensorFlag = true;

					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
				voteresult_dialog = new AlertDialog.Builder(Main.this).setTitle(R.string.dialogtitle_3_5)
						.setMessage(R.string.dialogcontext_3_5)
						.setPositiveButton(R.string.dialogbutton_3_5, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

								try {
									inStr = java.net.URLDecoder.decode(inStr, "UTF-8");
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
								Bundle bundleStr = new Bundle();
								bundleStr.putString("Key_Str", inStr);
								Intent intent = new Intent();
								intent.setClass(Main.this, Result.class);
								intent.putExtras(bundleStr);
								startActivity(intent);
							}
						}).show();
			}

			dialog1.dismiss();

		}// end method

	};

	/* �ۭq�q�� */
	public class noti extends Activity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			Toast.makeText(noti.this, "���촪�γq��", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	private void setNoti() {
		mNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Intent notifyIntent = new Intent(Main.this, noti.class);
		// notifyIntent.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		PendingIntent appIntent = PendingIntent.getActivity(Main.this, 0, notifyIntent, 0);
		// notifyIntent.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

		Notification myNoti = new Notification();
		long when = System.currentTimeMillis();
		myNoti.icon = R.drawable.join_icon;
		myNoti.tickerText = "�z����@�h�����ܽаT��";
		myNoti.when = when;
		myNoti.defaults = Notification.DEFAULT_ALL;
		myNoti.flags = Notification.FLAG_AUTO_CANCEL;
		myNoti.setLatestEventInfo(Main.this, "Join�q��", "�z����@�h�����ܽаT��", appIntent);

		mNotiManager.notify(0, myNoti);

	}

	/* sensor�B�z */
	private final SensorEventListener mSensorEventListener = new SensorEventListener() {

		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		public void onSensorChanged(SensorEvent event) {
			if (event.values[0] == 0 && sensorFlag && type == 0) {
				comcount = 0;
				for (Enumeration e = receiveinvite.elements(); e.hasMoreElements();) {
					invite_message.dismiss();
					a = (String) e.nextElement();
					if (recount == comcount) { // �P�_�ثe���F�X��see detail�A�M�w�n��ܪ��r��
						// try {
						// a = java.net.URLDecoder.decode(a, "UTF-8");
						// } catch (UnsupportedEncodingException e1) {
						// e1.printStackTrace();
						// }

						Bundle bundleStr = new Bundle();
						bundleStr.putString("Key_Str", a);
						bundleStr.putString("Key_User", TransUsername);
						bundleStr.putInt("mode", mode);
						Intent intent = new Intent();
						intent.setClass(Main.this, Join10.class);
						intent.putExtras(bundleStr);
						startActivity(intent);

					}
					comcount++;
				}
				recount++; // ���Ubutton recount �[1
				sensorFlag = false;
			} else if (event.values[0] == 0 && sensorFlag && type == 1) {
				try {
					inStr = java.net.URLDecoder.decode(inStr, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				Bundle bundleStr = new Bundle();
				bundleStr.putInt("mode", mode);
				bundleStr.putString("Key_Str", inStr);
				Intent intent = new Intent();
				intent.setClass(Main.this, Result.class);
				intent.putExtras(bundleStr);
				startActivity(intent);
				sensorFlag = false;
				voteresult_dialog.dismiss();
			}

		}
	};

	/** ���s�B�z�� */
	private OnClickListener execute = new OnClickListener() {
		public void onClick(View v) {

			Intent intent = new Intent();
			intent.setClass(Main.this, ChooseGroup.class);

			startActivityForResult(intent, 0);

		}
	};

	// �׸Ӹ�ƫ�^�Ǫ����G
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		case RESULT_OK:

			Bundle bunde = data.getExtras();
			String temTransUsername = bunde.getString("Key_username"); // �^���W�@��activity�����
			String temTransUserpwd = bunde.getString("Key_password");
			String temTransSex = bunde.getString("Key_radiostate");
			String temTransBirthday = bunde.getString("Key_birthday");
			String temTransCountry = bunde.getString("Key_country");
			String temTransCity = bunde.getString("Key_city");
			String temUseract = bunde.getString("Key_act");

			// add by Lawrence--2012/11/04
			myChooseYear = bunde.getInt("myChooseYear");
			myChooseMonth = bunde.getInt("myChooseMonth");
			myChooseDay = bunde.getInt("myChooseDay");

			if (temUseract.equals("modify")) { // �Y��^���G��modify�Ǩ�
				out.println("modify:" + temTransUsername + "@" + temTransUserpwd + "@" + temTransSex + "@"
						+ temTransBirthday + "@" + temTransCountry + "@" + temTransCity + "@"); // �ǰe��server�O�nregister����T

			} else if (temUseract.startsWith("execute")) { // �Y��^���G��execute�Ǩ�

				executegroup = temUseract.substring(7);

				mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Criteria criteria = new Criteria();
				best = mgr.getBestProvider(criteria, true);

				// ����location update
				if (mgr.isProviderEnabled(LocationManager.GPS_PROVIDER)
						|| mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

					mgr.requestLocationUpdates(best, 15000, 1, this); // �]�w�W��GPS�Ѽƪ��a��
					Toast.makeText(Main.this, "�A�w�g�}�l����JOIN�\��!�A�N�|�w������̷s���s�T��!", Toast.LENGTH_LONG).show();

				} else {

					startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					Toast.makeText(Main.this, "�A�����}��GPS�Φ�ʺ����w���(�̦n��̳����)�A�AJOIN�@��!", Toast.LENGTH_LONG).show();

				}
			}
		}
	}

	public void openDialog2(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private TextToSpeech.OnInitListener ttsInitListener = new TextToSpeech.OnInitListener() {

		public void onInit(int status) {

			/* �ϥά���ɰϥثe���䴩���� */
			Locale loc = new Locale("us", "", "");
			/* �ˬd�O�_�䴩��J���ɰ� */
			if (tts.isLanguageAvailable(loc) == TextToSpeech.LANG_AVAILABLE) {
				/* �]�w�y�� */
				tts.setLanguage(loc);
			}
			tts.setOnUtteranceCompletedListener(ttsUtteranceCompletedListener);
			Log.i(TAG, "TextToSpeech.OnInitListener");
		}

	};

	private TextToSpeech.OnUtteranceCompletedListener ttsUtteranceCompletedListener = new TextToSpeech.OnUtteranceCompletedListener() {

		public void onUtteranceCompleted(String utteranceId) {

			Log.i(TAG, "TextToSpeech.OnUtteranceCompletedListener");
		}
	};

	@Override
	protected void onDestroy() {

		super.onDestroy();

		/* ����TextToSpeech���귽 */
		tts.shutdown();
		Log.i(TAG, "tts.shutdown");

		if (mgr != null) { // ������location update�~�ݭn����
			mgr.removeUpdates(this);
		}
		/* ����y�� */
		if (ttsBound) {
			ttsBound = false;
			this.unbindService(connection);
		}

	}

	// Join3�����Ұ��檺�ʧ@
	public void finish() {

		super.finish();

		if (loginflag.equals("")) {

			openDialog2(view_name.getText().toString() + " Logout");
		}

		try {
			out.println("BYE");
			s.close();
		} catch (IOException e) {

		}

	}

	// Join3����LocationListener������

	public void onLocationChanged(Location location) {

		String latitude = Double.toString(location.getLatitude()); // �n��
		String longitude = Double.toString(location.getLongitude()); // �g��

		String temusername = view_name.getText().toString();

		userlocation = "userlocation:" + temusername + "@" + latitude + "@" + longitude + "@" + executegroup + "@";

		out.println(userlocation);
		// Toast.makeText(Join3.this, "Update!!", Toast.LENGTH_SHORT).show();

	}

	public void onProviderDisabled(String provider) {

	}

	public void onProviderEnabled(String provider) {

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	// �ŧi6��menu��
	public static final int Menu0 = Menu.FIRST;
	public static final int Menu1 = Menu.FIRST + 1;
	public static final int Menu2 = Menu.FIRST + 2;
	public static final int Menu3 = Menu.FIRST + 3;
	public static final int Menu4 = Menu.FIRST + 4;
	public static final int Menu5 = Menu.FIRST + 5;

	// �]�wmenu�䪺���e
	public void JoinMenu(Menu menu) {
		// menu.setQwertyMode(true);
		menu.add(0, Menu0, 0, "�ק���");

		menu.add(0, Menu1, 0, "�Ҧ����");

		menu.add(0, Menu2, 0, "LogOut");

		menu.add(0, Menu3, 0, "�[�J�s���s");

		menu.add(0, Menu4, 0, "�Ыطs���s");

		menu.add(0, Menu5, 0, "�h�X���s");

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		JoinMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// �P�_���F�ĴX�� menu(���ǥ�Menu0�}�l)
		switch (item.getItemId()) {
		case 1:
			// �ʧO:��^�^�� by kiwi
			// String gender;
			// if (view_sex.getText().toString().equals("�k��")) gender =
			// "Male";
			// else gender = "Female";
			//
			// �ק�ӤH���
			Intent intent = new Intent();
			intent.setClass(Main.this, EditProfile.class);
			// ���s���X�ͤ�r�� added by kiwi @ 2011/7/5
			String[] birth_temp = view_birthday.getText().toString().split("/");
			Bundle bundle = new Bundle();
			bundle.putString("Key_cusername", TransUsername);
			bundle.putString("Key_password", TransUserpwd);
			bundle.putString("Key_sex", view_sex.getText().toString());
			bundle.putString("Key_birthday_y", birth_temp[0]);
			bundle.putString("Key_birthday_m", birth_temp[1]);
			bundle.putString("Key_birthday_d", birth_temp[2]);
			// TODO ������a��ܤ覡���
			bundle.putString("Key_country", view_country.getText().toString());
			bundle.putString("Key_city", view_city.getText().toString());

			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
			break;

		case 2:
			// ���
			new AlertDialog.Builder(this).setTitle(R.string.dialog_title)
					.setItems(R.array.dialog_list, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							if (which == 0)
								mode = 0;
							if (which == 1)
								mode = 1;

						}
					}

					).show();

			break;
		case 3:
			// �n�X
			Main.this.finish();
			break;
		case 4:
			// �Q�[�J�s��Group
			Intent intentjoinmore = new Intent();
			intentjoinmore.setClass(Main.this, JoinNewGroup.class);
			startActivity(intentjoinmore);
			break;
		case 5:
			// �Q�Ыطs��Group
			Intent intentnewG = new Intent();
			intentnewG.setClass(Main.this, BuildNewGroup.class);
			startActivity(intentnewG);
			break;
		case 6:
			// �Q�h�X�w�[�J��Group

			break;

		}
		return super.onOptionsItemSelected(item);
	}

	// //////////////sensor/////////////////////
	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mSensorEventListener);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager
				.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), 1);

	}

	// ////////////////Text to speech////////////////////////
	// �n�� ServiceConnection
	private ServiceConnection connection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder iservice) {

			ttsService = ITts.Stub.asInterface(iservice);
			ttsBound = true;

			try {
				ttsService.initialize();
			} catch (RemoteException e) {
			}
		}

		public void onServiceDisconnected(ComponentName arg0) {
			ttsService = null;
			ttsBound = false;
		}
	};

	// �B�zbindService �� unbindService
	@Override
	protected void onStart() {
		super.onStart();
		if (!ttsBound) {
			String actionName = "com.shoushuo.android.tts.intent.action.InvokeTts";
			Intent intent = new Intent(actionName);
			this.bindService(intent, connection, Context.BIND_AUTO_CREATE);
		}

	}

}
