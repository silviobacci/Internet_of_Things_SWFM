package servlets.backend.getters;

import servlets.assets.notifications.ServerNotifier;

public interface AsyncContextInterface {
	public abstract void deleteContext(ServerNotifier context);
}
