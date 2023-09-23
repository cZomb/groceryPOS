# Grocery Store POS system project I made for my graduation project in college

## Main Window
-Program has the main window with a menu bar for navigation. <br /><br />

## New Sale Window

-NewSale window is used to pull items from the SQL database to fill the table, adding a new empty line below <br />
-ItemNo and Quantity are the only editable fields on this table as the other cells autofill based on database values<br />
-Total price values are then adjusted based on unit price and quantity, then sales tax is applied and totalled <br /> <br />
-Once invoices are posted, they are saved in a local file <br /> <br />

## Sale History Window

-SaleHistory window is used to review posted invoice information <br /><br />

-Date ranges are used to filter invoices within the range <br />
-Item text field is used to search if the ItemNo cell contains the string<br />
-Inv text field is used to search if the Invoice Num cell contains the string<br />
-Cat combobox is used to filter results based on the selected category<br />
-Sort combobox is used to sort results based on category, posting date, invoice number, alphabetically based in itemno, quantity, or total dollar amount<br /> 
-If right-clicking on an entry, the option to delete the entry will appear on the mouse cursor, starting with the cancel option to avoid accidentally deleting entries<br /> <br />

## Item List Window

-ItemList window is used to search the database for all available items <br /> <br />
-The text field is used to search the ItemNo and Description fields <br />
-the Sort and Cat comboboxes function the same as the SaleHistory window comboboxes

## Item Settings Window

-Item Settings window is used to add, modify or delete items from the database <br /><br />

-Adding an item checks to see if the item exists. If not, it will add the item to the database with ItemNo being used as the primary key <br />
-If the itemNo does exist, the Desc, Cat and Price values are adjusted and modified in the database <br />
-If the Delete radiobutton is selected, all fields other than the ItemNo textfield become non-editable and the item is removed from the database<br />

## Design info

-Since each Sale is made of several saleLines, and saleLines can't exist without a Sale, the saleLines class is an inner class of the Sale class. <br />
-With the SaleHistory window only used for retrieving values, each cell is stored as a string so that if an item is removed from the Item database it will not effect the SaleHistory window/classes <br />
-Each window is created in a multithreaded environment using thread-safe design to avoid common multithreading problems/issues <br />
-Exception Handling is used where appropriate to avoid bugs and errors<br />
-Asserts were used for testing and debugging<br />

## License

[MIT](https://choosealicense.com/licenses/mit/)
