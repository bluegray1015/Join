package com.join;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FriendAndLocationList extends Activity implements Runnable {
	// add by Lawrence--2012/11/03
	private ProgressDialog WaitDialog;
	private Bundle bundle;
	private int myChooseYear, myChooseMonth, myChooseDay;

	private EditText priority[][];
	private TextView texttitle[]; // 宣告負責儲存TextView reference的一維陣列
	private Button buttonexecute[][]; // 宣告負責儲存Button
										// reference的二維陣列(每個group項分屬的button)
	private CheckBox choicefri[][]; // 宣告負責儲存CheckBox
									// reference的二維陣列(每個group項分屬的CheckBox)
	private RadioButton destination[][];// 宣告負責儲存RadioButton
										// reference的陣列(每個group項分屬的RadioButton)
	private TextView nofri[]; // 宣告負責儲存TextView reference的一維陣列

	private String symbol0 = "&&"; // 宣告分隔group間的符號
	private String symbol1 = "!"; // 宣告分隔group和其相關蒐尋data的符號
	private String symbol2_1 = "@"; // 宣告某group底下，分隔搜尋到不同成員間的符號
	private String symbol2_2 = "%"; // 宣告某group底下，分隔搜尋到不同成員座標間的符號
	private String symbol3_1 = "#"; // 宣告某group底下，分隔搜尋到不同相關商店間的符號
	private String symbol3_2 = "*"; // 宣告某group底下，分隔搜尋到不同相關商店座標間的符號
	private String symbol3_3 = "/tele/"; // 宣告某group底下，分隔搜尋到不同相關商店電話的符號
	private String symbol3_4 = "/address/"; // 宣告某group底下，分隔搜尋到不同相關商店地址的符號
	private String myID;

	private String Friendloc[][];
	private String Placeloc[][];
	private String Placetele[][];
	private String Placeaddress[][];

	int friendnum[]; // 負責儲存執行的每個group中，搜尋到有幾個成員
	int destnum[]; // 負責儲存執行的每個group中，搜尋到有幾個相關商店

	String actStr[] = new String[10]; // 宣告儲存個別group分項的data字串

	int cutStr = 0; // 記錄有幾個group執行回傳

	private String action; // 儲存從server傳來的字串

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_and_location_list);

		// ----get從server回傳Search到的字串
		Bundle bundleStr = this.getIntent().getExtras();
		action = bundleStr.getString("Key_Str");
		myID = bundleStr.getString("Key_user");

		// add by Lawrence--2012/11/04
		myChooseYear = bundleStr.getInt("myChooseYear");
		myChooseMonth = bundleStr.getInt("myChooseMonth");
		myChooseDay = bundleStr.getInt("myChooseDay");

		Log.e("Lawrence", "action=" + action);
		Log.e("Lawrence", "myID=" + myID);

		// ----切割字串----
		if (action.startsWith("groupinformation:")) {
			int loc0 = 0;

			action = action.substring(17); // 先將標頭去除

			// ----將字串依活動(不同group)切開----
			while (loc0 >= 0) {
				loc0 = action.indexOf(symbol0, 0); // 尋找&&出現的index number
				if (loc0 >= 0) {
					actStr[cutStr] = action.substring(0, loc0);
					loc0 = loc0 + symbol0.length();
					action = action.substring(loc0);
					cutStr++;
					// ----共有cutStr個活動資訊----
				}
			} // end while

			showlayout();
			// buttonAct(); removed by kiwi @ 2011/7/5
		}
	}

	// private Button button_exit;

	private void showlayout() {

		LinearLayout layout = (LinearLayout) findViewById(R.id.ReceiveSuggestLayout);

		int loc1_1 = 0; // 宣告分開符號在字串的位置變數
		int loc2_1 = 0;
		int loc2_2 = 0;
		int loc3_1 = 0;
		int loc3_2 = 0;
		int loc3_3 = 0;
		int loc3_4 = 0;

		// -----宣告物件-----
		friendnum = new int[cutStr]; // 建置儲存每個group搜尋到members個數的儲存物件
		for (int i = 0; i < cutStr; i++) {

			friendnum[i] = 0; // 初始化為零
		}

		destnum = new int[cutStr]; // 建置儲存每個group搜尋到相關商店個數的儲存物件
		for (int i = 0; i < cutStr; i++) {

			destnum[i] = 0; // 初始化為零
		}

		// ----table個數---
		TableLayout tablelayout[] = new TableLayout[cutStr];// 建置區塊(區塊數和group數同)
		for (int i = 0; i < cutStr; i++) {

			tablelayout[i] = new TableLayout(this);
		}

		// ----每個table 50列---
		TableRow row[][] = new TableRow[cutStr][50]; // 建置區塊中的列數
		for (int i = 0; i < 50; i++) {

			for (int j = 0; j < cutStr; j++) {

				row[j][i] = new TableRow(this);
			}
		}

		// ----活動(group)的圖像----
		ImageView image[] = new ImageView[cutStr];
		for (int i = 0; i < cutStr; i++) {

			image[i] = new ImageView(this);
		}

		// ----每個活動(group)2個button----
		buttonexecute = new Button[cutStr][2];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < cutStr; j++) {
				buttonexecute[j][i] = new Button(this);
			}
		}

		// -----每個活動兩個固定text---
		TextView text[][] = new TextView[cutStr][2];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < cutStr; j++) {
				text[j][i] = new TextView(this);
			}
		}

		// -----分隔線----
		TextView texttool[] = new TextView[cutStr];
		for (int i = 0; i < cutStr; i++) {
			texttool[i] = new TextView(this);
			texttool[i].setText("-----------------------------------------------------------");
		}
		// ----活動的title---- 設定三個活動(cutStr=3)
		texttitle = new TextView[cutStr];
		for (int i = 0; i < cutStr; i++) {
			texttitle[i] = new TextView(this);
			texttitle[i].setTextSize(25);
			// texttitle[i].setTextColor(R.color.green);
		}

		// ---顯示附近沒有朋友的字串---
		nofri = new TextView[cutStr];
		for (int i = 0; i < cutStr; i++) {
			nofri[i] = new TextView(this);
			nofri[i].setTextColor(Color.RED);
			nofri[i].setText("附近沒有朋友喔!!!");
			nofri[i].setTextSize(20);
		}

		// ----附近朋友checkbox---
		choicefri = new CheckBox[cutStr][50];
		priority = new EditText[cutStr][50];
		for (int i = 0; i < 50; i++) {
			for (int j = 0; j < cutStr; j++) {
				choicefri[j][i] = new CheckBox(this);
				priority[j][i] = new EditText(this);
			}
		}
		// ---附近朋友圖像---
		ImageView imagefri[][] = new ImageView[cutStr][20];
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < cutStr; j++) {
				imagefri[j][i] = new ImageView(this);
			}
		}

		// ---- 附近朋友的(儲存location)---
		Friendloc = new String[cutStr][50];

		// ------附近地點(對應的RadioGroup)----
		RadioGroup destinationGroup[] = new RadioGroup[cutStr];
		for (int i = 0; i < cutStr; i++) {

			destinationGroup[i] = new RadioGroup(this);
		}

		// ------附近地點(對應的RadioButton)----
		destination = new RadioButton[cutStr][20];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < cutStr; j++) {
				destination[j][i] = new RadioButton(this);
			}
		}

		// ----附近地點的location---
		Placeloc = new String[cutStr][20];
		Placetele = new String[cutStr][20];
		Placeaddress = new String[cutStr][20];

		// ---每個活動的切割----
		// 每個活動會在一個tablelayout中，再將每個tablelayout加入layout
		for (int c = 0; c < cutStr; c++) // 每次回圈執行一個tablelayout(一個group)各元件的加入
		{
			tablelayout[c].addView(texttool[c]);

			// 將title切下(依照!符號)，即為此迴圈的group名稱
			loc1_1 = actStr[c].indexOf(symbol1, 0);
			texttitle[c].setText(actStr[c].substring(0, loc1_1));
			texttitle[c].setTextColor(Color.BLACK);

			// 判斷title,選擇對應group的圖像
			if (texttitle[c].getText().equals("Snack")) {
				image[c].setImageDrawable(getResources().getDrawable(R.drawable.snack));
			} else if (texttitle[c].getText().equals("KTV")) {
				image[c].setImageDrawable(getResources().getDrawable(R.drawable.ktv));
			} else if (texttitle[c].getText().equals("Shopping")) {
				image[c].setImageDrawable(getResources().getDrawable(R.drawable.shopping));
			}

			row[c][0].addView(image[c]); // 第一列加入group圖片
			row[c][0].addView(texttitle[c]); // 第一列再加入group名稱

			loc1_1 = loc1_1 + symbol1.length();
			actStr[c] = actStr[c].substring(loc1_1); // 取得去除group名稱後，剩下的group資訊

			// 設定每個group固定顯示的text內容
			text[c][0].setText("附近的朋友:");
			text[c][0].setTextSize(20); // added by kiwi @2011/7/4
			text[c][0].setTextColor(Color.BLACK);
			text[c][1].setText("附近的地點:");
			text[c][1].setTextSize(20); // added by kiwi @2011/7/4
			text[c][1].setTextColor(Color.BLACK);

			// 設定每個group固定顯示的button text內容
			buttonexecute[c][0].setText("送出邀請");
			buttonexecute[c][1].setText("查看地圖");

			row[c][1].addView(text[c][0]); // 將第二列加入"Nearby Friends:"

			// --button聆聽---

			buttonexecute[c][0].setOnClickListener(actButton);
			buttonexecute[c][1].setOnClickListener(seemapButton);

			// -----附近朋友名單切割，且將checkbox加入GUI----
			int j = 0;
			int r = 0;
			int row_ctl = 0; // 0:等待下一筆資料，1:將兩筆寫入tablerow中。added by kiwi
			int j_rec = 0; // 紀錄奇數筆的最後一筆資料
			while (loc2_1 >= 0) {
				TextView title = new TextView(this);
				title.setText("權重：");
				title.setTextColor(Color.BLACK);

				loc2_1 = actStr[c].indexOf(symbol2_1, 0); // 尋找@出現的index number
															// ->symbol2_1 =
															// "@";
				if (loc2_1 >= 0) { // 如果有資料

					choicefri[c][j].setText(actStr[c].substring(0, loc2_1)); // 設置每個members的checkbox
					choicefri[c][j].setTextColor(Color.BLUE);

					if (!(actStr[c].substring(0, loc2_1).equals(myID))) {
						// 因為前兩列有加入原件了，所以從2+r列開始加入
						row[c][2 + r].addView(choicefri[c][j]);
						// 2012/10/06, Lawrence 增加權重設定輸入框
						row[c][2 + r].addView(title);
						priority[c][j].setHint("0");
						priority[c][j].setHintTextColor(Color.LTGRAY);
						row[c][2 + r].addView(priority[c][j]);

						r++;
						// 使用者名單 2 in 1 row
						// if (row_ctl == 1) {
						// row[c][2 + r].addView(choicefri[c][j - 1]);
						// row[c][2 + r].addView(priority[c][j - 1]);
						// row[c][2 + r].addView(choicefri[c][j]);
						// // 2012/10/06, Lawrence 增加權重設定輸入框
						// row[c][2 + r].addView(priority[c][j]);
						//
						// row_ctl = 0;
						// r++;
						// } else {
						// j_rec = j;
						// row_ctl = 1;
						// }
					}
					loc2_1 = loc2_1 + symbol2_1.length();
					actStr[c] = actStr[c].substring(loc2_1);
					friendnum[c]++; // 計數member數
					j++;
				}
			} // end while
			if (row_ctl == 1) {// 沒資料後若為奇數筆資料將最後一筆加入 added by kiwi @2011/7/4
				row[c][2 + r].addView(choicefri[c][j_rec]);
				r++;
			}
			// end of 使用者名單 2 in 1 row
			// Toast.makeText(Join5.this,actStr[c], Toast.LENGTH_SHORT).show();
			loc2_1 = 0; // 為了下個不同group迴圈的執行

			// String qq=Integer.toString(friendnum[c]);
			// Toast.makeText(Join5.this,qq, Toast.LENGTH_SHORT).show();
			if (friendnum[c] == 1) {
				// Toast.makeText(Join5.this,"沒人", Toast.LENGTH_SHORT).show();
				row[c][2 + r].addView(nofri[c]);
				r++;
			}

			// -----附近朋友location切割，並儲存----
			int jj = 0;
			while (loc2_2 >= 0) {
				loc2_2 = actStr[c].indexOf(symbol2_2, 0); // 尋找%出現的index number
				if (loc2_2 >= 0) {
					Friendloc[c][jj] = actStr[c].substring(0, loc2_2);
					// Toast.makeText(layouttest.this,Friendloc[c][jj],
					// Toast.LENGTH_SHORT).show();
					loc2_2 = loc2_2 + symbol2_2.length();
					actStr[c] = actStr[c].substring(loc2_2);
					jj++;
				}
			} // end while
			loc2_2 = 0;
			// Toast.makeText(Join5.this,actStr[c], Toast.LENGTH_SHORT).show();
			row[c][2 + r].addView(text[c][1]); // 在加入所有near
												// friends，其後列加入"Nearest Place:"

			// -----附近地點切割，並加入各地點的Radio button到Radio Group--------
			int k = 0;
			String des_index = null;
			while (loc3_1 >= 0) {
				loc3_1 = actStr[c].indexOf(symbol3_1, 0); // 尋找#出現的index number
				if (k == 0)
					des_index = "A";
				else if (k == 1)
					des_index = "B";
				else if (k == 2)
					des_index = "C";
				else if (k == 3)
					des_index = "D";
				else if (k == 4)
					des_index = "E";
				else if (k == 5)
					des_index = "F";
				else if (k == 6)
					des_index = "G";
				else
					des_index = "H";
				if (loc3_1 >= 0) {
					destination[c][k].setText(des_index + " : " + actStr[c].substring(0, loc3_1)); // 設定Radio
																									// button名稱(店家名稱)
					destination[c][k].setTextColor(Color.BLUE);
					destinationGroup[c].addView(destination[c][k]); // 將此店家的Radio
																	// button加入此迴圈(group)的Radio
																	// Group，顯示GUI上
					loc3_1 = loc3_1 + symbol3_1.length();
					actStr[c] = actStr[c].substring(loc3_1);
					destnum[c]++; // 計數商家個數
					k++;
				}
			} // end while
			loc3_1 = 0;
			// Toast.makeText(Join5.this,actStr[c], Toast.LENGTH_SHORT).show();

			// ----附近地點location切割，並儲存-----
			int kkp = 0;
			while (loc3_2 >= 0) {
				loc3_2 = actStr[c].indexOf(symbol3_2, 0); // 尋找*出現的index number
				if (loc3_2 >= 0) {
					Placeloc[c][kkp] = actStr[c].substring(0, loc3_2); // 儲存location
					loc3_2 = loc3_2 + symbol3_2.length();
					actStr[c] = actStr[c].substring(loc3_2);
					kkp++;
				}
			} // end while
			loc3_2 = 0;

			// ----附近地點電話切割，並儲存-----
			int kkt = 0;
			while (loc3_3 >= 0) {
				loc3_3 = actStr[c].indexOf(symbol3_3, 0); // 尋找*出現的index number
				if (loc3_3 >= 0) {
					Placetele[c][kkt] = actStr[c].substring(0, loc3_3); // 儲存location
					loc3_3 = loc3_3 + symbol3_3.length();
					actStr[c] = actStr[c].substring(loc3_3);
					kkt++;
				}
			} // end while
			loc3_3 = 0;

			// ----附近地點地址切割，並儲存-----
			int kka = 0;
			while (loc3_4 >= 0) {
				loc3_4 = actStr[c].indexOf(symbol3_4, 0); // 尋找*出現的index number
				if (loc3_4 >= 0) {
					Placeaddress[c][kka] = actStr[c].substring(0, loc3_4); // 儲存location
					loc3_4 = loc3_4 + symbol3_4.length();
					actStr[c] = actStr[c].substring(loc3_4);
					kka++;
				}
			} // end while
			loc3_4 = 0;

			if (!(friendnum[c] == 1)) {
				row[c][3 + r].addView(buttonexecute[c][0]); // 將act button加到GUI上
			}
			row[c][3 + r].addView(buttonexecute[c][1]); // 將seemap button加到GUI上

			// --將各列(tablerow)放到tablelayout中---
			tablelayout[c].addView(row[c][0]);
			tablelayout[c].addView(row[c][1]);

			for (int tl = 0; tl < r + 1; tl++) {
				tablelayout[c].addView(row[c][2 + tl]);
			}
			tablelayout[c].addView(destinationGroup[c]);
			tablelayout[c].addView(row[c][r + 3]);

			// 將tablelayout整個放到layout裡
			layout.addView(tablelayout[c]);

		}// end for loop

	}

	// ----離開Join5 button的聆聽------- removed by kiwi @ 2011/7/5
	// private void buttonAct() {
	// button_exit = (Button) findViewById(R.id.join5exit);
	// button_exit.setOnClickListener(exit);
	// }
	private Button.OnClickListener exit = new Button.OnClickListener() {
		public void onClick(View v) {
			FriendAndLocationList.this.finish();
		}
	};

	// 按下act
	private Button.OnClickListener actButton = new Button.OnClickListener() {

		public void onClick(View v) {

			String bundlefri = "invitedfri:";
			String bundledest = null;
			Button actb = (Button) v;

			// 每個act要傳送的資訊
			for (int b = 0; b < cutStr; b++) {
				// 判斷為哪個group的act按鈕，只會有一個button被按
				if (actb == buttonexecute[b][0]) {
					// 讀取所要邀請的朋友名單
					for (int f = 0; f < friendnum[b]; f++) {
						// 可選擇多個被邀請人
						if (choicefri[b][f].isChecked()) {
							bundlefri += choicefri[b][f].getText() + "&";
						}

					}
					String txdestination;
					// 讀取選擇的地點
					for (int d = 0; d < destnum[b]; d++) {
						// 因為是單選Radio button，所以只會有一個被按
						if (destination[b][d].isChecked()) {
							txdestination = destination[b][d].getText().toString().substring(4);
							bundledest = txdestination;
						}
					}

					// 使用者沒勾任何選項時的偵錯
					if (bundlefri.equals("invitedfri:") || bundledest == null) { // 沒勾被邀請人或著沒勾建立活動的相關店家

						// 顯示警告方塊
						new AlertDialog.Builder(FriendAndLocationList.this).setTitle(R.string.dialogtitle)
								.setMessage(R.string.dialogcontext)
								.setPositiveButton(R.string.dialogbutton, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {

									}
								}).show();
					} else {
						// ---資料傳送至Join6----
						bundle = new Bundle();
						bundle.putString("Key_title", texttitle[b].getText().toString());
						bundle.putString("Key_invitedfriend", bundlefri);
						bundle.putString("Key_myID", myID);
						bundle.putString("Key_destination", bundledest);

						// add by Lawrence--2012/11/03
						bundle.putInt("myChooseYear", myChooseYear);
						bundle.putInt("myChooseMonth", myChooseMonth);
						bundle.putInt("myChooseDay", myChooseDay);

						WaitDialog = new ProgressDialog(FriendAndLocationList.this);
						WaitDialog.setTitle("分析中");
						WaitDialog.setIndeterminate(true);
						WaitDialog.setCancelable(false);
						WaitDialog.show();
						new Thread(FriendAndLocationList.this).start();
					}
				}
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		// ---Join6 Send後,將Join5關閉
		case RESULT_OK:
			FriendAndLocationList.this.finish();
		}
	}

	// 按下seemap
	private Button.OnClickListener seemapButton = new Button.OnClickListener() {
		public void onClick(View v) {
			String bundlefri = "";
			String bundlefriloc = "";
			String bundledes = "";
			String bundledesloc = "";
			String bundledestele = "";
			String bundledesadd = "";
			Button mapb = (Button) v;
			for (int b = 0; b < cutStr; b++) {
				if (mapb == buttonexecute[b][1]) {
					Bundle bundle = new Bundle();
					for (int f = 0; f < friendnum[b]; f++) {
						bundlefri += choicefri[b][f].getText().toString() + "&";
					}

					for (int f = 0; f < friendnum[b]; f++) {
						bundlefriloc += Friendloc[b][f] + "&";
					}
					for (int f = 0; f < destnum[b]; f++) {
						bundledes += destination[b][f].getText().toString() + "&";
					}
					for (int f = 0; f < destnum[b]; f++) {
						bundledesloc += Placeloc[b][f] + "&";

					}
					for (int f = 0; f < destnum[b]; f++) {
						bundledestele += Placetele[b][f] + "&";

					}
					for (int f = 0; f < destnum[b]; f++) {
						bundledesadd += Placeaddress[b][f] + "&";

					}

					if (friendnum[b] != 0) {
						bundle.putString("bundlefri", bundlefri);
						bundle.putString("bundlefriloc", bundlefriloc);
					}
					if (destnum[b] != 0) {
						bundle.putString("bundledes", bundledes);
						bundle.putString("bundledesloc", bundledesloc);
						bundle.putString("bundledestele", bundledestele);
						bundle.putString("bundledesadd", bundledesadd);

					}
					bundle.putString("bundlefriendnum", String.valueOf(friendnum[b]));
					bundle.putString("bundledestnum", String.valueOf(destnum[b]));
					bundle.putString("myID", myID);

					Intent intent = new Intent();
					intent.setClass(FriendAndLocationList.this, JoinMapView.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}

		}
	};

	// add by Lawrence--2012/11/03
	public void run() {
		try {
			Thread.sleep(4000);

			handler.sendEmptyMessage(0);
			startActivity(new Intent(FriendAndLocationList.this, TimeMergeResult.class).putExtras(bundle));
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			WaitDialog.dismiss();
		}
	};
}
