package com.agilismobility.ugotflagged.dtos;

import java.util.ArrayList;
import java.util.Vector;

import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.util.xpath.OpenXml;

public class BaseDTO {

	public ArrayList<String> errors = new ArrayList<String>();

	public BaseDTO(XMLHelper xml) {

		Vector<OpenXml> vec = xml.getDoc().elements("/errors/error");
		for (int i = 0; i < vec.size(); i++) {
			errors.add(vec.get(i).string("text()"));
		}
	}

}
