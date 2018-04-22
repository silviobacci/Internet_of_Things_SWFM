package interaction;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import OM2M.MNManager;
import OM2M.MNSubServerResource;
import OM2M.SubscriptionServer;

import java.awt.BorderLayout;
import java.awt.Color;

import communication.LoWPANManager;
import configuration.Setup;
import unipi.iot.Client.Constants;

public class GUI {

	private JFrame	frame;
	private JPanel 	SPanel,DPanel;

	private HashMap<String,JPanel> panels = new HashMap<String,JPanel>();

	private static LoWPANManager	context	=	new LoWPANManager();
	public static Setup s = Setup.getInstance();
	public static SubscriptionServer sServer;
	private static GUI instance;
	
	public GUI getInstance() {
		if(instance == null)
			instance = new GUI();
		return instance;
			
	}
	
	public static void main(String[] args) throws SocketException  {
		sServer =	new SubscriptionServer(Constants.SSERVER_PORT, new MNSubServerResource(Constants.SSRESOURCE_NAME, Constants.SSERVER_PORT));
		sServer.addEndpoints();
		sServer.start();
		s.start();
		Controller ctrl = new Controller();
		
		try {
			s.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Setup.getWinstance();
		MNManager.setWFI(Setup.getWinstance());
		MNManager.createStructure();
		
		context.setName("ADN");
		context.start();
		ctrl.start();
		
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				try {
					GUI window = new GUI();
					LoWPANManager.setGUI(window);
					window.frame.setVisible(true);

					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void addSensorGUI(String name) {
		if(panels.get(name) == null) {
			panels.put(name,GUIFactory.createSensorPanel(name));	
			SPanel.add(panels.get(name) , BorderLayout.NORTH);
			this.frame.pack();
			this.SPanel.repaint();
		}
	}
	
	public void addDamGUI(String name) {
		if(panels.get(name) == null) {
			panels.put(name,GUIFactory.createDamPanel(name));	
			DPanel.add(panels.get(name) , BorderLayout.CENTER);
			this.frame.pack();
			this.DPanel.repaint();
		}
		
	}
	
	public void updateTextBox(String name) {
		if(panels.get(name) != null) {
			panels.get(name).getComponents();
			if(!GUIFactory.getSelect().get((JTextField)panels.get(name).getComponent(7))){
				
		
				@SuppressWarnings("rawtypes")
				ActionListener a = 	((JComboBox)panels.get(name).getComponent(6)).getActionListeners()[0];
				ActionEvent e = new ActionEvent(panels.get(name).getComponent(6),0,"");
				e.setSource(panels.get(name).getComponent(6));
				a.actionPerformed(e);
			}
		}
	}
	
	
	public void updateDam(String name) {
		 if(LoWPANManager.getDamModule().get(name).isOpened())
			 ((JLabel)panels.get(name).getComponent(1)).setText(GUIFactory.OPENED);
		 else
			 ((JLabel)panels.get(name).getComponent(1)).setText(GUIFactory.CLOSED);
	
	}

	public GUI() {
		initialize();
	}

	private void initialize() {	
		this.frame = new JFrame("GUI");
		this.frame.setSize(400, 400);
		this.frame.setResizable(true);
		this.frame.setBackground(Color.orange.brighter());
		
		SPanel = new JPanel();
		SPanel.setLayout(new GridLayout(8, 9));
		SPanel.setVisible(true);
		DPanel = new JPanel();
		DPanel.setLayout(new GridLayout(4,4));
		DPanel.setVisible(true);
		frame.add(SPanel,BorderLayout.NORTH);
		frame.add(DPanel,BorderLayout.SOUTH);
	}

}
