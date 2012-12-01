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
	private EditText leerlingnummerVeld;
	private EditText wachtwoordVeld;
	private Button opslaanKnop;
	
	private String leerlingnummer;
	private String wachtwoord;
	
	public void onCreate(Bundle savedInstanceState) {
    	//laat de layout uit de xml file zien
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voorkeur);
        leerlingnummerVeld = (EditText) findViewById(R.id.leerlingnummerVeld);
        wachtwoordVeld = (EditText) findViewById(R.id.wachtwoordVeld);
        opslaanKnop = (Button) findViewById(R.id.opslaanKnop);
    }
    
    //de procedure voor de opslaanknop
    public void opslaan(View view){
    	if (view == opslaanKnop){
    		//als de velden gevuld zijn ga dan door
    		if(leerlingnummerVeld != null && wachtwoordVeld != null){
	    		
    			//haal de usernames en wachtwoorden op
	    		leerlingnummer = leerlingnummerVeld.getText().toString();
	    		wachtwoord = wachtwoordVeld.getText().toString();

	    		//sla de voorkeuren op 
			 	SharedPreferences voorkeuren = getSharedPreferences("Voorkeuren",Context.MODE_PRIVATE);
		        
		        SharedPreferences.Editor editor = voorkeuren.edit();
		        editor.putBoolean("eersteStart", false);
		        editor.putBoolean("gewijzigd", true);
		        editor.putString("leerlingnummer", leerlingnummer);
		        editor.putString("wachtwoord", wachtwoord);
		        
		        editor.commit();
		        
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