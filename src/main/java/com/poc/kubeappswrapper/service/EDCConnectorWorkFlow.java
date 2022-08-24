package com.poc.kubeappswrapper.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.manager.CertificateManager;
import com.poc.kubeappswrapper.manager.DAPsManager;
import com.poc.kubeappswrapper.manager.EDCControlplaneManager;
import com.poc.kubeappswrapper.manager.EDCDataplaneManager;
import com.poc.kubeappswrapper.manager.PostgresDBManager;
import com.poc.kubeappswrapper.manager.VaultManager;
import com.poc.kubeappswrapper.model.CustomerDetails;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EDCConnectorWorkFlow {

	private final CertificateManager certificateManager;
	private final DAPsManager dapsManager;
	private final VaultManager vaultManager;
	private final PostgresDBManager postgresManager;
	private final EDCControlplaneManager edcControlplaneManager;
	private final EDCDataplaneManager edcDataplaneManager;

	public CompletableFuture<Map<String, String>> getWorkFlow(CustomerDetails customerDetails, AppActions workflowAction,
			Map<String, String> inputConfiguration) {
		
		Executor executor = Executors.newFixedThreadPool(4);

		CompletableFuture<Map<String, String>> certificateTask = CompletableFuture.supplyAsync(() -> {
			return certificateManager.createCertificate(customerDetails, inputConfiguration);
		}).handle((res, ex) -> {
			if (ex != null) {
				System.out.println("Oops! We have an exception - " + ex.getMessage());
				return null;
			}
			return res;
		});

		certificateTask.thenApplyAsync(result -> {
			return dapsManager.registerClientInDAPs(customerDetails, result);
		}, executor).handle((res, ex) -> {
			if (ex != null) {
				System.out.println("Oops! We have an exception - " + ex.getMessage());
				return null;
			}
			return res;
		});

		certificateTask.thenApplyAsync(result -> {
			return vaultManager.uploadKeyandValues(customerDetails, result);
		}, executor).thenApply(result -> {
			inputConfiguration.putAll(result);
			return inputConfiguration;
		}).handle((res, ex) -> {
			if (ex != null) {
				System.out.println("Oops! We have an exception - " + ex.getMessage());
				return null;
			}
			return res;
		});

		CompletableFuture<Map<String, String>> pgDBTask = CompletableFuture.supplyAsync(() -> {
			return postgresManager.managePackage(customerDetails, workflowAction, "edc", inputConfiguration);
		}, executor).thenApply(result -> {
			inputConfiguration.putAll(result);
			return inputConfiguration;
		}).handle((res, ex) -> {
			if (ex != null) {
				System.out.println("Oops! We have an exception - " + ex.getMessage());
				return null;
			}
			return res;
		});

		CompletableFuture<Map<String, String>> edcControlplaneTask = certificateTask
				.thenCombine(pgDBTask, (certificateOutput, pgdbotput) -> {
					Map<String, String> inputConfiguration1 = new HashMap<>();
					inputConfiguration1.putAll(certificateOutput);
					inputConfiguration1.putAll(pgdbotput);
					return edcControlplaneManager.managePackage(customerDetails, workflowAction, inputConfiguration1);
				}).thenApply(result -> {
					inputConfiguration.putAll(result);
					return inputConfiguration;
				}).handle((res, ex) -> {
					if (ex != null) {
						System.out.println("Oops! We have an exception - " + ex.getMessage());
						return null;
					}
					return res;
				});

		edcControlplaneTask.thenApplyAsync(result -> {
			return edcDataplaneManager.managePackage(customerDetails, workflowAction, inputConfiguration);
		}, executor).thenApply(result -> {
			inputConfiguration.putAll(result);
			return inputConfiguration;
		}).handle((res, ex) -> {
			if (ex != null) {
				System.out.println("Oops! We have an exception - " + ex.getMessage());
				return null;
			}
			return res;
		});
		
		return edcControlplaneTask;
	}
}
