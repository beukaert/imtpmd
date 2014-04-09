package pie4all;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	public ServerCommunicator serverCommunicator;

	// constructor
	public JSONParser() {

	}

	public JSONObject getJSONFromUrl(String url) {

		serverCommunicator = new ServerCommunicator(null, "categorielijst");
		JSONObject jObj = serverCommunicator.serverBericht;
		JSONObject test2 = serverCommunicator.getServerBericht();
		System.out.println("FU:" + jObj + test2);
		
		/*try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}*/

		// return JSON String
		return jObj;

	}
}
