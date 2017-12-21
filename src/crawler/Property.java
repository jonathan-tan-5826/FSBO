package crawler;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Property {
	private String _owner;
	private String _phoneNumber1;
	private String _phoneNumber2;
	private String _emailAddress;
	private String _url;
	private int _salePrice;
	private String _streetAddress;
	private String _city;
	private String _state;
	private int _zipcode;
	private String _countyName;
	private BigDecimal _latitude;
	private BigDecimal _longitude;
	private int _listingId;
	private int _numBedrooms;
	private int _numBathroomsFull;
	private int _numBathroomsPart;
	private int _garage;
	private String _type;
	private String _subtype;
	private String _lotSize;
	private String _squareFeet;
	private int _yearBuilt;
	private String _schoolDistrict;
	private String _subdivision;
	private String _pageSource;
	private String _description;
	private String _amenities;
	private Timestamp _crawlTime;
	
	public Property() {
		_owner = "";
		_phoneNumber1 = "";
		_phoneNumber2 = "";
		_emailAddress = "";
		_url = "";
		_salePrice = -1;
		_streetAddress = "";
		_city = "";
		_state = "";
		_zipcode = -1;
		_countyName = "";
		_latitude = null;
		_longitude = null;
		_listingId = -1;
		_numBedrooms = -1;
		_numBathroomsFull = -1;
		_numBathroomsPart = -1;
		_garage = -1;
		_type = "";
		_subtype = "";
		_lotSize = "";
		_squareFeet = "-1";
		_yearBuilt = -1;
		_schoolDistrict = "";
		_subdivision = "";
		_pageSource = "";
		_description = "";
		_amenities = "";
		_crawlTime = null;
	}
	
	public String getOwner() {
		return _owner;
	}
	
	public void setOwner(String value) {
		if (!_owner.equals(value)) {
			_owner = value;
		}
	}
	
	public String getPhoneNumber1() {
		return _phoneNumber1;
	}
	
	public void setPhoneNumber1(String value) {
		if (!_phoneNumber1.equals(value)) {
			_phoneNumber1 = value;
		}
	}
	
	public String getPhoneNumber2() {
		return _phoneNumber2;
	}
	
	public void setPhoneNumber2(String value) {
		if (!_phoneNumber2.equals(value)) {
			_phoneNumber2 = value;
		}
	}
	
	public String getEmailAddress() {
		return _emailAddress;
	}
	
	public void setEmailAddress(String value) {
		if (!_emailAddress.equals(value)) {
			_emailAddress = value;
		}
	}
	
	public String getUrl() {
		return _url;
	}

	public void setUrl(String value) {
		if (!_url.equals(value)) {
			_url = value;
		}
	}
	
	public int getSalePrice() {
		return _salePrice;
	}

	public void setSalePrice(int value) {
		if (_salePrice != value) {
			_salePrice = value;
		}
	}

	public String getStreetAddress() {
		return _streetAddress;
	}

	public void setStreetAddress(String value) {
		if (!_streetAddress.equals(value)) {
			_streetAddress = value;
		}
	}

	public String getCity() {
		return _city;
	}

	public void setCity(String value) {
		if (!_city.equals(value)) {
			_city = value;
		}
	}

	public String getState() {
		return _state;
	}

	public void setState(String value) {
		if (!_state.equals(value)) {
			_state = value;
		}
	}

	public int getZipcode() {
		return _zipcode;
	}

	public void setZipcode(int value) {
		if (_zipcode != value) {
			_zipcode = value;
		}
	}

	public String getCountyName() {
		return _countyName;
	}

	public void setCountyName(String value) {
		if (!_countyName.equals(value)) {
			_countyName = value;
		}
	}

	public BigDecimal getLatitude() {
		return _latitude;
	}
	
	public void setLatitude(BigDecimal value) {
		if (_latitude != value) {
			_latitude = value;
		}
	}
	
	public BigDecimal getLongitude() {
		return _longitude;
	}
	
	public void setLongitude(BigDecimal value) {
		if (_longitude != value) {
			_longitude = value;
		}
	}
	
	public int getListingId() {
		return _listingId;
	}

	public void setListingId(int value) {
		if (_listingId != value) {
			_listingId = value;
		}
	}

	public int getNumBedrooms() {
		return _numBedrooms;
	}

	public void setNumBedrooms(int value) {
		if (_numBedrooms != value) {
			_numBedrooms = value;
		}
	}

	public int getNumBathroomsFull() {
		return _numBathroomsFull;
	}

	public void setNumBathroomsFull(int value) {
		if (_numBathroomsFull != value) {
			_numBathroomsFull = value;
		}
	}
	
	public int getNumBathroomsPart() {
		return _numBathroomsPart;
	}

	public void setNumBathroomsPart(int value) {
		if (_numBathroomsPart != value) {
			_numBathroomsPart = value;
		}
	}

	public int getGarage() {
		return _garage;
	}

	public void setGarage(int value) {
		if (_garage != value) {
			_garage = value;
		}
	}

	public String getType() {
		return _type;
	}

	public void setType(String value) {
		if (!_type.equals(value)) {
			_type = value;
		}
	}

	public String getSubtype() {
		return _subtype;
	}

	public void setSubtype(String value) {
		if (!_subtype.equals(value)) {
			_subtype = value;
		}
	}

	public String getLotSize() {
		return _lotSize;
	}

	public void setLotSize(String value) {
		if (!_lotSize.equals(value)) {
			_lotSize = value;
		}
	}

	public String getSquareFeet() {
		return _squareFeet;
	}

	public void setSquareFeet(String value) {
		if (!_squareFeet.equals(value)) {
			_squareFeet = value;
		}
	}

	public int getYearBuilt() {
		return _yearBuilt;
	}

	public void setYearBuilt(int value) {
		if (_yearBuilt != value) {
			_yearBuilt = value;
		}
	}

	public String getSchoolDistrict() {
		return _schoolDistrict;
	}

	public void setSchoolDistrict(String value) {
		if (!_schoolDistrict.equals(value)) {
			_schoolDistrict = value;
		}
	}

	public String getSubdivision() {
		return _subdivision;
	}

	public void setSubdivision(String value) {
		if (!_subdivision.equals(value)) {
			_subdivision = value;
		}
	}
	
	public String getPageSource() {
		return _pageSource;
	}
	
	public void setPageSource(String value) {
		if (!_pageSource.equals(value)) {
			_pageSource = value;
		}
	}
	
	public Timestamp getCrawlTime() {
		return _crawlTime;
	}
	
	public void setCrawlTime(Timestamp value) {
		if (_crawlTime != value) {
			_crawlTime = value;
		}
	}
	
	public String getDescription() {
		return _description;
	}
	
	public void setDescription(String value) {
		if (!_description.equals(value)) {
			_description = value;
		}
	}
	
	public String getAmenities() {
		return _amenities;
	}
	
	public void setAmenities(String value) {
		if (!_amenities.equals(value)) {
			_amenities = value;
		}
	}
	
	public void print() {
		System.out.println("ownerName: " + getOwner());
		System.out.println("phoneNumber1: " + getPhoneNumber1());
		System.out.println("phoneNumber2: " + getPhoneNumber2());
		System.out.println("emailAddress: " + getEmailAddress());
		System.out.println("url: " + getUrl());
		System.out.println("price: " + getSalePrice());
		System.out.println("address: " + getStreetAddress());
		System.out.println("city: " + getCity());
		System.out.println("state: " + getState());
		System.out.println("zipcode: " + getZipcode());
		System.out.println("countyName: " + getCountyName());
		System.out.println("latitude: " + getLatitude());
		System.out.println("longitude: " + getLongitude());
		System.out.println("listingId: " + getListingId());
		System.out.println("bedrooms: " + getNumBedrooms());
		System.out.println("bathroomsFull: " + getNumBathroomsFull());
		System.out.println("bathroomsPart: " + getNumBathroomsPart());
		System.out.println("garage: " + getGarage());
		System.out.println("type: " + getType());
		System.out.println("subtype: " + getSubtype());
		System.out.println("lotSize: " + getLotSize());
		System.out.println("squareFeet: " + getSquareFeet());
		System.out.println("yearBuilt: " + getYearBuilt());
		System.out.println("schoolDistrict: " + getSchoolDistrict());
		System.out.println("subdivision: " + getSubdivision());
		System.out.println("pageSource: " + getPageSource().substring(0, Math.min(getPageSource().length(), 100)));
		System.out.println("description: " + getDescription());
		System.out.println("amenities: " + getAmenities());
		System.out.println("crawlTime: " + getCrawlTime());
	}
}
