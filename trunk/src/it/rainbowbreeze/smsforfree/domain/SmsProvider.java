package it.rainbowbreeze.smsforfree.domain;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;


/**
 * Base provider for sms service
 * 
 *  Each service could have more "sub-services" (for example, the service act as a
 *  unified "proxy" for other services)
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public abstract class SmsProvider
	extends SmsService
{
	//---------- Ctors
	protected SmsProvider(ProviderDao dao, int numberOfParameters)
	{
		super(numberOfParameters);
		mDao = dao;
	}

	
	
	
	//---------- Private fields
	ProviderDao mDao;
	
	
	
	//---------- Public fields
	public static final String ERROR_NO_CREDENTIALS = "NO_CREDENTIALS";
	public static final String ERROR_CREDENTIALS_NOT_VALID = "CREDENTIALS_INVALID";
	public static final String ERROR_NO_REPLY_FROM_SITE = "NO_REPLY";
	
	public static final String CAPTCHAREQUEST = "captcharequest";
	

	
	//---------- Public properties
	
	/** Has this provider sub-services? */
	public abstract boolean hasSubServices();
	
	public abstract List<SmsService> getAllTemplate();
	public abstract SmsService getTemplate(String templateId);

	public abstract List<SmsService> getAllSubservices();
	public abstract SmsService getSubservice(String subserviceId);

    public abstract void setSelectedSubservice(String subserviceId);

	/**
	 * Provider has additional menu to show on option menu of
	 * ActSettingSmsService activity, when the settings in
	 * editing are related to the provider
	 */
    public abstract boolean hasProviderSettingsActivityCommands();
    
	/**
	 * Menu to show on option menu of ActSettingSmsService activity,
	 * when the settings in editing are related to the provider
	 */
    public abstract List<SmsProviderMenuCommand> getProviderSettingsActivityCommands();

	/** Provider has additional command to show on option menu of ActSubservicesList activity */
    public abstract boolean hasSubservicesListActivityCommands();
    
	/** Menu to show on option menu of ActSubservicesList activity */
    public abstract List<SmsProviderMenuCommand> getSubservicesListActivityCommandS();

    
    
    
	//---------- Public methods
	/**
	 * Send the message
	 * 
	 * @param destination
	 * @param body
	 */
	public abstract ResultOperation sendMessage(String serviceId, String destination, String body);
	
	
	public ResultOperation loadParameters(Context context){
		return mDao.loadProviderParameters(context, getParametersFileName(), this);
	}
	
	public ResultOperation saveParameters(Context context){
		return mDao.saveProviderParameters(context, getParametersFileName(), this);
	}

	public abstract ResultOperation saveTemplates(Context context);

	public abstract ResultOperation loadTemplates(Context context);
	
	public abstract ResultOperation saveSubservices(Context context);

	public abstract ResultOperation loadSubservices(Context context);
	
	public abstract SmsService newSubserviceFromTemplate(String templateId);

	public abstract boolean hasServiceParametersConfigured(String serviceId);

    /** Execute the menu command identified by its id
     * 
     * @param commandId
     * @param extraData
     * @return String with command result
     */
    public abstract ResultOperation executeCommand(int commandId, Bundle extraData);

    
    
    
	//---------- Private methods
	
	/** file name where save provider parameters */
	protected abstract String getParametersFileName();
	
	/** file name where save subservices templates */
	protected abstract String getTemplatesFileName();

	/** file name where save provider subservices */
	protected abstract String getSubservicesFileName();
	
	/**
	 * Checks if username and password are not empty
	 * Throws an error if one of the two are not correct
	 * 
	 * @param username
	 * @param password
	 */
	protected boolean checkCredentialsValidity(String username, String password)
	{
		return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password);
	}
	
	/**
	 * Create an exception for empty username or password
	 * @return
	 */
	protected ResultOperation getExceptionForInvalidCredentials()
	{
		return new ResultOperation(new Exception(ERROR_NO_CREDENTIALS));
	}  

}