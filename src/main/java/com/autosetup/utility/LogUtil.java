package com.autosetup.utility;

import org.apache.commons.lang.StringEscapeUtils;

public class LogUtil {
	
	public static String encode(String message) {
		return StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeJava(message));
	}

}
