package com.peterhanson;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;
	
	
	
	
	//choices in dropdown box
	public static List<String> contactNames = new ArrayList<String>();

	
	
	String name;
	String address;
	String phone;
	String email;
	String relationship;
	
	
	
	//variable to hold the selected value from dropdown box,
	//and also make "Create new contact" is selected by default
	private String selected = "Create new contact";
	
	public HomePage(final PageParameters parameters) {
		super(parameters);

		//add(new Label("version", getApplication().getFrameworkSettings().getVersion()));

		// TODO Add your page's components here
		
		this.updateContactNames();
		
		
//		DropDownChoice<String> listSites = new DropDownChoice<String>(
//				"contactDropDown", new PropertyModel<String>(this, "selected"), contactNames);
//		add(listSites);
		
		
	
		Form<?> form = new ContactForm("form");
		add(form);
		
		
		//testing hard coded values
		this.name = "john doe";
		this.address = "123 Pleasant Lane";
		this.phone = "3928439238";
		this.email = "john@email.com";
		this.relationship = "valued customer";
		//they appear in the fields
		
		
		//form.add(new TextField<String>("input", new PropertyModel<String>(this, "inputValue")));

    }
	
	class ContactForm extends Form<Object> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 9099971868919563238L;

		@SuppressWarnings("unchecked")
		public ContactForm(String id) {
			super(id);
			
			//TODO: add an event listener on change to the contactDropDown, or if impossible add a button next to it to see what
			//the current value for it is and then act accordingly
			add(new DropDownChoice<String>("contactDropDown", new PropertyModel<String>(HomePage.this, "selected"), contactNames));
			
//			add(new TextField<String>("Text", new PropertyModel(HomePage.this, "")));
			add(new TextField<String>("nameText", new PropertyModel<String>(HomePage.this, "name")));
			add(new TextField<String>("addressText", new PropertyModel<String>(HomePage.this, "address")));
			add(new TextField<String>("phoneText", new PropertyModel<String>(HomePage.this, "phone")));
			add(new TextField<String>("emailText", new PropertyModel<String>(HomePage.this, "email")));
			add(new TextField<String>("relationshipText", new PropertyModel<String>(HomePage.this, "relationship")));
			
			//potentially useful code snippet found online about adding an on change event listener
//			myForm.add(new AjaxEventBehavior("onchange") {
//			    @Override
//			    protected void onEvent(AjaxRequestTarget target) {
//			         handleFormChange(...);
//			    }
//			});
			
			//TODO: add these buttons to the html with wicket:id links
//			add(new Button("save") {
//				/**
//				 * 
//				 */
//				private static final long serialVersionUID = 6387503322049573347L;
//
//				@Override
//				public void onSubmit() {
//					HomePage.this.saveContact();
//				}
//			});
//			
//			add(new Button("delete") {
//				/**
//				 * 
//				 */
//				private static final long serialVersionUID = -7938382276221616082L;
//
//				@Override
//				public void onSubmit() {
//					HomePage.this.deleteContact();
//				}
//			});
		
		
		
		}
		
		
		
	}
	
	public void updateContactNames() {
		contactNames = new ArrayList<String>();
		contactNames.add(selected);
		for(String s: WicketApplication.getContactKeys()) {
			contactNames.add(s);
		}
	}
	
	//TODO: implement saveContact and deleteContact, 
	
	
	
	
	//handles case where the save/update button is clicked
	public void saveContact() {
		
	}
	
	public void deleteContact() {
		
	}
	
	
}
