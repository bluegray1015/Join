package com.join;

import com.join.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class Join8_2 extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showtype2();
    }
    
    private Button back;
    private Button join;
    
    private CheckBox sportone;
    private CheckBox sporttwo;
    
    private String selectsport1;
    private String selectsport2;
    
    public void showtype2() {
    	setContentView(R.layout.sport);
    	
    	sportone = (CheckBox)findViewById(R.id.sportgroup1);
    	sportone.setOnClickListener(selectsportgroup1);
    	
    	sporttwo = (CheckBox)findViewById(R.id.sportgroup2);
    	sporttwo.setOnClickListener(selectsportgroup2);
    	
    	back = (Button)findViewById(R.id.sportbackbutton);
    	back.setOnClickListener(backseemore);
    	
    	join = (Button)findViewById(R.id.sportjoinbutton);
    	join.setOnClickListener(joinselect);
    	
    }
    
    private OnClickListener selectsportgroup1 = new OnClickListener() {
    	public void onClick(View v) {
    		CheckBox cb = (CheckBox) v;
    		if (((CheckBox) cb).isChecked()) 
    		{            
    			selectsport1 = cb.getText().toString();        
    		} 
    		else 
    		{            
    			selectsport1 = null;        
    		}
    		
    	}
    };
    
    private OnClickListener selectsportgroup2 = new OnClickListener() {
    	public void onClick(View v) {
    		CheckBox cb = (CheckBox) v;
    		if (((CheckBox) cb).isChecked()) 
    		{            
    			selectsport2 = cb.getText().toString();        
    		} 
    		else 
    		{            
    			selectsport2 = null;        
    		}
    		
    	}
    };
    
    private OnClickListener backseemore = new OnClickListener() {
    	public void onClick(View v) {
    		Join8_2.this.finish();	
    	}
    };
    
    private OnClickListener joinselect = new OnClickListener() {
    	public void onClick(View v) {
    		transmitdata();
    		openshortdialog();
    	}
    };
    
    private void transmitdata() {
    	
    }
    
    private void openshortdialog() {
    	
    	if ((selectsport1 == null)&&(selectsport2 == null))
    	{
    		Toast.makeText(Join8_2.this, "Please Select at Least One Group of Sport!", Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		Toast.makeText(Join8_2.this, "You Have Joined The Groups You Just Selected!", Toast.LENGTH_SHORT).show();
    	}
    }
    
}