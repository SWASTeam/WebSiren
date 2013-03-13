/*
 * This file is part of WebSiren.
 *
 *  WebSiren is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  WebSiren is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with WebSiren.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.swas.explorer.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

/**
 * The FormFieldValidator class is used for validating fields of UI forms.
 */
public class FormFieldValidator {
	
	
	/**
	 * This function is for validating rule fields
	 * @param type
	 * @param group
	 * @param title
	 * @param phase
	 * @param action
	 * @return list of error messages
	 */
	public static List<String> validateRule(String type, String group, String title, String phase, String action){
		
		List<String> msg = new ArrayList<String>();
		
		if(type == null || type.equals("")){
			msg.add("selecting rule type is required");
		}
		if(group == null || group.equals("")){
			msg.add("selecting rule group is required");
		}
		if(title == null || title.equals("")){
			msg.add("rule title field is required");
		}
		if(action == null || action.equals("")){
			msg.add("selecting disruptive action is required");
		}
		if(action == null || action.equals("")){
			msg.add("selecting disruptive action is required");
		}
		if(phase == null || phase.equals("") || !isNumeric(phase)){
			msg.add("selecting phase is required");
		}
		return msg;
		
	}

	/**
	 * This function is for checking whether the string is numeric or not.
	 * @param str number in string form.
	 * @return true if its numeric otherwise false.
	 */
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    int num=Integer.parseInt(str);  
	  }  
	  catch(NumberFormatException | NullPointerException e)  
	  {  
		e.printStackTrace();
	    return false;  
	  }  
	  return true;  
	}
	
	/**
	 * normalises the list of transformations.
	 * @param transVals a string array of transformations.
	 * @return list of normalized transformations.
	 */
	public static List<String> normaliseTransformtions( String[] transVals ){
		
		ArrayList<String> transList = new ArrayList<String>();
		
		for(String tranVal : transVals){
			
			if(!tranVal.trim().equals("null") && !tranVal.trim().equals("")){
				transList.add(tranVal);
			}
			
		}
		
		return transList;
		
	}
	
	/**
	 * Checks whether user is loggedin or not.
	 * @param session
	 * @return true if user loggedin otherwise false.
	 */
	public static boolean isLogin(HttpSession session){
		
		if (session.getAttribute("userName") == null){
			return false;
		}  else{
			return true;
		}
		
	}
	

}
