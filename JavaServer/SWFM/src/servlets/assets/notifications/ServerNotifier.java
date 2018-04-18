package servlets.assets.notifications;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import oneM2M.INManager;
import oneM2M.resources.ReferenceResource;
import servlets.assets.JsonResponse;
import servlets.backend.getters.AsyncContextInterface;

public abstract class ServerNotifier implements Observer {
	private static final int TIMEOUT = 300000;
	private AsyncContextInterface servlet;
	
	private AsyncContext context;
	private CompleteListener listener;
	
	private AsyncContext createNewContext(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/event-stream");    
		response.setCharacterEncoding("UTF-8");
		
		final AsyncContext asyncContext = request.startAsync(request, response);
		asyncContext.setTimeout(TIMEOUT);
		return asyncContext;
	}
	
	public ServerNotifier(AsyncContextInterface f, HttpServletRequest request, HttpServletResponse response) {
		servlet = f;
		
		context = createNewContext(request, response);
		listener = new CompleteListener(this);
		context.addListener(listener);
		
		INManager.getObservableResource().addObserver(this);
		
		addObserver();
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
			return false;
		}
		
		return true;
	}
	
	protected abstract void deleteObserver();
	
	protected abstract void addObserver();
	
	protected abstract void update(JSONObject json);
	
	@Override
	public void update(Observable o, Object arg) {
		new Notifier(arg).start();
	}
	
	private class Notifier extends Thread {
		Object arg;
		
		public Notifier(Object a) {
			arg = a;
		}
		
		@Override
		public void run() {
			if(arg instanceof ReferenceResource)
				addObserver();
			else if(arg instanceof JSONObject)
				update((JSONObject) arg);
			super.run();
		}
	}
	
	private class CompleteListener implements AsyncListener {
		ServerNotifier notifier;
		
		public CompleteListener(ServerNotifier n) {
			notifier = n;
		}
		
		@Override
		public void onComplete(AsyncEvent event) throws IOException {
			if(!context.getResponse().isCommitted())
				servlet.deleteContext(notifier);
			deleteObserver();
		}

		@Override
		public void onTimeout(AsyncEvent event) throws IOException {
			context.complete();
		}

		@Override
		public void onError(AsyncEvent event) throws IOException {
		}

		@Override
		public void onStartAsync(AsyncEvent event) throws IOException {
		}
		
	}
}
