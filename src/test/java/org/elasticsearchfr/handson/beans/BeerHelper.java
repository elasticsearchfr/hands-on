package org.elasticsearchfr.handson.beans;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class BeerHelper {

	public static Beer generate() {

		return new Beer(generateBrand(), generateColour(), Math.random()*2, Math.random()*10);
		
	}

	private static String generateBrand() {

		Long result = Math.round(Math.random() * 3);

		switch (result.intValue()) {
		case 0:
			return "Heineken";
		case 1:
			return "Grimbergen";
		case 2:
			return "Kriek";
		default:
			break;
		}

		return null;
	}
	
	
	private static Colour generateColour() {

		Long result = Math.round(Math.random() * 3);

		switch (result.intValue()) {
		case 0:
			return Colour.DARK;
		case 1:
			return Colour.PALE;
		case 2:
			return Colour.WHITE;
		default:
			break;
		}

		return null;
	}
	
	
	
	public static Beer toBeer(String json) throws JsonParseException, JsonMappingException, IOException {
		// instance a json mapper
		ObjectMapper mapper = new ObjectMapper(); // create once, reuse
		Beer beer = mapper.readValue(json.getBytes(), Beer.class);
		return beer;

	}
}
