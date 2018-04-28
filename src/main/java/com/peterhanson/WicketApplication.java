package com.peterhanson;

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
		
		
		
		Jedis jedis = new Jedis("localhost");
		//jedis.set("foo", "bar");
		String value = jedis.get("foo");
		
		
		System.out.println(" ");
		System.out.println(" ");
		System.out.println(value);
		System.out.println(" ");
		System.out.println(" ");

		// add your configuration here
	}
}
