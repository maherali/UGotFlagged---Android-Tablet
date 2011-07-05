package com.agilismobility.ugotflagged.dtos;

import java.util.ArrayList;
import java.util.Vector;

import com.agilismobility.util.xpath.OpenXml;

public class PostDTO {
	public int totalLikes;
	public boolean canLike;
	public boolean liked;
	public int identifier;
	public int postType;
	public String plateNumber;
	public String plateIssuer;
	public int vehicleType;
	public String vehicle;
	public String title;
	public String text;
	public float lat;
	public float lng;
	public String street;
	public String city;
	public String state;
	public String country;
	public String author;
	public boolean adminAuthor;
	public String timeAgo;
	public int photoID;
	public String photoMainURL;
	public String photoiPhoneURL;
	public String photoFeedURL;
	public String authorAvatarURL;

	public ArrayList<ReplyDTO> replies;

	public PostDTO(OpenXml post) {
		this.liked = post.bool("liked/text()");
		this.canLike = post.integer("can_like/text()") == 1;
		this.identifier = post.integer("identifier/text()");
		this.postType = post.integer("post_type/text()");
		this.totalLikes = post.integer("total_likes/text()");
		this.plateNumber = post.string("plate_number/text()");
		this.plateIssuer = post.string("plate_issuer/text()");
		this.vehicleType = post.integer("vehicle_type/text()");
		this.vehicle = post.string("vehicle/text()");
		this.title = post.string("title/text()");
		this.text = post.string("text/text()");
		this.lat = post.real("lat/text()");
		this.lng = post.real("lng/text()");
		this.street = post.string("street/text()");
		this.city = post.string("city/text()");
		this.state = post.string("state/text()");
		this.country = post.string("country/text()");
		this.author = post.string("author/text()");
		this.adminAuthor = post.bool("admin_author/text()");
		this.timeAgo = post.string("timeago/text()");
		this.photoID = post.integer("photos/photo/identifier/text()");
		this.photoMainURL = post.string("photos/photo/main_url/text()");
		this.photoMainURL = !"".equals(this.photoMainURL) ? this.photoMainURL : null;
		this.photoiPhoneURL = post.string("photos/photo/iphone_url/text()");
		this.photoiPhoneURL = !"".equals(this.photoiPhoneURL) ? this.photoiPhoneURL : null;
		this.photoFeedURL = post.string("photos/photo/feed_url/text()");
		this.photoFeedURL = !"".equals(this.photoFeedURL) ? this.photoFeedURL : null;
		this.authorAvatarURL = post.string("author_avatar_url/text()");
		this.authorAvatarURL = !"".equals(this.authorAvatarURL) ? this.authorAvatarURL : null;

		replies = new ArrayList<ReplyDTO>();
		Vector<OpenXml> theReplies = post.elements("replies/reply");
		for (int i = 0; i < theReplies.size(); i++) {
			replies.add(new ReplyDTO(theReplies.get(i)));
		}
	}

	@Override
	public String toString() {
		return text;
	}
}
