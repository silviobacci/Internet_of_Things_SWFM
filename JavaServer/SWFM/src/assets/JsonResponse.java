package assets;

import org.json.simple.JSONObject;

public class JsonResponse {

	@SuppressWarnings("unchecked")
	public String create(boolean error, Object info) {
		try {
			JSONObject json = new JSONObject();
			json.put("error", error);
			json.put("message", info);
			System.out.println(Thread.currentThread().getId() + " " + json.toJSONString());
			return json.toJSONString().replace("\\", "");
		} 
		catch (Exception e) {
			return null;
		}
	}
}
