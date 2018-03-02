package frontend;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import assets.JsonResponse;

@SuppressWarnings("serial")
@WebServlet(name = "redirect", urlPatterns={"/frontend/redirect", "/backend/redirect"}, loadOnStartup = 1)
public class Redirect extends HttpServlet {
	@Override
	public void init() throws ServletException {
		System.out.println("INIT frontend/redirect");
		System.out.println("INIT backend/redirect");
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("text/plain");
		resp.getWriter().println("GET RESPONSE FROM: redirect");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("application/json");
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
				resp.getWriter().write(new JsonResponse().create(true, "Error creating json object."));
			}
		}
		else
			resp.getWriter().write(new JsonResponse().create(true, "Not yet logged."));
	}
}