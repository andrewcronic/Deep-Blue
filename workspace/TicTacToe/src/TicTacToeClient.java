


import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 *  Client -> Server           Server -> Client
 *  ----------------           ----------------
 *  MOVE <n>  (0 <= n <= 8)    WELCOME <char>  (char in {X, O}) SESSION (SessionNumber)
 *  QUIT                       VALID_MOVE
 *  CHECKUSER (username)       OTHER_PLAYER_MOVED <n>
 *  CHECKPASS (username(pass)  VICTORY
 *                             DEFEAT
 *                             TIE
 *                             MESSAGE <text>
 *                             NAME EXISTS / NAME NOT FOUND
 *                             PASSWORD ACCEPTED/ PASSWORD REJECTED/ ACCOUNT CREATED
 *
 */
public class TicTacToeClient {

    private JFrame frame = new JFrame("Tic Tac Toe");
    private JLabel messageLabel = new JLabel("");
    private ImageIcon icon;
    private ImageIcon opponentIcon;
    private Square[] board = new Square[9];
    private Square currentSquare;
    private static int PORT = 8901;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static int gameSessionNum = 0;
    private String userName;
    private String tempUserName;
    private JTextArea userInfo = new JTextArea("Username - " + userName);
    boolean reenteredName = false;


    /**
     * Constructs the client by connecting to a server, laying out the
     * GUI and registering GUI listeners.
     */
    public TicTacToeClient(String serverAddress) throws Exception {

        // Setup networking
        socket = new Socket(serverAddress, PORT);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);


        // Layout GUI
        messageLabel.setBackground(Color.lightGray);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(messageLabel, "South");
        frame.getContentPane().add(userInfo, "East");  // ADDED
        userInfo.setEditable(false);
        final JPanel boardPanel = new JPanel();
        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Quit Game");
        JMenuItem newGame = new JMenuItem("New Game");
        JMenu view = new JMenu("View");
        JMenu display = new JMenu("Display Settings");
        JMenu colorScheme = new JMenu("Color Scheme");
        JMenuItem color1 = new JMenuItem("Color Scheme 1");
        JMenuItem color2 = new JMenuItem("Color Scheme 2");
        JMenuItem color3 = new JMenuItem("Color Scheme 3");
        colorScheme.add(color1);
        colorScheme.add(color2);
        colorScheme.add(color3);
        view.add(colorScheme);
        view.add(display);
        JMenuItem displaySetting1 = new JMenuItem("Small Size");
        display.add(displaySetting1);
        JMenuItem displaySetting2 = new JMenuItem("Medium Size");
        display.add(displaySetting2);
        JMenuItem displaySetting3 = new JMenuItem("Large Size");
        display.add(displaySetting3);
        menubar.add(file);
	    menubar.add(view);
	    file.add(exit);
	    file.add(newGame);
       
       
       
        boardPanel.setBackground(Color.black);
        boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
        for (int i = 0; i < board.length; i++) {
            final int j = i;
            board[i] = new Square();
            board[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    currentSquare = board[j];
                    out.println("MOVE " + j + " SESSION " + gameSessionNum);}});
            boardPanel.add(board[i]);
        }
        frame.getContentPane().add(boardPanel, "Center");
        frame.getContentPane().add(menubar, "North");
       
        
        exit.addActionListener(new ActionListener() {
        	 
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });      
        
        displaySetting1.addActionListener(new ActionListener() {
       	 
            public void actionPerformed(ActionEvent e)
            {
                frame.setSize(240,240);
            }
        });   
        
        displaySetting2.addActionListener(new ActionListener() {
          	 
            public void actionPerformed(ActionEvent e)
            {
                frame.setSize(500,500);
            }
        });   
        
        displaySetting3.addActionListener(new ActionListener() {
         	 
            public void actionPerformed(ActionEvent e)
            {
                frame.setSize(800,800);
            }
        });   
        
        
        color1.addActionListener(new ActionListener() {
        	 
            public void actionPerformed(ActionEvent e)
            {
                boardPanel.setBackground(Color.BLUE);
            }
        });   
        
        color2.addActionListener(new ActionListener() {
       	 
            public void actionPerformed(ActionEvent e)
            {
                boardPanel.setBackground(Color.GREEN);
            }
        });  
        
        color3.addActionListener(new ActionListener() {
          	 
            public void actionPerformed(ActionEvent e)
            {
                boardPanel.setBackground(Color.RED);
            }
        });   
        
        
        newGame.addActionListener(new ActionListener() {
         	 
            public void actionPerformed(ActionEvent e)
            {
                wantsToPlayAgain();
            }
        });   
        
    }
        	
     
    

    /**
     * The main thread of the client will listen for messages
     * from the server.  The first message will be a "WELCOME"
     * message in which we receive our mark.  Then we go into a
     * loop listening for "VALID_MOVE", "OPPONENT_MOVED", "VICTORY",
     * "DEFEAT", "TIE", "OPPONENT_QUIT or "MESSAGE" messages,
     * and handling each message appropriately.  The "VICTORY",
     * "DEFEAT" and "TIE" ask the user whether or not to play
     * another game.  If the answer is no, the loop is exited and
     * the server is sent a "QUIT" message.  If an OPPONENT_QUIT
     * message is received then the loop will exit and the server
     * will be sent a "QUIT" message also.
     */
    public void play() throws Exception {
        String response;
        boolean readyToPlay = false;
        
        try {
            response = in.readLine();         

            boolean nameNeedsChecked = true;   	
            boolean passNeedsChecked = false;
        	String tempName = JOptionPane.showInputDialog(null, "What is your user name? : ", "Enter or Choose your User Name", 1);
        	out.println("CHECKUSER " + tempName);
        	
        	//MAKE SERVER CHECK IF NAME EXISTS
        	while(nameNeedsChecked)
        	{
        		String input = in.readLine();
        			
        	if (input.startsWith("NAME EXISTS"))
        	{
        		System.out.println("Name Exists");
        		nameNeedsChecked = false;
        		passNeedsChecked = true;
        	}
        	
        	if (input.startsWith("NAME NOT FOUND"))
        	{
        		System.out.println("Name Not Found");
        		nameNeedsChecked = false;
        		passNeedsChecked = true;
        	}
        	
        	}
        	

        	//prompt user to enter password once name is checked
        	String tempPass = JOptionPane.showInputDialog(null, "What is your password? : ", "Enter or Choose your Password", 1);
        	out.println("CHECKPASS " + tempName + "|" + tempPass);  //Delimited with piping symbol
        	System.out.println("CHECKPASS " + tempName + "|" + tempPass);
       
        	
        	
        	//MAKE SERVER CHECK PASSWORD FOR THAT USERNAME
        	while(passNeedsChecked && !nameNeedsChecked)
        	{
        		String input = in.readLine();
        		
        		if(input.startsWith("PASSWORD ACCEPTED"))
        		{

        			this.userName = tempName;
        			System.out.println("Login successful - " + this.userName);
        			readyToPlay = true;
        			passNeedsChecked = false;
        			userInfo.setText("Username - " + tempName + "\n" +
        					"Wins - " + GameUser.getWins() + "\n" +
        					"Losses - " + GameUser.getLosses()				        					
        					);
        		
        		}
        		
        		if(input.startsWith("PASSWORD REJECTED"))
        		{
        			System.out.println("Login failed for - " + tempName);
        
        			String newTempUser = JOptionPane.showInputDialog(null, "Reenter your username? : ", "Enter or Choose your username", 1);
                	String newTempPass = JOptionPane.showInputDialog(null, "What is your password? : ", "Enter or Choose your Password", 1);
                	out.println("CHECKPASS " + newTempUser + "|" + newTempPass);  //Delimited with piping symbol
                	System.out.println("CHECKPASS " + newTempUser + "|" + newTempPass);
                	this.tempUserName = newTempUser;
                	this.reenteredName = true;
        		}
        		
        		
        		if(input.startsWith("ACCOUNT CREATED"))
        		{
        			if(reenteredName == true)
        			{
        				System.out.println("Account Created.  Logged into - " + this.tempUserName);
            			this.userName = this.tempUserName;
            			readyToPlay = true;
            			passNeedsChecked = false;
            			userInfo.setText("Username - " + tempName + "\n" +
            				"Wins - " + GameUser.getWins() + "\n" +
            					"Losses - " + GameUser.getLosses()				        					
            					);
            			
            			
        			}
        			
        			if(reenteredName == false)
        			{
	        			System.out.println("Account Created.  Logged into - " + tempName);
	        			this.userName = tempName;
	        			readyToPlay = true;
	        			passNeedsChecked = false;
	        			
	        			userInfo.setText("Username - " + tempName + "\n" +
	        					"Wins - " + GameUser.getWins() + "\n" +
	        					"Losses - " + GameUser.getLosses()				        					
	        					);
        			}
        			
        		}
        	
        	}
        	
            
            while (readyToPlay) {
            	
            	
                response = in.readLine();
                System.out.println(response);
                
                if (response.startsWith("WELCOME")) 
                {
                    char mark = response.charAt(8);
                    icon = new ImageIcon(mark == 'X' ? "x.gif" : "o.gif");
                    opponentIcon  = new ImageIcon(mark == 'X' ? "o.gif" : "x.gif");
                    frame.setTitle("Tic Tac Toe - Player " + mark);
                    gameSessionNum = Integer.parseInt ("" + response.charAt(18));
                }
                             
                if (response.startsWith("VALID_MOVE")) 
                {
                    messageLabel.setText("Valid move, please wait");
                    currentSquare.setIcon(icon);
                    currentSquare.repaint();
                } else if (response.startsWith("OPPONENT_MOVED"))                 
                {
                    int loc = Integer.parseInt(response.substring(15));
                    board[loc].setIcon(opponentIcon);
                    board[loc].repaint();
                    messageLabel.setText("Opponent moved, your turn");
                } else if (response.startsWith("VICTORY")) 
                {
                    messageLabel.setText("You win");
                    TicTacToeServer.userArray[gameSessionNum].setWins(TicTacToeServer.userArray[gameSessionNum].getWins() + 1);
                    System.out.println("Number of wins for this user - " + GameUser.getWins());
                    
                    break;
                } else if (response.startsWith("DEFEAT")) 
                {
                    messageLabel.setText("You lose");
                    TicTacToeServer.userArray[gameSessionNum].setLosses(TicTacToeServer.userArray[gameSessionNum].getLosses() + 1);
                    break;
                } else if (response.startsWith("TIE")) 
                {
                    messageLabel.setText("You tied");
                    break;
                } else if (response.startsWith("MESSAGE")) 
                {
                    messageLabel.setText(response.substring(8));
                }
       
            }
            out.println("QUIT");
        }
        finally {
            socket.close();
        }
    }

    private boolean wantsToPlayAgain() {
        int response = JOptionPane.showConfirmDialog(frame,
            "Want to start a new game?",
            "Tic Tac Toe - New Game?",
            JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return response == JOptionPane.YES_OPTION;
    }


    /**
     * Graphical square in the client window.  Each square is
     * a white panel containing.  A client calls setIcon() to fill
     * it with an Icon, presumably an X or O.
     */
    static class Square extends JPanel 
    {
 
		private static final long serialVersionUID = 18889999;
		JLabel label = new JLabel((Icon)null);

        public Square() {
            setBackground(Color.white);
            add(label);
        }

        public void setIcon(Icon icon) {
            label.setIcon(icon);
        }
    }

    /**
     * Runs the client as an application.
     */
    public static void main(String[] args) throws Exception {
        
    	
    	while (true) { 

        	String serverAddress = (args.length == 0) ? "localhost" : args[1];
            TicTacToeClient client = new TicTacToeClient(serverAddress);  // deleted userName from constructor!
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setSize(300, 300);
            client.frame.setVisible(true);
            client.frame.setResizable(true);
            client.play();
            if (!client.wantsToPlayAgain()) {
                break;
                }
        	
        	  }
    }
    
}

        
    
