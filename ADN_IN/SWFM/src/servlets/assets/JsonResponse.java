package servlets.assets;

import org.json.simple.JSONObject;

public class JsonResponse {

	@SuppressWarnings("unchecked")
	public String create(boolean error, Object info) {
		try {
			JSONObject json = new JSONObject();
			json.put("error", error);
			json.put("message", info);
			return json.toJSONString().replace("\\", "");
		} 
		catch (Exception e) {
			return null;
		}
	}
}
