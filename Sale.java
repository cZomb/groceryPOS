package rrcGrocery;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

// stores info for all posted sales/invoices
public class Sale implements Serializable {
	// stores unique invoice number of sale
	private int invoiceNumber;
	public static int nextInvoiceNumber;

	// stores purchaseDate
	private LocalDate purchaseDate;

	// arraylist to store saleLines
	ArrayList<SaleLine> saleLines;

	// default constructor
	public Sale() {
		// initialize arraylist
		saleLines = new ArrayList<SaleLine>();

		// set a blank line
		saleLines.add(new SaleLine());
	}

	// invoice number getter
	public int getInvoiceNumber() {
		return invoiceNumber;
	}

	// purchase date getter
	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	// returns the string array used on the Sale table
	public String[][] getSaleString() {
		String[][] s = new String[saleLines.size()][4];

		for (int i = 0; i < saleLines.size(); i++) {
			// call Item's getString() method to format and create row data
			s[i] = saleLines.get(i).getString();
		}

		return s;
	}

	// returns a formated string for the SaleHistory window's table
	public String[][] getSaleHistoryString() {
		String[][] s = new String[saleLines.size()][6]; // create a string instance of the correct size
		Integer iInvNum = invoiceNumber;

		// loop through saleLines and format the string
		for (int i = 0; i < saleLines.size(); i++) {
			Sale.SaleLine sl = saleLines.get(i);

			s[i] = new String[] { iInvNum.toString(), purchaseDate.toString(), sl.itemNo, sl.cat, sl.qty, sl.price,
					sl.amount };
		}

		// return the formated string array
		return s;
	}

	// add a new SaleLine to the sale with a string parameter
	public SaleLine newSaleLine(String[] s) {
		return new SaleLine(s);
	}

	// add a new, blank SaleLine with no parameter
	public SaleLine newSaleLine() {
		return new SaleLine();
	}

	// inner class to handle each line of the invoice
	public class SaleLine implements Serializable {
		// stores Item Number, description, category, quantity, price, and total
		// amount(qty*price)
		String itemNo, desc, cat, qty, price, amount;

		// default constructor sets all cells blank
		SaleLine() {
			itemNo = desc = cat = qty = price = amount = "";
		}

		// string parameter constructor sets values based on string array values of
		// parameter
		SaleLine(String[] s) {
			itemNo = s[0];
			desc = s[1];
			cat = s[2];
			qty = s[3];
			price = s[4];

			// convert strings to double, multiply, then convert back to string
			amount = Double.valueOf(Double.parseDouble(qty) * Double.parseDouble(price)).toString();
		}

		// returns the formated saleLine string for the NewSaleWindow table
		String[] getString() {
			String[] s = { itemNo, desc, cat, qty, price, amount };
			return s;
		}

		// returns the formated SaleHistoryWindow string
		public String[] getSHString() {
			Integer inv = invoiceNumber;
			String[] s = { purchaseDate.toString(), inv.toString(), itemNo, cat, qty, price, amount };
			return s;
		}

		// updates amount cell
		void updateAmount() {
			Double q = Double.valueOf(qty);
			Double p = Double.valueOf(price);
			Double a = q * p;

			this.amount = String.format("%.2f", a);
		}
	}

	// purchase date setter
	public void setPurchaseDate(LocalDate now) {
		this.purchaseDate = now;
	}

	// invoice number setter
	public void setInvoiceNumber(int i) {
		this.invoiceNumber = i;
	}
}
