package POOP;

import javax.swing.JLabel;

public class Timer extends Thread {

	private int sec=0;
	private JLabel lab;
	private boolean radi=false;
	public Timer(JLabel lab) {
		start();
		this.lab=lab;
	}
	@Override
	public void run() {
		
		try {
			while(!interrupted())
			{
				
				synchronized (this) {
					while(!radi)
						wait();
				}
				lab.setText(this.toString());
				sleep(975);
				sec++;
				
			}
		}
		catch(InterruptedException e) {}
	}
	
	public synchronized void kreni()
	{
		radi=true;
		notifyAll();
	}
	
	public void prekini()
	{
		this.interrupt();
	}
	@Override
	public String toString() {
		return ""+((sec/60<10)?"0":"")+sec/60+":"+((sec%60<10)?"0":"")+sec%60;
	}

	public synchronized void resetTime()
	{
		sec=0;
		lab.setText(this.toString());
	}

}
