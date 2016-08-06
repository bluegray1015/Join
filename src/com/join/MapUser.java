package com.join;

import com.google.android.maps.GeoPoint;

public class MapUser 
{
	public String Name;//名字
	public String PhoneNumber;//電話號碼
	public String MsgOfNewLeader;
	public String StateString;
	public String PrepareString;
	public int State;//是不是隊長狀態
	public double Latit;//緯度
	public double Longit;//經度
	public int MyselfInt;//判斷是不是自己
	GeoPoint geopoint;
	public boolean flag;//用來判斷是否在線上
	public int ChangeLeader;//換隊長
	public int Slow;//塞車
	
	/***********************************************************
	 * Function Name : CombineMessage
	 * Parameter : void
	 * Return : String 回傳組好的訊息。
	 * Result : 回傳組好的訊息。
	 ***********************************************************/
	public String CombineMessage()
	{
		String message = Name
		+ "/" + PhoneNumber
		+ "/" + MsgOfNewLeader
		+ "/" + PrepareString
		+ "/" + String.valueOf(ChangeLeader)
		+ "/" + String.valueOf(State) 
		+ "/" + String.valueOf(Latit) 
		+ "/" + String.valueOf(Longit)
		+ "/" + String.valueOf(MyselfInt);
		return message;
	}
	/***********************************************************
	 * Function Name : AsignInfo
	 * Parameter : 		public String Name;//名字
						public String PhoneNumber;//電話號碼
						public String PrepareString;
						public int State;//狀態
						public int Latit;//緯度
						public int Longit;//經度
						public int MyselfInt;
	 * Return : void
	 * Result : 將資訊給放入MapUser物件中，並且設定好geopoint。
	 ***********************************************************/
	public void AsignInfo(String name, String phonenumber, String newleader ,String stateString, String preparestring, int state, double lati, double longi, int myselfint,int changeleader, int slow)
	{
		flag = true;
		Name = name;
		PhoneNumber = phonenumber;
		MsgOfNewLeader=newleader;
		StateString =stateString;
		PrepareString = preparestring;
		State = state;
		Latit = lati;
		Longit = longi;
		MyselfInt = myselfint;
		geopoint = new GeoPoint((int)lati , (int)longi);
		ChangeLeader=changeleader;
		Slow=slow;
	}
	
	public void AsignInfoWithoutGeoPoint(String name, String phonenumber,String newleader, String stateString,  String preparestring, int state, double lati, double longi, int myselfint,int changeleader,int slow)
	{
		flag = true;
		Name = name;
		PhoneNumber = phonenumber;
		MsgOfNewLeader=newleader;
		StateString = stateString;
		PrepareString = preparestring;
		State = state;
		Latit = lati;
		Longit = longi;
		MyselfInt = myselfint;
		ChangeLeader=changeleader;
		Slow=slow;
	//	geopoint = new GeoPoint((int)lati*1000000 , (int)longi*1000000);
	}
	
}
