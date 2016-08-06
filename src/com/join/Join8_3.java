package com.join;

import com.join.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class Join8_3 extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showtype3();
    }
    
    private CheckBox travelone;
    private CheckBox traveltwo;
    
    private String selecttravel1;
    private String selecttravel2;
    
    private Button back;
    private Button join;
    
    public void showtype3() {
    	setContentView(R.layout.travel);
    	
    	travelone = (CheckBox)findViewById(R.id.travelgroup1);
    	travelone.setOnClickListener(selecttravelgroup1);
    	
    	traveltwo = (CheckBox)findViewById(R.id.travelgroup2);
    	traveltwo.setOnClickListener(selecttravelgroup2);
    	
    	back = (Button)findViewById(R.id.travelbackbutton);
    	back.setOnClickListener(backseemore);
    	
    	join = (Button)findViewById(R.id.traveljoinbutton);
    	join.setOnClickListener(joinselect);
    	
    }
    
    private OnClickListener selecttravelgroup1 = new OnClickListener() {
    	public void onClick(View v) {
    		CheckBox cb = (CheckBox) v;
    		if (((CheckBox) cb).isChecked()) 
    		{            
    			selecttravel1 = cb.getText().toString();        
    		} 
    		else 
    		{            
    			selecttravel1 = null;        
    		}
    		
    	}
    };
    
    private OnClickListener selecttravelgroup2 = new OnClickListener() {
    	public void onClick(View v) {
    		CheckBox cb = (CheckBox) v;
    		if (((CheckBox) cb).isChecked()) 
    		{            
    			selecttravel2 = cb.getText().toString();        
    		} 
    		else 
    		{            
    			selecttravel2 = null;        
    		}
    		
    	}
    };
    
    private OnClickListener backseemore = new OnClickListener() {
    	public void onClick(View v) {
    		Join8_3.this.finish();	
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
    	
    	if ((selecttravel1 == null)&&(selecttravel2 == null))
    	{
    		Toast.makeText(Join8_3.this, "Please Select at Least One Group of Travel!", Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		Toast.makeText(Join8_3.this, "You Have Joined The Groups You Just Selected!", Toast.LENGTH_SHORT).show();
    	}
    }
    
}