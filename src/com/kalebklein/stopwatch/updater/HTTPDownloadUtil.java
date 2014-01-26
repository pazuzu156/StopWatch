package com.kalebklein.stopwatch.updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPDownloadUtil
{
	private HttpURLConnection connection;
	private InputStream inputStream;
	private int contentLength;
	private String fileName;
	
	public void downloadFile(String fileURL) throws IOException
	{
		URL url = new URL(fileURL);
		connection = (HttpURLConnection) url.openConnection();
		int responseCode = connection.getResponseCode();
		
		if(responseCode == HttpURLConnection.HTTP_OK)
		{
			String disposition = connection.getHeaderField("Content-Disposition");
			contentLength = connection.getContentLength();
			
			if(disposition != null)
			{
				int index = disposition.indexOf("filename=");
				if(index > 0)
				{
					fileName = disposition.substring(index + 10, disposition.length() - 1);
				}
			}
			else
			{
				fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
			}
			
			inputStream = connection.getInputStream();
		}
		else
		{
			throw new IOException("No file to download. Server replied with code: " + responseCode);
		}
	}
	
	public void disconnect() throws IOException
	{
		inputStream.close();
		connection.disconnect();
	}
	
	public String getFileName()
	{
		return this.fileName;
	}
	
	public InputStream getInputStream()
	{
		return this.inputStream;
	}
	
	public int getContentLength()
	{
		return this.contentLength;
	}
}
