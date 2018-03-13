package backend;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

import assets.JsonResponse;
import assets.QueryManagerIN;

@SuppressWarnings("serial")
@WebServlet(name = "getsensordata", urlPatterns={"/backend/getsensordata"}, loadOnStartup = 1)
public class GetSensorData extends HttpServlet {
	@Override
	public void init() throws ServletException {
		System.out.println("INIT backend/getsensordata");
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("text/plain");
		resp.getWriter().println("GET RESPONSE FROM: getsensordata");
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		QueryManagerIN mng = new QueryManagerIN();
		String id = req.getParameter("id");
		String data = req.getParameter("data");
		if(id == null || data == null) {
			resp.getWriter().write(new JsonResponse().create(true, "Some empty fileds."));
			return;
		}
		
		JSONArray response = null;
		
		switch(data) {
			case QueryManagerIN.LEVEL:
				response = mng.getSensorLevel(id);
				break;
			case QueryManagerIN.THRESHOLD:
				response = mng.getSensorThreshold(id);
				break;
			case QueryManagerIN.GPS:
				response = mng.getSensorPosition(id);
				break;
		}
		
		if(response == null) {
			resp.getWriter().write(new JsonResponse().create(true, "Empty content."));
			return;
		}

		resp.getWriter().write(new JsonResponse().create(false, response));
	}
}
