package nl.groenewoud.roosterapp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nl.groenewoud.roosterapp.RoosterActivity.ResponseReceiver;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.loopj.android.http.PersistentCookieStore;

import android.app.IntentService;
import android.content.Intent;

/*
 * Deze service handeld het downloaden van de broncode en het omzetten naar een handelbaar formaat af. 
 * Dit alles gebeurt op de achtergrond, zodat de gebruiker er geen invloed op heeft.
 */

public class DownloadService extends IntentService {

	public DownloadService() {
		
		super("DownloadService");
		
	}
	
	/*
	 * Variabelen die terug gestuurd worden naar de voorgrond om daar gebruikt te worden
	 */
	
	private String tabelCode;
	private String errorRegel;
	private String gebruikersnaam;
	private String updatetijd;
	private String weeknummer;

	private String user;
	private String paswoord;
	private boolean gewijzigd;
	/*
	 * Deze void wordt opgeroepen als de service gestart wordt. 
	 * Dit gebeurt als de gebruiker het rooster wil updaten.
	 */
	
	@Override
	protected void onHandleIntent(Intent intent){
		/*
		 * Dit zijn de variabelen waarin de gegevens opgeslagen zijn die nodig zijn bij
		 * het downloaden van het rooster. Zoals gebruikersnaam, wachtwoord, weeknummer etc. 
		 */
		
		user = intent.getExtras().getString("user");
		paswoord = intent.getExtras().getString("paswoord");
		gewijzigd = intent.getExtras().getBoolean("gewijzigd");
		
		/*
		 *  Omdat de service hergebruikt wordt bij een rooster update moeten de variabelen geleegd worden
		 *  om vervuiling te voorkomen
		 */
		
		tabelCode = "";
		errorRegel = "";
		gebruikersnaam = "";
		updatetijd = "";
		
		/*
		 * Hier wordt geprobeerd het rooster te downloaden. Als er tijdens het downloaden een
		 * fout met de verbinding plaats vindt (IOException, ClientProtocolException of een andere Exception),
		 * dan wordt die hier opgevangen en krijgt de gebruiker een berichtje dat er iets mis ging.
		 */
		
		try{
			downloadUrl("http://www.groenewoud.nl/infoweb/infoweb/index.php");
			
			/*
			 * Nu het downloaden voltooid is willen we de tekst leesbaar maken.
			 */
			
			if (tabelCode.length() > 0){
				// Er is een rooster gevonden, we ontdoen de tabel van alle fratsen om zo de leesbaarheid te verhogen
				tabelCode = tabelCode.replaceAll("<a href=\'(.+?)\'>(.+?)</a>", "$2");
				tabelCode = tabelCode.replaceAll("TITLE=\"(.+?)\"", "");
				tabelCode = tabelCode.replaceAll("(<tr>)?<td class=\"pic( wijziging)?\">(.+?)</td>(</tr>)?", "");
				tabelCode = tabelCode.replaceAll("<td class=\"pic informatie\" >(.+?)</td>", "");
				tabelCode = tabelCode.replaceAll("<div class=\"toets\"(.+?)>", "<div class=\"toets\">");
				tabelCode = tabelCode.replaceAll("<div class=\"les\"(.+?)>", "<div class=\"les\">");
				
				SimpleDateFormat datum = new SimpleDateFormat("EEE, d MMM", new Locale("nl", "NL"));
				SimpleDateFormat tijd = new SimpleDateFormat("HH:mm:ss", new Locale("nl", "NL"));
				updatetijd = "Geüpdate op " + datum.format(new Date()) + " om " + tijd.format(new Date());
			}
			
		} catch (ClientProtocolException e){
			errorRegel = "Er gaat iets mis " + e.toString();
		} catch (IOException e) {
			errorRegel = "Er gaat iets mis: " + e.toString();
		} catch (Exception e){
			errorRegel = "Er gaat iets mis: " + e.toString();
		}
		
		/*
		 * Als het eenmaal wel of niet gelukt is dan wordt het resultaat naar de voorgrond
		 * terug gestuurd om daar aan de gebruiker getoond te worden.
		 */

		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(ResponseReceiver.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra("tabelCode", tabelCode);
		broadcastIntent.putExtra("errorRegel", errorRegel);
		broadcastIntent.putExtra("gebruikersnaam", gebruikersnaam);
		broadcastIntent.putExtra("updatetijd", updatetijd);
		broadcastIntent.putExtra("weeknummer", weeknummer);
		sendBroadcast(broadcastIntent);
		
	}
	
	
	/*
	 * Hier worden de parameters voor de netwerkverbinding opgesteld, bijvoorbeeld de
	 * timeout tijd (10 seconden), de poort waarop er gedwonload moet worden (poort 80)
	 */
	
    private DefaultHttpClient createHttpClient() {
    	
    	HttpParams my_httpParams = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(my_httpParams, 10000); //Timeout in milliseconden
    	SchemeRegistry registry = new SchemeRegistry();
    	registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    	ThreadSafeClientConnManager multiThreadedConnectionManager = new ThreadSafeClientConnManager(my_httpParams, registry);
    	DefaultHttpClient httpclient = new DefaultHttpClient(multiThreadedConnectionManager, my_httpParams);
    	return httpclient;
    	
    }
	
    /*
     * In deze methode wordt de broncode gedownload en teruggestuurd naar de hoofd methode om omgezet
     * te worden naar handelbare code
     */
    
	private String downloadUrl(String infoweblink) throws IOException, ClientProtocolException, Exception {
		
		int statuscode;
		String broncode;
		
		/*
		 * Hier wordt de netwerkverbinding opgestart, eerst worden er variabelen gemaakt om
		 * de verbinding, het antwoord en de opslag van cookies in op te slaan. Daarna
		 * wordt de verbinding uitgevoerd, het antwoord en de http statuscode opgeslagen. 
		 */
		
		DefaultHttpClient httpclient;
		HttpResponse response;
		PersistentCookieStore cookieStore = new PersistentCookieStore(getBaseContext());
		httpclient = createHttpClient();
		httpclient.setCookieStore(cookieStore);
		cookieStore.clearExpired(new Date());
		HttpGet request = new HttpGet(infoweblink);
		response = httpclient.execute(request); // Hier wordt de verbinding uitgevoerd en het antwoord opgeslagen in de response variabele 
		statuscode = response.getStatusLine().getStatusCode(); // Hier wordt de http statuscode opgeslagen

		if(statuscode == 200){
			//Als de download succesvol was dan willen we inloggen, de week veranderen, na gaan of er wel een rooster online staat of het rooster weergeven
			broncode = EntityUtils.toString(response.getEntity());
			
			if(broncode.indexOf("<h1>Mijn rooster</h1>") == -1 || gewijzigd){
				//We willen inloggen of de week veranderen
				int csrfPositie = broncode.indexOf("name=\"csrf\" value=\"");
				
				if(csrfPositie == -1){
					errorRegel = "Ik kan de beveiliging van infoweb niet omzeilen.";
					return errorRegel;
				}
				
				else {
					//We hebben de verificatie code laten we inloggen
					String csrfCode = broncode.substring(csrfPositie + 19, csrfPositie + 51);
					
					//De onderstaande lijst is de informatie die meegstuurd wordt om in te loggen op infoweb
					List<NameValuePair> postData = new ArrayList<NameValuePair>(1);
					  postData.add(new BasicNameValuePair("user", user));
					  postData.add(new BasicNameValuePair("paswoord", paswoord));
					  postData.add(new BasicNameValuePair("login", "loginform"));
					  postData.add(new BasicNameValuePair("csrf", csrfCode));
					  					  
					//Hier wordt er ingelogd en wordt de broncode en statuscode ingeladen.
					HttpPost post = new HttpPost(infoweblink);
					post.setEntity(new UrlEncodedFormEntity(postData));
					response = httpclient.execute(post);
					statuscode = response.getStatusLine().getStatusCode();
					
					if(statuscode == 200){
						broncode = EntityUtils.toString(response.getEntity());
					}
					
					else if(statuscode == 404){
						// De pagina is niet gevonden
						errorRegel = "404 Pagina niet gevonden, Infoweb kon niet gevonden.";			
						return errorRegel;
					}
					
					else if(statuscode == 500){
						//Verkeerd wachtwoord/gebruikersnaam
						errorRegel = "Je hebt een verkeerde gebruikersnaam/wachtwoord ingevuld.";
						return errorRegel;
					}
					
					else if(statuscode > 0){
						//Er ging iets anders mis, maar er is wel een verbinding gemaakt met de server
						errorRegel =  "Error " + statuscode + " er ging iets mis! Infoweb is niet gevonden.";
						return errorRegel;
					}
					
					else {
						//Er was geen antwoord van de server
						errorRegel = "Error 0 de server gaf geen antwoord en ligt hoogstwaarschijnlijk plat.";
						return errorRegel;
					}	
				}
			}
			
			if(broncode.indexOf("Geen rooster") >= 0){
				int weekStart = broncode.indexOf("selected=\"selected\">");
				int weekEind = broncode.indexOf("</", weekStart);
				weeknummer = broncode.substring(weekStart + 20, weekEind);
				
				errorRegel = "Er staat geen rooster online";
				return errorRegel;
			}
			
			else if(broncode.indexOf("<h1>Mijn rooster</h1>") == -1){
				errorRegel = "Het is me niet gelukt om in te loggen";
				return errorRegel;
			}
			
			else{
				// Er staat een rooster op infoweb, haal het rooster uit de broncode en stuur deze terug voor analyse
				int tabelPositie = broncode.indexOf("<table class=\"roosterdeel\"");
				int tabelEinde = broncode.indexOf("<td id=\"linkerfooter\">");
				tabelCode = broncode.substring(tabelPositie, tabelEinde);
				
				int gebruikersnaamPositie = broncode.indexOf("<h2>Het rooster voor ");
				gebruikersnaam = broncode.substring(gebruikersnaamPositie + 21, broncode.indexOf("</h2>", gebruikersnaamPositie));
				
				int weekStart = broncode.indexOf("selected=\"selected\">");
				int weekEind = broncode.indexOf("</", weekStart);
				weeknummer = broncode.substring(weekStart + 20, weekEind);
				
				return tabelCode;
			}
		}
		
		//Er ging iets mis met de verbinding
		
		else if(statuscode == 404){
			// De pagina is niet gevonden
			errorRegel = "404 Pagina niet gevonden, Infoweb kon niet gevonden.";			
			return errorRegel;
		}

		else if(statuscode == 500){
			//Verkeerde wachtwoord of gebruikersnaam
			errorRegel = "Je hebt een verkeerde gebruikersnaam/wachtwoord ingevuld.";
			return errorRegel;
		}

		else if(statuscode > 0){
			//Er ging iets anders mis, maar er is wel een verbinding gemaakt met de server
			errorRegel =  "Error " + statuscode + " er ging iets mis! Infoweb is niet gevonden.";
			return errorRegel;
		}

		else {
			//Er was geen antwoord van de server
			errorRegel = "Error 0 de server gaf geen antwoord en ligt hoogstwaarschijnlijk plat.";
			return errorRegel;
		}		
	}

}