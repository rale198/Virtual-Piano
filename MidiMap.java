package POOP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MidiMap {

	private HashMap<String, Object[]> mapa = new HashMap<>();
	private HashMap<String, String> reverse=new HashMap<String, String>();

	private void loadFile() {
		try {
			

			BufferedReader br = new BufferedReader(new FileReader(new File("/Users/rale/Desktop/map.csv")));

			Stream<String> s = br.lines();

			s.forEach(str -> {
				Pattern p = Pattern.compile("(.),(.*),(.*[^\\n])");

				Matcher m = p.matcher(str);
				

				if (m.matches()) {
					String key = m.group(1);
					String val1 = m.group(2), val2 = m.group(3);

					reverse.put(val1, key);
					mapa.put(key, new Object[] { val1, val2 });
				}

			});

		} catch (FileNotFoundException err) {
			System.out.println(err);
		}
		;
	}

	public MidiMap() {
		this.loadFile();
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		mapa.forEach((str, a) -> {
			ret.append(str.toString() + " " + a[0].toString() + " " + a[1].toString() + "\n");
		});
		
		return ret.toString();

	}

	public void put(String str, Object[] obj)
	{
		mapa.put(str, obj);
	}
	
	public Object[] get(String str)
	{
		return mapa.get(str);
	}
	
	public String getReverse(String key)
	{
		return reverse.get(key);
	}
}
