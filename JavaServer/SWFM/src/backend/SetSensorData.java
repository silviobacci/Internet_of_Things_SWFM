package backend;

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

import assets.JsonResponse;
import assets.QueryManagerIN;

@SuppressWarnings("serial")
@WebServlet(name = "setsensordata", urlPatterns={"/backend/setsensordata"}, loadOnStartup = 1)
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
		
		if(payload == null || payload.get("id") == null || payload.get("data") == null || payload.get("ae") == null) {
			resp.getWriter().write(new JsonResponse().create(true, "Some problem with the post request."));
			return;
		}
		
		String admin_name = (String) req.getSession().getAttribute("username");
		String ae_id = (String) payload.get("ae");
		String sensor_id = (String) payload.get("id");
		JSONObject data = (JSONObject) payload.get("data");
		
		if(!mng.setSensorData(ae_id, sensor_id, data.toJSONString().replace("\"", "'"), admin_name)) {
			resp.getWriter().write(new JsonResponse().create(true, "State unchanged."));
			return;
		}

		resp.getWriter().write(new JsonResponse().create(false, "State changed."));
	}
}
