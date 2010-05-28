/**
 * 
 */
package it.rainbowbreeze.smsforfree.logic;

import java.util.ArrayList;
import java.util.Collections;

import android.content.ContextWrapper;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalBag;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.ProviderDao;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.providers.AimonProvider;
import it.rainbowbreeze.smsforfree.providers.JacksmsProvider;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class LogicManager {
	//---------- Ctors

	//---------- Private fields

	//---------- Public properties

	//---------- Public methods
	public static ResultOperation executeBeginTask(ContextWrapper context)
	{
		ResultOperation res = new ResultOperation();
		
		String usernameDesc = context.getString(R.string.common_username);
		String passwordDesc = context.getString(R.string.common_password);
		String senderDesc = context.getString(R.string.common_sender);
		String aimonIdApiDesc = context.getString(R.string.common_aimon_idapi);
		
		//load configurations
		AppPreferencesDao.instance().load(context);
		
		//check for application upgrade
		checkForUpgrade(context);
		
		//initialize provider list
		ProviderDao dao = new ProviderDao();
		GlobalBag.providerList = new ArrayList<SmsProvider>();
		JacksmsProvider jackProv = new JacksmsProvider(dao, usernameDesc, passwordDesc);
		//TODO check errors
		jackProv.loadParameters(context);
		jackProv.loadTemplates(context);
		jackProv.loadSubservices(context);
		GlobalBag.providerList.add(jackProv);
		AimonProvider aimonProv = new AimonProvider(dao, usernameDesc, passwordDesc, senderDesc, aimonIdApiDesc);
		aimonProv.loadParameters(context);
		GlobalBag.providerList.add(aimonProv);
		
		Collections.sort(GlobalBag.providerList);

		return res;
	}
	
	
	public static ResultOperation executeEndTast()
	{
		ResultOperation res = new ResultOperation();

		return res;
	}

	
	
	
	//---------- Private methods

	private static void checkForUpgrade(ContextWrapper context)
	{
		String currentAppVersion = AppPreferencesDao.instance().getAppVersion();
		
		if (!GlobalDef.appVersion.equals(currentAppVersion)) {
			//perform upgrade
		}
	}


}