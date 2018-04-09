package unipi.iot.Client;

public class provaThread {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Requester r1 = new Requester();
		Requester r2= new Requester();
		
		r1.start();
		r2.start();
	}

}
