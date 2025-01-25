package etu2802.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		Config.loadProperties(context);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// Nettoyage si n�cessaire
	}
}
