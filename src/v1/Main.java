package v1;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Main 
{
	public static void main(String[] args) 
	{
		int WIDTH = 800;
		int HEIGHT = 500;
		JFrame frame = new JFrame("Pong");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_O);
		frame.add(menubar);
		menubar.add(menu);
		
		JMenuItem menuItem = new JMenuItem("Sound");
		menuItem.setMnemonic(KeyEvent.VK_S);
		menu.add(menuItem);
		PongPanel pongPanel = new PongPanel();
		frame.add(pongPanel, BorderLayout.CENTER);
		
		frame.setJMenuBar(menubar);
		frame.setSize(WIDTH, HEIGHT);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}
}
