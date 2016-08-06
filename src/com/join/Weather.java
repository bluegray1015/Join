package com.join;
import android.content.*;
import java.net.*;
import java.util.StringTokenizer;
import java.io.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;
import android.content.DialogInterface;
import com.join.R;






 
public  class Weather extends Activity
{
	//public GeoPoint currentGeoPoint;
	public String currentLat;
	public String currentLong;
	public String[] array_datatime;
	public String[] array_condition;
	public String[] array_tempf;
	public String[] array_tempc;
	public String[] array_humidity;
	public String[] array_wind;
	public String[] array_dayofweek;
	public String[] array_low;
	public String[] array_high;
	public JoinMapView Join5_2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*取出經緯度和搜尋關鍵字並轉成string*/
        Bundle bundle = this.getIntent().getExtras();
        currentLat = bundle.getString("currentLat");
        currentLong = bundle.getString("currentLong");
		
		Join5_2=new JoinMapView();
		

        
		
         String[] array_datatime= new String[500];
    	 String[] array_condition= new String[500];
    	 String[] array_tempf= new String[500];
    	 String[] array_tempc= new String[500];
    	 String[] array_humidity= new String[500];
    	 String[] array_wind= new String[500];
    	 String[] array_dayofweek= new String[500];
    	 String[] array_low= new String[500];
    	 String[] array_high= new String[500];
    

        try 
        {
            URL url = new URL("http://www.google.com/ig/api?hl=zh-tw&weather=,,,"+ currentLat+","+ currentLong);
            URLConnection conn = url.openConnection();
           BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"Big5"));
            String line = null;
            line = reader.readLine();                 
            String[] tokens =line.split("<");
                     
            int data_count;int con_count;int temf_count;int temc_count;int hum_count;int wind_count;
            int day_count;int low_count;int high_count;
            data_count=0;  con_count=0;  temf_count=0;  temc_count=0;  hum_count=0;  wind_count=0; 
            day_count=0;  low_count=0;  high_count=0; 
            
            for(int i=0;i<tokens.length;i++)
            {
            	
            	if(tokens[i].startsWith("current_date_time data"))
            	{
   
            		StringTokenizer stkn = new StringTokenizer(tokens[i],"="); //以用空白做分隔
            		String msg=" ";
            		if(stkn.countTokens()>=1)msg=stkn.nextToken();
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		array_datatime[data_count]=new String(msg);
            		data_count++;
            
            		
            	}
            	else if(tokens[i].startsWith("condition data"))
            	{

            		StringTokenizer stkn = new StringTokenizer(tokens[i],"="); //以用空白做分隔
            		String msg=" ";
            		if(stkn.countTokens()>=1)msg=stkn.nextToken();
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		array_condition[con_count]=new String(msg);
            		 con_count++;
            		
            	}
            	else if(tokens[i].startsWith("temp_f data"))
            	{
            		StringTokenizer stkn = new StringTokenizer(tokens[i],"="); //以用空白做分隔
            		String msg=" ";
            		if(stkn.countTokens()>=1)msg=stkn.nextToken();
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		array_tempf[temf_count]=new String(msg);
            		temf_count++;
            		
            	}
            	else if(tokens[i].startsWith("temp_c data"))
            	{
            		StringTokenizer stkn = new StringTokenizer(tokens[i],"="); //以用空白做分隔
            		String msg=" ";
            		if(stkn.countTokens()>=1)msg=stkn.nextToken();
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		array_tempc[temc_count]=new String(msg);
            		temc_count++;
            	
            	}
            	else if(tokens[i].startsWith("humidity data"))
            	{
            		
            		
            		StringTokenizer stkn = new StringTokenizer(tokens[i],"="); //以用空白做分隔
            		String msg=" ";
            		if(stkn.countTokens()>=1)msg=stkn.nextToken();
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		array_humidity[hum_count]=new String(msg);
            		hum_count++;
            		
            	}
            	else if(tokens[i].startsWith("wind_condition data"))
            	{
            		
            		
            		StringTokenizer stkn = new StringTokenizer(tokens[i],"="); //以用空白做分隔
            		String msg=" ";
            		if(stkn.countTokens()>=1)msg=stkn.nextToken();
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		array_wind[wind_count]=new String(msg);
            		wind_count++;
            	}
            	else if(tokens[i].startsWith("day_of_week data"))
            	{
            		
            		
            		StringTokenizer stkn = new StringTokenizer(tokens[i],"="); //以用空白做分隔
            		String msg=" ";
            		if(stkn.countTokens()>=1)msg=stkn.nextToken();
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		array_dayofweek[day_count]=new String(msg);
            		day_count++;
            	}
            	else if(tokens[i].startsWith("low data"))
            	{
            		
            		
            		StringTokenizer stkn = new StringTokenizer(tokens[i],"="); //以用空白做分隔
            		String msg=" ";
            		if(stkn.countTokens()>=1)msg=stkn.nextToken();
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		array_low[low_count]=new String(msg);
            		low_count++;
            	}
            	else if(tokens[i].startsWith("high data"))
            	{
            		
            		
            		StringTokenizer stkn = new StringTokenizer(tokens[i],"="); //以用空白做分隔
            		String msg=" ";
            		if(stkn.countTokens()>=1)msg=stkn.nextToken();
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		if(stkn.hasMoreTokens())msg=stkn.nextToken("\"");
            		array_high[high_count]=new String(msg);
            		high_count++;
            	}           	          
            	          	
            }    
            int iIconResId3_weather=R.drawable.man;	
			new AlertDialog.Builder(this).setTitle("天氣狀況").setIcon(iIconResId3_weather).setMessage
            ("現在\n" 
             +"\n"
		   	 +"天氣 : "+array_condition[0]+"\n"+
		  	 "溫度 : "+array_tempc[0]+"度c , "+array_tempf[0]+"度f"+"\n"+
		  	array_humidity[0]+"\n"+
		  	array_wind[0]+"\n"+
		  	"\n"+
		  	"本周\n" 
		  	+"\n"
		  	+array_dayofweek[0]+" : "+array_condition[1]+"\n"+
		  "溫度 : "+array_low[0]+"度c - "+array_high[0]+"度c"+"\n"
		  +"\n"+
		  array_dayofweek[1]+" : "+array_condition[2]+"\n"+
		  "溫度 : "+array_low[1]+"度c - "+array_high[1]+"度c"+"\n"+
		  "\n"+
		 array_dayofweek[2]+" : "+array_condition[3]+"\n"+
		  "溫度 : "+array_low[2]+"度c - "+array_high[2]+"度c"+"\n"+
		  "\n"+
		  array_dayofweek[3]+" : "+array_condition[4]+"\n"+
		  "溫度 : "+array_low[3]+"度c - "+array_high[3]+"度c"+"\n"+ 
		  "\n"+
		  "資料更新時間 : "+array_datatime[0]
		  	)
		     
		    .setNeutralButton("確定",
		      new DialogInterface.OnClickListener()
		       {
		     public void onClick(DialogInterface dialoginterface,int i)
		            {                  	
		    	     Weather.this.finish();		
		                 }
		                })
		                       
		                 .show();
		                
            reader.close();
        }
        catch (MalformedURLException e) 
        {
            e.printStackTrace(); 
        }
        catch (IOException e)
        {
            e.printStackTrace(); 
        }
      
    }
	
	
	

	public void openDialog2(String msg){
		   Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		   }
	
	
	
	
}
