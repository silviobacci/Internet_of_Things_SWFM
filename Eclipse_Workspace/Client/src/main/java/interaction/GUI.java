package interaction;

import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import communication.CoapClientADN;
import unipi.iot.Client.Simulator;
import unipi.iot.Client.Starter;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class GUI {

	private JFrame frame;
	private JTextField EvoTextBox;
	private Simulator flowSimulator;
	private JRadioButton incrementRadio,decrementRadio;
	private CoapClientADN context = CoapClientADN.getInstance();
	private JButton updateBtn;
	private JPanel panel_1;
	private JLabel lblSensor;
	private JRadioButton incrementRadio1;
	private JRadioButton decrementRadio1;
	private JTextField Evo1TextBox;
	private JButton button;


	public static void main() {
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
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		
		
		
		try {
			context.getModulesAddresses();
			context.InitializeContext( new Integer(70),new Integer(150));
			context.observeAllSensors();
			Starter.DamController.start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//auto simulation click event handler
		JToggleButton autoSimulationBtn = new JToggleButton("Predefined simulation");
		autoSimulationBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//flowSimulator = new Simulator();
				//flowSimulator.start();
				
			}
		});
		frame.getContentPane().add(autoSimulationBtn, BorderLayout.SOUTH);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		JLabel lblNewLabel = new JLabel("Sensor2:");
		panel.add(lblNewLabel);
		
		//increment radio button mouse click event handler
		decrementRadio = new JRadioButton("decrease");
		decrementRadio.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				incrementRadio.setSelected(false);
			
				
			}
		});
		panel.add(decrementRadio);
		
		//increment radio button mouse click event handler
		incrementRadio = new JRadioButton("Increasing");
		panel.add(incrementRadio);
		incrementRadio.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				decrementRadio.setSelected(false);
	
			}
		});
		
		EvoTextBox = new JTextField();
		panel.add(EvoTextBox);
		EvoTextBox.setColumns(10);
		
		//update button mouse click event handler
		updateBtn = new JButton("Update");
		updateBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int evo = 1;
				
				if(decrementRadio.isSelected())
					evo = -1;
				if(EvoTextBox.getText().length() >0)
					evo *= Integer.parseInt( EvoTextBox.getText() );
					System.out.println("handler");
				context.SensorPostJSON("Sensor2",null,evo,null,null);
				
			}
		});
		panel.add(updateBtn);
		
		panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.CENTER);
		
		lblSensor = new JLabel("Sensor5:");
		panel_1.add(lblSensor);
		
		decrementRadio1 = new JRadioButton("Decrease");
		decrementRadio1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				incrementRadio1.setSelected(false);
			}
		});
		panel_1.add(decrementRadio1);
		
		incrementRadio1 = new JRadioButton("Increase");
		incrementRadio1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				decrementRadio1.setSelected(false);
				
			}
		});
		panel_1.add(incrementRadio1);
		
		Evo1TextBox = new JTextField();
		Evo1TextBox.setColumns(10);
		panel_1.add(Evo1TextBox);
		
		button = new JButton("Update");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int evo = 1;
				
				if(decrementRadio1.isSelected())
					evo=-1;
				
				if(Evo1TextBox.getText().length() >0)
					evo *= Integer.parseInt( Evo1TextBox.getText() );
				
				System.out.println("handler");
				context.SensorPostJSON("Sensor4",null,evo,null,null);
			}
		});
		panel_1.add(button);
		
		
		
	}


}
