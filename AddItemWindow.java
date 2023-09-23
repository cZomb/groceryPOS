package rrcGrocery;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import java.sql.*;

// AddItemWindow class handles the window for adding, modifying and deleting items
// since the SQL Database handles items, this class handles the database as well
public class AddItemWindow implements ActionListener, Runnable {
	private Thread t; // Thread object for multithreading

	private JFrame frame; // JFrame for main window

	private JLabel jlItemNo; // label for item number
	private JTextField jtfItemNo; // text field for item number

	private JLabel jlDesc; // labe for description
	private JTextArea jtaDesc; // text area for description
	private JScrollPane jspDesc; // scroll pane for description text area

	private JLabel jlCat; // label for category
	private JComboBox<Integer> jcbCat; // combo box for category
	private String[] sCategory = { "Produce", "Meat", "Seafood", "Deli", "Bakery" }; // string to hold combo box values

	private JLabel jlPrice; // label for price
	private JTextField jtfPrice; // text field for price

	private JButton jbCancel; // cancel button
	private JButton jbOk; // OK button

	private JRadioButton jrbAddMod; // radio button to select Add/Modify
	private JRadioButton jrbDelete; // radio button to select delete
	private ButtonGroup bgAddModDel; // button group to link the two buttons

	private ArrayList<Item> itemList; // arraylist to store item values

	private Connection conn; // Handles SQL connection to database

	// default constructor to call start() method for multithreading
	public AddItemWindow() {
		start();
	}
	
	// default constructor
	//public AddItemWindow() {
	private void createWindow() {
		// create window frame
		frame = new JFrame("Add Item");
		frame.setLayout(null); // set null layour to use absolute positioning
		frame.setResizable(false); // disable changing window size

		// create Item No label
		jlItemNo = new JLabel("Item No.");
		jlItemNo.setBounds(8, 8, 96, 32);
		frame.add(jlItemNo);

		// create item no text field
		jtfItemNo = new JTextField("");
		jtfItemNo.setBounds(64, 12, 128, 24);
		frame.add(jtfItemNo);

		// create description label
		jlDesc = new JLabel("Desc");
		jlDesc.setBounds(8, 40, 96, 32);
		frame.add(jlDesc);

		// create description text area
		jtaDesc = new JTextArea("Enter an Item No in above text field and press Enter key");
		jtaDesc.setBounds(64, 48, 128, 64);
		jtaDesc.setLineWrap(true);
		jtaDesc.setWrapStyleWord(true);

		// setup descritiont text area scroll pane
		jspDesc = new JScrollPane(jtaDesc);
		jspDesc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jspDesc.setBounds(64, 48, 128, 64);
		frame.add(jspDesc);

		// setup category combo box and label
		jlCat = new JLabel("Cat"); // create label
		jlCat.setBounds(204, 8, 64, 32);
		frame.add(jlCat);

		// create combo box
		jcbCat = new JComboBox(sCategory);
		jcbCat.setBounds(240, 12, 116, 22);
		frame.add(jcbCat);

		// setup price label and text field
		jlPrice = new JLabel("Price");
		jlPrice.setBounds(204, 40, 64, 32);
		frame.add(jlPrice);

		jtfPrice = new JTextField("0.00");
		jtfPrice.setBounds(240, 48, 116, 24);
		frame.add(jtfPrice);

		// add "Cancel" button
		jbCancel = new JButton("Cancel");
		jbCancel.setBounds(204, 84, 76, 24);
		frame.add(jbCancel);

		// add "Ok" button
		jbOk = new JButton("Ok");
		jbOk.setBounds(280, 84, 76, 24);
		frame.add(jbOk);

		// add "add/modify" radio button
		jrbAddMod = new JRadioButton("Add/Modify", true);
		jrbAddMod.setBounds(8, 120, 96, 24);
		frame.add(jrbAddMod);

		// add "delete" radio button
		jrbDelete = new JRadioButton("Delete");
		jrbDelete.setBounds(128, 120, 128, 24);
		frame.add(jrbDelete);

		// create button group
		bgAddModDel = new ButtonGroup();

		// add both radio buttons to "link" them together
		bgAddModDel.add(jrbAddMod);
		bgAddModDel.add(jrbDelete);

		// set window size, position and visibility
		frame.setSize(380, 200);// default size for windows
		frame.setVisible(true); // by default, visibility = true;
		frame.setLocationRelativeTo(null);

		// set action listeners
		jbCancel.addActionListener(this);
		jbOk.addActionListener(this);
		jtfItemNo.addActionListener(this);
		jrbAddMod.addActionListener(this);
		jrbDelete.addActionListener(this);
		jtfPrice.addActionListener(this);

		// assert window creation was successful
		assert frame != null : "Window creation was unseccessful";
	}

	// set the arraylist to store item values
	public void setItemList(ArrayList<Item> items) {
		this.itemList = items;
	}

	// override action listener for this window
	@Override
	public void actionPerformed(ActionEvent e) {
		// if the ItemNo text changes
		if (e.getSource() == jtfItemNo) {
			// if the item is in our database then change the window values, if not leave
			// them blank
			for (Item item : itemList) {
				if (jtfItemNo.getText().equals(item.getItemNo())) {
					jtaDesc.setText(item.getDescription());
					jcbCat.setSelectedItem(item.getCatString(item.getCat()));
					jtfPrice.setText(item.getPrice().toString());

					// a match has been found; no need to keep looking; return
					return;
				}
			}

			// loop has ended so no match was found; reset the other elements
			jtaDesc.setText("Item Doesn't Exist");
			jcbCat.setSelectedItem("Produce");
			jtfPrice.setText("0.00");
		}

		// check if price textfield contains a number, if not a valid number then reset
		// to 0.00
		if (!NewSaleWindow.isNumeric(jtfPrice.getText())) {
			jtfPrice.setText("0.00");
		}

		// if cancel button is pushed, close the window
		if (e.getSource() == jbCancel) {
			frame.dispose();
		} else if (e.getSource() == jbOk) {
			// Adding item if it doesn't exist and modifying its values if it does exist
			if (jrbAddMod.isSelected()) {
				// loop through item list
				for (Item item : itemList) {
					// if the item numbers match, then edit the selected categories since the item
					// already exists
					if (jtfItemNo.getText().equals(item.getItemNo())) {
						if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to modify this item?",
								"Confirm Modify", JOptionPane.YES_NO_OPTION) == 0) {
							// set item object values based in API textfields and combobox selections
							item.setDescription(jtaDesc.getText());
							item.setPrice(Double.parseDouble(jtfPrice.getText()));
							item.setCat(Item.Category.values()[(jcbCat.getSelectedIndex())]);

							// Modify row in database
							try (Connection conn = DriverManager.getConnection(MainWindow.CONNECTION_STR,
									MainWindow.USER_NAME, MainWindow.PASS_WORD);) {
								// a connection is successfully created
								assert conn != null : "Connection was unseccessful";

								// query string
								String sql = "UPDATE Items SET Description = \'" + item.getDescription()
										+ "\', Category = \'" + item.getCatString(item.getCat()) + "\', Price = \'"
										+ item.getPrice() + "\' WHERE ItemNo = \'" + item.getItemNo() + "\';";

								// create prepared statement based on SQL query string
								PreparedStatement ps = conn.prepareStatement(sql);

								// execute the statement
								ps.execute();

								this.conn = conn;

								// create popup window
								JOptionPane.showMessageDialog(frame, "Item Successfully Modified.");
							}
							// if an error occurred post exception information to terminal
							catch (SQLException se) {
								System.out.println(se);
							} finally {
								// after item information is read from database close the connection
								if (this.conn != null) {
									try {
										this.conn.close();
									} catch (SQLException se) {
										System.out.println(se);
									}
								}
							}

							// a match has been found, return and close window
							frame.dispose();
						}
						return;
					}
				}
				// the above loop finished successfully meaning there was no match
				// Item doesnt exist, add it to the list
				if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to Add this item?", "Confirm Add",
						JOptionPane.YES_NO_OPTION) == 0) {
					// create new item object based on entered values
					Item ni = new Item(jtfItemNo.getText(), jtaDesc.getText(),
							Item.Category.values()[(jcbCat.getSelectedIndex())],
							Double.parseDouble(jtfPrice.getText()));
					// add the item to the arraylist
					itemList.add(ni);

					// Modify row in database
					try (Connection conn = DriverManager.getConnection(MainWindow.CONNECTION_STR, MainWindow.USER_NAME,
							MainWindow.PASS_WORD);) {
						// a connection is successfully created
						assert conn != null : "Connection was unseccessful";

						// query string
						String sql = "INSERT INTO Items VALUES (\"" + ni.getItemNo() + "\", \"" + ni.getDescription()
								+ "\", \"" + ni.getCatString(ni.getCat()) + "\", \"" + ni.getPrice() + "\");";

						// create prepared statement based on SQL query string
						PreparedStatement ps = conn.prepareStatement(sql);

						// execute the statement
						ps.execute();

						this.conn = conn;
						// show a popup window confirming changes
						JOptionPane.showMessageDialog(frame, "Item Successfully Added.");
					}
					// if an error occurred post exception information to terminal
					catch (SQLException se) {
						System.out.println(se);
					} finally {
						// close the connection
						if (this.conn != null) {
							try {
								this.conn.close();
							} catch (SQLException se) {
								System.out.println(se);
							}
						}
					}

					// a match has been found, return and close window
					frame.dispose();
				}
			} else if (jrbDelete.isSelected()) {
				// delete is selected, start looping to search for a match
				for (int i = 0; i < itemList.size(); i++) {
					// loop through arraylist to find a match to delete
					if (jtfItemNo.getText().equals(itemList.get(i).getItemNo())) {
						if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this item?",
								"Confirm Delete", JOptionPane.YES_NO_OPTION) == 0) {
							// Modify row in database
							try (Connection conn = DriverManager.getConnection(MainWindow.CONNECTION_STR,
									MainWindow.USER_NAME, MainWindow.PASS_WORD);) {
								// a connection is successfully created
								assert conn != null : "Connection was unseccessful";

								// query string
								String sql = "DELETE FROM Items WHERE ItemNo = \'" + itemList.get(i).getItemNo()
										+ "\';";

								// create prepared statement based on SQL query string
								PreparedStatement ps = conn.prepareStatement(sql);

								// execute the statement
								ps.execute();

								this.conn = conn;

								// delete selected and a match is found; remove from list
								itemList.remove(i);

								JOptionPane.showMessageDialog(frame, "Item Successfully Deleted.");
							}
							// if an error occurred post exception information to terminal
							catch (SQLException se) {
								System.out.println(se);
							} finally {
								// close the connection
								if (this.conn != null) {
									try {
										this.conn.close();
									} catch (SQLException se) {
										System.out.println(se);
									}
								}
							}
							// return and close window
							frame.dispose();
						}
						// a match has been found, no need to continue looping; return
						return;
					}
				}
			}
		}

		// handle radio buttons selection
		if (e.getSource() == jrbAddMod) {
			jtaDesc.setEnabled(true);
			jtfPrice.setEnabled(true);
			jcbCat.setEnabled(true);
		} else if (e.getSource() == jrbDelete) {
			jtaDesc.setEnabled(false);
			jtfPrice.setEnabled(false);
			jcbCat.setEnabled(false);
		}
	}

	@Override
	public void run() {
		// create the window and its components
		createWindow();
	}

	// start method to start a new thread
	private void start() {
		if (t == null) {
			t = new Thread(this, "AIW Thread");
			t.start();
		}
	}
}
