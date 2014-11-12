package com.kalebklein.stopwatch;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class StopWatchWindow extends JFrame
{
	private static final long serialVersionUID = -4764744825746547852L;
	
	public static final String VERSION = "1.2.3";
	public static final int VERSION_CODE = 5;

	boolean currentTimeTickerOn = false, stopWatchOn = false, isRestarted = true, isPaused = false;
	Thread currentTimeTicker, stopWatchTicker, timeTrackerTicker;
	StopWatchWindow context = this;
	long startTime = 0L, stopTime = 0L, pausedTime = 0L;
	String elapsedTimeString = null;

	// Window objects
	private JPanel contentPane;
	private JLabel lblCurrentTime, lblCurrtime;
	private JLabel lblElapsedTime;
	private JLabel lblCounter;
	private JButton btnStart, btnPause, btnReset, btnAbout, btnCopyTime;

//	private Font appFont;
	private ImageIcon icon;
	private boolean iconLoaded = false;
	
	private FontLoader font;

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

		initFontLoader();
		setIcon();

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("StopWatch");
		setSize(400, 300);
		setLocationRelativeTo(null);

		if (iconLoaded) setIconImage(icon.getImage());

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
		
		btnCopyTime = new JButton("Copy Time to Clipboard");
		btnCopyTime.setBounds(148, 196, 221, 26);
		contentPane.add(btnCopyTime);

		// Set window's action listeners
		setWindowActionListeners();

		setVisible(true);

		if (font.isFontLoaded())
		{
			lblCounter.setFont(font.applyFont(font.getFont(), Font.BOLD, 50));
			btnAbout.setFont(font.applyFont(font.getFont(), Font.PLAIN, 12));
			btnReset.setFont(font.applyFont(font.getFont(), Font.PLAIN, 12));
			btnStart.setFont(font.applyFont(font.getFont(), Font.PLAIN, 12));
			btnPause.setFont(font.applyFont(font.getFont(), Font.PLAIN, 12));
			lblCounter.setFont(font.applyFont(font.getFont(), Font.BOLD, 50));
			lblElapsedTime.setFont(font.applyFont(font.getFont(), Font.BOLD, 40));
			lblCurrtime.setFont(font.applyFont(font.getFont(), Font.BOLD, 12));
			lblCurrentTime.setFont(font.applyFont(font.getFont(), Font.BOLD, 12));
			btnCopyTime.setFont(font.applyFont(font.getFont(), Font.PLAIN, 12));
			
		}
	}

	private void initFontLoader()
	{
		font = new FontLoader();
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

		// Handle an event when start button is pressed
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
				elapsedTimeString = null;
				
				// Reset all times to 0, so it doesn't attempt to
				// continue tracking time
				startTime = 0L;
				stopTime = 0L;
				pausedTime = 0L;
			}
		});

		// Handle an event when the about button is pressed
		btnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				new AboutWindow(context);
			}
		});
		
		// Handle an event when the copy button is pressed
		btnCopyTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if(elapsedTimeString == null)
				{
					JOptionPane.showMessageDialog(context, "You need to have started the StopWatch to copy it's time. "
							+ "If you reset it, time is at 0 again, and there's nothing to copy", "Copy Time Error",
							JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					StringSelection selection = new StringSelection(elapsedTimeString);
					Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
					clip.setContents(selection, null);
					JOptionPane pane = new JOptionPane("Time has been copied to your clipboard", JOptionPane.INFORMATION_MESSAGE);
					final JDialog dialog = pane.createDialog("Copy to Clipboard");
					Timer timer = new Timer(2500, new ActionListener() {
						public void actionPerformed(ActionEvent e)
						{
							dialog.dispose();
						}
					});
					timer.setRepeats(false);
					timer.start();
					dialog.setVisible(true);
				}
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
					int tick = (int) (System.currentTimeMillis() - startTime) / 1000;
					int days = tick / 86400, hours = (tick / 3600) - (days * 24),
							minutes = (tick / 60) - (days * 1440) - (hours * 60),
							seconds = tick % 60;
					String s = new String(formatter.format(hours) + ":" + formatter.format(minutes) + ":"
							+ formatter.format(seconds));
					lblCounter.setText(s);
					elapsedTimeString = String.format("Elapsed Time: %s days, %s hours, %s minutes, %s seconds",
							formatter.format(days),
							formatter.format(hours),
							formatter.format(minutes),
							formatter.format(seconds));
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
