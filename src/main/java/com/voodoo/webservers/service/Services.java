package com.voodoo.webservers.service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.QueryParam;

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


	@WebMethod
	String add_new_patient( String hospital_name, String ambulance_id , String p_name, String gender, String blood_grp,String condition,String problem,String police_case,String is_enabled);

	@WebMethod
	String update_patient( String hospital_name, String ambulance_id , String p_name, String p_id, String gender, String blood_grp,String condition,String problem,String police_case,String is_enabled);

	@WebMethod
	String get_patient_details( String hospital_name, String ambulance_id , String p_id) ;

	@WebMethod
	String update_heartrate(String hospital_name, String ambulance_id ,String p_id, String heartrate) ;

	@WebMethod
	String get_heartrate(String hospital_name, String ambulance_id , String p_id) ;

	@WebMethod
	String Testing() ;

	}
