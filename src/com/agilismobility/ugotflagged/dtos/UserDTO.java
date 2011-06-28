package com.agilismobility.ugotflagged.dtos;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.agilismobility.ugotflagged.utils.XMLHelper;

public class UserDTO {
	public String identifier;
	public String userName;
	public String firstName;
	public String lastName;
	public String email;
	public boolean admin;
	public int totalPosts;
	public int noFollowedUsers;
	public int noFollowedPlates;
	public String jonedTimeAgo;
	public int noFollowers;
	public boolean isFollowed;
	public boolean canFollow;
	public int avatarID;
	public String avatarMainURL;

	public ArrayList<PostDTO> posts;

	public UserDTO(XMLHelper xml) {
		Node theUser = xml.nodesForXPath("user").item(0);
		this.identifier = xml.textValueForNode(theUser, "identifier");
		this.userName = xml.textValueForNode(theUser, "user_name");
		this.firstName = xml.textValueForNode(theUser, "first_name");
		this.lastName = xml.textValueForNode(theUser, "last_name");
		this.email = xml.textValueForNode(theUser, "email");
		this.admin = xml.boolValueForNode(theUser, "admin");
		this.totalPosts = xml.intValueForNode(theUser, "total_posts");
		this.noFollowedUsers = xml.intValueForNode(theUser, "no_followed_users");
		this.noFollowedPlates = xml.intValueForNode(theUser, "no_followed_plates");
		this.jonedTimeAgo = xml.textValueForNode(theUser, "joined_timeago");
		this.noFollowers = xml.intValueForNode(theUser, "no_of_followers");
		this.isFollowed = xml.boolValueForNode(theUser, "is_followed");
		this.canFollow = xml.boolValueForNode(theUser, "can_follow");
		this.avatarID = xml.intValueForNode(theUser, "avatar/identifier");
		this.avatarMainURL = xml.textValueForNode(theUser, "avatar/main_url");

		posts = new ArrayList<PostDTO>();
		NodeList thePosts = xml.nodesForXPath(theUser, "posts/post");
		for (int i = 0; i < thePosts.getLength(); i++) {
			posts.add(new PostDTO(xml, thePosts.item(i)));
		}
	}
}
