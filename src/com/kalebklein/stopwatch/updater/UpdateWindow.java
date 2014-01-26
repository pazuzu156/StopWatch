package com.kalebklein.stopwatch.updater;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import com.kalebklein.stopwatch.StopWatchWindow;

public class UpdateWindow extends JFrame implements PropertyChangeListener
{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JProgressBar progressBar;
	public static JButton btnRestart;
	private JLabel lblInfo;
	UpdateWindow context = this;
	
	private Font appFont;
	private ImageIcon icon;
	private boolean fontLoaded = false, iconLoaded = false;
	
	public UpdateWindow()
	{
		setTitle("Updating...");
		setResizable(false);
		initzializeWindow();
	}

	public void initzializeWindow()
	{
		setFonts();
		setIcon();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(450, 146);
		setLocationRelativeTo(null);
		
		if(iconLoaded) setIconImage(icon.getImage());
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblInfo = new JLabel("Updating StopWatch. Please wait...");
		lblInfo.setBounds(10, 11, 414, 14);
		contentPane.add(lblInfo);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(10, 36, 414, 25);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		btnRestart = new JButton("Restart StopWatch");
		btnRestart.setEnabled(false);
		btnRestart.setBounds(10, 72, 414, 26);
		contentPane.add(btnRestart);
		
		setVisible(true);
		
		if (fontLoaded)
		{
			btnRestart.setFont(new Font(appFont.getName(), Font.PLAIN, 12));
			lblInfo.setFont(new Font(appFont.getName(), Font.PLAIN, 12));
			progressBar.setFont(new Font(appFont.getName(), Font.BOLD, 12));
		}
		
		registerEventListeners();
		beginUpdate();
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
			icon = new ImageIcon("res/images/updateicon.png");
			iconLoaded = true;
		}
		catch (Exception e)
		{
			System.out.println("Error loading icon! Reverting to defaults.");
		}
	}
	
	private void registerEventListeners()
	{
		btnRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restartStopWatch();
			}
		});
	}
	
	private void restartStopWatch()
	{
		try
		{
			ArrayList<String> command = new ArrayList<String>();
			
			if(System.getProperty("os.name").contains("Windows"))
			{
				command.add("StopWatch.exe");
			}
			else
			{
				String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
				File currentJar = new File(StopWatchWindow.class.getProtectionDomain().getCodeSource().getLocation().toURI());
				
				// is it a jar file?
				if(!currentJar.getName().endsWith(".jar"))
					return;
				
				command.add(javaBin);
				command.add("-jar");
				command.add(currentJar.getPath());
			}
			
			ProcessBuilder builder = new ProcessBuilder(command);
			builder.start();
		}
		catch (Exception e1)
		{
			JOptionPane.showMessageDialog(context, "Error opening StopWatch! Everything is good though, just manually reopen it.", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		System.exit(0);
	}
	
	private void beginUpdate()
	{
		String os = System.getProperty("os.name");
		String downloadURL = null;
		
		// Check for Windows or other as Windows has .exe and others have .jar
		if(os.contains("Windows"))
		{
			downloadURL = "http://cdn.kalebklein.com/sw/updates/StopWatch.exe";
		}
		else
		{
			downloadURL = "http://cdn.kalebklein.com/sw/updates/StopWatch.jar";
		}
		
		String saveDir = System.getProperty("user.dir"); // Returns current working directory
		
		try
		{
			progressBar.setValue(0);
			
			DownloadTask task = new DownloadTask(this, downloadURL, saveDir);
			task.addPropertyChangeListener(context);
			task.execute();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(context, "Error executing download task: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if(evt.getPropertyName().equals("progress"))
		{
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
			if(progress == 100)
			{
				lblInfo.setText("Update complete!");
			}
		}
	}
}
