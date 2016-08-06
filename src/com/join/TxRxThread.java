package com.join;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.view.View.OnClickListener;


public class TxRxThread extends Thread{
	
	private Socket s;        // �Ȥ�ݳs�uSocket����
	private BufferedReader in;
	private PrintWriter out;
	private Register join2;
	private Main join3;
	private String transusername = "";
	private String transuserpwd = "";
	private String transusersex = "";
	private String transuserbirthday = "";
	private String transusercountry = "";
	private String transusercity = "";
	private String transuseract = "";
	
	private String inStr;
	
	public TxRxThread(Socket socket, BufferedReader reader, PrintWriter writer) {    // ��thread��Constructor
			
		s = socket;
		in = reader;
		out = writer;
	}
	
	// ���w�o��Thread���T���[���(��Join3��)
	public void setMessageObserver(Main activity3) {
		join3 = activity3;
	}
	
	// ���w�o��Thread���T���[���(��Join2 check id��)
	public void setCheckidMessageObserver(Register activity2) {
		join2 = activity2;
	}
	
	public void setTransUserDatafor_register(String name, String pwd, String sex, String bir, String country, String city, String act) {
		transusername = name;
		transuserpwd = pwd;
		transusersex = sex;
		transuserbirthday = bir;
		transusercountry = country;
		transusercity = city;
		transuseract = act;
		
	}
	
	public void setTransUserDatafor_modify(String name, String pwd, String sex, String bir, String country, String city, String act) {
		transusername = name;
		transuserpwd = pwd;
		transusersex = sex;
		transuserbirthday = bir;
		transusercountry = country;
		transusercity = city;
		transuseract = act;
	}
	
	public void setTransUserDatafor_login(String name, String pwd,  String act) {
		transusername = name;
		transuserpwd = pwd;
		transuseract = act;
	}
	
	public void setTransUserDatafor_checkid(String name,  String act) {
		transusername = name;
		transuseract = act;
	}
	
	public String getUserData() {
		return inStr;
	}
	
	public void run(){
		
		try {
//			sleep(4000);
			
			if(s.isConnected()){
				
				if (transuseract.equals("login")){ // �Y��login���n�D
					
					out.println("ID:"+transusername+"@"+transuserpwd+ "@"); // �ǰe��server�O�nlogin����T
					
				}
				else if(transuseract.equals("register"))
				{
					
					out.println("register:" + transusername + "@" + transuserpwd + "@"
							                + transusersex + "@" + transuserbirthday + "@"
							                + transusercountry + "@" + transusercity + "@"); // �ǰe��server�O�nregister����T
					
				}
				else if(transuseract.equals("modify"))
				{
					
					out.println("modify:" + transusername + "@" + transuserpwd + "@"
							                + transusersex + "@" + transuserbirthday + "@"
							                + transusercountry + "@" + transusercity + "@"); // �ǰe��server�O�nregister����T
					
				}
				else if(transuseract.equals("checkid"))
				{
					
					out.println("checkid:"+transusername+"@"); // �ǰe��server�O�ncheck id����T
					
				}
				
			}
			
			// �t�d��ťserver�^�Ǫ�data
			while((inStr = in.readLine())!= null) {
				
				if (join2 == null){
					
					join3.goDataUpdatehandler(); // �P�O����join3�إߪ���ťthread
					
				}
				else
				{
					join2.goDataUpdatehandler(); // �P�O����join2�إߪ���ťthread
				}
				
			}
	    
			if(inStr == null){
				
				if (join2 == null){
					
					join3.overActivityhandler();
					
				}
				else
				{
					join2.overActivityhandler();
				}
				
			}
			
		}catch(IOException e){
			
		}catch(Exception e){
			
		}
		
	}

}
