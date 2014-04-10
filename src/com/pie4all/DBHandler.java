package com.pie4all;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DBHandler extends Activity {
	 /** Called when the activity is first created. */
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	 
	  SQLiteDatabase myDB= null;
	  //String TableName = "myTable";
	 
	  String Data="";
	 
	  /* Create a Database. */
	  try {
	   myDB = this.openOrCreateDatabase("pie4allDB", MODE_PRIVATE, null);
	 
	   /* Create a Table in the Database. */
	   myDB.execSQL("CREATE TABLE IF NOT EXISTS "
	     + "categories"
	     + " (naam VARCHAR);");
	 
	   /* Insert data to a Table*/
	   myDB.execSQL("INSERT INTO "
	     + "categories"
	     + " (naam)"
	     + " VALUES ('Vlaaien');");
	 
	   
	   System.out.println("db aangemaakt.. schijnt..");
	   
	   /*retrieve data from database */
	   Cursor c = myDB.rawQuery("SELECT * FROM categories", null);
	 
	   int Column1 = c.getColumnIndex("naam");
	   //int Column2 = c.getColumnIndex("Field2");
	 
	   // Check if our result was valid.
	   c.moveToFirst();
	   if (c != null) {
	    // Loop through all Results
	    do {
	     String Name = c.getString(Column1);
	     //int Age = c.getInt(Column2);
	     Data =Data +Name+"\n";
	    }while(c.moveToNext());
	   }
	   TextView tv = new TextView(this);
	   tv.setText(Data);
	   setContentView(tv);
	  }
	  catch(Exception e) {
	   Log.e("Error", "Error", e);
	  } finally {
	   if (myDB != null)
	    myDB.close();
	  }
	 }
}