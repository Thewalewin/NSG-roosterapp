package nl.groenewoud.roosterapp;

/*
 * In deze activity worden de tabbladen aangemaakt en worden ze aan de layout toegevoegd.
 */

//Referenties naar voor gedefinieerde java code die nodig is voor de werking van de app
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
	
	// Maak een variabele van GlobalVars aan als referentie naar de variabelen
	private GlobalVars gv;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Vul de variabele daadwerkelijk met de referentie
        gv = (GlobalVars) getApplicationContext();
        
        // Hier wordt de tabhost aangemaakt en uit de layout gehaald om er tabbladden in te stoppen
        TabHost tabHost = getTabHost();
        
        // Maak een Rooster tabblad met bijbehorende informatie, zoals tab naam, icoontje en activity
        TabSpec rooster = tabHost.newTabSpec("Rooster");
        rooster.setIndicator("Rooster", getResources().getDrawable(R.drawable.icoon_rooster_tab));
        Intent roosterIntent = new Intent(this, RoosterActivity.class);
        rooster.setContent(roosterIntent);
 
        // Aanmaken Anderen tab
        TabSpec anderen = tabHost.newTabSpec("Anderen");
        anderen.setIndicator("Anderen", getResources().getDrawable(R.drawable.icoon_anderen_tab));
        Intent anderenIntent = new Intent(this, AnderenActivity.class);
        anderen.setContent(anderenIntent);
         
        // Tabbladen aan de layout toevoegen
        tabHost.addTab(rooster);
        tabHost.addTab(anderen);
        
        /*
         *  Een lokale opslag voor voorkeuren openen
         *  Als dit de eerste start is roep dan het voorkeuren scherm op
         */
        
        SharedPreferences voorkeuren = getSharedPreferences("Voorkeuren", Context.MODE_PRIVATE);
        boolean eersteStart = voorkeuren.getBoolean("eersteStart", true);
        
        if(eersteStart){
        	//Laad het voorkeur scherm
        	startActivity(new Intent(this, VoorkeurActivity.class));
        	return;
        }
        else{
        	//Dit is niet de eerste start dus haal het wachtwoord en leerlingnummer uit de voorkeuren op en zet deze in de GlobalVars d.m.v de setters
        	gv.setLeerlingnummer(voorkeuren.getString("leerlingnummer", ""));
        	gv.setWachtwoord(voorkeuren.getString("wachtwoord", ""));
        }
    }
    
    // In het volgende stukje code wordt er een menu aangemaakt als iemand op de menu knop drukt
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    // Geef de layout van het menu weer
	    inflater.inflate(R.menu.mainmenu, menu);
	    return true;
	}
	
    // Hier wordt gezegd wat te doen als iemand in het menu een keuze maakt
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        // Als iemand op de optiesMenuknop drukt dan moet de VoorkeurActivity gestart worden
	    	case R.id.optiesMenuknop:     startActivity(new Intent(this, VoorkeurActivity.class));
	                            break;
	    }
	    return true;
	}
    
}