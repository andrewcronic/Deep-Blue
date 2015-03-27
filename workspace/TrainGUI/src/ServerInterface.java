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




public class ServerInterface 
{
	//READER and WRITER
    private static BufferedReader input;
    private static PrintWriter output;
    
    //SERVER HAS CONTROL?
    private static boolean serverHasControl = false;

	private static int trainID = 1;
	private JFrame frame = new JFrame("Train Server Interface");
	
	//PANELS
	private JPanel trackPanel = new JPanel();
	private JPanel sliderPanel = new JPanel();
	private JPanel statusPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	private JPanel trainButtonPanel = new JPanel();
	private JPanel northPanel = new JPanel();

	//BUTTONS
	private JButton train1Button = new JButton("TRAIN 1");
	private JButton train2Button = new JButton("TRAIN 2");
	private JButton train3Button = new JButton("TRAIN 3");
	private JButton hornButton = new JButton("HORN");
	private JButton lightButton = new JButton("LIGHTS");
	private JButton quickStopButton = new JButton("QUICK STOP");
	private JButton chatSendButton = new JButton("SEND MESSAGE");
	private JButton controlTrainOn = new JButton("TRAIN OVERRIDE: ON");
	private JButton controlTrainOff = new JButton("TRAIN OVERRIDE: OFF");
	
	//TEXTAREA
	private static JTextArea chatArea = new JTextArea();
	private static JTextArea chatTextArea = new JTextArea();
	
	//MENU
	private JMenuBar menu = new JMenuBar();
	private JMenu file = new JMenu("File");
	private JMenu help = new JMenu("Help");
	private JMenuItem quit = new JMenuItem("Quit");
	private JMenuItem about = new JMenuItem("About Program");
	
	//LABELS
	private JLabel trainIDLabel = new JLabel("   Train ID: " + trainID + "   ");
	private static JLabel speedPic = new JLabel();
	private static JLabel directionPic = new JLabel();
	private static JLabel imageLabel = new JLabel();
	private static JLabel speedLabel = new JLabel("       SPEED: ");
	private static JLabel directionLabel = new JLabel("   DIRECTION: ");
	private static JLabel chatLabel = new JLabel("CLIENT CHAT");
	private static JLabel colorScale = new JLabel();
	
	//IMAGE STRUCTURES
	BufferedImage speedImg = null;
	BufferedImage dirImg = null;
	private static ImageIcon[] speedArray = new ImageIcon[4];
	private static ImageIcon[] directionArray = new ImageIcon[3];
    private static ImageIcon[] imageArray = new ImageIcon[160];
	static final int sliderMin = -3;
	static final int sliderMax = 3;
	static final int sliderInitial = 0;    //initial slider setting (STOP)
	static JSlider speedSlider = new JSlider(JSlider.VERTICAL, sliderMin, sliderMax, sliderInitial);
	
	public ServerInterface()
	{
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

		//SET COMPONENT LAYOUT and COLOR
		trainButtonPanel.setLayout(new GridLayout(0,3));
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
		
		//SET EDITABLE
		chatTextArea.setEditable(false);
		
		//ADD CHANGE LISTENERS
		speedSlider.addChangeListener(new SliderListener());
		
		//SET FONTS
		trainIDLabel.setFont(new Font("Serif", Font.BOLD, 24));
		speedLabel.setFont(new Font("Serif", Font.BOLD, 20));
		directionLabel.setFont(new Font("Serif", Font.BOLD, 20));
		chatLabel.setFont(new Font("Serif", Font.BOLD, 18));
		
		//SET ICONS
		hornButton.setIcon(new ImageIcon("horn.jpg"));
		quickStopButton.setIcon(new ImageIcon("stop.jpg"));
		lightButton.setIcon(new ImageIcon("lights.jpg"));
		imageLabel.setIcon(new ImageIcon("1.jpg"));
		speedPic.setIcon(new ImageIcon("speed0.jpg"));
		directionPic.setIcon(new ImageIcon("direction0.jpg"));
		colorScale.setIcon(new ImageIcon("colorscale.jpg"));
		
		//MENU
		menu.add(file);
		menu.add(help);
		file.add(quit);
		help.add(about);
		
		
		//ADD COMPONENTS
		trainButtonPanel.add(train1Button);
		trainButtonPanel.add(train2Button);
		trainButtonPanel.add(train3Button);
		buttonPanel.add(trainIDLabel);
		buttonPanel.add(hornButton);
		buttonPanel.add(lightButton);
		buttonPanel.add(quickStopButton);
		buttonPanel.add(controlTrainOn);
		buttonPanel.add(controlTrainOff);
		northPanel.add(menu, BorderLayout.NORTH);
		northPanel.add(trainButtonPanel, BorderLayout.CENTER);
		northPanel.add(buttonPanel, BorderLayout.SOUTH);
		trackPanel.add(imageLabel);
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
		statusPanel.add(chatSendButton);
		sliderPanel.add(new JLabel("FORWARD"), BorderLayout.NORTH);
		sliderPanel.add(colorScale, BorderLayout.CENTER);
		sliderPanel.add(speedSlider, BorderLayout.EAST);
		sliderPanel.add(new JLabel("REVERSE"), BorderLayout.SOUTH);
		sliderPanel.add(new JLabel("STOP"), BorderLayout.WEST);
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
		trainButtonPanel.setVisible(true);
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
                		+ "Use the slider on the right side of the frame to control the speed of the train." + '\n' + '\n'
                		+ "SPEEDS:" + '\n' + "------------------------------" + '\n'
                		+ "3 - Forward, High Speed" + '\n'
                		+ "2 - Forward, Medium Speed" + '\n'
                		+ "1 - Forward, Low Speed" + '\n'
                		+ "0 - STOPPED, Not Moving" + '\n'
                		+ "-1 - Reverse, Low Speed" + '\n'
                		+ "-2 - Reverse, Medium Speed" + '\n'
                		+ "-3 - Reverse, High Speed" + '\n' + '\n'
                		+ "Click the Green Horn Button to sound the trains horn." + '\n'
                		+ "Click the Yellow Lights button to enable the trains lights");
                aboutArea.setEditable(false);
                aboutFrame.setSize(450, 300);
                aboutFrame.setLocationRelativeTo(null);
                aboutFrame.add(aboutArea);
                aboutFrame.setVisible(true);
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
                
	            if(serverHasControl == true)
	            {
	            	System.out.println("SERVER QUICK STOP BUTTON");
	                speedSlider.setValue(0);
	                setSpeedPic(0);
		            setDirectionPic(0);
	            	output.println("SPEED:0");
	            }
	            
	            if(serverHasControl == false)
	            {
	            	System.out.println("SERVER: CLIENT HAS CONTROL RIGHT NOW");
	            }
	            
            }
        }); 
		
		controlTrainOn.addActionListener(new ActionListener() {
	       	 
            public void actionPerformed(ActionEvent e)
            {           
                output.println("SERVERCONTROL:ON");
                serverHasControl = true;
                System.out.println("SERVER CONTROL: ON");
            }
        }); 
		
		controlTrainOff.addActionListener(new ActionListener() {
	       	 
            public void actionPerformed(ActionEvent e)
            {
            	output.println("SERVERCONTROL:OFF");
            	serverHasControl = false;
            	System.out.println("SERVER CONTROL: OFF");
            }
        }); 
        
		
		chatSendButton.addActionListener(new ActionListener() {
	       	 
            public void actionPerformed(ActionEvent e)
            {
              output.println("MESSAGE:SERVER: " + chatArea.getText());
              String s = "SERVER: " + chatArea.getText();
              setChatArea(s);
              chatArea.setText(null);
              
            }
        }); 
		
		train1Button.addActionListener(new ActionListener() {
	       	 
            public void actionPerformed(ActionEvent e)
            {
              trainID = 1;           
              trainIDLabel.setText("   Train ID: " + trainID + "   ");
            }
        }); 
		
		train2Button.addActionListener(new ActionListener() {
	       	 
            public void actionPerformed(ActionEvent e)
            {
              trainID = 2;           
              trainIDLabel.setText("   Train ID: " + trainID + "   ");
            }
        }); 
		
		train3Button.addActionListener(new ActionListener() {
	       	 
            public void actionPerformed(ActionEvent e)
            {
              trainID = 3;           
              trainIDLabel.setText("   Train ID: " + trainID + "   ");
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
	        		System.out.println("SERVER: CANNOT ADJUST - CLIENT HAS CONTROL RIGHT NOW.");
	        		
	        	}
	        	
	        	if(serverHasControl == true)
	        	{
		            int speedValue = (int)source.getValue();
		            System.out.println("SERVER SPEED MANUALLY UPDATED: " + speedValue);
		            
		            if(speedValue == 0)
			        {
			            setSpeedPic(0);
			            setDirectionPic(0);
			            output.println("SPEED:0");
			            
			        }
		            
		            if(speedValue == 3)
		            {
		            	setSpeedPic(3);
				        setDirectionPic(1);
			            output.println("SPEED:3");
		            }
		            
		            if(speedValue == 2)
		            {
		            	setSpeedPic(2);
				        setDirectionPic(1);
			            output.println("SPEED:2");
			            
		            }
		            
		            if(speedValue == 1)
		            {
		            	setSpeedPic(1);
				        setDirectionPic(1);
			            output.println("SPEED:1");
			            
		            }
		            
		            if(speedValue == -3)
		            {
		            	setSpeedPic(3);
				        setDirectionPic(2);
			            output.println("SPEED:-3");
			            
		            }
		            
		            if(speedValue == -2)
		            {
		            	setSpeedPic(2);
				        setDirectionPic(2);
			            output.println("SPEED:-2");
			            
		            }
		            
		            if(speedValue == -1)
		            {
		            	setSpeedPic(1);
				        setDirectionPic(2);
			            output.println("SPEED:-1");
			            
		            }
		            
		    
	            
	        	}
	        	
	        	        **/
	        	
	        	System.out.println("DELETED)");
	        	
	        	
            
	        }    
	    }
	}
	
	
	 static class Train extends Thread {
	        int trainID;
	        Socket socket;


	        /**
	         * Constructs a handler thread for a given socket and mark
	         * initializes the stream fields, displays the first two
	         * welcoming messages.
	         */
	        public Train(Socket socket) {
	            this.socket = socket;
	            this.trainID = 1;
	            
	            try 
	            {        
	                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	                output = new PrintWriter(socket.getOutputStream(), true);
	        			
        			//Read in all picture files and store in ImageArray
        			for(int i = 0; i < imageArray.length; i++)
        			{
        				
        				String fileNum = (i + 1) + ".jpg";
        				
        				BufferedImage tempImage = ImageIO.read(new File(fileNum));
        				imageArray[i] = new ImageIcon(tempImage);
        			}
        			
        			System.out.println("Images added to imageArray in ServerInterface");
        			output.println("SERVERCONTROL:OFF"); //INITIALLY SENDs CLIENT MESSAGE SAYING CLIENT HAS CONTROL
	        	
	            } 
	            
	            catch (IOException e) 
	            {
	                System.out.println("Train connection died: " + e);
	                e.printStackTrace();
	            }
	        }
	        
	        
	        
	        public void run() {
	            	
	        	System.out.println("SERVER RUN METHOD");
	        	
	        	String command;

	                // Repeatedly get commands from the client and process them.
	            while (true) 
	            {     
	            	
	            	//System.out.println("SERVER WHILE TRUE");
	            	//output.println("");
	            	
	                 try 
	                 {
	                	 
                		command = input.readLine();
                		
                		if(!command.equals(""))
                		{
            			setChatArea(command);
                		}
                		
	                	 
	                	//WHILE CLIENT HAS CONTROL
	                	//if(!serverHasControl)
	                	//{
	                		
	                		//System.out.println("SERVER: Client Controlling");
	                		
                		/**
						       if(command.startsWith("MESSAGE:"))
				                {
						    	   System.out.println("MESSAGE:  SENT");
						    	   String s = command.substring(8);
						    	   setChatArea(s);
				                }
				                **/
						       
						       if(command.startsWith("CLIENTSPEED:0"))
				                {
						    	   System.out.println("SERVER - SPEED UPDATED 0");
						    	   speedSlider.setValue(0);
						    	   setSpeedPic(0);
						    	   setDirectionPic(0);
				                }
						       
						       if(command.startsWith("CLIENTSPEED:1"))
				                {
						    	   System.out.println("SERVER - SPEED UPDATED 1");
						    	   speedSlider.setValue(1);
						    	   setSpeedPic(1);
						    	   setDirectionPic(1);
				                }
						       
						       if(command.startsWith("CLIENTSPEED:2"))
				                {
						    	   System.out.println("SERVER - SPEED UPDATED 2");
						    	   speedSlider.setValue(2);
						    	   setSpeedPic(2);
						    	   setDirectionPic(1);
				                }
						       
						       if(command.startsWith("CLIENTSPEED:3"))
				                {
						    	   System.out.println("SERVER - SPEED UPDATED 3");
						    	   speedSlider.setValue(3);
						    	   setSpeedPic(3);
						    	   setDirectionPic(1);
				                }
						       
						       if(command.startsWith("CLIENTSPEED:-1"))
				                {
						    	   System.out.println("SERVER - SPEED UPDATED 1");
						    	   speedSlider.setValue(-1);
						    	   setSpeedPic(1);
							       setDirectionPic(2);
				                }
						       
						       if(command.startsWith("CLIENTSPEED:-2"))
				                {
						    	   System.out.println("SERVER - SPEED UPDATED 2");
						    	   speedSlider.setValue(-2);
						    	   setSpeedPic(2);
						    	   setDirectionPic(2);
				                }
						       
						       if(command.startsWith("CLIENTSPEED:-3"))
				                {
						    	   System.out.println("SERVER - SPEED UPDATED 3");
						    	   speedSlider.setValue(-3);
						    	   setSpeedPic(3);
						    	   setDirectionPic(2);
				                }
						       
						       if(command.startsWith("IMAGE:"))
						       {
						    	   String imageNumber = command.substring(6);
						    	   int imgNum = Integer.valueOf(imageNumber);
						    	   
						    	   if(imgNum > imageArray.length - 1)
						    	   { 
							    	   imgNum = 0;
						    	   }
						    	   
						    	   if(imgNum < 0)
						    	   { 
							    	   imgNum = imageArray.length - 1;
						    	   }
						    	   
						    	   setImageLabel(imageArray[imgNum]);
						    	   output.println("SERVER IMAGE UPDATED: " + imgNum);
						       }

					       
	                	//}//END WHILE CLIENT HAS CONTROL
	                	
	                	
	                	//WHILE SERVER HAS CONTROL
	                	//if(serverHasControl)
	                	//{
		                	
		                	
		                	//System.out.println("SERVER: Server Controlling");
	                		
						       /**
	                		   if(command.startsWith("IMAGE:"))
						       {
						    	   String imageNumber = command.substring(6);
						    	   int imgNum = Integer.valueOf(imageNumber);
						    	   setImageLabel(imageArray[imgNum]);
						    	   output.println("SERVER IMAGE UPDATED: " + imageNumber);
						       }
						       
	                		   
	                		   if(command.startsWith("MESSAGE:"))
				                {
						    	   System.out.println("MESSAGE:  SENT");
						    	   String s = command.substring(8);
						    	   setChatArea(s);
				                }
				                **/
	                		
	                	//} //END WHILE SERVER HAS CONTROL
					       
					 }//END TRY

	                
	                 
	                 catch (Exception e) 
	                 {
						System.out.println("EXCEPTION IN WHILE TRUE OF RUN");
						e.printStackTrace();
	                 } 

	            }//END WHILE TRUE
	        }
	        
	 }
	 
	

	
	public static void main(String Args[])
	{

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
						
				new ServerInterface();
				System.out.println("SERVER AWAITING CONNECTION...");
				ServerSocket listener = new ServerSocket(8901);
				Socket clientSocket = listener.accept();
				Train train1 = new Train(clientSocket);
				train1.start();
		
			
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		speedPic.setIcon(speedArray[0]);
		directionPic.setIcon(directionArray[0]);
	

	}
	
	public static void setImageLabel(ImageIcon imageNumber)
	{
		imageLabel.setIcon(imageNumber);
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
	
	
	 
}
	    
	


