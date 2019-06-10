package feed;

/**
 * 
 * @author Theodore Miller
 * The basic message format for the RSS feed
 *
 */
public class Message {
	
	private String title;
	private String description;
	private String author;
	private String publishDate;
	private String guid;
	private String link;
	
	
	public String getPublishDate() {
		return publishDate;
	}
	
	public void setPublishDate(String s) {
		this.publishDate = s;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String s) {
		this.title = s;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String s) {
		this.description = s;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String s) {
		this.author = s;
	}
	
	public String getGuid() {
		return guid;
	}
	
	public void setGuid(String s) {
		this.guid = s;
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String s) {
		this.link = s;
	}
	
	/**
	 * An override of toString for to properly format the message to be printed out.
	 */
	@Override
	public String toString() {
		String message = "Title: " + title + "\n Publishing Date: " + publishDate + "\n Description: " + description + "\n Guid: " + guid + "\n Link: " + link; 
		return message;
		
	}

}
