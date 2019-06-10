package feed;

import java.util.ArrayList;
import java.util.List;

/**
 * The object that holds a websites url, name and the news feed.
 * @author Theodore Miller
 *
 */
public class RssFeed {

	/**
	 * The list of stories pulled from the website
	 */
	private List<Message> messageList = new ArrayList<Message>();
	
	private String newsWebsite;
	
	private String url;
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getNewsWebsite() {
		return newsWebsite;
	}
	
	public void setNewsWebsite(String website) {
		this.newsWebsite = website;
	}
	
	public List<Message> getMessages() {
		return messageList;
	}
	
	public void addMessage(Message m) {
		messageList.add(m);
	}
	
	/**
	 * Prints out the messages to the console.
	 */
	public void printMessages() {
		int i = 1;
		for(Message m : messageList) {
			System.out.println(i++ + "\n" + m.toString() + "\n");
		}
	}
	
	
}
