package unipi.iot.Client;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ){
    	CoapClientADN client= CoapClientADN.getInstance();
    	
    		
    		client.postJSON(0);
    		TimeUnit.SECONDS.sleep(5);
    		client.observe(0);
    		
    		
    }
}
