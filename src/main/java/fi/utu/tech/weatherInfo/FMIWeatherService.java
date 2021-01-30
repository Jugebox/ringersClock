package fi.utu.tech.weatherInfo;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.Serializable;
import java.net.URL;

public class FMIWeatherService implements Serializable {

	private final String CapURL = "https://opendata.fmi.fi/wfs?request=GetCapabilities";
	private final String FeaURL = "https://opendata.fmi.fi/wfs?request=GetFeature";
	private final String ValURL = "https://opendata.fmi.fi/wfs?request=GetPropertyValue";
	//private final String DataURL = "http://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::forecast::hirlam::surface::point::multipointcoverage&place=turku&";
	private static final String DataURL = "https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::simple&place=turku&maxlocations=1&parameters=t2m,ri_10min";
	/*
	 * In this method your are required to fetch weather data from The Finnish
	 * Meteorological Institute. The data is received in XML-format.
	 */

	public WeatherData getWeather() {
		String rain = null;
		String temp = null;
		Document data = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			data = builder.parse(new URL(DataURL).openStream());

			data.getDocumentElement().normalize();

			System.out.println("Root element:" + data.getDocumentElement().getNodeName());

			NodeList nodeList = data.getElementsByTagName("wfs:member");
			for (int i = 0; i < 2; i++) {

				Node node = nodeList.item(i);
				NodeList children = node.getChildNodes();

				for(int j = 0; j < children.getLength(); j++) {

					Node child = children.item(j);
					NodeList kids = child.getChildNodes();

					for(int k = 0; k < kids.getLength(); k++ ) {
						if(node.getNodeType() == Node.ELEMENT_NODE) {

							Element element = (Element)node;
							if(element.getElementsByTagName("BsWfs:ParameterName").item(0).getTextContent().equals("t2m")) {
								temp = element.getElementsByTagName("BsWfs:ParameterValue")
										.item(0)
										.getTextContent();
							}
							if(element.getElementsByTagName("BsWfs:ParameterName").item(0).getTextContent().equals("ri_10min")) {
								rain = element.getElementsByTagName("BsWfs:ParameterValue")
										.item(0)
										.getTextContent();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Temperature:"+ temp);
		System.out.println("Raining:"+ rain);

		WeatherData weatherData = new WeatherData(temp, rain);

		return weatherData;
	}

}
