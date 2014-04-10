package com.pie4all;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
	Button Btngetdata;
	ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
	
	public ServerCommunicator serverCommunicator1;
	public ServerCommunicator serverCommunicator2;
	public ServerCommunicator serverCommunicator3;
	public ServerCommunicator serverCommunicator4;
	public ServerCommunicator serverCommunicator5;
	public DBHandler db;
	private Spinner spinner2;
	public SQLiteDatabase myDB= null;

	
	//JSON Node Names 
	private static final String TAG_OS = "CATEGORIES";
	private static final String TAG_VER = "ver";
	private static final String TAG_NAME = "naam";
	private static final String TAG_API = "api";
	
	JSONArray jArray = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        setContentView(R.layout.activity_main);
        oslist = new ArrayList<HashMap<String, String>>();

        myDB = this.openOrCreateDatabase("pie4allDB", MODE_PRIVATE, null);
        
        //controleren of er een netwerkverbinding is
        if(isNetworkAvailable()){
        	System.out.println("Er is internet!");
        	//serverCommunicator1 = new ServerCommunicator(this, "categories", "{ \"categorielijst\" : \"\" }");
            //serverCommunicator2 = new ServerCommunicator(this, "vlaaien", "{ \"productenlijst\" : \"Vlaaien\" }");
            //serverCommunicator3 = new ServerCommunicator(this, "cakes", "{ \"productenlijst\" : \"Cakes\" }");
            //serverCommunicator4 = new ServerCommunicator(this, "bruidstaarten", "{ \"productenlijst\" : \"Bruidstaarten\" }");
            //serverCommunicator5 = new ServerCommunicator(this, "verjaardagstaarten", "{ \"productenlijst\" : \"Verjaardagstaarten\" }");
        
        	/* Create a Database. */
			  try {
			   /* Create a Table in the Database. */
			   myDB.execSQL("CREATE TABLE IF NOT EXISTS "
			     + "categories"
			     + " (naam VARCHAR);");
			 
			   /* Insert data to a Table
			   myDB.execSQL("INSERT INTO "
			     + "categories"
			     + " (naam)"
			     + " VALUES ('Vlaaien');");*/
			  
			  }
			  catch(Exception e) {
			   Log.e("Error", "Error", e);
			  }
        }
        else{
        	System.out.println("Er is geen internet!");
        	Cursor c = myDB.rawQuery("SELECT * FROM categories WHERE naam = 'Vlaaien'", null);
       	 	c.moveToFirst();
       	 
       	 	int Column1 = c.getColumnIndex("naam");
       	 
       	 	if (c != null) {
    		    // Loop through all Results
       		 String data = "";
    		    do {
    		     String Name = c.getString(Column1);
    		     //int Age = c.getInt(Column2);
    		     data = data +Name+"\n";
    		    }while(c.moveToNext());
    		    
    		    System.out.println("Oude data beschikbaar!");
       	 	}
       	 	else{
       	 		
       	 	}
        }
		
		  
		addItemsOnSpinner2();
		addListenerOnSpinnerItemSelection();
		
		//getData();
		//new JSONParse().execute();
		
		if (myDB != null)
		{
			myDB.close();
		}
    }
    
    private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
    
    public void getData(){
    	 Cursor c = myDB.rawQuery("SELECT * FROM categories WHERE naam = 'Vlaaien'", null);
    	 c.moveToFirst();
    	 
    	 int Column1 = c.getColumnIndex("naam");
    	 
    	 if (c != null) {
 		    // Loop through all Results
    		 String data = "";
    		 
 		    do {
 		     String Name = c.getString(Column1);
 		     //int Age = c.getInt(Column2);
 		     data = data +Name+"\n";
 		    }while(c.moveToNext());
 		    
 		    System.out.println("query gelukt!" + data);
 			 
 		 }
    	 
    	 
    	 
    }
    
    private class JSONParse extends AsyncTask<String, String, JSONObject> {
    	 private ProgressDialog pDialog;
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
             ver = (TextView)findViewById(R.id.vers);
			 name = (TextView)findViewById(R.id.name);
			 api = (TextView)findViewById(R.id.api);
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show(); 
    	}
    	
    	@Override
        protected JSONObject doInBackground(String... args) {

    		JSONObject json = serverCommunicator1.getServerBericht();
    		//JSONObject json = serverCommunicator.getCategories();
    		//JSONObject json = serverCommunicator.getProducts();
    		
    		return json;
    	}
    	 @Override
         protected void onPostExecute(JSONObject json) {
    		 pDialog.dismiss();
    		 try {
    				// Getting JSON Array from URL
    			 jArray = json.getJSONArray(TAG_OS);
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
    						new String[] { TAG_VER,TAG_NAME, TAG_API }, new int[] {
    								R.id.vers,R.id.name, R.id.api});

    				
    				
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
	 
	  // get the selected dropdown list value


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
