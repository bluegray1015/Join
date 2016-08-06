package com.join;

import com.join.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class JoinNewGroup extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showsearch();
	}

	private Button type_1;
	private Button type_2;
	private Button type_3;

	private Button back;
	private Button join;

	private CheckBox popone;
	private CheckBox poptwo;
	private CheckBox popthree;

	private String selectpop1;
	private String selectpop2;
	private String selectpop3;

	public void showsearch() {
		setContentView(R.layout.join_new_group);

		type_1 = (Button) findViewById(R.id.typebutton1);
		type_1.setOnClickListener(gotype1);

		type_2 = (Button) findViewById(R.id.typebutton2);
		type_2.setOnClickListener(gotype2);

		type_3 = (Button) findViewById(R.id.typebutton3);
		type_3.setOnClickListener(gotype3);

		popone = (CheckBox) findViewById(R.id.popgroup1);
		popone.setOnClickListener(selectpopgroup1);

		poptwo = (CheckBox) findViewById(R.id.popgroup2);
		poptwo.setOnClickListener(selectpopgroup2);

		popthree = (CheckBox) findViewById(R.id.popgroup3);
		popthree.setOnClickListener(selectpopgroup3);

		back = (Button) findViewById(R.id.popbackbutton);
		back.setOnClickListener(backlogin);

		join = (Button) findViewById(R.id.popjoinbutton);
		join.setOnClickListener(joinselect);

	}

	private OnClickListener gotype1 = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(JoinNewGroup.this, Join8.class);
			startActivity(intent);
		}
	};

	private OnClickListener gotype2 = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(JoinNewGroup.this, Join8_2.class);
			startActivity(intent);
		}
	};

	private OnClickListener gotype3 = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(JoinNewGroup.this, Join8_3.class);
			startActivity(intent);
		}
	};

	private OnClickListener selectpopgroup1 = new OnClickListener() {
		public void onClick(View v) {
			CheckBox cb = (CheckBox) v;
			if (((CheckBox) cb).isChecked()) {
				selectpop1 = cb.getText().toString();
			} else {
				selectpop1 = null;
			}

		}
	};

	private OnClickListener selectpopgroup2 = new OnClickListener() {
		public void onClick(View v) {
			CheckBox cb = (CheckBox) v;
			if (((CheckBox) cb).isChecked()) {
				selectpop2 = cb.getText().toString();
			} else {
				selectpop2 = null;
			}

		}
	};

	private OnClickListener selectpopgroup3 = new OnClickListener() {
		public void onClick(View v) {
			CheckBox cb = (CheckBox) v;
			if (((CheckBox) cb).isChecked()) {
				selectpop3 = cb.getText().toString();
			} else {
				selectpop3 = null;
			}

		}
	};

	private OnClickListener backlogin = new OnClickListener() {
		public void onClick(View v) {
			JoinNewGroup.this.finish();
		}
	};

	private OnClickListener joinselect = new OnClickListener() {
		public void onClick(View v) {
			transmitdata();
			openshortdialog();
		}
	};

	private void transmitdata() {

	}

	private void openshortdialog() {

		if ((selectpop1 == null) && (selectpop2 == null) && (selectpop3 == null)) {
			Toast.makeText(JoinNewGroup.this, "Please Select at Least One Group of Popularity!", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(JoinNewGroup.this, "You Have Joined The Groups You Just Selected!", Toast.LENGTH_SHORT)
					.show();
		}
	}

}