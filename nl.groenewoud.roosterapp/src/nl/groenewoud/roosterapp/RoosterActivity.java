package nl.groenewoud.roosterapp;

/*
 * In deze activity wordt het rooster gedownload en weergegeven
 */

import java.util.ArrayList;

import nl.groenewoud.roosterapp.GlobalVars.lesuur;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
 
public class RoosterActivity extends Activity {
	
	// Declareer de variabelen en arrays om de tabel te maken
	private TableLayout roosterTabel;
	private TableRow[] rij;
	private TextView[] uurCel;
	private TextView[] maandagCel;
	private TextView[] dinsdagCel;
	private TextView[] woensdagCel;
	private TextView[] donderdagCel;
	private TextView[] vrijdagCel;
	private TextView uurKolom;
	
		
	// Variabelen om de layout items in op te slaan om ze zo te gebruiken of aan te passen
	private Button updateKnop;
	private ImageButton vorigeweekKnop;
	private ImageButton volgendeweekKnop;
	private TextView gebruikerVeld;
	private TextView updatetijdVeld;
	private TextView weekVeld;
	
	// Variabelen voor de tabel, fout code en gegevens over de download
	private String tabelCode = "";
	private String errorRegel = "";
	private String gebruikersnaam = "";
	private String updatetijd = "";
	
	// Referentie naar de globale variabelen
	private GlobalVars gv; 
	
	// Een lokale versie van het rooster
	private ArrayList<lesuur> rooster;
	private String weeknummer = "";
	
	// Variabelen om het antwoord van de service in op te slaan en om een dialoog weer te geven tijdens het downloaden
	private ProgressDialog progressDialog;
	private ResponseReceiver receiver;

	// Dit stuk wordt aangeroepen als de activity start
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooster);
        
        // Vul de variabelen met de layout gegevens
        roosterTabel = (TableLayout) findViewById(R.id.roosterTabel);
        updateKnop = (Button) findViewById(R.id.updateKnop);
        vorigeweekKnop = (ImageButton) findViewById(R.id.vorigeweekKnop);
        volgendeweekKnop = (ImageButton) findViewById(R.id.volgendeweekKnop);
        gebruikerVeld = (TextView) findViewById(R.id.gebruikerVeld);
        updatetijdVeld = (TextView) findViewById(R.id.updatetijdVeld);
        weekVeld = (TextView) findViewById(R.id.weeknummerVeld);
        uurKolom = (TextView) findViewById(R.id.uurKolom);
        
        // Maak rijen een cellen voor de tabel 
        rij = new TableRow[9];
        
        uurCel = new TextView[9];
        maandagCel = new TextView[9];
        dinsdagCel = new TextView[9];
        woensdagCel = new TextView[9];
        donderdagCel = new TextView[9];
        vrijdagCel = new TextView[9];
        
        // Maak een tabel naar het normale rooster model, een andere optie is een proefwerkweek tabel
        tabelLayout(true);
        // Maak de referentie naar de globale variabelen
        gv = (GlobalVars) getApplicationContext();
        // Vul het rooster met het rooster uit de globale variabelen
        rooster = gv.getRooster();
        
        // Als de app al gestart is geweest hebben de leerlingnummer en wachtwoord variabele al een waarde, zo niet geef ze die waarde dan 
        if(gv.getLeerlingnummer() == "" || gv.getWachtwoord() == ""){
        	// Haal de waardes op uit de voorkeur opslag op de telefoon en zet die in de globale variabele
            SharedPreferences voorkeuren = getSharedPreferences("Voorkeuren", Context.MODE_PRIVATE); 
    	    gv.setLeerlingnummer(voorkeuren.getString("leerlingnummer", ""));
    	    gv.setWachtwoord(voorkeuren.getString("wachtwoord", ""));
        }
        
        // Als de app al gestart is geweest is er al een rooster bekend vul de tabel met het rooster
        if(gv.getRooster().size() > 0){
        	weergeven();
        }
        
        // Er is nog geen rooster geupdate doet dit dus meteen
        else {
        	roosterUpdate();
        }
	}
	
	// Voeg aan het menu uit de MainActivity de optie om te updaten toe
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    // Voeg de updateMenuKnop toe aan het bestaande menu
	    menu.add(0, R.id.updateMenuKnop, 0, "Update");
	    return true;
	}
	
	// Zeg wat er moet gebeuren als de gebruiker een optie aanklikt
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	// In het geval van de update knop moet het rooster geupdate worden
	        case R.id.updateMenuKnop:     roosterUpdate();
	                            break;
	        //Bij de opties knop moet de VoorkeurActivity gestart worden
	        case R.id.optiesMenuknop:     startActivity(new Intent(this, VoorkeurActivity.class));
	                            break;
	    }
	    return true;
	}
	
	//Als er boven de tabel op de roosterUpdate knop gedrukt is moet het rooster geupdate worden
	public void roosterUpdateKnop(View view){
		if (view == updateKnop){
			roosterUpdate();
		}
	}
	
	//Hier wil ik het rooster van de vorige week weergeven als er op de vorigeweekKnop gedrukt is
	public void vorigeweekKnop(View view){
		if (view == vorigeweekKnop){

		}
	}
	
	//Hier wil ik het rooster van de volgende week weergeven als er op de volgendeweekKnop gedrukt is
	public void volgendeweekKnop(View view){
		if (view == volgendeweekKnop){

		}
	}
	
	// Als de DownloadService een reactie terug stuurt dat hij klaar is moet er nog van alles gedaan worden  
	public class ResponseReceiver extends BroadcastReceiver {
		public static final String ACTION_RESP ="com.mamlambo.intent.action.MESSAGE_PROCESSED";
		
		//Als het antwoord binnen is gekomen worden de volgende dingen gedaan
		@Override
		public void onReceive(Context context, Intent intent) {
			/*
			 * Haal de tabelCode eventuele errorregels en gegevens over de update zoals 
			 * de naam van de gebruiker en de tijd van de update op en plaats deze in de
			 * variabelen
			 */
			tabelCode = intent.getStringExtra("tabelCode");
			errorRegel = intent.getStringExtra("errorRegel");
			gebruikersnaam = intent.getStringExtra("gebruikersnaam");
			updatetijd = intent.getStringExtra("updatetijd");
			weeknummer = intent.getStringExtra("weeknummer");

			// Als de download succesvol was ga dan sorteren
			if(tabelCode.length() > 0){
				String pwrooster = "<div class=\"reeelraster\"";
				//Gaat het om een proefwerkrooster of niet?
				if(tabelCode.indexOf(pwrooster) == -1){
					//Het is een normale week
					sorteren(true);
				}
				
				else {
					//proefwerkweek
					sorteren(false);
				}
				// Geef de gebruikersnaam updatetijd en gevonden week weer
				gebruikerVeld.setText("Dit is het rooster van " + gebruikersnaam);
				updatetijdVeld.setText(updatetijd);
				weekVeld.setText("Week : " + weeknummer);
			}
			// De download is niet gelukt geef de errorRegel weer
			else {
				Toast.makeText(RoosterActivity.this, errorRegel, Toast.LENGTH_SHORT).show();
			}
			// De gebruiker mag de app weer besturen haal het update dialoog weg
			progressDialog.dismiss();
		}
	}
	
	// Omdat er verschillende tabbelen (normaal en proefwerkweek ) zijn moeten ze op hun eigen manier gebouwd worden
	private void tabelLayout( boolean normaleweek){
		if(normaleweek){
			// Het gaat om een normale week, voeg alle 45 cellen toe aan de tabel en geef ze hun standaardwaarde
	        for(int i=0; i<9; i++){
	           
	        	Resources r = getResources();
	        	// Parameters van de voorgedefinierde layout
	        	TableRow.LayoutParams textviewParameters = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        	
	        	// De cellen moeten 1 pixel naar links verplaatst worden om een dunnere lijn weer tegeven
	        	int margindp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -1, r.getDisplayMetrics());
	            
	        	rij[i] = new TableRow(this);

	        	// Vul de array met tekstvelden
	        	uurCel[i] = new TextView(this);
	        	// Geef de uurcellen (helemala links) hun uren 0de t/m 8ste
	        	uurCel[i].setText(String.valueOf(i));
	        	// Zet de hoogte op 55 pixels
	           	uurCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, r.getDisplayMetrics()));
	           	// Centreer de informatie
	           	uurCel[i].setGravity(Gravity.CENTER_HORIZONTAL);
	           	// Stel de achtergrond (een vierkant met oranje rand) in
	           	uurCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
	           	// Het laatste item (helemala links onder) heeft een afwijkende achtergrond, zodat de links onder hoek afgerond is
	        	if(i==8){
	        		uurCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelhoek_links_onder));
	        	}
	        	// Voeg de cel toe aan de rij met de parameters die in de layout gedefinieerd stonden
	        	rij[i].addView(uurCel[i], uurKolom.getLayoutParams());
	        	
	        	// Idem voor de maandag t/m vrijdag cellen
	        	maandagCel[i] = new TextView(this);
	        	maandagCel[i].setText("");
	        	maandagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, r.getDisplayMetrics()));
	        	maandagCel[i].setGravity(Gravity.CENTER_HORIZONTAL);
	        	maandagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
	        	rij[i].addView(maandagCel[i]);
	        	
	        	dinsdagCel[i] = new TextView(this);
	
	        	dinsdagCel[i].setText("");
	        	dinsdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, r.getDisplayMetrics()));
	        	dinsdagCel[i].setGravity(Gravity.CENTER_HORIZONTAL);
	        	dinsdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
	        	rij[i].addView(dinsdagCel[i]);
	        	
	        	woensdagCel[i] = new TextView(this);
	        	woensdagCel[i].setText("");
	        	woensdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, r.getDisplayMetrics()));
	        	woensdagCel[i].setGravity(Gravity.CENTER_HORIZONTAL);
	        	woensdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
	        	rij[i].addView(woensdagCel[i]);
	        	        	
	        	donderdagCel[i] = new TextView(this);
	        	donderdagCel[i].setText("");
	        	donderdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, r.getDisplayMetrics()));
	        	donderdagCel[i].setGravity(Gravity.CENTER_HORIZONTAL);
	        	donderdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
	        	rij[i].addView(donderdagCel[i]);
	        	
	        	vrijdagCel[i] = new TextView(this);
	        	vrijdagCel[i].setText("");
	        	vrijdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, r.getDisplayMetrics()));
	        	vrijdagCel[i].setGravity(Gravity.CENTER_HORIZONTAL);
	        	vrijdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
	        	// De rechts onder cel heeft weer een afwijkende achtergrond zodat de rechts onderhoek afgerond is
	        	if(i==8){
	        		vrijdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelhoek_rechts_onder));
	        	}
	        	// De rechterkolom moet 1 pixel naar rechts verschoven worden anders heb je rechts een dikkere lijn
	        	textviewParameters.setMargins(0, 0, margindp, 0);
	        	rij[i].addView(vrijdagCel[i], textviewParameters);
	        	
	        	roosterTabel.addView(rij[i]);
	        }
		}
		else {
			// Hier moet nog een opbouw van de tabel voor het proefwerkrooster komen
		}
	}
	
	// Deze methode kijkt of de telefoon verbinding met het internet heeft. Ja dan krijg je true Nee dan krijg je false terug
	private boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}
	
	// Methode voor het aanvragen van een roosterupdate
	private void roosterUpdate() {
		// Als de telefoon online is mag er een aanvraag gedaan worden
		if(isOnline()){
			// Haal de pagina op
			progressDialog = new ProgressDialog(RoosterActivity.this);
			progressDialog.setMessage("Bezig met het ophalen van gegevens…");
			progressDialog.show();
			
			// Start de Download service
			Intent downloadIntent = new Intent(this, DownloadService.class);
			startService(downloadIntent);
			
			// Wacht op antwoord
			IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
			filter.addCategory(Intent.CATEGORY_DEFAULT);
			receiver = new ResponseReceiver();
			registerReceiver(receiver, filter);
		}
		
		else{
			// Laat een error zien: geen verbinding
			Toast.makeText(RoosterActivity.this, "Je telefoon is niet verbonden met het internet.", Toast.LENGTH_SHORT).show();
		}
	}
	
	// Dit is het sorteer mechanisme
	private void sorteren (boolean normaleweek){
		if(normaleweek){
			//Het gaat om een normale schoolweek
			
			//Variabelen met de positie van de verschillende uurvullingen die voor kunnen komen
			int lesPositie;
			int vrijPositie;
			int uitvalPositie;
			
			String lesScheiding = "<span class=\"nobr\">";
			String vrijScheiding = "<td class=\"vrij\">";
			String uitvalScheiding = "<td class=\"vervallen\">";
			
			for (int uur=0; uur<9; uur++){
				// Voor 9 rijen
				for (int dag=0; dag<5; dag++){
					// En 5 kolomen
					
					//Zoek de positie op van iedere uurvulling
					lesPositie = tabelCode.indexOf(lesScheiding);
					vrijPositie = tabelCode.indexOf(vrijScheiding);
					uitvalPositie = tabelCode.indexOf(uitvalScheiding);
					
					if((lesPositie < vrijPositie || vrijPositie == -1) && (lesPositie < uitvalPositie || uitvalPositie == -1) && lesPositie != -1){
						
						// Het eerst volgende item is een les haal de informatie uit de cel
						String les;
						les = tabelCode.substring(lesPositie + 19, tabelCode.indexOf("</span>"));
						// Ontdoe hem van allerlei html codes
						les = les.replaceAll("<br />", "\n");
						
						// Voeg de les toe aan de rooster lijst
						rooster.add(gv.new lesuur(dag, les));
						
						//Haal de les uit de tabelcode zodat de les niet nog een keer eruit gehaald wordt
						tabelCode = tabelCode.substring(tabelCode.indexOf("</table> </td>") + 14);
						
					}
					
					else if ((vrijPositie < lesPositie || lesPositie == -1) && (vrijPositie < uitvalPositie || uitvalPositie == -1) && vrijPositie != -1){
						
						// Het eerst voorkomende item is een tussenuur en voeg dit toe aan de lijst
						rooster.add(gv.new lesuur(dag, "vrij")); 
						// Haal ook dit uur uit de broncode
						tabelCode = tabelCode.substring(tabelCode.indexOf("</td>") + 5);
					
					}
					
					else {
						
						// Het volgende uur valt uit, idem voeg het uur toe aan de lijst en haal het uit de broncode
						rooster.add(gv.new lesuur(dag, "uitval"));
						tabelCode = tabelCode.substring(tabelCode.indexOf("</td>") + 5);
						
					}
					
				}
				
			}
			// Vul de tabel met de gegevens
			weergeven();
		}
		
		else{
			//We hebben te maken met een proefwerkweek of een uurgebonden rooster i.p.v een lesuur gebonden rooster
			tabelCode = tabelCode.substring(tabelCode.indexOf("</td>") + 5);
			
			// In de proefwerkweek kunnen we les of toets hebben ook is het belangrijk om te weten waar het einde van de kolom (dag) is
			String toetsScheiding = "<div class=\"toets\">";
			String lesScheiding = "<div class=\"les\">";
			String dagScheiding = "</td>";
			
			int toetsPositie = tabelCode.indexOf(toetsScheiding);
			int lesPositie = tabelCode.indexOf(lesScheiding);
			int dagPositie = tabelCode.indexOf(dagScheiding);
			
			for (int i=0; i<5; i++){
				//Zolang er nog een dag is ga dan door met zoeken
				while ( (toetsPositie < dagPositie && toetsPositie != -1) || (lesPositie < dagPositie && lesPositie != -1)){
					//Er staat nog een toets of les in deze dag
					if(toetsPositie < dagPositie && toetsPositie != -1){
						//Er is een toets haal de informatie eruit en plaats deze in de lijst
						String toetseinde = "</div>";
						int toetseindePositie = tabelCode.indexOf(toetseinde, toetsPositie);
						
						String toets = tabelCode.substring(toetsPositie + 19, toetseindePositie);
						toets = toets.replaceAll("<br />", "\n");
						
						rooster.add(gv.new lesuur(i, toets));
						tabelCode = tabelCode.substring(toetseindePositie + 6);
					}
					
					else{
						//Er is een les haal de informatie eruit en plaats hem in de lijst
						String leseinde = "</div>";
						int leseindePositie = tabelCode.indexOf(leseinde, lesPositie);
						
						String les = tabelCode.substring(lesPositie + 19, leseindePositie);
						les = les.replaceAll("<br />", "\n");
						
						rooster.add(gv.new lesuur(i, les));
						tabelCode = tabelCode.substring(leseindePositie + 6);
					}
					
					//bereken opnieuw de posities in de broncode
					toetsPositie = tabelCode.indexOf(toetsScheiding);
					lesPositie = tabelCode.indexOf(lesScheiding);
					dagPositie = tabelCode.indexOf(dagScheiding);
					
				}
			}
			
		}
		// Stel de globale variabele van het rooster gelijk aan het gevonden rooster
		gv.setRooster(rooster);
	}
	
	private void weergeven(){
		//We hebben een normaal rooster en willen dit weergeven
		for (int i=0; i<9; i++){
			// Zolang er nog een rij ( 0de t/m 8ste uur) is moeten we de tabel vullen met de lesinformatie
			maandagCel[i].setText(rooster.get(0 + i*5).lesinformatie);
			dinsdagCel[i].setText(rooster.get(1 + i*5).lesinformatie);
			woensdagCel[i].setText(rooster.get(2 + i*5).lesinformatie);
			donderdagCel[i].setText(rooster.get(3 + i*5).lesinformatie);
			vrijdagCel[i].setText(rooster.get(4 + i*5).lesinformatie);
		}
	}
	
}