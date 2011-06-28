package com.agilismobility.ugotflagged.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLHelper {

	private Document doc;
	private String xmlString;

	public XMLHelper(String xmlString) {
		this.xmlString = xmlString;
		this.doc = docFromString(xmlString);
	}

	public NodeList nodesForXPath(String query) {
		return nodesForXPath(doc, query);
	}

	public NodeList nodesForXPath(Node theNode, String query) {
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		XPathExpression expr;
		NodeList nodeSetResult = null;
		try {
			expr = xpath.compile(query);
			nodeSetResult = (NodeList) expr.evaluate(theNode, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return nodeSetResult;
	}

	public Document docFromString(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		dbf.setValidating(false);
		try {
			dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			dbf.setFeature("http://xml.org/sax/features/validation", false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			System.out.println("XML parse error: " + e.getMessage());
			return null;
		} catch (SAXException e) {
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("I/O exeption: " + e.getMessage());
			return null;
		}
		return doc;
	}

	public String getXmlString() {
		return xmlString;
	}

	public static String slurp(InputStream in) {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		try {
			for (int n; (n = in.read(b)) != -1;) {
				out.append(new String(b, 0, n));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out.toString();
	}

	public String textValueForNode(Node node, String query) {
		NodeList nodes = nodesForXPath(node, query);
		if (nodes.getLength() == 0) {
			return null;
		}
		Node item = nodes.item(0);
		if (item.getChildNodes().getLength() > 0) {
			return item.getChildNodes().item(0).getTextContent();
		} else {
			return null;
		}
	}

	public float floatValueForNode(Node node, String query) {
		String v = textValueForNode(node, query);
		if (v != null) {
			return new Float(v);
		} else {
			return 0.0f;
		}
	}

	public int intValueForNode(Node node, String query) {
		String v = textValueForNode(node, query);
		if (v != null) {
			return new Integer(v);
		} else {
			return 0;
		}
	}

	public boolean boolValueForNode(Node node, String query) {
		String v = textValueForNode(node, query);
		return new Boolean(v);
	}

}
