package com.join;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Start extends Activity {
	/* 獨一無二的menu選項identifier，用以識別事件 */
	static final private int MENU_ADD = Menu.FIRST;
	static final private int MENU_EDIT = Menu.FIRST + 1;
	static final private int MENU_DRAW = Menu.FIRST + 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		/* menu群組ID */
		int idGroup1 = 0;

		/* The order position of the item */
		int orderItem1 = Menu.NONE;
		int orderItem2 = Menu.NONE + 1;
		int orderItem3 = Menu.NONE + 2;

		/* 建立3個Menu選單 */
		menu.add(idGroup1, MENU_ADD, orderItem1, R.string.str_manu1).setIcon(android.R.drawable.ic_menu_add);
		menu.add(idGroup1, MENU_EDIT, orderItem2, R.string.str_manu2).setIcon(android.R.drawable.ic_dialog_info);
		menu.add(idGroup1, MENU_DRAW, orderItem3, R.string.str_manu3).setIcon(R.drawable.hipposmall);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = new Intent();
		switch (item.getItemId()) {
		case (MENU_ADD):
			/* 新建景點資料 */
			intent.setClass(Start.this, New.class);
			startActivity(intent);
			finish();
			break;
		case (MENU_EDIT):
			/* 編輯資料 */
			intent.setClass(Start.this, Modify.class);
			startActivity(intent);
			finish();
			break;
		case (MENU_DRAW):
			/* 前往系統亂數選擇景點功能 */
			intent.setClass(Start.this, Where.class);
			startActivity(intent);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}