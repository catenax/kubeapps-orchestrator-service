package com.poc.kubeappswrapper;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.util.ReflectionUtils;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
@Slf4j
public class KubeappsWrapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(KubeappsWrapperApplication.class, args);
	}

	@Bean
	public List<RetryListener> retryListeners() {

		return Collections.singletonList(new RetryListener() {

			@Override
			public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
				// The 'context.name' attribute has not been set on the context yet. So we have
				// to use reflection.
				Field labelField = ReflectionUtils.findField(callback.getClass(), "val$label");
				ReflectionUtils.makeAccessible(labelField);
				String label = (String) ReflectionUtils.getField(labelField, callback);
				log.trace("Starting retryable method {}", label);
				return true;
			}

			@Override
			public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
					Throwable throwable) {
				log.warn("Retryable method {} threw {}th exception {}", context.getAttribute("context.name"),
						context.getRetryCount(), throwable.toString());
			}

			@Override
			public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
					Throwable throwable) {
				log.trace("Finished retryable method {}", context.getAttribute("context.name"));
			}
		});
	}
}
