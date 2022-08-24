package com.poc.kubeappswrapper.service;

import static com.poc.kubeappswrapper.constant.AppActions.ADD;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import com.poc.kubeappswrapper.constant.AppActions;
import com.poc.kubeappswrapper.manager.DFTBackendManager;
import com.poc.kubeappswrapper.manager.DFTFrontendManager;
import com.poc.kubeappswrapper.manager.PostgresDBManager;
import com.poc.kubeappswrapper.model.CustomerDetails;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DFTAppWorkFlow {

	private final PostgresDBManager postgresManager;
	private final DFTBackendManager dftBackendManager;
	private final DFTFrontendManager dftFrontendManager;

	public CompletableFuture<Map<String, String>> getWorkFlow(
			CompletableFuture<Map<String, String>> edcControlplaneTask, CustomerDetails customerDetails,AppActions workflowAction,
			Map<String, String> inputConfiguration) {

		Executor executor = Executors.newFixedThreadPool(2);

		CompletableFuture<Map<String, String>> pgDBTask = CompletableFuture.supplyAsync(() -> {
			return postgresManager.managePackage(customerDetails, workflowAction, "dft", inputConfiguration);
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

		CompletableFuture<Map<String, String>> dftWorkFlow = edcControlplaneTask
				.thenCombine(pgDBTask, (edcconnector, pgdbotput) -> {
					Map<String, String> internalinputConfiguration = new HashMap<>();
					internalinputConfiguration.putAll(edcconnector);
					internalinputConfiguration.putAll(pgdbotput);
					return dftBackendManager.managePackage(customerDetails, workflowAction, internalinputConfiguration);
				}).handle((res, ex) -> {
					if (ex != null) {
						System.out.println("Oops! We have an exception - " + ex.getMessage());
						return null;
					}
					return res;
				});

		dftWorkFlow.thenApplyAsync(result -> {
			return dftFrontendManager.managePackage(customerDetails, workflowAction, result);
		}, executor).handle((res, ex) -> {
			if (ex != null) {
				System.out.println("Oops! We have an exception - " + ex.getMessage());
				return null;
			}
			return res;
		});

		return dftWorkFlow;
	}
}
