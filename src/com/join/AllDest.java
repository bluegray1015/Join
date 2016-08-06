package com.join;

import java.util.ArrayList;


public class AllDest {

	ArrayList<MapDest> DestArrayList = new ArrayList<MapDest>();
	MapDest myself;
	


	/***********************************************************
	 * Function Name : SearchDest
	 * Parameter : MapDest Dest �ǤJ�@��MapDest�����@�j�M���̾ڡC
	 * Return : int
	 * Result : 	�Y���bArrayList���j�M��o�Ӫ���(�H�W�r�����̾�)
	 * 				���N�^�ǳo�Ӫ���bArrayList����index�A�_�h�N�^
	 * 				��-1�C
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
	 * Parameter : MapDest Dest �ǤJ�@��MapDest���󪺦�}�@��������
	 * 							�n���J��ArrayList������C
	 * Return : void
	 * Result : �����|��Serch�Ӫ���O�_�w�g���@�˪��bArrayList���F�A
	 * 			�Y���A���N����s�A�_�h�N�[�J��̫᭱�C
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
	 * Return : String �^�Ǫ��r��O�Ҧ�Dest��information��_�Ӫ���
	 * 			�G�A�åB�b�̫e���[�W�ϥΪ̪��ƶq�C
	 * Result : �|�N�Ҧ�Dest��information��_�Өæ^�Ǧ��r��C
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
	 * Result : �N�ѼƤ����ۦ��ѨåB�s�J��ArrayList���C
	 ***********************************************************/
	void SplitAndSave(String str)
	{
		/*���N�Ҧ�Dest��flag���]��false�A����Y�ǨӪ���T������Dest�A���N�N��
		��s���\�A�ҥH���Dest��flag�]��true�A�����N���|�Q�R��*/
		String[] result = str.split("/");//���N�T�����
		int Destnumber = Integer.parseInt(result[0]);//�Ĥ@�Ӧr�N��̨ϥΪ̼ƥ�
//		SetAllDestFlagFalse();	//�N�Ҧ��ϥΪ̪�flag���]��false
		for (int i=0 ; i<Destnumber ; i++)//�̧ǱN���쪺�ϥΪ̸�T�@�@���J�A�L�{���ó]�w�ϥΪ�flag
		{
			MapDest temp = new MapDest();
			temp.AsignInfo(result[13*i+1],result[13*i+2],Integer.parseInt(result[13*i+3]),Integer.parseInt(result[13*i+4]),result[13*i+5]);
			AddDest(temp);
		}
//		CheckDestFlagAndUpdateDestList();//��sArrayList
	}
	/***********************************************************
	 * Function Name : NumberOfDest
	 * Parameter : void
	 * Return : int
	 * Result : �^�ǥثe�ϥΪ̪��ƥءC
	 ***********************************************************/
	int NumberOfDest()
	{
		return DestArrayList.size();
	}
	
}
