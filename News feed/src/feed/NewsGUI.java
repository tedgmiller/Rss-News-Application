package feed;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;



import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage; 

/**
 * 
 * @author Theodore Miller
 * The GUI for the News application
 *
 */
public class NewsGUI extends Application{
	
	/**
	 * The Different News feeds for the Application
	 */
	private List<RssFeed> feeds = new ArrayList<RssFeed>();
	
	private Read r = new Read();
	
	/**
	 * The file that the Application saves to
	 */
	private final String saveFile = "websites";

	/**
	 * The Start for the News GUI that sets up the stage and the elements within it.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		//Defining Elements
		primaryStage.setTitle("News Feed");
		Menu fileMenu = new Menu("File");
		MenuItem updateMenuItem = new MenuItem("Update(f5)");
		MenuBar menu = new MenuBar();
		Text storiesLabel = new Text("Current Stories");
		Text storyInformation = new Text("Story Information");
		Text storyInfoText = new Text();
		Button openPage = new Button("Open Selected");
		Button newWebpage = new Button("Add New Page");
		Button filterButton = new Button("Filter");
		Button removeWebsite = new Button("Remove Website");
		TextField filterText = new TextField("Keyword or Phrase filter");
		ChoiceBox<String> websiteSelection = new ChoiceBox<String>();
		ObservableList<String> names = FXCollections.observableArrayList();
		ListView<String> stories = new ListView<String>(names);
		//Loading the websites and adding them to necessary elements
		Utils.loadSites(saveFile, r, feeds);
		fillStories(names);
		fillWebsiteChoiceBox(websiteSelection);
		//Defining properties
		stories.onMouseClickedProperty();
		stories.setMinWidth(700);
		storyInfoText.setWrappingWidth(500);
		//Menu set up
		fileMenu.getItems().add(updateMenuItem);
		menu.getMenus().add(fileMenu);
		//Events
		removeWebsite.addEventFilter(MouseEvent.MOUSE_CLICKED, removeWebsite(websiteSelection, names));
		stories.addEventFilter(MouseEvent.MOUSE_CLICKED, getStoryInformation(stories, storyInfoText));
		openPage.addEventFilter(MouseEvent.MOUSE_CLICKED, openPageEvent(stories));
		filterText.setOnKeyPressed(filterBy(filterText, websiteSelection, names));
		filterButton.addEventFilter(MouseEvent.MOUSE_CLICKED, filterBy(filterText, websiteSelection, names));
		//Updates feeds.
		updateMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				updateWithProgressBar(names);
			}
			
		});
		//Removes the text from filterText when its clicked on the first time
		filterText.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				if(filterText.getText().equals("Keyword or Phrase filter")) {
					filterText.setText("");
				}
			}
			
		});
		//Handles the adding of a new webpage and the pop up for that
		newWebpage.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				final Stage dialog = new Stage();
				dialog.initModality(Modality.APPLICATION_MODAL);
	            dialog.initOwner(primaryStage);
	            TextField url = new TextField();
				TextField siteName = new TextField();
	            Button accept = new Button("Enter");
	            accept.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						try {
							RssFeed webFeed = r.readSite(url.getText(), siteName.getText());
							if(webFeed == null) {
								return;
							}
							for(Message m: webFeed.getMessages()) {
								names.add(webFeed.getNewsWebsite() + ": " + m.getTitle());
							}
							feeds.add(webFeed);
							Utils.saveSites(saveFile, feeds);
							fillWebsiteChoiceBox(websiteSelection);
							dialog.close();
						} catch (XMLStreamException | FileNotFoundException e) {
							System.err.println("An error occurred with getting the RSS information\n" + e.getMessage());
							e.printStackTrace();
						}
					}
	            	
	            });
				GridPane newPageGrid = new GridPane();
				newPageGrid.add(new Text("RSS url"), 0, 1);
				newPageGrid.add(new Text("webpage name"), 0, 0);
				newPageGrid.add(url, 1, 0);
				newPageGrid.add(siteName, 1, 1);
				newPageGrid.add(accept, 1, 2);
				Scene newPageScene = new Scene(newPageGrid);
				dialog.setScene(newPageScene);
				dialog.setTitle("Add New Webpage");
				dialog.show();
			}
			
		});
		//Gridpane for the stage
		GridPane grid = new GridPane();
		//GridPane for the inputs section
		GridPane inputGrid = new GridPane();
		//Story information grid
		GridPane infoGrid = new GridPane();
		//Grid for the top buttons
		GridPane buttonGrid = new GridPane();
		//Grid for the menu
		GridPane menuGrid = new GridPane();
		//Arrangment for Menu grid
		menuGrid.add(menu, 0, 0);
		//Arrangement for ButtonGrid
		buttonGrid.add(openPage, 0, 0);
		buttonGrid.add(newWebpage, 1, 0);
		buttonGrid.add(removeWebsite, 2, 0);
		//Arrangement for Input Grid
		inputGrid.add(filterText, 1, 1);
		inputGrid.add(websiteSelection, 0, 1);
		inputGrid.add(filterButton, 0, 2);
		//Arrangement for infoGrid
		infoGrid.add(buttonGrid, 0, 0);
		infoGrid.add(inputGrid, 0, 1);
		infoGrid.add(storyInformation, 0, 2);
		infoGrid.add(storyInfoText, 0, 3);
		//Arrangement for main grid
		grid.add(menuGrid, 0, 0);
		grid.add(storiesLabel, 0, 1);
		grid.add(stories, 0, 2);
		grid.add(infoGrid, 1, 2);
		//Scene and state setting
		Scene scene = new Scene(grid);
		scene.setOnKeyPressed(updateItems(names));
	    primaryStage.setScene(scene);
	    primaryStage.show(); 
	}
	
	/**
	 * Fills in the stories list with the currently registered stories in feeds
	 * @param names
	 * The observable list that holds the stories information
	 */
	private void fillStories(ObservableList<String> names) {
		for(RssFeed f: feeds) {
			addRssFeedToStories(names, f);
		}
	}
	
	/**
	 * Add the particular Rssfeed the the list of stories 
	 * @param names
	 * The List of all of the different stories
	 * @param f
	 * The RssFeed that will be added to stories
	 */
	private void addRssFeedToStories(ObservableList<String> names, RssFeed f) {
		for(Message m: f.getMessages()) {
			names.add(f.getNewsWebsite() + ": " + m.getTitle());
		}
	}
	
	/**
	 * Fills the website choice box with the currently registered websites
	 * @param c
	 * The Choice box that will be filled with the websites registered in feed
	 */
	private void fillWebsiteChoiceBox(ChoiceBox<String> c) {
		c.getItems().clear();
		c.getItems().add("All");
		for(RssFeed f: feeds) {
			c.getItems().add(f.getNewsWebsite());
		}
	}
	
	/**
	 * Returns an event that removes the selected website in the choicebox from the choicebox, feeds, and the stories from names
	 * @param choice
	 * The ChoiceBox that has selected the website and that the website will be removed from
	 * @param names
	 * The observable list that is holding the stories from all the different websites
	 * @return
	 * Returns a mouseEvent eventHandler that removes the selected webpage from choice, names, and feeds
	 */
	private EventHandler<MouseEvent> removeWebsite(ChoiceBox<String> choice, ObservableList<String> names){
		return new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(choice.getSelectionModel().getSelectedIndex() > 0) {
					feeds.remove(choice.getSelectionModel().getSelectedIndex() - 1);
					names.clear();
					fillStories(names);
					choice.getItems().clear();
					fillWebsiteChoiceBox(choice);
					try {
						Utils.saveSites(saveFile, feeds);
					} catch (FileNotFoundException | XMLStreamException e) {
						e.printStackTrace();
					}
				}
			}
			
		};
		
	}
	
	/**
	 * Gets the information on the selected story to display on storyInfoText
	 * @param stories
	 * The listview for the different stories. This is the list that will be looked at when getting the story information
	 * @param storyInfoText
	 * This is the Text that will show the story information
	 * @return
	 * Returns a MouseEvent EventHandler that gets a storys information from feed.
	 */
	private EventHandler<MouseEvent> getStoryInformation(ListView<String> stories, Text storyInfoText){
		return new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				String selected = stories.getSelectionModel().getSelectedItem();
				String selectedSite = getSelectedSite(selected);
				selected = selected.substring(selectedSite.length() + 2, selected.length());
				for(RssFeed f: feeds) {
					if(f.getNewsWebsite().equals(selectedSite)) {
						for(Message m: f.getMessages()) {
							if(m.getTitle().equals(selected)) {
								storyInfoText.setText("Website: " + f.getNewsWebsite() + "\n"
										+ "Title: " + m.getTitle() + "\n"
										+ "Publishing Date: " + m.getPublishDate() + "\n"
										+ "Description:\n" + m.getDescription() + "\n");
								break; 
							}
						}
						break;
					}
				}
			}
			
		};
	}
	
	/**
	 * Filters Feed and fills names with the new filtered information. Filters by the ChoiceBox and the TextField
	 * @param filterText
	 * The text that will be filtered by
	 * @param choice
	 * The choicebox that holds the different websites. Will filter by websites/
	 * @param names
	 * The observable list that is being shown in the gui for the stories
	 * @return
	 * Returns an MouseEvent EventHandler that with the given inputs filter feed
	 */
	private EventHandler<Event> filterBy(TextField filterText, ChoiceBox<String> choice, ObservableList<String> names){
		return new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				if(arg0.getClass().equals(KeyEvent.class)) {
					if(((KeyEvent)arg0).getCode() != KeyCode.ENTER) {
						return;
					}
				}
				//TextFilter is not being used
				if(filterText.getText().equals("") || filterText.getText().equals("Keyword or Phrase filter")) {
					//Website selection filter is being used
					if(choice.getSelectionModel().getSelectedIndex() > 0) {
						names.clear();
						addRssFeedToStories(names, feeds.get(choice.getSelectionModel().getSelectedIndex() - 1));
					}
					//Fills names with all of the stories
					else {
						names.clear();
						fillStories(names);
					}
				}
				//Text filter is being used
				else {
					//Website filter is being used as well
					if(choice.getSelectionModel().getSelectedIndex() > 0) {
						names.clear();
						for(RssFeed f: feeds) {
							if(f.getNewsWebsite().equals(choice.getSelectionModel().getSelectedItem())) {
								for(Message m: f.getMessages()) {
									if(Utils.hasKeywordOrPhrase(filterText.getText(), m.getTitle())) {
										names.add(f.getNewsWebsite() + ": " + m.getTitle());
									}
								}
							}
						}
					}
					//Website filter is not being used
					else {
						names.clear();
						for(RssFeed f: feeds) {
							for(Message m: f.getMessages()) {
								if(Utils.hasKeywordOrPhrase(filterText.getText(), m.getTitle())) {
									names.add(f.getNewsWebsite() + ": " + m.getTitle());
								}
							}
						}
					}
				}
			}
			
		};
	}
	
	/**
	 * Creates a MouseEvent EventHandler that with the ListView and feeds opens a webpage in the users default browser 
	 * @param stories
	 * The ListView that is holding all of the stories for the user
	 * @return
	 * Returns an MouseEvent EventHandler that with stories and feed opens a webpage in the users default browser
	 */
	private EventHandler<MouseEvent> openPageEvent(ListView<String> stories){
		return new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				try {
					String selected = stories.getSelectionModel().getSelectedItem();
					String selectedSite = getSelectedSite(selected);
					selected = selected.substring(selectedSite.length() + 2, selected.length());
					for(RssFeed f: feeds) {
						if(f.getNewsWebsite().equals(selectedSite)) {
							for(Message m: f.getMessages()) {
								if(m.getTitle().equals(selected)) {
									Utils.openWebpage(m);
									break; 
								}
							}
							break;
						}
					}
				} catch (IOException | URISyntaxException e) {
					System.err.println("Ran into an issue opening the webpage\n" + e.toString() );
					e.printStackTrace();
				}
			}
			
		};
	}
	
	/**
	 * Gets the website name from the selected story. 
	 * @param selected
	 * The string that is pulled from stories
	 * @return
	 * Return the website name as a String.
	 */
	private String getSelectedSite(String selected) {
		String selectedSite = "";
		for(String s: selected.split(" ")) {
			if(!s.contains(":")){ // Sees if it isnt the last word
				if(!selectedSite.equals("")) { //Finds if the word is the first word.
					selectedSite += " " + s;
				}
				else {
					selectedSite += s;
				}
			}
			else {
				if(!selectedSite.equals("")) {
					selectedSite += " " + s;
				}
				else {
					selectedSite += s;
				}
				break;
			}
		}
		return selectedSite.replaceAll("[^a-zA-Z0-9 ]", ""); //removes all special characters
	}
	
	/**
	 * Event that updates feeds when f5 is pressed
	 * @param names
	 * The Observablelist of news articles
	 * @return
	 * An Event EventHandler
	 */
	private EventHandler<Event> updateItems(ObservableList<String> names){
		return new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				if(arg0.getClass().equals(KeyEvent.class)) {
					if(((KeyEvent)arg0).getCode() != KeyCode.F5) {
						return;
					}
				}
				updateWithProgressBar(names);
			}
			
		};
		
	}
	
	/**
	 * Updates feed with the most recent information
	 * Creates a popup that displays a progress bar
	 * @param names
	 * The observable list of article names that is being shown
	 */
	private void updateWithProgressBar(ObservableList<String> names) {
		int i = 0;
		Stage pop = new Stage();
		Text updateText = new Text("Update");
		ProgressBar pb = new ProgressBar(); 
		GridPane grid = new GridPane();
		pb.setMinSize(150, 30);
		grid.add(updateText, 0, 0);
		grid.add(pb, 0, 1);
		Scene scene = new Scene(grid);
		pop.setTitle("Update");
		pop.setScene(scene);
		pop.show();
		for(RssFeed f: feeds) {
			try {
				f = r.readSite(f.getUrl(), f.getNewsWebsite());
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
			pb.setProgress(feeds.size()/++i);
			System.out.println(i + "\n");
		}
	}
	
	
	/**
	 * The Main that launches the GUI
	 * @param args
	 * Args doesn't do anything
	 * @throws XMLStreamException
	 */
	public static void main(String[] args) throws XMLStreamException {
		launch(args);
	}

}
