package frontend;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.json.JSONObject;

@SuppressWarnings("serial")
@WebServlet(name = "logout", urlPatterns={"/dashboard/logout"}, loadOnStartup = 2)
public class Logout extends HttpServlet {
	private String createResponse(boolean error, String info) {
		try {
			JSONObject json = new JSONObject();
			json.put("error", error);
			json.put("message", info);
			return json.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public void init() throws ServletException {
		System.out.println("INIT dashboard/logout");
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("text/plain");
		resp.getWriter().println();
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		HttpSession session = req.getSession();
		if(session.getAttribute("username") != null)
			session.invalidate();
		resp.getWriter().write(createResponse(false, "Logout done."));
	}
}