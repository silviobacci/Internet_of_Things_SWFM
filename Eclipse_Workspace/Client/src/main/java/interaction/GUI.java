package interaction;

import java.awt.EventQueue;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import communication.CoapClientADN;
import configuration.Setup;
import unipi.iot.Client.Simulator;
import unipi.iot.Client.Starter;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class GUI {

	private JFrame			frame;
	private JTextField 		EvoTextBox;
	private JRadioButton 	incrementRadio,decrementRadio;
	private static CoapClientADN	context	=	CoapClientADN.getInstance();
	public static Setup 	s 		= 	Setup.getInstance();
	private JButton 		updateBtn;
	private JPanel 			panel_1;
	private JLabel 			lblSensor;
	private JRadioButton 	incrementRadio1;
	private JRadioButton 	decrementRadio1;
	private JTextField 		Evo1TextBox;
	private JButton 		button;



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
		
		//for (String sensor :context.getMonitoringModule().keySet())
			this.frame.getContentPane().add(GUIFactory.createSensorPanel("Sensor3"), BorderLayout.NORTH);
			this.frame.getContentPane().add(GUIFactory.createDamPanel("Damd"), BorderLayout.CENTER);
		
		
	}


}
