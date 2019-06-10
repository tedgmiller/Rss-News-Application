package feed;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * An object that reads the xml from a website
 * @author Theodore Miller
 *
 */
public class Read {
	
	/**
	 * Reads the xml from a given websites rss page and returns an RssFeed object that holds the website information
	 * and the different news stories.
	 * @param url
	 * The Rss url for the given website
	 * @param website
	 * The name of the website
	 * @return
	 * Returns an RssFeed object
	 * @throws XMLStreamException
	 */
	public RssFeed readSite(String url, String website) throws XMLStreamException {
		RssFeed feed = new RssFeed();
		feed.setNewsWebsite(website);
		feed.setUrl(url);
		boolean header = true;
		String title = "";
		String guid = "";
		String link = "";
		String author = "";
		String desc = "";
		String publishDate = "";
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		if(Utils.createUrl(url) == null) {
			return null;
		}
		InputStream input = getUrlStream(Utils.createUrl(url));
		XMLEventReader eventReader = inputFactory.createXMLEventReader(input);
		//Reading all of the information from the given XML and creates message objects and adds it to feed
		while(eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if(event.isStartElement()) {
				String sectionName = event.asStartElement().getName().getLocalPart();
				switch(sectionName) {
				case "item":
					if(header) {
						header = false;
						event = eventReader.nextEvent();
					}
					break;
				case "title":
					title = Utils.getData(event, eventReader);
					break;
				case "guid":
					guid = Utils.getData(event, eventReader);
					break;
				case "link":
					link = Utils.getData(event, eventReader);
					break;
				case "author":
					author = Utils.getData(event, eventReader);
					break;
				case "description":
					desc = Utils.getData(event, eventReader);
					break;
				case "pubDate":
					publishDate = Utils.getData(event, eventReader);
					break;
				}
			}
			else if(event.isEndElement()) { //End of the given XML object
				if(event.asEndElement().getName().getLocalPart() == "item") {
					if(!header) {
						Message m = new Message();
						m.setTitle(title);
						m.setLink(link);
						m.setGuid(guid);
						m.setDescription(desc);
						m.setPublishDate(publishDate);
						m.setAuthor(author);
						feed.addMessage(m);
					}
				}
			}
		}
		return feed;
	}
	
	/**
	 * Gets the InputStream from the given URL
	 * @param url
	 * The url object that the InputStream will be gotten from
	 * @return
	 * The URL's InputStream
	 */
	private InputStream getUrlStream(URL url) {
		try {
			return url.openStream();
		}
		catch(IOException e) {
			System.out.println("Error opening the websites url");
			return null;
		}
	}
	

}
