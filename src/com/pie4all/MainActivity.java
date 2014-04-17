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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends Activity {
	ListView list;
	TextView ver;
	TextView name;
	TextView api;
	
	ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
	
	public ServerCommunicator serverCommunicator1;
	public ServerCommunicator serverCommunicator2;
	private Spinner spinner2;
	public SQLiteDatabase myDB = null;
	public Boolean error = false;
	public static String userCat = "vlaaien";
	
	//JSON namen
	private static final String TAG_NAME = "naam";
	
	public JSONArray jArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        setContentView(R.layout.activity_main);
        
        //arraylist aanmaken
        oslist = new ArrayList<HashMap<String, String>>();
        
        //database openen indien deze al bestaat, anders aanmaken.
        myDB = this.openOrCreateDatabase("pie4allDB", MODE_PRIVATE, null);
        
        //controleren of er een netwerkverbinding is
        if(isNetworkAvailable()){
        	System.out.println("Er is internet!");
	
        	//database vullen met data vanaf server
			try {
			   //communiceren met server, gegevens ophalen
			   serverCommunicator1 = new ServerCommunicator(this, "categories", "{ \"categorielijst\" : \"\" }");
			   serverCommunicator2 = new ServerCommunicator(this, "vlaaien", "{ \"productenlijst\" : \"Vlaaien\" }");
			   //serverCommunicator3 = new ServerCommunicator(this, "cakes", "{ \"productenlijst\" : \"Cakes\" }");
			   //serverCommunicator4 = new ServerCommunicator(this, "bruidstaarten", "{ \"productenlijst\" : \"Bruidstaarten\" }");
			   //serverCommunicator5 = new ServerCommunicator(this, "verjaardagstaarten", "{ \"productenlijst\" : \"Verjaardagstaarten\" }"d);
			
			   //tabellen aanmaken in de database
			   myDB.execSQL("DROP TABLE IF EXISTS categories");
			   myDB.execSQL("CREATE TABLE IF NOT EXISTS categories (json VARCHAR);");

			   myDB.execSQL("DROP TABLE IF EXISTS vlaaien");
			   myDB.execSQL("CREATE TABLE IF NOT EXISTS vlaaien (json VARCHAR);");
			 
			   //van JSON objects naar strings converteren zodat deze in de database geplaatst kunnen worden
			   JSONObject json1 = serverCommunicator1.getServerBericht();
			   String stringToBeInserted1 = json1.toString();
			   
			   JSONObject json2 = serverCommunicator2.getServerBericht();
			   String stringToBeInserted2 = json2.toString();
			   
			   //gegevens invoeren in tabellen
			   myDB.execSQL("INSERT INTO "
			     + "categories"
			     + " (json)"
			     + " VALUES ('"+stringToBeInserted1+"');");
			   
			   myDB.execSQL("INSERT INTO "
			     + "vlaaien"
			     + " (json)"
			     + " VALUES ('"+stringToBeInserted2+"');");
			   
			  }
			  catch(Exception e) {
				  //halt code indien fout
				  error = true;
				  System.out.println("DB niet bereikbaar!");
				  
				  //alert pop-up triggeren
				  AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
				  	
			       	// Setting Dialog Title
			       	alertDialog.setTitle("Pie4All");
			
			       	// Setting Dialog Message
			       	alertDialog.setMessage("De server lijkt overbelast te zijn. Onze excuses voor het ongemak.");
	       	 		
			       	// Setting Positive "Yes" Btn
			       	alertDialog.setPositiveButton("Ik probeer het later nog eens",
	       	        new DialogInterface.OnClickListener() {
	       	            public void onClick(DialogInterface dialog, int which) {
	       	                //App sluiten onclick
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
       	 		//halt code indien fout
       	 		error = true;
       	 		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		
		       	// Setting Dialog Title
		       	alertDialog.setTitle("Pie4All");
		
		       	// Setting Dialog Message
		       	alertDialog.setMessage("Geen gegevens beschikbaar. Verbind met het internet!");
       	 		
		       	// Setting Positive "Yes" Btn
		       	alertDialog.setPositiveButton("Ik probeer het later nog eens",
       	        new DialogInterface.OnClickListener() {
       	            public void onClick(DialogInterface dialog, int which) {
       	                //App sluiten onclick
       	            	finish();
       	            }
       	        });
		       	alertDialog.show();
       	 	}
        }
		
		//items toevoegen aan de spinner
		addItemsOnSpinner2();
		
		//listener toevoegen aan spinner
		addListenerOnSpinnerItemSelection();
		
		//controleren op fouten, anders doorgaan
		if(!error)
		{
			new JSONParse().execute();
		}
    }
    
    public static String setUserCat(String input)
    {
    	//categorie veranderen
    	userCat = input;
    	System.out.println("usercat = " + userCat);

		return userCat;
    }
    
    private boolean isNetworkAvailable() {
    	//controleren of het toestel verboden is met netwerk
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
    
    public String getData(){
    	//gegevens uit database halen
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
    	//trigger dialog
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
			 name = (TextView)findViewById(R.id.name);
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Ophalen lekkers ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show(); 
    	}
    	
    	@Override
        protected JSONObject doInBackground(String... args) {
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
		    	//string uit database omzetten naar json object
				String json1 = data;
				JSONObject jsonObject = new JSONObject(json1);
				
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
    		     // JSON object verwerken naar array
    			 jArray = json.getJSONArray(userCat);
    				for(int i = 0; i < jArray.length(); i++){
    				JSONObject c = jArray.getJSONObject(i);
    				
    				String name = c.getString(TAG_NAME);

    				// Adding value HashMap key => value
    				HashMap<String, String> map = new HashMap<String, String>();

    				map.put(TAG_NAME, name);
    				
    				oslist.add(map);
    				list=(ListView)findViewById(R.id.list);
    				
    				
    				ListAdapter adapter = new SimpleAdapter(MainActivity.this, oslist,
    						R.layout.list_v,
    						new String[] {TAG_NAME}, new int[] {R.id.name});
    				
    				list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							
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
    	//spinner menu items opzetten
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		List<String> list = new ArrayList<String>();
		list.add("vlaaien");
		list.add("cakes");
		list.add("bruidstaarten");
		list.add("verjaardagstaarten");
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(dataAdapter);
	}
	 
	public void addListenerOnSpinnerItemSelection() {
		//items toevoegen
		spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}
	 
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		//items toevoegen aan de actionbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	
    
}
