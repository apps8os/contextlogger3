package org.apps8os.contextlogger3.android.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public final class IOUtil {

	private IOUtil() {
	}

	public static final String getJSONString(InputStream is) throws IOException {
		String jsonString = null;
		Writer w = new StringWriter();
		char[] buffer = new char[1024];
		Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8000);
		int n;
		while((n = reader.read(buffer)) != -1){
			w.write(buffer, 0, n);
		}
		jsonString = w.toString();
		if(is != null){
			is.close();
		}
		return jsonString;
	}
	
}