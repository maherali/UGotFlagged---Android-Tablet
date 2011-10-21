package com.agilismobility.ugotflagged.dtos;

import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.util.xpath.OpenXml;

public class GeocodeDTO extends BaseDTO {

	public String street1, street2, city, state, zipCode, countryCode;

	public void parse(OpenXml post) {
		this.street1 = post.string("intersection/street1/text()");
		this.street2 = post.string("intersection/street2/text()");
		this.city = post.string("intersection/placename/text()");
		this.state = post.string("intersection/adminCode1/text()");
		this.zipCode = post.string("intersection/postalcode/text()");
		this.countryCode = post.string("intersection/countryCode/text()");
	}

	public GeocodeDTO(OpenXml geo) {
		parse(geo);
	}

	public GeocodeDTO(XMLHelper xml) {
		super(xml);
		OpenXml theGeo = xml.getDoc();
		try {
			parse(theGeo);
		} catch (Exception e) {
		}
	}
}
