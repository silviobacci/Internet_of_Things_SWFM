package oneM2M.notifications;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ObservableResource extends Observable {
	ArrayList<Observer> observers = new ArrayList<Observer>();

	public ObservableResource() {}
	
	public void notify(Object obj) {
		this.setChanged();
		this.notifyObservers(obj);
	}

	@Override
	public synchronized void addObserver(Observer o) {
		for(Observer ob : observers)
			if(ob == o)
				return;
		
		observers.add(o);
		super.addObserver(o);
	}
	
	@Override
	public synchronized void deleteObserver(Observer o) {
		for(Observer ob : observers)
			if(ob == o) {
				observers.remove(o);
				super.deleteObserver(o);
				return;
			}
	}
}
