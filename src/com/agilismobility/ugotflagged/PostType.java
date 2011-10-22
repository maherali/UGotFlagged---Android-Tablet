package com.agilismobility.ugotflagged;

import java.util.ArrayList;
import java.util.Vector;

import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.util.xpath.OpenXml;

public class PostType {

	public String name = "";
	public String image = "";
	public String id = "";

	@Override
	public String toString() {
		return name;
	}

	static ArrayList<PostType> parse(XMLHelper xmlHelper) {
		OpenXml root = xmlHelper.getDoc();
		ArrayList<PostType> list = new ArrayList<PostType>();
		Vector<OpenXml> postTypes = root.elements("array/item");
		for (int i = 0; i < postTypes.size(); i++) {
			list.add(new PostType().parse(postTypes.get(i)));
		}
		return list;
	}

	private PostType parse(OpenXml openXml) {
		this.name = openXml.element.getAttributeValue(null, "name");
		this.id = openXml.element.getAttributeValue(null, "itemid");
		this.image = openXml.element.getAttributeValue(null, "itemimage");
		return this;
	}
}
