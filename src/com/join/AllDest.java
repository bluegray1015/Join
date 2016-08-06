package com.join;

import java.util.ArrayList;


public class AllDest {

	ArrayList<MapDest> DestArrayList = new ArrayList<MapDest>();
	MapDest myself;
	


	/***********************************************************
	 * Function Name : SearchDest
	 * Parameter : MapDest Dest 傳入一個MapDest物件當作搜尋的依據。
	 * Return : int
	 * Result : 	若有在ArrayList中搜尋到這個物件(以名字做為依據)
	 * 				那就回傳這個物件在ArrayList中的index，否則就回
	 * 				傳-1。
	 ***********************************************************/
	int SearchDest(MapDest Dest)
	{
		for (int i = 0; i<DestArrayList.size(); i++)
		{
			MapDest temp = DestArrayList.get(i);
			if(temp.Name.equals(Dest.Name))
			{
				return i;
			}
		}
		return -1;
	}
	/***********************************************************
	 * Function Name : AddDest
	 * Parameter : MapDest Dest 傳入一個MapDest物件的位址作為等等我
	 * 							要插入到ArrayList的物件。
	 * Return : void
	 * Result : 首先會先Serch該物件是否已經有一樣的在ArrayList中了，
	 * 			若有，那就做更新，否則就加入到最後面。
	 ***********************************************************/
	void AddDest(MapDest Dest)
	{
		int index = SearchDest(Dest);
		if (index == -1)
		{
			DestArrayList.add(Dest);
			
		}
		else
		{
			DestArrayList.remove(index);
			DestArrayList.add(index, Dest);
		}
		
	}
	/***********************************************************
	 * Function Name : CombineAllDest
	 * Parameter : void
	 * Return : String 回傳的字串是所有Dest的information串起來的結
	 * 			果，並且在最前面加上使用者的數量。
	 * Result : 會將所有Dest的information串起來並回傳此字串。
	 ***********************************************************/
	String CombineAllDest()
	{
		int index = DestArrayList.size();
		String Dests = Integer.toString(index)+"/";
		for (int i = 0; i<index; i++)
		{
			MapDest temp = DestArrayList.get(i);
			Dests =	Dests + temp.CombineMessage() + "/";
		}
		return Dests;
	}
	/***********************************************************
	 * Function Name : SplitAndSave
	 * Parameter : String str
	 * Return : void
	 * Result : 將參數中的自串拆解並且存入到ArrayList中。
	 ***********************************************************/
	void SplitAndSave(String str)
	{
		/*先將所有Dest的flag都設成false，之後若傳來的資訊中有該Dest，那就代表
		更新成功，所以把該Dest的flag設成true，等等就不會被刪除*/
		String[] result = str.split("/");//先將訊息拆解
		int Destnumber = Integer.parseInt(result[0]);//第一個字代表者使用者數目
//		SetAllDestFlagFalse();	//將所有使用者的flag都設成false
		for (int i=0 ; i<Destnumber ; i++)//依序將收到的使用者資訊一一插入，過程中並設定使用者flag
		{
			MapDest temp = new MapDest();
			temp.AsignInfo(result[13*i+1],result[13*i+2],Integer.parseInt(result[13*i+3]),Integer.parseInt(result[13*i+4]),result[13*i+5]);
			AddDest(temp);
		}
//		CheckDestFlagAndUpdateDestList();//更新ArrayList
	}
	/***********************************************************
	 * Function Name : NumberOfDest
	 * Parameter : void
	 * Return : int
	 * Result : 回傳目前使用者的數目。
	 ***********************************************************/
	int NumberOfDest()
	{
		return DestArrayList.size();
	}
	
}
