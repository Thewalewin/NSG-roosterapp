package nl.groenewoud.roosterapp;

/*
 * In deze activity wordt het rooster gedownload en weergegeven
 */

import java.util.ArrayList;
import java.util.Calendar;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
 
public class RoosterActivity extends Activity {
	
	// Declareer een paar variabelen om de tabel te maken
	private TableLayout roosterTabel;
	private TableRow[] rij;
	private TextView[] uurCel;
	private TextView[] maandagCel;
	private TextView[] dinsdagCel;
	private TextView[] woensdagCel;
	private TextView[] donderdagCel;
	private TextView[] vrijdagCel;
	
		
	// Variabelen aanmaken
	private String[][] rooster = new String[5][9];
	private ArrayList<String> proefwerkrooster = new ArrayList<String>(0);
	
	private Button updateKnop;
	private TextView gebruikerVeld;
	private TextView updatetijdVeld;
	
	private String tabelCode = "";
	private String errorRegel = "";
	private String gebruikersnaam = "";
	private String updatetijd = "";
	
	private String leerlingnummer;
	private String wachtwoord;
	private String weeknummer;
	
	private ProgressDialog progressDialog;
	private ResponseReceiver receiver;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooster);
        
        roosterTabel = (TableLayout) findViewById(R.id.roosterTabel);
        updateKnop = (Button) findViewById(R.id.updateKnop);
        gebruikerVeld = (TextView) findViewById(R.id.gebruikerVeld);
        updatetijdVeld = (TextView) findViewById(R.id.updatetijdVeld);
        
        SharedPreferences voorkeuren = getSharedPreferences("Voorkeuren", Context.MODE_PRIVATE);
        leerlingnummer = voorkeuren.getString("leerlingnummer", "");
        wachtwoord = voorkeuren.getString("wachtwoord", "");
        
        // Maak rijen een cellen voor de tabel 
        rij = new TableRow[9];
        
        uurCel = new TextView[9];
        maandagCel = new TextView[9];
        dinsdagCel = new TextView[9];
        woensdagCel = new TextView[9];
        donderdagCel = new TextView[9];
        vrijdagCel = new TextView[9];
        
        tabelLayout();
        
        //Array voor Rooster maken
        for(int dag=0; dag<5; dag++){
        	for(int uur=0; uur<9; uur++){
        		rooster[dag][uur] = new String();
        	}
        }
        
        roosterUpdate();
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.updateMenuknop:     roosterUpdate();
	                            break;
	        case R.id.optiesMenuknop:     startActivity(new Intent(this, VoorkeurActivity.class));
	                            break;
	    }
	    return true;
	}

	private void roosterUpdate() {
		if(isOnline()){
			// Haal de pagina op
			progressDialog = new ProgressDialog(RoosterActivity.this);
			progressDialog.setMessage("Bezig met het ophalen van gegevens…");
			progressDialog.show();
			
	        Calendar calendar = Calendar.getInstance();
	        
	        if((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && calendar.get(Calendar.HOUR_OF_DAY) >= 17) || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
	        	weeknummer = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR) +1);
	        }
	        
	        else{
	        	weeknummer = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
	        }
			
			Intent downloadIntent = new Intent(this, DownloadService.class);
			downloadIntent.putExtra("user", leerlingnummer);
			downloadIntent.putExtra("paswoord", wachtwoord);
			downloadIntent.putExtra("weeknummer", weeknummer);
			startService(downloadIntent);
			
			IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
			filter.addCategory(Intent.CATEGORY_DEFAULT);
			receiver = new ResponseReceiver();
			registerReceiver(receiver, filter);
		}
		
		else{
			// Laat een error zien: geen verbinding
			Toast.makeText(RoosterActivity.this, "Je telefoon is niet verbonden met het internet.", Toast.LENGTH_LONG).show();
		}
	}
	
	public void roosterUpdateKnop(View view){
		if (view == updateKnop){
			roosterUpdate();
		}
	}
	
	private boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}
	
	private void tabelLayout(){
		
		// Geef de cellen eigenschappen en voeg ze toe aan de tabel
        for(int i=0; i<9; i++){
           
        	Resources r = getResources();
        	TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        	
        	int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -1, r.getDisplayMetrics());
        	int paddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics());
            layoutParams.setMargins(0, marginPx, marginPx, 0); // left, top, right, bottom
            
        	rij[i] = new TableRow(this);
        	        	
        	uurCel[i] = new TextView(this);
        	uurCel[i].setText(String.valueOf(i));
           	uurCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
           	uurCel[i].setPadding(paddingPx, 0, 0, 0);
        	uurCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
        	if(i==8){
        		uurCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelhoek_links_onder));
        	}
        	rij[i].addView(uurCel[i], layoutParams);
        	
        	maandagCel[i] = new TextView(this);
        	maandagCel[i].setText("Ma");
        	maandagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        	maandagCel[i].setPadding(paddingPx, 0, 0, 0);
        	maandagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
        	rij[i].addView(maandagCel[i], layoutParams);
        	
        	dinsdagCel[i] = new TextView(this);

        	dinsdagCel[i].setText("Di");
        	dinsdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        	
        	dinsdagCel[i].setPadding(paddingPx, 0, 0, 0);
        	dinsdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
        	rij[i].addView(dinsdagCel[i], layoutParams);
        	
        	woensdagCel[i] = new TextView(this);
        	woensdagCel[i].setText("Wo");
        	woensdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        	woensdagCel[i].setPadding(paddingPx, 0, 0, 0);
        	woensdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
        	rij[i].addView(woensdagCel[i], layoutParams);
        	        	
        	donderdagCel[i] = new TextView(this);
        	donderdagCel[i].setText("Do");
        	donderdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        	donderdagCel[i].setPadding(paddingPx, 0, 0, 0);
        	donderdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
        	rij[i].addView(donderdagCel[i], layoutParams);
        	
        	vrijdagCel[i] = new TextView(this);
        	vrijdagCel[i].setText("Vrij");
        	vrijdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        	vrijdagCel[i].setPadding(paddingPx, 0, 0, 0);
        	vrijdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
        	if(i==8){
        		vrijdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelhoek_rechts_onder));
        	}
        	rij[i].addView(vrijdagCel[i], layoutParams);
        	
        	roosterTabel.addView(rij[i]);
        }
		
	}
	
	
	public class ResponseReceiver extends BroadcastReceiver {
		public static final String ACTION_RESP ="com.mamlambo.intent.action.MESSAGE_PROCESSED";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			tabelCode = intent.getStringExtra("tabelCode");
			errorRegel = intent.getStringExtra("errorRegel");
			gebruikersnaam = intent.getStringExtra("gebruikersnaam");
			updatetijd = intent.getStringExtra("updatetijd");

			
			if(tabelCode.length() > 0){
				//Hier moet de uitvoer naar de tabel plaats vinden
				String pwrooster = "<div class=\"reeelraster\"";
				if(tabelCode.indexOf(pwrooster) == -1){
					//Het is een normale week
					sorteren(true);
				}
				
				else {
					//proefwerkweek
					sorteren(false);
				}
				gebruikerVeld.setText("Dit is het rooster van " + gebruikersnaam);
				updatetijdVeld.setText("Ge-update op: " + updatetijd);
			}
			else {
				Toast.makeText(RoosterActivity.this, errorRegel, Toast.LENGTH_LONG).show();
			}
			progressDialog.dismiss();
		}
	}
	
	private void sorteren (boolean normaleweek){
		
		if(normaleweek){
			
			//Het gaat om een normale schoolweek
			int lesPositie;
			int vrijPositie;
			int uitvalPositie;
			
			String lesScheiding = "<span class=\"nobr\">";
			String vrijScheiding = "<td class=\"vrij\">";
			String uitvalScheiding = "<td class=\"vervallen\">";
			
			for (int uur=0; uur<9; uur++){
				for (int dag=0; dag<5; dag++){
					lesPositie = tabelCode.indexOf(lesScheiding);
					vrijPositie = tabelCode.indexOf(vrijScheiding);
					uitvalPositie = tabelCode.indexOf(uitvalScheiding);
					
					if((lesPositie < vrijPositie || vrijPositie == -1) && (lesPositie < uitvalPositie || uitvalPositie == -1) && lesPositie != -1){
						
						// Het eerst volgende item is een les
						String les;
						les = tabelCode.substring(lesPositie + 19, tabelCode.indexOf("</span>"));
						les = les.replaceAll("<br />", "\n");
						
						rooster[dag][uur] = les;
						tabelCode = tabelCode.substring(tabelCode.indexOf("</table> </td>") + 14);
						
					}
					
					else if ((vrijPositie < lesPositie || lesPositie == -1) && (vrijPositie < uitvalPositie || uitvalPositie == -1) && vrijPositie != -1){
						
						// Het eerst voorkomende item is een tussenuur
						rooster[dag][uur] = "vrij";
						tabelCode = tabelCode.substring(tabelCode.indexOf("</td>") + 5);
					
					}
					
					else {
						
						// Het volgende uur valt uit
						rooster[dag][uur] = "uitval";
						tabelCode = tabelCode.substring(tabelCode.indexOf("</td>") + 5);
						
					}
					
				}
				
			}
			
			for (int i=0; i<9; i++){
				maandagCel[i].setText(rooster[0][i]);
				dinsdagCel[i].setText(rooster[1][i]);
				woensdagCel[i].setText(rooster[2][i]);
				donderdagCel[i].setText(rooster[3][i]);
				vrijdagCel[i].setText(rooster[4][i]);
			}
			
		}
		
		else{
			//We hebben te maken met een proefwerkweek of een uurgebonden rooster i.p.v een lesuur gebonden rooster
			tabelCode = tabelCode.substring(tabelCode.indexOf("</td>") + 5);
			
			String toetsScheiding = "<div class=\"toets\">";
			String lesScheiding = "<div class=\"les\">";
			String dagScheiding = "</td>";
			
			int toetsPositie = tabelCode.indexOf(toetsScheiding);
			int lesPositie = tabelCode.indexOf(lesScheiding);
			int dagPositie = tabelCode.indexOf(dagScheiding);
			
			for (int i=0; i<5; i++){
				while ( (toetsPositie < dagPositie && toetsPositie != -1) || (lesPositie < dagPositie && lesPositie != -1)){
					//Er staat nog een toets of les in deze dag
					if(toetsPositie < dagPositie && toetsPositie != -1){
						//Er is een toets
						String toetseinde = "</div>";
						int toetseindePositie = tabelCode.indexOf(toetseinde, toetsPositie);
						
						String toets = tabelCode.substring(toetsPositie + 19, toetseindePositie);
						toets = toets.replaceAll("<br />", "\n");
						
						toets = i + toets;
						proefwerkrooster.add(toets);
						tabelCode = tabelCode.substring(toetseindePositie + 6);
					}
					
					else{
						//Er is een les
						String leseinde = "</div>";
						int leseindePositie = tabelCode.indexOf(leseinde, lesPositie);
						
						String les = tabelCode.substring(lesPositie + 19, leseindePositie);
						les = les.replaceAll("<br />", "\n");
						
						les = i + les;
						proefwerkrooster.add(les);
						tabelCode = tabelCode.substring(leseindePositie + 6);
					}
					
					toetsPositie = tabelCode.indexOf(toetsScheiding);
					lesPositie = tabelCode.indexOf(lesScheiding);
					dagPositie = tabelCode.indexOf(dagScheiding);
					
				}
			}
			
		}
		
	}
	
}