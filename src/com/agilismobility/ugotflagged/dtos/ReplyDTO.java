package com.agilismobility.ugotflagged.dtos;

import org.w3c.dom.Node;

import com.agilismobility.ugotflagged.utils.XMLHelper;


public class ReplyDTO {
	public int identifier;
	public String authorAvatarURL;
	public int postID;
	public int ownerID;
	public String author;
	public String timeAgo;
	public String text;

	public ReplyDTO(XMLHelper xml, Node reply) {
		this.identifier = xml.intValueForNode(reply, "identifier");
		this.authorAvatarURL = xml.textValueForNode(reply, "author_avatar_url");
		this.ownerID = xml.intValueForNode(reply, "owner_id");
		this.author = xml.textValueForNode(reply, "author");
		this.timeAgo = xml.textValueForNode(reply, "timeago");
		this.postID = xml.intValueForNode(reply, "post_id");
		this.text = xml.textValueForNode(reply, "text");
	}

}
