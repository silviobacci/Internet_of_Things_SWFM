package resources;

import org.json.simple.JSONObject;

public class SubscriptionResource extends Resource {

	public SubscriptionResource(String _rn, Integer _ty, String _ri, String _pi, String _ct, String _lt) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
		// TODO Auto-generated constructor stub
	}

	public SubscriptionResource(String _rn, Integer _ty, String _ri) {
		super(_rn, _ty, _ri);
		// TODO Auto-generated constructor stub
	}

	public SubscriptionResource(String json, String type) {
		super(json, type);
		// TODO Auto-generated constructor stub
	}

	public SubscriptionResource(JSONObject created) {
		super(created);
		// TODO Auto-generated constructor stub
	}

}
