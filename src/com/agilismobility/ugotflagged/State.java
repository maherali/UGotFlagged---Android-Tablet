package com.agilismobility.ugotflagged;

import java.util.ArrayList;
import java.util.Vector;

import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.util.xpath.OpenXml;

public class State {
	public String name = "";
	public String id = "";

	@Override
	public String toString() {
		return (name + " (" + id + ")");
	}

	static ArrayList<State> parse(XMLHelper xmlHelper) {
		OpenXml root = xmlHelper.getDoc();
		ArrayList<State> list = new ArrayList<State>();
		Vector<OpenXml> states = root.elements("array/item");
		for (int i = 0; i < states.size(); i++) {
			list.add(new State().parse(states.get(i)));
		}
		return list;
	}

	private State parse(OpenXml openXml) {
		this.name = openXml.element.getAttributeValue(null, "name");
		this.id = openXml.element.getAttributeValue(null, "itemid");
		return this;
	}

}
