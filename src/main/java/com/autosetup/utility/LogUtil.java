package com.autosetup.utility;

import org.owasp.esapi.ESAPI;

public class LogUtil {

	private LogUtil() {
	}

	public static String encode(String message) {
	    message = message.replace( '\n' ,  '_' ).replace( '\r' , '_' )
	      .replace( '\t' , '_' );
	    message = ESAPI.encoder().encodeForHTML( message );
	    return message;
	}

}
