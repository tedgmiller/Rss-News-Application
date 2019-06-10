package feed;


import javax.xml.stream.XMLStreamException;

/**
 * The main for the News program
 * @author Theodore Miller
 *
 */
public class Main {

	/**
	 * The main method for the news program creates the feed controller and runs the run method
	 * @param args
	 * Does nothing
	 * @throws XMLStreamException
	 */
	public static void main(String[] args) throws XMLStreamException {
		//launch(args);
		FeedController f = new FeedController();
		f.run();
	}
	
	
}
