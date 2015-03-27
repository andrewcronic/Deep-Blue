import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



public class ServerInterface {
	
	static JPanel sessionPanel = new JPanel();
	JLabel inputLabel = new JLabel("null");
	static JTextArea commandText = new JTextArea("Client / Server Communication:" + "\n");
	static JScrollPane scroll = new JScrollPane(commandText);
	JMenuBar menubar = new JMenuBar();
	JPanel serverPanel = new JPanel();
	JMenu file = new JMenu("File");
	JMenuItem quit = new JMenuItem("Quit");
	JMenuItem viewStats = new JMenuItem("View Leaderboards");
	static Square[] board = new Square[9];
	static JButton[] buttonArray = new JButton[15];
	
	public ServerInterface()
	{
		JFrame frame = new JFrame("Server Side Interface");
    	frame.setSize(400,400);
    	frame.setResizable(true);
    	frame.setLocationRelativeTo(null);
    	
    	serverPanel.setLayout(new BorderLayout());
    	sessionPanel.setLayout(new BoxLayout(sessionPanel, BoxLayout.Y_AXIS));
    	commandText.setBackground(Color.GRAY);
    	commandText.setEditable(false);
    
 
    	file.add(quit);
    	file.add(viewStats);
    	menubar.add(file);
    	commandText.setVisible(true);
    	JScrollPane scroll = new JScrollPane(commandText);
    	scroll.setPreferredSize(new Dimension(400,100));
    	serverPanel.add(scroll, BorderLayout.SOUTH);
    	serverPanel.add(menubar, BorderLayout.NORTH);
    	serverPanel.add(sessionPanel, BorderLayout.CENTER);
    	sessionPanel.setVisible(true);

    	frame.add(serverPanel);
    	frame.setVisible(true);
    	
        quit.addActionListener(new ActionListener() {
       	 
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        }); 
        
        
        viewStats.addActionListener(new ActionListener() {
          	 
            public void actionPerformed(ActionEvent e)
            {
                JFrame statFrame = new JFrame("LeaderBoards");
                statFrame.setSize(400,400);
                
                JPanel statPanel = new JPanel();
                JTextArea statArea = new JTextArea("STATS!" + "\n");
                JScrollPane scroll = new JScrollPane(statArea);
                
                for (int i = 0; i < TicTacToeServer.userIndex; i++)
                {
                	
                	/**
                	if(!(TicTacToeServer.userArray[i].getName() == null))
                	{
                		statArea.append("Name: " + TicTacToeServer.userArray[i].getName() + "   Wins: " + TicTacToeServer.userArray[i].getWins() + "   Losses: " + TicTacToeServer.userArray[i].getLosses() + "\n");
                	}
                	**/
                	
                	if(!(TicTacToeServer.userArray[i].getName() == null))
                	{
                		statArea.append("Name: " + TicTacToeServer.userArray[i].getName() + "   Wins: " + TicTacToeServer.userArray[i].getWins() + "   Losses: " + TicTacToeServer.userArray[i].getLosses() + "\n");
                	}
                }
                
                
                statArea.setEditable(false);
                statPanel.add(scroll);
                statFrame.add(statPanel);
                
                statPanel.setVisible(true);
                statFrame.setVisible(true);
            }
        }); 
	}
	
	public void addSessionButton(int sessionID)
	{
		final int id = sessionID;
		JButton tempButton = new JButton("Game Session " + (id + 1));
		buttonArray[sessionID] = tempButton;
		tempButton.setVisible(true);
		sessionPanel.add(tempButton);
		sessionPanel.repaint();
		sessionPanel.setVisible(true);

		
		
		   tempButton.addActionListener(new ActionListener() {
		       	 
	            public void actionPerformed(ActionEvent e)
	            {
	            	
	                JFrame gameFrame = new JFrame("Game Session Window");
	                gameFrame.setSize(320,320);
	                JPanel boardPanel = new JPanel();                
	                
	                boardPanel.setBackground(Color.black);
	                boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
	                for (int i = 0; i < TicTacToeServer.gameArray[id].getSquareBoard().length; i++) 
	                {
	                    boardPanel.add(TicTacToeServer.gameArray[id].getSquareBoard()[i]);
	                }
	                
	                gameFrame.getContentPane().add(boardPanel, "Center");
	                gameFrame.setVisible(true);                
	                
	            }
	        }); 
		
	}
	
	public static void setText(String s)
	{
		String newline = "\n";
		commandText.append(s + newline);
		commandText.setCaretPosition(commandText.getDocument().getLength());
		
	}
	
	public static JTextArea getTextArea()
	{
	     return commandText;
	}
	
	public static void repaintAll()
	{
		sessionPanel.repaint();
		commandText.repaint();
		sessionPanel.setVisible(true);
		commandText.setVisible(true);
		
	}

	
	static class Square extends JPanel 
	{
        
		private static final long serialVersionUID = 777778989;
		JLabel label = new JLabel((Icon)null);

        public Square() 
        {
            setBackground(Color.white);
            add(label);
        }

        public void setIcon(Icon icon) {
            label.setIcon(icon);
        }
    }

}
