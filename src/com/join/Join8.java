package com.join;

import com.join.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class Join8 extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showtype1();
    }
    
    private Button back;
    private Button join;
    
    private CheckBox shoppingone;
    private CheckBox shoppingtwo;
    
    private String selectshopping1;
    private String selectshopping2;
    
    
    public void showtype1() {
    	setContentView(R.layout.shopping);
    	
    	shoppingone = (CheckBox)findViewById(R.id.shoppinggroup1);
    	shoppingone.setOnClickListener(selectshoppinggroup1);
    	
    	shoppingtwo = (CheckBox)findViewById(R.id.shoppinggroup2);
    	shoppingtwo.setOnClickListener(selectshoppinggroup2);
    	
    	back = (Button)findViewById(R.id.shopbackbutton);
    	back.setOnClickListener(backseemore);
    	
    	join = (Button)findViewById(R.id.shopjoinbutton);
    	join.setOnClickListener(joinselect);
    	
    }
    
    private OnClickListener selectshoppinggroup1 = new OnClickListener() {
    	public void onClick(View v) {
    		CheckBox cb = (CheckBox) v;
    		if (((CheckBox) cb).isChecked()) 
    		{            
    			selectshopping1 = cb.getText().toString();        
    		} 
    		else 
    		{            
    			selectshopping1 = null;        
    		}
    		
    	}
    };
    
    private OnClickListener selectshoppinggroup2 = new OnClickListener() {
    	public void onClick(View v) {
    		CheckBox cb = (CheckBox) v;
    		if (((CheckBox) cb).isChecked()) 
    		{            
    			selectshopping2 = cb.getText().toString();        
    		} 
    		else 
    		{            
    			selectshopping2 = null;        
    		}
    		
    	}
    };
    
    private OnClickListener backseemore = new OnClickListener() {
    	public void onClick(View v) {
    		Join8.this.finish();	
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
    	
    	if ((selectshopping1 == null)&&(selectshopping2 == null))
    	{
    		Toast.makeText(Join8.this, "Please Select at Least One Group of Shopping!", Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		Toast.makeText(Join8.this, "You Have Joined The Groups You Just Selected!", Toast.LENGTH_SHORT).show();
    	}
    }
    
}