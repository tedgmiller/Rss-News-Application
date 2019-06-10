package feed;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public final class Utils {

	/**
	 * Finds if string s contains the key 
	 * @param key
	 * @param s
	 * @return
	 */
	public static boolean hasKeyword(String key, String s) {
		s = s.toLowerCase();
		key.toLowerCase();
		s = s.replaceAll("[^a-zA-Z0-9 ]", "");
		key = key.replaceAll("[^a-zA-Z0-9 ]", "");
		String[] split = s.split(" ");
		for(String word: split) {
			if(key.equals(word) || key.equals(word + "s")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Finds if the given String key exists within the String s without special characters(:,',ect.)
	 * @param key
	 * The word or phrase being looked for
	 * @param s
	 * The String that is being looked through for the given word or phrase
	 * @return
	 * Returns a boolean true if the key exists within s and false if it doesn't.
	 */
	public static boolean hasKeywordOrPhrase(String key, String s) {
		s = s.toLowerCase();
		key.toLowerCase();
		s = s.replaceAll("[^a-zA-Z0-9 ]", "");
		key = key.replaceAll("[^a-zA-Z0-9 ]", "");
		String[] splitS = s.split(" ");
		String[] splitKey = key.split(" ");
		int j = 0;
		for(int i = 0; i < (splitS.length - splitKey.length); i++) {
			if(splitS[i].equals(splitKey[j]) || splitS[i].equals(splitKey[j] + "s")) {
				j++;
				if(splitKey.length == j) {
					return true;
				}
			}
			else {
				j = 0;
			}
		}
		return false;
	}
	
	/**
	 * Creates a URL object given a string that is the url.
	 * @param url
	 * A string that is the url for a website
	 * @return
	 * returns the new URL object. Returns null if there was an error creating the URL object
	 */
	public static URL createUrl(String url) {
		try {
			return new URL(url);
		}
		catch(MalformedURLException e) {
			return null;
		}
	}
	
	/**
	 * Used for the reading of XML files gets the data from the local part
	 * @param event
	 * A XML event that will hold the data
	 * @param eventReader
	 * The event reader for the current XML file being read
	 * @return
	 * Returns a string that is the data within the local part
	 * @throws XMLStreamException
	 */
	public static String getData(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
        String result = "";
        event = eventReader.nextEvent();
        if (event instanceof Characters) {
            result = event.asCharacters().getData();
        }
        return result;
    }
	
	/**
	 * Saves the registered sites within feeds into an XML document at the given location saveFile
	 * Save the websites URL and name
	 * @param saveFile
	 * The location and name of the given save file
	 * @param feeds
	 * The list of websites that need to be save.
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 */
	public static void saveSites(String saveFile, List<RssFeed> feeds) throws XMLStreamException, FileNotFoundException {
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(saveFile));
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		//defining end and tab 
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        //Starting the XML document
        StartDocument startDoc = eventFactory.createStartDocument();
        eventWriter.add(startDoc);
        eventWriter.add(end);
        StartElement rssStart = eventFactory.createStartElement("", "", "rss");
        eventWriter.add(rssStart);
        eventWriter.add(end);
        //Saving the websites
        for(RssFeed f: feeds) {
        	eventWriter.add(tab);
        	eventWriter.add(eventFactory.createStartElement("", "", "item"));
            eventWriter.add(end);
            eventWriter.add(tab);
        	createNode("url", f.getUrl(), eventWriter);
        	eventWriter.add(tab);
        	createNode("website", f.getNewsWebsite(), eventWriter);
        	eventWriter.add(tab);
        	eventWriter.add(eventFactory.createEndElement("", "", "item"));
            eventWriter.add(end);
        }
        //Ending the XML document
        eventWriter.add(eventFactory.createEndElement("", "", "rss"));
        eventWriter.add(end);
        eventWriter.add(eventFactory.createEndDocument());
        eventWriter.close();
	}
	
	/**
	 * Creates an XML node for save
	 * @param name
	 * The name of the given node in string form
	 * @param value
	 * The value to be stored in the Node in string form
	 * @param e
	 * The XMLEventWriter that is writing to the save file
	 * @throws XMLStreamException
	 */
	public static void createNode(String name, String value, XMLEventWriter e) throws XMLStreamException {
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD("\n");
	    XMLEvent tab = eventFactory.createDTD("\t");
	    StartElement sElement = eventFactory.createStartElement("", "", name);
	    e.add(tab);
	    e.add(sElement);
	    Characters characters = eventFactory.createCharacters(value);
        e.add(characters);
        EndElement eElement = eventFactory.createEndElement("", "", name);
        e.add(eElement);
        e.add(end);
	}
	
	/**
	 * Opens the given message in the web browser
	 * @param m
	 * The message the method will open
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void openWebpage(Message m) throws IOException, URISyntaxException {
		Desktop.getDesktop().browse(Utils.createUrl(m.getLink()).toURI());
	}
	
	/**
	 * Loads the websites from the given file. Checks that the save file exists.
	 * Gets the news stories from the sites and creates a RssFeed for each site
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public static void loadSites(String saveFile, Read r, List<RssFeed> feeds) throws FileNotFoundException, XMLStreamException {
		File save = new File(saveFile);
		//check that file exists
		if(!save.exists()) {
			return;
		}
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream input = new FileInputStream(saveFile);
		XMLEventReader eventReader = inputFactory.createXMLEventReader(input);
		String url = "";
		String website = "";
		boolean header = true;
		while(eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if(event.isStartElement()) {
				String sectionName = event.asStartElement().getName().getLocalPart();	
				//finding each element of the webpage to start the read
				switch(sectionName) {
				case "item":
					if(header) {
						header = false;
						event = eventReader.nextEvent();
					}
					break;
				case "url":
					url = Utils.getData(event, eventReader);
					break;
				case "website":
					website = Utils.getData(event, eventReader);
					break;
				}
			}
			else if(event.isEndElement()) {
				if(event.asEndElement().getName().getLocalPart() == "item") {
					//reads the website using the given information and saves it to feed
					feeds.add(r.readSite(url, website));
				}
			}
		}
	}
	
	
}
