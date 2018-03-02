package backend;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		resp.setContentType("application/json");
		QueryManagerIN mng = new QueryManagerIN();
		String ae_id = req.getParameter("ae_id");
		String sensor_id = req.getParameter("sensor_id");
		String data = req.getParameter("data");
		
		if(ae_id == null || sensor_id == null || data == null) {
			resp.getWriter().write(new JsonResponse().create(true, "Some empty fileds."));
			return;
		}
		
		String admin_name = (String) req.getSession().getAttribute("username");
		
		if(!mng.changeSensorThreshold(ae_id, sensor_id, data, admin_name)) {
			resp.getWriter().write(new JsonResponse().create(true, "State unchanged."));
			return;
		}

		resp.getWriter().write(new JsonResponse().create(false, "State changed."));
	}
}
