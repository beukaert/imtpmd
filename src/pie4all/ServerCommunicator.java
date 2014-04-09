package pie4all;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.util.Log;

public class ServerCommunicator implements Runnable
{
	private Activity activity;
	private Thread thread;
	private String typeOpdracht;
	public JSONObject serverBericht = null;
	public String json = "";
	
	public ServerCommunicator(Activity activity, String typeOpdr)
	{
		//we gebruiken de activity om de userinterface te updaten
		this.activity = activity;
		this.typeOpdracht = typeOpdr;

		//de nieuwe thread kan tekst verzenden en ontvangen van en naar een server
		this.setThread(new Thread(this));
		getThread().start();
	}

	//dit is een methode die niet op de UI thread wordt aangeroepen, maar door onze eigen nieuwe thread
	//we kunnen dus niet zomaar ontvangen berichten in een userinterface object stoppen m.b.v. view.setText( message )
	//hier gebruiken we de activity voor: activity.runOnUiThread( activity )
	@Override
	public void run()
	{
		try
		{
			Socket socket1 = new Socket();
			socket1.connect( new InetSocketAddress( "83.86.229.134", 4444 ), 4444 );
			
			//verzend een bericht naar de server
			this.sendMessage( "{ \""+typeOpdracht+"\" : \"\" }", socket1 );
			
			//wacht op een antwoord.
			final JSONObject respons = waitForResponse(socket1);
			
			if(respons instanceof JSONObject)
			{
				this.setServerBericht(respons);
				serverBericht = respons;
				System.out.println("ONTVANGEN3:" + serverBericht);
				
			}
			else{
				Log.i("Test", "not a json object");
			}
		}
		catch( UnknownHostException e )
		{
			Log.d("debug", "ServerCommunicator, can't find host");
			System.out.println("ServerCommunicator, can't find host");
		}
		catch( SocketTimeoutException e )
		{
			Log.d("debug", "ServerCommunicator, time-out");
			System.out.println("ServerCommunicator, time-out");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("ONTVANGEN4:" + serverBericht);
		getServerBericht();
	}
	

	//ook deze methoden kunnen niet naar de UI direct communiceren, hou hier rekening mee
	private void sendMessage( String message, Socket socket )
	{

		DataInputStream is;
		DataOutputStream os;
		boolean result = true;
		String noReset = "Could not reset.";
		String reset = "SEND TO SERVER.";
	 
		try {
			String string = message;
			is = new DataInputStream(socket.getInputStream());
			os = new DataOutputStream(socket.getOutputStream());
			PrintWriter pw = new PrintWriter(os);
			pw.println(string);
			pw.flush();
	 
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			JSONObject json = new JSONObject(in.readLine());
			if(!json.has("naam")) 
			{
				System.out.println("Could not reset.");
				result = false;
			}
			
			//is.close();
			//os.close();
	 
			}
			catch (IOException e) 
			{
				result = false;
				System.out.println(noReset);
				e.printStackTrace();			
			} 
			catch (JSONException e) 
			{
				result = false;
				System.out.println(noReset);
				e.printStackTrace();
			}
		
	}
	
	
	/*private void jsonToArray(JSONArray bestelInfo ) throws IOException
	{
		JSONObject bestelling = (JSONObject) bestelInfo.get(0);
		JSONObject koper = (JSONObject) bestelInfo.get(1);

		String productNaam = bestelling.get( "productnaam" ).toString();
		String productAantal = bestelling.get( "productaantal" ).toString();
		
		String koperNaam = koper.get( "kopernaam" ).toString();
		String koperAdres = koper.get( "koperadres" ).toString();
		String koperTelNr = koper.get( "kopertelnr" ).toString();
		String koperEmail = koper.get( "koperemail" ).toString();
		
	}*/

	
	
	//wacht op server bericht (na versturen)
	private JSONObject waitForResponse(Socket socket) throws JSONException
	{
		BufferedReader bufferedReader = null;
		String returnMessage = null;
		try
		{
			InputStream inputStream = socket.getInputStream();
			//InputStreamReader inputStreamReader = new InputStreamReader( inputStream );
			//bufferedReader = new BufferedReader( inputStreamReader );
			
			
			//setServerBericht(json);
			
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				
				json = reader.readLine();
				System.out.println("json IS BINNEN:" + json);
				
				
				while ((line = reader.readLine()) != null) {
					
				}
				//inputStream.close();
				//json = sb.toString();
			} catch (Exception e) {
				//Log.e("System.out", "Error converting result " + e.toString());
				//System.out.println("Error converting result" + json);
			}
			
			
			// try parse the string to a JSON object
			try {
				JSONArray ja = new JSONArray(json);
				JSONObject jo = new JSONObject();
				
				// populate the array
				jo.put("CATEGORIES",ja);
				serverBericht = jo;
				
			} catch (JSONException e) {
				Log.e("System.out", "Error parsing data " + e.toString());
				System.out.println("Error parsing data " + json);
			}

			// return JSON object
			return serverBericht;

		}
		
		catch (IOException e1){
			e1.printStackTrace();
			InetAddress adress = socket.getInetAddress();
			Log.d("debug", "Can't create inputStreamReader to talk to client " + adress);
			Log.d("debug", e1.getMessage());
		}
		
		System.out.println("ONTVANGEN1:" + serverBericht);
		return serverBericht;
	}

	public Thread getThread(){
		return thread;
	}

	public void setServerBericht(JSONObject respons){
		this.serverBericht = respons;
	}
	
	public void setThread(Thread thread){
		this.thread = thread;
	}

	public JSONObject getServerBericht(){
		System.out.println("serverbericht:" + serverBericht);
		return serverBericht;
	}

}