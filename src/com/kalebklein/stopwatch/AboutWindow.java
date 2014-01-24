package com.kalebklein.stopwatch;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class AboutWindow extends JFrame
{

	private static final long serialVersionUID = -3652128892688698737L;
	private JPanel contentPane;
	private JButton btnViewSite, btnViewSource;
	private JTextArea txtrAbout;
	private JScrollPane scrollPane;
	private DefaultCaret caret;
	private Font appFont;
	private boolean fontLoaded = false, iconLoaded = false;
	private ImageIcon icon;
	private JButton btnChangelog;

	public AboutWindow()
	{
		initializeWindow();
	}

	private void initializeWindow()
	{
		// To display GUI as it would normally look in the OS
		// Rather than the default Java look
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		setFonts();
		setIcon();

		setResizable(false);
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // If this is left uncommented, then the entire application closes. BAD!
		setTitle("About StopWatch");
		setSize(400, 340);
		setLocationRelativeTo(null);

		if (iconLoaded) setIconImage(icon.getImage());

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		btnViewSite = new JButton("View My Site");
		btnViewSite.setBounds(234, 237, 150, 23);
		contentPane.add(btnViewSite);

		btnViewSource = new JButton("View Source Code");
		btnViewSource.setBounds(10, 235, 150, 26);
		contentPane.add(btnViewSource);

		txtrAbout = new JTextArea();
		scrollPane = new JScrollPane(txtrAbout);
		caret = (DefaultCaret) txtrAbout.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		scrollPane.setBounds(10, 12, 374, 213);
		txtrAbout.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		txtrAbout.setEditable(false);
		txtrAbout.setBounds(10, 12, 374, 213);
		txtrAbout.setLineWrap(true);
		txtrAbout.setWrapStyleWord(true);
		contentPane.add(scrollPane);
		setVisible(true);

		String msg = new String(
				"StopWatch Version: " + StopWatchWindow.VERSION + "\n\n"
						+ "Created by: Kaleb Klein (c) 2014.\n\n"
						+ "This application was build for the Coursera class \"Programming Mobile Applications for Android Handheld Systems.\""
						+ "It seemed to my that maybe the instructor was going to have us do timesheets for a lot of our assignments, so I "
						+ "decided to go ahead and create this. And to keep it in the spirit of this class, I created this application in "
						+ "Java. I hope this helps, and you find it rather cool, especially those who'd like to see Java in action!\n\n"
						+ "If you find an issue with the application, please use the button on the bottom right and contact me about it. If you'd "
						+ "like to browse the source code, use the button on the bottom left. It'll link you to GitHub. Fork the project if you like :)\n\n"
						+ "Good luck in the course, and happy coding!");
		txtrAbout.setText(msg);
		txtrAbout.setCaretPosition(0);
		
		btnChangelog = new JButton("View Changelog");
		btnChangelog.setBounds(10, 272, 374, 26);
		contentPane.add(btnChangelog);

		registerEventListeners();

		if (fontLoaded)
		{
			txtrAbout.setFont(new Font(appFont.getName(), Font.PLAIN, 12));
			btnViewSource.setFont(new Font(appFont.getName(), Font.PLAIN, 12));
			btnViewSite.setFont(new Font(appFont.getName(), Font.PLAIN, 12));
			btnChangelog.setFont(new Font(appFont.getName(), Font.PLAIN, 12));
		}
	}

	private void setFonts()
	{
		try
		{
			FileInputStream is = new FileInputStream("res/fonts/ubuntu.ttf");
			appFont = Font.createFont(Font.TRUETYPE_FONT, is);
			fontLoaded = true;
		}
		catch (Exception e)
		{
			System.out.println("Error loading fonts! Reverting to defaults.");
		}
	}

	private void setIcon()
	{
		try
		{
			icon = new ImageIcon("res/images/icon.png");
			iconLoaded = true;
		}
		catch (Exception e)
		{
			System.out.println("Error loading icon! Reverting to defaults.");
		}
	}

	private void registerEventListeners()
	{
		// Event listener for the source code button
		btnViewSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				openURL("https://github.com/pazuzu156/StopWatch");
			}
		});

		// Event listener for the site button
		btnViewSite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				openURL("http://www.kalebklein.com");
			}
		});
		
		// Event listener for changelog
		btnChangelog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				openURL("http://cdn.kalebklein.com/sw/changelog.txt");
			}
		});
	}

	private void openURL(String URL)
	{
		try
		{
			URI url = new URI(URL);
			if (Desktop.isDesktopSupported())
			{
				Desktop.getDesktop().browse(url);
			}
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
