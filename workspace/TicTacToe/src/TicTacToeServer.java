

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;




/**
 * A server for a network multi-player tic tac toe game.  
 * The strings that are sent through the server socket are:
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
 
 */

public class TicTacToeServer 
{
	
	static ServerInterface s = new ServerInterface();
	private static int gameIndex = 0;
	static Game[] gameArray = new Game[15];  //holds up to 15 game instances
	static int userIndex = 0;
	static GameUser[] userArray = new GameUser[100];
	static HashMap<String, String>  userMap = new HashMap<String, String>();
	
	
	public static boolean nameExists(String s)
	{
		
		boolean exists = false;
		
		
		if(userMap.containsKey(s))
		{		
			exists = true;
		}
		
		
		if(!userMap.containsKey(s))
		{		
			exists = false;
		}
		
		return exists;
	}
	
	public static String getPassword(String username)
	{
		String pass = userMap.get(username);
		return pass;
	}
	
	
	
	
	
	

    /**
     * Runs the application. Pairs up clients that connect.
     */
    public static void main(String[] args) throws Exception 
    {
    	
    	
        ServerSocket listener = new ServerSocket(8901);
        System.out.println("Tic Tac Toe Server is Running");
        userMap.put("Andrew", "Cronic");
        userMap.put("Chantelle", "Lafrance");
        
        try 
        {
            while (true) 
            {
                Game game = new Game(gameIndex);
                Game.Player playerX = game.new Player(listener.accept(), 'X');
                Game.Player playerO = game.new Player(listener.accept(), 'O');
                playerX.setOpponent(playerO);
                playerO.setOpponent(playerX);
                game.currentPlayer = playerX;
                gameArray[gameIndex] = game;
                playerX.start();
                playerO.start();
                TicTacToeServer.s.addSessionButton(gameIndex);
                gameIndex++;
                
            }
        } 
        finally {
            listener.close();
        }
    }

}



class Square extends JPanel 
{
    
	private static final long serialVersionUID = 999997878;
	
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
 * A two-player game.
 */
class Game {

    /**
     * A board has nine squares.  Each square is either unowned or
     * it is owned by a player.  So we use a simple array of player
     * references.  If null, the corresponding square is unowned,
     * otherwise the array cell stores a reference to the player that
     * owns it.
     */
	
	private int gameSessionNum = 0;
	TicTacToeClient.Square[] squareBoard;
	
	public Game(int gameNum)
	{
		
		this.gameSessionNum = gameNum;
		this.squareBoard = new TicTacToeClient.Square[9];
		
		 for (int i = 0; i < squareBoard.length; i++)
		 {
	         squareBoard[i] = new TicTacToeClient.Square();  
		 }	
	}
	
	
	
    private Player[] board = {
        null, null, null,
        null, null, null,
        null, null, null};

    /**
     * The current player.
     */
    Player currentPlayer;
    
    public TicTacToeClient.Square[] getSquareBoard()
    {
    	return this.squareBoard;
    }
    
    public int getGameSessionNum()
    {
    	return this.gameSessionNum;
    }
    
    public void setGameSessionNum(int number)
    {
    	this.gameSessionNum = number;
    }

    /**
     * Returns whether the current state of the board is such that one
     * of the players is a winner.
     */
    public boolean hasWinner() {
        return
            (board[0] != null && board[0] == board[1] && board[0] == board[2])
          ||(board[3] != null && board[3] == board[4] && board[3] == board[5])
          ||(board[6] != null && board[6] == board[7] && board[6] == board[8])
          ||(board[0] != null && board[0] == board[3] && board[0] == board[6])
          ||(board[1] != null && board[1] == board[4] && board[1] == board[7])
          ||(board[2] != null && board[2] == board[5] && board[2] == board[8])
          ||(board[0] != null && board[0] == board[4] && board[0] == board[8])
          ||(board[2] != null && board[2] == board[4] && board[2] == board[6]);
    }

    /**
     * Returns whether there are no more empty squares.
     */
    public boolean boardFilledUp() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Called by the player threads when a player tries to make a
     * move.  This method checks to see if the move is legal: that
     * is, the player requesting the move must be the current player
     * and the square in which she is trying to move must not already
     * be occupied.  If the move is legal the game state is updated
     * (the square is set and the next player becomes current) and
     * the other player is notified of the move so it can update its
     * client.
     */
    public synchronized boolean legalMove(int location, Player player) {
        if (player == currentPlayer && board[location] == null) {
            board[location] = currentPlayer;
            currentPlayer = currentPlayer.opponent;
            currentPlayer.otherPlayerMoved(location);
            return true;
        }
        return false;
    }

    /**
     * The class for the helper threads in this multithreaded server
     * application.  A Player is identified by a character mark
     * which is either 'X' or 'O'.  For communication with the
     * client the player has a socket with its input and output
     * streams.  Since only text is being communicated we use a
     * reader and a writer.
     */
    class Player extends Thread {
        char mark;
        Player opponent;
        Socket socket;
        BufferedReader input;
        PrintWriter output;

        /**
         * Constructs a handler thread for a given socket and mark
         * initializes the stream fields, displays the first two
         * welcoming messages.
         */
        public Player(Socket socket, char mark) {
            this.socket = socket;
            this.mark = mark;
            
            try {
            	
                boolean needMoreInformation = true;
                input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

                output = new PrintWriter(socket.getOutputStream(), true);
                output.println("LOGIN");
                
                
                //while we still need login information from client
                while(needMoreInformation)
                {
                	
                    String command = input.readLine();
          
                
                if(command.startsWith("CHECKUSER "))
                {
                	String checkName = command.substring(10);
                	
                	if (TicTacToeServer.nameExists(checkName) == true)
                	{
                		output.println("NAME EXISTS");
                	}
                	
                	else
                	{
                		output.println("NAME NOT FOUND");                		
                	}    	
                	
                }
                
                if(command.startsWith("CHECKPASS ")) 
                {
                  	
                	System.out.println("made it to checkpass on server");
     
   
                	int split = command.indexOf("|");
                	String user = command.substring(10 , (split)); 
                	String pass = command.substring(split + 1);
                	System.out.println("User:" + user + " Pass:" + pass);
                	
                	if (TicTacToeServer.nameExists(user) == true)
                	{
                		
                		if(pass.equals(TicTacToeServer.userMap.get(user)))
                		{
                			output.println("PASSWORD ACCEPTED");
                			needMoreInformation = false;
                		}		
                		
                		if(!(pass.equals(TicTacToeServer.userMap.get(user))))
                		{
                			output.println("PASSWORD REJECTED");
                		}
                		
                	}
                	
                	if (TicTacToeServer.nameExists(user) == false)
                	{
                		
                		System.out.println("CREATING ACCOUNT " + user);
                		GameUser newUser = new GameUser(user, pass, 0, 0);
                	    output.println("ACCOUNT CREATED");
                	    needMoreInformation = false;
                
                	}    
                	
                	
                	
                }
                
              
                
                }
                
                
                if(!needMoreInformation)
                {
                
                output.println("WELCOME " + mark + " SESSION " + gameSessionNum);
                output.println("MESSAGE Waiting for opponent to connect");
                
                }
            } catch (IOException e) {
                System.out.println("Player died: " + e);
                e.printStackTrace();
            }
        }

        /**
         * Accepts notification of who the opponent is.
         */
        public void setOpponent(Player opponent) {
            this.opponent = opponent;            
        }

        /**
         * Handles the otherPlayerMoved message.
         */
        public void otherPlayerMoved(int location) {
            output.println("OPPONENT_MOVED " + location);
            output.println(
                hasWinner() ? "DEFEAT" : boardFilledUp() ? "TIE" : "");
        }

        /**
         * The run method of this thread.
         */
        public void run() {
            try {
                // The thread is only started after everyone connects.
                output.println("MESSAGE All players connected. Game Session Number: " + gameSessionNum);
                ServerInterface.repaintAll();
              
                // Tell the first player that it is their turn.
                if (mark == 'X') {
                    output.println("MESSAGE Your move");
                }
                
             

                // Repeatedly get commands from the client and process them.
                while (true) {
                    String command = input.readLine();
                    ServerInterface.setText("" + command);
                    ServerInterface.repaintAll();
                    
                    
                    if (command.startsWith("MOVE")) {
                        int location = Integer.parseInt(command.substring(5, 6));
                        int gameSession = Integer.parseInt(command.substring(15));  
                 
                        if (legalMove(location, this)) {
                            TicTacToeServer.gameArray[gameSession].squareBoard[location].setIcon(new ImageIcon(mark == 'X' ? "x.gif" : "o.gif"));
                            output.println("VALID_MOVE");
                            output.println(hasWinner() ? "VICTORY"
                                         : boardFilledUp() ? "TIE"
                                         : "");
                        } else {
                            output.println("MESSAGE Not a legal move");
                        }
                    } else if (command.startsWith("QUIT")) {
                    	ServerInterface.sessionPanel.remove(ServerInterface.buttonArray[gameSessionNum]);
                        return;
                    }
                }
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            } finally {
                try {socket.close();} catch (IOException e) {}
            }
        }
    }
}



