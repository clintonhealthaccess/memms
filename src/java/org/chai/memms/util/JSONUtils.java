package org.chai.memms.util;

/* 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class JSONUtils {

	public static String getJSONFromMap(Map<String, ? extends Object> map) {
		String result = null;
		if (map != null) {
			try {
				JSONObject jsonObject = (JSONObject)JSONSerializer.toJSON(map);
				result = jsonObject.toString();
			} catch (JSONException e) {
				// log
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapFromJSON(String jsonString) {
		Map<String, Object> descriptions = new HashMap<String, Object>();
		if (jsonString != null) {
			try {
				JSONObject jsonObject = JSONObject.fromObject(jsonString);
				return (Map<String, Object>)getObjectFromJSONObject(jsonObject);
			} catch (JSONException e) {
				// log
			}
		}
		return descriptions;
	}
	
	private static Object getObjectFromJSONObject(Object object) {
		if (object instanceof JSONObject) {
			Map<String, Object> descriptions = new HashMap<String, Object>();
			@SuppressWarnings("unchecked")
			Iterator<String> keyIterator = ((JSONObject)object).keys();
			while (keyIterator.hasNext()) {
				String type = (String) keyIterator.next();
				descriptions.put(type, getObjectFromJSONObject(((JSONObject)object).get(type)));
			}
			return descriptions;
		}
		else return object;
	}
	
}