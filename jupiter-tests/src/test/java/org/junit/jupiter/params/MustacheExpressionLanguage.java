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

import java.io.StringReader;
import java.io.StringWriter;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import org.junit.platform.commons.expression.Expression;
import org.junit.platform.commons.expression.ExpressionLanguage;

public class MustacheExpressionLanguage implements ExpressionLanguage {

	MustacheFactory mustacheFactory;

	MustacheExpressionLanguage() {
		mustacheFactory = new DefaultMustacheFactory();
	}

	@Override
	public String getId() {
		return "mustache";
	}

	@Override
	public Expression parse(String template) {
		Mustache mustache = mustacheFactory.compile(new StringReader(template), template);
		return ctx -> {
			StringWriter stringWriter = new StringWriter();
			mustache.execute(stringWriter, ctx);
			return stringWriter.toString();
		};
	}
}
