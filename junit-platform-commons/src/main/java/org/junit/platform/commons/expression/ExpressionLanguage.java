package org.junit.platform.commons.expression;

public interface ExpressionLanguage {

	String getId();

	Expression parse(String template);
}
