package com.peterhanson;

//Created by Peter Hanson, a simple POJO with 5 String fields to hold information about a contact
public class Contact {
	
	private String name; //The name of the contact, must not be null and have at least 1 character, and be unique from other contact names
	private String address; //The address of the contact
	private String phone; //The phone number of the contact, in ########## format
	private String email; //the email of the contact, *@*.*
	private String relationship; //the nature of the relationship with the contact
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRelationship() {
		return relationship;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	
	public Contact(String name, String address, String phone, String email, String relationship) {
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.email = email;
		this.relationship = relationship;
	}

}
