package POOP;

import java.util.ArrayList;

public class Chord extends Note {

	private ArrayList<Note> lista = new ArrayList<Note>();

	public Chord() throws GDuration {
		super(new Duration(4, 1));
	}

	public Chord dodaj(Note n) {
		lista.add(n);
		return this;
	}

	@Override
	public String toString() {

		StringBuilder s=new StringBuilder();
		for (int i = 0; i < lista.size(); i++)
				s.append(lista.get(i)).append("\n");

		return s.toString();

	}
	
	public int size() {
		return lista.size();
	}
	
	public MusicSymbol get(int idx)
	{
		return lista.get(idx);
	}

	
	@Override
	public String txt() {
		StringBuilder s=new StringBuilder();
		
		for(int i=0;i<lista.size();i++)
			s.append(lista.get(i).txt()+"\n");
		
		return s.toString();
	}
}
