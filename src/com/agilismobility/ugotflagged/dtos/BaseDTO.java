package com.agilismobility.ugotflagged.dtos;

import java.util.ArrayList;

import org.w3c.dom.NodeList;

import com.agilismobility.ugotflagged.utils.XMLHelper;

public class BaseDTO {

	public ArrayList<String> errors = new ArrayList<String>();

	public BaseDTO(XMLHelper xml) {
		NodeList theErrors = xml.nodesForXPath("errors/error");
		for (int i = 0; i < theErrors.getLength(); i++) {
			errors.add(theErrors.item(i).getChildNodes().item(0).getTextContent());
		}
	}

}
