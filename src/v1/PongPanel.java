package v1;

import java.awt.Color;
import java.awt.Font;
//import java.math.*;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PongPanel extends JPanel implements ActionListener, KeyListener {

	private boolean showTitleScreen = true;
	private boolean playing = false;
	private boolean gameOver = false;
	private boolean isPlayerOneWinner = false;
	private boolean isPlayerTwoWinner = false;
	
	String playerOneWins = "Player 1 Wins!";
	String playerTwoWins = "Player 2 Wins!";
	String promptPlayAgain = "Press p to play again";

	// key pressed
	private boolean upPressed = false;
	private boolean downPressed = false;
	private boolean wPressed = false;
	private boolean sPressed = false;

	// ball size and location
	private int ballX = 400;
	private int ballY = 250;
	private int ballDiameter = 20;
	private double ballDeltaX = 5.0;
	private int ballDeltaY = 3;
	private double maxBallSpeed = 14.0;

	private int fps = 1000 / 60;
	private int paddleSpeed = 5;
	private int scoreToWin = 1;

	// player one
	private int playerOneX = 25;
	private int playerOneY = 250;
	private int playerOneWidth = 10;
	private int playerOneHeight = 76;
	
	// player two
	private int playerTwoX = 760;
	private int playerTwoY = 250;
	private int playerTwoWidth = 10;
	private int playerTwoHeight = 76;

	// score
	private int playerOneScore = 0;
	private int playerTwoScore = 0;

	private static final long serialVersionUID = 1L;

	public PongPanel() {
		setBackground(Color.BLACK);

		// set key listeners
		setFocusable(true);
		addKeyListener(this);

		// set 60 fps
		Timer timer = new Timer(fps, this);
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		step();
	}
	
	public void step() {
		if (playing) {
				if (wPressed)
					playerOneY = playerOneY - paddleSpeed;
				if (sPressed)
					playerOneY = playerOneY + paddleSpeed;
				if (upPressed)
					playerTwoY = playerTwoY - paddleSpeed;
				if (downPressed)
					playerTwoY = playerTwoY + paddleSpeed;

				// where will the ball be after it moves?
				int nextBallLeft = (int) (ballX + ballDeltaX);
				int nextBallRight = (int) (ballX + ballDiameter + ballDeltaX);
				int nextBallTop = ballY + ballDeltaY;
				int nextBallBottom = ballY + ballDiameter + ballDeltaY;

				int bottomBoundary = getHeight() - 15;
				int topBoundary = 15;
				int leftBoundary = 15;
				int rightBoundary = getWidth() - 15;

				// ball bounces off top and bottom of screen
				if (nextBallTop < topBoundary || nextBallBottom > bottomBoundary) {
					playBoundarySound();
					ballDeltaY *= -1;
				}

				// will ball go off the left?
				if (nextBallLeft < leftBoundary) {
					ballDeltaX *= -1;
				}
				// will the ball go off the right side?
				if (nextBallRight > rightBoundary) {
					ballDeltaX *= -1;
				}

				// move the ball
				ballX = (int) (ballX + ballDeltaX);
				ballY = ballY + ballDeltaY;

				// keep paddles on screen
				// top
				if (playerOneY < topBoundary)
					playerOneY = topBoundary;
				if (playerTwoY < topBoundary)
					playerTwoY = topBoundary;
				// bottom
				if (playerOneY + playerOneHeight > bottomBoundary)
					playerOneY = bottomBoundary - playerOneHeight;
				if (playerTwoY + playerTwoHeight > bottomBoundary)
					playerTwoY = bottomBoundary - playerTwoHeight;

				// collision detection paddle and ball
				int playerOneBottom = playerOneY + playerOneHeight;
				int playerOneTop = playerOneY;
				int playerOneRight = playerOneWidth + playerOneX;
				int playerOneCenterPaddle = (playerOneTop + playerOneBottom) / 2;

				int playerTwoBottom = playerTwoY + playerTwoHeight;
				int playerTwoTop = playerTwoY;
				int playerTwoLeft = playerTwoX;
				int playerTwoCenterPaddle = (playerTwoTop + playerTwoBottom) / 2;

				// Player One collision detection
				if (nextBallLeft < playerOneRight) { 
					if (nextBallTop > playerOneBottom || nextBallBottom < playerOneTop) {
						if (nextBallLeft < leftBoundary){
							playerTwoScore++;
							System.out.println("PLAYER 2 SCORED");
							
							if(playerTwoScore == scoreToWin){
								gameOver = true;
								isPlayerTwoWinner = true;
								resetScores();
								centerBallLocation();
								ballDeltaX = 5.0;
								ballDeltaY = 3;
							}
							else{
								ballDeltaX = 5.0;
								ballDeltaY = 3;
								resetBallLocation();
							}
						}
					} else {
						if (ballY == playerOneCenterPaddle)
							ballDeltaY = 0;
						if (ballY < playerOneCenterPaddle)
							ballDeltaY = -5;
						if (ballY > playerOneCenterPaddle)
							ballDeltaY = 5;
						
						playPaddleSound();
						ballDeltaX *= -1.2;// ball hit paddle, increase speed
						if (ballDeltaX > maxBallSpeed)
							ballDeltaX = maxBallSpeed;
						System.out.println("ballY = " + ballY + "playerTwoCenter = " + playerTwoCenterPaddle);
					}
				}

				// Player Two collision detection
				if (nextBallRight > playerTwoLeft) {
					if (nextBallTop > playerTwoBottom || nextBallBottom < playerTwoTop) {
						if (nextBallRight > rightBoundary) {
							playerOneScore++;
							System.out.println("PLAYER 1 SCORED");
							if(playerOneScore == scoreToWin){
								gameOver = true;
								isPlayerOneWinner = true;
								resetScores();
								centerBallLocation();
								ballDeltaX = -5.0;
								ballDeltaY = -3;
							}
							else{
								ballDeltaX = -5.0;
								ballDeltaY = -3;
								resetBallLocation();
							}
						}
					} else {
						if (ballY < playerTwoCenterPaddle)
							ballDeltaY = -5;
						if (ballY > playerTwoCenterPaddle)
							ballDeltaY = 5;
						if (ballY == playerTwoCenterPaddle)
							ballDeltaY = 0;
						
						playPaddleSound();
						ballDeltaX *= -1.2;
						if (ballDeltaX < -(maxBallSpeed))
							ballDeltaX = -(maxBallSpeed);
						System.out.println("ballY = " + ballY + "playerOneCenter = " + playerOneCenterPaddle);
					}

				}
				// tell Jpanel to repaint
				repaint();
			}
		}

	

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (showTitleScreen) {
			System.out.println("showing title scren");
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
			g.drawString("Press 'p' to play", 300, getHeight() / 2);
			showTitleScreen = false;
		}
		if (playing && !gameOver) {
			g.setColor(Color.WHITE);
			g.drawRect(15, 15, getWidth() - 30, getHeight() - 30);
			g.fillOval(ballX, ballY, ballDiameter, ballDiameter);
			g.fillRect(playerOneX, playerOneY, playerOneWidth, playerOneHeight);
			g.fillRect(playerTwoX, playerTwoY, playerTwoWidth, playerTwoHeight);

			// dotted line
			for (int i = 15; i < getHeight() - 15; i += 15)
				g.drawLine(getWidth() / 2, i, getWidth() / 2, i);

			// draw the scores
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
			g.drawString(String.valueOf(playerOneScore), getWidth() / 2 - 115, 75);
			g.drawString(String.valueOf(playerTwoScore), getWidth() / 2 + 115, 75);
			isPlayerOneWinner = false;
			isPlayerTwoWinner = false;
		}
		if(gameOver){
			System.out.println("Showing gameOver screen");
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
			if(isPlayerOneWinner){
				g.drawString(playerOneWins, 300, getHeight() / 2);
				playing = false;
			}
			if(isPlayerTwoWinner){
				g.drawString(playerTwoWins, 300, getHeight() / 2);
				playing = false;
			}
			gameOver = false;
			g.setFont(new Font(Font.DIALOG, Font.PLAIN, 22));
			g.drawString(promptPlayAgain, 300, getHeight() - 100);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_P)
			playing = true;
		// playerOne and playerTwo movement
		if (e.getKeyCode() == KeyEvent.VK_UP)
			upPressed = true;
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			downPressed = true;
		if (e.getKeyCode() == KeyEvent.VK_W)
			wPressed = true;
		if (e.getKeyCode() == KeyEvent.VK_S)
			sPressed = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP)
			upPressed = false;
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			downPressed = false;
		if (e.getKeyCode() == KeyEvent.VK_W)
			wPressed = false;
		if (e.getKeyCode() == KeyEvent.VK_S)
			sPressed = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public void centerBallLocation() {
		ballX = getWidth() / 2;
		ballY = getHeight() / 2;

	}
	
	public void resetBallLocation(){
		ballX = getWidth() / 2;
	}
	
	public void resetScores(){
		playerOneScore = 0;
		playerTwoScore = 0;
	}
	
	public void playPaddleSound() {
	    try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("C:/Users/Joey/Downloads/soundFiles/pong_paddleblip.wav").getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}
	
	public void playBoundarySound(){
		try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("C:/Users/Joey/Downloads/soundFiles/pong_boundblip.wav").getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}
}
