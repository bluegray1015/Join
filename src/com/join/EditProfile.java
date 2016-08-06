package com.join;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class EditProfile extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile);

		showoriginal();

	}

	private TextView origin_username;
	private LineEditText origin_password;
	private LineEditText origin_retype;
	private LineEditText origin_birth; // added by kiwi @ 2011/7/3
	// private LineAutoCompleteTextView origin_year;
	// private LineAutoCompleteTextView origin_month;
	// private LineAutoCompleteTextView origin_day;
	private LineAutoCompleteTextView origin_country;
	private LineAutoCompleteTextView origin_city;
	private ImageView origin_myImageView;
	private Button cancel;
	private Button modify;
	private Button cchangephotoButton;
	private String radiostate = "";
	// private String omonth;
	private String ocountry;
	private String Useract = "modify";
	private String newbir;
	String bir[] = new String[3];

	public void showoriginal() {

		origin_username = (TextView) findViewById(R.id.cusernameEdit);
		origin_password = (LineEditText) findViewById(R.id.cpasswordEdit);
		origin_retype = (LineEditText) findViewById(R.id.cretypeEdit);
		origin_myImageView = (ImageView) findViewById(R.id.cmyImageView);
		origin_birth = (LineEditText) findViewById(R.id.cbirthEdit);
		// origin_year = (LineAutoCompleteTextView)findViewById(R.id.cyearEdit);
		// origin_month =
		// (LineAutoCompleteTextView)findViewById(R.id.cmonthEdit);
		// origin_day = (LineAutoCompleteTextView)findViewById(R.id.cdayEdit);
		origin_country = (LineAutoCompleteTextView) findViewById(R.id.ccountryEdit);
		origin_city = (LineAutoCompleteTextView) findViewById(R.id.ccityEdit);

		final RadioButton cradio_male = (RadioButton) findViewById(R.id.cmaleRadio);
		final RadioButton cradio_female = (RadioButton) findViewById(R.id.cFemaleRadio);
		cradio_male.setOnClickListener(radio_listener);
		cradio_female.setOnClickListener(radio_listener);

		// ArrayAdapter<String> yearadapter = new ArrayAdapter<String>(this,
		// R.layout.small_list_item_1, YEARS);
		// origin_year.setAdapter(yearadapter);
		//
		// ArrayAdapter<String> monthadapter = new ArrayAdapter<String>(this,
		// R.layout.small_list_item_1, MONTHS);
		// origin_month.setAdapter(monthadapter);
		//
		// ArrayAdapter<String> dayadapter = new ArrayAdapter<String>(this,
		// R.layout.small_list_item_1, DAYS);
		// origin_day.setAdapter(dayadapter);

		ArrayAdapter<String> countryadapter = new ArrayAdapter<String>(this, R.layout.small_list_item_1, COUNTRIES);
		origin_country.setAdapter(countryadapter);

		ArrayAdapter<String> cityadapter = new ArrayAdapter<String>(this, R.layout.small_list_item_1, CITIES);
		origin_city.setAdapter(cityadapter);

		Bundle bundle = this.getIntent().getExtras();
		origin_username.setText(bundle.getString("Key_cusername")); // 擷取上個activity的資料
		origin_password.setText(bundle.getString("Key_password"));
		origin_retype.setText(bundle.getString("Key_password"));
		// origin_year.setText();
		// origin_day.setText();
		origin_city.setText(bundle.getString("Key_city"));

		// 切除month後面的斜線-----------
		// omonth = bundle.getString("Key_birthday_m"); //
		// 擷取上個activity的資料birthday month
		// int loc1 = 0;
		// String symslope= getString(R.string.divi); // 宣告要找的符號
		// loc1 = omonth.indexOf(symslope,0); // 尋找/出現的index number
		// if (loc1 >= 0)
		// {
		origin_birth.setText(bundle.getString("Key_birthday_y") + "/" + bundle.getString("Key_birthday_m") + "/"
				+ bundle.getString("Key_birthday_d")); // 將字串(1~loc1)的字串取出
		// }

		// 切除country後面的,-----------
		ocountry = bundle.getString("Key_country"); // 擷取上個activity的資料country
		int loc2 = 0;
		String symcomma = getString(R.string.comma); // 宣告要找的符號
		loc2 = ocountry.indexOf(symcomma, 0); // 尋找,出現的index number
		if (loc2 >= 0) {
			origin_country.setText(ocountry.substring(0, loc2)); // 將字串(1~loc2)的字串取出
		}
		// -------------------------------

		radiostate = bundle.getString("Key_sex"); // 擷取上個activity的資料sex
		// 設sex初使值
		if (radiostate.equals(cradio_male.getText().toString())) {
			cradio_male.setChecked(true);
		} else if (radiostate.equals(cradio_female.getText().toString())) {
			cradio_female.setChecked(true);
		}

		cancel = (Button) findViewById(R.id.cancelButton_join11);
		modify = (Button) findViewById(R.id.modifyButton_join11);
		cchangephotoButton = (Button) findViewById(R.id.cchangephotoButton);
		cchangephotoButton.setOnClickListener(cchangephoto);
		cancel.setOnClickListener(cancelmofify);
		modify.setOnClickListener(modifyclick);
		origin_birth.setOnClickListener(datePicker); // added by kiwi @ 2011/7/3
		origin_birth.setInputType(0); // soft keyboard is not allowed.
	}

	private OnClickListener datePicker = new OnClickListener() {

		// String[] birth_temp = origin_birth.getText().toString().split("/");

		private int mYear = 1980;
		// private int mMonth=Integer.parseInt(birth_temp[1])-1;
		private int mMonth = 0;
		private int mDay = 1;

		public void onClick(View v) {
			// datePicker to change date
			new DatePickerDialog(EditProfile.this, new DatePickerDialog.OnDateSetListener() {

				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					origin_birth.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
				}
			}, mYear, mMonth, mDay).show();
			// origin_birth.setText(birth_temp[0]);
		}

	};
	private OnClickListener modifyclick = new OnClickListener() { // 當按下返回，所執行的動作
		public void onClick(View v) {
			if (origin_username.getText().toString().equals("") || origin_birth.getText().toString().equals("")
					|| origin_country.getText().toString().equals("") || origin_city.getText().toString().equals("")
					|| radiostate.equals("")) { // 若其中一欄尚未填入，所執行的動作

				// 顯示警告方塊
				new AlertDialog.Builder(EditProfile.this).setTitle(R.string.dialogtitle_2_1)
						.setMessage(R.string.dialogcontext_2_1)
						.setPositiveButton(R.string.dialogbutton_2_1, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();

			} else if (origin_password.getText().toString().trim().length() < 4) {

				// 顯示警告方塊
				new AlertDialog.Builder(EditProfile.this).setTitle(R.string.dialogtitle_2_2)
						.setMessage(R.string.dialogcontext_2_2)
						.setPositiveButton(R.string.dialogbutton_2_2, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();

			} else if (!(origin_password.getText().toString().equals(origin_retype.getText().toString()))) { // 若密碼重複確認不相同，所執行的動作

				// 顯示警告方塊
				new AlertDialog.Builder(EditProfile.this).setTitle(R.string.dialogtitle_2_3)
						.setMessage(R.string.dialogcontext_2_3)
						.setPositiveButton(R.string.dialogbutton_2_3, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();

			} else {
				// newbir = origin_year.getText().toString()+"."+
				// origin_month.getText().toString()+"."+
				// origin_day.getText().toString()+"."; // 先將生日資訊結合在一起
				newbir = origin_birth.getText().toString().replace("/", ".") + ".";
				// 將性別轉成英文 by kiwi@2011/6/30
				String gender;
				if (radiostate.equals("男性"))
					gender = "Male";
				else
					gender = "Female";
				// kiwi end
				Intent intent = new Intent();
				// intent.setClass(Join11.this, Join3.class);
				Bundle bundle = new Bundle();
				bundle.putString("Key_username", origin_username.getText().toString());
				bundle.putString("Key_password", origin_password.getText().toString());
				bundle.putString("Key_radiostate", gender);
				bundle.putString("Key_birthday", newbir);
				bundle.putString("Key_country", origin_country.getText().toString());
				bundle.putString("Key_city", origin_city.getText().toString());
				bundle.putString("Key_act", Useract);
				intent.putExtras(bundle);

				EditProfile.this.setResult(RESULT_OK, intent);
				// startActivity(intent);
				EditProfile.this.finish();
			}
		}
	};

	private OnClickListener cancelmofify = new OnClickListener() { // 當按下返回，所執行的動作
		public void onClick(View v) {
			EditProfile.this.finish();
		}
	};
	private OnClickListener radio_listener = new OnClickListener() {
		public void onClick(View v) {
			RadioButton rb = (RadioButton) v;
			radiostate = rb.getText().toString();

		}
	};

	private static final String[] COUNTRIES = new String[] { "Afghanistan", "Albania", "Algeria", "American Samoa",
			"American", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua B.", "Argentina", "Armenia", "Aruba",
			"Australia", "Austria", "Azerbaijan", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize",
			"Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia H.", "Botswana", "Bouvet Island", "Brazil",
			"British Indian O.T.", "British V. Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi",
			"Cote d'Ivoire", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African",
			"Chad", "Chile", "China", "Christmas Island", "Cocos Islands", "Colombia", "Comoros", "Congo",
			"Cook Islands", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech", "Congo", "Denmark", "Djibouti",
			"Dominica", "Dominican", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea",
			"Estonia", "Ethiopia", "Faeroe Islands", "Falkland Islands", "Fiji", "Finland", "Macedonia", "France",
			"French Guiana", "French Polynesia", "French Southern T.", "Gabon", "Georgia", "Germany", "Ghana",
			"Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea",
			"Guinea-Bissau", "Guyana", "Haiti", "H. Island M. Islands", "Honduras", "Hong Kong", "Hungary", "Iceland",
			"India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan",
			"Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho",
			"Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Madagascar", "Malawi",
			"Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius",
			"Mayotte", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique",
			"Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands A.", "New Caledonia", "New Zealand",
			"Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea", "Northern Marianas", "Norway",
			"Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines",
			"Pitcairn Islands", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russia", "Rwanda",
			"Sqo Tome P.", "Saint Helena", "Saint Kitts N.", "Saint Lucia", "Saint Pierre M.", "Saint Vincent G.",
			"Samoa", "San Marino", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia",
			"Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia S.S.I.", "South Korea", "Spain",
			"Sri Lanka", "Sudan", "Suriname", "Svalbard J. M.", "Swaziland", "Sweden", "Switzerland", "Syria",
			"Taiwan", "Tajikistan", "Tanzania", "Thailand", "The Bahamas", "The Gambia", "Togo", "Tokelau", "Tonga",
			"Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks C.I.", "Tuvalu", "Virgin Islands",
			"Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States M.O.I.",
			"Uruguay", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Wallis and Futuna",
			"Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe" };

	private static final String[] CITIES = new String[] {

	"Abidjan", "Aglomerado G.B.A.", "Ahmedabad", "Alexandria", "Ankara", "Athens", "Atlanta", "Baghdad", "Bandung B.",
			"Bangalore", "Bangkok", "Barcelona", "Beijing", "Belo Horizonte", "Berlin", "Bogota", "Boston", "Brasilia",
			"Cairo", "Canton", "Chengdu", "Chennai", "Chicago", "Chittagong", "Chongqing", "Dacca", "Dallas", "Delhi",
			"Detroit", "Dong Guan", "Fortaleza", "Guadalajara", "Guiyang", "Hanoi", "Harbin", "Ho Chi Minh",
			"Hong Kong", "Houston", "Hyderabad", "Istanbul", "Jabotabek", "Johannesburg", "Karachi", "Khartoum",
			"Kinshasa", "Kolkata", "Kuala Lumpur", "Kyoto", "Lagos", "Lahore", "Lima", "London", "Los Angeles",
			"Madrid", "Manila", "Medellin", "Melbourne", "Mexico", "Miami", "Milan", "Monterey", "Montreal", "Moscow",
			"Mumbai", "Nagoya", "Nanjing", "New York", "Osaka", "Paris", "Petersburg", "Philadelphia", "Phoenix",
			"Port Alegre", "Pune", "Pusan", "Pyongyang", "Rangoon", "Recife", "Rio de Janeiro", "Riyadh", "Rome",
			"Ruhr Gebiet", "Salvador", "San Diego", "San Francisco", "Sao Paulo", "Seoul", "Shanghai", "Shen Yang",
			"Shenzhen", "Singapore", "Surat", "Sydney", "Taipei", "Tehran", "Tianjin", "Tokyo", "Toronto",
			"Washington", "Wuhan", "Xi'an" };

	public void openDialog2(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private OnClickListener cchangephoto = new OnClickListener() {
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			ContentResolver cr = this.getContentResolver();

			try {
				Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

				resizeImage(bitmap, 185, 210);
				/* 將Bitmap設定到ImageView */
				origin_myImageView.setImageBitmap(bitmap);

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
