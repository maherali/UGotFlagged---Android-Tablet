package com.agilismobility.ugotflagged.dtos;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.agilismobility.ugotflagged.utils.XMLHelper;

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
	public ArrayList<ReplyDTO> replies;

	public PostDTO(XMLHelper xml, Node post) {
		this.totalLikes = xml.intValueForNode(post, "total_likes");
		this.liked = xml.boolValueForNode(post, "liked");
		this.canLike = xml.intValueForNode(post, "can_like") == 1;
		this.identifier = xml.intValueForNode(post, "identifier");
		this.postType = xml.intValueForNode(post, "post_type");
		this.totalLikes = xml.intValueForNode(post, "total_likes");
		this.plateNumber = xml.textValueForNode(post, "plate_number");
		this.plateIssuer = xml.textValueForNode(post, "plate_issuer");
		this.vehicleType = xml.intValueForNode(post, "vehicle_type");
		this.vehicle = xml.textValueForNode(post, "vehicle");
		this.title = xml.textValueForNode(post, "title");
		this.text = xml.textValueForNode(post, "text");
		this.lat = xml.floatValueForNode(post, "lat");
		this.lng = xml.floatValueForNode(post, "lng");
		this.street = xml.textValueForNode(post, "street");
		this.city = xml.textValueForNode(post, "city");
		this.state = xml.textValueForNode(post, "state");
		this.country = xml.textValueForNode(post, "country");
		this.author = xml.textValueForNode(post, "author");
		this.adminAuthor = xml.boolValueForNode(post, "admin_author");
		this.timeAgo = xml.textValueForNode(post, "timeago");
		this.photoID = xml.intValueForNode(post, "photo/identifier");
		this.photoMainURL = xml.textValueForNode(post, "photo/main_url");
		this.photoiPhoneURL = xml.textValueForNode(post, "photo/iphone_url");
		this.photoFeedURL = xml.textValueForNode(post, "photo/feed_url");
		replies = new ArrayList<ReplyDTO>();
		NodeList theReplies = xml.nodesForXPath(post, "replies/reply");
		for (int i = 0; i < theReplies.getLength(); i++) {
			replies.add(new ReplyDTO(xml, theReplies.item(i)));
		}
	}
	
	@Override
	public String toString() {
		return text;
	}
}
