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
	private TextView texttitle[]; // �ŧi�t�d�x�sTextView reference���@���}�C
	private Button buttonexecute[][]; // �ŧi�t�d�x�sButton
										// reference���G���}�C(�C��group�����ݪ�button)
	private CheckBox choicefri[][]; // �ŧi�t�d�x�sCheckBox
									// reference���G���}�C(�C��group�����ݪ�CheckBox)
	private RadioButton destination[][];// �ŧi�t�d�x�sRadioButton
										// reference���}�C(�C��group�����ݪ�RadioButton)
	private TextView nofri[]; // �ŧi�t�d�x�sTextView reference���@���}�C

	private String symbol0 = "&&"; // �ŧi���jgroup�����Ÿ�
	private String symbol1 = "!"; // �ŧi���jgroup�M������`�Mdata���Ÿ�
	private String symbol2_1 = "@"; // �ŧi�Ygroup���U�A���j�j�M�줣�P���������Ÿ�
	private String symbol2_2 = "%"; // �ŧi�Ygroup���U�A���j�j�M�줣�P�����y�ж����Ÿ�
	private String symbol3_1 = "#"; // �ŧi�Ygroup���U�A���j�j�M�줣�P�����ө������Ÿ�
	private String symbol3_2 = "*"; // �ŧi�Ygroup���U�A���j�j�M�줣�P�����ө��y�ж����Ÿ�
	private String symbol3_3 = "/tele/"; // �ŧi�Ygroup���U�A���j�j�M�줣�P�����ө��q�ܪ��Ÿ�
	private String symbol3_4 = "/address/"; // �ŧi�Ygroup���U�A���j�j�M�줣�P�����ө��a�}���Ÿ�
	private String myID;

	private String Friendloc[][];
	private String Placeloc[][];
	private String Placetele[][];
	private String Placeaddress[][];

	int friendnum[]; // �t�d�x�s���檺�C��group���A�j�M�즳�X�Ӧ���
	int destnum[]; // �t�d�x�s���檺�C��group���A�j�M�즳�X�Ӭ����ө�

	String actStr[] = new String[10]; // �ŧi�x�s�ӧOgroup������data�r��

	int cutStr = 0; // �O�����X��group����^��

	private String action; // �x�s�qserver�ǨӪ��r��

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_and_location_list);

		// ----get�qserver�^��Search�쪺�r��
		Bundle bundleStr = this.getIntent().getExtras();
		action = bundleStr.getString("Key_Str");
		myID = bundleStr.getString("Key_user");

		// add by Lawrence--2012/11/04
		myChooseYear = bundleStr.getInt("myChooseYear");
		myChooseMonth = bundleStr.getInt("myChooseMonth");
		myChooseDay = bundleStr.getInt("myChooseDay");

		Log.e("Lawrence", "action=" + action);
		Log.e("Lawrence", "myID=" + myID);

		// ----���Φr��----
		if (action.startsWith("groupinformation:")) {
			int loc0 = 0;

			action = action.substring(17); // ���N���Y�h��

			// ----�N�r��̬���(���Pgroup)���}----
			while (loc0 >= 0) {
				loc0 = action.indexOf(symbol0, 0); // �M��&&�X�{��index number
				if (loc0 >= 0) {
					actStr[cutStr] = action.substring(0, loc0);
					loc0 = loc0 + symbol0.length();
					action = action.substring(loc0);
					cutStr++;
					// ----�@��cutStr�Ӭ��ʸ�T----
				}
			} // end while

			showlayout();
			// buttonAct(); removed by kiwi @ 2011/7/5
		}
	}

	// private Button button_exit;

	private void showlayout() {

		LinearLayout layout = (LinearLayout) findViewById(R.id.ReceiveSuggestLayout);

		int loc1_1 = 0; // �ŧi���}�Ÿ��b�r�ꪺ��m�ܼ�
		int loc2_1 = 0;
		int loc2_2 = 0;
		int loc3_1 = 0;
		int loc3_2 = 0;
		int loc3_3 = 0;
		int loc3_4 = 0;

		// -----�ŧi����-----
		friendnum = new int[cutStr]; // �ظm�x�s�C��group�j�M��members�Ӽƪ��x�s����
		for (int i = 0; i < cutStr; i++) {

			friendnum[i] = 0; // ��l�Ƭ��s
		}

		destnum = new int[cutStr]; // �ظm�x�s�C��group�j�M������ө��Ӽƪ��x�s����
		for (int i = 0; i < cutStr; i++) {

			destnum[i] = 0; // ��l�Ƭ��s
		}

		// ----table�Ӽ�---
		TableLayout tablelayout[] = new TableLayout[cutStr];// �ظm�϶�(�϶��ƩMgroup�ƦP)
		for (int i = 0; i < cutStr; i++) {

			tablelayout[i] = new TableLayout(this);
		}

		// ----�C��table 50�C---
		TableRow row[][] = new TableRow[cutStr][50]; // �ظm�϶������C��
		for (int i = 0; i < 50; i++) {

			for (int j = 0; j < cutStr; j++) {

				row[j][i] = new TableRow(this);
			}
		}

		// ----����(group)���Ϲ�----
		ImageView image[] = new ImageView[cutStr];
		for (int i = 0; i < cutStr; i++) {

			image[i] = new ImageView(this);
		}

		// ----�C�Ӭ���(group)2��button----
		buttonexecute = new Button[cutStr][2];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < cutStr; j++) {
				buttonexecute[j][i] = new Button(this);
			}
		}

		// -----�C�Ӭ��ʨ�өT�wtext---
		TextView text[][] = new TextView[cutStr][2];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < cutStr; j++) {
				text[j][i] = new TextView(this);
			}
		}

		// -----���j�u----
		TextView texttool[] = new TextView[cutStr];
		for (int i = 0; i < cutStr; i++) {
			texttool[i] = new TextView(this);
			texttool[i].setText("-----------------------------------------------------------");
		}
		// ----���ʪ�title---- �]�w�T�Ӭ���(cutStr=3)
		texttitle = new TextView[cutStr];
		for (int i = 0; i < cutStr; i++) {
			texttitle[i] = new TextView(this);
			texttitle[i].setTextSize(25);
			// texttitle[i].setTextColor(R.color.green);
		}

		// ---��ܪ���S���B�ͪ��r��---
		nofri = new TextView[cutStr];
		for (int i = 0; i < cutStr; i++) {
			nofri[i] = new TextView(this);
			nofri[i].setTextColor(Color.RED);
			nofri[i].setText("����S���B�ͳ�!!!");
			nofri[i].setTextSize(20);
		}

		// ----����B��checkbox---
		choicefri = new CheckBox[cutStr][50];
		priority = new EditText[cutStr][50];
		for (int i = 0; i < 50; i++) {
			for (int j = 0; j < cutStr; j++) {
				choicefri[j][i] = new CheckBox(this);
				priority[j][i] = new EditText(this);
			}
		}
		// ---����B�͹Ϲ�---
		ImageView imagefri[][] = new ImageView[cutStr][20];
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < cutStr; j++) {
				imagefri[j][i] = new ImageView(this);
			}
		}

		// ---- ����B�ͪ�(�x�slocation)---
		Friendloc = new String[cutStr][50];

		// ------����a�I(������RadioGroup)----
		RadioGroup destinationGroup[] = new RadioGroup[cutStr];
		for (int i = 0; i < cutStr; i++) {

			destinationGroup[i] = new RadioGroup(this);
		}

		// ------����a�I(������RadioButton)----
		destination = new RadioButton[cutStr][20];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < cutStr; j++) {
				destination[j][i] = new RadioButton(this);
			}
		}

		// ----����a�I��location---
		Placeloc = new String[cutStr][20];
		Placetele = new String[cutStr][20];
		Placeaddress = new String[cutStr][20];

		// ---�C�Ӭ��ʪ�����----
		// �C�Ӭ��ʷ|�b�@��tablelayout���A�A�N�C��tablelayout�[�Jlayout
		for (int c = 0; c < cutStr; c++) // �C���^�����@��tablelayout(�@��group)�U���󪺥[�J
		{
			tablelayout[c].addView(texttool[c]);

			// �Ntitle���U(�̷�!�Ÿ�)�A�Y�����j�骺group�W��
			loc1_1 = actStr[c].indexOf(symbol1, 0);
			texttitle[c].setText(actStr[c].substring(0, loc1_1));
			texttitle[c].setTextColor(Color.BLACK);

			// �P�_title,��ܹ���group���Ϲ�
			if (texttitle[c].getText().equals("Snack")) {
				image[c].setImageDrawable(getResources().getDrawable(R.drawable.snack));
			} else if (texttitle[c].getText().equals("KTV")) {
				image[c].setImageDrawable(getResources().getDrawable(R.drawable.ktv));
			} else if (texttitle[c].getText().equals("Shopping")) {
				image[c].setImageDrawable(getResources().getDrawable(R.drawable.shopping));
			}

			row[c][0].addView(image[c]); // �Ĥ@�C�[�Jgroup�Ϥ�
			row[c][0].addView(texttitle[c]); // �Ĥ@�C�A�[�Jgroup�W��

			loc1_1 = loc1_1 + symbol1.length();
			actStr[c] = actStr[c].substring(loc1_1); // ���o�h��group�W�٫�A�ѤU��group��T

			// �]�w�C��group�T�w��ܪ�text���e
			text[c][0].setText("���񪺪B��:");
			text[c][0].setTextSize(20); // added by kiwi @2011/7/4
			text[c][0].setTextColor(Color.BLACK);
			text[c][1].setText("���񪺦a�I:");
			text[c][1].setTextSize(20); // added by kiwi @2011/7/4
			text[c][1].setTextColor(Color.BLACK);

			// �]�w�C��group�T�w��ܪ�button text���e
			buttonexecute[c][0].setText("�e�X�ܽ�");
			buttonexecute[c][1].setText("�d�ݦa��");

			row[c][1].addView(text[c][0]); // �N�ĤG�C�[�J"Nearby Friends:"

			// --button��ť---

			buttonexecute[c][0].setOnClickListener(actButton);
			buttonexecute[c][1].setOnClickListener(seemapButton);

			// -----����B�ͦW����ΡA�B�Ncheckbox�[�JGUI----
			int j = 0;
			int r = 0;
			int row_ctl = 0; // 0:���ݤU�@����ơA1:�N�ⵧ�g�Jtablerow���Cadded by kiwi
			int j_rec = 0; // �����_�Ƶ����̫�@�����
			while (loc2_1 >= 0) {
				TextView title = new TextView(this);
				title.setText("�v���G");
				title.setTextColor(Color.BLACK);

				loc2_1 = actStr[c].indexOf(symbol2_1, 0); // �M��@�X�{��index number
															// ->symbol2_1 =
															// "@";
				if (loc2_1 >= 0) { // �p�G�����

					choicefri[c][j].setText(actStr[c].substring(0, loc2_1)); // �]�m�C��members��checkbox
					choicefri[c][j].setTextColor(Color.BLUE);

					if (!(actStr[c].substring(0, loc2_1).equals(myID))) {
						// �]���e��C���[�J���F�A�ҥH�q2+r�C�}�l�[�J
						row[c][2 + r].addView(choicefri[c][j]);
						// 2012/10/06, Lawrence �W�[�v���]�w��J��
						row[c][2 + r].addView(title);
						priority[c][j].setHint("0");
						priority[c][j].setHintTextColor(Color.LTGRAY);
						row[c][2 + r].addView(priority[c][j]);

						r++;
						// �ϥΪ̦W�� 2 in 1 row
						// if (row_ctl == 1) {
						// row[c][2 + r].addView(choicefri[c][j - 1]);
						// row[c][2 + r].addView(priority[c][j - 1]);
						// row[c][2 + r].addView(choicefri[c][j]);
						// // 2012/10/06, Lawrence �W�[�v���]�w��J��
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
					friendnum[c]++; // �p��member��
					j++;
				}
			} // end while
			if (row_ctl == 1) {// �S��ƫ�Y���_�Ƶ���ƱN�̫�@���[�J added by kiwi @2011/7/4
				row[c][2 + r].addView(choicefri[c][j_rec]);
				r++;
			}
			// end of �ϥΪ̦W�� 2 in 1 row
			// Toast.makeText(Join5.this,actStr[c], Toast.LENGTH_SHORT).show();
			loc2_1 = 0; // ���F�U�Ӥ��Pgroup�j�骺����

			// String qq=Integer.toString(friendnum[c]);
			// Toast.makeText(Join5.this,qq, Toast.LENGTH_SHORT).show();
			if (friendnum[c] == 1) {
				// Toast.makeText(Join5.this,"�S�H", Toast.LENGTH_SHORT).show();
				row[c][2 + r].addView(nofri[c]);
				r++;
			}

			// -----����B��location���ΡA���x�s----
			int jj = 0;
			while (loc2_2 >= 0) {
				loc2_2 = actStr[c].indexOf(symbol2_2, 0); // �M��%�X�{��index number
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
			row[c][2 + r].addView(text[c][1]); // �b�[�J�Ҧ�near
												// friends�A���C�[�J"Nearest Place:"

			// -----����a�I���ΡA�å[�J�U�a�I��Radio button��Radio Group--------
			int k = 0;
			String des_index = null;
			while (loc3_1 >= 0) {
				loc3_1 = actStr[c].indexOf(symbol3_1, 0); // �M��#�X�{��index number
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
					destination[c][k].setText(des_index + " : " + actStr[c].substring(0, loc3_1)); // �]�wRadio
																									// button�W��(���a�W��)
					destination[c][k].setTextColor(Color.BLUE);
					destinationGroup[c].addView(destination[c][k]); // �N�����a��Radio
																	// button�[�J���j��(group)��Radio
																	// Group�A���GUI�W
					loc3_1 = loc3_1 + symbol3_1.length();
					actStr[c] = actStr[c].substring(loc3_1);
					destnum[c]++; // �p�ưӮa�Ӽ�
					k++;
				}
			} // end while
			loc3_1 = 0;
			// Toast.makeText(Join5.this,actStr[c], Toast.LENGTH_SHORT).show();

			// ----����a�Ilocation���ΡA���x�s-----
			int kkp = 0;
			while (loc3_2 >= 0) {
				loc3_2 = actStr[c].indexOf(symbol3_2, 0); // �M��*�X�{��index number
				if (loc3_2 >= 0) {
					Placeloc[c][kkp] = actStr[c].substring(0, loc3_2); // �x�slocation
					loc3_2 = loc3_2 + symbol3_2.length();
					actStr[c] = actStr[c].substring(loc3_2);
					kkp++;
				}
			} // end while
			loc3_2 = 0;

			// ----����a�I�q�ܤ��ΡA���x�s-----
			int kkt = 0;
			while (loc3_3 >= 0) {
				loc3_3 = actStr[c].indexOf(symbol3_3, 0); // �M��*�X�{��index number
				if (loc3_3 >= 0) {
					Placetele[c][kkt] = actStr[c].substring(0, loc3_3); // �x�slocation
					loc3_3 = loc3_3 + symbol3_3.length();
					actStr[c] = actStr[c].substring(loc3_3);
					kkt++;
				}
			} // end while
			loc3_3 = 0;

			// ----����a�I�a�}���ΡA���x�s-----
			int kka = 0;
			while (loc3_4 >= 0) {
				loc3_4 = actStr[c].indexOf(symbol3_4, 0); // �M��*�X�{��index number
				if (loc3_4 >= 0) {
					Placeaddress[c][kka] = actStr[c].substring(0, loc3_4); // �x�slocation
					loc3_4 = loc3_4 + symbol3_4.length();
					actStr[c] = actStr[c].substring(loc3_4);
					kka++;
				}
			} // end while
			loc3_4 = 0;

			if (!(friendnum[c] == 1)) {
				row[c][3 + r].addView(buttonexecute[c][0]); // �Nact button�[��GUI�W
			}
			row[c][3 + r].addView(buttonexecute[c][1]); // �Nseemap button�[��GUI�W

			// --�N�U�C(tablerow)���tablelayout��---
			tablelayout[c].addView(row[c][0]);
			tablelayout[c].addView(row[c][1]);

			for (int tl = 0; tl < r + 1; tl++) {
				tablelayout[c].addView(row[c][2 + tl]);
			}
			tablelayout[c].addView(destinationGroup[c]);
			tablelayout[c].addView(row[c][r + 3]);

			// �Ntablelayout��ө��layout��
			layout.addView(tablelayout[c]);

		}// end for loop

	}

	// ----���}Join5 button����ť------- removed by kiwi @ 2011/7/5
	// private void buttonAct() {
	// button_exit = (Button) findViewById(R.id.join5exit);
	// button_exit.setOnClickListener(exit);
	// }
	private Button.OnClickListener exit = new Button.OnClickListener() {
		public void onClick(View v) {
			FriendAndLocationList.this.finish();
		}
	};

	// ���Uact
	private Button.OnClickListener actButton = new Button.OnClickListener() {

		public void onClick(View v) {

			String bundlefri = "invitedfri:";
			String bundledest = null;
			Button actb = (Button) v;

			// �C��act�n�ǰe����T
			for (int b = 0; b < cutStr; b++) {
				// �P�_������group��act���s�A�u�|���@��button�Q��
				if (actb == buttonexecute[b][0]) {
					// Ū���ҭn�ܽЪ��B�ͦW��
					for (int f = 0; f < friendnum[b]; f++) {
						// �i��ܦh�ӳQ�ܽФH
						if (choicefri[b][f].isChecked()) {
							bundlefri += choicefri[b][f].getText() + "&";
						}

					}
					String txdestination;
					// Ū����ܪ��a�I
					for (int d = 0; d < destnum[b]; d++) {
						// �]���O���Radio button�A�ҥH�u�|���@�ӳQ��
						if (destination[b][d].isChecked()) {
							txdestination = destination[b][d].getText().toString().substring(4);
							bundledest = txdestination;
						}
					}

					// �ϥΪ̨S�ĥ���ﶵ�ɪ�����
					if (bundlefri.equals("invitedfri:") || bundledest == null) { // �S�ĳQ�ܽФH�εۨS�īإ߬��ʪ��������a

						// ���ĵ�i���
						new AlertDialog.Builder(FriendAndLocationList.this).setTitle(R.string.dialogtitle)
								.setMessage(R.string.dialogcontext)
								.setPositiveButton(R.string.dialogbutton, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {

									}
								}).show();
					} else {
						// ---��ƶǰe��Join6----
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
						WaitDialog.setTitle("���R��");
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
		// ---Join6 Send��,�NJoin5����
		case RESULT_OK:
			FriendAndLocationList.this.finish();
		}
	}

	// ���Useemap
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
