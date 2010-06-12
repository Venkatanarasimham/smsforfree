/**
 * 
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.IExecuteProviderCommandActivity;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsServiceCommand;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import it.rainbowbreeze.smsforfree.logic.ExecuteProviderCommandAsyncTask;
import it.rainbowbreeze.smsforfree.util.GlobalUtils;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * @author rainbowbreeze
 *
 */
public class ActSubservicesList
	extends ListActivity
	implements IExecuteProviderCommandActivity
{
	//---------- Private fields
	private static final int OPTIONMENU_ADDSERVICE = 10;
	private static final int CONTEXTMENU_ADDSERVICE = 1;
	private static final int CONTEXTMENU_EDITSERVICE = 2;
	private static final int CONTEXTMENU_DELETESERVICE = 3;
	
	private SmsProvider mProvider;
    ArrayAdapter<SmsService> mListAdapter;
    TextView mLblNoSubservices;




	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actsubserviceslist);
		
		getDataFromIntent(getIntent());
		
		if (null == mProvider)
			return;
		
		//update title
        setTitle(String.format(
        		getString(R.string.actsubserviceslist_title),
        		SmsForFreeApplication.instance().getAppName(),
        		mProvider.getName()));

        mLblNoSubservices = (TextView) findViewById(R.id.actsubservicelist_lblNoSubservices);
        mListAdapter = new ArrayAdapter<SmsService>(this, 
	              android.R.layout.simple_list_item_1, mProvider.getAllSubservices());
		setListAdapter(mListAdapter);
		
		//register the context menu to defaul ListView of the view
		//alternative method:
		//http://www.anddev.org/creating_a_contextmenu_on_a_listview-t2438.html
		registerForContextMenu(getListView());
		
		showHideInfoLabel();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean canContinue = super.onCreateOptionsMenu(menu);
		if (!canContinue) return canContinue;
		
		//creates it's own menu
		menu.add(0, OPTIONMENU_ADDSERVICE, 0, R.string.actsubserviceslist_mnuAddService)
			.setIcon(android.R.drawable.ic_menu_add);

		//checks for provider's extended commands
		if (null != mProvider && mProvider.hasSubservicesListActivityCommands()) {
			for (SmsServiceCommand command : mProvider.getSubservicesListActivityCommands()) {
				MenuItem item = menu.add(0,
						command.getCommandId(), command.getCommandOrder(), command.getCommandDescription());
				if (command.hasIcon()) item.setIcon(command.getCommandIcon());
			}
		}
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.setHeaderTitle(R.string.actsubserviceslist_mnuHeaderTitle);
		menu.add(0, CONTEXTMENU_ADDSERVICE, 0, R.string.actsubserviceslist_mnuAddService)
			.setIcon(android.R.drawable.ic_menu_add);
		menu.add(0, CONTEXTMENU_EDITSERVICE, 1, R.string.actsubserviceslist_mnuEditService)
			.setIcon(android.R.drawable.ic_menu_edit);
		menu.add(0, CONTEXTMENU_DELETESERVICE, 2, R.string.actsubserviceslist_mnuDeleteService)
			.setIcon(android.R.drawable.ic_menu_delete);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//update the list and avoid the IllegalStateException when a new subservice is added
		if (null != mListAdapter) mListAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case OPTIONMENU_ADDSERVICE:
			addNewService();
			break;
		
		//execute one of the provider's command
		default:
			//preparing the background task for sending message
			ExecuteProviderCommandAsyncTask task = new ExecuteProviderCommandAsyncTask(
					this,
					this,
					getString(R.string.common_msg_executingCommand),
					mProvider,
					item.getItemId(),
					null);
			
			//and execute the command
			task.execute();
			//at the end of the execution, the executeCommandComplete() method will be called
		}
		
		return true;
	}


	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		SmsService service;
	    // This is actually where the magic happens.
	    // As we use an adapter view (which the ListView is)
	    // We can cast item.getMenuInfo() to AdapterContextMenuInfo 
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		
		switch (item.getItemId()) {
		case CONTEXTMENU_ADDSERVICE:
			addNewService();
			break;
		case CONTEXTMENU_EDITSERVICE:
			//find current selected service
			service = (SmsService) getListAdapter().getItem(menuInfo.position);
			//and edit it
			ActivityHelper.openSettingsSmsService(this, mProvider.getId(), service.getTemplateId(), service.getId());
			break;
		case CONTEXTMENU_DELETESERVICE:
			service = (SmsService) getListAdapter().getItem(menuInfo.position);
			mProvider.getAllSubservices().remove(service);
			mListAdapter.notifyDataSetChanged();
			ResultOperation res = mProvider.saveSubservices(this);
			if (null == res || res.HasErrors())
				ActivityHelper.reportError(this, res);
			showHideInfoLabel();
			break;
		}
		
		return true;
	}
		
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		SmsService service = mProvider.getAllSubservices().get(position);
		
		ActivityHelper.openSettingsSmsService(this, mProvider.getId(), service.getTemplateId(), service.getId());
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if (RESULT_OK != resultCode) {
			return;
		}
		
		switch (requestCode) {
		case ActivityHelper.REQUESTCODE_PICKTEMPLATE:
			//get templateId
			String templateId = data.getExtras().getString(ActivityHelper.INTENTKEY_SMSTEMPLATEID);
			//and launch the creation of new subservice
			ActivityHelper.openSettingsSmsService(this, mProvider.getId(), templateId, SmsService.NEWSERVICEID);
			break;
			
		case ActivityHelper.REQUESTCODE_SERVICESETTINGS:
			showHideInfoLabel();
			break;
		}
	}
	
	

	//---------- Public methods
	/**
	 * Called by AsyncTask when the command execution completed
	 * @param res
	 */
	public void executeCommandComplete(ResultOperation res) {
		ActivityHelper.showCommandExecutionResult(this.getBaseContext(), res);
	}
		

	
	
	//---------- Private methods

	private void getDataFromIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		//checks if intent 
		if(extras != null) {
			String id = extras.getString(ActivityHelper.INTENTKEY_SMSPROVIDERID);
			mProvider = GlobalUtils.findProviderInList(SmsForFreeApplication.instance().getProviderList(), id);
		} else {
			mProvider = null;
		}
	}
	
	
	/**
	 * Show or hide label with description
	 */
	private void showHideInfoLabel()
	{
		if (mProvider.getAllSubservices().size() == 0) {
			mLblNoSubservices.setVisibility(View.VISIBLE);
		} else {
			mLblNoSubservices.setVisibility(View.GONE);
		}
	}


	/**
	 * Add new service 
	 */
	private void addNewService() {
		if (!mProvider.hasTemplatesConfigured()) {
			ActivityHelper.showInfo(this, R.string.actsubserviceslist_msgNoTemplates);
		} else {
			//launch the activity for selecting subservice template
			ActivityHelper.openTemplatesList(this, mProvider.getId());
		}
	}
	

}
