/***
 * Irc class : simple implementation of a chat using JAVANAISE 
 * Contact: 
 *
 * Authors: 
 */

package irc;

import java.awt.*;
import java.awt.event.*;


import jvn.*;
import java.util.Objects;


public class Irc {
	public TextArea		text;
	public TextField	data;
	Frame 			frame;
	ISentence      sentence;


	/**
	 * main method
	 * create a JVN object nammed IRC for representing the Chat application
	 **/
	public static void main(String argv[]) {
		ISentence sentence = (ISentence) JvnProxy.newInstance(new Sentence(), "IRC");
		new Irc(sentence);

	}

	/**
	 * IRC Constructor
	 **/
	public Irc(ISentence s) {
		sentence = s;
		frame=new Frame();
		frame.setLayout(new GridLayout(1,1));
		text=new TextArea(10,60);
		text.setEditable(false);
		text.setForeground(Color.red);
		frame.add(text);
		data=new TextField(40);
		frame.add(data);
		Button read_button = new Button("read");
		read_button.addActionListener(new readListener(this));
		frame.add(read_button);
		Button write_button = new Button("write");
		write_button.addActionListener(new writeListener(this));
		frame.add(write_button);
		frame.setSize(545,201);
		text.setBackground(Color.black);
		frame.setVisible(true);
		frame.addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				try {
					Objects.requireNonNull(JvnServerImpl.jvnGetServer()).jvnTerminate();
					System.exit(0);
				} catch (JvnException e) {
					throw new RuntimeException(e);
				}

			}
		} );
	}
}


/**
 * Internal class to manage user events (read) on the CHAT application
 **/
class readListener implements ActionListener {
	Irc irc;

	public readListener (Irc i) {
		irc = i;
	}

	/**
	 * Management of user events
	 **/
	public void actionPerformed (ActionEvent e) {
		try {
			String s = irc.sentence.read();
			irc.data.setText(s);
			irc.text.append(s+"\n");
		} catch (Exception je) {
			System.out.println("IRC problem : " + je.getMessage());
		}

	}
}

/**
 * Internal class to manage user events (write) on the CHAT application
 **/
class writeListener implements ActionListener {
	Irc irc;

	public writeListener (Irc i) {
		irc = i;
	}

	/**
	 * Management of user events
	 **/
	public void actionPerformed (ActionEvent e) {
		try {
			String s = irc.data.getText();
			irc.sentence.write(s);
		} catch (Exception je) {
			System.out.println("IRC problem  : " + je.getMessage());
		}
	}
}
