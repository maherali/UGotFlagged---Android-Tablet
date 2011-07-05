package com.agilismobility.ugotflagged.dtos;

import com.agilismobility.util.xpath.OpenXml;

public class PlateDTO {
	public int identifier;
	public String tag;
	public String issuer;
	public int timesReported;
	public boolean isFollowing;

	public PlateDTO(OpenXml plate) {
		this.identifier = plate.integer("identifier/text()");
		this.tag = plate.string("tag/text()");
		this.issuer = plate.string("issuer/text()");
		this.timesReported = plate.integer("times_reported/text()");
		this.isFollowing = plate.bool("is_following/text()");
	}

}
