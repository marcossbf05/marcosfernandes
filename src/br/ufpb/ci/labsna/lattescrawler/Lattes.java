package br.ufpb.ci.labsna.lattescrawler;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Alexandre N�brega Duarte - alexandre@ci.ufpb.br - http://alexandrend.com
 */
public class Lattes {

	//Utilizado para tratar lattes de h�monimos, o que pode gerar problemas com o processamento do grafo.
	private static Map<String,Integer>cvNames = Collections.synchronizedMap(new HashMap<String,Integer> ()); 
	
	
	private String lattesID;
	private Dictionary <String,Integer>connections;
	private String name;
	private String nivel;

	public Lattes(String lattesID) {
		this.lattesID = lattesID;
		connections = new Hashtable<String,Integer>();	
	}

	public void addConnection(String otherLattesID) {

		Integer i = (Integer)connections.get(otherLattesID);
		if( i == null ) i = new Integer(0);
		
		i = i + 1;		
		connections.put(otherLattesID,i);
	}

	public void setName(String name) {
			
		Integer i = Lattes.cvNames.get(name);
		
		if( i == null ) {
			this.name = name;
			Lattes.cvNames.put(name, new Integer(1));
		} else {
			this.name = name + " (" + i + ")";
			i = i + 1;
			Lattes.cvNames.put(name, i);
		}
			
	}
	
	public String getName(){
		return name;
	}
	
	public String getLattesID() {
		return lattesID;
	}
	
	public Dictionary <String,Integer> getConnections() {
		return connections;
	}

	public void setPQ(String nivelPQ) {
		setNivel("PQ-"+nivelPQ);
	}

	public void setDT(String nivelDT) {
		setNivel( "DT-"+nivelDT);
		
	}
	
	public void setNivel(String nivel) {
		this.nivel = nivel;
	}
	
	public String getNivel(){
		return this.nivel;
	}

	public void extractData(LattesCrawler lc)  {
		
		try {
			
			Document doc = Jsoup.connect("http://lattes.cnpq.br/" + lattesID).timeout(60*1000).get();
			
			String title[] = Jsoup.parse(doc.select(".nome").toString()).text().split("Bolsista");
			setName(title[0]);
			this.nivel = null;

			if( title.length > 1 )
				this.nivel =  "Bolsista " + title[1];

			Elements links = doc.select("a[href]");		
			for (Element link : links) {
			    String l = link.attr("abs:href");
			    if( l.startsWith("http://lattes.cnpq.br") && !l.endsWith(lattesID))
			    	addConnection( l.substring(22));
			}
		} catch (Exception e) {
			System.err.println( "http://lattes.cnpq.br/" + lattesID);		
			e.printStackTrace();
		}	
	
	}
	
	
	public boolean equals (Lattes o) {
		return o.getLattesID().equals(getLattesID());
	}
	
}
