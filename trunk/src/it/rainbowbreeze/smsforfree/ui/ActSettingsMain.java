/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalBag;
import it.rainbowbreeze.smsforfree.data.AppPreferencesDao;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

/**
 * @author rainbowbreeze
 *
 */
public class ActSettingsMain
	extends PreferenceActivity
{
	//---------- Private fields
	private CheckBoxPreference mChkResetData;
//	private CheckBoxPreference mChkInserIntoPim;
	private EditTextPreference mTxtSignature;

	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.layout.actsettingsmain);
		
		mChkResetData = (CheckBoxPreference) findPreference("actsettingsmain_chkResetDataAfterSend");
		mTxtSignature = (EditTextPreference) findPreference("actsettingsmain_txtSignature");
//		mChkInserIntoPim = (CheckBoxPreference) findPreference("actsettingsmain_chkInsertSmsIntoPim");
		
		//Get the custom preference
		Preference customPref = (Preference) findPreference("actsettingsmain_providersPref");
		customPref.setOnPreferenceClickListener(providersPrefsClickListener);
		
		//set preferences value
		mChkResetData.setChecked(AppPreferencesDao.instance().getAutoClearMessage());
		mTxtSignature.setText(AppPreferencesDao.instance().getSignature());
		
		mChkResetData.setOnPreferenceChangeListener(mChkResetDataChangeListener);
		mTxtSignature.setOnPreferenceChangeListener(mTxtSignatureChangeListener);
	}


	/**
	 * Called when providers preferences button is pressed
	 */
	private OnPreferenceClickListener providersPrefsClickListener = new OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {
			//checks if only on provider is configured
			
			if (1 == GlobalBag.providerList.size()) {
				//open directly the setting for the only provider present
				ActivityHelper.openSettingsSmsService(ActSettingsMain.this, GlobalBag.providerList.get(0).getId());
			} else {
				//open providers list
				ActivityHelper.openProvidersList(ActSettingsMain.this);
			}
			return true;
		}
	};
	
	
	private OnPreferenceChangeListener mTxtSignatureChangeListener = new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			AppPreferencesDao.instance().setSignature(newValue.toString());
			return AppPreferencesDao.instance().save();
		}
	};
	
	
	private OnPreferenceChangeListener mChkResetDataChangeListener = new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			AppPreferencesDao.instance().setAutoClearMessage(((Boolean)newValue).booleanValue());
			return AppPreferencesDao.instance().save();
		}
	};
	
	
	//---------- Public methods

	
	
	
	//---------- Private methods

//	@Override
//	protected boolean saveDataFromViews() {
//		AppPreferencesDao.instance().setAutoClearMessage(mChkResetData.isChecked());
////		AppPreferencesDao.instance().setInsertMessageIntoPim(mChkInserIntoPim.isChecked());
//		AppPreferencesDao.instance().setSignature(mTxtSignature.getText());
//		return AppPreferencesDao.instance().save();
//	}
}