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

package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.libs.common.RainbowResultOperation;
import it.rainbowbreeze.libs.ui.RainbowActivityHelper;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.App;
import it.rainbowbreeze.smsforfree.common.LogFacility;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ContactDao;
import it.rainbowbreeze.smsforfree.domain.TextMessage;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;


public class ActivityHelper
	extends RainbowActivityHelper {
	//---------- Private fields
	
	


	//---------- Public properties
	public final static int REQUESTCODE_NONE = 0;
	public final static int REQUESTCODE_PICKCONTACT = 3;
	public final static int REQUESTCODE_PICKTEMPLATE = 4;
	public final static int REQUESTCODE_COMPACTMESSAGE = 5;
	public final static int REQUESTCODE_SETTINGS = 6;
	public final static int REQUESTCODE_SERVICESETTINGS = 7;
	
	public final static String INTENTKEY_SMSSERVICEID = "SmsService";
	public final static String INTENTKEY_SMSPROVIDERID = "SmsProvider";
	public final static String INTENTKEY_SMSTEMPLATEID = "SmsTemplate";
	public final static String INTENTKEY_PICKTEMPLATE = "PickTemplate";
	public final static String INTENTKEY_MESSAGE = "Message";
	public final static String INTENTKEY_SENDLOGREPORT = "SendLogReport";
	public static final String INTENTKEY_MESSAGE_DESTIONATION = "MessageDestination";
	public static final String INTENTKEY_MESSAGE_TEXT = "MessageText";




	//---------- Public methods
	/**
	 * @param logFacility
	 * @param context
	 */
	public ActivityHelper(LogFacility logFacility, Context context) {
		super(logFacility, context);
	}
	
	
	

	//---------- Public methods

    public void openSendSms(Activity callerActivity, TextMessage message) {
        Bundle extras = null;
        if (null != message) {
            extras = new Bundle();
            extras.putString(INTENTKEY_MESSAGE_DESTIONATION, message.getDestination());
            extras.putString(INTENTKEY_MESSAGE_TEXT, message.getMessage());
        }
        openActivity(callerActivity, ActSendSms.class, extras, true, REQUESTCODE_MAINACTIVITY);
    }
    
	/**
	 * Open Sms settings activity for editing a subservice preferences
	 * 
	 * @param callerActivity caller activity
	 * @param providerId the id of the provider that own the subservice
	 * @param templateId the id of the template on which the service is
	 *                   based
	 * @param serviceId the id of the subservice to edit.
	 *                  SmsService.NEWSERVICEID when adding a new service
	 */
	public void openSettingsSmsService(Activity callerActivity, String providerId, String templateId, String serviceId)
	{
		mBaseLogFacility.i("Launching activity SettingSmsService for provider " + providerId + " template " + templateId + " service " + serviceId);
        Intent intent = new Intent(callerActivity, ActSettingsSmsService.class);
		intent.putExtra(INTENTKEY_SMSPROVIDERID, providerId);
		intent.putExtra(INTENTKEY_SMSTEMPLATEID, templateId);
		intent.putExtra(INTENTKEY_SMSSERVICEID, serviceId);
		openActivity(intent, callerActivity, true, REQUESTCODE_SERVICESETTINGS);
	}
	
	/**
	 * Open Sms settings activity for editing a provider preferences
	 * 
	 * @param callerActivity caller activity
	 * @param providerId the id of the provider to edit
	 */
	public void openSettingsSmsService(Activity callerActivity, String providerId)
	{
		//putting null into templateId and subserviceId, the ActSettingsSmsService knows what entity edit
		openSettingsSmsService(callerActivity, providerId, null, null);
	}
	
	/**
	 * Open provider list activity
	 */
	public void openProvidersList(Activity callerActivity)
	{
		openActivity(callerActivity, ActProvidersList.class, null, false, REQUESTCODE_NONE);
	}
	
	/**
	 * Open about activity
	 */
	public void openAbout(Activity callerActivity)
	{
		openActivity(callerActivity, ActAbout.class, null, false, REQUESTCODE_NONE);
	}
	
	/**
	 * Open message templates activity
	 */
	public void openMessageTemplates(Activity callerActivity)
	{
		openActivity(callerActivity, ActMessageTemplates.class, null, false, REQUESTCODE_NONE);
	}
	
	/**
	 * Open compact message activity
	 * @param callerActivity
	 * @param message the message to compact
	 */
	public void openCompactMessage(Activity callerActivity, String message)
	{
        Intent intent = new Intent(callerActivity, ActCompactMessage.class);
		intent.putExtra(INTENTKEY_MESSAGE, message);
		openActivity(intent, callerActivity, true, REQUESTCODE_COMPACTMESSAGE);
	}

	/**
	 * Open provider's templates list activity
	 */
	public void openTemplatesList(Activity callerActivity, String providerId)
	{
        Intent intent = new Intent(callerActivity, ActTemplatesList.class);
		intent.putExtra(INTENTKEY_SMSPROVIDERID, providerId);
		openActivity(intent, callerActivity, true, REQUESTCODE_PICKTEMPLATE);
	}
	/**
	 * Open provider subservice configuration activity
	 */
	public void openSubservicesList(Activity callerActivity, String providerId)
	{
        Intent intent = new Intent(callerActivity, ActSubservicesList.class);
		intent.putExtra(INTENTKEY_SMSPROVIDERID, providerId);
		openActivity(intent, callerActivity, false, REQUESTCODE_NONE);
	}
	
	/**
	 * 
	 * @param callerActivity
	 */
	public void openPickContact(Activity callerActivity)
	{
		Intent intent = ContactDao.instance().getPickContactIntent();
		openActivity(intent, callerActivity, true, REQUESTCODE_PICKCONTACT);
	}
	
    /**
     * Open Settings activity
     * 
     * @param callerActivity caller activity
     */
    public void openSettingsMain(Activity callerActivity)
    {
        openSettingsMain(
                ActSettingsMain.class,
                callerActivity,
                false,
                App.APP_DISPLAY_NAME,
                App.APP_INTERNAL_VERSION,
                App.EMAIL_FOR_LOG,
                App.LOG_TAG);
    }
    
	
	public void reportError(Context context, Exception exception, int returnCode)
	{
		//First of all, examines return code for standard errors
		String userMessage;
		switch (returnCode) {
		case ResultOperation.RETURNCODE_ERROR_APPLICATION_ARCHITECTURE:
			userMessage = String.format(
					context.getString(R.string.common_msg_architecturalError), exception.getMessage());
			break;
		case ResultOperation.RETURNCODE_ERROR_COMMUNICATION:
			userMessage = String.format(
					context.getString(R.string.common_msg_communicationError), exception.getMessage());
			break;
		case ResultOperation.RETURNCODE_ERROR_GENERIC:
			userMessage = String.format(
					context.getString(R.string.common_msg_genericError), exception.getMessage());
			break;
		case ResultOperation.RETURNCODE_ERROR_EMPTY_REPLY:
			userMessage = context.getString(R.string.common_msg_noReplyFromProvider);
			break;
		case ResultOperation.RETURNCODE_ERROR_LOAD_PROVIDER_DATA:
			userMessage = String.format(
					context.getString(R.string.common_msg_cannotLoadProviderData), exception.getMessage());
			break;
		case ResultOperation.RETURNCODE_ERROR_NOCREDENTIAL:
			userMessage = context.getString(R.string.common_msg_noCredentials);
			break;
		case ResultOperation.RETURNCODE_ERROR_SAVE_PROVIDER_DATA:
			userMessage = String.format(
					context.getString(R.string.common_msg_cannotSaveProviderData), exception.getMessage());
			break;
		default:
			userMessage = String.format(
					context.getString(R.string.common_msg_architecturalError), "No error result code managed.");
			break;
		}
		
		//display the error to the user
		reportError(context, userMessage);
		//and log the error
		if (ResultOperation.RETURNCODE_ERROR_NOCREDENTIAL != returnCode &&
				ResultOperation.RETURNCODE_ERROR_EMPTY_REPLY != returnCode) {
			mBaseLogFacility.e(exception);
		}
	}
	

	/**
	 * Process in a standard way the result of SmsService extended command
	 * execution
	 * 
	 * @param context
	 * @param result
	 */
	public void showCommandExecutionResult(Context context, RainbowResultOperation<String> result)
	{
		//show command results
		if (result.hasErrors()) {
			reportError(context, result);
		} else {
			if (!TextUtils.isEmpty(result.getResult())){
				//shows the output of the command
				mBaseLogFacility.i(result.getResult());
				showInfo(context, result.getResult());
			}
		}		
	}


	@Override
	public ProgressDialog createProgressDialog(
	        Context context, String title, String message) {
	    //set progress dialog as not cancelable
	    ProgressDialog progressDialog = super.createProgressDialog(context, title, message);
	    if (null != progressDialog)
	        progressDialog.setCancelable(false);
	    return progressDialog;
	}


	
	//---------- Private methods
	
}