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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.TestExtensionContext.getExtensionContextReturningSingleMethod;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.support.ParameterDeclarations;

public class MustacheExpressionLanguageTests {

	private final ParameterizedTestExtension parameterizedTestExtension = new ParameterizedTestExtension();

	@Test
	void correctlyComputesDisplayNameTemplateWithoutPlaceholders() {
		var testTemplateInvocationContexts = testTemplateInvocationContextsFor(
			TestCaseTemplateWithoutPlaceholders::new);
		assertThat(testTemplateInvocationContexts.getFirst().getDisplayName(0)).isEqualTo("foo");
	}

	private List<TestTemplateInvocationContext> testTemplateInvocationContextsFor(Supplier<Object> testCaseFactory) {
		var extensionContext = getExtensionContextReturningSingleMethod(testCaseFactory.get());
		this.parameterizedTestExtension.supportsTestTemplate(extensionContext);
		return this.parameterizedTestExtension.provideTestTemplateInvocationContexts(extensionContext).toList();
	}

	static class TestCaseTemplateWithoutPlaceholders {

		@ParameterizedTest(name = "foo")
		@ArgumentsSource(FooArgumentsProvider.class)
		void method() {
		}

		static class FooArgumentsProvider implements ArgumentsProvider {

			@Override
			public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters,
					ExtensionContext context) {
				return Stream.of(arguments(new FooArgument("123", "abc")));
			}
		}
	}

	@Test
	void correctlyComputesDisplayNameTemplateSimplePlaceholder() {
		var testTemplateInvocationContexts = testTemplateInvocationContextsFor(TestCaseSimplePlaceholder::new);
		assertThat(testTemplateInvocationContexts.get(0).getDisplayName(0)).isEqualTo("foo 123");
		assertThat(testTemplateInvocationContexts.get(1).getDisplayName(0)).isEqualTo("foo 456");
	}

	static class TestCaseSimplePlaceholder {

		@ParameterizedTest(name = "foo {{bar}}")
		@ArgumentsSource(FooArgumentsProvider.class)
		void method() {
		}

		static class FooArgumentsProvider implements ArgumentsProvider {

			@Override
			public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters,
					ExtensionContext context) {
				return Stream.of(arguments(new FooArgument("123", "abc")), arguments(new FooArgument("456", "def")));
			}
		}
	}

	@Test
	void correctlyComputesDisplayNameTemplateInvalidPlaceholder() {
		var testTemplateInvocationContexts = testTemplateInvocationContextsFor(TestCaseInvalidPlaceholder::new);
		assertThat(testTemplateInvocationContexts.getFirst().getDisplayName(0)).isEqualTo("foo ");
	}

	static class TestCaseInvalidPlaceholder {

		@ParameterizedTest(name = "foo {{barbaz}}")
		@ArgumentsSource(FooArgumentsProvider.class)
		void method() {
		}

		static class FooArgumentsProvider implements ArgumentsProvider {

			@Override
			public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters,
					ExtensionContext context) {
				return Stream.of(arguments(new FooArgument("123", "abc")));
			}
		}
	}

	@Test
	void correctlyComputesDisplayNameTemplateMultiplePlaceholders() {
		var testTemplateInvocationContexts = testTemplateInvocationContextsFor(TestCaseMultiplePlaceholders::new);
		assertThat(testTemplateInvocationContexts.getFirst().getDisplayName(0)).isEqualTo("foo 123 abc foo");
	}

	static class TestCaseMultiplePlaceholders {

		@ParameterizedTest(name = "foo {{bar}} {{baz}} foo")
		@ArgumentsSource(FooArgumentsProvider.class)
		void method() {
		}

		static class FooArgumentsProvider implements ArgumentsProvider {

			@Override
			public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters,
					ExtensionContext context) {
				return Stream.of(arguments(new FooArgument("123", "abc")));
			}
		}
	}

	@Test
	void correctlyComputesDisplayNameTemplateNestedPlaceholders() {
		var testTemplateInvocationContexts = testTemplateInvocationContextsFor(TestCaseNestedPlaceholders::new);
		assertThat(testTemplateInvocationContexts.getFirst().getDisplayName(0)).isEqualTo("123: abc");
	}

	static class TestCaseNestedPlaceholders {

		@ParameterizedTest(name = "{{foo.bar}}: {{foo.baz}}")
		@ArgumentsSource(FooArgumentsProvider.class)
		void method() {
		}

		static class FooArgumentsProvider implements ArgumentsProvider {

			@Override
			public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters,
					ExtensionContext context) {
				return Stream.of(arguments(new FooArgumentWrapper(new FooArgument("123", "abc"))));
			}
		}
	}

	@Test
	void correctlyComputesDisplayNameTemplatePlaceholderList() {
		var testTemplateInvocationContexts = testTemplateInvocationContextsFor(TestCasePlaceholderList::new);
		assertThat(testTemplateInvocationContexts.getFirst().getDisplayName(0)).isEqualTo("(123, abc)(456, def)");
	}

	static class TestCasePlaceholderList {

		@ParameterizedTest(name = "{{#foos}}({{bar}}, {{baz}}){{/foos}}")
		@ArgumentsSource(FooArgumentsProvider.class)
		void method() {
		}

		static class FooArgumentsProvider implements ArgumentsProvider {

			@Override
			public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters,
					ExtensionContext context) {
				return Stream.of(
					arguments(new FooArgumentList(new FooArgument("123", "abc"), new FooArgument("456", "def"))));
			}
		}
	}

	static class FooArgument {
		String bar;
		String baz;

		public FooArgument(String bar, String baz) {
			this.bar = bar;
			this.baz = baz;
		}
	}

	static class FooArgumentWrapper {
		FooArgument foo;

		public FooArgumentWrapper(FooArgument foo) {
			this.foo = foo;
		}
	}

	static class FooArgumentList {
		List<FooArgument> foos;

		public FooArgumentList(FooArgument... foos) {
			this.foos = Arrays.asList(foos);
		}
	}
}
