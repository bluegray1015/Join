package com.join;

import com.google.android.maps.GeoPoint;

public class MapUser 
{
	public String Name;//�W�r
	public String PhoneNumber;//�q�ܸ��X
	public String MsgOfNewLeader;
	public String StateString;
	public String PrepareString;
	public int State;//�O���O�������A
	public double Latit;//�n��
	public double Longit;//�g��
	public int MyselfInt;//�P�_�O���O�ۤv
	GeoPoint geopoint;
	public boolean flag;//�ΨӧP�_�O�_�b�u�W
	public int ChangeLeader;//������
	public int Slow;//�먮
	
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
	 * Parameter : 		public String Name;//�W�r
						public String PhoneNumber;//�q�ܸ��X
						public String PrepareString;
						public int State;//���A
						public int Latit;//�n��
						public int Longit;//�g��
						public int MyselfInt;
	 * Return : void
	 * Result : �N��T����JMapUser���󤤡A�åB�]�w�ngeopoint�C
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
