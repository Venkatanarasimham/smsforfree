package it.rainbowbreeze.smsforfree.providers;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.common.GlobalBag;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.util.Base64;
import it.rainbowbreeze.smsforfree.util.ParserUtils;

/**
 * Old Jacksms provider - don't take into account!
 * @author rainbowbreeze
 *
 */
public class OldJacksmsService
{
	//---------- Ctors
	public OldJacksmsService()
	{
		mUrlDictionary = new OldJacksmsDictionary();
	}

	
	
	
	//---------- Private fields
	private final static String LOG_TAG = "JacksmsApiManager";
	private OldJacksmsDictionary mUrlDictionary;

	

	public String getServiceName() {
		return "old JackSMS";
	}
	
	public boolean hasConfigurations() {
		return false;
	}
	
	
	
	
	//---------- Public methods

    private static OldJacksmsService mInstance;
    public static OldJacksmsService instance()
    {
    	if (null == mInstance)
    		mInstance = new OldJacksmsService();
    	return mInstance;
    }
    
    
    
    public ResultOperation getJCode()
    {
    	ResultOperation res = new ResultOperation();
    	WebserviceClient client = new WebserviceClient();
    	String reply;
    	
    	try {
    		reply = client.RequestGET(mUrlDictionary.getSessionCodeUrl());
    		String jcode = ParserUtils.getStringBetween(reply, OldJacksmsDictionary.PARAM_JCODE, OldJacksmsDictionary.PARAMS_SEPARATOR);
    		res.setResultAsString(jcode); 
		} catch (ClientProtocolException e) {
			res.setException(e);
			e.printStackTrace();
		} catch (IOException e) {
			res.setException(e);
			e.printStackTrace();
		}
		return res;
    }
    
    
    public ResultOperation checkCredentials(String username, String password)
    {
//    	//args check
//    	try {
//    		checkCredentialsValidity(username, password);
//    	} catch (IllegalArgumentException e) {
//    		return new ResultOperation(e);
//		}
    	
    	ResultOperation res = new ResultOperation();
    	WebserviceClient client = new WebserviceClient();
    	HashMap<String, String> data = new HashMap<String, String>();
		String paramValue;
    	
    	data.put("u", Base64.encodeBytes(username.getBytes()));
    	data.put("p", Base64.encodeBytes(password.getBytes()));
    	String reply;
    	try {
    		reply = client.requestPost(mUrlDictionary.getCheckCredentialsUrl(), data);
		} catch (ClientProtocolException e) {
			// TODO
			e.printStackTrace();
			return new ResultOperation(e);
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
			return new ResultOperation(e);
		}
    	
//		if (TextUtils.isEmpty(reply))
//			return new ResultOperation(new Exception(ERROR_NO_REPLY_FROM_SITE));
		
		//login result
		paramValue = ParserUtils.getStringBetween(reply, OldJacksmsDictionary.PARAM_CREDENTIALS_CORRECT, OldJacksmsDictionary.PARAMS_SEPARATOR);
		
		if (OldJacksmsDictionary.CREDENTIALS_CORRECT.equalsIgnoreCase(paramValue))
		{
			//good credentials
			GlobalBag.jacksms_logged = true;
			res.setResultAsBoolean(new Boolean(true));
			//set also the session code and read the sms send by the user
			paramValue = ParserUtils.getStringBetween(reply, OldJacksmsDictionary.PARAM_JCODE, OldJacksmsDictionary.PARAMS_SEPARATOR);
			GlobalBag.jacksms_jcode = paramValue;
			paramValue = ParserUtils.getStringBetween(reply, OldJacksmsDictionary.PARAM_SENT, OldJacksmsDictionary.PARAMS_SEPARATOR);
			GlobalBag.jacksms_sentmessage = paramValue;
		} else {
			//wrong credentials
			GlobalBag.jacksms_logged = false;
			res.setResultAsBoolean(new Boolean(false));
			GlobalBag.jacksms_jcode = "";
			GlobalBag.jacksms_sentmessage = "";
		}

		return res;
    }
}