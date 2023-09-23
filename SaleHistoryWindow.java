package rrcGrocery;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DateFormatter;

// window for retrieving posted sale/invoice information
public class SaleHistoryWindow implements ActionListener, Runnable {
	private Thread t; // Thread object for multithreading

	private ArrayList<Sale> salesList; // container for sales

	private JFrame frame; // main window
	private JPanel panel; // panel to store scroll pane and table

	private JScrollPane jsp; // scroll pane for table
	private JTable table; // main table for invoices

	// text JLabels to title window elements
	private JLabel jlFrom;
	private JLabel jlTo;
	private JLabel jlCat;
	private JLabel jlSku;
	private JLabel jlInv;
	private JLabel jlSort;

	// spinners for date range and spinner model for spinners
	private SpinnerDateModel sdmTo;
	private SpinnerDateModel sdmFrom;

	// spinner values
	private JSpinner jsFrom;
	private JSpinner jsTo;

	// category dropdown for filtering table results
	private JComboBox jcbCategory;
	private String[] sCategory = { "All", "Produce", "Meat", "Seafood", "Deli", "Bakery" };

	// Sort Combobox for sorting table results
	private JComboBox jcbSort;
	private String[] sSort = { "Date", "Alphabetical", "Invoice Number", "Amount", "Quantity" };

	// Text Fields for item Sku and Invoice number
	private JTextField jtfSku;
	private JTextField jtfInvoice;

	// Button to apply all sort/filter selections to table
	private JButton jbSearch;

	// used for changing column width
	private TableColumnModel columnModel;

	// column titles
	private String columns[] = { "Date", "Invoice #", "Item No.", "Category", "Quantity", "Unit Price", "Amount" };

	// row data/information
	private String rows[][] = { { " ", " ", " ", " ", " ", " ", " " } };

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

	// default constructor
	public SaleHistoryWindow() {
		start();
	}

	private void createWindow() {
		// create and set defaults for main window
		frame = new JFrame("Sale History");
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

		// add FromDate elements to panel
		jlFrom = new JLabel("From");
		jlFrom.setBounds(8, 8, 32, 16);

		// create spinner model
		sdmFrom = new SpinnerDateModel();
		sdmTo = new SpinnerDateModel();

		// create from spinner and set default values
		jsFrom = new JSpinner(sdmFrom);

		// change date formatting
		JSpinner.DateEditor fromEditor = new JSpinner.DateEditor(jsFrom, "dd.MM.yyyy");
		DateFormatter fromFormatter = (DateFormatter) fromEditor.getTextField().getFormatter();

		// set formatter values
		fromFormatter.setAllowsInvalid(false);
		fromFormatter.setOverwriteMode(true);

		jsFrom.setEditor(fromEditor);
		jsFrom.setBounds(56, 7, 96, 26);

		// add "From" spinner to main window
		frame.add(jsFrom);

		// add "From" label to main window
		frame.add(jlFrom);

		// add ToDate elements to panel
		jlTo = new JLabel("To");
		jlTo.setBounds(168, 8, 32, 16);

		// create "to" spinner and set values
		jsTo = new JSpinner(sdmTo);

		// Change date formatting
		JSpinner.DateEditor toEditor = new JSpinner.DateEditor(jsTo, "dd.MM.yyyy");
		DateFormatter toFormatter = (DateFormatter) toEditor.getTextField().getFormatter();

		// set formatter values
		toFormatter.setAllowsInvalid(false);
		toFormatter.setOverwriteMode(true);

		jsTo.setEditor(toEditor);
		jsTo.setBounds(192, 7, 96, 26);

		// add "to" spinner to main window
		frame.add(jsTo);

		// add "to" label to main window
		frame.add(jlTo);

		// add category label
		jlCat = new JLabel("Cat");
		jlCat.setBounds(304, 8, 32, 16);
		frame.add(jlCat);

		// add category combo box
		jcbCategory = new JComboBox(sCategory);
		jcbCategory.setBounds(332, 8, 96, 22);
		frame.add(jcbCategory);

		// add item number label
		jlSku = new JLabel("Item");
		jlSku.setBounds(8, 44, 32, 16);

		// add item number textfield
		jtfSku = new JTextField("");
		jtfSku.setBounds(56, 42, 96, 24);
		frame.add(jtfSku);
		frame.add(jlSku);

		// add invoice number label
		jlInv = new JLabel("Inv");
		jlInv.setBounds(164, 44, 32, 16);
		frame.add(jlInv);

		// add invoice number text field
		jtfInvoice = new JTextField("");
		jtfInvoice.setBounds(192, 42, 96, 24);
		frame.add(jtfInvoice);

		// add sort label
		jlSort = new JLabel("Sort");
		jlSort.setBounds(300, 44, 32, 16);
		frame.add(jlSort);

		// add sort combo box
		jcbSort = new JComboBox(sSort);
		jcbSort.setBounds(332, 42, 96, 22);
		frame.add(jcbSort);

		// add sort button
		jbSearch = new JButton("Search");
		jbSearch.setBounds(452, 42, 80, 22);
		frame.add(jbSearch);

		// attach tabel panel to frame and set default values for window
		frame.add(panel);
		frame.setSize(640, 480);// default size for windows
		frame.setVisible(true); // by default, visibility = true;
		frame.setLocationRelativeTo(null);

		// add action listenners
		jbSearch.addActionListener(this);

		// add mouse listener to table
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent me) {
				// check which row the user clicked on
				int r = table.rowAtPoint(me.getPoint());
				if (r >= 0 && r < table.getRowCount()) {
					table.setRowSelectionInterval(r, r);
				} else {
					table.clearSelection();
				}

				int rowindex = table.getSelectedRow();

				if (rowindex < 0)
					return;

				// if the user clicked on a valid row of the table
				if (me.isPopupTrigger() && me.getComponent() instanceof JTable) {
					// create the popup menu
					JPopupMenu popup = new JPopupMenu();

					// add items to the menu
					popup.add(new JMenuItem("Cancel"));
					popup.addSeparator();

					JMenuItem delete = new JMenuItem("Delete");

					// add actionlistener for delete option
					delete.addActionListener(new ActionListener() {
						// counter for the forEach lambda
						int i = 0;

						@Override
						public void actionPerformed(ActionEvent ae) {
							String sInvoice = table.getModel().getValueAt(r, 1).toString();

							// temporary arraylist to modify while using salesList to iterate
							ArrayList<Sale> tempAL = new ArrayList<Sale>(salesList);

							// for (int i = 0; i < salesList.size(); i++) {

							// lambda to cycle through each Sale
							// checks if the invoice selected exists, and if so removes it
							// from the list, updates the table, and writes the changes to
							// the external text file
							salesList.forEach((n) -> {
								// Integer object to store current invoiceNumber
								Integer inv = tempAL.get(i).getInvoiceNumber();

								// check whether the incoice selected exists in the list
								if (sInvoice.equals(inv.toString())) {
									// remove selected invoice
									tempAL.remove(i);

									// update the salesList to the temporary array list
									salesList = new ArrayList<Sale>(tempAL);

									// update the main salesList used by all sub-windows
									MainWindow.setSalesList(salesList);

									// update the SaleHistory table
									updateSTable(salesList);

									// after deleting an invoice, update the history text file
									try {
										FileOutputStream outFile = new FileOutputStream("invoices.txt");

										ObjectOutputStream outStream = new ObjectOutputStream(outFile);

										outStream.writeObject(tempAL);
										outStream.flush();
										outStream.close();
									} catch (IOException ioe) {
										System.out.println(ioe);
									}

									// selected invoice is deleted, no point in continuing the loop; return;
									return;
								}

								i++;
							});
						}
					});

					// add delete to the popup menu
					popup.add(delete);

					// show the menu
					popup.show(me.getComponent(), me.getX(), me.getY());
				}
			}
		});

		// assert window creation was successful
		assert frame != null : "Window creation was unseccessful";
	}

	// set the sale list externally
	public void setSalesList(ArrayList<Sale> al) {
		this.salesList = al;
	}

	// update table based on Sale list
	private void updateSTable(ArrayList<Sale> salesList) {
		// loop through each sale and get it's values as strings to instert into
		// rows[][]
		int iRows = 0;

		// loop through sales to get max saleLine length for string array max size
		for (int i = 0; i < salesList.size(); i++) {
			iRows += salesList.get(i).saleLines.size();
		}

		// create new array for table info based on max size needed
		String[][] row = new String[iRows][7];

		// int for keeping track of current row count
		int iter = 0;

		// loop through each sale
		for (int i = 0; i < salesList.size(); i++) {
			// invoice number wrapper to convert to string later
			Integer invNum = salesList.get(i).getInvoiceNumber();

			// current sale line
			Sale.SaleLine sl;

			// loop through each sale line and add its string to rows
			for (int j = 0; j < salesList.get(i).saleLines.size(); j++) {
				// iterate through next sale line
				sl = salesList.get(i).saleLines.get(j);

				// assign each cell the appropriate values
				row[iter][0] = salesList.get(i).getPurchaseDate().toString();
				row[iter][1] = invNum.toString();
				row[iter][2] = sl.itemNo;
				row[iter][3] = sl.cat;
				row[iter][4] = sl.qty;
				row[iter][5] = sl.price;
				row[iter][6] = sl.amount;

				// increment iterator
				iter++;
			}
		}

		// update table with above values
		DefaultTableModel dtm = updateTable(row, columns);
		tableModel = dtm;
		this.table.setModel(tableModel);
	}

	// update table based on a Sale.SaleLine list
	private void updateTable(ArrayList<Sale.SaleLine> lines) {
		// loop through each sale and get it's values as strings to instert into
		// rows[][]

		// create new array for table info based on max size needed
		String[][] row = new String[lines.size()][7];

		// loop through each sale
		for (int i = 0; i < lines.size(); i++) {
			// assign each cell the appropriate values
			row[i] = lines.get(i).getSHString();
		}

		// update table with above values
		DefaultTableModel dtm = updateTable(row, columns);
		tableModel = dtm;
		this.table.setModel(tableModel);
	}

	// handle user input
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbSearch) {
			// create temporary arraylist of sales
			ArrayList<Sale> tempAL = new ArrayList<Sale>();

			// filter out individual saleLines based on:
			// date range; affects Sale objects
			Date df = sdmFrom.getDate();
			Date dt = sdmTo.getDate();

			for (int i = 0; i < salesList.size(); i++) {
				Sale sale = salesList.get(i);
				LocalDate pd = sale.getPurchaseDate();

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

				Date d = new Date();

				try {
					d = sdf.parse(pd.toString());
				} catch (ParseException pe) {
					System.out.println(pe);
				}

				if ((d.after(df) && d.before(dt)) || d.equals(df) || d.equals(dt)) {
					tempAL.add(sale);
				}
			}

			// invoice text field; affects Sale objects
			String sInv = jtfInvoice.getText();
			ArrayList<Sale> tal2 = new ArrayList<Sale>();
			for (int i = 0; i < tempAL.size(); i++) {

				Sale sale = tempAL.get(i);
				if (((Integer) sale.getInvoiceNumber()).toString().contains(sInv)) {
					tal2.add(sale);
				}
			}

			// update the temporary arraylist with the values filtered from the invoice text
			// field
			tempAL = new ArrayList<Sale>(tal2);

			// sort for Sale-based sorting
			String sort = jcbSort.getSelectedItem().toString();

			// Date sorting
			if (sort.equals("Date")) {
				tempAL = new GenericSort<Sale>(tempAL, dateCompare).get();
			} else
			// InvoiceNumber sorting
			if (sort.equals("Invoice Number")) {
				tempAL = new GenericSort<Sale>(tempAL, invCompare).get();
			}

			// create new arraylist for the individual sales lines that will be further
			// filtered below
			ArrayList<Sale.SaleLine> slList = new ArrayList<Sale.SaleLine>();

			// loop through each saleLine of each Sale and add it to the list
			tempAL.forEach((n) -> n.saleLines.forEach((sl) -> slList.add(sl)));

			// create new temporary saleline list for filtering
			ArrayList<Sale.SaleLine> catList = new ArrayList<Sale.SaleLine>();

			// category filter; affects SaleLine objects
			if (!jcbCategory.getSelectedItem().equals("All")) {
				// create String to store selected category
				String sc = jcbCategory.getSelectedItem().toString();

				// loop through each saleline in the arraylist
				for (int i = 0; i < slList.size(); i++) {
					if (sc.equals(slList.get(i).cat)) {
						catList.add(slList.get(i));
					}
				}
			} else {
				catList = new ArrayList<Sale.SaleLine>(slList);
			}

			// item text field; affects SaleLine objects
			String sItem = jtfSku.getText();
			ArrayList<Sale.SaleLine> inList = new ArrayList<Sale.SaleLine>();

			if (jtfSku.getText() != null) {
				for (int i = 0; i < catList.size(); i++) {
					if (catList.get(i).itemNo.contains(sItem)) {
						inList.add(catList.get(i));
					}
				}
			} else {
				inList = new ArrayList<Sale.SaleLine>(catList);
			}

			// sort for SaleLine-based sorting
			// sort alphabetically
			if (sort.equals("Alphabetical")) {
				inList = new GenericSort<Sale.SaleLine>(inList, alphaCompare).get();
			} else
			// sort based on amount
			if (sort.equals("Amount")) {		
				inList = new GenericSort<Sale.SaleLine>(inList, amountCompare).get();

			} else
			// sort based on quantity
			if (sort.equals("Quantity")) {
				inList = new GenericSort<Sale.SaleLine>(inList, quantityCompare).get();
			}

			// update table with the filtered list
			updateTable(inList);
		}
	}

	// return a copy of the salesList arraylist
	public ArrayList<Sale> getSalesList() {
		return new ArrayList<Sale>(this.salesList);
	}

	// update table info based on rows (s1) and columns (s2)
	private DefaultTableModel updateTable(String[][] s1, String[] s2) {
		DefaultTableModel dtm = new DefaultTableModel(s1, s2) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};

		return dtm;
	}

	@Override
	public void run() {
		// create the window and window components
		createWindow();
	}

	// start the thread if it is null
	private void start() {
		if (t == null) {
			t = new Thread(this, "SHW Thread");
			t.start();
		}
	}

	// comparator for date sorting
	public static Comparator<Sale> dateCompare = new Comparator<Sale>() {
		@Override
		public int compare(Sale s1, Sale s2) {
			return s1.getPurchaseDate().compareTo(s2.getPurchaseDate());
		}
	};

	// comparator for Invoice sorting
	public static Comparator<Sale> invCompare = new Comparator<Sale>() {
		@Override
		public int compare(Sale s1, Sale s2) {
			return s1.getInvoiceNumber() - s2.getInvoiceNumber();
		}
	};

	// comparator for Alphabetical sorting
	public static Comparator<Sale.SaleLine> alphaCompare = new Comparator<Sale.SaleLine>() {
		@Override
		public int compare(Sale.SaleLine sl1, Sale.SaleLine sl2) {
			return sl1.itemNo.compareTo(sl2.itemNo);
		}
	};

	// comparator for Amount sorting
	public static Comparator<Sale.SaleLine> amountCompare = new Comparator<Sale.SaleLine>() {
		@Override
		public int compare(Sale.SaleLine sl1, Sale.SaleLine sl2) {
			Double d1 = Double.parseDouble(sl1.amount);
			Double d2 = Double.parseDouble(sl2.amount);

			return d1.compareTo(d2);
		}
	};

	// comparator for Quantity sorting
	public static Comparator<Sale.SaleLine> quantityCompare = new Comparator<Sale.SaleLine>() {
		@Override
		public int compare(Sale.SaleLine sl1, Sale.SaleLine sl2) {
			Integer i1 = Integer.valueOf(sl1.qty);
			Integer i2 = Integer.valueOf(sl2.qty);

			return i1.compareTo(i2);
		}
	};
}