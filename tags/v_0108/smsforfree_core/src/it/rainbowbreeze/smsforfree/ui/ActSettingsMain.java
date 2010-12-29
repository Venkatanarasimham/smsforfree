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

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.common.App;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import it.rainbowbreeze.smsforfree.data.SmsDao;
import it.rainbowbreeze.smsforfree.logic.PrepareLogToSendThread;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.TextUtils;

/**
 * Application main settings
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ActSettingsMain
	extends PreferenceActivity
{
	//---------- Private fields
	private CheckBoxPreference mChkResetData;
	private CheckBoxPreference mChkInsertSmsIntoPim;
	private EditTextPreference mTxtSignature;
	private EditTextPreference mTxtPrefix;
	private ProgressDialog mProgressDialog;
	
	private PrepareLogToSendThread mPrepareLogThread;
	
	private boolean mMustSendLog;
	
	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setTitle(String.format(
        		getString(R.string.actsettingsmain_title), App.instance().getAppName()));
		addPreferencesFromResource(R.layout.actsettingsmain);
		getDataFromIntent(getIntent());
		
		mChkResetData = (CheckBoxPreference) findPreference("actsettingsmain_chkResetDataAfterSend");
		mChkInsertSmsIntoPim = (CheckBoxPreference) findPreference("actsettingsmain_chkInsertSmsIntoPim");
		mTxtSignature = (EditTextPreference) findPreference("actsettingsmain_txtSignature");
		mTxtPrefix = (EditTextPreference) findPreference("actsettingsmain_txtDefaultInternationalPrefix");
		
		//Get the custom providers' preference and register listener for it
		Preference providerPref = findPreference("actsettingsmain_providersPref");
		providerPref.setOnPreferenceClickListener(providersPrefClickListener);
		//Get the custom providers' preference and register listener for it
		Preference templatesPref = findPreference("actsettingsmain_templatesPref");
		templatesPref.setOnPreferenceClickListener(templatesPrefClickListener);
		//send application log
		Preference sendLog = findPreference("actsettingsmain_sendLog");
		sendLog.setOnPreferenceClickListener(sendLogClickListener);
		
		//set value of other preferences
		mChkResetData.setChecked(AppPreferencesDao.instance().getAutoClearMessage());
		mChkInsertSmsIntoPim.setChecked(AppPreferencesDao.instance().getInsertMessageIntoPim());
		mTxtSignature.setText(AppPreferencesDao.instance().getSignature());
		mTxtPrefix.setText(AppPreferencesDao.instance().getDefaultInternationalPrefix());
		
		//register listeners
		mChkResetData.setOnPreferenceChangeListener(mChkResetDataChangeListener);
		mChkInsertSmsIntoPim.setOnPreferenceChangeListener(mChkInsertSmsIntoPimChangeListener);
		mTxtSignature.setOnPreferenceChangeListener(mTxtSignatureChangeListener);
		mTxtPrefix.setOnPreferenceChangeListener(mTxtPrefixChangeListener);
		
		//can send the log only when the activity is called for the first time
		if(null != savedInstanceState) mMustSendLog = false;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mPrepareLogThread = (PrepareLogToSendThread) getLastNonConfigurationInstance();
		if (null != mPrepareLogThread) {
			//create and show a new progress dialog
			mProgressDialog = ActivityHelper.createAndShowProgressDialog(this, R.string.common_msg_executingCommand);
			//register new handler
			mPrepareLogThread.registerCallerHandler(mPrepareLogHandler);
		}
		
		if (mMustSendLog) {
			//create the log email
			sendLogClickListener.onPreferenceClick(null);
			mMustSendLog = false;
		}
	}

	@Override
	protected void onStop() {
		if (null != mPrepareLogThread) {
			//unregister handler from background thread
			mPrepareLogThread.registerCallerHandler(null);
		}
		super.onStop();
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		//save eventually open background thread
		return mPrepareLogThread;
	}


	/**
	 * Called when providers preferences button is pressed
	 */
	private OnPreferenceClickListener providersPrefClickListener = new OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {
			//checks if only on provider is configured
			
			if (1 == App.instance().getProviderList().size()) {
				//open directly the setting for the only provider present
				ActivityHelper.openSettingsSmsService(ActSettingsMain.this, App.instance().getProviderList().get(0).getId());
			} else {
				//open providers list
				ActivityHelper.openProvidersList(ActSettingsMain.this);
			}
			return true;
		}
	};
	
	
	/**
	 * Called when template button is pressed
	 */
	private OnPreferenceClickListener templatesPrefClickListener = new OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {
			ActivityHelper.openMessageTemplates(ActSettingsMain.this);
			return true;
		}
	};
	
	
	/**
	 * Called when send log button is pressed
	 */
	private OnPreferenceClickListener sendLogClickListener = new OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {
			Activity activity = ActSettingsMain.this;
			//create new progress dialog
			mProgressDialog = ActivityHelper.createAndShowProgressDialog(activity, R.string.common_msg_executingCommand);

			//preparing the background thread for executing service command
			mPrepareLogThread = new PrepareLogToSendThread(
					activity.getApplicationContext(),
					mPrepareLogHandler);
			//and execute the command
			mPrepareLogThread.start();
			return true;
		}
	};
	
	
	private OnPreferenceChangeListener mTxtSignatureChangeListener = new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			AppPreferencesDao.instance().setSignature(newValue.toString());
			return AppPreferencesDao.instance().save();
		}
	};
	
	
	private OnPreferenceChangeListener mTxtPrefixChangeListener = new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			AppPreferencesDao.instance().setDefaultInternationalPrefix(newValue.toString());
			return AppPreferencesDao.instance().save();
		}
	};
	
	
	private OnPreferenceChangeListener mChkResetDataChangeListener = new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			AppPreferencesDao.instance().setAutoClearMessage(((Boolean)newValue).booleanValue());
			return AppPreferencesDao.instance().save();
		}
	};
	
	private OnPreferenceChangeListener mChkInsertSmsIntoPimChangeListener = new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (!SmsDao.instance().isSentSmsProviderAvailable(preference.getContext())) {
				ActivityHelper.showInfo(preference.getContext(), R.string.actsettingsmain_msgNoSmsProviderAvailable);
				return false;
			} else {
				AppPreferencesDao.instance().setInsertMessageIntoPim(((Boolean)newValue).booleanValue());
				return AppPreferencesDao.instance().save();
			}
		}
	};

	/**
	 * Hander to call when the execute command menu option ended
	 */
	private Handler mPrepareLogHandler = new Handler() {
		public void handleMessage(Message msg)
		{
			//check if the message is for this handler
			if (msg.what != PrepareLogToSendThread.WHAT_PREPARELOGTOSEND)
				return;
			
			//dismisses progress dialog
			if (null != mProgressDialog && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
			ResultOperation<String> res = mPrepareLogThread.getResult();
			if (res.hasErrors()) {
				//some errors
				ActivityHelper.reportError(ActSettingsMain.this, res);
			} else {
				//send email with log
				ActivityHelper.sendEmail(
						ActSettingsMain.this,
						GlobalDef.EMAIL_FOR_LOG,
						String.format(getString(R.string.common_sendlogSubject), App.instance().getAppName() + " " + GlobalDef.appVersionDescription),
						String.format(getString(R.string.common_sendlogBody), res.getResult()));
			}
			//free the thread
			mPrepareLogThread = null;
		};
	};


	
	
	//---------- Public methods

	
	
	
	//---------- Private methods
	/**
	 * Get data from intent and configured internal fields
	 * @param intent
	 */
	private void getDataFromIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		//check if a direct call to send log preference is needed
		if(extras != null && !TextUtils.isEmpty(extras.getString(ActivityHelper.INTENTKEY_SENDLOGREPORT))) {
			mMustSendLog = true;
		} else {
			mMustSendLog = false;
		}
	}
}