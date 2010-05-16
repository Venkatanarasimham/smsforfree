/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import java.util.Collections;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalBag;
import it.rainbowbreeze.smsforfree.common.GlobalUtils;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ActSettingsSmsService
	extends ActBaseDataEntry
{
	//---------- Private fields
	private SmsService mEditedService;
	private SmsService mTemplateService;
	private SmsProvider mProvider;
	private boolean mIsEditingAProvider;
	private boolean mIsNewService;
	private Button mBtnConfigureSubservices;
	private TextView mLblServiceName;
	private TextView mTxtServiceName;
	private TextView mLblServiceInfo;
	private TextView mTxtServiceInfo;

	private final static int MAXFIELDS = 10;
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.actsettingssmsservice);
        getDataFromIntent(getIntent());
        
        if (null == mProvider) return;
        
        mBtnConfigureSubservices = (Button) findViewById(R.id.actsettingssmsservice_btnConfigsubservices);
        mBtnConfigureSubservices.setOnClickListener(mBtnConfigureSubservicesClickListener);
        mLblServiceName = (TextView) findViewById(R.id.actsettingssmsservice_lblServiceName);
        mTxtServiceName = (EditText) findViewById(R.id.actsettingssmsservice_txtServiceName);
        mLblServiceInfo = (TextView) findViewById(R.id.actsettingssmsservice_lblServiceInfo);
        mTxtServiceInfo = (EditText) findViewById(R.id.actsettingssmsservice_txtServiceInfo);
        
		//display or not the button
		if (mIsEditingAProvider) {
			mLblServiceName.setVisibility(View.GONE);
			mTxtServiceName.setVisibility(View.GONE);
			mLblServiceInfo.setVisibility(View.GONE);
			mTxtServiceInfo.setVisibility(View.GONE);
			if (mProvider.hasSubServices()) {
				mBtnConfigureSubservices.setVisibility(View.VISIBLE);
			} else {
				mBtnConfigureSubservices.setVisibility(View.GONE);
			}
		} else {
			mLblServiceName.setVisibility(View.VISIBLE);
			mTxtServiceName.setVisibility(View.VISIBLE);
			mLblServiceInfo.setVisibility(View.GONE);
			mTxtServiceInfo.setVisibility(View.GONE);
			mBtnConfigureSubservices.setVisibility(View.GONE);
		}
	}
	
	private OnClickListener mBtnConfigureSubservicesClickListener = new OnClickListener() {
		public void onClick(View v) {
			//open the subservice configuration activity
			ActivityHelper.openSubservicesList(ActSettingsSmsService.this, mProvider.getId());
		}
	};




	//---------- Public methods

	
	
	
	//---------- Private methods
	@Override
	protected void backupData() {
		//TODO
		//no way to backup data now
	}

	@Override
	protected void restoreData() {
		//TODO
		//no way to restore data now
	}

	@Override
	protected void loadData() {
		//update title
        this.setTitle(String.format(
        		getString(R.string.actsettingssmsservice_titleEdit),
        		mTemplateService.getName()));

        //set the name, if the object edited is a subservice
		if (!mIsEditingAProvider)
			mTxtServiceName.setText(mEditedService.getName());
		
        //update texts visibility and values
		for (int i = 0; i < MAXFIELDS; i++){
        	TextView lblDesc = null;
        	EditText txtValue = null;
        	
        	//get description and value views
        	switch (i) {
			case 0:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter00);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter00);
				break;
			case 1:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter01);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter01);
				break;
			case 2:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter02);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter02);
				break;
			case 3:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter03);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter03);
				break;
			case 4:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter04);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter04);
				break;
			case 5:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter05);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter05);
				break;
			case 6:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter06);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter06);
				break;
			case 7:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter07);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter07);
				break;
			case 8:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter08);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter08);
				break;
			case 9:
				lblDesc = (TextView) findViewById(R.id.actsettingssmsservice_lblParameter09);
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter09);
				break;
			}
        	
        	//set the content of the view
        	if (i < mTemplateService.getParametersNumber()) {
        		lblDesc.setText(mTemplateService.getParameterDesc(i));
        		if (!mIsNewService) {
        			txtValue.setText(mEditedService.getParameterValue(i));
        		}
        	} else {
        		lblDesc.setVisibility(View.GONE);
        		txtValue.setVisibility(View.GONE);
        	}
        }
		
	}

	@Override
	protected void saveData() {
		//read object name, if object edited is a subservice
		if (!mIsEditingAProvider)
			((SmsConfigurableService)mEditedService).setName(mTxtServiceName.getText().toString());
			
		for (int i = 0; i < MAXFIELDS; i++){
        	EditText txtValue = null;
        	
        	//get value views
        	switch (i) {
			case 0:
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter00);
				break;
			case 1:
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter01);
				break;
			case 2:
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter02);
				break;
			case 3:
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter03);
				break;
			case 4:
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter04);
				break;
			case 5:
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter05);
				break;
			case 6:
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter06);
				break;
			case 7:
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter07);
				break;
			case 8:
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter08);
				break;
			case 9:
				txtValue = (EditText) findViewById(R.id.actsettingssmsservice_txtParameter09);
				break;
			}
        	
        	//save the data inside the object
        	if (i < mTemplateService.getParametersNumber()) {
        		mEditedService.setParameterValue(i, txtValue.getText().toString());
        	}
		}
		
		//persist the parameters
		ResultOperation res;
		if (mIsEditingAProvider) {
			//update provider data
			res = mProvider.saveParameters(this);
		} else if (mIsNewService) {
			mProvider.getAllSubservices().add(mEditedService);
			//save new subservice
			res = mProvider.saveSubservices(this);
		} else {
			//update subservice
			res = mProvider.saveSubservices(this);
		}
		if (null == res || res.HasErrors())
			ActivityHelper.reportError(this, res);
	}

	private void getDataFromIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		//checks if current editing is for a provider or a subservice
		if(extras != null) {
			String providerId = extras.getString(ActivityHelper.INTENTKEY_SMSPROVIDERID);
			mProvider = GlobalUtils.findProviderInList(GlobalBag.providerList, providerId);
			String subserviceId = extras.getString(ActivityHelper.INTENTKEY_SMSSERVICEID);
			if (TextUtils.isEmpty(subserviceId)) {
				//edit a provider preferences
				mIsEditingAProvider = true;
				mIsNewService = false;
				//template and service to edit are always the same provider
				mTemplateService = mProvider;
				mEditedService = mProvider;
			} else if (SmsService.NEWSERVICEID.equals(subserviceId)) {
				mIsEditingAProvider = false;
				mIsNewService = true;
				String templateId = extras.getString(ActivityHelper.INTENTKEY_SMSTEMPLATEID);
				mTemplateService = mProvider.getTemplate(templateId);
				//create new service
				mEditedService = mProvider.newSubserviceFromTemplate(templateId);
			} else {
				//edit a subservice preferences
				mIsEditingAProvider = false;
				mIsNewService = false;
				String templateId = extras.getString(ActivityHelper.INTENTKEY_SMSTEMPLATEID);
				mTemplateService = mProvider.getTemplate(templateId);
				mEditedService = mProvider.getSubservice(subserviceId);
			}

		} else {
			mEditedService = null;
		}
	}

}
