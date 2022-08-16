package com.poc.kubeappswrapper.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CertificateManager {

	@Value("${opensslconfile}")
	private String opensslconfile;
	

	public String readCertificate(String tenantName) {
		return executeCommand("openssl x509 -in "+tenantName+"_cert.cert");
	}
	
	public String readPublicCertificate(String tenantName) {
		return executeCommand("cat "+tenantName+".key");
	}
	
	public String createCertificate(String tenantName) {
		String params = "/C=DE/ST=BE/L=Berline/CN=www." + tenantName + ".com";
		
		//executeCommand("sh /Users/A118448265/Desktop/runpath/register.sh "+tenantName);
		executeCommand("openssl genpkey -out " + tenantName + ".key -algorithm RSA -pkeyopt rsa_keygen_bits:2048");
		executeCommand("openssl req -new -x509 -key " + tenantName + ".key -nodes -days 365 -out " + tenantName
				+ "_cert.cert -config " + opensslconfile + " -subj '" + params + "'");

		String subId = executeCommand("openssl x509 -in " + tenantName
				+ "_cert.cert -noout -text| grep -A1 'Subject Key Identifier'| tail -n 1");

		String authId = executeCommand("openssl x509 -in " + tenantName
				+ "_cert.cert -noout -text| grep -A1 'Authority Key Identifier' | tail -n 1 ");

		File f = new File(tenantName + "_cert.cert");
		if (f.exists())
			log.info("certificate exist after creation");
		
		return subId.trim() +":"+ authId.trim();
	}

	
	private String executeCommand(String command) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		StringBuilder output = new StringBuilder();
		// Run a command
		processBuilder.command("bash", "-c", command);
		
		try {
			Process process = processBuilder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			int exitVal = process.waitFor();
			if (exitVal == 0) {
				//log.info(output.toString());
			} else {
				// abnormal...
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return output.toString();
	}
}
