package POOP;

public class Duration {

	private int im,br;
	
	public Duration(int im, int br) throws GDuration {
		super();
		if(im!=8&&im!=4) throw new GDuration("Wrong Duration!!!");
		
		this.im=im;
		this.br=br;
	}

	public int getIm() {
		return im;
	}

	public int getBr() {
		return br;
	}
	
	

};
