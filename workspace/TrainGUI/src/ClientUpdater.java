import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ClientUpdater extends Thread
{
	
	static ImageIcon imgIcon = new ImageIcon();
	static ImageIcon[] imageArray = new ImageIcon[160];
	static int threadDelay = 81;
	static boolean stopped = true;
	static ImageIcon currentImage = new ImageIcon("1.jpg"); //sets initial screen to 1.jpg
	static int currentImageNumber = 0; // KEEP ZERO
	static int currentSpeed = -1; // SET DEPENDING ON DIRECTION!
	
	public void run()
	{
		System.out.println("ClientUpdater Thread Running.");

		try {

			//Read in all picture files and store in ImageArray
			for(int i = 0; i < imageArray.length; i++)
			{			
				String fileNum = (i + 1) + ".jpg";
				
				BufferedImage tempImage = ImageIO.read(new File(fileNum));
				imageArray[i] = new ImageIcon(tempImage);
				
				ClientInterface.setServerControl(false);
			}
			
			System.out.println("Images added to imageArray in ClientUpdater");

			
			String command;
			boolean serverControlling;
			
			while(true)
			{
				
				//SerialTest.out.println("" + '\n');
				
				//command = ClientInterface.in.readLine();
				String serialCommand = ClientInterface.serialIn.readLine();		
				serverControlling = ClientInterface.getServerControl();
				
				ClientInterface.out.println("IMAGE:" + currentImageNumber); //maybe move this below the processing so current imageNumber is actually current.
				
				
				
				if(!serialCommand.equals(null))
				{
					ClientInterface.setChatArea(serialCommand);
				}
				
				//ARDUINO BOARD 1 - LEFTMOST
				
				if(serialCommand.equals("101111111"))
				{
					currentImageNumber = 8;
					ClientInterface.setImageLabel(imageArray[8]);
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("110111111"))
				{
					currentImageNumber = 1;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
					System.out.println("WORKING!");
				}
				
				if(serialCommand.startsWith("111011111"))
				{
					currentImageNumber = 3;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("111101111"))
				{
					currentImageNumber = 5;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("111110111"))
				{
					currentImageNumber = 7;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("111111011"))
				{
					currentImageNumber = 9;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("111111101"))
				{
					currentImageNumber = 11;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("111111110"))
				{
					currentImageNumber = 12;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				//ARDUINO BOARD 2 - CENTER
				
				if(serialCommand.equals("20111111"))
				{
					currentImageNumber = 20;
					ClientInterface.setImageLabel(imageArray[8]);
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("21011111"))
				{
					currentImageNumber = 36;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
					System.out.println("WORKING!");
				}
				
				if(serialCommand.startsWith("21101111"))
				{
					currentImageNumber = 54;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("21110111"))
				{
					currentImageNumber = 73;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("21111011"))
				{
					currentImageNumber = 128;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("21111101"))
				{
					currentImageNumber = 160;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("21111110"))
				{
					currentImageNumber = 5;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				
				//ARDUINO BOARD 3 - RIGHTMOST
				
				if(serialCommand.equals("111111"))
				{
					currentImageNumber = 8;
					ClientInterface.setImageLabel(imageArray[8]);
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("011111"))
				{
					currentImageNumber = 1;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
					System.out.println("WORKING!");
				}
				
				if(serialCommand.startsWith("101111"))
				{
					currentImageNumber = 3;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("110111"))
				{
					currentImageNumber = 5;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("111011"))
				{
					currentImageNumber = 7;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("111101"))
				{
					currentImageNumber = 9;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
				if(serialCommand.startsWith("111110"))
				{
					currentImageNumber = 11;
					ClientInterface.out.println("IMAGE:" + currentImageNumber);
				}
				
			

				
				/**
				
				if(!command.equals(""))
	    		{
				ClientInterface.setChatArea(command);
	    		}
	
	
				if(command.startsWith("SERVERCONTROL:ON"))
				{
					ClientInterface.setServerControl(true);
					ClientInterface.setControlLabel("   CONTROLLING: SERVER");
					System.out.println("CLIENT: SERVER CONTROL ON");
				}
				
				if(command.startsWith("SERVERCONTROL:OFF"))
				{
					ClientInterface.setServerControl(false);
					ClientInterface.setControlLabel("   CONTROLLING: CLIENT");
					System.out.println("CLIENT: SERVER CONTROL OFF");
				}
				
				
				if(command.startsWith("MESSAGE:"))
				{
					String s = command.substring(8);
					ClientInterface.setChatArea(s);						
				}
				
				**/
					
							
				//IF SPEED IS POSITIVE
				if(currentSpeed > 0)
				{
					
					if(currentImageNumber > imageArray.length - 1)
					{
						setCurrentImageNumber(0);
					}
						
					if(currentImageNumber < 0)
					{
						setCurrentImageNumber(imageArray.length - 1);
					}
				
						
					ClientInterface.setImageLabel(imageArray[currentImageNumber]);
					setCurrentImage(imageArray[currentImageNumber]);
					setCurrentImageNumber(currentImageNumber + 1);
	
					try 
					{
						Thread.sleep(threadDelay);
					} 
					
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				
		
				
				//IF SPEED IS NEGATIVE
				if(currentSpeed < 0)
				{
					
					if(currentImageNumber > imageArray.length - 1)
					{
						setCurrentImageNumber(0);
					}
						
					if(currentImageNumber < 0)
					{
						setCurrentImageNumber(imageArray.length - 1);
					}
			
					ClientInterface.setImageLabel(imageArray[currentImageNumber]);
					setCurrentImage(imageArray[currentImageNumber]);
					setCurrentImageNumber(currentImageNumber - 1);
					
					try 
					{
						Thread.sleep(threadDelay);
					} 
					
					catch (InterruptedException e) 
					{
					
						e.printStackTrace();
					}
				}
				
				/**
				if(currentSpeed == 0)
				{
					ClientInterface.setImageLabel(currentImage);
				}
				**/
				
			} //END WHILE TRUE
			
		} //END TRY
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	} //END RUN
	
	public static void setThreadDelay(int delayNum)
	{
		threadDelay = delayNum;
	}
	
	public static void setCurrentSpeed(int trainSpeed)
	{
			currentSpeed = trainSpeed;
			System.out.println("Current Speed Method: " + trainSpeed);
	}
	
	public static int getCurrentSpeed()
	{
		return currentSpeed;
	}
	
	public static void setCurrentImageNumber(int imgNum)
	{
		
		currentImageNumber = imgNum;
	}
	
	public static void setStopped(boolean isStopped)
	{
		stopped = isStopped;
	}
	
	public static void setCurrentImage(ImageIcon currentImg)
	{
		currentImage = currentImg;
	}
}