/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weee2;

import eventlog.IEventJournal;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 *
 * @author hbakiewicz
 */
public final class OrderProcessor implements Runnable {
    
    private String ck_key;
    private String cs_key;
    private static IEventJournal log;
    private Level lvl;
    private String cfg_fil;
    private static String NETWORK_NAME = "www.jpcosmetics.pl";
    private static final String ORDERS_COUNT = "http://www.jpcosmetics.pl/wc-api/v3/orders/count";
    private static final String PRODUCTS_COUNT = "products/count";
    private static String PRODUCTS = "products";
    private static String ORDERS = "orders";
    private static final String SCOPE = "*"; //all permissions
    private static String API_ADRESS;
    private static String API_VER;
    private static String KONTRID;
    private static String UZID;
    private static String MAGID;
    private static int page_count = 0;
    private static List<oTowar> list = new ArrayList<>();
    private OAuthService service;
    private JTextArea Jta;
    private dbManager dbm;
    private JProgressBar jprbar;
    private String comment;
    DateFormat dateShort = new SimpleDateFormat("yyyy-MM-dd");
    private boolean mode = false;
    
    public OrderProcessor(String ck_key, String cs_key, IEventJournal lo, Level lv, String _cfg_file, JTextArea _jta, dbManager _dbm, JProgressBar _jprbar) {
        this.ck_key = ck_key;
        this.cs_key = cs_key;
        OrderProcessor.log = lo;
        this.lvl = lv;
        this.cfg_fil = _cfg_file;
        this.Jta = _jta;
        this.dbm = _dbm;
        this.jprbar = _jprbar;
        
        Properties prop = new Properties();
        
        try {
            
            InputStream input = new FileInputStream(cfg_fil);
            
            prop.load(input);
            NETWORK_NAME = prop.getProperty("NETWORK_NAME", "");
            API_ADRESS = prop.getProperty("API_ADRESS", "");
            API_VER = prop.getProperty("API_VER", "");
            KONTRID = prop.getProperty("kontrid", "1");
            UZID = prop.getProperty("uzid", "1");
            MAGID = prop.getProperty("magid", "1");
            
            PRODUCTS = API_ADRESS + API_VER + "products";
            ORDERS = API_ADRESS + API_VER + ORDERS;
            service = new ServiceBuilder().provider(OneLeggedApi10.class)
                    .apiKey(ck_key)
                    .apiSecret(cs_key)
                    .signatureType(SignatureType.QueryString)
                    .debug()
                    .build();
            
        } catch (IOException ex) {
            System.out.println("brak pliku config.properties");
            System.exit(1);
        }
        
    }
    
    public void setMode(boolean mode) {
        this.mode = mode;
    }
    
    public void getOrders() {
        // Now let's go and ask for a protected resource!
        log.logEvent(Level.FINER, "Ustanawiam połącznie ze sklepem, zamowień: " + ORDERS_COUNT);
        ORDERS = API_ADRESS + API_VER + "orders";
        Jta.append("Łączę za sklepem, adres: " + ORDERS + "\n");
        /*
            Order status. By default are available the status: 
            pending, processing, on-hold, completed, cancelled, refunded and failed.
         */
        //OAuthRequest request = new OAuthRequest(Verb.GET, ORDERS + "?status=processing&filter[created_at_min]=2017-01-04");
        OAuthRequest request = new OAuthRequest(Verb.GET, ORDERS + "?status=processing&filter[created_at_min]="+dateShort.format(new Date()));
        log.logEvent(Level.FINER, "OAuthRequest: " + ORDERS);
        service.signRequest(new Token("", ""), request);
        log.logEvent(Level.FINER, "Request: " + request);
        Response response = request.send();
        log.logEvent(Level.INFO, "Odpowiedź serwera: " + response.getCode());
        log.logEvent(Level.FINER, "Odpowiedź serwera: " + response.getBody());
        parseOrder(response.getBody());
        
    }
    
    @Override
    public void run() {
        getOrders();
    }
    
    public void parseOrder(String josnoOrder) {
        List<wooOrder> worList = new ArrayList<>();
        try {
            JSONObject obk = new JSONObject(josnoOrder);
            //JSONArray jOrPoz = new JSONArray(new JSONObject(josnoOrder).getJSONArray("orders"));
            JSONArray jOrPoz = obk.getJSONArray("orders");
            
            log.logEvent(Level.FINER, "Wejście do parseOrder ");
            log.logEvent(Level.INFO, "Pobrano zamówienia, do przetworzenia : " + jOrPoz.length());
            Jta.append("Pobrano zamówienia, do przetworzenia : " + jOrPoz.length() + "\n");
            for (int i = 0; i < jOrPoz.length(); i++) {
                wooOrder wo = new wooOrder();
                JSONObject jso = jOrPoz.getJSONObject(i);
                log.logEvent(Level.INFO, "Zamówienie : " + jso.getInt("order_number"));
                Jta.append("Zamówienie : " + jso.getInt("order_number") + "\n");
                wo.setOrder_number(jso.getInt("order_number"));
                wo.setCreated_at(jso.getString("created_at"));
                wo.setSubtotal(jso.getString("subtotal"));
                wo.setOrderPoz(prseOrderPoz(jso));
                //check if order is complete 

                if (!(wo.getOrderPoz().size() == jso.getJSONArray("line_items").length())) {
                    wo.setValid(false);
                }
                wo.setComment(comment);
                wo.setKonitrid(KONTRID);
                wo.setMagId(MAGID);
                wo.setUzId(UZID);
                
                worList.add(wo);
            }
        } catch (JSONException ec) {
            log.logEvent(Level.SEVERE, "parseOrder ", ec);
        }
        log.logEvent(Level.FINER, "Koniec  do parseOrder ");
        log.logEvent(Level.FINER, "parseOrder,  Zapisuję do bazy  do : ");
        try {
            dbm.saveOrder(worList);
        } catch (SQLException ex) {
            log.logEvent(Level.SEVERE, " parseOrder ", ex);
        }
        log.logEvent(Level.FINER, "parseOrder Koniec zapisu do bazy");
        Jta.append("Koniec zapisu do bazy\n");
        if (mode) {
            System.exit(0);
        }
        
    }
    
    public List<wooOrderLine> prseOrderPoz(JSONObject josPoz) {
        comment = "";
        log.logEvent(Level.FINER, "Wejście  do  prseOrderPoz ");
        
        JSONArray jpoz = josPoz.getJSONArray("line_items");
        log.logEvent(Level.FINER, "Pozycji : " + jpoz.length() + " w zamówieniu : " + josPoz.getInt("order_number"));
        Jta.append("Pozycji : " + jpoz.length() + " , w zamówieniu : " + josPoz.getInt("order_number") + "\n");
        List<wooOrderLine> lit = new ArrayList<>();
        try {
            for (int i = 0; i < jpoz.length(); i++) {
                
                JSONObject row = jpoz.getJSONObject(i);
                wooOrderLine orLine = new wooOrderLine(row.getInt("id"),
                        row.getString("total"),
                        row.getString("price"),
                        row.getInt("quantity"),
                        row.getString("name"),
                        row.getInt("product_id"));
                try {
                    orLine.setPcmTowId(dbm.getTowIdByOpis3(String.valueOf(row.getInt("product_id"))));
                    orLine.setTax_level(dbm.getTaxByOpis3(String.valueOf(row.getInt("product_id"))));
                    lit.add(orLine);
                } catch (SQLException ex) {
                    comment = comment + row.getInt("product_id") + " | ";
                    log.logEvent(Level.FINER, "Brak w bazie powiązania z product ID : " + row.getInt("product_id")
                            + " w zamówieniu " + josPoz.getInt("order_number"));
                    Jta.append("Brak w bazie powiązania z product ID : " + row.getInt("product_id")
                            + " w zamówieniu " + josPoz.getInt("order_number") + "\n");
                }
                
            }
        } catch (JSONException ex) {
            log.logEvent(Level.SEVERE, " prseOrderPoz ", ex);
        }
        log.logEvent(Level.FINER, "Koniec  prseOrderPoz ");
        return lit;
    }
    
}
