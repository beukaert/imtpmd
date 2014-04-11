package com.pie4all;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
 
public class CustomOnItemSelectedListener implements OnItemSelectedListener {
 
public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
	Toast.makeText(parent.getContext(), 
	parent.getItemAtPosition(pos).toString() + " laden..",
	Toast.LENGTH_SHORT).show();
	
	MainActivity.setUserCat(parent.getItemAtPosition(pos).toString());
	
  }
 
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}
 
}