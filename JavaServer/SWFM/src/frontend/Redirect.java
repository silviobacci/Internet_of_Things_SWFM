package frontend;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.json.JSONObject;

@SuppressWarnings("serial")
@WebServlet(name = "redirect", urlPatterns={"/frontend/redirect", "/dashboard/redirect"}, loadOnStartup = 2)
public class Redirect extends HttpServlet {
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
		System.out.println("INIT frontend/redirect");
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("text/plain");
		resp.getWriter().println("RISPOSTA");
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("text/plain");
		HttpSession session = req.getSession();
		
		if(session.getAttribute("username") != null) {
			try {
				JSONObject message = new JSONObject();
				message.put("email", session.getAttribute("email"));
				message.put("username", session.getAttribute("username"));
				message.put("name", session.getAttribute("name"));
				message.put("surname", session.getAttribute("surname"));
				message.put("avatar", session.getAttribute("avatar"));
				message.put("cover", session.getAttribute("cover"));
				message.put("admin", session.getAttribute("admin"));
				
				JSONObject json = new JSONObject();
				json.put("error", false);
				json.put("message", message);
				resp.getWriter().write(json.toString());
			}
			catch(Exception e) {
				resp.getWriter().write(createResponse(true, "Error creating json object."));
			}
		}
		else
			resp.getWriter().write(createResponse(true, "Not yet logged."));
	}
}