package rrcGrocery;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

// main window for retrieving InventoryItem information
public class ItemListWindow implements ActionListener, Runnable {
	private Thread t; // thread object

	private JFrame frame; // main window
	private JPanel panel; // panel to store scroll pane and table

	private JScrollPane jsp; // scroll pane for table
	private JTable table; // main table for inventory items

	private JTextField jtfSearch; // Text Field to search
	private JButton jbSearch; // search button

	private JLabel jlCat; // category label
	private JComboBox jcbCat; // category combobox
	private String[] sCategory = { "All", "Produce", "Meat", "Seafood", "Deli", "Bakery" }; // category strings

	private JLabel jlSort; // sort label
	private JComboBox jcbSort; // sort combo box
	private String[] sSort = { "Alphabetical", "Unit Price" }; // string values in sort combo box

	private ArrayList<Item> itemList; // arraylist to store items

	private TableColumnModel columnModel; // used for changing column width

	// column titles
	private String columns[] = { "Item No.", "Description", "Category", "Unit Price" };

	// row data/information
	private String rows[][] = { { " ", " ", " ", " ", " ", " " } };

	// renderer to aling cell text of main table
	private DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

	// create tableModel to change editable values
	// table is used to return values given in textfields, comboboxes and spinner,
	// therefore table is not editable
	private DefaultTableModel tableModel = new DefaultTableModel(rows, columns) {
		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	};

	// aligns text in table cells accordingly
	private void setTableCellAlignment(int alignment) {
		renderer.setHorizontalAlignment(alignment);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.setDefaultRenderer(table.getColumnClass(i), renderer);
		}
		// repaint to show table cell changes
		table.updateUI();
	}

	public ItemListWindow(ArrayList<Item> al) {
		this.itemList = al;
		start();
	}

	// default constructor
	// public ItemListWindow() {
	private void createWindow() {
		frame = new JFrame("Item List"); // create new jframe instance for the main window
		frame.setLayout(null); // remove default layouts to enable absolute positioning of window elements
		frame.setResizable(false); // disable changing window size

		// create main panel
		panel = new JPanel();
		panel.setBounds(3, 80, 640, 400);
		panel.setLayout(null);
		table = new JTable(rows, columns);

		// set table model for editability
		table.setModel(tableModel);

		// set widths of columns
		columnModel = table.getColumnModel();
		// columnModel.getColumn(0).setPreferredWidth(75);

		// align text to the right
		setTableCellAlignment(SwingConstants.RIGHT);

		// create scroll panel to attach table to
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(0, 0, 640 - ((Integer) UIManager.get("ScrollBar.width")).intValue(), 360);

		// add it to the panel
		panel.add(sp);

		// search text fields
		jtfSearch = new JTextField("");
		jtfSearch.setBounds(2, 8, 160, 26);
		frame.add(jtfSearch);

		// search button
		jbSearch = new JButton("Search");
		jbSearch.setBounds(164, 8, 96, 24);
		frame.add(jbSearch);

		// add sort label
		jlSort = new JLabel("Sort");
		jlSort.setBounds(272, 8, 32, 16);
		frame.add(jlSort);

		// add sort combo box
		jcbSort = new JComboBox(sSort);
		jcbSort.setBounds(308, 8, 96, 22);
		frame.add(jcbSort);

		// add category label
		jlCat = new JLabel("Cat");
		jlCat.setBounds(416, 8, 32, 16);
		frame.add(jlCat);

		// add category combo box
		jcbCat = new JComboBox(sCategory);
		jcbCat.setBounds(452, 8, 96, 22);
		frame.add(jcbCat);

		// attach tabel panel to frame and set default values for window
		frame.add(panel);
		frame.setSize(640, 480);// default size for windows
		frame.setVisible(true); // by default, visibility = true;
		frame.setLocationRelativeTo(null);

		// set action listeners
		jbSearch.addActionListener(this);

		// assert window creation was successful
		assert frame != null : "Window creation was unseccessful";
	}

	// set the arraylist to store item values and update table model
	public void setItemList(ArrayList<Item> items) {
		this.itemList = items; // set itemList reference

		updateTable(itemList);
	}

	// comparator used by alphabetical sorting
	public static Comparator<Item> itemNoCompare = new Comparator<Item>() {

		@Override
		public int compare(Item i1, Item i2) {
			String in1 = i1.getItemNo();
			String in2 = i2.getItemNo();

			// compare the two strings and return for ascending order
			return in1.compareTo(in2);
		}
	};

	// comparator used by price sorting
	public static Comparator<Item> priceCompare = new Comparator<Item>() {

		@Override
		public int compare(Item i1, Item i2) {
			Double in1 = i1.getPrice();
			Double in2 = i2.getPrice();

			// compare the two strings and return for ascending order
			return (int) (in1 * 100 - in2 * 100);
		}
	};

	@Override
	public void actionPerformed(ActionEvent e) {
		// search button will be used to check which values are selected in the sort
		// combobox, cat combobox, and "search" textfield then will update the table
		// accordingly
		if (e.getSource() == jbSearch) {
			// copy array list
			ArrayList<Item> tempAL = new ArrayList<Item>();

			// filter based on category combobox selection
			if (!jcbCat.getSelectedItem().equals("All")) {
				for (int i = 0; i < itemList.size(); i++) {
					Item ci = itemList.get(i);

					if (jcbCat.getSelectedItem().toString().equals(ci.getCatString(ci.getCat()))) {
						tempAL.add(ci);
					}
				}
			} else {
				tempAL = itemList;
			}

			// filter based on search bar contents; checks sku and description for
			// containing string

			// get Search textfield string
			String tfs = jtfSearch.getText();
			ArrayList<Item> tempALSearch = new ArrayList<Item>();

			for (int i = 0; i < tempAL.size(); i++) {
				Item ti = tempAL.get(i);

				if (ti.getItemNo().toLowerCase().contains(tfs.toLowerCase())
						|| ti.getDescription().toLowerCase().contains(tfs.toLowerCase())) {
					tempALSearch.add(ti);
				}
			}

			tempAL = tempALSearch;

			// sort with generics based on combobox
			// alphabetical sorts based on sku/itemno
			// price sorts based on item's price
			if (jcbSort.getSelectedItem().toString().equals("Alphabetical")) {
				Collections.sort(tempAL, itemNoCompare);
			} else if (jcbSort.getSelectedItem().toString().equals("Unit Price")) {
				Collections.sort(tempAL, priceCompare);
			}

			// update table based on temporary list
			updateTable(tempAL);
		}
	}

	@Override
	public void run() {
		// call method to initialize window components
		createWindow();

		// call setItemList method to update the jTable
		setItemList(itemList);
	}

	// start method to start a new thread
	private void start() {
		if (t == null) {
			t = new Thread(this, "ILW Thread");
			t.start();
		}
	}

	void updateTable(ArrayList<Item> items) {
		// new string array to store table data
		String data[][] = new String[items.size()][4];

		// loop through itemList container and copy item info to table String array
		for (int i = 0; i < items.size(); i++) {
			// call Item's getItemTableString() method to format and create row data
			data[i] = items.get(i).getItemTableString();
		}

		// update table with new data
		tableModel = new DefaultTableModel(data, columns) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};

		this.table.setModel(tableModel);
	}
}
