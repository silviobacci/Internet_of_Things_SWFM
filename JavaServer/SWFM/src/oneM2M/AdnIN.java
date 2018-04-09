package oneM2M;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "adnin", urlPatterns={"/onem2m/adnin"}, loadOnStartup = 1)
public class AdnIN  extends HttpServlet {
	@Override
	public void init() throws ServletException {
		System.out.println("INIT frontend/adnin");
		new StarterINManager().start();
	}
	
	private class StarterINManager extends Thread {
		private final int COAP_PORT = 5685;
		private final String RESOURCE_NAME = "notification_resource";
		private final String IP_ADDRESS_OM2M = "127.0.0.1";
		
		@Override
		public void run() {
			new INManager(IP_ADDRESS_OM2M, RESOURCE_NAME, COAP_PORT);
		}
	}
}
