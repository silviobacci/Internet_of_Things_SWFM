package backend;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import assets.QueryManagerIN;
import assets.ServerNotifier;
import assets.ServerNotifierMarker;

@SuppressWarnings("serial")
@WebServlet(name = "getmarkerdata", urlPatterns={"/backend/getmarkerdata"}, asyncSupported = true, loadOnStartup = 1)
public class GetMarkerData extends HttpServlet {
	private ConcurrentMap<String, ServerNotifier> contexts = new ConcurrentHashMap<>();
	
	@Override
	public void init() throws ServletException {
		System.out.println("INIT backend/getmarkerdata");
	}
	
	private boolean alreadyRegistered(HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("username");
		
		if(username == null)
			return false;
		
		if(contexts.containsKey(username))
			return true;
		else
			return false;
	}
	
	private boolean createNewContext(HttpServletRequest req, HttpServletResponse resp) {
		String username = (String) req.getSession().getAttribute("username");
		
		if (username == null)
			return false;
		
		ServerNotifierMarker notifier = new ServerNotifierMarker(req, resp);
		
		contexts.put(username, notifier);
		
		return true;
	}
	
	private void sendNotification(HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("username");

		contexts.get(username).sendNotification(new QueryManagerIN().getMarkerData());
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		createNewContext(req, resp);
		
		if(alreadyRegistered(req))
			sendNotification(req);
	}
}
