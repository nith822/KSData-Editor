package textPaster;

import java.io.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class ButtonPasteSwing extends JFrame 
implements ActionListener, KeyListener, WindowListener{

	private JButton pasteButton;
	private JTextArea ta;
	private Clipboard clipboard;
	private BufferedWriter out;
	
	private int linesAdded;
	private int charsAdded;
	
	//args[0] destination file to Paste to
	public static void main(String[] args) {
		new ButtonPasteSwing(args[0]).setVisible(true);
	}

	public ButtonPasteSwing(String file) {
		super("Text Paster");
		add(pasteButton = new JButton("Paste to File"), "North");
		add(ta = new JTextArea(), "Center");
		JScrollPane scroll = new JScrollPane(ta);
		add(scroll);
		setSize(250, 250);
		setAlwaysOnTop(true);
		this.addWindowListener(this);
		pasteButton.addActionListener(this);
		pasteButton.addKeyListener(this);
		ta.setEditable(false);
		ta.addKeyListener(this);
		pasteButton.requestFocusInWindow();
		clipboard = getToolkit().getSystemClipboard();
		try {
			out = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void windowClosing(WindowEvent e) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			out.newLine();
			out.newLine();
			out.append("[[  " + dateFormat.format(date) + "  ]]");
			out.newLine();
			out.append("[[  " + "Lines added: " + linesAdded + "  ]]");
			out.newLine();
			out.append("[[  " + "Chars added: " + charsAdded + "  ]]");
			out.newLine();
			out.newLine();
			out.newLine();
			out.newLine();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		super.dispose();
		System.exit(0);
	}
	
	public void actionPerformed(ActionEvent evt) {
		addText();
	}

	public void addText() {
		try {
			String s = (String) clipboard.getData(DataFlavor.stringFlavor);
			ta.setText(ta.getText() + "\n\n" + s);
			out.append(s);
			out.newLine();
			out.newLine();
			linesAdded++;
			charsAdded+= s.length();
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyChar() == '\\') {
				addText();
		}	
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		
	}
}

