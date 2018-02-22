package frontend;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import assets.DbManager;
import assets.JsonResponse;

@SuppressWarnings("serial")
@WebServlet(name = "login", urlPatterns={"/frontend/login"}, loadOnStartup = 1)
public class Login extends HttpServlet {
	
	private HttpSession createSession(HttpServletRequest req, HashMap<String, Object> user) {
		HttpSession session = req.getSession();
		try {
	        session.setAttribute("email", user.get("email"));
	        session.setAttribute("username", user.get("username"));
	        session.setAttribute("name", user.get("name"));
	        session.setAttribute("surname", user.get("surname"));
	        session.setAttribute("avatar", user.get("avatar"));
	        session.setAttribute("cover", user.get("cover"));
	        session.setAttribute("admin", user.get("admin"));
	        
	        return session;
		}
		catch(Exception e){
			return  null;
		}  
	}
	
	@Override
	public void init() throws ServletException {
		System.out.println("INIT frontend/login");
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("text/plain");
		resp.getWriter().println("RISPOSTA");
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{	
		DbManager db = new DbManager();
		
		resp.setContentType("text/plain");
		
		if(req.getParameter("username") == null || req.getParameter("password") == null) {
			resp.getWriter().write(new JsonResponse().create(true, "You have left a field empty."));
			return;
		}
		
		String usr = req.getParameter("username");
		String pwd = db.encrypt_password(req.getParameter("password"));
		HashMap<String, Object> user = null;
		
		try {
			user = db.login(usr, pwd);
		} 
		catch (Exception e) {
			resp.getWriter().write(new JsonResponse().create(true, "Error while performing query."));
		}
		
		if (user == null) {
			resp.getWriter().write(new JsonResponse().create(true, "Authentication failed."));
			return;
		}
		
		if(createSession(req, user) != null)
			resp.getWriter().write(new JsonResponse().create(false, "Login completed."));
		else
			resp.getWriter().write(new JsonResponse().create(true, "Error while retreiving fields."));
	}
}
