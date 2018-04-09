package backend;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import assets.QueryManagerIN;
import assets.ServerNotifier;
import assets.ServerNotifierSensor;

@SuppressWarnings("serial")
@WebServlet(name = "getsensordata", urlPatterns={"/backend/getsensordata"}, asyncSupported = true, loadOnStartup = 1)
public class GetSensorData extends HttpServlet {
	private ConcurrentMap<String, ServerNotifier> contexts = new ConcurrentHashMap<>();
	
	@Override
	public void init() throws ServletException {
		System.out.println("INIT backend/getsensordata");
	}
	
	private boolean alreadyRegistered(HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("username");
		
		if(username == null)
			return false;
		
		if(contexts.containsKey(username))
			return true;
		else
			return false;
	}
	
	private boolean createNewContext(HttpServletRequest req, HttpServletResponse resp, String reference_id, String ae_id, String ae_name) {
		String username = (String) req.getSession().getAttribute("username");
		
		if (username == null)
			return false;
		
		ServerNotifierSensor notifier = new ServerNotifierSensor(req, resp, reference_id, ae_id, ae_name);
		
		contexts.put(username, notifier);
		
		return true;
	}
	
	private void sendNotification(HttpServletRequest req, String reference_id, String ae_id, String ae_name) {
		String username = (String) req.getSession().getAttribute("username");

		contexts.get(username).sendNotification(new QueryManagerIN().getSensorData(reference_id, ae_id, ae_name));
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String reference_id = req.getParameter("reference_id");
		String ae_id = req.getParameter("ae_id");
		String ae_name = req.getParameter("ae_name");
		
		if(reference_id == null || ae_id == null || ae_name == null)
			return;
		
		createNewContext(req, resp, reference_id, ae_id, ae_name);
		
		if(alreadyRegistered(req))
			sendNotification(req, reference_id, ae_id, ae_name);
	}
	
	/*
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		QueryManagerIN mng = new QueryManagerIN();
	
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
		JSONObject payload = null;
		
		try {
			payload = (JSONObject) JSONValue.parseWithException(jb.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(payload == null) {
			resp.getWriter().write(new JsonResponse().create(true, "Some problem with the post request."));
			return;
		}
		
		String reference_id = (String) payload.get("reference_id");
		String ae_id = (String) payload.get("ae_id");
		String ae_name = (String) payload.get("ae_name");
		String sensor_id = (String) payload.get("sensor_id");
		
		if(reference_id == null || ae_id == null || ae_name == null) {
			resp.getWriter().write(new JsonResponse().create(true, "Some problem with the post request."));
			return;
		}
		
		JSONArray response = null;
		
		if(sensor_id == null)
			response = mng.getSensorData(reference_id, ae_id, ae_name);
		else
			response = mng.getSensorData(reference_id, ae_id, ae_name, sensor_id);
		
		if(response == null) {
			resp.getWriter().write(new JsonResponse().create(true, "Empty content."));
			return;
		}

		resp.getWriter().write(new JsonResponse().create(false, response));
	}
	*/
}
