package com.agilismobility.util.xpath;

public class XPathOperation {

	private String operationString = null;
	private String value;
	private String operator;
	private String argument;
	private int argumentType;

	public XPathOperation(String operationString, String value, String operator, String argument) {
		super();
		this.operationString = operationString;
		this.value = value;
		this.operator = operator;
		this.argument = argument;
	}

	public XPathOperation() {
		// TODO Auto-generated constructor stub
	}

	public boolean evaluateBooleanOperation() {

		operationString.trim();
		this.operator = extractOperator();
		this.argument = extractArgument();

		argument.trim();
		// switch (argumentType) {
		if (argument.startsWith("'") || argument.startsWith("\"")) {
			argument = argument.substring(1);
			if (argument.endsWith("'") || argument.endsWith("\"")) {
				argument = argument.substring(0, argument.length() - 1);

				return evaluateString();
			}
		}

		return false;
	}

	private boolean evaluateString() {

		if (operator.equals("="))
			return value.equalsIgnoreCase(argument);
		else if (operator.equals("!="))
			return !value.equalsIgnoreCase(argument);
		else {
		}
		return false;
	}

	private String extractArgument() {

		if (operator != null) {
			return operationString.substring(operator.length());
		} else {
		}

		return null;
	}

	private String extractOperator() {

		if (operationString.startsWith("=")) {
			return "=";
		} else if (operationString.startsWith("!=")) {
			return "!=";
		} else if (operationString.startsWith("<")) {
			return "<";
		} else if (operationString.startsWith(">")) {
			return ">";
		} else {
		}

		return null;
	}

	public String getOperationString() {
		return operationString;
	}

	public void setOperationString(String operationString) {
		this.operationString = operationString;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

	public void setArgumentType(int returnType) {
		this.argumentType = returnType;
	}

	public int getArgumentType() {
		return argumentType;
	}

}
