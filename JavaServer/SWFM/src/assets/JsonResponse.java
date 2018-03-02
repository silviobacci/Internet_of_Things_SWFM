package assets;

import org.json.*;

public class JsonResponse {

	public String create(boolean error, Object info) {
		try {
			JSONObject json = new JSONObject();
			json.put("error", error);
			json.put("message", info);
			return json.toString();
		} 
		catch (Exception e) {
			return null;
		}
	}
}
