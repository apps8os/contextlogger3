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
 * Chaudhary Nalin (nalin.chaudhary@aalto.fi)- Class is adapted from Funf 
 * example's source code.
 */
package org.apps8os.contextlogger.android.utils;

import java.lang.reflect.Type;
import java.util.Map;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.mit.media.funf.Utils;

public class JsonUtils {
	
	public static Gson getGson() {
		return new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Bundle.class, new BundleJsonSerializer()).create();
	}

	public static class BundleJsonSerializer implements JsonSerializer<Bundle> {
		@Override
		public JsonElement serialize(Bundle bundle, Type type, JsonSerializationContext context) {
			JsonObject object = new JsonObject();
			for (Map.Entry<String, Object> entry : Utils.getValues(bundle).entrySet()) {
				object.add(entry.getKey(), context.serialize(entry.getValue()));
			}
			return object;
		}
	}
}
