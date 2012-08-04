package org.elasticsearchfr.handson.beans;

import java.io.Serializable;

public class Food implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private FoodType type;
	private float price;
	private String tags[];
	
	
	public Food() {
	}
	
	/**
	 * @param type
	 * @param price
	 * @param tags
	 */
	public Food(FoodType type, float price, String[] tags) {
		this.type = type;
		this.price = price;
		this.tags = tags;
	}

	/**
	 * @return the type
	 */
	public FoodType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(FoodType type) {
		this.type = type;
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

	/**
	 * @return the tags
	 */
	public String[] getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	

	
}
