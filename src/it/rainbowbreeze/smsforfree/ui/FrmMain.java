package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.providers.AimonDictionary;
import it.rainbowbreeze.smsforfree.providers.AimonService;
import it.rainbowbreeze.smsforfree.providers.JacksmsService;
import it.rainbowbreeze.smsforfree.providers.OldJacksmsService;
import android.app.Activity;
import android.os.Bundle;

public class FrmMain extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        JacksmsService.instance().loadTemplateServices();
        JacksmsService.instance().loadUserService();
        JacksmsService.instance().loadCredentials();
        JacksmsService.instance().sendSms(62, "+393927686894", "Ciao da me che sono il re!");
        
    }
}