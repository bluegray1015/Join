package com.join;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.ItemizedOverlay;
import com.join.R;
import android.content.Context;
import android.telephony.TelephonyManager;

import android.app.Service;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import android.content.Intent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

public class JoinMapView extends MapActivity {
	public Bitmap mypic = null;
	public Bitmap despic = null;
	private LocationManager mLocationManager01;
	private String strLocationPrivider = "";
	public Location mLocation01 = null;
	private TextView mTextView01;
	private MapView mv;
	public GeoPoint currentGeoPoint;
	private int intZoomLevel = 20;
	private MarkerOverlay mOverlay2;
	// 建構一個命名為myself的MapUser物件來存放自己的資訊
	public MapUser myself = new MapUser();
	// 建構一個命名為alluser的AllUser物件來存放所有使用者的資訊
	public AllUser alluser = new AllUser();
	// 建構一個命名為alldest的AllDest物件來存放所有商家的資訊
	public AllDest alldest = new AllDest();
	/* 為了demo用 創假資料 */
	// public MapUser Kapin = new MapUser();
	// public MapUser Rebaca = new MapUser();
	// public MapUser LiChun = new MapUser();
	// public MapUser Alan = new MapUser();

	boolean CheckForChooseDestination = false;
	boolean TagForChooseDestination = false;
	public GeoPoint NewDestination = null;
	private String searchword;
	private String usersearchword;
	/* 氣象 */
	public Weather weatherdata;

	private String friendname;
	private String friendloc;
	private String desname;
	private String desloc;
	private String destele;
	private String desadd;
	private int friendnum;
	private int desnum;
	private String myID;

	class MarkerOverlay extends ItemizedOverlay<OverlayItem> {

		public GeoPoint currentGeoPoint_click;
		private double distance;
		Context mCtx;

		private List<OverlayItem> items = new ArrayList<OverlayItem>();

		public MarkerOverlay(Drawable defaultMarker, Context mCtx, GeoPoint currentGeoPoint) {
			super(boundCenterBottom(defaultMarker));
			this.mCtx = mCtx;
			items.clear();
			currentGeoPoint_click = currentGeoPoint;
			myself.geopoint = currentGeoPoint;
			myself.Latit = currentGeoPoint.getLatitudeE6();
			myself.Longit = currentGeoPoint.getLongitudeE6();
			// for(int i = 0;i<alluser.NumberOfUser();i++)
			// {
			// MapUser tempmapuser = alluser.UserArrayList.get(i);
			// items.add(new OverlayItem(tempmapuser.geopoint, tempmapuser.Name,
			// tempmapuser.StateString));
			// }
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return items.get(i);
		}

		@Override
		public int size() {
			return items.size();
		}

		public double GetDistance(GeoPoint gp1, GeoPoint gp2) {
			double Lat1r = ConvertDegreeToRadians(gp1.getLatitudeE6() / 1E6);
			double Lat2r = ConvertDegreeToRadians(gp2.getLatitudeE6() / 1E6);
			double Long1r = ConvertDegreeToRadians(gp1.getLongitudeE6() / 1E6);
			double Long2r = ConvertDegreeToRadians(gp2.getLongitudeE6() / 1E6);
			/* 地球半徑(KM) */
			double R = 6371;
			double d = Math.acos(Math.sin(Lat1r) * Math.sin(Lat2r) + Math.cos(Lat1r) * Math.cos(Lat2r)
					* Math.cos(Long2r - Long1r))
					* R;
			return d * 1000;
		}

		private double ConvertDegreeToRadians(double degrees) {
			return (Math.PI / 180) * degrees;
		}

		/* format移動距離的method */
		public String format(double num) {
			NumberFormat formatter = new DecimalFormat("###");
			String s = formatter.format(num);
			return s;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {

			if (TagForChooseDestination == true) {// 判斷是不是由選擇地圖而來的Touch事件
				if (event.getAction() == 1) {

					CheckForChooseDestination = true;
					NewDestination = mv.getProjection().fromPixels((int) event.getX(), (int) event.getY());
					;

					new AlertDialog.Builder(JoinMapView.this).setTitle("確定目的無誤")
							.setPositiveButton("確定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialoginterface, int i) {
									TagForChooseDestination = false;
									new AlertDialog.Builder(JoinMapView.this).setTitle("領隊更新目的地")
											.setPositiveButton("確定", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialoginterface, int i) {
												}
											}).show();
									myself.PrepareString = String.valueOf(NewDestination.getLatitudeE6()) + "-"
											+ String.valueOf(NewDestination.getLongitudeE6());
								}
							}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialoginterface, int i) {
									CheckForChooseDestination = false;
									TagForChooseDestination = false;
									new AlertDialog.Builder(JoinMapView.this).setTitle("請重新點選")
											.setPositiveButton("確定", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialoginterface, int i) {
												}
											}).show();
								}
							}).show();
				}
			}

			else {// 判斷是不是一般的Touch事件
				if (event.getAction() == 1) {

					for (int i = 0; i < alluser.NumberOfUser(); i++) {
						MapUser tempuser = alluser.UserArrayList.get(i);
						Point temppoint = mapView.getProjection().toPixels(tempuser.geopoint, null);
						float XpixelBetweenEventAndIcon = Math.abs(event.getX() - temppoint.x);
						float YpixelBetweenEventAndIcon = Math.abs(event.getY() - temppoint.y);
						if ((XpixelBetweenEventAndIcon < 35) && (YpixelBetweenEventAndIcon < 35)) {
							distance = GetDistance(myself.geopoint, tempuser.geopoint);
							if (format(distance) == "NaN") {
								distance = 0;
							}
							doTouchHeadIconAction(tempuser.Name, tempuser.StateString, distance, tempuser.PhoneNumber,
									R.drawable.man, tempuser.MyselfInt);
						}
					}

					for (int i = 0; i < alldest.NumberOfDest(); i++) {
						MapDest tempdest = alldest.DestArrayList.get(i);
						Point temppoint = mapView.getProjection().toPixels(tempdest.geopoint, null);
						float XpixelBetweenEventAndIcon = Math.abs(event.getX() - temppoint.x);
						float YpixelBetweenEventAndIcon = Math.abs(event.getY() - temppoint.y);
						if ((XpixelBetweenEventAndIcon < 35) && (YpixelBetweenEventAndIcon < 35)) {
							distance = GetDistance(myself.geopoint, tempdest.geopoint);
							if (format(distance) == "NaN") {
								distance = 0;
							}
							doTouchDesIconAction(tempdest.Name, tempdest.PhoneNumber, R.drawable.dish, distance,
									tempdest.Addrname);
						}
					}

				}
			}

			return false;
		}

		/***********************************************************
		 * 複寫函數：draw 功能： 重畫整個MapView上的圖示，所畫的圖示只包含alluser物件 內的user。
		 ***********************************************************/
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
			super.draw(canvas, mapView, shadow);

			if (CheckForChooseDestination) {
				Log.v("asdasdasdasdasd", "asdasdasd");
				Point flagPoint = new Point();
				Bitmap flagBit = null;

				flagBit = BitmapFactory.decodeResource(getResources(), R.drawable.flag);

				// if(myself.Competition == 2)
				// {
				// String[] ArrFlag = myself.PrepareString.split("-");
				//
				// if((ArrFlag[0]!=null)&&(ArrFlag[1]!=null))
				// {
				// GeoPoint flagGeo = new
				// GeoPoint(Integer.parseInt(ArrFlag[0]),Integer.parseInt(ArrFlag[1]));
				// mapView.getProjection().toPixels(flagGeo, flagPoint);
				// }
				// }
				// else
				// {
				mapView.getProjection().toPixels(NewDestination, flagPoint);
				// }
				canvas.drawBitmap(flagBit, flagPoint.x, flagPoint.y - 50, null);
			}

			int meindex = -1;
			if (myself.Name != null) {
				meindex = alluser.SearchUser(myself);
			}
			for (int i = 0; i < alluser.UserArrayList.size(); i++) {
				if (i != meindex) {
					drawuserpicture(canvas, mapView, i, mypic);

				}

			}

			for (int i = 0; i < alldest.DestArrayList.size(); i++) {
				drawdespicture(canvas, mapView, i, despic);
			}

			if ((myself.Name != null) && (meindex != -1)) {
				drawuserpicture(canvas, mapView, meindex, mypic);
			}
			return true;
		}

		public void doTouchDesIconAction(final String name, final String phoneNumber, int man, double distance,
				final String addrname) {

			new AlertDialog.Builder(JoinMapView.this)
					.setTitle(name)
					.setIcon(man)
					.setMessage(
							"地址 : " + addrname + "\n" + "距離 : " + format(distance) + "m" + "\n" + "電話 : " + phoneNumber
									+ "\n").setPositiveButton("打電話", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
							Intent myIntentDial = new Intent("android.intent.action.CALL");
							myIntentDial.setData(Uri.parse("tel:" + phoneNumber));
							startActivity(myIntentDial);
						}
					}).setNeutralButton("詳細資訊", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
							try {
								String searchdest_url = java.net.URLEncoder.encode(name.substring(3), "UTF-8");
								double lat = (myself.Latit / 1000000);
								double lon = (myself.Longit / 1000000);
								String str_lat = Double.toString(lat);
								String str_lon = Double.toString(lon);

								Intent intent = new Intent();
								intent.setAction(android.content.Intent.ACTION_VIEW);

								intent.setData(

								Uri.parse("http://maps.google.com.tw/maps?f=q&source=s_q&hl=zh-TW&"
										+ "geocode=&q="
										+ searchdest_url
										+ "+loc:+"
										+ str_lat
										+ ","
										+ str_lon
										+ "&sll=24.797215,120.999631&sspn=0.006827,0.01663&brcurrent=3,0x34683608af7574fd:0x9643333e378eabc8,0,0x34684a77b55ae7f7:0x5e4ce862e2a11ffb&"
										+ "ie=UTF8&ll=24.810421,120.98917&spn=0.113123,0.266075&z=13"));
								startActivity(intent);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}

						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
						}
					}).show();
		}

		private void doTouchHeadIconAction(final String strName, final String state, double distance,
				final String strPhone, int iIconResId, int myselfint) {
			if (myselfint == 1) {
				new AlertDialog.Builder(JoinMapView.this)
						.setTitle(strName)
						.setIcon(iIconResId)
						.setMessage(
								"狀態 : " + state + "\n" + "距離 : " + format(distance) + "m" + "\n" + "電話 : " + strPhone
										+ "\n").setPositiveButton("更換顯圖", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
								Intent intent = new Intent();

								/* 開啟Pictures畫面Type設定為image */
								intent.setType("image/*");

								/* 使用Intent.ACTION_GET_CONTENT這個Action */
								intent.setAction(Intent.ACTION_GET_CONTENT);

								/* 取得相片後返回本畫面 */
								startActivityForResult(intent, 1);
							}
						}).setNeutralButton("更換狀態", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {

								LayoutInflater user_factory = LayoutInflater.from(JoinMapView.this);
								View changestate_dialog = user_factory.inflate(R.layout.changestate, null);
								final EditText statetext = (EditText) changestate_dialog
										.findViewById(R.id.changestateEdit);
								statetext.setText(myself.StateString);
								int iIconResIdchangestate = R.drawable.man;
								new AlertDialog.Builder(JoinMapView.this).setView(changestate_dialog).setTitle("請輸入新狀態")
										.setIcon(iIconResIdchangestate)

										.setPositiveButton("確定", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialoginterface, int i) {
												myself.StateString = statetext.getText().toString();

											}
										})

										.setNegativeButton("取消", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialoginterface, int i) {

											}
										}).show();

							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
							}
						}).show();

			} else {
				new AlertDialog.Builder(JoinMapView.this)
						.setTitle(strName)
						.setIcon(iIconResId)
						.setMessage(
								"狀態 : " + state + "\n" + "距離 : " + format(distance) + "m" + "\n" + "電話 : " + strPhone
										+ "\n").setPositiveButton("打電話", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
								Intent myIntentDial = new Intent("android.intent.action.CALL");
								myIntentDial.setData(Uri.parse("tel:" + strPhone));
								startActivity(myIntentDial);
							}
						}).setNeutralButton("傳簡訊", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
								Uri uri = Uri.parse("smsto:" + strPhone);
								Intent it = new Intent(Intent.ACTION_SENDTO, uri);
								it.putExtra("sms_body", "Hi,I am near you!");
								startActivity(it);
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
							}
						}).show();
			}
		}

	}

	/***********************************************************
	 * 變數名稱： 宣告五個Menu物件作為按鈕。 功能： 分別替他們加上編號作為判斷按下哪個之用。
	 ***********************************************************/
	public static final int Menu0 = Menu.FIRST;
	public static final int Menu1 = Menu.FIRST + 1;
	public static final int Menu2 = Menu.FIRST + 2;
	public static final int Menu3 = Menu.FIRST + 3;
	public static final int Menu4 = Menu.FIRST + 4;
	public static final int Menu5 = Menu.FIRST + 5;

	public void populateMenu(Menu menu) {
		menu.setQwertyMode(true);
		MenuItem item0 = menu.add(0, Menu0, 0, "更換領隊");
		{
			item0.setIcon(R.drawable.menu_icon_changeleader);
		}
		MenuItem item1 = menu.add(0, Menu1, 0, "狀況發布");
		{
			item1.setIcon(R.drawable.menu_icon_alarm);
		}
		MenuItem item2 = menu.add(0, Menu2, 0, "行程資訊");
		{
			item2.setIcon(R.drawable.menu_icon_weather);
		}
		MenuItem item3 = menu.add(0, Menu3, 0, "聊天室");
		{
			item3.setIcon(R.drawable.menu_icon_changepic);
		}
		MenuItem item4 = menu.add(0, Menu4, 0, "搜尋地圖");
		{
			item4.setIcon(R.drawable.menu_icon_reservation);
		}
		MenuItem item6 = menu.add(0, Menu5, 0, "選擇目的地");
		{
			item6.setIcon(R.drawable.menu_icon_changedest);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	// TouchMenuItem
	// 選擇了哪個 Menu Item
	public boolean onOptionsItemSelected(MenuItem item) {
		// 按下了第幾個 Menu Item
		switch (item.getItemId()) {

		case 1:// 更換Leader TouchMenuItem3
			Log.d("==ButtonSelect==3==", "==ButtonSelect==3==");
			if ((myself.State == 1) || (myself.State == 3)) {
				final ArrayAdapter<String> adapter;

				List<String> UsersStr = new ArrayList<String>();
				for (int j = 0; j < alluser.NumberOfUser(); j++) {
					if (!alluser.UserArrayList.get(j).Name.equals(myself.Name)) {
						UsersStr.add(alluser.UserArrayList.get(j).Name);
					}
				}
				adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, UsersStr);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				LayoutInflater factory = LayoutInflater.from(JoinMapView.this);
				View changeleaderdialog = factory.inflate(R.layout.info, null);

				final Spinner mySpinner = (Spinner) changeleaderdialog.findViewById(R.id.Spinner01);
				mySpinner.setAdapter(adapter);

				new AlertDialog.Builder(JoinMapView.this).setTitle("更換領隊").setView(changeleaderdialog)
						.setPositiveButton("確定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
								String chooseuser = mySpinner.getSelectedItem().toString();
								myself.ChangeLeader = 1;
								myself.MsgOfNewLeader = chooseuser;
								if (myself.State == 1) {
									myself.State = 0;
								}
								if (myself.State == 3) {
									myself.State = 2;
								}
								for (int ii = 0; ii < alluser.NumberOfUser(); ii++) {
									if (alluser.UserArrayList.get(ii).Name == chooseuser) {
										alluser.UserArrayList.get(ii).State = 1;
									}
								}
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
							}
						}).show();
			} else {
				new AlertDialog.Builder(JoinMapView.this).setTitle("此為領隊專屬功能")
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
							}
						}).show();
			}
			break;
		case 2:// 緊急通報 TouchMenuItem4
			Log.d("==ButtonSelect==4==", "==ButtonSelect==4==");
			AlertDialog.Builder emergency = new AlertDialog.Builder(JoinMapView.this);

			emergency.setTitle("發布通報");
			emergency.setMessage("是否發布通報");

			if ((myself.State == 0) || (myself.State == 1)) {
				emergency.setPositiveButton("遇到狀況", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						switch (myself.State) {
						case 0:
							myself.State = 2;// 隊員流血
							break;
						case 1:
							myself.State = 3;// 隊長流血
							break;
						}
					}
				});
			} else if ((myself.State == 2) || (myself.State == 3)) {
				emergency.setPositiveButton("沒事了", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						switch (myself.State) {
						case 2:
							myself.State = 0;
							break;
						case 3:
							myself.State = 1;
							break;
						}
					}
				});
			}
			if (myself.Slow == 0) {
				emergency.setNeutralButton("車況阻塞", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						myself.Slow = 1;
					}
				});
			} else if (myself.Slow == 1) {
				emergency.setNeutralButton("一路順風", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						myself.Slow = 0;
					}
				});
			}
			emergency.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i) {
				}
			}).show();
			break;
		case 3:

			int iIconResId = R.drawable.man;
			new AlertDialog.Builder(JoinMapView.this).setTitle("請選擇").setIcon(iIconResId)
					.setMessage("天氣狀況:查詢各地天氣狀況\n" + "路徑規畫:輸入起點,終點,交通工具來進行路徑規畫")

					.setPositiveButton("天氣狀況", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {

							// weatherdata=new Weather(currentGeoPoint);

							Intent intent = new Intent();
							intent.setClass(JoinMapView.this, Weather.class);

							Bundle bundle = new Bundle();
							bundle.putString("currentLat", Integer.toString(currentGeoPoint.getLatitudeE6()));
							bundle.putString("currentLong", Integer.toString(currentGeoPoint.getLongitudeE6()));

							intent.putExtras(bundle);
							startActivity(intent);
							//
							// try {
							// Thread.sleep(4000);
							// int iIconResId3_weather=R.drawable.man;
							// openDialog2(weatherdata.array_condition[0]);
							// new AlertDialog.Builder
							// (Join5_2.this).setTitle("天氣狀況").setIcon(iIconResId3_weather).setMessage
							// (" 現在\n"
							//
							// +"天氣 : "+weatherdata.array_condition[0]+"\n"+
							// "溫度 : "+weatherdata.array_tempc[0]+"度c , "+weatherdata.array_tempf[0]+"度f"+"\n"+
							// "濕度 : "+weatherdata.array_humidity[0]+"\n"+
							// "風 : "+weatherdata.array_wind[0]+"\n"+
							// "本周\n" +
							// weatherdata.array_dayofweek[0]+" : "+weatherdata.array_condition[1]+"\n"+
							// "溫度 : "+weatherdata.array_low[0]+"度c - "+weatherdata.array_high[0]+"度c"+"\n"+
							// weatherdata.array_dayofweek[1]+" : "+weatherdata.array_condition[2]+"\n"+
							// "溫度 : "+weatherdata.array_low[1]+"度c - "+weatherdata.array_high[1]+"度c"+"\n"+
							// weatherdata.array_dayofweek[2]+" : "+weatherdata.array_condition[3]+"\n"+
							// "溫度 : "+weatherdata.array_low[2]+"度c - "+weatherdata.array_high[2]+"度c"+"\n"+
							// weatherdata.array_dayofweek[3]+" : "+weatherdata.array_condition[4]+"\n"+
							// "溫度 : "+weatherdata.array_low[3]+"度c - "+weatherdata.array_high[3]+"度c"
							// )
							//
							// .setNeutralButton("確定",
							// new DialogInterface.OnClickListener()
							// {
							// public void onClick(DialogInterface
							// dialoginterface,int i)
							// {
							//
							// }
							// })
							//
							// .show();
							// } catch (InterruptedException e) {
							//
							// e.printStackTrace();
							// }

						}
					})

					.setNeutralButton("路徑規畫", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
							Intent intent = new Intent();
							intent.setAction(android.content.Intent.ACTION_VIEW);

							/* 傳入路徑規劃所需要的地標位址 */

							intent.setData(

							Uri.parse("http://maps.google.com/maps?f=d&saddr=" + "" + "&daddr=" + "" + "&hl=tw"));
							startActivity(intent);

						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
						}
					}).show();

			break;
		case 4:

			break;

		case 5:
			LayoutInflater factory = LayoutInflater.from(JoinMapView.this);
			View searchdialog = factory.inflate(R.layout.search, null);
			final Spinner spinner = (Spinner) searchdialog.findViewById(R.id.search_array);
			final ArrayAdapter<CharSequence> adapter;
			adapter = ArrayAdapter.createFromResource(this, R.array.search_word_array,
					android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			// 設定項目被選取之後的動作
			spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
				public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
					searchword = adapterView.getSelectedItem().toString();
				}

				public void onNothingSelected(AdapterView arg0) {
					searchword = null;
				}
			});

			int iIconResId5 = R.drawable.man;
			new AlertDialog.Builder(JoinMapView.this).setView(searchdialog).setTitle("搜尋地圖").setIcon(iIconResId5)
					.setMessage("利用內建關鍵字或自訂關鍵字來搜尋地圖")

					.setPositiveButton("內建搜尋", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {

							try {
								String searchword_url = java.net.URLEncoder.encode(searchword, "UTF-8");
								double lat = (myself.Latit / 1000000);
								double lon = (myself.Longit / 1000000);
								String str_lat = Double.toString(lat);
								String str_lon = Double.toString(lon);

								Intent intent = new Intent();
								intent.setAction(android.content.Intent.ACTION_VIEW);

								intent.setData(

								Uri.parse("http://maps.google.com.tw/maps?f=q&source=s_q&hl=zh-TW&"
										+ "geocode=&q="
										+ searchword_url
										+ "+loc:+"
										+ str_lat
										+ ","
										+ str_lon
										+ "&sll=24.797215,120.999631&sspn=0.006827,0.01663&brcurrent=3,0x34683608af7574fd:0x9643333e378eabc8,0,0x34684a77b55ae7f7:0x5e4ce862e2a11ffb&"
										+ "ie=UTF8&ll=24.810421,120.98917&spn=0.113123,0.266075&z=13"));
								startActivity(intent);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}

						}
					}).setNeutralButton("自訂搜尋", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
							LayoutInflater user_factory = LayoutInflater.from(JoinMapView.this);
							View search_user_dialog = user_factory.inflate(R.layout.usersearch, null);
							final EditText usertext = (EditText) search_user_dialog.findViewById(R.id.usersearchEdit);
							int iIconResId5_5 = R.drawable.man;
							new AlertDialog.Builder(JoinMapView.this).setView(search_user_dialog).setTitle("請輸入關鍵字")
									.setIcon(iIconResId5_5).setMessage("自訂關鍵字來搜尋地圖")

									.setPositiveButton("自訂搜尋", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialoginterface, int i) {
											usersearchword = usertext.getText().toString();
											try {
												String usersearchword_url = java.net.URLEncoder.encode(usersearchword,
														"UTF-8");
												/* 傳入使用者的地標位址 */
												double lat = (myself.Latit / 1000000);
												double lon = (myself.Longit / 1000000);
												String str_lat = Double.toString(lat);
												String str_lon = Double.toString(lon);

												Intent intent = new Intent();
												intent.setAction(android.content.Intent.ACTION_VIEW);

												intent.setData(

												Uri.parse("http://maps.google.com.tw/maps?f=q&source=s_q&hl=zh-TW&"
														+ "geocode=&q="
														+ usersearchword_url
														+ "+loc:+"
														+ str_lat
														+ ","
														+ str_lon
														+ "&sll=24.797215,120.999631&sspn=0.006827,0.01663&brcurrent=3,0x34683608af7574fd:0x9643333e378eabc8,0,0x34684a77b55ae7f7:0x5e4ce862e2a11ffb&"
														+ "ie=UTF8&ll=24.810421,120.98917&spn=0.113123,0.266075&z=13"));
												startActivity(intent);
											} catch (UnsupportedEncodingException e) {
												e.printStackTrace();
											}

										}
									})

									.setNegativeButton("取消", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialoginterface, int i) {

										}
									}).show();

						}
					})

					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {

						}
					}).show();

			break;
		case 6:
			if ((myself.State == 1) || (myself.State == 3)) {
				int iIconResId6 = R.drawable.man;
				new AlertDialog.Builder(JoinMapView.this).setTitle("請選擇(限領隊)").setIcon(iIconResId6)
						.setMessage("點選目的地:在地圖上點選目的地\n" + "我的資料庫:從個人資料庫中加入目的地")

						.setPositiveButton("點選目的地", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
								TagForChooseDestination = true;
								Toast.makeText(getBaseContext(), "請在地圖上點選新目的地", Toast.LENGTH_LONG).show();
							}
						}).setNeutralButton("我的資料庫", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
								Intent intent = new Intent();
								intent.setClass(JoinMapView.this, Start.class);
								startActivity(intent);
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
							}
						}).show();
			} else {
				new AlertDialog.Builder(JoinMapView.this).setTitle("此為領隊專屬功能")
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {
							}
						}).show();
			}

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		/* 整理data */
		dataimport();
		setContentView(R.layout.join_map_view);
		mTextView01 = (TextView) findViewById(R.id.myTextView1);
		/* 建立MapView物件 */
		mv = (MapView) findViewById(R.id.myMapView1);

		/* 建立LocationManager物件取得系統LOCATION服務 */
		mLocationManager01 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		/* 第一次執行向Location Provider取得Location */
		mLocation01 = getLocationPrivider(mLocationManager01);

		if (mLocation01 != null) {
			processLocationUpdated(mLocation01);
		} else {
			mTextView01.setText(getResources().getText(R.string.str_err_location).toString());
		}
		/* 建立LocationManager物件，監聽Location變更時事件，更新MapView */
		mLocationManager01.requestLocationUpdates(strLocationPrivider, 2000, 10, mLocationListener01);

		// 取得本機號碼
		TelephonyManager tm = (TelephonyManager) this.getSystemService(Service.TELEPHONY_SERVICE);
		String mytel_num = tm.getLine1Number();
		if (mytel_num != null)
			myself.PhoneNumber = mytel_num;

	}

	public final LocationListener mLocationListener01 = new LocationListener() {

		public void onLocationChanged(Location location) {
			/* 當手機收到位置變更時，將location傳入取得地理座標 */
			processLocationUpdated(location);
		}

		public void onProviderDisabled(String provider) {

			/* 當Provider已離開服務範圍時 */
		}

		public void onProviderEnabled(String provider) {

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	};

	public String getAddressbyGeoPoint(GeoPoint gp) {
		String strReturn = "";
		try {
			/* 當GeoPoint不等於null */
			if (gp != null) {
				/* 建立Geocoder物件 */
				Geocoder gc = new Geocoder(JoinMapView.this, Locale.getDefault());

				/* 取出地理座標經緯度 */
				double geoLatitude = (int) gp.getLatitudeE6() / 1E6;
				double geoLongitude = (int) gp.getLongitudeE6() / 1E6;

				/* 自經緯度取得地址（可能有多行地址） */
				List<Address> lstAddress = gc.getFromLocation(geoLatitude, geoLongitude, 1);
				StringBuilder sb = new StringBuilder();

				/* 判斷地址是否為多行 */
				if (lstAddress.size() > 0) {
					Address adsLocation = lstAddress.get(0);

					for (int i = 0; i < adsLocation.getMaxAddressLineIndex(); i++) {
						sb.append(adsLocation.getAddressLine(i)).append("\n");
					}
					sb.append(adsLocation.getLocality()).append("\n");
					sb.append(adsLocation.getPostalCode()).append("\n");
					sb.append(adsLocation.getCountryName());
				}

				/* 將擷取到的地址，組合後放在StringBuilder物件中輸出用 */
				strReturn = sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strReturn;
	}

	public Location getLocationPrivider(LocationManager lm) {
		Location retLocation = null;
		try {
			Criteria mCriteria01 = new Criteria();
			mCriteria01.setAccuracy(Criteria.ACCURACY_FINE);
			mCriteria01.setAltitudeRequired(false);
			mCriteria01.setBearingRequired(false);
			mCriteria01.setCostAllowed(true);
			mCriteria01.setPowerRequirement(Criteria.POWER_LOW);
			strLocationPrivider = lm.getBestProvider(mCriteria01, true);
			retLocation = lm.getLastKnownLocation(strLocationPrivider);
		} catch (Exception e) {
			mTextView01.setText(e.toString());
			e.printStackTrace();
		}
		return retLocation;
	}

	private GeoPoint getGeoByLocation(Location location) {
		GeoPoint gp = null;
		try {
			/* 當Location存在 */
			if (location != null) {
				double geoLatitude = location.getLatitude() * 1E6;
				double geoLongitude = location.getLongitude() * 1E6;
				gp = new GeoPoint((int) geoLatitude, (int) geoLongitude);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gp;
	}

	private MyLocationOverlay mylayer;
	private MapController mc;

	public void refreshMapViewByGeoPoint(GeoPoint gp, MapView mv, int zoomLevel, boolean bIfSatellite) {
		// try
		// {
		mv.displayZoomControls(true);
		/* 取得MapView的MapController */
		MapController mc = mv.getController();
		/* 移至該地理座標位址 */
		mc.animateTo(gp);
		mv.setBuiltInZoomControls(true);
		/* 放大地圖層級 */
		mc.setZoom(15);

		/* 延伸學習：取得MapView的最大放大層級 */
		// mv.getMaxZoomLevel()

		/* 設定MapView的顯示選項（衛星、街道） */

		mv.setSatellite(false);
		mv.setStreetView(false);
		mv.setTraffic(true);
		List<Overlay> overlays = mv.getOverlays();
		overlays.clear();
		mylayer = new MyLocationOverlay(this, mv);
		mylayer.runOnFirstFix(new Runnable() {
			public void run() {
				// Zoom in to current location
				MapView mv = (MapView) findViewById(R.id.myMapView1);
				MapController mc = mv.getController();
				mc.setZoom(15);
				mc.animateTo(mylayer.getMyLocation());
			}
		});
		overlays.add(mylayer);
		Drawable pin = getResources().getDrawable(android.R.drawable.btn_star_big_on);
		pin.setBounds(0, 0, pin.getMinimumWidth(), pin.getMinimumHeight());

		Drawable marker = getResources().getDrawable(R.drawable.man);
		mOverlay2 = new MarkerOverlay(marker, this, currentGeoPoint);
		overlays.add(mOverlay2);
	}

	// catch(Exception e)
	// {
	// e.printStackTrace();
	// }
	// }
	/* 當手機收到位置變更時，將location傳入更新當下GeoPoint及MapView */
	private void processLocationUpdated(Location location) {
		/* 傳入Location物件，取得GeoPoint地理座標 */
		currentGeoPoint = getGeoByLocation(location);

		/* 更新MapView顯示Google Map */
		refreshMapViewByGeoPoint(currentGeoPoint, mv, intZoomLevel, true);

		mTextView01.setText(getResources().getText(R.string.str_my_location).toString() + "\n" +
		/* 延伸學習：取出GPS地理座標： */

		getResources().getText(R.string.str_longitude).toString()
				+ String.valueOf((int) currentGeoPoint.getLongitudeE6() / 1E6) + "\n"
				+ getResources().getText(R.string.str_latitude).toString()
				+ String.valueOf((int) currentGeoPoint.getLatitudeE6() / 1E6) + "\n" +

				getAddressbyGeoPoint(currentGeoPoint));
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void drawuserpicture(Canvas canvas, MapView mapView, int i, Bitmap mypic) {

		Point screenPts = new Point();

		MapUser temp = null;
		if (i < alluser.UserArrayList.size()) {
			temp = alluser.UserArrayList.get(i);
		} else {
			return;
		}
		mapView.getProjection().toPixels(temp.geopoint, screenPts);
		Bitmap hatdraw = null;
		Bitmap another = null;
		Bitmap slow = null;
		Bitmap tempuserpic = null;
		if (temp.MyselfInt == 1 && mypic != null) {
			tempuserpic = mypic;
		} else {
			tempuserpic = BitmapFactory.decodeResource(getResources(), R.drawable.man);
			;
		}
		canvas.drawBitmap(tempuserpic, screenPts.x - 20, screenPts.y - 15, null);
		switch (temp.State) {
		case 0:
			hatdraw = BitmapFactory.decodeResource(getResources(), R.drawable.memberhat);
			break;
		case 1:
			hatdraw = BitmapFactory.decodeResource(getResources(), R.drawable.leaderhat);
			break;
		case 2:
			hatdraw = BitmapFactory.decodeResource(getResources(), R.drawable.memberhat);
			another = BitmapFactory.decodeResource(getResources(), R.drawable.blood);
			break;
		case 3:
			hatdraw = BitmapFactory.decodeResource(getResources(), R.drawable.leaderhat);
			another = BitmapFactory.decodeResource(getResources(), R.drawable.blood);
			break;
		}

		canvas.drawBitmap(hatdraw, screenPts.x - 13, screenPts.y - 28, null);

		if (another != null)// 補畫有狀況的狀況
		{
			canvas.drawBitmap(another, screenPts.x - 12, screenPts.y + 15, null);
		}
		if (temp.Slow == 1)// 補畫塞車的時候的圖示
		{
			slow = BitmapFactory.decodeResource(getResources(), R.drawable.perspiration);
			canvas.drawBitmap(slow, screenPts.x + 5, screenPts.y + 15, null);
		}
	}

	public void drawdespicture(Canvas canvas, MapView mapView, int i, Bitmap despic) {

		Point screenPts = new Point();

		MapDest temp = null;
		if (i < alldest.DestArrayList.size()) {
			temp = alldest.DestArrayList.get(i);
		} else {
			return;
		}
		mapView.getProjection().toPixels(temp.geopoint, screenPts);
		Bitmap tempuserpic = null;
		// 把8個可能目的地圖匯入
		Bitmap[] despic_array = new Bitmap[8];
		despic_array[0] = BitmapFactory.decodeResource(getResources(), R.drawable.des_a);
		despic_array[1] = BitmapFactory.decodeResource(getResources(), R.drawable.des_b);
		despic_array[2] = BitmapFactory.decodeResource(getResources(), R.drawable.des_c);
		despic_array[3] = BitmapFactory.decodeResource(getResources(), R.drawable.des_d);
		despic_array[4] = BitmapFactory.decodeResource(getResources(), R.drawable.des_e);
		despic_array[5] = BitmapFactory.decodeResource(getResources(), R.drawable.des_f);
		despic_array[6] = BitmapFactory.decodeResource(getResources(), R.drawable.des_g);
		despic_array[7] = BitmapFactory.decodeResource(getResources(), R.drawable.des_h);
		if (despic != null) {
			tempuserpic = despic;
		} else {
			tempuserpic = despic_array[i];
		}
		canvas.drawBitmap(tempuserpic, screenPts.x, screenPts.y, null);
	}

	public void openDialog2(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			ContentResolver cr = this.getContentResolver();

			try {
				Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

				mypic = resizeImage(bitmap, 80, 100);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {

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

		return resizedBitmap;

	}

	public void dataimport() {

		Bundle bundle = this.getIntent().getExtras();

		friendnum = Integer.valueOf(bundle.getString("bundlefriendnum"));
		desnum = Integer.valueOf(bundle.getString("bundledestnum"));
		/* 將合併的字串切開 */
		if (friendnum != 0) {
			myID = bundle.getString("myID");
			friendname = bundle.getString("bundlefri");
			friendloc = bundle.getString("bundlefriloc");
			String[] friendnametokens = friendname.split("&");
			String[] friendloctokens = friendloc.split("&");
			double[] friendloclat = new double[friendnum];
			double[] friendloclong = new double[friendnum];
			for (int i = 0; i < friendloctokens.length; i++) {
				String[] msg = friendloctokens[i].split(",");
				msg[0] = msg[0].substring(1);
				friendloclat[i] = Double.valueOf(msg[0]);
				friendloclat[i] = friendloclat[i] * 1000000;
				friendloclong[i] = Double.valueOf(msg[1]);
				friendloclong[i] = friendloclong[i] * 1000000;
			}
			MapUser[] allmapuser = new MapUser[friendnum];
			for (int i = 0; i < friendnum; i++) {
				allmapuser[i] = new MapUser(); /* 幹 沒加這行會錯 */

				if (friendnametokens[i].equals(myID)) {
					myself.AsignInfo(friendnametokens[i], "0932961079", myID, "第一次用JOIN > <", null, 1, friendloclat[i],
							friendloclong[i], 1, 0, 0);
					alluser.AddUser(myself);
				} else {
					allmapuser[i].AsignInfo(friendnametokens[i], "0988588091", allmapuser[i].Name, "豬哥亮真是好看", null, 0,
							friendloclat[i], friendloclong[i], 0, 0, 0);
					alluser.AddUser(allmapuser[i]);
				}

			}
		}
		if (desnum != 0) {
			desname = bundle.getString("bundledes");
			desloc = bundle.getString("bundledesloc");
			destele = bundle.getString("bundledestele");
			desadd = bundle.getString("bundledesadd");

			String[] desnametokens = desname.split("&");
			String[] desloctokens = desloc.split("&");

			String[] desteletokens = destele.split("&");
			String[] desaddtokens = desadd.split("&");

			double[] desloclat = new double[desnum];
			double[] desloclong = new double[desnum];

			for (int i = 0; i < desloctokens.length; i++) {
				String[] msg = desloctokens[i].split(",");
				msg[0] = msg[0].substring(1);
				desloclat[i] = Double.valueOf(msg[0]);
				desloclat[i] = desloclat[i] * 1000000;
				desloclong[i] = Double.valueOf(msg[1]);
				desloclong[i] = desloclong[i] * 1000000;
			}
			MapDest[] allmapdest = new MapDest[desnum];
			for (int i = 0; i < desnum; i++) {
				if (desteletokens[i].equals("null"))
					desteletokens[i] = "無資料";
				if (desaddtokens[i].equals("null"))
					desaddtokens[i] = "無資料";
				allmapdest[i] = new MapDest(); /* 幹 沒加這行會錯 */
				allmapdest[i].AsignInfo(desnametokens[i], desteletokens[i], desloclat[i], desloclong[i],
						desaddtokens[i]);
				alldest.AddDest(allmapdest[i]);
			}
		}

		// Toast.makeText(Join5_2.this, friendname, Toast.LENGTH_SHORT).show();
		// Toast.makeText(Join5_2.this, friendloc, Toast.LENGTH_SHORT).show();
		// Toast.makeText(Join5_2.this, desname, Toast.LENGTH_SHORT).show();
		// Toast.makeText(Join5_2.this, desloc, Toast.LENGTH_SHORT).show();
		// Toast.makeText(Join5_2.this, desloc, Toast.LENGTH_SHORT).show();
		// Toast.makeText(Join5_2.this, desloc, Toast.LENGTH_SHORT).show();

		// /*為了demo用 創假資料*/
		// Kapin.AsignInfo("Taco", "0910696990","我自己"
		// ,"Regular life-It's very intensive",null, 0, 25066608,121532569,
		// 0,0,0);
		// Rebaca.AsignInfo("Yi-Ting", "0910032074","我自己" ,"認真體會生活中的美好",null
		// ,0,25099908,121522569, 0,0,0);
		// LiChun.AsignInfo("LiChun", "0933919802","我自己"
		// ,"教授捕手部落格 -http://tw.myblog.yahoo.com/wanglichuntw/",null, 0,
		// 25078808,121524000, 0,0,0);
		// Alan.AsignInfo("Alan", "0932961079", "我自己","要去高雄玩摟~~",null ,0,
		// 25070008,121519569, 0,0,0);
		// myself.AsignInfo("myself", "090000000","我自己", "第一次用 JOIN ^.^",
		// null,1,0,0,1,0,0);
		// Taco.AsignInfo("章魚", "0988973613", "我自己","認真做自己~~最愛老公了",null ,0,
		// 24791280,121001569, 0,0,0);
		// /*將假資料放到array*/
		// alluser.AddUser(Kapin);
		// alluser.AddUser(Rebaca);
		// alluser.AddUser(LiChun);
		// alluser.AddUser(Alan);
		// alluser.AddUser(Taco);
		// alluser.AddUser(myself);
	}
}
