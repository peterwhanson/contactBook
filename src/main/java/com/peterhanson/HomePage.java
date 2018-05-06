package com.peterhanson;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;

//Created by Peter Hanson, the home page for the contact list web application
public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;
	
	//contactNames holds the choices that appear in select dropdown box
	public static ArrayList<String> contactNames = new ArrayList<String>();

	public static String name; //The model for the current input value in nameTextField
	public static String address; //The model for the current input value in addressTextField
	public static String phone; //The model for the current input value in phoneTextField
	public static String email; //The model for the current input value in emailTextField
	public static String relationship; //The model for the current input value in relationshipTextField
	
	
	//and also make "Create new contact" is selected by default
	public static String selected = "Create new contact"; //variable to hold the currently selected value from dropdown box
	public static final String CREATE_NEW_CONTACT_LITERAL = "Create new contact"; //used to compare other strings to, for determining function behavior
	
	//The constructor for the HomePage
	public HomePage(final PageParameters parameters) {
		super(parameters);

		updateContactNames(); //Reads model list of Contact objects from database
		
		//create and add the form to the HomePage
		Form<?> form = new ContactForm("form");
		add(form);
		
		//Test print out all contact names
		//System.out.println(contactNames);
    }
	
	//inner class for the custom Form object
	class ContactForm extends Form<Object> {

		private static final long serialVersionUID = 9099971868919563238L;

		//The components to the form for the fields
		DropDownChoice<String> contactSelectField;
		TextField<String> nameTextField;
		TextField<String> addressTextField;
		TextField<String> phoneTextField;
		TextField<String> emailTextField;
		TextField<String> relationshipTextField;
		
		//The constructor, pass the wicket:id to link it to the correct HTML element
		public ContactForm(String id) {
			super(id);
			
			//initiate the select component and attach a model to it
			add(contactSelectField = new DropDownChoice<String>("contactDropDown", new PropertyModel<String>(HomePage.this, "selected"), contactNames));
			
			//add dynamic ajax behavior to respond to change of selected option
			contactSelectField.add(new AjaxFormComponentUpdatingBehavior("change") {
		        private static final long serialVersionUID = 1L;
		        @Override
		        protected void onUpdate(AjaxRequestTarget target) {
		            
		        	String selectedContactName = contactSelectField.getDefaultModelObjectAsString();
		        	
		        	if(contactSelectField.getDefaultModelObjectAsString() != null) {
		        		
		        		if(selectedContactName.equals(CREATE_NEW_CONTACT_LITERAL) == true) {
		        			//clear fields if creating a new contact
		        			HomePage.name = "";
    						HomePage.address = "";
    						HomePage.phone = "";
    						HomePage.email = "";
    						HomePage.relationship = "";
    						
    						//front end update view with new model
    						setResponsePage(getPage());
		        			
		        		}else {
		        			//user clicked on existing contact name, populate fields associated with that name
		        			
		        			for(String s: contactNames) {
		        				if(s.equals(contactSelectField.getDefaultModelObjectAsString())) {
		        					for(Contact c: WicketApplication.contacts) {
		        						if(c.getName().equals(s)) {
		        							
			        						HomePage.name = c.getName();
			        						HomePage.address = c.getAddress();
			        						HomePage.phone = c.getPhone();
			        						HomePage.email = c.getEmail();
			        						HomePage.relationship = c.getRelationship();
				        					
			        						//front end update view with new model
			        						setResponsePage(getPage());
			        			
			        						break;
			        					}
		        					}
		        				}
		        			}
		        		}
		        	}
		        }
		    });
			
			
			
			add(nameTextField = new TextField<String>("nameText", new PropertyModel<String>(HomePage.this, "name")));
			//nameTextField.setRequired(true); //possibly useful for input validation
			add(addressTextField = new TextField<String>("addressText", new PropertyModel<String>(HomePage.this, "address")));
			add(phoneTextField = new TextField<String>("phoneText", new PropertyModel<String>(HomePage.this, "phone")));
			add(emailTextField = new TextField<String>("emailText", new PropertyModel<String>(HomePage.this, "email")));
			add(relationshipTextField = new TextField<String>("relationshipText", new PropertyModel<String>(HomePage.this, "relationship")));
			
			//potentially useful code snippet found online about adding an on change event listener
//			myForm.add(new AjaxEventBehavior("onchange") {
//			    @Override
//			    protected void onEvent(AjaxRequestTarget target) {
//			         handleFormChange(...);
//			    }
//			});
			
			//add new custom button to form to be used for creating and updating contacts
			add(new Button("save") {
				private static final long serialVersionUID = 6387503322049573347L;

				@Override
				public void onSubmit() {
					
					//Input validation section, similar to what I would do if I had time to figure out Wicket input validation
					//I tried to get error messages to show in Wicket but I did not have time to figure it out
//					if(nameTextField.getInput() == null || !nameTextField.hasRawInput()) {
//						//error("Must include a name");
//						return;
//					}
//						
//					if(nameTextField.getInput().length() > 64) {
//						//error("Name must be 64 or less characters long");
//						return;
//					}
//						
//					if(!phoneTextField.getInput().matches("^[0-9]*$")) {
//						//error("Phone number must be numbers only");
//						return;
//					}
						
					//if creating new contact...
					if(contactSelectField.getDefaultModelObjectAsString().equals(HomePage.CREATE_NEW_CONTACT_LITERAL)) {
						
						//call function on back end to save the new contact
						HomePage.this.saveNewContact(new Contact(nameTextField.getInput(), addressTextField.getInput(), phoneTextField.getInput(), emailTextField.getInput(), relationshipTextField.getInput()));
						
						//update dropdown model
						selected = nameTextField.getInput();
						contactSelectField.setChoices(contactNames);
	
					}else {
						//updating existing contact...
						HomePage.this.updateExistingContact(new Contact(nameTextField.getInput(), addressTextField.getInput(), phoneTextField.getInput(), emailTextField.getInput(), relationshipTextField.getInput()));
					}
					
					//front end update view with new model
					contactSelectField.render();
					setResponsePage(getPage());		
				}
			});
			
			add(new Button("delete") {
				
				private static final long serialVersionUID = -7938382276221616082L;

				@Override
				public void onSubmit() {
					//delete the selected contact
					HomePage.this.deleteContact(nameTextField.getInput());
					//front end update view with new model
					contactSelectField.setChoices(contactNames);
					selected = "Create new contact";
					HomePage.name = "";
					HomePage.address = "";
					HomePage.phone = "";
					HomePage.email = "";
					HomePage.relationship = "";
					setResponsePage(getPage());
				}
			});
		}	
	}
	
	//update the model for the dropdown select component based on names of existing contacts from the database
	public static void updateContactNames() {
		contactNames = new ArrayList<String>();
		contactNames.add(CREATE_NEW_CONTACT_LITERAL);
		for(String s: WicketApplication.getContactKeys()) {
			contactNames.add(s);
		}	
	}
	 

	//handles case where the save button is clicked while "create new contact" is selected
	public void saveNewContact(Contact contactToSave) {
		WicketApplication.addContactToDatabase(contactToSave);
	}
	
	//handles case where the save button is clicked while a pre-existing contact is selected
	public void updateExistingContact(Contact contactToUpdate) {
		WicketApplication.editContactInDatabase(contactToUpdate);
	}
	
	//handles case where the delete button is clicked
	public void deleteContact(String nameToDelete) {
		WicketApplication.removeContactFromDatabase(nameToDelete);
	}
	
}
