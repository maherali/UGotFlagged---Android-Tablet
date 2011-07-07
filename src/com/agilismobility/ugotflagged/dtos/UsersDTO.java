package com.agilismobility.ugotflagged.dtos;

import java.util.ArrayList;
import java.util.Vector;

import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.util.xpath.OpenXml;

public class UsersDTO extends BaseDTO {

	ArrayList<UserDTO> users;

	public UsersDTO(XMLHelper xml) {
		super(xml);
		OpenXml root = xml.getDoc();
		users = new ArrayList<UserDTO>();
		Vector<OpenXml> theUsers = root.elements("user");
		for (int i = 0; i < theUsers.size(); i++) {
			users.add(new UserDTO().parse(theUsers.get(i)));
		}
	}

	public ArrayList<UserDTO> getUsers() {
		return users;
	}

}
