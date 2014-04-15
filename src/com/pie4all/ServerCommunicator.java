package com.pie4all;

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

import android.app.Activity;
import android.util.Log;

public class ServerCommunicator implements Runnable
{
	private Activity activity;
	private Thread thread;
	private String typeOpdracht;
	public JSONObject serverBericht = null;
	public String json = "";
	public String jArrayName = "";


	public ServerCommunicator(Activity activity, String jArrayName, String typeOpdr)
	{
		this.activity = activity;
		this.typeOpdracht = typeOpdr;
		this.jArrayName = jArrayName;

		this.setThread(new Thread(this));
		getThread().start();
	}


	@Override
	public void run()
	{
		try
		{
			Socket socket1 = new Socket();
			socket1.connect( new InetSocketAddress( "83.86.229.134", 4444 ), 4444 );

			//verzend een bericht naar de server
			this.sendMessage(typeOpdracht, socket1);

			//wacht op een antwoord.
			final JSONObject respons = waitForResponse(socket1);

			if(respons instanceof JSONObject)
			{
				this.setServerBericht(respons);
				serverBericht = respons;
				System.out.println("respons");

			}
			else{
				Log.i("Test", "not a json object");
			}
		}
		catch( UnknownHostException e )
		{
			System.out.println("ServerCommunicator, can't find host");
		}
		catch( SocketTimeoutException e )
		{
			System.out.println("ServerCommunicator, time-out");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//System.out.println("ONTVANGEN4:" + serverBericht);
		getServerBericht();
	}


//ook deze methoden kunnen niet naar de UI direct communiceren, hou hier rekening mee
	private void sendMessage( String message, Socket socket )
	{

		DataInputStream is;
		DataOutputStream os;

		try {
			String string = message;
			is = new DataInputStream(socket.getInputStream());
			os = new DataOutputStream(socket.getOutputStream());
			PrintWriter pw = new PrintWriter(os);
			pw.println(string);
			pw.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			in.readLine();

			}
			catch (IOException e) 
			{
				System.out.println("Connection error!");	
			} 

	}

	//wacht op server bericht (na versturen)
	private JSONObject waitForResponse(Socket socket) throws JSONException
	{

		try
		{
			InputStream inputStream = socket.getInputStream();

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
				json = reader.readLine();
				inputStream.close();
				
			} catch (Exception e) {
				System.out.println("Error converting result" + json);
			}


			// try parse the string to a JSON object
			try {
				JSONArray ja = new JSONArray(json);
				JSONObject jo = new JSONObject();

				// populate the array
				jo.put(jArrayName,ja);
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
		//System.out.println("serverbericht:" + serverBericht);
		return serverBericht;
	}

}