package org.junit.platform.commons.expressionlanguage;

public interface ExpressionLanguage {

	String getId();

	Expression parse(String template);
}
