package com.peterhanson;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.Application;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import redis.clients.jedis.Jedis;

//The application class
public class WicketApplication extends WebApplication
{
	
	//The Redis Database Structure:
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
	
	public static Jedis jedis = new Jedis("localhost"); //The interface between java and redis, assumes redis is running on localhost
	public static Set<String> contactKeys; //A list of all contacts' names, their primary key
	public static ArrayList<Contact> contacts; //a list of all contacts as model objects

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

		updateContactKeys();
		populateContacts();
	}
	

	//returns contactKeys
	public static Set<String> getContactKeys(){
		return contactKeys;
	}
	
	//updates contactKeys based on database
	public static void updateContactKeys(){
		contactKeys = jedis.smembers(CONTACT_KEYS_SET_NAME);
	}
	
	//adds a contact name
	public static void addToContactKeys(String newContactName) {
		jedis.sadd(CONTACT_KEYS_SET_NAME, newContactName);
		updateContactKeys();
	}
	
	//removes a contact name
	public static void removeFromContactKeys(String oldContactName) {
		jedis.srem(CONTACT_KEYS_SET_NAME, oldContactName);
		updateContactKeys();
	}
	
	//populates the java model object holding the contacts based on the database state
	public static void populateContacts() {
		
		contacts = new ArrayList<Contact>();
		
		for(String nameKey: contactKeys) {
			Map<String, String> contactProperties = jedis.hgetAll(CONTACT_HASHMAP_PREFIX + nameKey);
			Contact contact = new Contact(nameKey, contactProperties.get("address"), contactProperties.get("phone"), contactProperties.get("email"), contactProperties.get("relationship"));
			contacts.add(contact);
		}
		HomePage.updateContactNames();	
	}
	
	//adds a new contact to the database based on passed contact model
	public static void addContactToDatabase(Contact newContact) {
		//the set that keeps track of contacts
		addToContactKeys(newContact.getName());
		
		//the string to access the right hashmap
		String contactHashmapKey = CONTACT_HASHMAP_PREFIX + newContact.getName();
		
		jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_NAME, newContact.getName());
		jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_ADDRESS, newContact.getAddress());
		jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_PHONE, newContact.getPhone());
		jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_EMAIL, newContact.getEmail());
		jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_RELATIONSHIP, newContact.getRelationship());
		contacts.add(newContact);
		HomePage.updateContactNames();
	}
	
	//removes a contact based on name passed from the database
	public static void removeContactFromDatabase(String oldContactName) {
		removeFromContactKeys(oldContactName);
		jedis.hdel(CONTACT_HASHMAP_PREFIX + oldContactName, HASHMAP_FIELDNAME_NAME, HASHMAP_FIELDNAME_ADDRESS, HASHMAP_FIELDNAME_PHONE, HASHMAP_FIELDNAME_EMAIL, HASHMAP_FIELDNAME_RELATIONSHIP);
		populateContacts();
	}
	
	//edits a preexisting contact in the database based on passed contact
	public static void editContactInDatabase(Contact updatedContact) {
		String contactHashmapKey = CONTACT_HASHMAP_PREFIX + updatedContact.getName();
		jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_ADDRESS, updatedContact.getAddress());
		jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_PHONE, updatedContact.getPhone());
		jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_EMAIL, updatedContact.getEmail());
		jedis.hset(contactHashmapKey, HASHMAP_FIELDNAME_RELATIONSHIP, updatedContact.getRelationship());
		populateContacts();
	}
	
}
