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

package it.rainbowbreeze.smsforfree.data;

import it.rainbowbreeze.smsforfree.domain.ContactPhone;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.Contacts.People;

@SuppressWarnings("deprecation")
public class ContactDaoSdk3_4
	extends ContactDao
{
	//---------- Ctors

	
	
	
	//---------- Private fields

	
	
	
	//---------- Public properties


	
	
	//---------- Public methods
	@Override
	public Intent getPickContactIntent()
	{
		return new Intent(Intent.ACTION_PICK, People.CONTENT_URI);
	}


	/*
     * To query a content provider, you can use either the ContentResolver.query()
     * method or the Activity.managedQuery() method. Both methods take the same set
     * of arguments, and both return a Cursor object. However, managedQuery() causes
     * the activity to manage the life cycle of the Cursor. A managed Cursor handles
     * all of the niceties, such as unloading itself when the activity pauses, and
     * requerying itself when the activity restarts. You can ask an Activity to begin
     * managing an unmanaged Cursor object for you by calling
     * Activity.startManagingCursor(). 
     */
    

	@Override
	public List<ContactPhone> getContactNumbers(Activity callerActivity, Uri contactUri)
	{
		ArrayList<ContactPhone> phones = new ArrayList<ContactPhone>();
		
		String id = String.valueOf(ContentUris.parseId(contactUri));
		Cursor pCur = callerActivity.managedQuery(
				Contacts.Phones.CONTENT_URI,
				null,
				Contacts.Phones.PERSON_ID + "= ?",
				new String[]{id},
				null);
		
 		while (pCur.moveToNext()) { 
 		    // This would allow you get several phones number
 			String number = pCur.getString(pCur.getColumnIndex(Contacts.Phones.NUMBER));
 			String numberType = pCur.getString(pCur.getColumnIndex(Contacts.Phones.TYPE)); 
 			phones.add(new ContactPhone(numberType, number));
 		} 
 		pCur.close();
 		
	return phones;
	}

	
	

	//---------- Private methods

}
