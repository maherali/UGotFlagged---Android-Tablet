package com.agilismobility.ugotflagged.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import com.agilismobility.util.xpath.OpenXml;

public class XMLHelper {

	private OpenXml doc;
	private String xmlString;

	public XMLHelper(String xmlString) {
		this.xmlString = xmlString;
		this.doc = docFromString(xmlString);
	}

	public Vector<OpenXml> nodesForXPath(String query) {
		return doc.elements(query);
	}

	public OpenXml docFromString(String xml) {
		return OpenXml.parse(xml);
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

	public OpenXml getDoc() {
		return doc;
	}

}
