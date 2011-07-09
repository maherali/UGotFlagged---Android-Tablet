package com.agilismobility.ugotflagged.dtos;

import java.util.ArrayList;
import java.util.Vector;

import com.agilismobility.ugotflagged.utils.XMLHelper;
import com.agilismobility.util.xpath.OpenXml;

public class UserDTO extends BaseDTO {
	public int identifier;
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
	public String password;

	public ArrayList<PostDTO> posts;

	public UserDTO parse(OpenXml theUser) {
		this.identifier = theUser.integer("identifier/text()");
		this.userName = theUser.string("user_name/text()");
		this.firstName = theUser.string("first_name/text()");
		this.lastName = theUser.string("last_name/text()");
		this.email = theUser.string("email/text()");
		this.admin = theUser.bool("admin/text()");
		this.totalPosts = theUser.integer("total_posts/text()");
		this.noFollowedUsers = theUser.integer("no_followed_users/text()");
		this.noFollowedPlates = theUser.integer("no_followed_plates/text()");
		this.jonedTimeAgo = theUser.string("joined_timeago/text()");
		this.noFollowers = theUser.integer("no_of_followers/text()");
		this.isFollowed = theUser.bool("is_followed/text()");
		this.canFollow = theUser.bool("can_follow/text()");
		this.avatarID = theUser.integer("avatar/identifier/text()");
		this.avatarMainURL = theUser.string("avatar/main_url/text()");
		this.avatarMainURL = !"".equals(this.avatarMainURL) ? this.avatarMainURL : null;
		posts = new ArrayList<PostDTO>();
		Vector<OpenXml> thePosts = theUser.elements("posts/post");
		for (int i = 0; i < thePosts.size(); i++) {
			posts.add(new PostDTO(thePosts.get(i)));
		}
		return this;
	}

	public UserDTO() {

	}

	public UserDTO(XMLHelper xml) {
		super(xml);
		OpenXml theUser = xml.getDoc();
		parse(theUser);
	}
}
