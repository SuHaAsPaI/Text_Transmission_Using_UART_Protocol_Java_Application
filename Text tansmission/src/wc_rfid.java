

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.*;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class wc_rfid {
	static String data_to_be_sent;
	
	static String port;
	static int baud;
	static int number_of_data_bits;
	static int number_of_stop_bits;
	static int parity;


	static JFrame frame_1;
	static JFrame frame_2;
	
	static JLabel label_1;
	static JLabel label_2;
	static JLabel label_3;
	static JLabel label_4;
	static JLabel label_5;
	static JLabel label_6;
	static JLabel label_7;
	
	static JTextField textField;
	
	static JComboBox<String> comboBox_1;
	static JComboBox<Integer> comboBox_2;
	static JComboBox<Integer> comboBox_3;
	static JComboBox<String> comboBox_4;
	static JComboBox<String> comboBox_5;
	
	static JButton button_1;
	static JButton button_2;
	static JButton button_3;
	
	static boolean operation_frame_launched=false,serial_port_close=false;
	static SerialPort serialPort;
	
	static LinkedList<Byte> rfid_number=new LinkedList<Byte>();
	static int number_of_cards_in_buffer=-1;
	
	static void initialize_setting_frame()
	{
		frame_1 = new JFrame("Settings");
		frame_1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame_1.setSize(300,300);
		frame_1.setLayout(new GridLayout(6, 1));
		
		label_1 = new JLabel("PORT: ");
		label_2 = new JLabel("BAUD: ");
		label_3 = new JLabel("NUMBER OF DATA BITS: ");
		label_4 = new JLabel("NUMBER OF STOP BITS: ");
		label_5 = new JLabel("PARITY: ");
		
		String[] comboBox_1_description=SerialPortList.getPortNames();
		
		Integer[] comboBox_2_description={	SerialPort.BAUDRATE_110,
											SerialPort.BAUDRATE_300,
											SerialPort.BAUDRATE_600,
											SerialPort.BAUDRATE_1200,
											SerialPort.BAUDRATE_4800,
											SerialPort.BAUDRATE_9600,
											SerialPort.BAUDRATE_14400,
											SerialPort.BAUDRATE_19200,
											SerialPort.BAUDRATE_38400,
											SerialPort.BAUDRATE_57600,
											SerialPort.BAUDRATE_115200,
											SerialPort.BAUDRATE_128000,
											SerialPort.BAUDRATE_256000};
		
		Integer[] comboBox_3_description={SerialPort.DATABITS_5,SerialPort.DATABITS_6,SerialPort.DATABITS_7,SerialPort.DATABITS_8};
		String[] comboBox_4_description={"1","1.5","2"};
		String[] comboBox_5_description={"NONE","EVEN","ODD"};
		
		comboBox_1 = new JComboBox<>(comboBox_1_description);
		comboBox_2 = new JComboBox<>(comboBox_2_description);
		comboBox_3 = new JComboBox<>(comboBox_3_description);
		comboBox_4 = new JComboBox<>(comboBox_4_description);
		comboBox_5 = new JComboBox<>(comboBox_5_description);
		
		button_1=new JButton("Start");
		button_1.addActionListener(new button_1_listener());
		
		frame_1.add(label_1);
		frame_1.add(comboBox_1);

		frame_1.add(label_2);
		frame_1.add(comboBox_2);
		
		frame_1.add(label_3);
		frame_1.add(comboBox_3);
		
		frame_1.add(label_4);
		frame_1.add(comboBox_4);
		
		frame_1.add(label_5);
		frame_1.add(comboBox_5);
		
		frame_1.add(button_1);		
		
		frame_1.setVisible(true);
	}
	
	static void initialize_operation_frame()
	{
		frame_2 = new JFrame("Operation");
		frame_2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame_2.setSize(400, 250);
		frame_2.setLayout(new GridLayout(5, 1));
		
		button_2=new JButton("Close");
		button_2.addActionListener(new button_2_listener());
		button_3=new JButton("Send");
		button_3.addActionListener(new button_3_listener());
		
		textField=new JTextField();
		
		label_6 = new JLabel("Data send to be sent");
		label_7 = new JLabel("Data Received:");
		
		frame_2.add(label_6);
		frame_2.add(textField);
		frame_2.add(label_7);
		
		frame_2.add(button_3);	
		frame_2.add(button_2);		
		
		frame_2.setVisible(true);
		        
        frame_2.addWindowListener(new WindowAdapter() 
		{
            public void windowClosing(WindowEvent e) 
            {
            	serial_port_close=true;
            }
        }
        );
		
		operation_frame_launched=true;
	}
	
	static void initialize_com_port() throws SerialPortException
	{
		serialPort = new SerialPort(port);
		serialPort.openPort();
        serialPort.setParams(baud, number_of_data_bits, number_of_stop_bits, parity);
	}
	
	public static void main(String[] args) {
		
		byte[] buffer_1,buffer_2= new byte[12];
		boolean id_equal=false;
		int count_0;
		
		initialize_setting_frame();
		
		while(operation_frame_launched == false)
		{
			try 
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		
    	try 
    	{
			initialize_com_port();
			serialPort.addEventListener(new SerialPortReader());
			
			
			while(operation_frame_launched==true)
			{
				if(serialPort.getInputBufferBytesCount()!=0)
				{
					buffer_1 = serialPort.readBytes(40);
					String readed=new String(buffer_1);
					System.out.print(readed);
				} 
				
				Thread.sleep(200);
				
				if(serial_port_close==true)
				{
					serialPort.closePort();
					operation_frame_launched=false;
				}
			}
		} 
    	catch (SerialPortException e1) 
    	{
			e1.printStackTrace();
		} 
    	catch (InterruptedException e) 
    	{
			e.printStackTrace();
		}
		
    	System.exit(0);
	}
	
	static class button_1_listener implements ActionListener 
	{
        public void actionPerformed(ActionEvent e) 
        {
        	
        	port=(String)comboBox_1.getSelectedItem();
        	baud=(Integer)comboBox_2.getSelectedItem();
        	number_of_data_bits=(Integer)comboBox_3.getSelectedItem();
        	
        	switch(comboBox_4.getSelectedIndex())
        	{
        		
        	case 0:
        		number_of_stop_bits=SerialPort.STOPBITS_1;
        		break;
        		
        	case 1:
        		number_of_stop_bits=SerialPort.STOPBITS_1_5;
        		break;
        	
        	case 2:
        		number_of_stop_bits=SerialPort.STOPBITS_2;
        		break;
        		        	
        	}
        	
        	switch(comboBox_5.getSelectedIndex())
        	{
        		
        	case 0:
        		parity=SerialPort.PARITY_NONE;
        		break;
        		
        	case 1:
        		parity=SerialPort.PARITY_EVEN;
        		break;
        	
        	case 2:
        		parity=SerialPort.PARITY_ODD;
        		break;
        		
        	}
        	
        	System.out.println("Port Settings: "+port+" "+baud+" "+number_of_data_bits+" "+number_of_stop_bits+" "+parity);
        	
        	frame_1.setVisible(false);
        	frame_1.dispose();
        	initialize_operation_frame();
        	
        }          
	}
	
	static class button_2_listener implements ActionListener 
	{
        public void actionPerformed(ActionEvent e) 
        {
        	serial_port_close=true;
        	frame_2.setVisible(false);
        	frame_2.dispose();   	
        }          
	}
	
	static class button_3_listener implements ActionListener 
	{
        public void actionPerformed(ActionEvent e) 
        {
        	data_to_be_sent=textField.getText();
        	String sd="";
        	if(data_to_be_sent.length()<30){
        		System.out.println("Data gap "+(30-data_to_be_sent.length()));
        		for(int j=1;j<=(30-data_to_be_sent.length());j++){
        			sd=" "+sd;
        		}
        	}
        	System.out.println(data_to_be_sent);
        	try {
        		String df=data_to_be_sent+sd;
        		System.out.println("Data final "+df.length());
				serialPort.writeBytes(df.getBytes());
			} catch (SerialPortException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	textField.setText("");
        }          
	}
	
	static class SerialPortReader implements SerialPortEventListener{

		@Override
		public void serialEvent(SerialPortEvent event1) {
			// TODO Auto-generated method stub
			if(event1.isRXCHAR()){//If data is available
                if(event1.getEventValue() == 10){//Check bytes count in the input buffer
                    //Read data, if 10 bytes available 
                    try {
                        byte buffer[] = serialPort.readBytes(30);
                        final String readed = new String(buffer);
                        label_7.setText("Data Received:"+readed);

                        System.out.println("««Readed from COM " + readed);
                    }
                    catch (SerialPortException ex) {
                        System.out.println(ex);
                    }
                }
            }
			
		}
		
		
	}


}


