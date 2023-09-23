package rrcGrocery;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

// window for processing a new sale
public class NewSaleWindow extends JFrame implements ActionListener, TableModelListener, Runnable {
	private Thread t; // thread object for multithreading

	final float GST = 0.05f; // stores gst constant
	final float PST = 0.07f; // stores pst constant

	private ArrayList<Sale> salesList; // arraylist to store saleList

	private JFrame frame; // main window
	private JTable table; // table for adding invoice info
	private JScrollPane sp; // scrollpane to lock table to
	private JPanel panel; // panel for all tabel elements to connect to

	private JButton jbCancel; // cancel button to close window
	private JButton jbPost; // post button to post invoice and update database. closes window if successful

	// text labels
	// labels for the titles
	private JLabel jlSubTotal;
	private JLabel jlPst;
	private JLabel jlGst;
	private JLabel jlTotal;

	// labels for the values
	private JLabel jlSubTotalValue;
	private JLabel jlPstValue;
	private JLabel jlGstValue;
	private JLabel jlTotalValue;

	private TableColumnModel columnModel; // used for changing column width
	private ArrayList<Item> itemList; // arraylist to store itemList
	private Sale sale; // sale object

	// column titles
	private String columns[] = { "Item No.", "Description", "Category", "Quantity", "Unit Price", "Amount" };

	// row data/information
	private String rows[][] = { { " ", " ", " ", " ", " ", " " } };

	// renderer to aling cell text of main table
	private DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

	// create tableModel to change editable values
	private DefaultTableModel tableModel = updateTable(rows, columns);

	// aligns text in table cells accordingly
	private void setTableCellAlignment(int alignment) {
		renderer.setHorizontalAlignment(alignment);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.setDefaultRenderer(table.getColumnClass(i), renderer);
		}
		// repaint to show table cell changes
		table.updateUI();
	}

	// main constructor
	public NewSaleWindow() {
		start();
	}

	// create the window and window components
	public void createWindow() {
		// instantiate main Sale object to store invoice information
		sale = new Sale();

		// main window
		frame = new JFrame("New Sale");
		frame.setLayout(null); // remove default layouts to enable absolute positioning of window elements
		frame.setResizable(false); // disable changing window size

		// create main panel
		panel = new JPanel();
		panel.setBounds(2, 2, 640, 320);
		panel.setLayout(null);

		// main table on window
		table = new JTable(rows, columns);

		// set table model for editability
		table.setModel(tableModel);

		// set widths of columns
		columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(75);
		columnModel.getColumn(1).setPreferredWidth(200);
		columnModel.getColumn(2).setPreferredWidth(40);
		columnModel.getColumn(3).setPreferredWidth(40);

		// align text to the right
		setTableCellAlignment(SwingConstants.RIGHT);

		// create scroll panel to attach table to
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(0, 0, 640 - ((Integer) UIManager.get("ScrollBar.width")).intValue(), 320);

		// add it to the window
		panel.add(sp);

		// create and add buttons to window
		// cancel button
		jbCancel = new JButton("Cancel");
		jbCancel.setBounds(400, 400, 96, 32);
		frame.add(jbCancel);

		// post button
		jbPost = new JButton("Post");
		jbPost.setBounds(504, 400, 96, 32);
		frame.add(jbPost);

		// add labels to frame
		// SubTotal labels
		jlSubTotal = new JLabel("Subtotal: ");
		jlSubTotal.setBounds(400, 326, 89, 16);
		frame.add(jlSubTotal);

		// PST labels
		jlPst = new JLabel("PST");
		jlPst.setBounds(400, 358, 89, 16);
		frame.add(jlPst);

		// GST labels
		jlGst = new JLabel("GST");
		jlGst.setBounds(400, 342, 89, 16);
		frame.add(jlGst);

		// Total labels
		jlTotal = new JLabel("Total: ");
		jlTotal.setBounds(400, 374, 89, 16);
		frame.add(jlTotal);

		// SubTotalValue labels
		jlSubTotalValue = new JLabel("$0.00", SwingConstants.RIGHT);
		jlSubTotalValue.setBounds(500, 326, 89, 16);
		frame.add(jlSubTotalValue);

		// PSTValue labels
		jlPstValue = new JLabel("$0.00", SwingConstants.RIGHT);
		jlPstValue.setBounds(500, 358, 89, 16);
		frame.add(jlPstValue);

		// GSTValue labels
		jlGstValue = new JLabel("$0.00", SwingConstants.RIGHT);
		jlGstValue.setBounds(500, 342, 89, 16);
		frame.add(jlGstValue);

		// TotalValue labels
		jlTotalValue = new JLabel("$0.00", SwingConstants.RIGHT);
		jlTotalValue.setBounds(500, 374, 89, 16);
		frame.add(jlTotalValue);

		// add the JPanel to the main window
		frame.add(panel);

		// set window size and visibility
		frame.setSize(640, 480); // default size for windows
		frame.setVisible(true); // by default, visibility = true;
		frame.setLocationRelativeTo(null);

		// set action listeners
		jbCancel.addActionListener(this);
		jbPost.addActionListener(this);

		table.getModel().addTableModelListener(this);

		// assert window creation was successful
		assert frame != null : "Window creation was unseccessful";
	}

	// set the arraylist to store item values
	public void setItemList(ArrayList<Item> items) {
		this.itemList = items;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbCancel) {
			// you hit cancel; close window as if nothing happened
			frame.dispose();
		} else if (e.getSource() == jbPost) {
			// confirm all invoice info is correct then update database
			if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to post this invoice?", "Post",
					JOptionPane.YES_NO_OPTION) == 0) {
				sale.setPurchaseDate(LocalDate.now());
				sale.setInvoiceNumber(Sale.nextInvoiceNumber);
				Sale.nextInvoiceNumber++;

				// remove the empty line before posting
				sale.saleLines.remove(sale.saleLines.size() - 1);
				salesList.add(sale);

				// "Invoice Successfully Posted" popup window
				JOptionPane.showMessageDialog(frame, "Invoice Successfully Posted.");

				// Write to invoice file
				try {
					FileOutputStream outFile = new FileOutputStream("invoices.txt");

					ObjectOutputStream outStream = new ObjectOutputStream(outFile);

					outStream.writeObject(salesList);
					outStream.flush();
					outStream.close();
				} catch (IOException ioe) {
					System.out.println(ioe);
				}

				// close window after posting
				frame.dispose();
			}
		}
	}

	// table has been modified
	// extract the modified data and act accordingly
	@Override
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow(); // the row that was edited
		int col = e.getColumn(); // the column that was edited

		// data holds cel info
		String data = (String) tableModel.getValueAt(row, col);
		data = data.trim();

		if (col == 0) {
			// if the first column was edited then an itemNo was changed
			boolean validLine = false;

			// loop through itemList to find a match
			for (int i = 0; i < itemList.size(); i++) {
				// a match was found
				if (data.equals(itemList.get(i).getItemNo())) {
					sale.saleLines.set(row, sale.newSaleLine(itemList.get(i).getSaleLineString()));
					validLine = true;
					break;
				}
			}

			// if the item number input isn't valid, reset the row to blank
			if (!validLine) {
				sale.saleLines.set(row, sale.newSaleLine());
			}
		} else if (col == 3) {
			// if the third column was edited then a quantity was changed
			// make sure itemNo isn't empty
			for (int i = 0; i < itemList.size(); i++) {
				// if a match is found then the item is valid
				if (sale.saleLines.get(row).itemNo.equals(itemList.get(i).getItemNo())) {
					// check if the cell has a valid number
					if (isNumeric(data)) {
						// a valid number, update saleLine.amount
						sale.saleLines.get(row).qty = data;
						sale.saleLines.get(row).updateAmount();

					} else {
						// number is not valid, set qty to 1
						sale.saleLines.get(row).qty = "1";
						sale.saleLines.get(row).updateAmount();
					}
				}
			}
		}

		Double subTotal = 0.00; // set subtotal to 0.00

		// trim sale of any empty sale lines then add an empty one to the bottom/end
		for (int i = 0; i < sale.saleLines.size(); i++) {
			if (sale.saleLines.get(i).itemNo.equals("")) {
				sale.saleLines.remove(i);
			} else {
				// convert each subtotal string to Double and sum them together
				subTotal += Double.parseDouble(sale.saleLines.get(i).amount);
			}
		}

		// add the newSaleLine to the saleLines container
		sale.saleLines.add(sale.newSaleLine());

		// update table
		DefaultTableModel dtm = updateTable(sale.getSaleString(), columns);
		tableModel = dtm;
		this.table.setModel(tableModel);
		table.getModel().addTableModelListener(this);

		// update subtotal/gst/pst/total
		jlSubTotalValue.setText("$" + String.format("%.2f", subTotal));

		// update pst text
		Double dPst = subTotal * PST;
		jlPstValue.setText("$" + String.format("%.2f", dPst));

		// update gst text
		Double dGst = subTotal * GST;
		jlGstValue.setText("$" + String.format("%.2f", dGst));

		// update total text
		Double dTotal = subTotal + (subTotal * PST) + (subTotal * GST);
		jlTotalValue.setText("$" + String.format("%.2f", dTotal));
	}

	// checks whether a string contains a valid number and return the result
	public static boolean isNumeric(String str) {
		try {
			// if the text can be parsed to double then its a number
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			// if a numberFormatException is caught then the text isn't a number
			return false;
		}
	}

	// update table info based on rows (s1) and columns (s2)
	private DefaultTableModel updateTable(String[][] s1, String[] s2) {
		DefaultTableModel dtm = new DefaultTableModel(s1, s2) {
			@Override
			public boolean isCellEditable(int row, int col) {
				boolean editable = false;

				// only allow editing of columns 0 and 3
				if (col == 0 || col == 3) {
					editable = true;
				}

				return editable;
			}
		};

		return dtm;
	}

	// set the salesList container externally
	public void setSalesList(ArrayList<Sale> al) {
		this.salesList = al;
	}

	@Override
	public void run() {
		// create the window and window components
		createWindow();
	}

	private void start() {
		if (t == null) {
			t = new Thread(this, "NSW Thread");
			t.start();
		}
	}
}
