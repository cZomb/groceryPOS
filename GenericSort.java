package rrcGrocery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// Generics class for sorting array lists in SaleHistory class
// takes array lists and comparator, sorts the lists and returns the result
public class GenericSort<T> {

	// list to store values for sorting
	private ArrayList<T> list;

	// ArrayList-only constructor sets arraylist
	GenericSort(ArrayList<T> al) {
		this.list = al;
	}

	// ArrayList and Comparator constructor
	// sets array list then sorts it based on the constructor
	GenericSort(ArrayList<T> al, Comparator<T> c) {
		this.list = al;
		sort(c);
	}

	// getter for ArrayList
	public ArrayList<T> get() {
		return this.list;
	}

	// method to add new elements to the list
	public void add(T t) {
		this.list.add(t);
	}

	// set the array list
	public void set(ArrayList<T> al) {
		this.list = al;
	}

	// sort the list based on a comparator
	public void sort(Comparator<T> c) {
		Collections.sort(list, c);
	}
}
