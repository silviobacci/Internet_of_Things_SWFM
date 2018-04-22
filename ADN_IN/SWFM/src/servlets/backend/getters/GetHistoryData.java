package servlets.backend.getters;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.assets.notifications.ServerNotifier;
import servlets.assets.notifications.ServerNotifierHistory;
import servlets.assets.oneM2M.QueryManagerIN;

@SuppressWarnings("serial")
@WebServlet(name = "gethistorydata", urlPatterns={"/backend/gethistorydata"}, asyncSupported = true, loadOnStartup = 2)
public class GetHistoryData  extends HttpServlet implements AsyncContextInterface {
	private ArrayList<ServerNotifier> contexts;

	@Override
	public void init() throws ServletException {
		contexts = new ArrayList<ServerNotifier>();
		System.out.println("INIT backend/gethistorydata");
	}
	
	@Override
	public void deleteContext(ServerNotifier context) {
		if(contexts.contains(context))
			contexts.remove(context);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String reference_id = req.getParameter("reference_id");
		String ae_id = req.getParameter("ae_id");
		String ae_name = req.getParameter("ae_name");
		String sensor_id = req.getParameter("sensor_id");
		
		if(reference_id == null || ae_id == null || ae_name == null || sensor_id == null)
			return;
		
		ServerNotifierHistory notifier = new ServerNotifierHistory(this, req, resp, reference_id, ae_id, ae_name, sensor_id);
		
		contexts.add(notifier);
		
		notifier.sendNotification(QueryManagerIN.getSensorHistory(reference_id, sensor_id));
	}
}
