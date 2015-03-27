import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ClientInterface 
{
	//NETWORK and READERS and WRITERS
	private static int PORT = 8901;
	private Socket socket;
	public static BufferedReader in;
	public static PrintWriter out;   
    private ServerSocket serialListener;
	static Socket serialSocket;
	static BufferedReader serialIn;
	static PrintWriter serialOut;
	//private Socket serialSocket;
	private static int serialPort = 8902;
	
	//CONTROLS SERVER OVERRIDE
	static boolean serverHasControl = false;
	
	private static int trainID = 1;
	private JFrame frame = new JFrame("Train Client Interface - Train " + trainID);
	
	//PANELS
	private JPanel trackPanel = new JPanel();
	private JPanel sliderPanel = new JPanel();
	private JPanel statusPanel = new JPanel();
	private static JPanel buttonPanel = new JPanel();
	private JPanel northPanel = new JPanel();
	
	//BUTTONS
	private JButton hornButton = new JButton("HORN");
	private JButton lightButton = new JButton("LIGHTS");
	private JButton quickStopButton = new JButton("QUICK STOP");
	private JButton sendChatButton = new JButton("SEND MESSAGE");
	
	//MENU
	private JMenuBar menu = new JMenuBar();
	private JMenu file = new JMenu("File");
	private JMenu help = new JMenu("Help");
	private JMenuItem quit = new JMenuItem("Quit");
	private JMenuItem about = new JMenuItem("About Program");

	//TEXTAREA
	private static JTextArea chatArea = new JTextArea();
	private static JTextArea chatTextArea = new JTextArea();
	
	//LABELS
	private static JLabel chatLabel = new JLabel("SERVER CHAT:");
	private JLabel trainIDLabel = new JLabel("   Train ID: " + trainID + "   ");
	private static JLabel speedPic = new JLabel();
	private static JLabel directionPic = new JLabel();
	private static JLabel imageLabel = new JLabel();
	private static JLabel speedLabel = new JLabel("       SPEED: ");
	private static JLabel directionLabel = new JLabel("   DIRECTION: ");
	private static JLabel colorScale = new JLabel();
	private static JLabel controlLabel = new JLabel("   CONTROLLING: CLIENT");
	
	//Speed Direction Components
	BufferedImage speedImg = null;
	BufferedImage dirImg = null;
	private static ImageIcon[] speedArray = new ImageIcon[4];
	private static ImageIcon[] directionArray = new ImageIcon[3];
	static final int sliderMin = -3;
	static final int sliderMax = 3;
	static final int sliderInitial = 0;    //initial slider setting (STOP)
	static JSlider speedSlider = new JSlider(JSlider.VERTICAL, sliderMin, sliderMax, sliderInitial);
	
	public ClientInterface(String serverAddress)
	{
		//SETUP SOCKET, READER, and WRITER
		  try 
		  {
			socket = new Socket(serverAddress, PORT);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    out = new PrintWriter(socket.getOutputStream(), true);
		    
		    //SETUP SERIAL CONNECTION
		    serialListener = new ServerSocket(8903);
			serialSocket = serialListener.accept();
			serialIn = new BufferedReader(new InputStreamReader(serialSocket.getInputStream()));
            serialOut = new PrintWriter(serialSocket.getOutputStream(), true);
		  } 
		  
		  catch (Exception e) 
		  {
			System.out.println("EXCEPTION OCCURED");
			e.printStackTrace();
		  } 
	    
	     
		//CONFIGURE FRAME  
		frame.setBackground(Color.blue);
		frame.setSize(1000,800);
		frame.setLayout(new BorderLayout());
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		//Turn on labels at major tick marks.
		speedSlider.setMajorTickSpacing(1);
		speedSlider.setMinorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);

		//Set COMPONENT Layouts and COLORS
		trackPanel.setLayout(new GridBagLayout());
		trackPanel.setBackground(Color.gray);
		sliderPanel.setLayout(new BorderLayout());
		sliderPanel.setBackground(Color.LIGHT_GRAY);
		statusPanel.setLayout(new GridLayout(0,4,0,0));
		statusPanel.setBackground(Color.LIGHT_GRAY);
		northPanel.setBackground(Color.LIGHT_GRAY);
		northPanel.setLayout(new BorderLayout());
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setBackground(Color.LIGHT_GRAY);
		hornButton.setBackground(Color.green);
		lightButton.setBackground(Color.yellow);
		quickStopButton.setBackground(Color.red);
		speedSlider.setBackground(Color.white);
		sendChatButton.setBackground(Color.GRAY);
		sendChatButton.setForeground(Color.white);
		
		//SET EDITABLE
		chatTextArea.setEditable(false);
		
		//CHANGE LISTENERS
		speedSlider.addChangeListener(new SliderListener());
		
		//SET FONTS
		trainIDLabel.setFont(new Font("Serif", Font.BOLD, 24));
		speedLabel.setFont(new Font("Serif", Font.BOLD, 20));
		directionLabel.setFont(new Font("Serif", Font.BOLD, 20));
		chatLabel.setFont(new Font("Serif", Font.BOLD, 18));
		
		//SET ICONS
		hornButton.setIcon(new ImageIcon("horn.jpg"));
		lightButton.setIcon(new ImageIcon("lights.jpg"));
		quickStopButton.setIcon(new ImageIcon("stop.jpg"));
		colorScale.setIcon(new ImageIcon("colorscale.jpg"));
		imageLabel.setIcon(new ImageIcon("1.jpg")); //INITIAL IMAGE
		
		//menu visibility was here
		
		//ADD COMPONENTS
		menu.add(file);
		menu.add(help);
		file.add(quit);
		help.add(about);
		//BUTTON PANEL
		buttonPanel.add(trainIDLabel);
		buttonPanel.add(hornButton);
		buttonPanel.add(lightButton);
		buttonPanel.add(quickStopButton);
		buttonPanel.add(controlLabel);
		//NORTH PANEL
		northPanel.add(menu, BorderLayout.NORTH);
		northPanel.add(buttonPanel, BorderLayout.SOUTH);
		//TRACK PANEL
		trackPanel.add(imageLabel);
		//STATUS PANEL
		statusPanel.add(speedLabel);
		statusPanel.add(directionLabel);
		statusPanel.add(chatLabel);
		statusPanel.add(new JLabel());
		statusPanel.add(speedPic); 
		statusPanel.add(directionPic);
		statusPanel.add(new JScrollPane(chatTextArea));
		statusPanel.add(new JLabel());
		statusPanel.add(new JLabel());
		statusPanel.add(new JLabel());
		statusPanel.add(new JScrollPane(chatArea));
		statusPanel.add(sendChatButton);
		//SLIDER PANEL
		sliderPanel.add(new JLabel("FORWARD"), BorderLayout.NORTH);
		sliderPanel.add(colorScale, BorderLayout.CENTER);
		sliderPanel.add(speedSlider, BorderLayout.EAST);
		sliderPanel.add(new JLabel("REVERSE"), BorderLayout.SOUTH);
		sliderPanel.add(new JLabel("STOP"), BorderLayout.WEST);
		//FRAME
		frame.add(northPanel, BorderLayout.NORTH);
		frame.add(trackPanel, BorderLayout.CENTER);
		frame.add(sliderPanel, BorderLayout.EAST);
		frame.add(statusPanel, BorderLayout.SOUTH);
		
		//SET VISIBLE
		quit.setVisible(true);
		help.setVisible(true);
		file.setVisible(true);
		menu.setVisible(true);
		about.setVisible(true);
		northPanel.setVisible(true);
		buttonPanel.setVisible(true);
		statusPanel.setVisible(true);
		sliderPanel.setVisible(true);
		trackPanel.setVisible(true);
		frame.setVisible(true);
		
		
		//ACTION LISTENERS
		quit.addActionListener(new ActionListener() {
	       	 
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        }); 
		
		about.addActionListener(new ActionListener() {
	       	 
            public void actionPerformed(ActionEvent e)
            {
                JFrame aboutFrame = new JFrame("User / System Documentation");
                JTextArea aboutArea = new JTextArea("HOW TO USE PROGRAM: " +'\n'
                		+ "SERVER CONTROLS HERE");
                aboutArea.setEditable(false);
                aboutFrame.setSize(450, 300);
                aboutFrame.setLocationRelativeTo(null);
                aboutFrame.add(aboutArea);
                aboutFrame.setVisible(true);
            }
        }); 
		
		
		sendChatButton.addActionListener(new ActionListener() {
	       	 
            public void actionPerformed(ActionEvent e)
            {
                out.println("MESSAGE:TRAIN " + trainID + ": " + chatArea.getText());
                chatTextArea.append("Train " + trainID + ": " + chatArea.getText() +'\n');
                chatArea.setText(null);
     
            }
        }); 
		
		hornButton.addActionListener(new ActionListener() {
	       	 
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("HORN PRESSED");
            }
        }); 
		
		lightButton.addActionListener(new ActionListener() {
	       	 
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("LIGHTS PRESSED");
            }
        }); 
		
		quickStopButton.addActionListener(new ActionListener() {
	       	 
            public void actionPerformed(ActionEvent e)
            {
            	
            	if(ClientInterface.getServerControl() == false)
            	{
            		 System.out.println("QUICK STOP BUTTON");
                     speedSlider.setValue(0);
                     ClientUpdater.setCurrentSpeed(0);
                     ClientUpdater.setStopped(true);
                     setSpeedPic(0);
     	             setDirectionPic(0);
            	}
            	
            	else
            	{
            		System.out.println("CANNOT QUICK STOP - SERVER CONTROLLING");
            	}
               
	            
            }
        }); 
		
		
		}
		
	
	class SliderListener implements ChangeListener {
	    public void stateChanged(ChangeEvent e) {
	    	
	        JSlider source = (JSlider)e.getSource();
	        
	        if (!source.getValueIsAdjusting()) 
	        {
	        	/**
	        	if(serverHasControl == false)
	        	{
	        		
		            int speedValue = (int)source.getValue();
		            System.out.println("Client Slider Value Adjusted");

		            
		            if(speedValue == 0)
			        {
		            
				            ClientUpdater.setCurrentSpeed(0);
				            setSpeedPic(0);
				            setDirectionPic(0);
					        out.println("CLIENTSPEED:0");
				        
			        }
		            
		            if(speedValue == 3)
		            {
		            	ClientUpdater.setThreadDelay(300);
		            	ClientUpdater.setCurrentSpeed(3);
		            	setSpeedPic(3);
				        setDirectionPic(1);
				        out.println("CLIENTSPEED:3");
				        
		            }
		            
		            if(speedValue == 2)
		            {
		            	ClientUpdater.setThreadDelay(500);
		            	ClientUpdater.setCurrentSpeed(2);
		            	setSpeedPic(2);
				        setDirectionPic(1);
				        out.println("CLIENTSPEED:2");
				        
		            }
		            
		            if(speedValue == 1)
		            {
		            	ClientUpdater.setThreadDelay(1000);
		            	ClientUpdater.setCurrentSpeed(1);
		            	setSpeedPic(1);
				        setDirectionPic(1);
				        out.println("CLIENTSPEED:1");
				        
		            }
		            
		            if(speedValue == -3)
		            {
		            	ClientUpdater.setThreadDelay(300);
		            	ClientUpdater.setCurrentSpeed(-3);
		            	setSpeedPic(3);
				        setDirectionPic(2);	    
				        out.println("CLIENTSPEED:-3");
				        
		            }
		            
		            if(speedValue == -2)
		            {
		            	ClientUpdater.setThreadDelay(500);
		            	ClientUpdater.setCurrentSpeed(-2);
		            	setSpeedPic(2);
				        setDirectionPic(2);				        
				        out.println("CLIENTSPEED:-2");
				        
		            }
		            
		            if(speedValue == -1)
		            {
		            	ClientUpdater.setThreadDelay(1000);
		            	ClientUpdater.setCurrentSpeed(-1);
		            	setSpeedPic(1);
				        setDirectionPic(2);
				        out.println("CLIENTSPEED:-1");
				        
		            }
	            
	        	}//END IF CLIENT HAS CONTROL
	        	
	        	if (serverHasControl == true)
	        	{
	        		System.out.println("SERVER HAS CONTROL RIGHT NOW.");
	        	}
	        	
	        	**/
	        	
	        	System.out.println("DELETED");
            
	        }    
	    }
	}
	
	
	public static void main(String Args[])
	{
		//READ IN SPEED and DIRECTION IMAGES
		try {
			
			for(int i = 0; i < speedArray.length; i++)
			{
				String speedNum = "speed" + (i) + ".jpg";
				ImageIcon tempSpeed = new ImageIcon(ImageIO.read(new File(speedNum)));
				speedArray[i] = tempSpeed;
				
			}
			
			for(int i = 0; i < directionArray.length; i++)
			{
				String directionNum = "direction" + (i) + ".jpg";
				ImageIcon tempDir = new ImageIcon(ImageIO.read(new File(directionNum)));
				directionArray[i] = tempDir;
				
			}		
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		speedPic.setIcon(speedArray[0]); //SETS STOPPED INITIALLY
		directionPic.setIcon(directionArray[0]); //SETS STOPPED INITIALLY

		String serverAddress = ("localhost");
		ClientInterface client = new ClientInterface(serverAddress);
		
		ClientUpdater t = new ClientUpdater();
		t.start();	

	}
	
	public static void setImageLabel(ImageIcon imageNumber)
	{
		imageLabel.setIcon(imageNumber);
		//out.println("IMAGE:" + ClientUpdater.currentImageNumber);
	}
	
	public static void setSpeedPic(int speedNumber)
	{
		speedPic.setIcon(speedArray[speedNumber]);
	}
	
	public static void setDirectionPic(int dirNumber)
	{
		directionPic.setIcon(directionArray[dirNumber]);
	}
	
	
	public static void setChatArea(String s)
	{
		chatTextArea.append(s + '\n');
		chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
	}
	
	public static void setServerControl(boolean s)
	{
		serverHasControl = s;
	}
	
	public static boolean getServerControl()
	{
		return serverHasControl;
	}
	
	public static void setSliderValue(int value)
	{
		speedSlider.setValue(value);
	}
	
	public static void setControlLabel(String s)
	{
		controlLabel.setText(s);
		buttonPanel.repaint();
	}


}




