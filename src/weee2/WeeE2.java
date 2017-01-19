/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weee2;

import eventlog.EEventLogException;
import eventlog.EventJournalFactory;
import eventlog.IEventJournal;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.oauth.OAuthService;
import org.scribe.model.Verb;
import org.scribe.model.Token;

/**
 *
 * @author hbakiewicz
 */
public class WeeE2 {

    private IEventJournal log;
    public String version = "PriceImp - 0.1";
    public String current_path;
    SimpleDateFormat sdf = new SimpleDateFormat("ddMyyhhmm");
    public String edi_date = sdf.format(new Date());
    public String dbname, dbuser, dbpassword, dbport, dbconnectstring, log_lvl, przel, edi_pref, store_code, liv_location, liv_output;
    public String output_path;
    public dbManager dbm;

    private static final String NETWORK_NAME = "Woocommerce";
    private static final String ORDERS_COUNT = "http://www.jpcosmetics.pl/wc-api/v3/orders/count";
    private static final String PRODUCTS = "http://www.jpcosmetics.pl/wc-api/v3/products";
    private static final String SCOPE = "*"; //all permissions

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
 OAuthService service = new ServiceBuilder().provider(OneLeggedApi10.class)
                .apiKey("ck_b47b5e8c74436950f5984ffe40626e0e92adc4ab")
                .apiSecret("cs_8844811334721a8fe2d95ff9d43ae4ca6e517323")
                .signatureType(SignatureType.QueryString)
                .debug()
                /*.scope(SCOPE).*/
                .build();

        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected         resource...");
        OAuthRequest request = new OAuthRequest(Verb.GET, PRODUCTS);

        service.signRequest(new Token("", ""), request);
        Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getHeader("X-WC-TotalPages"));

        //System.out.println(response.getBody());
        parseJo(response.getBody());

        System.out.println();
        System.out.println("Thats it man! Go and build something awesome with Scribe! :)");

    }

    private static void parseJo(String str) {
        JSONObject obj = new JSONObject(str);
        //String pageName = obj.getJSONObject("product").getString("title");
        //System.out.println("Title: "+pageName);
        JSONArray arr = obj.getJSONArray("products");
        for (int i = 0; i < arr.length(); i++) {

            JSONObject post_id = arr.getJSONObject(i);
            //post_id.getString("title");
            System.out.println(post_id.getString("title"));
            System.out.println(post_id.getInt("id"));
            System.out.println(post_id.getInt("stock_quantity"));

        }
    }

}
