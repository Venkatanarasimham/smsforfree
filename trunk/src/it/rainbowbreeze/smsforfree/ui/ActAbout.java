package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.GlobalDef;
import it.rainbowbreeze.smsforfree.common.SmsForFreeApplication;
import it.rainbowbreeze.smsforfree.logic.LogicManager;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ActAbout
	extends Activity
{
	//---------- Ctors

	//---------- Private fields

	//---------- Public properties

	//---------- Events
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.actabout);
        setTitle(R.string.actabout_title);
        
        TextView lblVersion = (TextView)findViewById(R.id.actabout_lblAppVersion);
        String version = GlobalDef.appVersionDescription;
        if (SmsForFreeApplication.instance().isLiteVersionApp()) version = version + " Lite";
        lblVersion.setText(version);
	}

	//---------- Public methods

	//---------- Private methods

}
