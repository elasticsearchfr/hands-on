package org.elasticsearchfr.handson.beans;

import java.io.Serializable;

public class Beer implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String brand;
	private Colour colour;
	private float size;
	private float price;
	
	public Beer() {
	}
	
	/**
	 * @param brand
	 * @param colour
	 * @param size
	 * @param price
	 */
	public Beer(String brand, Colour colour, float size, float price) {
		this.brand = brand;
		this.colour = colour;
		this.size = size;
		this.price = price;
	}



	/**
	 * @return the brand
	 */
	public String getBrand() {
		return brand;
	}
	/**
	 * @param brand the brand to set
	 */
	public void setBrand(String brand) {
		this.brand = brand;
	}
	/**
	 * @return the colour
	 */
	public Colour getColour() {
		return colour;
	}
	/**
	 * @param colour the colour to set
	 */
	public void setColour(Colour colour) {
		this.colour = colour;
	}
	/**
	 * @return the size
	 */
	public float getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(float size) {
		this.size = size;
	}
	/**
	 * @return the price
	 */
	public float getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(float price) {
		this.price = price;
	}
	
	
}
