
public class GameUser {
	
	private static String name;
	private String password;
	private static int wins;
	private static int losses;
	
	public GameUser(String userName, String userPass, int numberWins, int numberLosses)
	{
		name = userName;
		password = userPass;
		wins = numberWins;
		losses = numberLosses;
		TicTacToeServer.userArray[TicTacToeServer.userIndex] = this;
		TicTacToeServer.userMap.put(name, password);
		TicTacToeServer.userIndex++;
	}
	
	public static String getName()
	{
		return name;
	}
	
	public static int getWins()
	{
		return wins;
	}
	
	public static int getLosses()
	{
		return losses;
	}
	
	public static void setWins(int n)
	{
		wins = n;
	}
	
	public static void setLosses(int n)
	{
		losses = n;
	}
	

}
