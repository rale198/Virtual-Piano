package POOP;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class KeyDelegate extends JFrame implements KeyListener {

	public KeyDelegate() {
		setVisible(true);
		setBounds(250, 250, 250, 250);
		addKeyListener(this);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println(e.getKeyChar());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	
	public static void main(String[] args) {

		KeyDelegate kl=new KeyDelegate();
		kl.addKeyListener(kl);
	}

}
