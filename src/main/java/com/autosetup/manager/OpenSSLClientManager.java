package com.autosetup.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

@Service
public class OpenSSLClientManager {

	public String executeCommand(String command) {
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
