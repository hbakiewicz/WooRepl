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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 *
 * @author hbakiewicz
 *
 * ck_b47b5e8c74436950f5984ffe40626e0e92adc4ab
 * cs_8844811334721a8fe2d95ff9d43ae4ca6e517323
 */
public class productManager implements Runnable {

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
    private static int page_count = 0;
    private static List<oTowar> list = new ArrayList<>();
    private OAuthService service;
    private JTextArea Jta;
    private dbManager dbm;
    private JProgressBar jprbar;
    private int mode = 0;
    private int packet = 10;

    public productManager(String ck_key, String cs_key, IEventJournal lo, Level lv, String _cfg_file, JTextArea _jta, dbManager _dbm, JProgressBar _jprbar) {
        this.ck_key = ck_key;
        this.cs_key = cs_key;
        productManager.log = lo;
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
            packet = Integer.valueOf(prop.getProperty("packet", ""));
            PRODUCTS = API_ADRESS + API_VER + "products";
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

    public void setMode(int mode) {
        this.mode = mode;
    }

    private void connectTo() {
        try {

            log.logEvent(lvl, "=== " + NETWORK_NAME + "'s OAuth Workflow ===");

            // Now let's go and ask for a protected resource!
            log.logEvent(Level.FINER, "Ustanawiam połącznie ze sklepem  " + PRODUCTS);
            Jta.append("Łączę za sklepem, adres: " + PRODUCTS + "\n");
            OAuthRequest request = new OAuthRequest(Verb.GET, PRODUCTS);
            log.logEvent(Level.FINER, "OAuthRequest: " + PRODUCTS);
            service.signRequest(new Token("", ""), request);
            log.logEvent(Level.FINER, "Request: " + request);
            Response response = request.send();
            log.logEvent(Level.INFO, "Odpowiedź serwera: " + response.getCode());
            log.logEvent(Level.FINER, "Odpowiedź serwera: " + response.getBody());
            //System.out.println(response.getBody());
            Map<String, String> hed = response.getHeaders();
            String t = hed.get("X-WC-TotalPages");
            if (!t.isEmpty()) {
                log.logEvent(Level.INFO, "Liczba stron do przetworzenia : " + response.getHeader("X-WC-TotalPages"));
                page_count = Integer.valueOf(response.getHeader("X-WC-TotalPages"));
                Jta.append("Liczba stron do przetworzenia : " + page_count + "\n");
                jprbar.setVisible(true);
                jprbar.setMaximum(page_count);
            } else {
                log.logEvent(Level.INFO, "Pusty nagłówek brak stron do przetworzenia  ");
                Jta.append("Pusty nagłówek brak stron do przetworzenia  \n");
            }
        } catch (OAuthException e) {
            log.logEvent(Level.SEVERE, "connectTo : ", e);
        }
    }

    private static void parseJo(String str) {

        JSONObject obj = new JSONObject(str);
        //String pageName = obj.getJSONObject("product").getString("title");
        //System.out.println("Title: "+pageName);
        JSONArray arr = obj.getJSONArray("products");
        for (int i = 0; i < arr.length(); i++) {

            JSONObject post_id = arr.getJSONObject(i);
            oTowar otw = new oTowar();
            try {
                otw.setCenaSp(post_id.getString("price"));
            } catch (JSONException e) {
                log.logEvent(Level.SEVERE, "price : ", e);
            }
            try {
                otw.setNazwa(post_id.getString("title"));
            } catch (JSONException e) {
                log.logEvent(Level.SEVERE, "title : ", e);
            }
            try {
                otw.setTow_id(post_id.getInt("id"));
            } catch (JSONException e) {
                log.logEvent(Level.SEVERE, "id : ", e);
            }
            try {
                otw.setIlosc(Integer.toString(post_id.getInt("stock_quantity")));
            } catch (JSONException e) {
                log.logEvent(Level.SEVERE, "stock_quantity : ", e);
            }
            try {
                log.logEvent(Level.FINER, "Tow form shop: Nazwa "
                        + post_id.getString("title") + ";"
                        + post_id.getInt("id") + ";"
                        + post_id.getInt("stock_quantity") + ";"
                        + post_id.getString("price"));
            } catch (JSONException e) {
                log.logEvent(Level.SEVERE, " to log  : ", e);
            }
            list.add(otw);

        }

    }

    public List<oTowar> prepereTowListAll() {
        for (int i = 1; i < page_count; i++) {
            jprbar.setValue(i);
            jprbar.setString("Przetwarzam : " + i + " z " + page_count);
            jprbar.repaint();
            log.logEvent(Level.INFO, "Przetwarzam : " + i + " z " + page_count);
            //Jta.append("Przetwarzam : " + i + " z " + page_count + "\n");
            OAuthRequest request = new OAuthRequest(Verb.GET, PRODUCTS + "?page=" + i);
            log.logEvent(Level.FINER, "OAuthRequest: " + PRODUCTS);
            service.signRequest(new Token("", ""), request);
            log.logEvent(Level.FINER, "Request: " + request);
            Response response = request.send();
            log.logEvent(Level.FINER, "Odpowiedź serwera: " + response.getCode());

            parseJo(response.getBody());

        }
        log.logEvent(Level.INFO, "Pobrano wszystkie towary  ");
        Jta.append("Pobrano wszystkie towary \n");
        jprbar.setVisible(false);
        return list;

    }

    @Override
    public void run() {
        /*
        mode 
        0: connect get  all products from shop and try to find it equivaletn in PcMarket by name  
        1: get all tow from PCmarket which have Product Id in Indeks1 and update stock in shop  
         */
        switch (mode) {
            case 0: {
                connectTo();
                prepereTowListAll();
                try {
                    margeTowByName();
                } catch (SQLException ex) {
                    log.logEvent(Level.SEVERE, "getTowList run ", ex);
                }
                break;
            }
            case 1: {
                updateStockById();
                break;
            }

            case 2: {
                getOrder();
                break;
            }

            case 3: {
                updeteBulkStockInShop();
                break;
            }
            case 4: {
                updeteBulkStockInShop();
                System.exit(0);
                break;
            }
            case 5: {
                updateStockById();
                System.exit(0);
                break;
            }

        }

    }

    private void margeTowByName() throws SQLException {
        int updeted = 0;
        int skipped = 0;
        int current = 0;
        List<oTowar> PcmListTow = dbm.getActiveTowarFromPCM();
        //PcmListTow = 
        jprbar.setVisible(true);
        jprbar.setMaximum(list.size());
        for (oTowar towar : list) {
            current++;
            jprbar.setValue(current);

            String towid = findName(PcmListTow, towar.getNazwa());
            if (!"0".equals(towid)) {
                updeted++;
                dbm.updOpis3TowById(Integer.valueOf(towid), String.valueOf(towar.getTow_id()));

            } else {
                skipped++;
                log.logEvent(Level.INFO, "nie powiązano towaru  o nazwie " + towar.getNazwa());
            }

        }
        jprbar.setVisible(false);
        Jta.append("Znaleziono towarów w bazie : " + updeted + "\n");
        Jta.append("Pominięto  : " + skipped + "\n");
        log.logEvent(Level.INFO, "Znaleziono towarów w bazie : " + updeted);
        log.logEvent(Level.INFO, "Pominięto  : " + skipped);

    }

    private boolean updeteStockInShop(int id, int stock) {

        try {

            OAuthRequest request = new OAuthRequest(Verb.PUT, PRODUCTS + "/" + id);
            log.logEvent(Level.FINER, "updeteStockInShop:OAuthRequest: " + PRODUCTS + "/" + id);
            //String josno = "{\"product\": {\"stock_quantity\":"+stock+"}}";
            String jsono = getJSON(stock).toString();
            log.logEvent(Level.FINER, "updeteStockInShop:getJSON: " + jsono);
            //"product":[""],"stock_quantity":[0]}
            //request.addPayload(getJSON(Stock).toString());
            //System.out.println(jsono);
            request.addPayload(jsono);
            service.signRequest(new Token("", ""), request);

            log.logEvent(Level.FINER, "updeteStockInShop:Request: " + request);
            Response response = request.send();
            //log.logEvent(Level.FINER, "updeteStockInShop:Odpowiedź serwera getCode: " + response.getCode());
            //log.logEvent(Level.FINER, "updeteStockInShop:Odpowiedź serwera getBody: " + response.getBody());
            if (response.getCode() == 200) {
                return true;
            } else {
                log.logEvent(Level.FINER, "updeteStockInShop:Odpowiedź serwera getCode: " + response.getCode());
                log.logEvent(Level.FINER, "updeteStockInShop:Odpowiedź serwera getBody: " + response.getBody());
                return false;
            }
        } catch (Exception ex) {
            log.logEvent(Level.SEVERE, "updeteStockInShop ", ex);
        }
        return false;
    }

    private boolean updeteBulkStockInShop() {

        try {

           // final OAuthRequest request = new OAuthRequest(Verb.PUT, PRODUCTS + "/bulk");
            log.logEvent(Level.FINER, "updeteStockInShop:OAuthRequest: " + PRODUCTS + "/bulk");
            Jta.append("Rozpoczynam aktualizację stanów na sklepie : " + NETWORK_NAME + "\n");
            int _loccount = 1;
            List<ListDivBy100> _listDivBy100 = prepListDivBy(dbm.getActiveListFromPCM());
            Jta.append("Zaktualizowano : " + (_loccount-1) + " z " + _listDivBy100.size() + "  \n");
            for (ListDivBy100 listDivBy100 : _listDivBy100) {
                OAuthRequest request = new OAuthRequest(Verb.PUT, PRODUCTS + "/bulk");
                String jsono = getJSONBulk(listDivBy100.getListDiv());
                log.logEvent(Level.FINER, "updeteStockInShop:getJSON: " + jsono);

                request.addPayload(jsono);
                service.signRequest(new Token("", ""), request);

                log.logEvent(Level.FINER, "updeteStockInShop:Request: " + request);
                Response response = request.send();
                //log.logEvent(Level.FINER, "updeteStockInShop:Odpowiedź serwera getCode: " + response.getCode());
                //log.logEvent(Level.FINER, "updeteStockInShop:Odpowiedź serwera getBody: " + response.getBody());
                if (response.getCode() == 200) {
                    log.logEvent(Level.FINER, "updeteStockInShop : zaktualizowano towary na sklepie " + response.getBody());
                    Jta.append("Zaktualizowano : " + _loccount + " z " + _listDivBy100.size() + "  \n");
                    //return true;
                } else {
                    Jta.append("Coś poszło nie tak !!!,  więcej szczegółów w logu aplikacji  \n");
                    log.logEvent(Level.FINER, "updeteStockInShop:Odpowiedź serwera getCode: " + response.getCode());
                    log.logEvent(Level.FINER, "updeteStockInShop:Odpowiedź serwera getBody: " + response.getBody());
                    return false;
                }
                _loccount++;
                
            }
            Jta.append("Zakończono aktualizację towarów  na sklepie \n");
            return true;

        } catch (Exception ex) {
            log.logEvent(Level.SEVERE, "updeteStockInShop ", ex);
        }
        return false;
    }

    private List<ListDivBy100> prepListDivBy(List<oTowar> li)  {
        int coun = 0;
        ArrayList<ListDivBy100> listb = new ArrayList<>();
       
        ArrayList<oTowar> lista = new ArrayList<>();
        for (oTowar towar : li) {
            lista.add(towar);
            coun++;
            if (coun == packet) {
                ListDivBy100 ltby1 = new ListDivBy100();
                ltby1.setListDiv((List<oTowar>) lista.clone());
                listb.add(ltby1);
                coun = 0;
                lista.clear();

                //return listb;
            }

        }
        ListDivBy100 ltby2 = new ListDivBy100();
        ltby2.setListDiv((List<oTowar>) lista.clone());
        listb.add(ltby2);
        return listb;

    }

    private JSONObject getJSON(int Stock) {

        //pro.append("stock_quantity", Stock);
        obj_product prod = new obj_product();
        prod.setProduct(new product_details(Stock));

        JSONObject jobProducts = new JSONObject(prod);
        //jobProducts.append("product", );
        //jobProducts.append("stock_quantity", Stock);
        return jobProducts;

    }

    private String getJSONBulk(List<oTowar> StockList) {
        //JSONObject pro = new JSONObject();
        //pro.append("stock_quantity", Stock);
        //JSONArray arr = new JSONArray();
        obj_product prod = new obj_product();
        JSONArray jobProducts = new JSONArray();
        for (oTowar lista : StockList) {

            prod.products.add(new product_details(Integer.valueOf(lista.getIlosc()), lista.getTow_id()));
            jobProducts = new JSONArray(prod.getProducts());
            //arr.put(jobProducts);

        }

        JSONObject jobProd = new JSONObject(prod);
        System.out.println(jobProd.toString());
        //jobProducts.append("product", );
        //jobProducts.append("stock_quantity", Stock);

        return jobProd.toString();

    }

    private void updateStockById() {
        int current = 0;

        jprbar.setVisible(true);
        //jprbar.setMaximum(listo.size());
        log.logEvent(Level.INFO, "Aktualizacja stanów na sklepie : " + API_ADRESS);
        Jta.append("Aktualizacja stanów na sklepie : " + API_ADRESS + "\n");
        try {
            List<oTowar> listo = dbm.getActiveListFromPCM();
            jprbar.setMaximum(listo.size());
            for (oTowar towar : listo) {
                current++;

                int ilosc = Integer.valueOf(towar.getIlosc());

                if (updeteStockInShop(towar.getTow_id(), ilosc)) {

                    jprbar.setValue(current);
                    jprbar.repaint();
                    Jta.append("Aktualizuję towar o nazwie : " + towar.getNazwa() + "\n");
                    log.logEvent(Level.INFO, "Aktualizuję towar o nazwie : " + towar.getNazwa() + " ; Stan : " + towar.getIlosc());
                    Jta.append("Stan : " + towar.getIlosc() + "\n");
                } else {
                    Jta.append("Błąd aktualizacji towaru : " + towar.getNazwa() + "\n");
                    log.logEvent(Level.FINE, "Błąd aktualizacji towaru : " + towar.getNazwa());
                }
            }
            jprbar.setVisible(false);
            Jta.append("Zakończono aktualizację ");
            log.logEvent(Level.INFO, "Zakończono aktualizację ");
        } catch (SQLException ex) {
            log.logEvent(Level.SEVERE, "updateStockById ", ex);
        }
    }

    public void getOrder() {
        // Now let's go and ask for a protected resource!
        log.logEvent(Level.FINER, "Ustanawiam połącznie ze sklepem, zamowień: " + ORDERS_COUNT);
        ORDERS = API_ADRESS + API_VER + "orders";
        Jta.append("Łączę za sklepem, adres: " + ORDERS + "\n");
        /*
            Order status. By default are available the status: 
            pending, processing, on-hold, completed, cancelled, refunded and failed.
         */

        OAuthRequest request = new OAuthRequest(Verb.GET, ORDERS + "?status=on-hold&filter[created_at_min]=2017-01-04");
        log.logEvent(Level.FINER, "OAuthRequest: " + ORDERS);
        service.signRequest(new Token("", ""), request);
        log.logEvent(Level.FINER, "Request: " + request);
        Response response = request.send();
        log.logEvent(Level.INFO, "Odpowiedź serwera: " + response.getCode());
        log.logEvent(Level.FINER, "Odpowiedź serwera: " + response.getBody());
        //System.out.println(response.getBody());

    }

    private String findName(List<oTowar> loti, String wooName) {
        //String text = "SEMILAC LAKIER HYBRYDOWY 160 I`M NOT SURE - 7ML";
        String text = wooName.toUpperCase();

        for (oTowar towar : loti) {
            if (towar.getNazwa().contains("SEMILAC LAKIER 106") && text.contains("Semilac lakier hybrydowy 106 Wet Marengo - 7 ml")) {
                System.out.println("weee2.productManager.findName()");
            }

            //String patternString = ".*SEMILAC.* .*LAKIER.* .*160.* .*7ML.*"; 
            //.*YOUR.*.*NATURAL.*.*SIDE.*.*OLEJ.*.*Z.*.*PESTEK.*.*MALIN.*.*TŁOCZONY.*.*NA.*.*ZIMNO.*.*10ML.*
            String nazwa = towar.getNazwa().toUpperCase();
            boolean matches = check(nazwa.split(" "), text);
            if (matches) {
                System.out.println("wooName: " + wooName + " | PcmName :" + towar.getNazwa());
                return String.valueOf(towar.getTow_id());
            }
        }

        return "0";

    }

    private String preepereName(String[] ty) {
        String f = "";
        for (String ty1 : ty) {
            f = f + " .*" + ty1 + ".*";
        }
        return f;
    }

    public static boolean check(String[] a, String b) {
        boolean ret = true;
        for (String string : a) {
            if (!b.contains(string)) {
                ret = false;
            }
        }
        return ret;
    }
}
