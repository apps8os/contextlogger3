/**
 * Copyright (c) 2012 Aalto University and the authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining 
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 *  
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 *  
 * Authors:
 * Chaudhary Nalin (nalin.chaudhary@aalto.fi)
 */
package org.sizzlelab.contextlogger.android.io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class ExportDatabaseFileTask extends AsyncTask<String, Void, Boolean> {
	public final static String EXPORT = "export";
	public final static String UPLOAD = "upload";
	
    protected void onPreExecute() {
    }

    protected Boolean doInBackground(final String... args) {
    	if (args[1].equals(EXPORT)){
    		return exportFile();
    	} else if (args[1].equals(UPLOAD)) {
    		return uploadFile(args[0]);
    	}
    	return false;
    }

    private boolean exportFile(){
    	File dbFile =
            new File(Environment.getDataDirectory() + "/data/org.sizzlelab.contextlogger.android/databases/CL_database.db");

	   File exportDir = new File(Environment.getExternalStorageDirectory(), "");
	   if (!exportDir.exists()) {
	      exportDir.mkdirs();
	   }
	   File outputFile = new File(exportDir, dbFile.getName());
	
	   try {
	      outputFile.createNewFile();
	      this.copyFile(dbFile, outputFile);
	      return true;
	   } catch (IOException e) {
	      Log.e("app", e.getMessage(), e);
	      return false;
	   }
    }
    
    private boolean uploadFile(String urlToUse){
    	String Boundary = "--7d021a37605f0";
    	URL url;
    	File dbFile =
            new File(Environment.getDataDirectory() + "/data/org.sizzlelab.contextlogger.android/databases/CL_database.db");
    	try {
			url = new URL(urlToUse);
			HttpURLConnection theUrlConnection = (HttpURLConnection) url.openConnection();
	        theUrlConnection.setDoOutput(true);
	        theUrlConnection.setDoInput(true);
	        theUrlConnection.setUseCaches(false);
	        theUrlConnection.setChunkedStreamingMode(1024);
	        theUrlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary="
	                + Boundary);

	        DataOutputStream httpOut = new DataOutputStream(theUrlConnection.getOutputStream());
	        
	        String str = "--" + Boundary + "\r\n"
            + "Content-Disposition: form-data;name=\"logdata\"; filename=\"" + dbFile.getName() + "\"\r\n"
            + "Content-Type: text/plain\r\n"
            + "\r\n";

	        httpOut.write(str.getBytes());

	        FileInputStream uploadFileReader = new FileInputStream(dbFile);
	        int numBytesToRead = 1024;
	        int availableBytesToRead;
	        while ((availableBytesToRead = uploadFileReader.available()) > 0)
	        {
	        	byte[] bufferBytesRead;
	        	bufferBytesRead = availableBytesToRead >= numBytesToRead ? new byte[numBytesToRead]
	        	                                                                    : new byte[availableBytesToRead];
	        	uploadFileReader.read(bufferBytesRead);
	        	httpOut.write(bufferBytesRead);
	        	httpOut.flush();
	        }
	        httpOut.write(("--" + Boundary + "--\r\n").getBytes());
	        httpOut.write(("--" + Boundary + "--\r\n").getBytes());
	        httpOut.flush();
	        httpOut.close();

	        InputStream is = theUrlConnection.getInputStream();
	        StringBuilder response = new StringBuilder();
	        byte[] respBuffer = new byte[4096];
	        while (is.read(respBuffer) >= 0)
	        {
	            response.append(new String(respBuffer).trim());
	        }
	        is.close();
	        android.util.Log.d("app", response.toString());
	        return true;
		} catch (MalformedURLException e){
			android.util.Log.d("app", "malformed url");
		}
    	catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
        
    }
    
    protected void onPostExecute(final Boolean success) {
    	if (success) {
          android.util.Log.d("app", "Export successful!");
       } else {
    	   android.util.Log.d("app", "Export failed!");
       }
    }

    void copyFile(File src, File dst) throws IOException {
       FileChannel inChannel = new FileInputStream(src).getChannel();
       FileChannel outChannel = new FileOutputStream(dst).getChannel();
       try {
          inChannel.transferTo(0, inChannel.size(), outChannel);
       } finally {
          if (inChannel != null)
             inChannel.close();
          if (outChannel != null)
             outChannel.close();
       }
    }

 }