package com.join;

import com.google.android.maps.GeoPoint;

public class MapDest
{
	public String Name;//名字
	public String PhoneNumber;//電話號碼
	public String Addrname;//中文地址
	public double Latit;//緯度
	public double Longit;//經度
	GeoPoint geopoint;

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
		+ "/" + String.valueOf(Latit) 
		+ "/" + String.valueOf(Longit)
		+ "/" + Addrname;
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
	 * Result : 將資訊給放入MapDest物件中，並且設定好geopoint。
	 ***********************************************************/
	public void AsignInfo(String name, String phonenumber, double lati, double longi,String Addr)
	{
		Addrname=Addr;
		Name = name;
		PhoneNumber = phonenumber;
		Latit = lati;
		Longit = longi;
		geopoint = new GeoPoint((int)lati , (int)longi);

	}
	
	public void AsignInfoWithoutGeoPoint(String name, String phonenumber, double lati, double longi,String Addr)
	{
		Addrname=Addr;
		Name = name;
		PhoneNumber = phonenumber;
		Latit = lati;
		Longit = longi;
	//	geopoint = new GeoPoint((int)lati*1000000 , (int)longi*1000000);
	}
	
}
