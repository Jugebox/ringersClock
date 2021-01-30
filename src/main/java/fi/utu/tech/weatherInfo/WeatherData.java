package fi.utu.tech.weatherInfo;

/*
 * Class presenting current weather
 * Is returned by weather service class
 */

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class WeatherData {
	private boolean temperature;
	private boolean notRaining;
	/*
	 * What kind of data is needed? What are the variable types. Define class
	 * variables to hold the data
	 */

	/*
	 * Since this class is only a container for weather data we only need to set the
	 * data in the constructor.
	 */

	public WeatherData(String temp, String rain) {
		if(Double.parseDouble(temp) > 0.0) this.temperature = true;
		else this.temperature = false;

		System.out.println("Is it warm? " + temperature);

		if(Double.parseDouble(rain) > 0.0) this.notRaining = false;
		else this.notRaining = true;

		System.out.println("Is it not raining? " + notRaining);
	}

	public boolean isNotRaining() {
		return notRaining;
	}

	public boolean isTemperature() {
		return temperature;
	}
}
