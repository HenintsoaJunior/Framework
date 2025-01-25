package etu2802.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletContext;

public class Config {
	private static final Properties prop = new Properties();

	// M�thode pour initialiser les propri�t�s en utilisant ServletContext
	public static void loadProperties(ServletContext context) {
		try (InputStream input = context.getResourceAsStream("/WEB-INF/conf/config.properties")) {
			if (input == null) {
				System.out.println("Sorry, unable to find database.properties");
				return;
			}
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static String getSESSION() {
		return prop.getProperty("SESSION");
	}
}
