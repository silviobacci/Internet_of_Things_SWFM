package assets;

import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

public class ServerNotifier{
	private HttpServletRequest req;
	private HttpServletResponse resp;
	private AsyncContext context;
	
	private AsyncContext createNewContext() {
		resp.setContentType("text/event-stream");    
		resp.setCharacterEncoding("UTF-8");
		
		final AsyncContext asyncContext = req.startAsync(req, resp);
		asyncContext.setTimeout(0);
		return asyncContext;
	}
	
	public ServerNotifier(HttpServletRequest request, HttpServletResponse response) {
		req = request;
		resp = response;
		
		context = createNewContext();
	}
	
	public boolean sendNotification(JSONArray response){
		try{
			PrintWriter writer = context.getResponse().getWriter();
				
			if(writer == null)
				return false;
			
			if(response == null || response.isEmpty())
				writer.write("data: " + new JsonResponse().create(true, "Empty content.") + "\n\n");
			else
				writer.write("data: " +  new JsonResponse().create(false, response) + "\n\n");
			
			writer.flush();
		}
		catch (Exception e){
			e.printStackTrace();
			context.complete();
			return false;
		}
		
		return true;
	}
}
