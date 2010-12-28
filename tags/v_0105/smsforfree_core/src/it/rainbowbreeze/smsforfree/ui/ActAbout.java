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
import it.rainbowbreeze.smsforfree.common.App;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ActAbout
	extends Activity
{
	//---------- Private fields
	private final static int OPTIONMENU_CHANGESLOG = 1;
	private final static int OPTIONMENU_EMAIL = 2;
	private final static int DIALOG_CHANGESLOG = 1;

	
	
	
	//---------- Public properties

	
	
	
	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.actabout);
        setTitle(String.format(
        		getString(R.string.actabout_title), App.instance().getAppName()));
        
        TextView lblVersion = (TextView)findViewById(R.id.actabout_lblAppVersion);
        String version = GlobalDef.appVersionDescription;
        if (App.instance().isLiteVersionApp()) version = version + " " + GlobalDef.lite_description;
        lblVersion.setText(version);

        TextView lblSentSms = (TextView)findViewById(R.id.actabout_lblSentSms);
        String sentSms = String.valueOf(LogicManager.getSmsSentToday());
        if (App.instance().isLiteVersionApp()) sentSms = sentSms + "/" + App.instance().getAllowedSmsForDay();
        lblSentSms.setText(String.format(
        		getString(R.string.actabout_lblSentSms), sentSms));
        
        TextView lblDescription2 = (TextView) findViewById(R.id.actabout_lblDescription2);
        lblDescription2.setText(String.format(getString(R.string.actabout_lblDescription2), GlobalDef.EMAIL_FOR_LOG));
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!super.onCreateOptionsMenu(menu)) return false;
		
    	menu.add(0, OPTIONMENU_CHANGESLOG, 4, R.string.actabout_mnuChangeslog)
			.setIcon(R.drawable.ic_menu_archive);
    	menu.add(0, OPTIONMENU_EMAIL, 5, R.string.actabout_mnuEmail)
			.setIcon(android.R.drawable.ic_menu_send);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case OPTIONMENU_CHANGESLOG:
			showDialog(DIALOG_CHANGESLOG);
			break;
		
		case OPTIONMENU_EMAIL:
			ActivityHelper.sendEmail(this,
					GlobalDef.EMAIL_FOR_LOG,
					String.format(getString(R.string.actabout_msgEmailSubject), App.instance().getAppName()),
					getString(R.string.actabout_msgEmailBody));
			break;
		}
		
		
		return true;
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id)
	{
		Dialog returnDialog;
		
		switch (id) {
		case DIALOG_CHANGESLOG:
			returnDialog = ActivityHelper.createInformativeDialog(this,
					R.string.actabout_msgChangeslogTitle, R.string.actabout_msgChangeslog, R.string.common_btnOk);
			break;

		default:
			returnDialog = super.onCreateDialog(id);
		}
		
		return returnDialog;
	}

	//---------- Public methods

	//---------- Private methods

}