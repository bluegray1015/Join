package com.join;

import com.google.android.maps.GeoPoint;

public class MapDest
{
	public String Name;//�W�r
	public String PhoneNumber;//�q�ܸ��X
	public String Addrname;//����a�}
	public double Latit;//�n��
	public double Longit;//�g��
	GeoPoint geopoint;

	/***********************************************************
	 * Function Name : CombineMessage
	 * Parameter : void
	 * Return : String �^�ǲզn���T���C
	 * Result : �^�ǲզn���T���C
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
	 * Parameter : 		public String Name;//�W�r
						public String PhoneNumber;//�q�ܸ��X
						public String PrepareString;
						public int State;//���A
						public int Latit;//�n��
						public int Longit;//�g��
						public int MyselfInt;
	 * Return : void
	 * Result : �N��T����JMapDest���󤤡A�åB�]�w�ngeopoint�C
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
