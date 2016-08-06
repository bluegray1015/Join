package com.join;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.FileNotFoundException;

import com.join.R;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

public class BuildNewGroup extends Activity {

	private String group_type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.build_new_group);

		// 下拉式選單
		Spinner spinner = (Spinner) findViewById(R.id.join9_grouptypearray);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.join9_group_types,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		// 設定項目被選取之後的動作
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
				group_type = adapterView.getSelectedItem().toString();
			}

			public void onNothingSelected(AdapterView arg0) {
				group_type = null;
			}
		});
		findViews();
		setListeners();
	}

	private EditText group_name;
	private EditText group_leader;
	private Button button_new;
	private Button button_cancel;
	private Button joinchange;
	private ImageView myImageView9;

	private void findViews() {
		group_name = (EditText) findViewById(R.id.join9_groupname);
		group_leader = (EditText) findViewById(R.id.join9_groupleader);
		button_new = (Button) findViewById(R.id.join9_newgroup);
		button_cancel = (Button) findViewById(R.id.join9_cancelnew);
		myImageView9 = (ImageView) findViewById(R.id.myImageView9);
		joinchange = (Button) findViewById(R.id.join9change);

	}

	private void setListeners() {
		button_new.setOnClickListener(newgroup);
		button_cancel.setOnClickListener(cancel);
		joinchange.setOnClickListener(change);
	}

	private Button.OnClickListener newgroup = new Button.OnClickListener() {
		public void onClick(View v) {
			// TODO
		}
	};
	private Button.OnClickListener cancel = new Button.OnClickListener() {
		public void onClick(View v) {
			BuildNewGroup.this.finish();
		}
	};
	private OnClickListener change = new OnClickListener() {
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
				/* 將Bitmap設定到ImageView */
				myImageView9.setImageBitmap(bitmap);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
