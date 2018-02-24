package frontend;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@SuppressWarnings("serial")
@WebServlet(name = "getdata", urlPatterns={"/dashboard/getdata"}, loadOnStartup = 1)
public class GetData extends HttpServlet {
	
	@Override
	public void init() throws ServletException {
		System.out.println("INIT frontend/getdata");
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{	
	}
}
