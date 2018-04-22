package servlets.backend;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@WebServlet(name = "logout", urlPatterns={"/backend/logout"}, loadOnStartup = 2)
public class Logout extends HttpServlet {
	@SuppressWarnings("unchecked")
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
		System.out.println("INIT backend/logout");
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("application/json");
		HttpSession session = req.getSession();
		if(session.getAttribute("username") != null)
			session.invalidate();
		resp.getWriter().write(createResponse(false, "Logout done."));
	}
}