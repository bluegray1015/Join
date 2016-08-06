package com.join;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.view.View.OnClickListener;


public class TxRxThread extends Thread{
	
	private Socket s;        // 客戶端連線Socket物件
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
	
	public TxRxThread(Socket socket, BufferedReader reader, PrintWriter writer) {    // 此thread的Constructor
			
		s = socket;
		in = reader;
		out = writer;
	}
	
	// 指定這個Thread的訊息觀察者(給Join3用)
	public void setMessageObserver(Main activity3) {
		join3 = activity3;
	}
	
	// 指定這個Thread的訊息觀察者(給Join2 check id用)
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
				
				if (transuseract.equals("login")){ // 若為login的要求
					
					out.println("ID:"+transusername+"@"+transuserpwd+ "@"); // 傳送給server是要login的資訊
					
				}
				else if(transuseract.equals("register"))
				{
					
					out.println("register:" + transusername + "@" + transuserpwd + "@"
							                + transusersex + "@" + transuserbirthday + "@"
							                + transusercountry + "@" + transusercity + "@"); // 傳送給server是要register的資訊
					
				}
				else if(transuseract.equals("modify"))
				{
					
					out.println("modify:" + transusername + "@" + transuserpwd + "@"
							                + transusersex + "@" + transuserbirthday + "@"
							                + transusercountry + "@" + transusercity + "@"); // 傳送給server是要register的資訊
					
				}
				else if(transuseract.equals("checkid"))
				{
					
					out.println("checkid:"+transusername+"@"); // 傳送給server是要check id的資訊
					
				}
				
			}
			
			// 負責傾聽server回傳的data
			while((inStr = in.readLine())!= null) {
				
				if (join2 == null){
					
					join3.goDataUpdatehandler(); // 判別此為join3建立的傾聽thread
					
				}
				else
				{
					join2.goDataUpdatehandler(); // 判別此為join2建立的傾聽thread
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
