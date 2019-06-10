package feed;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.xml.stream.XMLStreamException;

/**
 * The controller for the Command line application version of The RSS news application.
 * This object is not currently in use because of the gui.
 * @author Theodore Miller
 *
 */
public class FeedController {
	
	private List<RssFeed> feeds = new ArrayList<RssFeed>();
	
	private Read r = new Read();
	
	private final String saveFile = "websites";
	
	/**
	 * The run application of the controller. Starts the input loop for the application.
	 */
	public void run() {
		init();
		Scanner s = new Scanner(System.in);
		String input = "";
		//The help string that gives all the different commands of the application
		String help = "Quit program: quit\n" //added
				+ "List registered sites: lrs\n" //added
				+ "Filter all by Keyword: fbw\n" //added
				+ "Filter one site by Keyword: fsbw\n"
				+ "List all news: ls\n" //added
				+ "List specific site news: sls\n" //added
				+ "Add website: aw" //added
				+ "Update sites and stories: update\n" //added
				+ "Open Story: os\n" //added
				+ "Remove Website: rs\n"; 
				
		while(true) {
			System.out.println("Please enter command: ");
			input = s.nextLine();
			switch(input) {
			case "quit":
				try {
					Utils.saveSites(saveFile, feeds);
				} catch (FileNotFoundException | XMLStreamException e1) {
					throw new RuntimeException(e1);
				}
				s.close();
				return;
			case "help":
				System.out.println(help);
				break;
			case "lrs": //Lists the registered sites
				for(RssFeed r : feeds) {
					System.out.println(r.getNewsWebsite());
				}
				break;
			case "ls": //lists all of the stories giving separating by news sites.
				for(RssFeed r: feeds) {
					System.out.println(r.getNewsWebsite());
					int i = 1;
					for(Message m: r.getMessages()) {
						System.out.println(i++ + "\n" + m.toString());
					}
					System.out.println("-------------------------");
				}
				break;
			case "fbw": // Searches for news stories containing the keyword and separates by news site
				System.out.println("Please give Keyword: ");
				input = s.nextLine();
				for(RssFeed r: feeds) {
					int i = 1;
					System.out.println(r.getNewsWebsite());
					for(Message m: r.getMessages()) {
						if(Utils.hasKeyword(input, m.getTitle())) {
							System.out.println(i + "\n"+ m.toString());
						}
						i++;
					}
					System.out.println("-------------------------");
				}
				break;
			case "sls": //Lists the stories of a particular site
				System.out.println("Please Enter News site: ");
				input = s.nextLine();
				for(RssFeed r: feeds) {
					if(r.getNewsWebsite().toLowerCase().equals(input.toLowerCase())) {
						r.printMessages();
					}
				}
				break;
			case "aw": //adds a new website and searches for stories
				String name, url;
				RssFeed tempFeed = new RssFeed();
				System.out.println("Please enter websites name: ");
				name = s.nextLine();
				System.out.println("The url for the website must be an RSS url from the website");
				System.out.println("Please enter websites url: ");
				url = s.nextLine();
				try {
					tempFeed = r.readSite(url, name);
				} catch (XMLStreamException e) {
					System.out.println("URL error please try again. URL needs to be a RSS link");
				}
				if(tempFeed == null) {
					System.out.println("Bad URL");
				}
				else {
					feeds.add(tempFeed);
				}
			case "update": //updates the save file and the websites
				try{
					Utils.saveSites(saveFile, feeds);
				} catch (FileNotFoundException | XMLStreamException e) {
					throw new RuntimeException(e);
				}
				for(int i = 0; i < feeds.size(); i++) {
					try {
						feeds.set(i, r.readSite(feeds.get(i).getUrl(), feeds.get(i).getNewsWebsite()));
					} catch (XMLStreamException e) {
						System.err.println("Failed to find website " + feeds.get(i).getNewsWebsite());
						e.printStackTrace();
					}
				}
				break;
			case "os": // opens the web-page for a story given its number and web-site name
				String site;
				int storyNumber;
				System.out.println("Please enter news site");
				site = s.nextLine();
				System.out.println("Please enter story number");
				storyNumber = Integer.parseInt(s.nextLine());
				for(RssFeed f: feeds) {
					if(f.getNewsWebsite().toLowerCase().equals(site.toLowerCase())) {
						if(storyNumber >= 0 && storyNumber <= f.getMessages().size()) {
							try {
								Utils.openWebpage(f.getMessages().get(storyNumber - 1));
							} catch (IOException | URISyntaxException e) {
								throw new RuntimeException(e);
							}
							break;
						}
					}
				}
				break;
			case "rs": //Removes a site from the feed
				System.out.println("Please enter the site");
				input = s.nextLine();
				for(RssFeed f: feeds) {
					if(f.getNewsWebsite().toLowerCase().equals(input.toLowerCase())) {
						feeds.remove(f);
						break;
					}
				}
				break;
			}
		}
	}
	
	/**
	 * The initialization for the controller. Loads the sites
	 */
	private void init(){
		try {
			Utils.loadSites(saveFile, r, feeds);;
		} catch (FileNotFoundException | XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
	
}
