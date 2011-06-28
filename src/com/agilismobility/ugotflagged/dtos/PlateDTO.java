package com.agilismobility.ugotflagged.dtos;

import org.w3c.dom.Node;

import com.agilismobility.ugotflagged.utils.XMLHelper;


public class PlateDTO {
	public int identifier;
	public String tag;
	public String issuer;
	public int timesReported;
	public boolean isFollowing;

	public PlateDTO(XMLHelper xml, Node plate) {
		this.identifier = xml.intValueForNode(plate, "identifier");
		this.tag = xml.textValueForNode(plate, "tag");
		this.issuer = xml.textValueForNode(plate, "issuer");
		this.timesReported = xml.intValueForNode(plate, "times_reported");
		this.isFollowing = xml.boolValueForNode(plate, "is_following");
	}
}
/*
 * add this to lifelog iphone server proxy request.cachePolicy =
 * NSURLRequestReloadIgnoringLocalAndRemoteCacheData;
 * request.HTTPShouldHandleCookies = true; request.timeoutInterval = 60;
 */
