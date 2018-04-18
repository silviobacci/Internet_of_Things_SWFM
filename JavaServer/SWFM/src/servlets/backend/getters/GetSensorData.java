package servlets.backend.getters;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.assets.notifications.ServerNotifier;
import servlets.assets.notifications.ServerNotifierSensor;
import servlets.assets.oneM2M.QueryManagerIN;

@SuppressWarnings("serial")
@WebServlet(name = "getsensordata", urlPatterns={"/backend/getsensordata"}, asyncSupported = true, loadOnStartup = 1)
public class GetSensorData extends HttpServlet implements AsyncContextInterface {
	private ArrayList<ServerNotifier> contexts;
	
	@Override
	public void init() throws ServletException {
		contexts = new ArrayList<ServerNotifier>();
		System.out.println("INIT backend/getsensordata");
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
		
		if(reference_id == null || ae_id == null || ae_name == null)
			return;
		
		ServerNotifierSensor notifier = new ServerNotifierSensor(this, req, resp, reference_id, ae_id, ae_name);
		
		contexts.add(notifier);
		
		notifier.sendNotification(QueryManagerIN.getSensorData(reference_id, ae_id, ae_name));
	}
}
