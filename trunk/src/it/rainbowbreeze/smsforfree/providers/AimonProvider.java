package it.rainbowbreeze.smsforfree.providers;

import java.io.IOException;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.data.WebserviceClient;
import it.rainbowbreeze.smsforfree.domain.SmsProviderMenuCommand;
import it.rainbowbreeze.smsforfree.domain.SmsServiceParameter;
import it.rainbowbreeze.smsforfree.domain.SmsSingleProvider;
import it.rainbowbreeze.smsforfree.ui.ActSettingsSmsService;
import it.rainbowbreeze.smsforfree.ui.ActivityHelper;
import it.rainbowbreeze.smsforfree.util.Base64;

public class AimonProvider
	extends SmsSingleProvider
{
	//---------- Ctors
	public AimonProvider(ProviderDao dao, Context context)
	{
		super(dao, PARAM_NUMBER);

		setParameterDesc(PARAM_INDEX_USERNAME, context.getString(R.string.aimon_username));
		setParameterDesc(PARAM_INDEX_PASSWORD, context.getString(R.string.aimon_password));
		setParameterFormat(PARAM_INDEX_PASSWORD, SmsServiceParameter.FORMAT_PASSWORD);
		setParameterDesc(PARAM_INDEX_SENDER, context.getString(R.string.aimon_sender));
		setParameterDesc(PARAM_INDEX_ID_API, context.getString(R.string.aimon_idapi));
		
		//initializes the command list
		mProviderSettingsMenuCommand = new ArrayList<SmsProviderMenuCommand>();
		SmsProviderMenuCommand command;
		command = new SmsProviderMenuCommand(
				COMMAND_CHECKCREDENTIALS, context.getString(R.string.aimon_commandCheckCredentials), 1000);
		mProviderSettingsMenuCommand.add(command);
		command = new SmsProviderMenuCommand(
				COMMAND_CHECKCREDITS, context.getString(R.string.aimon_commandCheckCredits), 1001); 
		mProviderSettingsMenuCommand.add(command);
		
		//save some messages
		mMessages = new String[3];
		mMessages[MSG_INDEX_INVALID_CREDENTIALS] = context.getString(R.string.aimon_msg_invalidCredentials);
		mMessages[MSG_INDEX_VALID_CREDENTIALS] = context.getString(R.string.aimon_msg_validCredentials);
		mMessages[MSG_INDEX_SERVER_ERROR] = context.getString(R.string.aimon_msg_serverError);
	}
	
	
	//---------- Private fields
	private final static int PARAM_NUMBER = 4;
	private final static int PARAM_INDEX_USERNAME = 0;
	private final static int PARAM_INDEX_PASSWORD = 1;
	private final static int PARAM_INDEX_SENDER = 2;
	private final static int PARAM_INDEX_ID_API = 3;

	private final static int MSG_INDEX_INVALID_CREDENTIALS = 0;
	private final static int MSG_INDEX_VALID_CREDENTIALS = 1;
	private final static int MSG_INDEX_SERVER_ERROR = 2;

	private final static int COMMAND_CHECKCREDENTIALS = 1000;
	private final static int COMMAND_CHECKCREDITS = 1001;
	
	private String[] mMessages;
	
	
	

	//---------- Public fields
	public final static String ID_API_FIXED_SENDER = "106";
	public final static String ID_API_FREE_SENDER_NO_REPORT = "59";
	public final static String ID_API_FREE_SENDER_REPORT = "84";
	
	
	
	
	//---------- Public properties
	@Override
	public String getId()
	{ return "Aimon"; }

	@Override
	public String getName()
	{ return "Aimon"; }

	@Override
	public int getParametersNumber()
	{ return PARAM_NUMBER; }

	@Override
	public int getMaxMessageLenght() {
		return 160;
	}
    
	@Override
	public boolean hasProviderSettingsActivityCommands() {
		return true;
	}

	private List<SmsProviderMenuCommand> mProviderSettingsMenuCommand;
	@Override
	public List<SmsProviderMenuCommand> getProviderSettingsActivityCommands() {
		return mProviderSettingsMenuCommand;
	}
	

	
	
    //---------- Public methods
    @Override
	public ResultOperation sendMessage(String serviceId, String destination, String body) {
		return sendSms(
				getParameterValue(PARAM_INDEX_USERNAME),
				getParameterValue(PARAM_INDEX_PASSWORD),
				getParameterValue(PARAM_INDEX_SENDER),
				destination,
				body,
				getParameterValue(PARAM_INDEX_ID_API));
	}

	@Override
    public ResultOperation executeCommand(int commandId, Bundle extraData)
	{
		ResultOperation res;
		String currentUsername = null;
		String currentPassword = null;

		//controls if some parameters must be retrived from extraData
		switch (commandId) {
		case COMMAND_CHECKCREDENTIALS:
		case COMMAND_CHECKCREDITS:
			currentUsername = extraData.getString(String.valueOf(PARAM_INDEX_USERNAME));
			currentPassword = extraData.getString(String.valueOf(PARAM_INDEX_PASSWORD));
		}
		
		//execute commands
		switch (commandId) {
		case COMMAND_CHECKCREDENTIALS:
			res = verifyCredentials(currentUsername, currentPassword);
			break;

		case COMMAND_CHECKCREDITS:
			res = verifyCredit(currentUsername, currentPassword);
			break;

		default:
			res = new ResultOperation("Nothing to execute");
		}

		return res;
	}

	
	//---------- Private methods

	@Override
	protected String getParametersFileName()
	{ return GlobalDef.aimonParametersFileName; }

	@Override
	protected String getTemplatesFileName()
	{ return null; }

	@Override
	protected String getSubservicesFileName()
	{ return null; }

	/**
	 * Sends an sms
	 */
	private ResultOperation sendSms(
    		String username,
    		String password,
    		String sender,
    		String destination,
    		String body,
    		String idApi)
    {
    	//args check
    	if (!checkCredentialsValidity(username, password))
    		return getExceptionForInvalidCredentials();
    	
    	String okSender;
    	String okDestination;
    	String okBody;
    	
    	//sender is a phone number and starts with international prefix
    	if (sender.substring(0, 1).equals("+")) {
    		//checks the length
    		if (sender.length() > AimonDictionary.MAX_SENDER_LENGTH_NUMERIC) {
    			okSender = sender.substring(0, AimonDictionary.MAX_SENDER_LENGTH_NUMERIC);
    		} else {
    			okSender = sender;
    		}
    	} else {
        	if (TextUtils.isDigitsOnly(sender)) {
        		//sender is a phone number, add international prefix
        		okSender = "+39" + sender;
        		//and check length
        		if (okSender.length() > AimonDictionary.MAX_SENDER_LENGTH_NUMERIC) {
        			okSender = okSender.substring(0, AimonDictionary.MAX_SENDER_LENGTH_NUMERIC);
        		}
        	} else {
        		//sender is a name, checks length and encodes it
            	if (sender.length() > AimonDictionary.MAX_SENDER_LENGTH_ALPHANUMERIC){
            		okSender = sender.substring(0, AimonDictionary.MAX_SENDER_LENGTH_ALPHANUMERIC);
            	} else {
            		okSender = sender;
            	}
        	}
    	}
    	okSender = Base64.encodeBytes(okSender.getBytes());

    	//check the destination
    	if (destination.substring(0, 1).equals("+")) {
    		okDestination = destination.substring(1);
    	} else {
    		okDestination = "39" + destination;
    	}
    	
    	//checks body length
    	if (body.length() > AimonDictionary.MAX_BODY_LENGTH) {
    		okBody = body.substring(0, AimonDictionary.MAX_BODY_LENGTH);
    	} else {
    		okBody = body;
    	}
    	//TODO : remove unsupported characters
    	//encode the body
		okBody = Base64.encodeBytes(okBody.getBytes());
    	
    	
    	HashMap<String, String> data = new HashMap<String, String>();
    	appendCredential(data, username, password);
    	data.put(AimonDictionary.PARAM_SENDER, okSender);
    	data.put(AimonDictionary.PARAM_DESTINATION, okDestination);
    	data.put(AimonDictionary.PARAM_BODY, okBody);
    	data.put(AimonDictionary.PARAM_ID_API, idApi);
    	
    	//sends the sms
    	ResultOperation res = doRequest(AimonDictionary.URL_SEND_SMS, data);
    	
    	//check results
    	if (res.HasErrors()) return res;
    	
		//exams the result
		if (res.getResultAsString().startsWith(AimonDictionary.RESULT_SENDSMS_OK))
			//ok
			return res;
		else {
			//some sort of error
			res.setException(new Exception(res.getResultAsString()));
		}
		return res;    	
    	
    }


	/**
	 * Verifies how much credits the user has
	 * @param username
	 * @param password
	 * @return
	 */
    private ResultOperation verifyCredit(String username, String password)
    {
    	//args check
    	if (!checkCredentialsValidity(username, password))
    		return getExceptionForInvalidCredentials();
    	
		HashMap<String, String> data = new HashMap<String, String>();
    	appendCredential(data, username, password);
    	
    	//call the api that gets the credit
    	ResultOperation res = doRequest(AimonDictionary.URL_GET_CREDIT, data);
    	if (res.HasErrors()) return res;

    	//analyzes reply in case of errors
    	String errorDesc = parseReplyForErrors(res.getResultAsString());
    	if (!TextUtils.isEmpty(errorDesc))
    		return new ResultOperation(new Exception(errorDesc));
    	
    	//at this point reply can only contains the remaining credits
		return res;
    }


    /**
     * Verifies if username and password are correct
     * @return an error if the user is not authenticated, otherwise the message to show
     */
	private ResultOperation verifyCredentials(String username, String password) {
		ResultOperation res;
		
		//if i can obtain number of credits from aimon, username e password are correct
		res = verifyCredit(getParameterValue(PARAM_INDEX_USERNAME),
				getParameterValue(PARAM_INDEX_PASSWORD));
		
		//routes to caller method some error
		if (res.HasErrors())
			return res;

		//at this point, in the reply there are the user credits, so user
		//is authenticated
		return new ResultOperation(mMessages[MSG_INDEX_VALID_CREDENTIALS]);
	}
	
	
    /**
     * Append username and password on headers map
     * @param data
     * @param username
     * @param password
     */
	private void appendCredential(HashMap<String, String> data, String username, String password)
    {
    	data.put(AimonDictionary.PARAM_USERNAME, username);
    	data.put(AimonDictionary.PARAM_PASSWORD, password);
    }
    

	/**
	 * Execute the http request
	 * @param url
	 * @param data
	 * @return
	 */
    private ResultOperation doRequest(String url, HashMap<String, String> data)
    {
    	String reply = "";
    	WebserviceClient client = new WebserviceClient();
    	
    	try {
    		reply = client.requestPost(url, data);
		} catch (ClientProtocolException e) {
			return new ResultOperation(e);
		} catch (IOException e) {
			return new ResultOperation(e);
		}
    	
    	//empty reply
    	if (TextUtils.isEmpty(reply)) {
			return new ResultOperation(new Exception(ERROR_NO_REPLY_FROM_SITE));
		}

    	//return the reply
    	return new ResultOperation(reply);
    }

    
	/**
	 * Parse the webservice reply searching for know errors code
	 * 
	 * @return empty string if it's all ok otherwise the ResultOperation object with
	 *   error code inside
	 */
	public String parseReplyForErrors(String reply)
	{
		String res = "";
		
		//access denied
		if (reply.startsWith(AimonDictionary.RESULT_ERROR_ACCESS_DENIED))
			res = mMessages[MSG_INDEX_INVALID_CREDENTIALS];

		//server error
		if (reply.startsWith(AimonDictionary.RESULT_ERROR_INTERNAL_SERVER_ERROR))
			res = mMessages[MSG_INDEX_SERVER_ERROR];

		//return null if no errors are detected
		return res;
	}
}