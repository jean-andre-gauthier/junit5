/*
 * Copyright 2015-2025 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.jupiter.params;

import static org.junit.jupiter.params.ParameterizedTestExtension.DECLARATION_CONTEXT_KEY;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExecutableInvoker;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.MediaType;
import org.junit.jupiter.api.extension.TestInstances;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.engine.execution.NamespaceAwareStore;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.support.store.NamespacedHierarchicalStore;

public class TestExtensionContext {

	private TestExtensionContext() {
	}

	public static ExtensionContext getExtensionContextReturningSingleMethod(Object testCase) {
		return getExtensionContextReturningSingleMethod(testCase, ignored -> Optional.empty());
	}

	public static ExtensionContext getExtensionContextReturningSingleMethod(Object testCase,
			Function<String, Optional<String>> configurationSupplier) {

		Class<?> testClass = testCase.getClass();
		var method = ReflectionUtils.findMethods(testClass, it -> "method".equals(it.getName())).stream().findFirst();

		return new ExtensionContext() {

			private final NamespacedHierarchicalStore<org.junit.platform.engine.support.store.Namespace> store = new NamespacedHierarchicalStore<>(
				null);

			@Override
			public Optional<Method> getTestMethod() {
				return method;
			}

			@Override
			public Optional<ExtensionContext> getParent() {
				return Optional.empty();
			}

			@Override
			public ExtensionContext getRoot() {
				return this;
			}

			@Override
			public String getUniqueId() {
				return null;
			}

			@Override
			public String getDisplayName() {
				return null;
			}

			@Override
			public Set<String> getTags() {
				return null;
			}

			@Override
			public Optional<AnnotatedElement> getElement() {
				return Optional.empty();
			}

			@Override
			public Optional<Class<?>> getTestClass() {
				return Optional.of(testClass);
			}

			@Override
			public List<Class<?>> getEnclosingTestClasses() {
				return List.of();
			}

			@Override
			public Optional<TestInstance.Lifecycle> getTestInstanceLifecycle() {
				return Optional.empty();
			}

			@Override
			public Optional<Object> getTestInstance() {
				return Optional.empty();
			}

			@Override
			public Optional<TestInstances> getTestInstances() {
				return Optional.empty();
			}

			@Override
			public Optional<Throwable> getExecutionException() {
				return Optional.empty();
			}

			@Override
			public Optional<String> getConfigurationParameter(String key) {
				return configurationSupplier.apply(key);
			}

			@Override
			public <T> Optional<T> getConfigurationParameter(String key, Function<String, T> transformer) {
				return configurationSupplier.apply(key).map(transformer);
			}

			@Override
			public void publishReportEntry(Map<String, String> map) {
			}

			@Override
			public void publishFile(String fileName, MediaType mediaType, ThrowingConsumer<Path> action) {
			}

			@Override
			public void publishDirectory(String name, ThrowingConsumer<Path> action) {
			}

			@Override
			public Store getStore(Namespace namespace) {
				var store = new NamespaceAwareStore(this.store,
					org.junit.platform.engine.support.store.Namespace.create(namespace.getParts()));
				method //
						.map(it -> new ParameterizedTestContext(testClass, it,
							it.getAnnotation(ParameterizedTest.class))) //
						.ifPresent(ctx -> store.put(DECLARATION_CONTEXT_KEY, ctx));
				return store;
			}

			@Override
			public Store getStore(StoreScope scope, Namespace namespace) {
				return getStore(namespace);
			}

			@Override
			public ExecutionMode getExecutionMode() {
				return ExecutionMode.SAME_THREAD;
			}

			@Override
			public ExecutableInvoker getExecutableInvoker() {
				return new ExecutableInvoker() {
					@Override
					public Object invoke(Method method, Object target) {
						return null;
					}

					@Override
					public <T> T invoke(Constructor<T> constructor, Object outerInstance) {
						return ReflectionUtils.newInstance(constructor);
					}
				};
			}
		};
	}
}
