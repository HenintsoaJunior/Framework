package etu2802;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 10,  // 10 MB
    maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class FrontController extends HttpServlet {
    private HashMap<String, Mapping> mappingUrls;

    public HashMap<String, Mapping> getMappingUrls() {
        return mappingUrls;
    }

    public void setMappingUrls(HashMap<String, Mapping> mappingUrls) {
        this.mappingUrls = mappingUrls;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            String pkg = this.getInitParameter("package");
            ServletContext context = getServletContext();
            mappingUrls = Utils.allMappingUrls(context, pkg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            String url = request.getRequestURI();
            String contextPath = request.getContextPath();
            url = url.substring(contextPath.length());

            Mapping map = mappingUrls.get(url);
            if (map == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                Utils.generateNotFoundPage(out);
                return;
            }

            String packageNames = this.getInitParameter("package");
            String[] packages = packageNames.split(",");
            boolean foundClass = false;

            for (String packageName : packages) {
                packageName = packageName.trim();
                String fullClassName = packageName + "." + map.getClassName();
                
                try {
                    Class<?> clazz = Class.forName(fullClassName);
                    Object object = clazz.newInstance();
                    Utils.dispatchModelView(request, response, object, url, mappingUrls);
                    foundClass = true;
                    break;
                } catch (ClassNotFoundException e) {
                    // Continue to next package
                    continue;
                }
            }

            if (!foundClass) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                Utils.generateNotFoundPage(out);
            }

        } catch (Exception e) {
            // Log the error for debugging but don't show it to the user
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>500 Internal Server Error</title>");
            out.println("<style>");
            out.println("body {font-family: Tahoma, Arial, sans-serif; color: #333; background-color: white;}");
            out.println("h1 {color: #FF4444; text-align: center;}");
            out.println(".container {width: 80%; margin: 0 auto; text-align: center;}");
            out.println("</style></head>");
            out.println("<body><div class='container'>");
            out.println("<h1>500 Internal Server Error</h1>");
            out.println("<p>An internal server error occurred. Please try again later.</p>");
            out.println("</div></body></html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}