package com.join;

import java.util.ArrayList;


public class AllUser {

	ArrayList<MapUser> UserArrayList = new ArrayList<MapUser>();
	MapUser myself;
	


	/***********************************************************
	 * Function Name : SearchUser
	 * Parameter : MapUser user 傳入一個MapUser物件當作搜尋的依據。
	 * Return : int
	 * Result : 	若有在ArrayList中搜尋到這個物件(以名字做為依據)
	 * 				那就回傳這個物件在ArrayList中的index，否則就回
	 * 				傳-1。
	 ***********************************************************/
	int SearchUser(MapUser user)
	{
		for (int i = 0; i<UserArrayList.size(); i++)
		{
			MapUser temp = UserArrayList.get(i);
			if(temp.Name.equals(user.Name))
			{
				return i;
			}
		}
		return -1;
	}
	/***********************************************************
	 * Function Name : AddUser
	 * Parameter : MapUser user 傳入一個MapUser物件的位址作為等等我
	 * 							要插入到ArrayList的物件。
	 * Return : void
	 * Result : 首先會先Serch該物件是否已經有一樣的在ArrayList中了，
	 * 			若有，那就做更新，否則就加入到最後面。
	 ***********************************************************/
	void AddUser(MapUser user)
	{
		int index = SearchUser(user);
		if (index == -1)
		{
			UserArrayList.add(user);
			
		}
		else
		{
			UserArrayList.remove(index);
			UserArrayList.add(index, user);
		}
		
	}
	/***********************************************************
	 * Function Name : CombineAllUser
	 * Parameter : void
	 * Return : String 回傳的字串是所有User的information串起來的結
	 * 			果，並且在最前面加上使用者的數量。
	 * Result : 會將所有User的information串起來並回傳此字串。
	 ***********************************************************/
	String CombineAllUser()
	{
		int index = UserArrayList.size();
		String users = Integer.toString(index)+"/";
		for (int i = 0; i<index; i++)
		{
			MapUser temp = UserArrayList.get(i);
			users =	users + temp.CombineMessage() + "/";
		}
		return users;
	}
	/***********************************************************
	 * Function Name : SplitAndSave
	 * Parameter : String str
	 * Return : void
	 * Result : 將參數中的自串拆解並且存入到ArrayList中。
	 ***********************************************************/
	void SplitAndSave(String str)
	{
		/*先將所有User的flag都設成false，之後若傳來的資訊中有該User，那就代表
		更新成功，所以把該User的flag設成true，等等就不會被刪除*/
		String[] result = str.split("/");//先將訊息拆解
		int usernumber = Integer.parseInt(result[0]);//第一個字代表者使用者數目
//		SetAllUserFlagFalse();	//將所有使用者的flag都設成false
		for (int i=0 ; i<usernumber ; i++)//依序將收到的使用者資訊一一插入，過程中並設定使用者flag
		{
			MapUser temp = new MapUser();
			temp.AsignInfo(result[13*i+1],result[13*i+2],result[13*i+3],result[13*i+4],result[13*i+5],Integer.parseInt(result[13*i+6]) , Integer.parseInt(result[13*i+7]), Integer.parseInt(result[13*i+8]), Integer.parseInt(result[13*i+9]),Integer.parseInt(result[13*i+10]),Integer.parseInt(result[13*i+11]));
			AddUser(temp);
		}
//		CheckUserFlagAndUpdateUserList();//更新ArrayList
	}
	/***********************************************************
	 * Function Name : NumberOfUser
	 * Parameter : void
	 * Return : int
	 * Result : 回傳目前使用者的數目。
	 ***********************************************************/
	int NumberOfUser()
	{
		return UserArrayList.size();
	}
	
}
