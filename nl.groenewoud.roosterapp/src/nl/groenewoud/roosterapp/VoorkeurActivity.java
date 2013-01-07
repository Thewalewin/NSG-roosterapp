package nl.groenewoud.roosterapp;

/*
 * In deze activity worden instellingen weergegeven en kunnen ze aangepast worden
 */


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class VoorkeurActivity extends Activity {
	// Variabelen om de layout items in op te slaan om ze zo te gebruiken of aan te passen
	private EditText leerlingnummerVeld;
	private EditText wachtwoordVeld;
	private Button opslaanKnop;
	
	//Lokale instantie van de leerlingnummer en wachtwoord variabele
	private String leerlingnummer;
	private String wachtwoord;
	
	//Referentie naar de globale variabelen
	private GlobalVars gv;
	
	//Dit stukje wordt uitgevoerd als de VoorkeurActivity opgestart wordt
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voorkeur);
        //Stel de referentie naar de globale variavelen in
        gv = (GlobalVars) getApplicationContext();
        
        // Vul de variabelen met de layout gegevens
        leerlingnummerVeld = (EditText) findViewById(R.id.leerlingnummerVeld);
        wachtwoordVeld = (EditText) findViewById(R.id.wachtwoordVeld);
        opslaanKnop = (Button) findViewById(R.id.opslaanKnop);
    }
    
    // De procedure voor de opslaanknop
    public void opslaan(View view){
    	if (view == opslaanKnop){
    		// Als de velden gevuld zijn ga dan door
    		if(leerlingnummerVeld != null && wachtwoordVeld != null){
	    		
    			// Haal de usernames en wachtwoorden op
	    		leerlingnummer = leerlingnummerVeld.getText().toString();
	    		wachtwoord = wachtwoordVeld.getText().toString();

	    		// Sla de voorkeuren op 
			 	SharedPreferences voorkeuren = getSharedPreferences("Voorkeuren",Context.MODE_PRIVATE);
		        
		        SharedPreferences.Editor editor = voorkeuren.edit();
		        editor.putBoolean("eersteStart", false);
		        editor.putString("leerlingnummer", leerlingnummer);
		        editor.putString("wachtwoord", wachtwoord);
		        
		        editor.commit();
		        
		        // Stel de globale variabelen gelijk aan de zojuist ingevulde gegevens
		        gv.setLeerlingnummer(leerlingnummer);
		        gv.setWachtwoord(wachtwoord);
		        
		        //laat een berichtje weergeven dat alles opgeslagen is en sluit het voorkeurscherm af
	    		Toast.makeText(this, "Opgeslagen", Toast.LENGTH_LONG).show();
	    		this.finish();
    		}
    		else{
    			//een van de velden is niet correct ingevuld
    			Toast.makeText(this, "Vul gebruiker gegevens in", Toast.LENGTH_LONG).show();
    		}
    	}
    }    
}