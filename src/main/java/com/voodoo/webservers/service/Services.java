package com.voodoo.webservers.service;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author chamerling
 * 
 */
@WebService
public interface Services {

	@WebMethod
	String validate_user(String user_name,String password) ;

	@WebMethod
	String register_user( String User_Name,String Password, String Hospital_name,String Type_of_User);

	}
