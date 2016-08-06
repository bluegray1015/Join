package com.join;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

public class Login extends Activity {
	public SharedPreferences share;

	private LineEditText usernameET;
	private LineEditText passwordET;
	private Button registerBtn;
	private Button loginBtn;
	private ImageView logo;
	private Spinner serverSpinner;
	private ArrayAdapter<String> adapter;

	private String username, password;
	private String radiostate = "";
	private String birthday = "";
	private String country = "";
	private String city = "";
	private String Useract = "login";
	private String serverSelected = "140.113.122.232";
	private static final String[] serverList = { "140.113.179.17", "140.113.122.245", "210.71.220.94" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		share = PreferenceManager.getDefaultSharedPreferences(Login.this);

		username = share.getString("username", "");
		password = share.getString("password", "");

		showindex(); // 設定好GUI和按鈕處理者

		PackageManager pm = getPackageManager();
		try {
			pm.getPackageInfo("com.shoushuo.android.tts", 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			new AlertDialog.Builder(Login.this).setTitle("小提醒").setMessage("尚未安裝語音程式")
					.setPositiveButton("下載", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							Uri uri = Uri.parse("market://details?id=com.shoushuo.android.tts");
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(intent);

						}
					}).show();
		}
	}

	public void showindex() {
		setContentView(R.layout.login);

		usernameET = (LineEditText) findViewById(R.id.idEdit);
		passwordET = (LineEditText) findViewById(R.id.passwordEdit);
		registerBtn = (Button) findViewById(R.id.registerButton);
		loginBtn = (Button) findViewById(R.id.loginButton);
		logo = (ImageView) findViewById(R.id.loginImage);
		serverSpinner = (Spinner) findViewById(R.id.serverSpinner);
		// 加入serverlist
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, serverList);
		serverSpinner.setAdapter(adapter);

		usernameET.setText(username);
		passwordET.setText(password);

		loginBtn.setOnClickListener(gologin);
		registerBtn.setOnClickListener(goregister);
		logo.setOnClickListener(toggleSpinner);
		serverSpinner.setVisibility(View.GONE);

	}

	private OnClickListener toggleSpinner = new OnClickListener() {

		public void onClick(View v) {
			if (serverSpinner.getVisibility() == View.VISIBLE)
				serverSpinner.setVisibility(View.GONE);
			else
				serverSpinner.setVisibility(View.VISIBLE);
		}

	};
	private OnClickListener gologin = new OnClickListener() {
		public void onClick(View v) {

			if (usernameET.getText().toString().equals("") || passwordET.getText().toString().equals("")) {

				// 顯示警告方塊
				new AlertDialog.Builder(Login.this).setTitle(R.string.dialogtitle_1_1)
						.setMessage(R.string.dialogcontext_1_1)
						.setPositiveButton(R.string.dialogbutton_1_1, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
			} else if ((usernameET.getText().toString().trim().length() < 4)
					|| (passwordET.getText().toString().trim().length() < 4)) {

				// 顯示警告方塊
				new AlertDialog.Builder(Login.this).setTitle(R.string.dialogtitle_1_2)
						.setMessage(R.string.dialogcontext_1_2)
						.setPositiveButton(R.string.dialogbutton_1_2, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();
			} else {
				//add by Lawrence
				SharedPreferences.Editor usernameSP = share.edit().putString("username",
						usernameET.getText().toString().trim());
				SharedPreferences.Editor passwordSP = share.edit().putString("password",
						passwordET.getText().toString().trim());
				usernameSP.commit();
				passwordSP.commit();

				serverSelected = serverSpinner.getSelectedItem().toString();
				Intent intent = new Intent();
				intent.setClass(Login.this, Main.class);
				Bundle bundle = new Bundle();
				bundle.putString("Key_username", usernameET.getText().toString().trim());
				bundle.putString("Key_password", passwordET.getText().toString());

				// 為了和register攜帶的變數量一樣，故要宣告此區
				bundle.putString("Key_radiostate", radiostate);
				bundle.putString("Key_birthday", birthday);
				bundle.putString("Key_country", country);
				bundle.putString("Key_city", city);
				bundle.putString("Key_act", Useract);
				// TODO 把ServerSpinner的值bundle，可有預備server
				// usernameET.setText(null); // 清空填入區
				// passwordET.setText(null);

				bundle.putString("Key_server", serverSelected);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}

		}
	};

	private OnClickListener goregister = new OnClickListener() {
		public void onClick(View v) {

			serverSelected = serverSpinner.getSelectedItem().toString();
			Intent intent = new Intent();
			intent.setClass(Login.this, Register.class);
			Bundle bundle = new Bundle();
			bundle.putString("Key_server", serverSelected);
			usernameET.setText(""); // 清空填入區
			passwordET.setText("");
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};

}