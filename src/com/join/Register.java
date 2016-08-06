package com.join;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;

public class Register extends Activity {

	private LineEditText username;
	private Button changephotoButton;
	private ImageView myImageView;
	private LineEditText password;
	private LineEditText retype;
	private LineEditText birth;
	// private LineAutoCompleteTextView year;
	// private LineAutoCompleteTextView month;
	// private LineAutoCompleteTextView day;
	private LineAutoCompleteTextView country;
	private LineAutoCompleteTextView city;
	// private Button checkid;
	private Button back;
	private Button submit;

	public ProgressDialog dialog_checkid;

	private String radiostate = "";
	private String birthday = "";
	private String Useract = "register";
	private String TransServer = "";

	// Socket連線變數
	public static Socket s;
	public static BufferedReader in;
	public static PrintWriter out;
	private String inStr;

	// 客戶端傳送接收處理執行緒
	private TxRxThread checkidconnection;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		showregister();

	}

	private void showregister() {
		Bundle bundle = this.getIntent().getExtras();
		TransServer = bundle.getString("Key_server");
		username = (LineEditText) findViewById(R.id.usernameEdit);
		password = (LineEditText) findViewById(R.id.passwordEdit);
		retype = (LineEditText) findViewById(R.id.retypeEdit);
		country = (LineAutoCompleteTextView) findViewById(R.id.countryEdit);
		city = (LineAutoCompleteTextView) findViewById(R.id.cityEdit);
		birth = (LineEditText) findViewById(R.id.birthdayEdit);
		final RadioButton radio_male = (RadioButton) findViewById(R.id.maleRadio);
		final RadioButton radio_female = (RadioButton) findViewById(R.id.FemaleRadio);
		radio_male.setOnClickListener(radio_listener);
		radio_female.setOnClickListener(radio_listener);

		// ArrayAdapter<String> yearadapter = new ArrayAdapter<String>(this,
		// R.layout.small_list_item_1, YEARS);
		// year.setAdapter(yearadapter);
		//
		// ArrayAdapter<String> monthadapter = new ArrayAdapter<String>(this,
		// R.layout.small_list_item_1, MONTHS);
		// month.setAdapter(monthadapter);
		//
		// ArrayAdapter<String> dayadapter = new ArrayAdapter<String>(this,
		// R.layout.small_list_item_1, DAYS);
		// day.setAdapter(dayadapter);

		ArrayAdapter<String> countryadapter = new ArrayAdapter<String>(this, R.layout.small_list_item_1, COUNTRIES);
		country.setAdapter(countryadapter);

		ArrayAdapter<String> cityadapter = new ArrayAdapter<String>(this, R.layout.small_list_item_1, CITIES);
		city.setAdapter(cityadapter);

		myImageView = (ImageView) findViewById(R.id.myImageView);
		changephotoButton = (Button) findViewById(R.id.changephotoButton);
		changephotoButton.setOnClickListener(changephoto);

		// checkid = (Button)findViewById(R.id.checkidButton);
		back = (Button) findViewById(R.id.backButton);
		submit = (Button) findViewById(R.id.submitButton);

		// checkid.setOnClickListener(gocheckid);
		back.setOnClickListener(backindex);
		submit.setOnClickListener(submitdata);

		username.setOnFocusChangeListener(gocheckid);
		birth.setOnClickListener(datePicker);
		birth.setInputType(0);

	}
	private OnClickListener datePicker = new OnClickListener() {
		private int mYear = 1980;
		private int mMonth = 0;
		private int mDay = 1;

		public void onClick(View v) {

			// datePicker to change date
			new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {

				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

					birth.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
				}
			}, mYear, mMonth, mDay).show();
		}

	};
	private OnClickListener backindex = new OnClickListener() { // 當按下返回，所執行的動作
		public void onClick(View v) {
			Register.this.finish();
		}
	};
	// modified by kiwi 直接在id輸入完成時確認帳號 @2011/7/1
	private OnFocusChangeListener gocheckid = new OnFocusChangeListener() { // 當按下檢查ID，所執行的動作

		public void onFocusChange(View v, boolean hasFocus) {

			if (!hasFocus) {
				if (username.getText().toString().equals("")) {

					// 顯示警告方塊
					new AlertDialog.Builder(Register.this).setTitle(R.string.dialogtitle_2_4).setMessage(R.string.dialogcontext_2_4)
							.setPositiveButton(R.string.dialogbutton_2_4, new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {

								}
							}).show();

				} else {

					// 建立check id socket
					createsocket();

					// 顯示Progress對話進度方塊
					dialog_checkid = new ProgressDialog(Register.this);
					dialog_checkid.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					dialog_checkid.setTitle(R.string.processdialogtitle_checkid);
					dialog_checkid.setMessage(getResources().getString(R.string.processdialogcontext_checkid));
					dialog_checkid.setIndeterminate(true);
					dialog_checkid.setCancelable(true);
					dialog_checkid.show();

					String transuseract = "checkid";
					checkidconnection = new TxRxThread(s, in, out);
					checkidconnection.setCheckidMessageObserver(Register.this);
					checkidconnection.setTransUserDatafor_checkid(username.getText().toString().trim(), transuseract);
					checkidconnection.start();

				}
			}
		}
	};

	public void createsocket() {

		// 建立client socket
		try {
			s = new Socket(TransServer, 8188);
			in = new BufferedReader(new InputStreamReader(s.getInputStream())); // 建立Client
																				// socket
																				// input
																				// stream
			out = new PrintWriter(s.getOutputStream(), true); // 建立Client socket
																// output stream

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 當server回傳斷線時，所執行的動作
	public void overActivityhandler() {
		overHandler.sendEmptyMessage(0);
	}

	private Handler overHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			try {
				s.close(); // 關閉socket

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	};

	// 當server回傳check結果資訊時，所執行的動作
	public void goDataUpdatehandler() {
		messageHandler.sendEmptyMessage(0);
	}

	private Handler messageHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			inStr = checkidconnection.getUserData();

			String checkidresult = inStr.substring(14);

			if (checkidresult.equals("exist")) {

				// 顯示警告方塊
				new AlertDialog.Builder(Register.this).setTitle(R.string.dialogtitle_2_5).setMessage(R.string.dialogcontext_2_5_1)
						.setPositiveButton(R.string.dialogbutton_2_5, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

								out.println("BYE");
								username.setText(""); // 將不能註冊的帳號清空

							}
						}).show();

			} else if (checkidresult.equals("noexist")) {

				// 顯示警告方塊
				new AlertDialog.Builder(Register.this).setTitle(R.string.dialogtitle_2_5).setMessage(R.string.dialogcontext_2_5_2)
						.setPositiveButton(R.string.dialogbutton_2_5, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

								out.println("BYE");

							}
						}).show();

			}

			dialog_checkid.dismiss();

		}
	};

	private OnClickListener changephoto = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();

			/* 開啟Pictures畫面Type設定為image */
			intent.setType("image/*");

			/* 使用Intent.ACTION_GET_CONTENT這個Action */
			intent.setAction(Intent.ACTION_GET_CONTENT);

			/* 取得相片後返回本畫面 */
			startActivityForResult(intent, 1);
		}
	};

	private OnClickListener submitdata = new OnClickListener() { // 當按下交遞使用者資料，所執行的動作
		public void onClick(View v) {

			if (username.getText().toString().equals("") || birth.getText().toString().equals("") || country.getText().toString().equals("")
					|| city.getText().toString().equals("") || radiostate.equals("")) { // 若其中一欄尚未填入，所執行的動作

				// 顯示警告方塊
				new AlertDialog.Builder(Register.this).setTitle(R.string.dialogtitle_2_1).setMessage(R.string.dialogcontext_2_1)
						.setPositiveButton(R.string.dialogbutton_2_1, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();

			} else if ((username.getText().toString().trim().length() < 4) || (password.getText().toString().trim().length() < 4)) {

				// 顯示警告方塊
				new AlertDialog.Builder(Register.this).setTitle(R.string.dialogtitle_2_2).setMessage(R.string.dialogcontext_2_2)
						.setPositiveButton(R.string.dialogbutton_2_2, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();

			} else if (!(password.getText().toString().equals(retype.getText().toString()))) { // 若密碼重複確認不相同，所執行的動作

				// 顯示警告方塊
				new AlertDialog.Builder(Register.this).setTitle(R.string.dialogtitle_2_3).setMessage(R.string.dialogcontext_2_3)
						.setPositiveButton(R.string.dialogbutton_2_3, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();

			} else { // 以上兩個條件皆不符合，才執行的動作
						// 先將生日資訊結合在一起
						// birthday = year.getText().toString()+"."+
				// month.getText().toString()+"."+
				// day.getText().toString()+".";
				birthday = birth.getText().toString().replace("/", ".") + "."; // modified
																				// by
																				// kiwi
																				// @2011/7/1

				// 將性別轉成英文 by kiwi@2011/6/30
				String gender;
				if (radiostate.equals("男性"))
					gender = "Male";
				else
					gender = "Female";
				// kiwi end
				Intent intent = new Intent();
				intent.setClass(Register.this, Main.class);

				Bundle bundle = new Bundle();
				bundle.putString("Key_username", username.getText().toString().trim());
				bundle.putString("Key_password", password.getText().toString());
				bundle.putString("Key_radiostate", gender);
				bundle.putString("Key_birthday", birthday);
				bundle.putString("Key_country", country.getText().toString());
				bundle.putString("Key_city", city.getText().toString());
				bundle.putString("Key_act", Useract);

				intent.putExtras(bundle);
				startActivity(intent);
				Register.this.finish();

			}

		}
	};

	private OnClickListener radio_listener = new OnClickListener() {
		public void onClick(View v) {
			RadioButton rb = (RadioButton) v;
			radiostate = rb.getText().toString();

		}
	};

	private static final String[] COUNTRIES = new String[]{"Afghanistan", "Albania", "Algeria", "American Samoa", "American", "Andorra", "Angola",
			"Anguilla", "Antarctica", "Antigua B.", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahrain", "Bangladesh",
			"Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia H.", "Botswana", "Bouvet Island", "Brazil",
			"British Indian O.T.", "British V. Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cote d'Ivoire", "Cambodia", "Cameroon",
			"Canada", "Cape Verde", "Cayman Islands", "Central African", "Chad", "Chile", "China", "Christmas Island", "Cocos Islands", "Colombia",
			"Comoros", "Congo", "Cook Islands", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech", "Congo", "Denmark", "Djibouti", "Dominica",
			"Dominican", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Faeroe Islands",
			"Falkland Islands", "Fiji", "Finland", "Macedonia", "France", "French Guiana", "French Polynesia", "French Southern T.", "Gabon",
			"Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea",
			"Guinea-Bissau", "Guyana", "Haiti", "H. Island M. Islands", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran",
			"Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos",
			"Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Madagascar", "Malawi",
			"Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia",
			"Moldova", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands",
			"Netherlands A.", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea",
			"Northern Marianas", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines",
			"Pitcairn Islands", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russia", "Rwanda", "Sqo Tome P.",
			"Saint Helena", "Saint Kitts N.", "Saint Lucia", "Saint Pierre M.", "Saint Vincent G.", "Samoa", "San Marino", "Saudi Arabia", "Senegal",
			"Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia S.S.I.",
			"South Korea", "Spain", "Sri Lanka", "Sudan", "Suriname", "Svalbard J. M.", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan",
			"Tajikistan", "Tanzania", "Thailand", "The Bahamas", "The Gambia", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia",
			"Turkey", "Turkmenistan", "Turks C.I.", "Tuvalu", "Virgin Islands", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom",
			"United States", "United States M.O.I.", "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Wallis and Futuna",
			"Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"};

	private static final String[] CITIES = new String[]{

	"Abidjan", "Aglomerado G.B.A.", "Ahmedabad", "Alexandria", "Ankara", "Athens", "Atlanta", "Baghdad", "Bandung B.", "Bangalore", "Bangkok",
			"Barcelona", "Beijing", "Belo Horizonte", "Berlin", "Bogota", "Boston", "Brasilia", "Cairo", "Canton", "Chengdu", "Chennai", "Chicago",
			"Chittagong", "Chongqing", "Dacca", "Dallas", "Delhi", "Detroit", "Dong Guan", "Fortaleza", "Guadalajara", "Guiyang", "Hanoi", "Harbin",
			"Ho Chi Minh", "Hong Kong", "Houston", "Hyderabad", "Istanbul", "Jabotabek", "Johannesburg", "Karachi", "Khartoum", "Kinshasa",
			"Kolkata", "Kuala Lumpur", "Kyoto", "Lagos", "Lahore", "Lima", "London", "Los Angeles", "Madrid", "Manila", "Medellin", "Melbourne",
			"Mexico", "Miami", "Milan", "Monterey", "Montreal", "Moscow", "Mumbai", "Nagoya", "Nanjing", "New York", "Osaka", "Paris", "Petersburg",
			"Philadelphia", "Phoenix", "Port Alegre", "Pune", "Pusan", "Pyongyang", "Rangoon", "Recife", "Rio de Janeiro", "Riyadh", "Rome",
			"Ruhr Gebiet", "Salvador", "San Diego", "San Francisco", "Sao Paulo", "Seoul", "Shanghai", "Shen Yang", "Shenzhen", "Singapore", "Surat",
			"Sydney", "Taipei", "Tehran", "Tianjin", "Tokyo", "Toronto", "Washington", "Wuhan", "Xi'an"};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			ContentResolver cr = this.getContentResolver();

			try {
				Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

				resizeImage(bitmap, 185, 210);
				/* 將Bitmap設定到ImageView */
				myImageView.setImageBitmap(bitmap);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static Drawable resizeImage(Bitmap bitmap, int w, int h) {

		// load the origial Bitmap

		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();

		int height = BitmapOrg.getHeight();

		int newWidth = w;

		int newHeight = h;
		// calculate the scale

		float scaleWidth = ((float) newWidth) / width;

		float scaleHeight = ((float) newHeight) / height;
		// create a matrix for the manipulation

		Matrix matrix = new Matrix();

		// resize the Bitmap

		matrix.postScale(scaleWidth, scaleHeight);

		// if you want to rotate the Bitmap

		// matrix.postRotate(45);

		// recreate the new Bitmap

		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,

		height, matrix, true);

		// make a Drawable from Bitmap to allow to set the Bitmap

		// to the ImageView, ImageButton or what ever

		return new BitmapDrawable(resizedBitmap);

	}

}