package com.agilismobility.ugotflagged.dtos;

import com.agilismobility.util.xpath.OpenXml;

public class ReplyDTO {
	public int identifier;
	public String authorAvatarURL;
	public int postID;
	public int ownerID;
	public String author;
	public String timeAgo;
	public String text;

	public ReplyDTO(OpenXml reply) {
		this.identifier = reply.integer("identifier/text()");
		this.authorAvatarURL = reply.string("author_avatar_url/text()");
		this.authorAvatarURL = !"".equals(this.authorAvatarURL) ? this.authorAvatarURL : null;
		this.ownerID = reply.integer("owner_id/text()");
		this.author = reply.string("author/text()");
		this.timeAgo = reply.string("timeago/text()");
		this.postID = reply.integer("post_id/text()");
		this.text = reply.string("text/text()");
	}

}
