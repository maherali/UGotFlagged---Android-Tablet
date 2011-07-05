package com.agilismobility.util.xpath;

import java.util.Vector;

import org.kxml2.kdom.Node;

public class XPathExpression {
	private String[] locationStepStringsArray;
	private Vector resultNodeSet;
	private XPathOperation operation;

	public XPathExpression(Node startNode, String expression) {
		Vector tmp = new Vector();

		this.operation = new XPathOperation();
		// parse
		if (expression.startsWith("//")) {
			// this way of handling "//" is obviously incomplete
			// but we allow it like this because of the lacking resources
			tmp.addElement("//");
			expression = new String(expression.toCharArray(), 2, expression.length() - 2);
		} else if (expression.startsWith("/")) {
			tmp.addElement("/");
			// trace the root element
			expression = new String(expression.toCharArray(), 1, expression.length() - 1);
		}

		// because there is no support for StringTokenizer
		// on j2me we remove this
		/*
		 * StringTokenizer st = new StringTokenizer(expression, "/");
		 * locationStepStringsArray = new String[st.countTokens()]; for(int i =
		 * 0; i < locationStepStringsArray.length; i++) {
		 * locationStepStringsArray[i] = st.nextToken(); }
		 */
		for (int start = 0, end = 0; end < expression.length() - 1 && end != -1; start = end + 1) {
			end = expression.indexOf("/", start);

			// if last token - Check if it is followed by an operator
			if (end == -1) {
				if ((end = expression.indexOf("=", start)) != -1) {
					tmp.addElement(new String(expression.toCharArray(), start, end - start));
					operation.setOperationString(new String(expression.toCharArray(), end, expression.length() - end));
					end = -1;
					break;
				} else {
					tmp.addElement(new String(expression.toCharArray(), start, expression.length() - start));
				}
			} else {
				tmp.addElement(new String(expression.toCharArray(), start, end - start));
			}

		}
		locationStepStringsArray = new String[tmp.size()];
		tmp.copyInto(locationStepStringsArray);
		tmp = null;

		// the result node set should contain nodes
		// with regard to the starting poing of the xpath expression
		// for now just pass the root of the document
		resultNodeSet = new Vector();
		resultNodeSet.addElement(startNode);

		// start processing every location
		for (int j = 0; j < locationStepStringsArray.length; j++) {
			String locationStepString = locationStepStringsArray[j];
			XPathLocationStep locationStep = new XPathLocationStep(locationStepString);

			resultNodeSet = locationStep.getResult(resultNodeSet);
		}
	}

	public Vector getResult() {
		return resultNodeSet;
	}

	// public XPathOperation getOperation() {
	// return operation;
	// }
	//
	// public void setOperation(XPathOperation operation) {
	// this.operation = operation;
	// }

}
