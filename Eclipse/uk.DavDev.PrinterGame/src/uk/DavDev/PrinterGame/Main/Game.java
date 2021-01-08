package uk.DavDev.PrinterGame.Main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.Random;
import java.util.Scanner;

public class Game extends Canvas implements Runnable{
	public static String printer="SII RP-E10";
	public static int x=24;
	public static int direction=0;
	public static int Speed= 4;
	public static int[] snakeImgX = {312, 312, 312};
	public static int WallX=15;
	public static float distance = 0;
	public static int[] roadX = {195, 195, 195};
	public static String[] background = {"Road1", "Road2", "Road3"};
	public static String[] backgrounds = {"Road1", "Road2", "Road3","Road4", "Road5", "Road6"};
	
	int wallTravel=2;
	
	private static final long serialVersionUID = -334193104503512328L;
	
	public static final int WIDTH = 640, HEIGHT = WIDTH / 12 * 9;
	
	public String gameString = "                                               ";
	
	private Thread thread;
	public static boolean Started=false;
	public static boolean running = true;
	public int FPS;
	public Game() {
		

		new Window(WIDTH, HEIGHT, "Receipt Printer Game!", this);
		
	}
	
	public synchronized void start() {
		this.addKeyListener(new KeyInput());
		System.out.println(HEIGHT);
		thread = new Thread(this);
		thread.start();
		if(printer!="None") {
			PrinterService printerService = new PrinterService();
			printerService.printString(printer, "\n\n\n\nSnake Game!\n\n\n\n\n");
		}
		Started=true;
		//running=true;
	}
	public synchronized void stop() {
		PrinterService printerService = new PrinterService();
		if(printer!="None") {
			printerService.printString(printer, gameString + "\n\n\n\nDistance: " + String.valueOf(distance) + "cm\n\n\n\n\n\n\n\n\n\n");
	        byte[] cutP = new byte[] { 0x1d, 'V', 1 };
	        printerService.printBytes(printer, cutP);
		}

        try {
			thread.join();
			running=false;
		}catch(Exception e) {
			e.printStackTrace();
		}
		


        
	}
	Random rnd = new Random();
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = Speed;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime=now;
			while(delta >= 1) {
				tick();
				delta--;
			}
			if(running)
				render();
			frames++;
			
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				FPS=frames;
				//System.out.println("FPS: " + frames);
				frames = 0;
			}
		}
		stop();
	}
    // function that rotates s towards left by d 
	public static String rotate(String s, int offset) {
		  int i = offset % s.length();
		  return s.substring(i) + s.substring(0, i);
	}
	private void tick() {
		PrinterService printerService = new PrinterService();
		if(direction==0)
			x-=1;
		else
			x+=1;
		if(Started) {

			roadX[2]=roadX[1];
			roadX[1]=roadX[0];
			roadX[0]=WallX*13;
			background[2]=background[1];
			background[1]=background[0];
			background[0]=backgrounds[rnd.nextInt(6)];
			snakeImgX[2]=snakeImgX[1];
			snakeImgX[1]=snakeImgX[0];
			snakeImgX[0]=x*13;
			gameString="                                               ";
			gameString = gameString.substring(0,x-1)+'|'+gameString.substring(x+1);
			gameString = gameString.substring(0,x)+'|'+gameString.substring(x+2);
			int d=WallX;
			gameString = gameString.substring(0,WallX-1)+'█'+gameString.substring(WallX+1);
			gameString = gameString.substring(0,WallX+18)+'█'+gameString.substring(WallX+20);
			
			if(WallX!=wallTravel) {
				if(WallX>wallTravel)
					WallX-=1;
				else
					WallX+=1;
			}else {
				wallTravel=rnd.nextInt(18)+1;
			}
			System.out.println(gameString);
			
			if(printer!="None")
				printerService.printString(printer, gameString + "\n");
			
			
			//gameString=rotate(gameString,1);
			distance+=0.5;
			System.out.println(distance);
		}
		
		

	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		requestFocusInWindow();
		if(Started) {
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, WIDTH, HEIGHT);
	        Toolkit t=Toolkit.getDefaultToolkit();  
	        Image i=t.getImage("src/images/Snake background.png");  
			for (int n = 0; n < background.length; n++) {
		        Image roadImage=t.getImage("src/images/"+background[n] + ".png");
		        g.drawImage(roadImage, 0,159*n,this);  
			}
	        
			g.setColor(Color.white);
			for (int n = 0; n < roadX.length; n++) {
		        Image roadImage=t.getImage("src/images/Road.png");  
		        g.drawImage(roadImage, roadX[n],159*n,this);  
			}

	        Image snakeImage=t.getImage("src/images/Snake Head.png");  
	        g.drawImage(snakeImage, snakeImgX[0],0,this);
	        snakeImage=t.getImage("src/images/Snake Body.png");
	        g.drawImage(snakeImage, snakeImgX[1],159,this);
	        g.drawImage(snakeImage, snakeImgX[2],318,this);
			
			g.drawString("FPS: " + FPS, 0, 435);
			

			
			g.dispose();
			bs.show();
		}else {
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, WIDTH, HEIGHT);
			g.dispose();
			bs.show();
		}
		if(x<=WallX) {
			stop();
		}if(x>=WallX+19) {
			stop();
		}
		
	}
	
	public static void main(String args[]) {
		new Game();
        PrinterService printerService = new PrinterService();
        
        System.out.println(printerService.getPrinters());
	}
}
