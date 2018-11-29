package com.app.sample.fchat.model;

import java.io.Serializable;

public class Friend implements Serializable {
	private String id;
	private String name;
	private String photo;
	private String email;
	public Friend(String id, String name, String photo, String email) {
		this.id = id;
		this.name = name;
		this.photo = photo;
		this.email=email;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhoto() {
		return photo;
	}
	public String getEmail(){return email;}

	public void setId(String id){ this.id = id; }

	public void setName(String name){ this.name = name; }

	public  void setPhoto(String photo){ this.photo = photo; }
	public void setEmail(String email){this.email=email;}
}
