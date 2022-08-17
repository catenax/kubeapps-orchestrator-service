package com.poc.kubeappswrapper.utility;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.proxy.keyclock.KeyClockManagerProxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeyClockServiceManager {

	private final  KeyClockManagerProxy keyClockProxy;

	public String createNewRealm() {
		String inputJson = "{\n" + "		    'id': 'newrealm',\n" + "		    'realm': 'newrealm',\n"
				+ "		    'displayName': 'New Realm',\n" + "		    'enabled': true,\n"
				+ "		    'sslRequired': 'external',\n" + "		    'registrationAllowed': false,\n"
				+ "		    'loginWithEmailAllowed': true,\n" + "		    'duplicateEmailsAllowed': false,\n"
				+ "		    'resetPasswordAllowed': false,\n" + "		    'editUsernameAllowed': false,\n"
				+ "		    'bruteForceProtected': true\n" + "		  }";

		JSONObject jsonObject = new JSONObject(inputJson);

		return "";//keyClockProxy.createRealm(jsonObject);
	}

	public String createNewUser() {
		return "";
	}

}
