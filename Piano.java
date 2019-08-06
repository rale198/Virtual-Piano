package POOP;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.sound.midi.*;

public class Piano extends JFrame implements KeyListener, Runnable {

	private static final byte[] octave = { 2, 3, 4, 5, 6 };
	private static final String[] height = { "C", "D", "E", "F", "G", "A", "B" };
	private static final String[] sharped = { "C#", "D#", "F#", "G#", "A#" };
	private static final int offset = 35;// offset in JBUTTON array for # elements

	// CANVAS USE fields
	private noteCanvas platno = new noteCanvas();

	// Thread needs fields
	private boolean radi = false;
	private Thread nit = new Thread(this);

	// Recording composition
	private Composition recordC;
	private boolean sr = false;

	// Buttons
	private JButton[] buttons = new JButton[60];
	// idx[t]=button[idx]
	private HashMap<String, Integer> idxHelper = new HashMap<String, Integer>();

	// COMPOSITION AND MIDIMAP
	private Composition composition = new Composition();
	private MidiMap mapa = new MidiMap();

	// MENU FIELDS
	private MidiPlayer player;
	private MenuBar menibar;
	private Menu meni;
	private MenuItem load, autoplay, play, record, end, write, importFiles;
	private boolean loaded2 = false;
	private JTextArea area = new JTextArea();

	// PIANO
	private JLayeredPane keyBoard;
	private boolean startPlaying = false;

	// MACOS USE ONLY
	private static void initSystem() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	// NORTH ELEMENTS;

	private JTextField pathName = new JTextField();
	private Checkbox note, keyb, text, midi;
	private JButton pause, exit, start, stop, continueButton;
	private JLabel timer = new JLabel("Timer", Label.RIGHT);
	private Timer t;

	public Piano() throws HeadlessException, MidiUnavailableException {
		super("Virtual Piano");
		setBounds(0, 0, 1400, 1000);
		initSystem();
		setNorth();
		setMenuBar();
		setSouth();
		nit.start();
		this.addKeyListener(this);

		add(platno, "Center");
		player = new MidiPlayer();
		setVisible(true);
		setResizable(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}

	// AFTER LOADING THE COMPOSITION
	private void enableAll() {
		note.setEnabled(true);
		keyb.setEnabled(true);
	}

	private JPanel myPanel;

	private void setNorth() {
		JPanel myp = new JPanel(new GridLayout(1, 11));

		pathName.setEnabled(true);
		myp.add(pathName);
		CheckboxGroup grupa1 = new CheckboxGroup();
		note = new Checkbox("Note", grupa1, true);
		keyb = new Checkbox("Keyboard", grupa1, false);

		note.setEnabled(false);
		keyb.setEnabled(false);

		myp.add(note);
		myp.add(keyb);

		continueButton = new JButton("Continue");
		pause = new JButton("Pause");
		exit = new JButton("Exit");
		start = new JButton("Start");
		start.addActionListener(l -> {
			t.kreni();
			start.setEnabled(false);
			stop.setEnabled(true);
			sr = true;
			requestFocusInWindow();
			helpList = new RecordList();
		});
		stop = new JButton("Stop");
		stop.addActionListener(l -> {
			t.prekini();
			stop.setEnabled(false);
			// sr = false;
			importFiles.setEnabled(true);
			midi.setEnabled(true);
			text.setEnabled(true);
			recordC = helpList.makeComposition();
		});

		pause.setEnabled(false);
		start.setEnabled(false);
		continueButton.setEnabled(false);
		stop.setEnabled(false);
		exit.setEnabled(false);

		myp.add(continueButton);
		myp.add(pause);
		myp.add(exit);
		myp.add(start);
		myp.add(stop);
		CheckboxGroup group2 = new CheckboxGroup();

		text = new Checkbox(".TXT", group2, true);
		midi = new Checkbox(".MIDI", group2, false);

		text.setEnabled(false);
		midi.setEnabled(false);
		myp.add(text);
		myp.add(midi);
		myp.add(timer);
		myPanel = new JPanel(new GridLayout(2, 1));
		JPanel names = new JPanel(new GridLayout(1, 11));
		names.setBackground(Color.DARK_GRAY);
		JLabel lab = new JLabel("Path Name");
		lab.setForeground(Color.CYAN);
		names.add(lab);
		lab = new JLabel("    ", Label.RIGHT);
		lab.setForeground(Color.CYAN);
		names.add(lab);
		lab = new JLabel("Write");
		lab.setForeground(Color.CYAN);
		names.add(lab);
		lab = new JLabel("    ", Label.RIGHT);
		lab.setForeground(Color.CYAN);
		names.add(lab);
		lab = new JLabel("Autoplay");
		lab.setForeground(Color.CYAN);
		names.add(lab);
		lab = new JLabel("    ", Label.RIGHT);
		lab.setForeground(Color.CYAN);
		names.add(lab);
		lab = new JLabel("Record");
		lab.setForeground(Color.CYAN);
		names.add(lab);
		lab = new JLabel("    ", Label.RIGHT);
		lab.setForeground(Color.CYAN);
		names.add(lab);
		lab = new JLabel("    ", Label.RIGHT);
		lab.setForeground(Color.CYAN);
		names.add(lab);
		lab = new JLabel("Import");
		lab.setForeground(Color.CYAN);
		names.add(lab);
		lab = new JLabel("Timer", Label.RIGHT);
		lab.setForeground(Color.CYAN);
		names.add(lab);
		myPanel.add(names);
		myPanel.add(myp);
		add(myPanel, "North");
	}

	private void makePiano() {
		int x = 55, y = 0;

		keyBoard = new JLayeredPane();
		keyBoard.setPreferredSize(new Dimension(900, 162));
		keyBoard.add(Box.createRigidArea(new Dimension(x, 0)));
		createButtons(x, y);

	}

	private void createButtons(int x, int y) {
		for (int i = 0; i < octave.length; i++) {
			for (int j = 0; j < height.length; j++) {
				int idx = i * height.length + j;
				idxHelper.put(mapa.getReverse((height[j] + octave[i])), idx);
				buttons[idx] = new JButton();
				buttons[idx].setBackground(Color.WHITE);
				buttons[idx].setName(mapa.getReverse(height[j] + octave[i]));
				buttons[idx].setActionCommand(buttons[idx].getName());
				buttons[idx].setText(buttons[idx].getName());
				buttons[idx].setFont(new Font(null, Font.CENTER_BASELINE, 20));
				buttons[idx].setMargin(new Insets(10, 0, -100, 0));
				buttons[idx].addKeyListener(this);
				buttons[idx].setBounds(x, y, 35, 135);
				buttons[idx].addMouseListener(ml);
				keyBoard.add(buttons[idx], new Integer(1));
				keyBoard.add(Box.createRigidArea(new Dimension(2, 0)));

				x += 37;
			}
		}

		x = 0;

		String name = "";
		for (int i = 0; i < octave.length; i++) {

			// Make 5 "keys"

			int br = i * 5 + offset + 0;
			buttons[br] = new JButton();
			buttons[br].setBackground(Color.BLACK);
			name = sharped[0] + octave[i];
			idxHelper.put(mapa.getReverse(name), i * 5 + offset + 0);
			buttons[br].setName(mapa.getReverse(name));
			buttons[br].setText(mapa.getReverse(name));
			buttons[br].setForeground(Color.WHITE);
			buttons[br].setFont(new Font(null, Font.BOLD, 15));
			buttons[br].setMargin(new Insets(0, 0, 0, 0));
			buttons[br].setActionCommand(name);
			buttons[br].addKeyListener(this);
			buttons[br].addMouseListener(ml);

			br++;
			buttons[br] = new JButton();
			buttons[br].setBackground(Color.BLACK);
			name = sharped[1] + octave[i];
			idxHelper.put(mapa.getReverse(name), i * octave.length + offset + 1);
			buttons[br].setText(mapa.getReverse(name));
			buttons[br].setForeground(Color.WHITE);
			buttons[br].setFont(new Font(null, Font.BOLD, 15));
			buttons[br].setMargin(new Insets(0, 0, 0, 0));
			buttons[br].setName(mapa.getReverse(name));
			buttons[br].setActionCommand(name);
			buttons[br].addKeyListener(this);
			buttons[br].addMouseListener(ml);

			br++;
			buttons[br] = new JButton();
			buttons[br].setBackground(Color.BLACK);
			name = sharped[2] + octave[i];
			idxHelper.put(mapa.getReverse(name), i * octave.length + offset + 2);
			buttons[br].setText(mapa.getReverse(name));
			buttons[br].setForeground(Color.WHITE);
			buttons[br].setFont(new Font(null, Font.BOLD, 15));
			buttons[br].setMargin(new Insets(0, 0, 0, 0));
			buttons[br].setName(mapa.getReverse(name));
			buttons[br].setActionCommand(name);
			buttons[br].addKeyListener(this);
			buttons[br].addMouseListener(ml);

			br++;
			buttons[br] = new JButton();
			buttons[br].setBackground(Color.BLACK);
			name = sharped[3] + octave[i];
			idxHelper.put(mapa.getReverse(name), i * octave.length + offset + 3);
			buttons[br].setText(mapa.getReverse(name));
			buttons[br].setForeground(Color.WHITE);
			buttons[br].setFont(new Font(null, Font.BOLD, 15));
			buttons[br].setMargin(new Insets(0, 0, 0, 0));
			buttons[br].setName(mapa.getReverse(name));
			buttons[br].setActionCommand(name);
			buttons[br].addKeyListener(this);
			buttons[br].addMouseListener(ml);

			br++;
			buttons[br] = new JButton();
			name = sharped[4] + octave[i];
			idxHelper.put(mapa.getReverse(name), i * octave.length + offset + 4);
			buttons[br].setText(mapa.getReverse(name));
			buttons[br].setForeground(Color.WHITE);
			buttons[br].setFont(new Font(null, Font.BOLD, 15));
			buttons[br].setMargin(new Insets(0, 0, 0, 0));
			buttons[br].setBackground(Color.BLACK);
			buttons[br].setName(mapa.getReverse(name));
			buttons[br].setActionCommand(name);
			buttons[br].addKeyListener(this);
			buttons[br].addMouseListener(ml);

			// Place the 5 keys
			buttons[i * 5 + offset + 0].setBounds(77 + (260 * i), y, 25, 95);
			keyBoard.add(buttons[i * 5 + offset + 0], new Integer(2));

			buttons[i * 5 + offset + 1].setBounds(115 + (260 * i), y, 25, 95);
			keyBoard.add(buttons[i * 5 + offset + 1], new Integer(2));

			buttons[i * 5 + offset + 2].setBounds(188 + (260 * i), y, 25, 95);
			keyBoard.add(buttons[i * 5 + offset + 2], new Integer(2));

			buttons[i * 5 + offset + 3].setBounds(226 + (260 * i), y, 25, 95);
			keyBoard.add(buttons[i * 5 + offset + 3], new Integer(2));

			buttons[i * 5 + offset + 4].setBounds(264 + (260 * i), y, 25, 95);
			keyBoard.add(buttons[i * 5 + offset + 4], new Integer(2));
		}
	}

	private void setSouth() {
		makePiano();
		add(keyBoard, "South");
	}

	private boolean srnew = false;

	private void setMenuBar() {

		menibar = new MenuBar();
		setMenuBar(menibar);
		meni = new Menu("Options");
		menibar.add(meni);

		load = new MenuItem("Load", new MenuShortcut('L'));
		write = new MenuItem("Write", new MenuShortcut('W'));
		autoplay = new MenuItem("Autoplay", new MenuShortcut('S'));
		play = new MenuItem("Play", new MenuShortcut('P'));
		record = new MenuItem("Record", new MenuShortcut('R'));
		end = new MenuItem("End", new MenuShortcut('E'));
		importFiles = new MenuItem("Export", new MenuShortcut('I'));

		write.setEnabled(false);
		autoplay.setEnabled(false);
		play.setEnabled(false);
		importFiles.setEnabled(false);

		load.addActionListener(e -> {
			composition = new Composition();
			composition.loadComposition("/Users/rale/Desktop/fajlovi/" + pathName.getText() + ".txt", mapa, this);
			enableAll();
			write.setEnabled(true);
			loaded2 = true;
		});
		end.addActionListener(e -> {
			if (srnew == true) {
				showRecordOptions();
			} else {
				prekini();
				dispose();
			}
		});

		write.addActionListener(e -> {
			platno.setComposition(this.composition);
			boolean state = false;
			if (note.getState())
				state = true;
			platno.setLetters(state);
			platno.repaint();
			autoplay.setEnabled(true);
			play.setEnabled(true);

		});

		autoplay.addActionListener(l -> {
			exit.setEnabled(true);
			pause.setEnabled(true);

			pathName.setEnabled(false);
			write.setEnabled(false);
			autoplay.setEnabled(false);
			play.setEnabled(false);
			record.setEnabled(false);

			platno.setComposition(composition);
			if (nit == null || nit.isAlive() == false) {
				nit = new Thread(this);
				nit.start();
			}
			this.kreni();
		});
		pause.addActionListener(l -> {
			continueButton.setEnabled(true);
			pause.setEnabled(false);
			this.zaustavi();
		});
		exit.addActionListener(l -> {
			this.prekini();
			pause.setEnabled(false);
			continueButton.setEnabled(false);
			exit.setEnabled(false);
			nit = null;
		});

		continueButton.addActionListener(l -> {
			this.kreni();
			continueButton.setEnabled(false);
			pause.setEnabled(true);
		});

		play.addActionListener(l -> {
			if (loaded2)
				startPlaying = true;
			requestFocusInWindow();
		});

		record.addActionListener(l -> {
			start.setEnabled(true);
			t = new Timer(timer);
			recordC = new Composition();
			midi.setEnabled(true);
			text.setEnabled(true);
			requestFocusInWindow();
		});

		importFiles.addActionListener(l -> {

			if (sr == true) {
				srnew = true;
				t.resetTime();
				if (midi.getState() == false)
					new FileWr().writeFile(recordC, this.pathName.getText());
				else
					new MidiFormatter().writeFile(recordC, this.pathName.getText());
			}

		});
		meni.add(load);
		meni.add(write);
		meni.add(play);
		meni.add(autoplay);
		meni.add(record);
		meni.add(importFiles);
		meni.add(end);
	}

	public static void main(String[] args) {
		try {
			Piano p = new Piano();
		} catch (HeadlessException | MidiUnavailableException e) {
			e.printStackTrace();
		}

	}

	private void showRecordOptions() {

		myPanel.removeAll();
		myPanel = null;
		myPanel = new JPanel();
		myPanel.setLayout(new GridLayout(2, 3));
		JLabel[] first = new JLabel[] { new JLabel(), new JLabel("Do you want to import recorded file?"),
				new JLabel() };
		JButton yes1 = new JButton("Yes, .mid extension"), yes2 = new JButton("Yes, .txt extension"),
				no1 = new JButton("No");
		for (int i = 0; i < 3; i++)
			myPanel.add(first[i]);
		myPanel.add(yes1);
		myPanel.add(yes2);
		myPanel.add(no1);

		yes1.requestFocus();
		yes2.requestFocus();
		no1.requestFocus();
		yes1.addActionListener(l -> {
			new FileWr().writeFile(recordC, "saved.txt");
			prekini();
			dispose();
		});
		yes2.addActionListener(l -> {
			new MidiFormatter().writeFile(recordC, "midi.mid");
			prekini();
			dispose();
		});
		no1.addActionListener(l -> {
			prekini();
			dispose();
		});

		requestFocusInWindow();
		add(myPanel, "North");
		this.validate();
		this.repaint();

	}

	public synchronized void writeError(String msg) {
		platno.writeError(msg);
	}

	// not in Use
	@Override
	public void keyTyped(KeyEvent e) {
	}

	// for virtual assistant
	private int stringIdx = 0;
	private String[] strChecks = new String[10];

	private boolean checked(Chord ch) {

		int ret = 0;
		for (int i = 0; i < ch.size(); i++)
			for (int j = 0; j < ch.size(); j++)
				if (strChecks[i].equals(mapa.getReverse(ch.get(j).toString())) == true)
					ret++;
		return (ret == stringIdx);
	}

	private long timeClicked = -1;
	private RecordList helpList;

	@Override
	public synchronized void keyPressed(KeyEvent e) {
		String key = e.getKeyChar() + "";
		if (e.getKeyChar() == 65535)
			return; // shift or caps_lock char value 0xFFFFFFF
		if (startPlaying == true) {
			MusicSymbol mus = composition.get(platno.getIdx());
			if (mus instanceof Chord) {
				strChecks[stringIdx++] = key;
				Chord ch = (Chord) mus;
				if (stringIdx == ch.size()) {

					if (checked(ch)) {
						platno.incIdxAutoPlay();
						platno.repaint();
					}
					stringIdx = 0;
				}
			} else if (mus instanceof Note) {
				Note nota = (Note) mus;
				if ((mapa.getReverse(nota.toString()).equals(key))) {
					platno.incIdxAutoPlay();
					platno.repaint();

				} else {
					platno.setIdx(0);
					platno.repaint();
				}

			}
		}

		if (sr == true) {
			timeClicked=System.currentTimeMillis();
		}
		Object[] keys = mapa.get(key);
		int nota = 0;
		if (keys != null) {
			nota = Integer.parseInt(keys[1].toString());

			player.play(nota);
			buttons[idxHelper.get(key)].setBackground(Color.RED);
		}
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
		String key = e.getKeyChar() + "";

		int idx = idxHelper.get(key) == null ? -1 : idxHelper.get(key);

		if (idx > -1) {
			Color clr = Color.BLACK;
			if (idx < 35)
				clr = Color.WHITE;

			buttons[idx].setBackground(clr);

			if (sr == true) {
				long finaltime = System.currentTimeMillis() - timeClicked;
				Duration d = null;
				try {
					d = new Duration(((finaltime > 1) ? 4 : 8), 1);
				} catch (GDuration e1) {
				}
				Object[] obj = mapa.get(key);

				byte midival = Byte.parseByte(obj[1].toString());
				String str = obj[0].toString();
				byte octv = Byte.parseByte(str.charAt(str.length() == 2 ? 1 : 2) + "");

				Note note = new Note(d, octv, str.charAt(0), (str.length() == 3), midival);
				helpList.put(note, timeClicked);
			}
		}
	}

	public synchronized void kreni() {
		radi = true;
		notifyAll();
	}

	public synchronized void zaustavi() {
		radi = false;
	}

	public void prekini() {
		if (nit != null)
			nit.interrupt();
	}

	@Override
	public void run() {
		try {
			int idx = 0;

			while (!Thread.interrupted()) {
				synchronized (this) {
					while (!radi)
						wait();
				}
				if (idx >= composition.size())
					break;
				MusicSymbol mus = composition.get(idx);
				idx++;

				if (mus instanceof Chord) {
					Chord ch = (Chord) mus;
					for (int i = 0; i < ch.size(); i++) {
						Note str = (Note) ch.get(i);
						int nota = str.getMidi();
						player.play(nota);
						buttons[idxHelper.get(mapa.getReverse(str.toString()))].setBackground(Color.RED);
					}

				} else if (mus instanceof Note) {
					player.play(((Note) mus).getMidi());
					buttons[idxHelper.get(mapa.getReverse(mus.toString()))].setBackground(Color.RED);
				}
				Thread.sleep((long) (1. / mus.getDur().getBr() * 400));
				platno.incIdx();
				platno.repaint();
				if (mus instanceof Chord) {
					Chord ch = (Chord) mus;

					for (int i = 0; i < ch.size(); i++) {
						Note str = (Note) ch.get(i);
						Color clr = (idxHelper.get(mapa.getReverse(str.toString())) > 34) ? Color.BLACK : Color.WHITE;
						buttons[idxHelper.get(mapa.getReverse(str.toString()))].setBackground(clr);

					}
				} else if (mus instanceof Note) {
					Color clr = (idxHelper.get(mapa.getReverse(mus.toString())) > 34) ? Color.BLACK : Color.WHITE;
					buttons[idxHelper.get(mapa.getReverse(mus.toString()))].setBackground(clr);
				}
			}
		} catch (InterruptedException e) {
		}

		platno.setIdx(0);
		platno.repaint();
		exit.setEnabled(false);
		pause.setEnabled(false);
		pathName.setEnabled(true);
		write.setEnabled(true);
		autoplay.setEnabled(true);
		play.setEnabled(true);
		record.setEnabled(true);
	}

	private int locidx;
	private MouseListener ml = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			Object[] obj = mapa.get(btn.getName());
			player.play(Integer.parseInt(obj[1].toString()));
			locidx = idxHelper.get(btn.getName());
			buttons[locidx].setBackground(new Color(238, 130, 238));

		};

		public void mouseReleased(MouseEvent e) {

			Color clr = Color.BLACK;
			if (locidx < 35)
				clr = Color.WHITE;
			buttons[locidx].setBackground(clr);

		};
	};

}
