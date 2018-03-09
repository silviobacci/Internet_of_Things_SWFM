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
@WebServlet(name = "getmarkerdata", urlPatterns={"/backend/getmarkerdata"}, loadOnStartup = 1)
public class GetMarkerData extends HttpServlet {
	@Override
	public void init() throws ServletException {
		System.out.println("INIT backend/getmarkerdata");
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("application/json");
		QueryManagerIN mng = new QueryManagerIN();
		JSONArray response = mng.getMarkerData();
		
		if(response == null) {
			resp.getWriter().write(new JsonResponse().create(true, "Empty content."));
			return;
		}

		resp.getWriter().write(new JsonResponse().create(false, response));
	}
}
