package servlets.backend.getters;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.assets.notifications.ServerNotifier;
import servlets.assets.notifications.ServerNotifierMarker;
import servlets.assets.oneM2M.QueryManagerIN;

@SuppressWarnings("serial")
@WebServlet(name = "getmarkerdata", urlPatterns={"/backend/getmarkerdata"}, asyncSupported = true, loadOnStartup = 2)
public class GetMarkerData extends HttpServlet implements AsyncContextInterface {
	private ArrayList<ServerNotifier> contexts;
	
	@Override
	public void init() throws ServletException {
		contexts = new ArrayList<ServerNotifier>();
		System.out.println("INIT backend/getmarkerdata");
	}
	
	@Override
	public void deleteContext(ServerNotifier context) {
		if(contexts.contains(context))
			contexts.remove(context);
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		ServerNotifierMarker notifier = new ServerNotifierMarker(this, req, resp);
		
		contexts.add(notifier);
		
		notifier.sendNotification(QueryManagerIN.getMarkerData());
	}
}
