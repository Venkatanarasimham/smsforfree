/**
 * Copyright (C) 2010 Alfredo Morresi
 * 
 * This file is part of SmsForFree project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */

package it.rainbowbreeze.smsforfree.common;

import android.content.Context;
import android.util.Log;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.common.App;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.logic.LogicManager;


/**
 * Utility class for tests
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class TestUtils {
	//---------- Private fields
	
	

	
	//---------- Public methods
	
	/**
	 * Backup the values of service's parameters
	 * 
	 * @param service service to backup
	 * @return an array with the backup of the parameters
	 */
	public static SmsServiceParameter[] backupServiceParameters(SmsService service)
	{
		if (null == service) return null;
		
		SmsServiceParameter[] params;
		
		int numberOfParams = service.getParametersNumber();
		params = new SmsServiceParameter[numberOfParams];
		
		for (int i = 0; i < numberOfParams; i++) {
			SmsServiceParameter param = new SmsServiceParameter();
			param.setDesc(service.getParameterDesc(i));
			param.setValue(service.getParameterValue(i));
			param.setFormat(service.getParameterFormat(i));
			params[i] = param;
		}
			
		return params;
	}

	/**
	 * Restore the value of the parameters in a service
	 * @param service
	 * @param params
	 */
	public static void restoreServiceParameters(SmsService service, SmsServiceParameter[] params)
	{
		if (null == service || null == params) return;

		
		for (int i = 0; i < params.length; i++) {
			SmsServiceParameter param = params[i];
			service.setParameterDesc(i, param.getDesc());
			service.setParameterValue(i, param.getValue());
			service.setParameterFormat(i, param.getFormat());
		}
	}
	
	
	/**
	 * Initialize the context
	 * 
	 * Similar to what happens in the {@link SmsForFreeApplication#onCreate()}
	 * 
	 * @param context
	 * @return
	 */
	public static boolean beginTask(Context context)
	{
		boolean result;

		//initializes the application (needs for global variables)
		App app = new App();
		
		//executes begin task
		ResultOperation<Void> res = LogicManager.executeBeginTask(context);
		if (res.hasErrors()) {
			Log.e("SmsForFreeTest", String.valueOf(res.getReturnCode()));
			result = false;
		} else {
			result = true;
		}
		
		return result;
	}

	
	/**
	 * Mock same values of the SmsForFreeApplication object
	 */
	public static void loadAppPreferences(Context context)
	{
		//load configurations
		AppPreferencesDao.instance().load(context);
	}
	
	
	//---------- Private methods

}
