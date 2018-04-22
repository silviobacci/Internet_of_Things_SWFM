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
		Setup.init(getServletContext().getRealPath(config_folder));
		INManager.init();
		QueryManagerIN.init();
		INManager.start();
		System.out.println("INIT frontend/adnin");
	}
}
