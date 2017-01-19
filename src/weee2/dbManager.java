/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weee2;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcin
 */
public class dbManager {

    private final String connect_string;
    private final String user;
    private final String password;
    private final String baza;
    private Connection conn;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat dateShort = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat rok_2 = new SimpleDateFormat("YY");
    DateFormat rok_4 = new SimpleDateFormat("yyyy");
    DateFormat miesi = new SimpleDateFormat("MM");

    private long aktDokID;

    public dbManager(String connect_string, String user, String password, String baza) {
        this.connect_string = connect_string;
        this.user = user;
        this.password = password;
        this.baza = baza;

        //tworze połącznie do bazy danych 
        try {
            this.conn = dbConnect(connect_string + ";databaseName=" + baza, user, password);
            //aktDokID = new aktDokId(conn).wardok(); //pobieram aktualny DokId na jakim będę pracował 
        } catch (SQLException ex) {
            Logger.getLogger(dbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getBaza() {
        return baza;
    }

    private Connection dbConnect(String db_connect_string,
            String db_userid,
            String db_password
    ) throws SQLException {

        Connection lacze = null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            lacze = DriverManager.getConnection(db_connect_string,
                    db_userid, db_password);
            System.out.println("connected");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
        return lacze;

    }

    public void updateSql(String ss) throws SQLException {

        try {
            Statement st = this.conn.createStatement();
            System.out.println(ss);
            st.executeUpdate(ss);
        } catch (SQLException ex) {

            Logger.getLogger(dbManager.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    public ResultSet zapySql(String ss) throws SQLException {

        Statement st;
        try {
            st = this.conn.createStatement();
        } catch (SQLException ex) {

            Logger.getLogger(dbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        //System.out.println(ss);
        return st.executeQuery(ss);

    }

    //funkcja cene towarów 
    public String get_price(String towid) throws SQLException {
        String p = "";
        ResultSet n;
        n = zapySql("use " + baza + " select "
                + " Round(CenaDet*(1+(CAST( Stawka AS DECIMAL))/10000),2) as CenaDet,"
                + "CenaEw,CenaHurt "
                + "from Towar where TowId = " + towid);
        while (n.next()) {
            p = p + n.getString("CenaEw") + ";"
                    + n.getString("CenaDet") + ";"
                    + n.getString("CenaHurt") + ";";

        }

        byte ptext[] = p.getBytes(ISO_8859_1);
        String value = new String(ptext, UTF_8);
        return value;
    }

    public boolean check_price(String kod_kreskowy, String cena_det) throws SQLException {
        String p;
        ResultSet n;
        n = zapySql("use " + baza + " select "
                + " Round(CenaDet*(1+(CAST( Stawka AS DECIMAL))/10000),2) as CenaDet  "
                + "from Towar where Kod = '" + kod_kreskowy + "'");
        if (n.next()) {
            p = n.getString("CenaDet").replace(".0000000", "");
            System.out.println(p + " | " + cena_det);
            if (p.equals(cena_det)) {
                return true;
            }

        } else {
            System.out.println("brak towaru o kodzie " + kod_kreskowy);
            return true;
        }
        return false;
    }

    //funkcja zwraca listę kontrahentów 
    public String get_kontra(String lastUP) throws SQLException {
        String p = "";
        ResultSet n;
        n = zapySql("use " + baza + " select * from Kontrahent " + lastUP);
        while (n.next()) {
            p = p + n.getString("Nazwa") + ";" + n.getString("Ulica") + ";";

        }

        byte ptext[] = p.getBytes(ISO_8859_1);
        String value = new String(ptext, UTF_8);
        return value;
    }

    public boolean add_asort(String asort_name) throws SQLException {
        String p = "";
        ResultSet n;
        n = zapySql("use " + baza + " select nazwa  from asort  where nazwa = '" + asort_name.replace("'", "''") + "'");
        if (n.next()) {
            p = n.getString("Nazwa");

        } else {
            updateSql(" insert into asort(Nazwa,Marza,OpcjaMarzy,HurtRabat,OpcjaRabatu,NocNarzut,OpcjaNarzutu) values ('" + asort_name.replace("'", "''") + "',0,1,0,0,0,1)");
            return true;
        }
        return false;
    }

    //funkcja zwraca listę listę pozycji do walidacji  
    public void markAsValidated(String dokid) throws SQLException {

        updateSql("update dok set Opcja4 = 9 where dokid = " + dokid);
    }

    public boolean check_tow(String kod) throws SQLException {
        String p = "";
        ResultSet n;
        n = zapySql("use " + baza + " select nazwa  from towar  where kod = '" + kod + "'");
        if (n.next()) {
            p = n.getString("Nazwa");
            return true;

        }
        return false;
    }

    public int getTowIdByname(String tow_name) throws SQLException {

        ResultSet n;
        n = zapySql("use " + baza + " select TowId  from towar  where nazwa like upper('" + tow_name + "')");
        if (n.next()) {
            return n.getInt("TowId");

        }
        return 0;
    }

    public String getTowIdByOpis3(String Opis3) throws SQLException {

        ResultSet n;
        n = zapySql("use " + baza + " select TowId  from towar  where Opis3 =  '" + Opis3 + "'");
        if (n.next()) {
            return n.getString("TowId");

        } else {
            throw new SQLException();
        }

    }
    
        public String getTaxByOpis3(String Opis3) throws SQLException {

        ResultSet n;
        n = zapySql("use " + baza + " select Stawka  from towar  where Opis3 =  '" + Opis3 + "'");
        if (n.next()) {
            return n.getString("Stawka");

        } else {
            throw new SQLException();
        }

    }

    public void updOpis3TowById(int TowID, String Opis3) throws SQLException {

        updateSql("use " + baza + " update towar set Opis3 = " + Opis3 + ", Zmiana = getDate() where TowId = '" + TowID + "'");

    }

    public List<oTowar> getActiveListFromPCM() throws SQLException {
        List<oTowar> lito = new ArrayList<>();
        ResultSet n;
        n = zapySql("use " + baza + " select sum(Istw.StanMag) as stock , Towar.Opis3, Towar.Nazwa as Nazwa from Towar, "
                + "Istw where Towar.TowId = Istw.TowId and Towar.Opis3 <> '' "
                + "and towar.Aktywny = 1  and Istw.StanMag  > 0 group by Istw.StanMag,Towar.Opis3,Towar.Nazwa");
        while (n.next()) {
            oTowar ot = new oTowar();
            ot.setIlosc(n.getString("stock").replace(".0000", ""));
            ot.setTow_id(n.getInt("Opis3"));
            ot.setNazwa(n.getString("Nazwa"));
            // System.out.println("");
            lito.add(ot);
        }
        return lito;
    }

    public List<oTowar> getActiveTowarFromPCM() throws SQLException {
        List<oTowar> lito = new ArrayList<>();
        ResultSet n;
        n = zapySql("use " + baza + " select * from towar where aktywny = 1 ");
        while (n.next()) {
            oTowar ot = new oTowar();
            //ot.setIlosc(n.getString("stock").replace(".0000", ""));
            ot.setTow_id(n.getInt("TowId"));
            ot.setNazwa(n.getString("Nazwa").replace("7ML", ""));
            //System.out.println("");
            lito.add(ot);
        }
        return lito;
    }

    public boolean saveOrder(List<wooOrder> wor) throws SQLException {
        //String dokid = "0";
        int result = 1;

        for (int i = 0; i < wor.size(); i++) {
            //check if oreder exist in DB
            if (!chkOrderInDB(wor.get(i).getOrder_number())) {
                if (!insDok(wor.get(i))) {
                    result = 0;
                }
                 String dokid = getLastDokID();
                
                if (!insDokKontr(dokid, wor.get(i).getKonitrid())) {
                    result = 0;
                }
                if (!insTekst(dokid, "96", "'"+dateFormat.format(new Date()) + "; Status: oczekuje; Użytkownik: ADMIN'")) {
                    result = 0;
                }
                if (!insTekst(dokid, "17", "'"+wor.get(i).getComment()+ "'")) {
                    result = 0;
                }
                List<wooOrderLine> worde = wor.get(i).getOrderPoz();
                for (wooOrderLine orderLine : worde) {
                    if (!insPozDok(orderLine, dokid)) {
                        result = 0;
                    }

                }

            }

        }
        if (result == 1) {
            return true;
        } else {
            return false;
        }
    }

    public String getLastDokID() throws SQLException {

        ResultSet n;
        n = zapySql("use " + baza + " select (max (dokid ) ) as dokid  from dok ");
        if (n.next()) {

            return n.getString("dokid");

        }
        return "-1";
    }

    public boolean chkOrderInDB(int orderNumber) throws SQLException {
        String orde = String.valueOf(orderNumber);
        ResultSet n;
        n = zapySql("use " + baza + " select NrDok from dok where typdok = 49 and NrDok like '" + orde +"' or Nrdok like  '"+ orde +" !!!'");
        if (n.next()) {

            return true;

        }
        return false;
    }

    public boolean insDok(wooOrder wo) throws SQLException {
        String orderNumber = String.valueOf(wo.getOrder_number());
        if (!wo.isValid()) {
            //ordier is incomplete add !!! to order number 
        orderNumber = orderNumber + " !!!";
        }
        try {
            updateSql("use " + baza + " insert into Dok ("
                    + " UzId, MagId, Data, KolejnyWDniu,"
                    + " DataDod, DataPom, NrDok, TypDok, Aktywny,"
                    + " Opcja1, Opcja2, Opcja3, Opcja4, CenyZakBrutto, CenySpBrutto,"
                    + " FormaPlat, TerminPlat, PoziomCen, RabatProc,"
                    + " Netto, Podatek, NettoUslugi, PodatekUslugi,"
                    + " NettoDet, PodatekDet, NettoDetUslugi, PodatekDetUslugi,"
                    + " NettoMag, PodatekMag, NettoMagUslugi, PodatekMagUslugi,"
                    + " Razem, DoZaplaty, Zaplacono,"
                    + " Kwota1, Kwota2, Kwota3, Kwota4, Kwota5, Kwota6, Kwota7, Kwota8, Kwota9, Kwota10, Kwota11, Kwota12,"
                    + " Param1, Param2, Param3, Param4, Param5, Param6, EksportFK, Zmiana)"
                    + "select "
                    + " " + wo.getUzId() + ", " + wo.getMagId() + ", getdate(), 2,"
                    + " getdate(), getdate(), '" + orderNumber + "', 49, 1,"
                    + " 0, 0, 0, 0, 0, 0,"
                    + " 0, 0, 1, 0.0,"
                    + " (select  ROUND("+wo.getSubtotal()+"/(1+(CAST( 2300 AS DECIMAL))/10000),2)),"
                    + " (select "+wo.getSubtotal()+" - ROUND("+wo.getSubtotal()+"/(1+(CAST( 2300 AS DECIMAL))/10000),2)),"
                    + " 0.0, 0.0,"
                    + " 0.0, 0.0, 0.0, 0.0,"
                    + " 0.0, 0.0, 0.0, 0.0,"
                    + " " + wo.getSubtotal() + ", 0.0, 0.0,"
                    + " 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,"
                    + " 0, 0, 0, 0, 0, 0, 0, getdate()");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean insDokKontr(String DokId, String KontrId) throws SQLException {
        try {
            updateSql("use " + baza + " insert into DokKontr (DokId, KontrId) "
                    + " values (" + DokId + ","
                    + KontrId + ")");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean insTekst(String DokId, String Znaczenie, String Tekst) throws SQLException {
        try {
            updateSql("use " + baza + " insert into TekstDok(DokId, Znaczenie, Tekst)"
                    + "values (" + DokId + ","
                    + Znaczenie + ","
                    + Tekst + ")");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean insPozDok(wooOrderLine woOrd, String DokId) throws SQLException {
        try {
            updateSql("use " + baza + " insert into PozDok (DokId, Kolejnosc, NrPozycji, TowId, TypPoz,"
                    + " IloscPlus, IloscMinus, PoziomCen, Metoda, CenaDomyslna,"
                    + " CenaPrzedRab, RabatProc, CenaPoRab, Wartosc,"
                    + " CenaDet, CenaMag, Stawka, TypTowaru, IleWZgrzewce)"
                    + " values (" + DokId + ","
                    + " (select ISNULL(max(kolejnosc+1),1) from PozDok where dokid = " + DokId + ")," //Kolejnosc
                    + " (select ISNULL(max(NrPozycji+1),1) from PozDok where dokid = " + DokId + ")," //NrPozycji
                    + woOrd.getPcmTowId() + "," //TowId
                    + "0,"
                    + Integer.toString(woOrd.getQuantity()) + "," //IloscPlus
                    + " 0," //IloscMinus
                    + " 1," //PoziomCen
                    + " 3," //Metoda
                    + " (select  ROUND(" + woOrd.getPrice().replace(",", ".") + "/(1+(CAST( Stawka AS DECIMAL))/10000),2) from towar where towid = " + woOrd.getPcmTowId() + ")," //CenaDomyślna
                    + " (select  ROUND(" + woOrd.getPrice().replace(",", ".") + "/(1+(CAST( Stawka AS DECIMAL))/10000),2) from towar where towid = " + woOrd.getPcmTowId() + ")," //CenaPrzedRab
                    + " 0," //RabatProc
                    + " (select  ROUND(" + woOrd.getPrice().replace(",", ".") + "/(1+(CAST( Stawka AS DECIMAL))/10000),2) from towar where towid = " + woOrd.getPcmTowId() + ")," //CenaPoRab
                    + " (select  ROUND(" + Integer.toString(woOrd.getQuantity()) + " * " + woOrd.getPrice().replace(",", ".") + "/(1+(CAST( Stawka AS DECIMAL))/10000),2) from towar where towid = " + woOrd.getPcmTowId() + ")," //Warotsc
                    + " (select  CenaDet from towar where towid = " + woOrd.getPcmTowId() + ")," //CenaDet
                    + " (select  CenaMag from Istw where towid = " + woOrd.getPcmTowId() + ")," //CenaMag
                    + " (select  Stawka from towar where towid = " + woOrd.getPcmTowId() + ")," //Stawka
                    + " 0," //TypTowaru
                    + " 0.0)" //IleWZgrzewce
            );
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

}
