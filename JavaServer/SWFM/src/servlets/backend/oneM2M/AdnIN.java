package servlets.backend.oneM2M;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import oneM2M.INManager;
import oneM2M.configuration.Setup;
import servlets.assets.oneM2M.QueryManagerIN;

@SuppressWarnings("serial")
@WebServlet(name = "adnin", urlPatterns={"/onem2m/adnin"}, loadOnStartup = 1)
public class AdnIN  extends HttpServlet {
	private final String config_folder = "/configuration/";
	@Override
	public void init() throws ServletException {
		System.out.println("INIT frontend/adnin");
		new StarterINManager().start();
	}
	
	private class StarterINManager extends Thread {
		@Override
		public void run() {
			super.run();
			Setup.init(getServletContext().getRealPath(config_folder));
			INManager.init();
			QueryManagerIN.init();
			INManager.start();
		}
	}
}
