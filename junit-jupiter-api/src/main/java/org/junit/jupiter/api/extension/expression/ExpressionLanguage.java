package org.junit.jupiter.api.extension.expression;

public interface ExpressionLanguage {

	String getId();

	Expression parse(String template);
}
