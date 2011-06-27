package com.agilismobility.ugotflagged.proxy;

import android.os.Bundle;

public class ServerResponse {
	// private OpenXml xml;
	private boolean mInvalidVersion;
	private boolean bConnectionError;
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
		super();
		// this.xml = xml;
		// if (xml != null) {
		// mInvalidVersion = "true".equals(xml.string("@invalid_version"));
		// if(mInvalidVersion){
		// updateAvailableURL = xml.string("@update_url");
		// }
		// }
		// Bundle resultBundle = new Bundle();
		// String err = null;
		// if (responseCode == 0) {
		// bConnectionError = true;
		// if (localError != null) {
		// resultBundle.putString("error", localError);
		// resultBundle.putString("severity", "normal");
		// }
		// }
		// if (responseCode == 500 || responseCode == 404) {
		// err =
		// "TripCase has encountered an error and our team has been notified of the issue.";
		// if (localError != null) {
		// err = localError + ". " + String.format("Response Code: %d.",
		// responseCode) + err;
		// }
		// resultBundle.putString("error", localError);
		// resultBundle.putString("severity", "normal");
		// }
		// if (isInvalidVersion()) {
		// resultBundle.putString("error", getErrors());
		// resultBundle.putString("severity", "invalid_version");
		// }
		// if (xml != null && ((xml.elements("/errors/error/text()").size() > 0)
		// || (xml.elements("errors/error/text()").size() > 0))) {
		// resultBundle.putString("error", getErrors());
		// resultBundle.putString("severity", "normal");
		// }
		// if (xml != null) {
		// Vector<OpenXml> updates = xml.elements("/StartupResponse/Update");
		// if (updates.size() > 0) {
		// updateAvailableMessage =
		// xml.string("/StartupResponse/Update/Message/text()");
		// updateAvailableURL =
		// xml.string("/StartupResponse/Update/UpdateUrl/text()");
		// }
		//
		// }
		// this.mExpandedErrors = resultBundle;
	}

	//
	// public String getXml() {
	// return xml;
	// }

	public boolean hasErrors() {
		return mExpandedErrors.size() > 0;
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