package servlets.backend.setters;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import servlets.assets.JsonResponse;
import servlets.assets.oneM2M.QueryManagerIN;

@SuppressWarnings("serial")
@WebServlet(name = "setsensordata", urlPatterns={"/backend/setsensordata"}, loadOnStartup = 2)
public class SetSensorData extends HttpServlet {
	@Override
	public void init() throws ServletException {
		System.out.println("INIT backend/setsensordata");
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("text/plain");
		resp.getWriter().println("GET RESPONSE FROM: setsensordata");
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
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
		String admin_name = (String) req.getSession().getAttribute("username");
		JSONObject data = (JSONObject) payload.get("data");
		
		if(reference_id == null || ae_id == null || ae_name == null || sensor_id == null || admin_name == null || data == null) {
			resp.getWriter().write(new JsonResponse().create(true, "Some problem with the post request."));
			return;
		}
		
		if(!QueryManagerIN.setSensorData(reference_id, ae_id, ae_name, sensor_id, data, admin_name)) {
			resp.getWriter().write(new JsonResponse().create(true, "Request is impossible to send."));
			return;
		}

		resp.getWriter().write(new JsonResponse().create(false, "Request sent: please wait."));
	}
}
