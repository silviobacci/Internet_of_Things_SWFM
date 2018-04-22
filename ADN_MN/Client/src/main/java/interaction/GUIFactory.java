package interaction;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import OM2M.ModulesManager;
import communication.LoWPANManager;
import unipi.iot.Client.Constants;

public class GUIFactory {
	private static final String SENSOR_NAME	= "Sensor: ";
	private static final String DAM_NAME	= "Dam: ";
	private static final String FLOW	= "Change the flow:";
	private static final String SET		= "Change the levels: ";
	public static final String 	OPENED	= "OPENED";
	public 	static final String CLOSED	= "CLOSED";
	private static final int 	UP		= 1;
	private static final String WL		= "Water Level";
	private static final String WT		= "Water Threshold";
	private static final String MIN		= "Min Level";
	private static final String MAX		= "Max Level";
	private static HashMap<JTextField,Boolean> select = new HashMap<JTextField,Boolean>();

	
	//Dam buttons listener
	private static MouseAdapter upBtnListener(final String sensorName) {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ModulesManager.SensorPostJSON(sensorName, null, new Integer(UP), null,  null, null);
				
			}
		};
	}
	
	private static MouseAdapter downBtnListener(final String sensorName) {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ModulesManager.SensorPostJSON(sensorName, null, new Integer(-UP), null,  null, null);
				
			}
		};
	}
	
	private static MouseAdapter stableBtnListener(final String sensorName) {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ModulesManager.SensorPostJSON(sensorName, null, new Integer(0), null,  null, null);
				
			}
		};
	}
	
	private static MouseAdapter damBtnListener(final String damName, final JLabel lab) {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!LoWPANManager.getDamModule().get(damName).isOpened()) {
					ModulesManager.DamPostJSON(damName, Constants.OPEN);
					lab.setText(OPENED);
				}else {
					ModulesManager.DamPostJSON(damName, Constants.CLOSED);
					lab.setText(CLOSED);
				}
			}
		};
	}
	
	//Sensor comboBox and textfield listeners
	private static ActionListener ComboActionListner(final String sensorName, final JTextField txt) {
		return new ActionListener() {
			@SuppressWarnings("rawtypes")
			public void actionPerformed(ActionEvent e) {
				if(LoWPANManager.getMonitoringModule().get(sensorName) != null) {
	                JComboBox comboBox = (JComboBox) e.getSource();
	                
	                Object selected = comboBox.getSelectedItem();
	                
	                if(selected.toString().equals(WL)) 
	                	txt.setText(LoWPANManager.getMonitoringModule().get(sensorName).getLevel()+"");
	                	
	                else if(selected.toString().equals(WT))
	                	txt.setText(LoWPANManager.getMonitoringModule().get(sensorName).getThreshold()+"");
	                	
	                else if(selected.toString().equals(MIN))
	                	txt.setText(LoWPANManager.getMonitoringModule().get(sensorName).getMin()+"");
	                	
	                else if(selected.toString().equals(MAX))
	                	txt.setText(LoWPANManager.getMonitoringModule().get(sensorName).getMax()+"");
                	       
				}
			}
        };
	}
	
	private static FocusListener  fListener(final JTextField self) {
		return new FocusListener() {

			public void focusGained(FocusEvent e) {
				select.put(self, Boolean.TRUE);
				
			}

			public void focusLost(FocusEvent e) {
				select.put(self, Boolean.FALSE);
				
			}
		};
		

 
}
	
	private static ActionListener textEnterListner(final String sensorName, @SuppressWarnings("rawtypes") final JComboBox combo, final JTextField self) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                
                if(combo.getSelectedItem().toString().equals(WL)) 
                	ModulesManager.SensorPostJSON(sensorName, Integer.parseInt(self.getText()), null, null, null, null);
                	
                else  if(combo.getSelectedItem().toString().equals(WT)) 
                	ModulesManager.SensorPostJSON(sensorName, null, null,null, null,  Integer.parseInt(self.getText()));
                	
                else  if(combo.getSelectedItem().toString().equals(MIN)) 
                	ModulesManager.SensorPostJSON(sensorName, null, null, Integer.parseInt(self.getText()), null, null);
                	
                else  if(combo.getSelectedItem().toString().equals(MAX)) 
                	ModulesManager.SensorPostJSON(sensorName, null, null, null, Integer.parseInt(self.getText()), null);
                	       
                self.setBackground(new Color(0,255,0));
                try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                self.setBackground(UIManager.getColor("TextField.background"));
			}
        };
	}
	
	//Sensor panel
	public static JPanel createSensorPanel(String sensorName) {
		JPanel SensorPanel = new JPanel();
		SensorPanel.setLayout(new FlowLayout());
		SensorPanel.setName(sensorName);
		SensorPanel.add( new JLabel(SENSOR_NAME + sensorName + " - "));
		SensorPanel.add( new JLabel(FLOW));
		
		JButton up =new JButton("UP");
		up.addMouseListener( upBtnListener(sensorName) );
		SensorPanel.add(up);
		
		JButton stable = new JButton("STABLE");
		stable.addMouseListener( stableBtnListener(sensorName) );
		SensorPanel.add(stable);
		
		JButton down =new JButton("DOWN");
		down.addMouseListener( downBtnListener(sensorName) );
		SensorPanel.add(down);
		SensorPanel.add( new JLabel(SET));
	
		JComboBox<String> parameters = new JComboBox<String>();
		parameters.addItem(WL);
		parameters.addItem(WT);
		parameters.addItem(MIN);
		parameters.addItem(MAX);
		parameters.setVisible(true);
		
		JTextField param = new JTextField();
		select.put(param, Boolean.FALSE);
		param.setPreferredSize(new Dimension(100,25));
		param.addActionListener(textEnterListner(sensorName, parameters, param));
		parameters.addActionListener(ComboActionListner(sensorName, param));
		param.addFocusListener(fListener(param));
		SensorPanel.add(parameters);
		
		SensorPanel.add(param);
		
		return SensorPanel;
		
	}
	
	//Dam panel
	public static JPanel createDamPanel(String damName) {
		JPanel damPanel = new JPanel();
		damPanel.setLayout(new FlowLayout());
		damPanel.setName(damName);	
		damPanel.add( new JLabel(DAM_NAME + damName + " - Current state: "));
		
		JLabel state = new JLabel(CLOSED);
		damPanel.add(state);
		
		JButton up =new JButton("OPEN/CLOSE");
		up.addMouseListener( damBtnListener(damName,state) );
		damPanel.add(up);
			
		return damPanel;	
	}

	public static HashMap<JTextField, Boolean> getSelect() {
		return select;
	}

	public static void setSelect(HashMap<JTextField, Boolean> select) {
		GUIFactory.select = select;
	}
}
