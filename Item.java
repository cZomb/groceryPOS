package rrcGrocery;

import java.io.Serializable;

// Item window handles all the items used on invoices
public class Item implements Serializable {
	private String itemNo; // stores sku number
	private Double price; // stores price
	private String description; // stores the item's description

	// categories of items
	static public enum Category {
		PRODUCE, MEAT, SEAFOOD, DELI, BAKERY;
	};

	// stores category
	private Category cat;

	// constructor accepts item number, description, category and price
	public Item(String itemNo, String description, Category cat, Double price) {
		this.itemNo = itemNo; // set item number
		this.description = description; // set description
		this.cat = cat; // set category
		this.price = price; // set price
	}

	// returns a string formatted for use with the Item List window's table
	public String[] getItemTableString() {
		String[] s = { itemNo.toString(), description.toString(), getCatString(this.cat), price.toString() };
		return s;
	}

	// returns a string formatted for use with the SaleLines table
	public String[] getSaleLineString() {
		String[] s = { itemNo.toString(), description.toString(), getCatString(this.cat), "1", price.toString() };
		return s;
	}

	// return the appropriate category string based on category
	public String getCatString(Category c) {
		String s = new String();

		switch (c) {
		case PRODUCE:
			s = "Produce";
			break;
		case MEAT:
			s = "Meat";
			break;
		case SEAFOOD:
			s = "Seafood";
			break;
		case DELI:
			s = "Deli";
			break;
		case BAKERY:
			s = "Bakery";
			break;
		}

		return s;
	}

	// getters and setters
	public String getItemNo() {
		return this.itemNo;
	}

	public void setItemNo(String s) {
		this.itemNo = s;
	}

	public Double getPrice() {
		return this.price;
	}

	public void setPrice(double p) {
		this.price = p;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String s) {
		this.description = s;
	}

	public Category getCat() {
		return this.cat;
	}

	public static Category getCat(String s) {
		// create return category and set default to PRODUCE
		Category c = Category.PRODUCE;

		// check the string parameter and set the return value appropriately
		switch (s) {
		case "Meat":
			c = Category.MEAT;
			break;
		case "Seafood":
			c = Category.SEAFOOD;
			break;
		case "Deli":
			c = Category.DELI;
			break;
		case "Bakery":
			c = Category.BAKERY;
			break;
		}
		
		return c;
	}

	public void setCat(Category c) {
		this.cat = c;
	}
}
