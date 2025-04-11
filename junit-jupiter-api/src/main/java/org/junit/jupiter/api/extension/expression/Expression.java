/*
 * Copyright 2015-2025 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.jupiter.api.extension.expression;

import java.io.IOException;

public interface Expression {

	default void evaluateAndAppend(ExpressionContext context, Appendable appendable) throws IOException {
		appendable.append(evaluate(context));
	}

	String evaluate(ExpressionContext context);
}
