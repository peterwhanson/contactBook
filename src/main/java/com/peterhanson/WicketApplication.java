package com.peterhanson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import redis.clients.jedis.Jedis;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 * 
 * @see com.peterhanson.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
	
	
	//Redis Database Structure
	//Contact objects are just objects with 5 strings, the name will be the primary key
	//A set of all primary keys, the name is stored in the String CONTACT_KEYS_SET_NAME
	//A hashmap, the name is stored in the String CONTACT_HASHMAP_PREFIX, called "contactsMap" + name, with 5 fields: "name", "address", "phone", "email", "relationship"
			
	public static final String CONTACT_KEYS_SET_NAME = "contactsKeys";
	public static final String CONTACT_HASHMAP_PREFIX = "contactsMap";
	public static final String HASHMAP_FIELDNAME_NAME = "name";
	public static final String HASHMAP_FIELDNAME_ADDRESS = "address";
	public static final String HASHMAP_FIELDNAME_PHONE = "phone";
	public static final String HASHMAP_FIELDNAME_EMAIL = "email";
	public static final String HASHMAP_FIELDNAME_RELATIONSHIP = "relationship";
	
	public static Jedis jedis = new Jedis("localhost");
	public static Set<String> contactKeys;
	public static ArrayList<Contact> contacts;
	
	
	
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();
		
		this.updateContactKeys();
		this.populateContacts();
		
		
		
//		System.out.println(" ");
//		System.out.println(" ");
//		System.out.println(value);
//		System.out.println(" ");
//		System.out.println(" ");

		// add your configuration here
	}
	

	
	public static Set<String> getContactKeys(){
		
		return contactKeys;
	}
	
	public void updateContactKeys(){
		
		this.contactKeys = this.jedis.smembers(CONTACT_KEYS_SET_NAME);
//		this.populateContacts();
		
	}
	
	public void addToContactKeys(String newContactName) {
		this.jedis.sadd(CONTACT_KEYS_SET_NAME, newContactName);
		this.updateContactKeys();
	}
	
	public void removeFromContactKeys(String oldContactName) {
		this.jedis.srem(CONTACT_KEYS_SET_NAME, oldContactName);
		this.updateContactKeys();
	}
	
	public void populateContacts() {
		
		contacts = new ArrayList<Contact>();
		
		for(String nameKey: contactKeys) {
			Map<String, String> contactProperties = this.jedis.hgetAll(CONTACT_HASHMAP_PREFIX + nameKey);
			Contact contact = new Contact();
			contact.setName(nameKey);
			contact.setAddress(contactProperties.get("address"));
			contact.setPhone(contactProperties.get("phone"));
			contact.setEmail(contactProperties.get("email"));
			contact.setRelationship(contactProperties.get("relationship"));
			contacts.add(contact);
		}
		
	}
	
	public void addContact(Contact newContact) {
		
		
		this.addToContactKeys(newContact.getName());
		
		String contactHashmapKey = CONTACT_HASHMAP_PREFIX + newContact.getName();
		
		this.jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_NAME, newContact.getName());
		this.jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_ADDRESS, newContact.getAddress());
		this.jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_PHONE, newContact.getPhone());
		this.jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_EMAIL, newContact.getEmail());
		this.jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_RELATIONSHIP, newContact.getRelationship());
		contacts.add(newContact);
		
	}
	
	public void removeContact(String oldContactName) {
		
		this.removeFromContactKeys(oldContactName);
		this.jedis.hdel(CONTACT_HASHMAP_PREFIX + oldContactName);
		this.populateContacts();
		
	}
	
	public void editContact(Contact updatedContact) {
		
		String contactHashmapKey = CONTACT_HASHMAP_PREFIX + updatedContact.getName();
		this.jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_ADDRESS, updatedContact.getAddress());
		this.jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_PHONE, updatedContact.getPhone());
		this.jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_EMAIL, updatedContact.getEmail());
		this.jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_RELATIONSHIP, updatedContact.getRelationship());
		this.populateContacts();
	}
	
	
}
