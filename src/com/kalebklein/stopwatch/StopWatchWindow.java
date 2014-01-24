package com.kalebklein.stopwatch;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class StopWatchWindow extends JFrame
{
	private static final long	serialVersionUID	= -4764744825746547852L;

	boolean						currentTimeTickerOn	= false, stopWatchOn = false, isRestarted = true, isPaused = false;
	Thread						currentTimeTicker, stopWatchTicker, timeTrackerTicker;
	StopWatchWindow				context				= this;
	long						startTime			= 0L, stopTime = 0L, pausedTime = 0L;

	// Window objects
	private JPanel				contentPane;
	private JLabel				lblCurrentTime, lblCurrtime;
	private JLabel				lblElapsedTime;
	private JLabel				lblCounter;
	private JButton				btnStart, btnPause, btnReset, btnAbout;
	
	private Font appFont;
	private ImageIcon icon;
	private boolean fontLoaded = false, iconLoaded = false;

	public StopWatchWindow()
	{
		setResizable(false);
		prepareWindow();

		currentTimeTickerOn = true;
		startCurrentTimeTicker();
	}

	private void prepareWindow()
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

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("StopWatch");
		setSize(400, 300);
		setLocationRelativeTo(null);
		
		if(iconLoaded)
			setIconImage(icon.getImage());
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lblCurrentTime = new JLabel("Current Time:");
		lblCurrentTime.setBounds(12, 244, 102, 16);
		contentPane.add(lblCurrentTime);

		lblCurrtime = new JLabel("currTime");
		lblCurrtime.setBounds(126, 244, 106, 16);
		contentPane.add(lblCurrtime);

		lblElapsedTime = new JLabel("Elapsed Time:");
		lblElapsedTime.setFont(new Font("Dialog", Font.BOLD, 40));
		lblElapsedTime.setBounds(62, 12, 332, 50);
		contentPane.add(lblElapsedTime);

		lblCounter = new JLabel("00:00:00");
		lblCounter.setFont(new Font("Dialog", Font.BOLD, 50));
		lblCounter.setBounds(96, 62, 298, 50);
		contentPane.add(lblCounter);

		btnStart = new JButton("Start");
		btnStart.setBounds(25, 162, 98, 26);
		contentPane.add(btnStart);

		btnPause = new JButton("Pause");
		btnPause.setEnabled(false);
		btnPause.setBounds(148, 162, 98, 26);
		contentPane.add(btnPause);
		
		btnReset = new JButton("Reset");
		btnReset.setEnabled(false);
		btnReset.setBounds(271, 162, 98, 26);
		contentPane.add(btnReset);

		btnAbout = new JButton("About");
		btnAbout.setBounds(271, 233, 98, 26);
		contentPane.add(btnAbout);

		// Set window's action listeners
		setWindowActionListeners();

		setVisible(true);
		
		
		if(fontLoaded)
		{
			lblCounter.setFont(new Font(appFont.getName(), Font.BOLD, 50));
			btnAbout.setFont(new Font(appFont.getName(), Font.BOLD, 11));
			btnReset.setFont(new Font(appFont.getName(), Font.BOLD, 11));
			btnStart.setFont(new Font(appFont.getName(), Font.BOLD, 11));
			btnPause.setFont(new Font(appFont.getName(), Font.BOLD, 11));
			lblCounter.setFont(new Font(appFont.getName(), Font.BOLD, 50));
			lblElapsedTime.setFont(new Font(appFont.getName(), Font.BOLD, 40));
			lblCurrtime.setFont(new Font(appFont.getName(), Font.BOLD, 12));
			lblCurrentTime.setFont(new Font(appFont.getName(), Font.BOLD, 12));
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
		catch(Exception e)
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
		catch(Exception e)
		{
			System.out.println("Error loading icon! Reverting to defaults.");
		}
	}

	// A Threaded timer for displaying the current time
	private void startCurrentTimeTicker()
	{
		currentTimeTicker = new Thread("CurrentTimeTickerThread") {
			@Override
			public void run()
			{
				while (currentTimeTickerOn)
				{
					DateFormat format = new SimpleDateFormat("hh:mm:ss a");
					Date now = new Date(System.currentTimeMillis());

					lblCurrtime.setText(format.format(now.getTime()));
				}
			}
		};
		currentTimeTicker.start();
	}

	private void setWindowActionListeners()
	{
		// If user closes window while stopwatch is going
		// Be sure they know it.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				if (stopWatchOn)
				{
					int response = JOptionPane.showConfirmDialog(context, "You still have the stopwatch going. "
							+ "Are you sure you wanna exit?", "WARNING", JOptionPane.YES_NO_OPTION);
					if (response == JOptionPane.YES_OPTION)
					{
						context.dispose();
						System.exit(0);
					}
				}
				else
				{
					context.dispose();
					System.exit(0);
				}
			}
		});

		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				btnStart.setText("Resume");
				btnStart.setEnabled(false);
				btnPause.setEnabled(true);
				btnReset.setEnabled(false);

				if (isPaused)
				{
					startTime = System.currentTimeMillis() + pausedTime;
					pausedTime = 0L;
					stopWatchOn = true;
					begin();
				}
				else
				{
					startTime = System.currentTimeMillis();
					stopWatchOn = true;
					begin();
				}
			}
		});

		// Handle an event when the stop button is pressed
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				btnStart.setEnabled(true);
				btnPause.setEnabled(false);
				btnReset.setEnabled(true);
				trackPausedTime();
			}
		});

		// Handle an event when the reset button is pressed
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				btnReset.setEnabled(false);
				btnStart.setText("Start");
				lblCounter.setText("00:00:00");
			}
		});
		
		// Handle an event when the about button is pressed
		btnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AboutWindow();
			}
		});
	}

	private void begin()
	{
		stopWatchTicker = new Thread("StopWatchTickerThread") {
			@Override
			public void run()
			{
				while (stopWatchOn)
				{
					DecimalFormat formatter = new DecimalFormat("00");
					int seconds = (int) (System.currentTimeMillis() - startTime) / 1000;
					int days = seconds / 86400,
							hours = (seconds / 3600) - (days * 24),
							mins = (seconds / 60) - (days * 1440) - (hours * 60),
							sec = seconds % 60;
					String s = new String(formatter.format(hours)
							+ ":"
							+ formatter.format(mins)
							+ ":"
							+ formatter.format(sec));
					lblCounter.setText(s);
				}
			}
		};
		stopWatchTicker.start();
	}

	private void trackPausedTime()
	{
		timeTrackerTicker = new Thread("PausedTimeTrackerTickerThread") {
			@Override
			public void run()
			{
				long now = System.currentTimeMillis();
				pausedTime -= (now - startTime);
				stopWatchOn = false;
				isPaused = true;
			}
		};
		timeTrackerTicker.start();
	}
}
