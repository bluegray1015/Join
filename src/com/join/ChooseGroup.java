package com.join;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.Toast;

public class ChooseGroup extends Activity {
	// add by Lawrence--2012/11/03
	private DatePicker datePicker;
	private int myYear, myMonth, myDay;

	private Button button_execute;
	private Button button_cancel;

	private String Useract = "execute";
	private String CheckBoxtype = "";
	private String[] group = new String[3];
	private boolean[] noselect = new boolean[3];
	private String groupsum = "";

	private CheckBox[] check = new CheckBox[3];

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_group);

		// Lawrence--2012/11/04
		final Calendar c = Calendar.getInstance();
		myYear = c.get(Calendar.YEAR);
		myMonth = c.get(Calendar.MONTH);
		myDay = c.get(Calendar.DAY_OF_MONTH);

		findViews();
		setListeners();
	}

	private void findViews() {
		// add by Lawrence--2012/11/03
		datePicker = (DatePicker) findViewById(R.id.datepicker);
		datePicker.init(myYear, myMonth, myDay, (OnDateChangedListener) myDateSetListener);

		button_execute = (Button) findViewById(R.id.join4_execute);
		button_cancel = (Button) findViewById(R.id.join4_cancel);

		check[0] = (CheckBox) findViewById(R.id.join4_select1);
		check[1] = (CheckBox) findViewById(R.id.join4_select2);
		check[2] = (CheckBox) findViewById(R.id.join4_select3);

		check[0].setOnClickListener(checkbox_listener);
		check[1].setOnClickListener(checkbox_listener);
		check[2].setOnClickListener(checkbox_listener);
	}

	// add by Lawrence--2012/11/04
	private OnDateChangedListener myDateSetListener = new OnDateChangedListener() {
		public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			myYear = year;
			myMonth = monthOfYear;
			myDay = dayOfMonth;
		}
	};

	private void setListeners() {
		button_execute.setOnClickListener(click1);
		button_cancel.setOnClickListener(click2);
	}

	private OnClickListener checkbox_listener = new OnClickListener() {
		public void onClick(View v) {
			CheckBox cb = (CheckBox) v;

			if (cb.isChecked()) { // ���󦳤Ŀ�~����
				CheckBoxtype = cb.getText().toString();
				for (int i = 0; i < check.length; i++) {
					if (cb == check[i]) {
						group[i] = CheckBoxtype; // ��w�x�s��group
					}
				}
			} else {
				CheckBoxtype = null;
				for (int i = 0; i < check.length; i++) {
					if (cb == check[i]) {
						group[i] = CheckBoxtype; // ��w�x�s��group
					}
				}
			}
		}
	};

	// ����Uexecute�ɡA�Ұ��檺�ʧ@
	private Button.OnClickListener click1 = new Button.OnClickListener() {
		public void onClick(View v) {

			boolean noselecttotal = (group[0] == null);

			for (int i = 0; i < check.length; i++) {
				noselecttotal = (noselecttotal && (group[i] == null));
				if (!(group[i] == null)) {
					groupsum += group[i] + ",";
				}
			}

			// Toast.makeText(Join4.this, groupsum, Toast.LENGTH_SHORT).show();

			// �Y�S���group
			if (!noselecttotal) {
				Useract += groupsum;

				// add by Lawrence--2012/11/04
				String date = "�z��ܪ�����O�G" + String.valueOf(myYear) + "�~" + String.valueOf(myMonth + 1) + "��"
						+ String.valueOf(myDay) + "��";
				Toast.makeText(ChooseGroup.this, date, Toast.LENGTH_LONG).show();

				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("Key_username", "");
				bundle.putString("Key_password", "");
				bundle.putString("Key_radiostate", "");
				bundle.putString("Key_birthday", "");
				bundle.putString("Key_country", "");
				bundle.putString("Key_city", "");
				bundle.putString("Key_act", Useract);

				// add by Lawrence--2012/11/04
				bundle.putInt("myChooseYear", myYear);
				bundle.putInt("myChooseMonth", myMonth);
				bundle.putInt("myChooseDay", myDay);

				intent.putExtras(bundle);

				ChooseGroup.this.setResult(RESULT_OK, intent);
				ChooseGroup.this.finish();
			} else {
				Toast.makeText(ChooseGroup.this, "���I��ܤ֤@�Ӹs��!", Toast.LENGTH_SHORT).show();
			}
		}
	};

	// ����Ucancel�ɡA�Ұ��檺�ʧ@
	private Button.OnClickListener click2 = new Button.OnClickListener() {
		public void onClick(View v) {
			ChooseGroup.this.finish();
		}
	};
}