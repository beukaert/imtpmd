package com.pie4all;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class MainActivity extends Activity {
	ListView list;
	TextView ver;
	TextView name;
	TextView api;
	
	ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
	
	public ServerCommunicator serverCommunicator1;
	public ServerCommunicator serverCommunicator2;
	//public ServerCommunicator serverCommunicator3;
	//public ServerCommunicator serverCommunicator4;
	//public ServerCommunicator serverCommunicator5;
	
	private Spinner spinner2;
	public SQLiteDatabase myDB = null;
	
	public static String userCat = "Vlaaien";
	
	//JSON Node Names 
	private static final String TAG_NAME = "naam";
	private static final String TAG_PRICE = "prijs";
	
	public JSONArray jArray = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        setContentView(R.layout.activity_main);
        
        oslist = new ArrayList<HashMap<String, String>>();
        
        myDB = this.openOrCreateDatabase("pie4allDB", MODE_PRIVATE, null);
        
        
        
        //controleren of er een netwerkverbinding isd
        if(isNetworkAvailable()){
        	System.out.println("Er is internet!");
        	
        	serverCommunicator1 = new ServerCommunicator(this, "Categories", "{ \"categorielijst\" : \"\" }");
            serverCommunicator2 = new ServerCommunicator(this, "Vlaaien", "{ \"productenlijst\" : \"Vlaaien\" }");
            //serverCommunicator3 = new ServerCommunicator(this, "cakes", "{ \"productenlijst\" : \"Cakes\" }");
           // serverCommunicator4 = new ServerCommunicator(this, "bruidstaarten", "{ \"productenlijst\" : \"Bruidstaarten\" }");
           //serverCommunicator5 = new ServerCommunicator(this, "verjaardagstaarten", "{ \"productenlijst\" : \"Verjaardagstaarten\" }");
        	
        	/* Create a Database. */
			  try {
			   /* Create a Table in the Database.d */
			   myDB.execSQL("DROP TABLE IF EXISTS Categories");
			   myDB.execSQL("CREATE TABLE IF NOT EXISTS Categories (json VARCHAR);");
			   
			   myDB.execSQL("DROP TABLE IF EXISTS Vlaaien");
			   myDB.execSQL("CREATE TABLE IF NOT EXISTS Vlaaien (json VARCHAR);");
			 
			   JSONObject json1 = serverCommunicator1.serverBericht;
			   String stringToBeInserted1 = json1.toString();
			   
			   JSONObject json2 = serverCommunicator2.serverBericht;
			   String stringToBeInserted2 = json2.toString();
			   
			   if(stringToBeInserted1 != ""){
				   
				   System.out.println("JSON" + stringToBeInserted1);
			   }
			   else{
				   System.out.println("FAILED");
			   }
			   
			   //Insert data to a Table
			   myDB.execSQL("INSERT INTO "
			     + "Categories"
			     + " (json)"
			     + " VALUES ('"+stringToBeInserted1+"');");
			   
			   myDB.execSQL("INSERT INTO "
			     + "Vlaaien"
			     + " (json)"
			     + " VALUES ('"+stringToBeInserted2+"');");
			   
			  }
			  catch(Exception e) {
				  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
					
			       	// Setting Dialog Title
			       	alertDialog.setTitle("Pie4All");
			
			       	// Setting Dialog Message
			       	alertDialog.setMessage("Crashed on loading db");
	       	 		
			       	// Setting Positive "Yes" Btn
			       	alertDialog.setPositiveButton("Ik probeer het later nog eens",
	       	        new DialogInterface.OnClickListener() {
	       	            public void onClick(DialogInterface dialog, int which) {
	       	                //App sluiten
	       	            	finish();
	       	            }
	       	        });
			       	alertDialog.show();
			  }
        }
        else{
        	System.out.println("Er is geen internet!");
        	
       	 	try {
       	 		myDB.rawQuery("SELECT json FROM sqlite_master WHERE type = 'table' AND name = 'categories'", null);
    		    System.out.println("Oudere data beschikbaar!");
       	 	}
       	 	catch(Exception e){
       	 		
       	 		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		
		       	// Setting Dialog Title
		       	alertDialog.setTitle("Pie4All");
		
		       	// Setting Dialog Message
		       	alertDialog.setMessage("Geen gegevens beschikbaar. Verbind met het internet!");
       	 		
		       	// Setting Positive "Yes" Btn
		       	alertDialog.setPositiveButton("Ik probeer het later nog eens",
       	        new DialogInterface.OnClickListener() {
       	            public void onClick(DialogInterface dialog, int which) {
       	                //App sluiten
       	            	finish();
       	            }
       	        });
		       	alertDialog.show();
       	 	}
        }
		
		  
		addItemsOnSpinner2();
		addListenerOnSpinnerItemSelection();
		
		new JSONParse().execute();
		
		/*if (myDB != null)
		{
			myDB.close();
		}*/
    }
    
    public static String setUserCat(String input)
    {
    	userCat = input;
    	System.out.println("usercat = " + userCat);

		return userCat;
    }
    
    private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
    
    public String getData(){
    	 Cursor c = myDB.rawQuery("SELECT json FROM "+userCat, null);
    	 c.moveToFirst();
    	 
    	 String data = "";
    	 
    	 int Column1 = c.getColumnIndex("json");
    	 
    	 if (c != null) {
 		    // Loop through all Results
    		 
 		    do {
 		     String Name = c.getString(Column1);
 		     //int Age = c.getInt(Column2);
 		     data = data +Name+"\n";
 		    }while(c.moveToNext());
 		    
 		    System.out.println("query gelukt! " + data);
 			 
 		 }
    	 
    	 return data;
    }
    
    private class JSONParse extends AsyncTask<String, String, JSONObject> {
    	 private ProgressDialog pDialog;
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
			 name = (TextView)findViewById(R.id.name);
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show(); 
    	}
    	
    	@Override
        protected JSONObject doInBackground(String... args) {

    		//JSONObject json = serverCommunicator1.serverBericht;
    		//System.out.println("wtf" + json);
    		
    		JSONObject json = null;
			try {
				
				//json ophalen uit database
				Cursor c = myDB.rawQuery("SELECT json FROM "+userCat, null);
		    	 c.moveToFirst();
		    	 
		    	 String data = "";
		    	 
		    	 int Column1 = c.getColumnIndex("json");
		    	 
		    	 if (c != null) {
		 		    // Loop through all Results
		    		 
		 		    do {
		 		     String Name = c.getString(Column1);
		 		     //int Age = c.getInt(Column2);
		 		     data = data +Name+"\n";
		 		    }while(c.moveToNext());
		 		    
		 		    System.out.println("query gelukt! " + data);
		 			 
		 		 }

				String json1 = data;
				JSONObject jsonObject = new JSONObject(json1);

				/*"{\"categories\":[{\"naam\":\"Vlaaien\"},{\"naam\":\"Cakes\"},{\"naam\":\"Bruidstaarten\"},{\"naam\":\"Verjaardagstaarten\"}]}";*/
				
				json = jsonObject;
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println("dit niet");
				e.printStackTrace();
			}
    		
    		return json;
    	}
    	 @Override
         protected void onPostExecute(JSONObject json) {
    		 pDialog.dismiss();
    		 try {
    				// Getting JSON Array from URL
    			 jArray = json.getJSONArray(userCat);
    				for(int i = 0; i < jArray.length(); i++){
    				JSONObject c = jArray.getJSONObject(i);
    				
    				// Storing  JSON item in a Variable
    				//String ver = c.getString(TAG_VER);
    				String name = c.getString(TAG_NAME);
    				//String api = c.getString(TAG_API);
    				

    				// Adding value HashMap key => value
    				HashMap<String, String> map = new HashMap<String, String>();

    				//map.put(TAG_VER, ver);
    				map.put(TAG_NAME, name);
    				//map.put(TAG_API, api);
    				
    				oslist.add(map);
    				list=(ListView)findViewById(R.id.list);
    				
    				
    				ListAdapter adapter = new SimpleAdapter(MainActivity.this, oslist,
    						R.layout.list_v,
    						new String[] {TAG_NAME}, new int[] {
    								R.id.name});

    				
    				
    				list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						public void onItemClick(AdapterView<?> parent, View view,
						                        int position, long id) {
						   
							//Toast.makeText(MainActivity.this, "You Clicked at "+oslist.get(+position).get("naam"), Toast.LENGTH_SHORT).show();
							
							// selected item 
							TextView txt = (TextView) view.findViewById(R.id.name);
							String product = txt.getText().toString();
							  
							// Launching new Activity on selecting single List Item
							Intent i = new Intent(getApplicationContext(), SingleListItem.class);
							// sending data to new activity
							i.putExtra("product", product);
							startActivity(i);
					          	 
						}
    		        });
    				
    				list.setAdapter(adapter);
    			}
    		 } 
    		 catch (JSONException e) {
    			e.printStackTrace();
    		}

    		 
    	 }
    }
    
    
    public void addItemsOnSpinner2() {
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		List<String> list = new ArrayList<String>();
		list.add("Vlaaien");
		list.add("Cakes");
		list.add("Bruidstaarten");
		list.add("Verjaardagstaarten");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(dataAdapter);
	}
	 
	public void addListenerOnSpinnerItemSelection() {
		spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());
		
	}
	 


	public void onClick(View src) {
		//dit word de preference setting welke categorie
		//EditText naamEditText = (EditText) this.findViewById(R.id.naamVeld); 
		//String naam = naamEditText.getText().toString();
		
		//System.out.println("bericht:" + bericht);
		
		//serverCommunicator = new ServerCommunicator(this, "categorielijst");
		//System.out.println("hij verzend");
	}
    
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		
		/*spinner2 = (Spinner) findViewById(R.id.spinner2);
		List<String> list = new ArrayList<String>();
		list.add("Vlaaien");
		list.add("Cakes");
		list.add("Bruidstaarten");
		list.add("Verjaardagstaarten");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(dataAdapter);*/
		
	
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	
    
}
