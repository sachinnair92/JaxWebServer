package com.voodoo.webservers.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
/**
 * @author chamerling
 * 
 */
@WebService
public interface Services {

	@WebMethod(operationName="validate_user")
	String validate_user(@WebParam(name = "username") String user_name,@WebParam(name = "password") String password) ;

	@WebMethod
	String register_user(@WebParam(name = "username")String User_Name,@WebParam(name = "password")String Password, @WebParam(name = "hospital_name")String Hospital_name,@WebParam(name = "type_of_user")String Type_of_User);


	@WebMethod
	String add_new_patient(@WebParam(name = "hospital_name") String hospital_name,@WebParam(name = "ambulance_id") String ambulance_id ,@WebParam(name = "p_name") String p_name,@WebParam(name = "gender") String gender,@WebParam(name = "blood_grp") String blood_grp,@WebParam(name = "condition")String condition,@WebParam(name = "problem")String problem,@WebParam(name = "police_case")String police_case,@WebParam(name = "is_enabled")String is_enabled);

	@WebMethod
	String update_patient(@WebParam(name = "hospital_name") String hospital_name,@WebParam(name = "ambulance_id") String ambulance_id ,@WebParam(name = "p_name") String p_name,@WebParam(name = "p_id") String p_id,@WebParam(name = "gender") String gender,@WebParam(name = "blood_grp") String blood_grp,@WebParam(name = "condition")String condition,@WebParam(name = "problem")String problem,@WebParam(name = "police_case")String police_case,@WebParam(name = "is_enabled")String is_enabled);

	@WebMethod
	String get_patient_details( @WebParam(name = "hospital_name") String hospital_name,@WebParam(name = "ambulance_id") String ambulance_id ,@WebParam(name = "p_id") String p_id) ;

	@WebMethod
	String update_heartrate(@WebParam(name = "hospital_name") String hospital_name,@WebParam(name = "ambulance_id") String ambulance_id ,@WebParam(name = "p_id") String p_id,@WebParam(name = "heartrate") String heartrate) ;

	@WebMethod
	String get_heartrate(@WebParam(name = "hospital_name") String hospital_name,@WebParam(name = "ambulance_id") String ambulance_id ,@WebParam(name = "p_id") String p_id) ;

	@WebMethod
	String get_Patient_List(@WebParam(name = "hospital_name") String hospital_name) ;


	}
