package org.junit.platform.commons.expression;

import java.io.IOException;

public interface Expression {

	default void evaluateAndAppend(ExpressionContext context, Appendable appendable) throws IOException {
		appendable.append(evaluate(context));
	}

	String evaluate(ExpressionContext context);
}
