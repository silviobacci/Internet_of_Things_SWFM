package interaction;

import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import communication.CoapClientADN;
import configuration.Setup;

public class GUI {

	private JFrame			frame;

	private static CoapClientADN	context	=	new CoapClientADN();
	public static Setup 	s 		= 	Setup.getInstance();
	
	public static void main(String[] args)  {
		s.start();
		try {
			s.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	context.setWInstance(s.getWinstance());
		context.start();
		
		
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				try {
					GUI window = new GUI();
					
					window.frame.setVisible(true);

					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		this.frame = new JFrame("GUI");
		this.frame.setSize(800, 1200);
		this.frame.getContentPane().add(GUIFactory.createSensorPanel("Sensor3"), BorderLayout.NORTH);
		this.frame.getContentPane().add(GUIFactory.createDamPanel("Damd"), BorderLayout.CENTER);
		
	}


}
