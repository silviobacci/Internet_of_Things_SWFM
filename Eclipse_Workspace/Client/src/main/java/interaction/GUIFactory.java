package interaction;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import communication.CoapClientADN;
import configuration.Setup;

public class GUIFactory {
	
	private static final String FLOW	= "Flow:";
	private static final String SET		= "Set:";
	private static final int UP			= 1;
	private static final String WL		= "Water level";
	private static final String WT		= "Water threshold";
	private static final String MIN		= "Min level";
	private static final String MAX		= "Max level";
	
	private JFrame			frame;
	private JTextField 		EvoTextBox;
	private JRadioButton 	incrementRadio,decrementRadio;
	private JButton 		updateBtn;
	private JPanel 			panel_1;
	private JLabel 			lblSensor;
	private JRadioButton 	incrementRadio1;
	private JRadioButton 	decrementRadio1;
	private JTextField 		Evo1TextBox;
	private JButton 		button;
	
	private static CoapClientADN	context	=	CoapClientADN.getInstance();

	private static MouseAdapter upBtnListener(final String sensorName) {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				context.SensorPostJSON(sensorName, null, new Integer(UP), null,  null, null);
				
			}
		};
	}
	
	private static MouseAdapter downBtnListener(final String sensorName) {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				context.SensorPostJSON(sensorName, null, new Integer(-UP), null,  null, null);
				
			}
		};
	}
	
	private static MouseAdapter stableBtnListener(final String sensorName) {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				context.SensorPostJSON(sensorName, null, new Integer(0), null,  null, null);
				
			}
		};
	}
	
	private static ActionListener ComboActionListner(final String sensorName, final JTextField txt) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();

                Object selected = comboBox.getSelectedItem();
                
                if(selected.toString().equals(WL)) 
                	txt.setText(context.getMonitoringModule().get(sensorName).getLevel()+"");
                	
                else if(selected.toString().equals(WT))
                	txt.setText(context.getMonitoringModule().get(sensorName).getThreshold()+"");
                	
                else if(selected.toString().equals(MIN))
                	txt.setText(context.getMonitoringModule().get(sensorName).getMin()+"");
                	
                else if(selected.toString().equals(MAX))
                	txt.setText(context.getMonitoringModule().get(sensorName).getMax()+"");
                	       
				
			}
        };
	}
	
	private static ActionListener textEnterListner(final String sensorName, final JComboBox combo, final JTextField self) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                
                if(combo.getSelectedItem().toString().equals(WL)) 
                	context.SensorPostJSON(sensorName, Integer.parseInt(self.getText()), null, null, null, null);
                	
                else  if(combo.getSelectedItem().toString().equals(WT)) 
                	context.SensorPostJSON(sensorName, null, null,null, null,  Integer.parseInt(self.getText()));
                	
                else  if(combo.getSelectedItem().toString().equals(MIN)) 
                	context.SensorPostJSON(sensorName, null, null, Integer.parseInt(self.getText()), null, null);
                	
                else  if(combo.getSelectedItem().toString().equals(MAX)) 
                	context.SensorPostJSON(sensorName, null, null, null, Integer.parseInt(self.getText()), null);
                	       
				
			}
        };
	}
	
	
	public static JPanel createSensorPanel(String sensorName) {
		JPanel SensorPanel = new JPanel();
		SensorPanel.setLayout(new FlowLayout());
		SensorPanel.setName(sensorName);
		
		SensorPanel.add( new JLabel(sensorName));
		SensorPanel.add( new JLabel(FLOW));
		
		JButton up =new JButton("Up");
		up.addMouseListener( upBtnListener(sensorName) );
		SensorPanel.add(up);
		
		JButton stable =new JButton("Stable");
		up.addMouseListener( stableBtnListener(sensorName) );
		SensorPanel.add(stable);
		
		JButton down =new JButton("Down");
		up.addMouseListener( downBtnListener(sensorName) );
		SensorPanel.add(down);
		
		SensorPanel.add( new JLabel(SET));
	
		JComboBox parameters = new JComboBox();
		parameters.addItem(WL);
		parameters.addItem(WT);
		parameters.addItem(MIN);
		parameters.addItem(MAX);
		
		JTextField param = new JTextField();
		param.addActionListener(textEnterListner(sensorName, parameters, param));
		parameters.addActionListener(ComboActionListner(sensorName, param));
		
		return SensorPanel;
		
	}
}
