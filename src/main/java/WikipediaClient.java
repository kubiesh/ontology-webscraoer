import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WikipediaClient {
    private final static String httpsPrefix = "https://";
    private final static String apiURL = ".wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=&explaintext=&redirects=1&titles=";
    
    private static String responseBody;
    private static List<WikipediaPageData> pageDataList;
   

    public static void scrapArticleContent(String country, String article) {
        clearClassFields();

        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target(httpsPrefix.concat(country).concat(apiURL).concat(article));
        Invocation.Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON);
        
        Response response = request.get();
        if (response.getStatusInfo().getFamily() == javax.ws.rs.core.Response.Status.Family.SUCCESSFUL) {
            //LOGGER
            System.out.println("Success! " + response.getStatus());
        } else {
            //LOGGER
            System.out.println("ERROR! " + response.getStatus());
        }
        responseBody = request.get(String.class);
        
        //LOGGER
        System.out.println(responseBody);
        System.out.println("-----------------");

        parseData();
    }

    public static boolean hasPagesData() {
        return !pageDataList.isEmpty();
    }

    public static List<WikipediaPageData> getPagesData() {
        return pageDataList;
    }

    private static void clearClassFields() {
        responseBody = "";
        pageDataList = new LinkedList<>();
    }

    private static void parseData() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JsonNode pagesNode =  rootNode.path("query").path("pages");

        //-------------------------
        //Debugging
        Iterator<String> it = pagesNode.fieldNames();
        while(it.hasNext()) {
            String field = it.next();
            System.out.println(field);
        }
        //-------------------------


        //If there is no article found, break parsing
        JsonNode noResultsCheck = pagesNode.get("-1");
        if (noResultsCheck != null) {
            System.out.println("No article found");
            return;
        }
        Iterator<Map.Entry<String,JsonNode>> iterator = pagesNode.fields();
        while (iterator.hasNext()) {
            JsonNode articleNode = iterator.next().getValue();
            String title = articleNode.path("title").textValue();
            String content = articleNode.path("extract").asText();
            WikipediaPageData pageData = new WikipediaPageData(title, content);
            pageDataList.add(pageData);
            //LOGGER
            System.out.println("TITLE: " + title);
            System.out.println("-----------------");
            System.out.println("CONTENT: " + content);
        }
    }

    public static String entryToWikipediaFormat(String entry) {
        String entryInWikipediaFormat = entry.replaceAll("\\s","+");
        System.out.println("Wikiformat: "+entryInWikipediaFormat);
        return entryInWikipediaFormat;
    }
}
