package nl.groenewoud.roosterapp;

/*
 * In deze activity worden de tabbladen aangemaakt en worden ze aan de layout toegevoegd.
 */

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TabHost tabHost = getTabHost();
        
        // Declaratie Rooster tabblad
        TabSpec rooster = tabHost.newTabSpec("Rooster");
        rooster.setIndicator("Rooster", getResources().getDrawable(R.drawable.icoon_rooster_tab));
        Intent roosterIntent = new Intent(this, RoosterActivity.class);
        rooster.setContent(roosterIntent);
 
        // Declaratie Anderen tab
        TabSpec anderen = tabHost.newTabSpec("Anderen");
        anderen.setIndicator("Anderen", getResources().getDrawable(R.drawable.icoon_anderen_tab));
        Intent anderenIntent = new Intent(this, AnderenActivity.class);
        anderen.setContent(anderenIntent);
        
        // Declaratie Agenda tab
        TabSpec agenda = tabHost.newTabSpec("Agenda");
        agenda.setIndicator("Agenda", getResources().getDrawable(R.drawable.icoon_agenda_tab));
        Intent agendaIntent = new Intent(this, AgendaActivity.class);
        agenda.setContent(agendaIntent);
 
         
        // Tabbladen aan de layout toevoegen
        tabHost.addTab(rooster);
        tabHost.addTab(anderen);
        tabHost.addTab(agenda);
        
        /*
         *  Een lokale opslag voor voorkeuren aanmaken
         *  Als dit de eerste start is roep dan het voorkeuren scherm op
         */
        
        SharedPreferences voorkeuren = getSharedPreferences("Voorkeuren", Context.MODE_PRIVATE);
        boolean eersteStart = voorkeuren.getBoolean("eersteStart", true);
        
        if(eersteStart == true){
        	//Laad het voorkeur scherm
        	startActivity(new Intent(this, VoorkeurActivity.class));
        	return;
        }
        
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mainmenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.optiesMenuknop:     startActivity(new Intent(this, VoorkeurActivity.class));
	                            break;
	    }
	    return true;
	}
    
}