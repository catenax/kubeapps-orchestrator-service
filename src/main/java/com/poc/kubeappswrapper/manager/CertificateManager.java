package com.poc.kubeappswrapper.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.model.CustomerDetails;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CertificateManager {

	@Value("${opensslconfile}")
	private String opensslconfile;

	public String readCertificate(String tenantName) {
		return executeCommand("openssl x509 -in " + tenantName + "_cert.cert");
	}

	public String readPublicCertificate(String tenantName) {
		return executeCommand("cat " + tenantName + ".key");
	}

	public Map<String, String> createCertificate(CustomerDetails customerDetails, Map<String, String> inputData) {

		String tenantName = customerDetails.getTenantName();
		String C = Optional.ofNullable(customerDetails.getCountry()).map(r -> r).orElse("DE");
		String ST = Optional.ofNullable(customerDetails.getState()).map(r -> r).orElse("BE");
		String L = Optional.ofNullable(customerDetails.getCity()).map(r -> r).orElse("Berline");

		String params = "/C=" + C + "/ST=" + ST + "/L=" + L + "/CN=www." + tenantName + ".com";

		executeCommand("openssl genpkey -out " + tenantName + ".key -algorithm RSA -pkeyopt rsa_keygen_bits:2048");
		executeCommand("openssl req -new -x509 -key " + tenantName + ".key -nodes -days 365 -out " + tenantName
				+ "_cert.cert -config " + opensslconfile + " -subj '" + params + "'");

		String subId = executeCommand("openssl x509 -in " + tenantName
				+ "_cert.cert -noout -text| grep -A1 'Subject Key Identifier'| tail -n 1");

		String authId = executeCommand("openssl x509 -in " + tenantName
				+ "_cert.cert -noout -text| grep -A1 'Authority Key Identifier' | tail -n 1 ");

//		File f = new File(tenantName + "_cert.cert");
//		if (f.exists())
//			log.info("certificate exist after creation");

		Map<String, String> outputData = new HashMap<>();
		outputData.put("dapsclientid", subId.trim() + ":" + authId.trim());
		log.info(tenantName + "- certificate created");
		return outputData;
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
				// log.info(output.toString());
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
