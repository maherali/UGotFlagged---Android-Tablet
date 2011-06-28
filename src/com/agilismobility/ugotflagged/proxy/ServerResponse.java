package com.agilismobility.ugotflagged.proxy;

import android.os.Bundle;

public class ServerResponse {
	private String xml;
	private boolean mInvalidVersion;
	private Bundle mExpandedErrors;
	private String updateAvailableMessage;
	private String updateAvailableURL;

	public boolean isUpdateAvailable() {
		return updateAvailableMessage != null;
	}

	public String getUpdateMessage() {
		return updateAvailableMessage;
	}

	public String getUpdateURL() {
		return updateAvailableURL;
	}

	public ServerResponse(String xml, int responseCode, String localError) {
		this.xml = xml;
		if (xml != null && !xml.isEmpty()) {

		}

	}

	public String getXML() {
		return xml;
	}

	public boolean hasErrors() {
		return false;
		//return mExpandedErrors.size() > 0;
	}

	public String getErrors() {
		StringBuffer theErrors = new StringBuffer();
		// Vector errors = xml.elements("/errors/error/text()");
		// if (errors.size() == 0)
		// errors = xml.elements("errors/error/text()");
		// for (int i = 0; i < errors.size(); i++) {
		// theErrors.append(errors.elementAt(i).toString()).append("\n");
		// }
		return theErrors.toString();
	}

	public boolean isInvalidVersion() {
		return mInvalidVersion;
	}

	public Bundle getExpandedErrors() {
		return mExpandedErrors;
	}

	public String getExpandedErrorsDescription() {
		return getExpandedErrors().getString("error");
	}

	// public boolean isConnectionError() {
	// return bConnectionError;
	// }
}