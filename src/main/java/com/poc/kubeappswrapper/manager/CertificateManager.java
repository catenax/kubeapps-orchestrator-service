package com.poc.kubeappswrapper.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.constant.TriggerStatusEnum;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerDetails;
import com.poc.kubeappswrapper.entity.AutoSetupTriggerEntry;
import com.poc.kubeappswrapper.exception.ServiceException;
import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.utility.Certutil;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificateManager {

	@Value("${opensslconfile}")
	private String opensslconfile;

	private final AutoSetupTriggerManager autoSetupTriggerManager;

	@SneakyThrows
	public Map<String, String> createCertificate(CustomerDetails customerDetails, Map<String, String> inputData,
			AutoSetupTriggerEntry triger) {

		Map<String, String> outputData = new HashMap<>();
		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("CERTIFICATE").triggerIdforinsert(triger.getTriggerId()).build();

		try {

			String tenantName = customerDetails.getTenantName();
			log.info(tenantName + "- certificate creating");

			String C = Optional.ofNullable(customerDetails.getCountry()).map(r -> r).orElse("DE");
			String ST = Optional.ofNullable(customerDetails.getState()).map(r -> r).orElse("BE");
			String L = Optional.ofNullable(customerDetails.getCity()).map(r -> r).orElse("Berline");

			String params = String.format("O=%s, OU=%s, C=%s, ST=%s, L=%s, CN=%s", tenantName,
					customerDetails.getBpnNumber(), C, ST, L, "www." + tenantName + ".com");

			Certutil.CertKeyPair certificateDetails = Certutil.generateSelfSignedCertificateSecret(params, null, null);
			X509Certificate certificate = certificateDetails.certificate();
			String clientId = Certutil.getClientId(certificate);

			outputData.put("dapsclientid", clientId);
			outputData.put("selfsigncertificate", Certutil.getAsString(certificate));
			outputData.put("selfsigncertificateprivatekey",
					Certutil.getAsString(certificateDetails.keyPair().getPrivate()));

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());
			log.info(tenantName + "- certificate created");

		} catch (Exception ex) {

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("CertificateManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}

		return outputData;

	}

	public Map<String, String> createCertificateusingOpenssl(CustomerDetails customerDetails,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {
		Map<String, String> outputData = new HashMap<>();
		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("CERTIFICATE").triggerIdforinsert(triger.getTriggerId()).build();

		try {
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

			outputData.put("dapsclientid", subId.trim() + ":" + authId.trim());
			outputData.put("selfsigncertificate", readCertificate(tenantName));
			outputData.put("selfsigncertificateprivatekey", readPrivateCertificate(tenantName));

			log.info(tenantName + "- certificate created");
		} catch (Exception ex) {

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("CertificateManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails);
		}

		return outputData;
	}

	private String readPrivateCertificate(String tenantName) {
		return executeCommand("openssl x509 -in " + tenantName + "_cert.cert");
	}

	private String readCertificate(String tenantName) {
		return executeCommand("cat " + tenantName + ".key");
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
