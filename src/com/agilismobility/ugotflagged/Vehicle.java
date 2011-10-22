package com.agilismobility.ugotflagged;

import java.util.ArrayList;
import java.util.Vector;

import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.util.xpath.OpenXml;

public class Vehicle {
	public String name = "";
	public String image = "";
	public String id = "";

	@Override
	public String toString() {
		return name;
	}

	static ArrayList<Vehicle> parse(XMLHelper xmlHelper) {
		OpenXml root = xmlHelper.getDoc();
		ArrayList<Vehicle> list = new ArrayList<Vehicle>();
		Vector<OpenXml> vehicles = root.elements("array/item");
		for (int i = 0; i < vehicles.size(); i++) {
			list.add(new Vehicle().parse(vehicles.get(i)));
		}
		return list;
	}

	private Vehicle parse(OpenXml openXml) {
		this.name = openXml.element.getAttributeValue(null, "name");
		this.id = openXml.element.getAttributeValue(null, "itemid");
		this.image = openXml.element.getAttributeValue(null, "itemimage");
		return this;
	}

}
