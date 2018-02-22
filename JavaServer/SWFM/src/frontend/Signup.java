package frontend;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import assets.DbManager;
import assets.JsonResponse;

@SuppressWarnings("serial")
@WebServlet(name = "frontend_signup", urlPatterns={"/frontend/signup"}, loadOnStartup = 1)
public class Signup extends HttpServlet {
	@Override
	public void init() throws ServletException {
		System.out.println("INIT frontend/signup");
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("text/plain");
		resp.getWriter().println();
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		DbManager db = new DbManager();
		
		resp.setContentType("text/plain");
		
		String email = req.getParameter("email");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String name = req.getParameter("name");
		String surname = req.getParameter("surname");
		
		if (email == null || username == null || password == null || name == null || surname == null) {
			resp.getWriter().write(new JsonResponse().create(true, "You have left a field empty."));
			return;
		}
		
		try {
			if(req.getSession().getAttribute("username") == username || db.alreadyExist(username)) {
				resp.getWriter().write(new JsonResponse().create(true, "User already exists."));
				return;
			}
			
			if(db.signup(req.getParameterMap()))
				resp.getWriter().write(new JsonResponse().create(false, "Signup complete."));
			else
				resp.getWriter().write(new JsonResponse().create(true, "Signup failed."));
		}
		catch (Exception e) {
			resp.getWriter().write(new JsonResponse().create(true, "Signup failed."));
		}
	}
}
