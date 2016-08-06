package com.join;

import java.util.ArrayList;


public class AllUser {

	ArrayList<MapUser> UserArrayList = new ArrayList<MapUser>();
	MapUser myself;
	


	/***********************************************************
	 * Function Name : SearchUser
	 * Parameter : MapUser user �ǤJ�@��MapUser�����@�j�M���̾ڡC
	 * Return : int
	 * Result : 	�Y���bArrayList���j�M��o�Ӫ���(�H�W�r�����̾�)
	 * 				���N�^�ǳo�Ӫ���bArrayList����index�A�_�h�N�^
	 * 				��-1�C
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
	 * Parameter : MapUser user �ǤJ�@��MapUser���󪺦�}�@��������
	 * 							�n���J��ArrayList������C
	 * Return : void
	 * Result : �����|��Serch�Ӫ���O�_�w�g���@�˪��bArrayList���F�A
	 * 			�Y���A���N����s�A�_�h�N�[�J��̫᭱�C
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
	 * Return : String �^�Ǫ��r��O�Ҧ�User��information��_�Ӫ���
	 * 			�G�A�åB�b�̫e���[�W�ϥΪ̪��ƶq�C
	 * Result : �|�N�Ҧ�User��information��_�Өæ^�Ǧ��r��C
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
	 * Result : �N�ѼƤ����ۦ��ѨåB�s�J��ArrayList���C
	 ***********************************************************/
	void SplitAndSave(String str)
	{
		/*���N�Ҧ�User��flag���]��false�A����Y�ǨӪ���T������User�A���N�N��
		��s���\�A�ҥH���User��flag�]��true�A�����N���|�Q�R��*/
		String[] result = str.split("/");//���N�T�����
		int usernumber = Integer.parseInt(result[0]);//�Ĥ@�Ӧr�N��̨ϥΪ̼ƥ�
//		SetAllUserFlagFalse();	//�N�Ҧ��ϥΪ̪�flag���]��false
		for (int i=0 ; i<usernumber ; i++)//�̧ǱN���쪺�ϥΪ̸�T�@�@���J�A�L�{���ó]�w�ϥΪ�flag
		{
			MapUser temp = new MapUser();
			temp.AsignInfo(result[13*i+1],result[13*i+2],result[13*i+3],result[13*i+4],result[13*i+5],Integer.parseInt(result[13*i+6]) , Integer.parseInt(result[13*i+7]), Integer.parseInt(result[13*i+8]), Integer.parseInt(result[13*i+9]),Integer.parseInt(result[13*i+10]),Integer.parseInt(result[13*i+11]));
			AddUser(temp);
		}
//		CheckUserFlagAndUpdateUserList();//��sArrayList
	}
	/***********************************************************
	 * Function Name : NumberOfUser
	 * Parameter : void
	 * Return : int
	 * Result : �^�ǥثe�ϥΪ̪��ƥءC
	 ***********************************************************/
	int NumberOfUser()
	{
		return UserArrayList.size();
	}
	
}
