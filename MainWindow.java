package rrcGrocery;

import javax.swing.*;

import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// main window of software
// menubar for selecting all other windows/features
public class MainWindow implements ActionListener {
	// main itemList for each window
	private ArrayList<Item> itemList;
	private static ArrayList<Sale> salesList;

	public static void setSalesList(ArrayList<Sale> al) {
		salesList = al;
	}

	// main window
	private JFrame frame = new JFrame("Grocery POS");

	// menu bar for window
	private JMenuBar jmb = new JMenuBar();

	// menus for menubar
	private JMenu jmSale = new JMenu("Sale");
	private JMenu jmInventory = new JMenu("Inventory");

	// menu items for sale tab
	private JMenuItem jmiNewSale = new JMenuItem("New Sale");
	private JMenuItem jmiSaleHistory = new JMenuItem("Sale History");

	// menu items for inventory tab
	private JMenuItem jmiItemList = new JMenuItem("Item List");
	private JMenuItem jmiItemSettings = new JMenuItem("Item Settings");

	// SQL Database Variables
	private Connection conn;

	static String USER_NAME = "root";
	static String PASS_WORD = "root";
	static String CONNECTION_STR = "jdbc:mysql://localhost/rrcGrocery";
	private String loadAll = "SELECT * FROM Items";

	public MainWindow() {
		Sale.nextInvoiceNumber = 10000;

		// default constructor
		itemList = new ArrayList<Item>();
		salesList = new ArrayList<Sale>();

		/*
		 * // add default items for testing purpose: itemList.add(new Item("apple",
		 * "a basic, simple apple", Item.Category.PRODUCE, 1.49)); itemList.add(new
		 * Item("orange", "a normal orange", Item.Category.PRODUCE, 1.67));
		 * itemList.add(new Item("ryebread", "a loaf of rye bread",
		 * Item.Category.BAKERY, 2.99));
		 */

		// connect to database to load item information
		try {
			connectToDatabase();
		} catch (SQLException e) {
			System.out.println(e);
		}

		// Load files here during main window constructor into appropriate container if
		// the file exists

		// check to see if the invoices.txt file exists
		// if true, read invoices from file
		File f = new File("invoices.txt");

		if (f.isFile() && f.canRead()) {
			try {
				FileInputStream inFile = new FileInputStream("invoices.txt");
				ObjectInputStream inStream = new ObjectInputStream(inFile);

				salesList = (ArrayList<Sale>)inStream.readObject();

				inStream.close();
			} catch (IOException | ClassNotFoundException ex) {
				System.out.println(ex);
			} finally {
				if (salesList.size() > 0) {
					Sale.nextInvoiceNumber = salesList.get(salesList.size() - 1).getInvoiceNumber() + 1;
				} else {
					Sale.nextInvoiceNumber = 10000;
				}
			}
		}

		// for testing, simply close on 'X' button
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		// add sale menu items
		jmSale.add(jmiNewSale);
		jmSale.add(jmiSaleHistory);

		// add inventory menu items
		jmInventory.add(jmiItemList);
		jmInventory.add(new JSeparator());
		jmInventory.add(jmiItemSettings);

		// add menu to menu bar
		jmb.add(jmSale);
		jmb.add(jmInventory);

		// add menubar to window
		frame.setJMenuBar(jmb);

		// set size and visibility of window
		frame.setSize(640, 480); // default size for windows
		frame.setVisible(true); // by default, visibility = true;

		// add action listeners
		jmiNewSale.addActionListener(this);
		jmiSaleHistory.addActionListener(this);

		jmiItemList.addActionListener(this);
		jmiItemSettings.addActionListener(this);

		// assert window creation was successful
		assert frame != null : "Window creation was unseccessful";
	}

	// handle action events of the added action listeners
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jmiNewSale) {
			// create the NewSaleWindow instance
			NewSaleWindow nsw = new NewSaleWindow();

			// pass itemList container to the NewSaleWindow object
			nsw.setItemList(itemList);

			// pass salesList container to the NewSaleWindow object
			nsw.setSalesList(salesList);
		} else if (e.getSource() == jmiSaleHistory) {
			// create the SaleHistoryWindow instance
			SaleHistoryWindow shw = new SaleHistoryWindow();

			// pass salesList container to the NewSaleWindow object
			shw.setSalesList(salesList);
		} else if (e.getSource() == jmiItemList) {
			// create the ItemList window instance
			ItemListWindow ilw = new ItemListWindow(itemList);
		} else if (e.getSource() == jmiItemSettings) {
			// create the AddItemWindow instance
			AddItemWindow aiw = new AddItemWindow();

			// pass itemList container to the NewSaleWindow object
			aiw.setItemList(itemList);
		}
	}

	// connect to the database and read initial itemList information
	private void connectToDatabase() throws SQLException {
		// connect to database, setup the Statement, initialize result set based on
		// statement
		try (Connection conn = DriverManager.getConnection(CONNECTION_STR, USER_NAME, PASS_WORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(loadAll)) {
			// a connection is successfully created
			assert conn != null : "Connection was unseccessful";
			this.conn = conn;

			// cycle through rows to store item information in the collection
			while (rs.next()) {
				itemList.add(new Item(rs.getString("ItemNo"), rs.getString("Description"),
						Item.getCat(rs.getString("Category")), rs.getDouble("Price")));
			}
		}
		// if an error occurred post exception information to terminal
		catch (SQLException e) {
			System.out.println(e);
		} finally {
			// after item information is read from database close the connection
			if (this.conn != null) {
				this.conn.close();
			}
		}
	}
}
