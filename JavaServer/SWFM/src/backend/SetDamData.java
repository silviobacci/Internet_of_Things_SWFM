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
@WebServlet(name = "setdamdata", urlPatterns={"/backend/setdamdata"}, loadOnStartup = 1)
public class SetDamData extends HttpServlet {
	@Override
	public void init() throws ServletException {
		System.out.println("INIT backend/setdamdata");
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("text/plain");
		resp.getWriter().println("GET RESPONSE FROM: setdamdata");
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		QueryManagerIN mng = new QueryManagerIN();
		String ae_id = req.getParameter("ae_id");
		String dam_id = req.getParameter("dam_id");
		String data = req.getParameter("data");
		
		if(ae_id == null || dam_id == null || data == null) {
			resp.getWriter().write(new JsonResponse().create(true, "Some empty fileds."));
			return;
		}
		
		String admin_name = (String) req.getSession().getAttribute("username");
		
		if(!mng.changeDamState(ae_id, dam_id, data, admin_name)) {
			resp.getWriter().write(new JsonResponse().create(true, "State unchanged."));
			return;
		}

		resp.getWriter().write(new JsonResponse().create(false, "State changed."));
	}
}
