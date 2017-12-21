package crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import crawler.Property;

public class Crawler {
	private static HtmlUnitDriver _primaryDriver, _secondaryDriver;
	private static Connection _connection;
//	private static FirefoxProfile _profile;
	private static final String[] GROUP_ONE = { "AL", "AR", "CA"};
	private static final String[] GROUP_TWO = { "AK", "AZ", "MI", "NY" };
	private static final String[] GROUP_THREE = { "CO", "GA", "PA", "UT" };
	private static final String[] GROUP_FOUR = { "MD", "ME", "NM", "TX" };
	private static final String[] GROUP_FIVE = { "CT", "IA", "MS", "VA", "WA", "WY" };
	private static final String[] GROUP_SIX = { "DC", "MO", "MT", "NC", "ND", "NE", "NH" };
	private static final String[] GROUP_SEVEN = { "DE", "FL", "HI", "IL", "KS", "RI" };
	private static final String[] GROUP_EIGHT = { "ID", "IN", "KY", "LA", "MA", "OR" };
	private static final String[] GROUP_NINE = { "MN", "NJ", "NV", "OK", "PR", "SC", "SD", "VT" };
	private static final String[] GROUP_TEN = { "OH", "TN", "WI", "WV" };

	/**
	 * @title main
	 * @param args<String[]>
	 * @return
	 * @desc Main function
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Error: Not enough arguments passed in.");
			System.out.println("Correct usage: ./crawler <selection/state abbrev>");
			System.exit(1);
		}
		_connection = getConnection();
		_primaryDriver = null;
		_secondaryDriver = null;
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
	    java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
	    java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		
//		File file = new File("G:/Eclipse/eclipse/chromedriver.exe");
//		System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());

		String selection = args[0];
		if (selection.equals("all")) {
			crawlStates(GROUP_ONE);
			crawlStates(GROUP_TWO);
			crawlStates(GROUP_THREE);
			crawlStates(GROUP_FOUR);
			crawlStates(GROUP_FIVE);
			crawlStates(GROUP_SIX);
			crawlStates(GROUP_SEVEN);
			crawlStates(GROUP_EIGHT);
			crawlStates(GROUP_NINE);
			crawlStates(GROUP_TEN);
		} else if (selection.equals("1")) {
			crawlStates(GROUP_ONE);
		} else if (selection.equals("2")) {
			crawlStates(GROUP_TWO);
		} else if (selection.equals("3")) {
			crawlStates(GROUP_THREE);
		} else if (selection.equals("4")) {
			crawlStates(GROUP_FOUR);
		} else if (selection.equals("5")) {
			crawlStates(GROUP_FIVE);
		} else if (selection.equals("6")) {
			crawlStates(GROUP_SIX);
		} else if (selection.equals("7")) {
			crawlStates(GROUP_SEVEN);
		} else if (selection.equals("8")) {
			crawlStates(GROUP_EIGHT);
		} else if (selection.equals("9")) {
			crawlStates(GROUP_NINE);
		} else if (selection.equals("10")) {
			crawlStates(GROUP_TEN);
		} else if (selection.length() == 2) {
			crawlState(selection);
		} else {
			System.out.println("Invalid argument passed in. Valid arguments: all, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, state abbrev");
			System.out.println("Application exiting.");
			System.exit(1);
		}
		System.out.println("Crawling completed (100%).");

		tryClosePrimaryDriver();
		tryCloseSecondaryDriver();
	}

	/**
	 * @title crawlStates
	 * @param crawlStates<String[]>
	 * @return
	 * @desc Calls crawlState() on each crawlState<String> in
	 *       crawlStates<String[]>
	 */
	public static void crawlStates(String[] crawlStates) throws Exception {
		try {
			for (String crawlState : crawlStates) {
				crawlState(crawlState);
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * @title crawlState
	 * @param crawlState<String>
	 * @return
	 * @desc Crawls FSBO website for listing links obtained from crawlState
	 *       search results and writes the page sources of listing links
	 *       to text files.
	 */
	public static void crawlState(String crawlState) throws Exception {
		try {
			System.out.println("Crawling " + crawlState);
			_primaryDriver = new CustomHtmlUnitDriver();
			_primaryDriver.setJavascriptEnabled(true);
//			_primaryDriver = new ChromeDriver();
			_primaryDriver.get("http://fsbo.com/");
			Thread.sleep(2500);

			WebElement searchBox = _primaryDriver.findElement(By.id("searchQuery"));
			searchBox.sendKeys(crawlState);
			WebElement searchButton = _primaryDriver.findElement(By.id("basicSearchButtonmobile"));
			searchButton.click();
			Thread.sleep(3000);
			System.out.println("URL: " + _primaryDriver.getCurrentUrl());

			boolean nextPageDoesExist = true;
			while (nextPageDoesExist) {
				// Get all links on page
				List<WebElement> linkElements = _primaryDriver.findElements(
						By.xpath("//a[starts-with(@href, 'http://fsbo.com/listings/listings/show/id/')]"));
				List<String> hrefLinks = new ArrayList<String>();
				
				// Filter out duplicate links
				for (int i = 0; i < linkElements.size(); ++i) {
					String currLink = linkElements.get(i).getAttribute("href");
					boolean isDuplicateLink = false;

					for (int j = 0; j < hrefLinks.size() && !isDuplicateLink; ++j) {
						String linkToCompare = hrefLinks.get(j);

						if (currLink.equalsIgnoreCase(linkToCompare)) {
							isDuplicateLink = true;
						} else {
							isDuplicateLink = false;
						}
					}

					if (!isDuplicateLink) {
						hrefLinks.add(currLink);
					}
				}
				
				// For each unique link, scrape and store page source
				for (int i = 0; i < (hrefLinks.size()); ++i) {
					String currLink = hrefLinks.get(i);

					if ((currLink != null) && (!currLink.isEmpty())) {
						scrapePageSourceFromListingUrl(crawlState, currLink);
					}
				}

				// If "next" exists on page, select it to view next page.
				if (nextPageDoesExist(_primaryDriver)) {
					nextPageDoesExist = true;
					WebElement nextButton = _primaryDriver.findElement(By.className("nextPage"));
					nextButton.click();
					Thread.sleep(3000);
				} else {
					nextPageDoesExist = false;
				}
			}

			// Output finished crawling
			System.out.println("Finished crawling state " + crawlState + "... closing driver.");
			_primaryDriver.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * @title parseAndSaveDataFromFile
	 * @param file<File>
	 * @return
	 * @desc Parses file<File> to get property data and saves data to database
	 */
	public static void parseAndSaveDataFromPageSource(int listingId, String url, String pageSource) throws Exception {
		Document document = Jsoup.parse(pageSource);

		Property property = new Property();
		property.setUrl(url);
		property.setCrawlTime(getCurrentTimestamp());
		property.setPageSource(pageSource);
		property = getPropertyWithPropertyTableInformationFromDocument(property, document);

		// Check if property is a home (type != "Land/Lot")
		// If type == "Land/Lot", continue
		// Else set price, address, and save to database
		if (property.getType().equals("Land/Lot")) {
			System.out.println("Property is not a home. Closing " + listingId);
			return;
		} else {
			// Set property description
			property = getPropertyWithDescriptionFromDocument(property, document);

			// Set property amenities
			property = getPropertyWithAmenitiesFromDocument(property, document);

			// Set property contact information
			property = getPropertyWithContactInformationFromDocument(property, document);

			// Set property price
			property.setSalePrice(getPriceFromDocument(document));

			// Set address details
			property = getPropertyWithAddressDetailsFromDocument(property, document);

			property.print();

			// Insert property data to database
			savePropertyToDatabase(property);

			// Output finished crawling
			System.out.println("Finished parsing " + listingId);
		}
	}
	
	/**
	 * @title savePropertyToDatabase
	 * @param property<Property>
	 * @return
	 * @desc Inserts property's data to database
	 */
	public static void savePropertyToDatabase(Property property) throws SQLException {
		System.out.println("Entered savePropertyToDatabase");

		String sqlStatement = "INSERT INTO fsbo_home(owner, phone_number1, phone_number2, email_address, url, sale_price, street_address, city, state, zipcode, county_name, latitude, longitude, listingId, num_bedrooms, num_bathrooms_full, num_bathrooms_part, garage, type, subtype, lot_size, square_feet, year_built, school_district, subdivision, description, amenities, crawl_time) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement preparedStatement = _connection.prepareStatement(sqlStatement);

		// 1
		if (property.getOwner().length() > 0) {
			preparedStatement.setString(1, property.getOwner());
		} else {
			preparedStatement.setString(1, null);
		}
		// 2
		if (property.getPhoneNumber1().length() > 0) {
			preparedStatement.setString(2, property.getPhoneNumber1());
		} else {
			preparedStatement.setString(2, null);
		}
		// 3
		if (property.getPhoneNumber2().length() > 0) {
			preparedStatement.setString(3, property.getPhoneNumber2());
		} else {
			preparedStatement.setString(3, null);
		}
		// 4
		if (property.getEmailAddress().length() > 0) {
			preparedStatement.setString(4, property.getEmailAddress());
		} else {
			preparedStatement.setString(4, null);
		}
		// 5
		preparedStatement.setString(5, property.getUrl());
		// 6
		preparedStatement.setInt(6, property.getSalePrice());
		// 7
		if (property.getStreetAddress().length() > 0) {
			preparedStatement.setString(7, property.getStreetAddress());
		} else {
			preparedStatement.setString(7, null);
		}
		// 8
		if (property.getCity().length() > 0) {
			preparedStatement.setString(8, property.getCity());
		} else {
			preparedStatement.setString(8, null);
		}
		// 9
		if (property.getState().length() > 0) {
			preparedStatement.setString(9, property.getState());
		} else {
			preparedStatement.setString(9, null);
		}
		// 10
		preparedStatement.setInt(10, property.getZipcode());
		// 11
		if (property.getCountyName().length() > 0) {
			preparedStatement.setString(11, property.getCountyName());
		} else {
			preparedStatement.setString(11, null);
		}
		// 12
		preparedStatement.setBigDecimal(12, property.getLatitude());
		// 13
		preparedStatement.setBigDecimal(13, property.getLongitude());
		// 14
		preparedStatement.setInt(14, property.getListingId());
		// 15
		preparedStatement.setInt(15, property.getNumBedrooms());
		// 16
		preparedStatement.setInt(16, property.getNumBathroomsFull());
		// 17
		preparedStatement.setInt(17, property.getNumBathroomsPart());
		// 18
		preparedStatement.setInt(18, property.getGarage());
		// 19
		if (property.getType().length() > 0) {
			preparedStatement.setString(19, property.getType());
		} else {
			preparedStatement.setString(19, null);
		}
		// 20
		if (property.getSubtype().length() > 0) {
			preparedStatement.setString(20, property.getSubtype());
		} else {
			preparedStatement.setString(20, null);
		}
		// 21
		if (property.getLotSize().length() > 0) {
			preparedStatement.setString(21, property.getLotSize());
		} else {
			preparedStatement.setString(21, null);
		}
		// 22
		if (property.getSquareFeet().length() > 0) {
			preparedStatement.setString(22, property.getSquareFeet());
		} else {
			preparedStatement.setString(22, null);
		}
		// 23
		preparedStatement.setInt(23, property.getYearBuilt());
		// 24
		if (property.getSchoolDistrict().length() > 0) {
			preparedStatement.setString(24, property.getSchoolDistrict());
		} else {
			preparedStatement.setString(24, null);
		}
		// 25
		if (property.getSubdivision().length() > 0) {
			preparedStatement.setString(25, property.getSubdivision());
		} else {
			preparedStatement.setString(25, null);
		}
		// 26
		if (property.getDescription().length() > 0) {
			preparedStatement.setString(26, property.getDescription());
		} else {
			preparedStatement.setString(26, null);
		}
		// 27
		if (property.getAmenities().length() > 0) {
			preparedStatement.setString(27, property.getAmenities());
		} else {
			preparedStatement.setString(27, null);
		}
		// 28
		preparedStatement.setTimestamp(28, property.getCrawlTime());
		preparedStatement.executeUpdate();
		preparedStatement.close();
	}
	
	/**
	 * @title getPropertyWithPropertyTableInformationFromDocument
	 * @param property<Property>,
	 *            document<Document>
	 * @return property<Property> with added property information extracted from
	 *         document
	 */
	public static Property getPropertyWithPropertyTableInformationFromDocument(Property property, Document document) {
		System.out.println("Entered getPropertyWithPropertyTableInformationFromDocument");

		Element table = document.select("table[class=listing-data table table-striped]").first();
		float bathrooms = -1;
		int bathroomsFull = -1;
		int bathroomsPart = -1;

		for (Element row : table.select("tr")) {
			Elements tds = row.select("td");
			if (tds.size() > 1) {
				String title = tds.get(0).text().trim();
				String value = tds.get(1).text().trim();
				if (title.equalsIgnoreCase("Listing ID:")) {
					property.setListingId(convertToInt(value));
				} else if (title.equalsIgnoreCase("Bedrooms:")) {
					property.setNumBedrooms(convertToInt(value));
				} else if (title.equalsIgnoreCase("Bathrooms:")) {
					bathrooms = convertToFloat(value);
					if (bathrooms >= 0) {
						if (bathrooms % 1 != 0) {
							bathrooms -= 0.5;
							bathroomsPart = 1;
						} else {
							bathroomsPart = 0;
						}
						bathroomsFull = Math.round(bathrooms);
					}
					property.setNumBathroomsFull(bathroomsFull);
					property.setNumBathroomsPart(bathroomsPart);
				} else if (title.equalsIgnoreCase("Garage:")) {
					float rawValue = convertToFloat(value);
					property.setGarage((int) Math.floor(rawValue));
				} else if (title.equalsIgnoreCase("Type:")) {
					property.setType(value);
				} else if (title.equalsIgnoreCase("Subtype:")) {
					property.setSubtype(value);
				} else if (title.equalsIgnoreCase("Lot Size:")) {
					property.setLotSize(value);
				} else if (title.equalsIgnoreCase("Sq. Feet:")) {
					property.setSquareFeet(value);
				} else if (title.equalsIgnoreCase("Year Built:")) {
					property.setYearBuilt(convertToInt(value));
				} else if (title.equalsIgnoreCase("School District:")) {
					property.setSchoolDistrict(value);
				} else if (title.equalsIgnoreCase("Subdivision:")) {
					property.setSubdivision(value);
				}
			}
		}

		return property;
	}

	/**
	 * @title getPropertyWithDescriptionFromDocument
	 * @param property<Property>,
	 *            document<Document>
	 * @return property<Property> with added property description extracted from
	 *         document
	 */
	public static Property getPropertyWithDescriptionFromDocument(Property property, Document document) {
		String description = document.select("div.hidden-xs.property-description").first().text().trim();
		property.setDescription(description);

		return property;
	}

	/**
	 * @title getPropertyWithAmenitiesFromDocument
	 * @param property<Property>,
	 *            document<Document>
	 * @return property<Property> with added property amenities extracted from
	 *         document
	 */
	public static Property getPropertyWithAmenitiesFromDocument(Property property, Document document) {
		Elements amenities_uls = document.select("div.hidden-xs.more-amenities > ul");

		if (amenities_uls.size() > 0) {
			Element amenities_ul = amenities_uls.first();

			Elements amenities_li = amenities_ul.select("li");
			String amenities = "";

			for (int i = 0; i < amenities_li.size(); ++i) {
				String value = amenities_li.get(i).text().trim();
				amenities += value;

				if (i != amenities_li.size() - 1) {
					amenities += ";";
				}
			}
			property.setAmenities(amenities);
		}

		return property;
	}

	/**
	 * @title getPropertyWithContactInformationFromDocument
	 * @param property<Property>,
	 *            document<Document>
	 * @return property<Property> with added contact information extracted from
	 *         document
	 */
	public static Property getPropertyWithContactInformationFromDocument(Property property, Document document) {
		System.out.println("Entered getPropertyWithContactInformationFromDocument");
		Element contactModalIn = document.select("div#sellerModal").first();

		if (!contactModalIn.text().isEmpty()) {
			Element contactModalBody = contactModalIn.select("div.modal-dialog div.modal-content div.modal-body")
					.first();
			if (!contactModalBody.text().isEmpty()) {
				Element contactModalBodyDiv = contactModalBody.select("div").first();
				Elements divs = contactModalBodyDiv.select("div");
				for (int i = 0; i < divs.size(); ++i) {
					String message = divs.get(i).text().trim();
					String value = "";
					if (message.equalsIgnoreCase("Contact:")) {
						value = divs.get(++i).text().trim();
						property.setOwner(value);
					} else if (message.equalsIgnoreCase("Phone:")) {
						value = divs.get(++i).text().trim();
						String filteredValue = getNumericalCharacters(value);

						if (isValidPhoneNumber(filteredValue)) {
							if (property.getPhoneNumber1().isEmpty()) {
								property.setPhoneNumber1(filteredValue);
							} else {
								property.setPhoneNumber2(filteredValue);
							}
						} else {
							String temp = getEmailAddressFromText(value);

							if (temp.length() > 0) {
								property.setEmailAddress(temp);
							}
						}
					} else if (message.equalsIgnoreCase("Email:")) {
						value = divs.get(++i).text().trim();
						String temp = getEmailAddressFromText(value);

						if (temp.length() > 0) {
							property.setEmailAddress(temp);
						}
					} else if (message.equalsIgnoreCase("Email Address:")) {
						value = divs.get(++i).text().trim();
						String temp = getEmailAddressFromText(value);

						if (temp.length() > 0) {
							property.setEmailAddress(temp);
						}
					}
				}

				// Check if email address is in description
				String emailAddress = getEmailAddressFromPropertyDescription(property, document);

				if (emailAddress.length() > 0) {
					property.setEmailAddress(getEmailAddressFromPropertyDescription(property, document));
				}
			}
		}

		return property;
	}

	/**
	 * @title getPropertyWithAddressDetailsFromDocument
	 * @param property<Property>,
	 *            document<Document>
	 * @return property<Property> with added address information extracted from
	 *         document
	 * @throws Exception
	 */
	public static Property getPropertyWithAddressDetailsFromDocument(Property property, Document document)
			throws Exception {
		System.out.println("Entered getPropertyWithAddressDetailsFromDocument");

		Elements addressMobileSpan = document.select("div.address-copy span.address-mobile");
		if (addressMobileSpan.text().length() > 0) {
			Elements aHrefs = addressMobileSpan.select("a[href]");
			String homeAddress = "";

			if (aHrefs.size() > 0) {
				// googleMapUrl exists
				Element aHref = aHrefs.first();
				homeAddress = aHref.html();
			} else {
				// googleMapUrl does not exist
				homeAddress = addressMobileSpan.html();
			}

			if ((homeAddress != null) && (!homeAddress.isEmpty())) {
				property = getPropertyWithAddressDetailsFromHomeAddress(property, homeAddress);
			}
		}

		property.setCountyName(getCountyNameFromDocument(document));

		BigDecimal latitudeLongitude[] = getLatitudeLongitudeFromPropertyAddress(property);
		property.setLatitude(latitudeLongitude[0]);
		property.setLongitude(latitudeLongitude[1]);

		return property;
	}

	/**
	 * @title getPropertyWithAddressDetailsFromHomeAddress
	 * @param property<Property>,
	 *            homeAddress<String>
	 * @return property<Property> with added address information extracted from
	 *         homeAddress<String>
	 * @throws Exception
	 */
	public static Property getPropertyWithAddressDetailsFromHomeAddress(Property property, String homeAddress)
			throws Exception {
		String[] homeAddressParts = homeAddress.split("<br>");
		String address = "";
		String city = "";
		String state = "";
		String zipcode = "";

		if (homeAddressParts != null) {
			if (homeAddressParts.length > 1) {
				address = homeAddressParts[0].trim();
			}

			String cityStateZipcode = homeAddressParts[homeAddressParts.length - 1].trim();
			String[] cityStateZipcodeParts = cityStateZipcode.split(",");
			city = cityStateZipcodeParts[0].trim();

			String[] stateZipcodeParts = cityStateZipcodeParts[cityStateZipcodeParts.length - 1].trim().split(" ");
			state = stateZipcodeParts[0].trim();
			zipcode = stateZipcodeParts[1].trim();
		}

		property.setStreetAddress(address);
		property.setCity(city);
		property.setState(state);
		property.setZipcode(convertToInt(zipcode));

		return property;
	}

	/**
	 * @title getLatitudeLongitudeFromPropertyAddress
	 * @param property<Property>
	 * @return result<BigDecimal[]> which contains latitude, longitude obtained
	 *         by google map api
	 * @throws Exception
	 */
	public static BigDecimal[] getLatitudeLongitudeFromPropertyAddress(Property property) throws Exception {
		BigDecimal result[] = new BigDecimal[2];
		BigDecimal latitude = null;
		BigDecimal longitude = null;
		String address = property.getStreetAddress();
		String city = property.getCity();
		String state = property.getState();
		int zipcode = property.getZipcode();
		String fullAddress = "";

		if (!address.isEmpty()) {
			fullAddress += address + ", ";
		}
		fullAddress += city + ", " + state + " " + zipcode;
		fullAddress = fullAddress.replaceAll(" ", "%20");
		String googleApiUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + fullAddress
				+ "&sensor=true+CA&key=AIzaSyBgvoZkIBE55I5iBk2lLJMmPknbhDZ5B9g";
		System.out.println(fullAddress);
		URL url = new URL(googleApiUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		String output;
		String out = "";
		while ((output = br.readLine()) != null) {
			out += output;
		}

		JSONObject json = (JSONObject) JSONSerializer.toJSON(out);
		String status = json.getString("status");
		if (status.equalsIgnoreCase("OK")) {
			JSONArray results = json.getJSONArray("results");

			if (results.size() > 0) {
				for (int i = 0; i < results.size(); i++) {
					json = results.getJSONObject(i);
					JSONObject geoObject = json.getJSONObject("geometry");
					JSONObject location = geoObject.getJSONObject("location");
					String s_latitude = location.getString("lat");
					String s_longitude = location.getString("lng");

					if (s_latitude != "") {
						latitude = new BigDecimal(s_latitude);
					}
					if (s_longitude != "") {
						longitude = new BigDecimal(s_longitude);
					}
				}
			}
		} else {
			if (!status.equalsIgnoreCase("ZERO_RESULTS")) {
				throw new Exception("Error from Google API. Status != 'OK' and results may exist.");
			}
		}
		conn.disconnect();

		result[0] = latitude;
		result[1] = longitude;

		return result;
	}

	/**
	 * @title getCountyNameFromDocument
	 * @param document<Document>
	 * @return Extracted countyName<String> if found in document, empty string
	 *         otherwise
	 */
	public static String getCountyNameFromDocument(Document document) {
		System.out.println("Entered getCountyNameFromDocument");

		if (document.select("div.address-copy").text().length() > 0) {
			String addressCopyHtml = document.select("div.address-copy").first().html();
			String[] addressCopyHtmlParts = addressCopyHtml.split("<br>");
			String countyName = addressCopyHtmlParts[addressCopyHtmlParts.length - 1];

			return countyName;
		} else {
			return "";
		}
	}

	/**
	 * @title getPriceFromDocument
	 * @param document<Document>
	 * @return Extracted price<int> if found in document, 0 otherwise
	 */
	public static int getPriceFromDocument(Document document) {
		System.out.println("Entered getPriceFromDocument");

		if (document.select("div.address-copy span.price").text().length() > 0) {
			String rawPrice = document.select("div.address-copy span.price").text().trim();

			return convertToInt(rawPrice);
		} else {
			return 0;
		}
	}

	/**
	 * @title getEmailAddressFromPropertyDescription
	 * @param property<Property>
	 * @return Extracted email address<String> if found in document, empty
	 *         string otherwise
	 */
	public static String getEmailAddressFromPropertyDescription(Property property, Document document) {
		String description = property.getDescription();
		String emailAddress = "";

		if (description.isEmpty()) {
			property = getPropertyWithDescriptionFromDocument(property, document);
		}

		emailAddress = getEmailAddressFromText(description);

		return emailAddress;
	}

	/**
	 * @title getEmailAddressFromText
	 * @param text<String>
	 * @return Extracted email address if found in text, empty string otherwise
	 */
	public static String getEmailAddressFromText(String text) {
		String emailAddress = "";
		Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9]+").matcher(text);

		while (m.find()) {
			emailAddress = m.group();

			if (!emailAddress.isEmpty()) {
				break;
			}
		}

		return emailAddress;
	}

	/**
	 * @title getCurrentTimestamp
	 * @param
	 * @return currentTimestamp<Timestamp>
	 */
	private static Timestamp getCurrentTimestamp() {
		System.out.println("Entered getCurrentTimestamp");

		Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		return currentTimestamp;
	}

	/**
	 * @title getNumericalCharacters
	 * @param value<String>
	 * @return value<String> with numerical characters only
	 */
	public static String getNumericalCharacters(String value) {
		return value.replaceAll("[^0-9]", "");
	}

	/**
	 * @title getNumericalCharactersAndDecimals
	 * @param value<String>
	 * @return value<String> with numerical characters and decimals only
	 */
	public static String getNumericalCharactersAndDecimals(String value) {
		return value.replaceAll("[^0-9.]", "");
	}

	/**
	 * @title convertToInt
	 * @param value<String>
	 * @return Converts value<String> to an integer after removing all
	 *         non-numerical characters
	 */
	public static int convertToInt(String value) {
		String filteredValue = getNumericalCharacters(value);

		if (filteredValue.length() > 0) {
			return Integer.parseInt(filteredValue);
		} else {
			return -1;
		}
	}

	/**
	 * @title convertToFloat
	 * @param value<String>
	 * @return Converts value<String> to a float after removing all
	 *         non-numerical characters
	 */
	public static float convertToFloat(String value) {
		String filteredValue = getNumericalCharactersAndDecimals(value);

		if (filteredValue.length() > 0) {
			return Float.parseFloat(filteredValue);
		} else {
			return -1;
		}
	}

	/**
	 * @title isValidPhoneNumber
	 * @param value<String>
	 * @return True if value<String> is a valid phone number, false otherwise
	 */
	public static boolean isValidPhoneNumber(String value) {
		String filteredValue = getNumericalCharacters(value);

		return (filteredValue.length()) >= 10 && (filteredValue.length() <= 11);
	}
	
	/**
	 * @title writeStringToFile
	 * @param fileName<String>,
	 *            text<String>
	 * @return
	 * @desc Creates fileName, if it does not exist, in /pagesources and writes
	 *       text to file.
	 */
	public static void writeStringToFile(String fileName, String text) throws Exception {
		String directory = "./pagesources/other/";
		
		writeStringToFile(directory, fileName, text);
	}

	/**
	 * @title writeStringToFile
	 * @param fileName<String>,
	 *            text<String>
	 * @return
	 * @desc Creates fileName, if it does not exist, in /pagesources and writes
	 *       text to file.
	 */
	public static void writeStringToFile(String directory, String fileName, String text) throws Exception {
		String fullFileName = directory + fileName;
		
		File file = new File(fullFileName);
		FileUtils.writeStringToFile(file, text, "UTF-8");
	}

	/**
	 * @title getListingIdFromUrl
	 * @param url<String>
	 * @return listingId<String> extracted from the FSBO Url
	 * @desc FSBO Listing Url Format:
	 *       http://fsbo.com/listings/listings/show/id/183006/
	 */
	public static String getListingIdFromUrl(String url) {
		String[] urlParts = url.split("/");
		String listingId = "";

		for (int i = 0; i < urlParts.length; ++i) {
			if (urlParts[i].equalsIgnoreCase("id")) {
				listingId = urlParts[i+1];
				break;
			}
		}

		return listingId;
	}

	/**
	 * @title scrapePageSourceFromListingUrl
	 * @param url<String>
	 * @return
	 * @desc Gets the page source from the url<String>,
	 *       parses the page source for necessary information,
	 *       and writes it to a text file @ "/pagesources/STATE/listingId.txt"
	 */
	public static void scrapePageSourceFromListingUrl(String state, String url) throws Exception {
		System.out.println("Entered savePageSourceFromListingUrl");
		// Open link in new Chrome page
//		_secondaryDriver = new ChromeDriver();
		_secondaryDriver = new CustomHtmlUnitDriver();
		_secondaryDriver.setJavascriptEnabled(true);
		_secondaryDriver.get(url);
		Thread.sleep(2500);

		// Get page source to work offline
		String pageSource = _secondaryDriver.getPageSource();
		String listingId = getListingIdFromUrl(url);
		String fileName = listingId + ".txt";
		String directory = "./pagesources/" + state + "/";
		parseAndSaveDataFromPageSource(convertToInt(listingId), url, pageSource);
		writeStringToFile(directory, fileName, pageSource);
		
		_secondaryDriver.close();
	}

	/**
	 * @title nextPageDoesExist
	 * @param driver<WebDriver>
	 * @return True if "nextPage" element is found in driver, false otherwise
	 */
	public static boolean nextPageDoesExist(WebDriver driver) {
		System.out.println("Entered nextPageDoesExist");

		return driver.findElements(By.className("nextPage")).size() > 0;
	}
	
	/**
	 * @title getConnection
	 * @param
	 * @return connection<Connection> to MySQL database
	 */
	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String urldb = "jdbc:mysql://localhost/homeDB";
		String user = "jonathan";
		String password = "password";
		Connection connection = DriverManager.getConnection(urldb, user, password);
		return connection;
	}

	/**
	 * @title tryClosePrimaryDriver
	 * @param
	 * @return
	 * @desc Ends _primaryDriver<WebDriver>'s session if session is not null
	 */
	public static void tryClosePrimaryDriver() {
		if (_primaryDriver != null) {
			_primaryDriver.close();
		}
	}

	/**
	 * @title tryCloseSecondaryDriver
	 * @param
	 * @return
	 * @desc Ends _secondaryDriver<WebDriver>'s session if session is not null
	 */
	public static void tryCloseSecondaryDriver() {
		if (_secondaryDriver != null) {
			_secondaryDriver.close();
		}
	}
}
