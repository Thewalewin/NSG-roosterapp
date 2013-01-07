package nl.groenewoud.roosterapp;

/*
 * Hier worden globale variabelen opgeslagen om ze door de hele app te gebruiken
 */

// Referenties naar java klassen die nodig zijn voor de werking van de app
import java.util.ArrayList;
import android.app.Application;

public class GlobalVars extends Application {
	
	/*
	 *  De Globale variabelen leerlingnummer en wachtwoord protected betekend dat ze alleen leesbaar zijn vanuit mijn app. 
	 *  De buitenwereld kan ze niet uitlezen.
	 */
	protected String leerlingnummer = "";
	protected String wachtwoord = "";
	
	// De klasse lesuur is een klasse die ik zelf gemaakt heb om de informatie over de lesuren in op te slaan
	protected class lesuur {
		// De klasse lesuur bevat een integer(geheel getal) n die de dag bijhoud en een String (variabele die tekst kan bevatten) lesinformatie
		int n;
		String lesinformatie;
		
		// Als er een nieuw lesuur aangemaakt worden moeten de meegegeven waarden opgeslagen worden in de klasse
		public lesuur(int dag, String les) {
			n = dag;
			lesinformatie = les;
		}
	  }
	
	// Maak een lijst van het type lesuur met naam rooster, de begin grote is 0
	protected ArrayList<lesuur> rooster = new ArrayList<lesuur>(0);
	
	/* 
	 *  Hieronder staan alle getters en setters voor de variabelen, een getter stuurt de opgeslagen informatie terug een setter veranderd deze
	 *  Bijv: de methode getLeerlingnummer(), return leerlingnummer wil niets meer zeggen dan: stuur leerlingnummer terug
	 *  setLeerlingnummer(String leerlingnummer) doet het volgende: de leerlingnummer van deze klasse (this.leerlingnummer) 
	 *  krijgt de meegeleverde waarde leerlingnummer toegewezen
	 */
	protected String getLeerlingnummer(){
		return leerlingnummer;
	}
	  
	protected void setLeerlingnummer(String leerlingnummer){
		this.leerlingnummer = leerlingnummer;
	}
	  
	protected String getWachtwoord(){
		return wachtwoord;
	}
	  
	protected void setWachtwoord(String wachtwoord){
		this.wachtwoord = wachtwoord;
	}
	  
	protected ArrayList<lesuur> getRooster(){
		return rooster;
	}
	  
	protected void setRooster(ArrayList<lesuur> rooster){
		this.rooster = rooster;
	}
	/*
	 * Met de laatste methode clearRooster kan de huidige lijst geleegd worden om zo besmetting te voorkomen  
	 */
	protected void clearRooster(){
		this.rooster.clear();
		this.rooster.trimToSize();
	}
}