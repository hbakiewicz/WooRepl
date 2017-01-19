/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weee2;

import eventlog.EEventLogException;
import eventlog.EventJournalFactory;
import eventlog.IEventJournal;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author hbakiewicz
 */
public class WeeEx extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;

    private IEventJournal log;
    public String version = "WooRepl - 0.1";
    public String current_path;
    SimpleDateFormat sdf = new SimpleDateFormat("ddMyyhhmm");
    public String edi_date = sdf.format(new Date());
    public String dbname, dbuser, dbpassword, dbport, dbconnectstring, log_lvl, przel, edi_pref, store_code, liv_location, liv_output;
    public String output_path;
    private String ck_key;
    private String cs_key;
    public dbManager dbm;
    private Future<String> task;
    private static String arg;

    public WeeEx(String _arg) {
        WeeEx.arg = _arg;
        initComponents();

        Path currentRelativePath = Paths.get("");
        current_path = currentRelativePath.toAbsolutePath().toString();

        Properties prop = new Properties();

        try {

            InputStream input = new FileInputStream("config.properties");

            prop.load(input);
            dbname = prop.getProperty("dbname", "pcmarket");
            dbconnectstring = prop.getProperty("dbconnectstring", "jdbc:sqlserver://localhost");
            dbuser = prop.getProperty("dbuser", "sa");
            dbpassword = prop.getProperty("dbpassword", "mayhem26");
            dbport = prop.getProperty("dbport", "1433");
            log_lvl = prop.getProperty("log_level", "INFO");
            ck_key = prop.getProperty("ck_key", "");
            cs_key = prop.getProperty("cs_key", "");
            store_code = prop.getProperty("store_id", "001351");
            liv_location = prop.getProperty("liv_location", "c:\\");
            liv_output = prop.getProperty("liv_output", "c:\\");
            jProgressBar1.setVisible(false);
            log = EventJournalFactory.createEventJournal("T", current_path + "\\wooex_log", 2000000, 64, Level.parse(log_lvl));
            dbm = new dbManager(dbconnectstring, dbuser, dbpassword, dbname);
            jmUpdeteBulk.setText("Aktualizacja stanów w pakietach po [" + prop.getProperty("packet", "") + "]");
            //dbm.
        } catch (EEventLogException | FileNotFoundException | NullPointerException ex) {
            System.out.println("brak pliku config.properties");
            System.exit(1);

        } catch (IOException ex) {
            System.out.println("brak pliku config.properties");
            System.exit(1);
        }
        log.logEvent(Level.INFO, "Current relative path is: " + current_path);
        log.logEvent(Level.INFO, "wersja programu  " + version);
        DefaultCaret caret = (DefaultCaret) jTextArea1.getCaret(); // ←
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        /*
        a - all product -bulk 
        o - all one by one 
        z - get current order 
         */

        if (arg.equals("a") || arg.equals("A")) {
            final ExecutorService service;
            service = Executors.newFixedThreadPool(1);
            productManager a = new productManager(ck_key, cs_key, log, Level.INFO, "config.properties", jTextArea1, dbm, jProgressBar1);
            a.setMode(4);
            service.submit(a);
        }
        if (arg.equals("o") || arg.equals("O")) {
            final ExecutorService service;
            service = Executors.newFixedThreadPool(1);
            productManager a = new productManager(ck_key, cs_key, log, Level.INFO, "config.properties", jTextArea1, dbm, jProgressBar1);
            a.setMode(5);
            service.submit(a);
        }

        if (arg.equals("z") || arg.equals("Z")) {
            final ExecutorService service;
            service = Executors.newFixedThreadPool(1);
            OrderProcessor order = new OrderProcessor(ck_key, cs_key, log, Level.INFO, "config.properties", jTextArea1, dbm, jProgressBar1);
            order.setMode(true);
            service.submit(order);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jmUpdeteBulk = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel1.setText("Monitor aktywności ");

        fileMenu.setMnemonic('f');
        fileMenu.setText("Funkcje");

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Powiąż towary według nazwy ");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        jMenuItem1.setText("Aktulaizacja stanów (kolejno)");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem1);

        jmUpdeteBulk.setText("Aktualizacja stanów (w pakietach)");
        jmUpdeteBulk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmUpdeteBulkActionPerformed(evt);
            }
        });
        fileMenu.add(jmUpdeteBulk);

        jMenuItem2.setText("Pobierz zamówienia");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem2);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");

        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText("Cut");
        cutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(cutMenuItem);

        copyMenuItem.setMnemonic('y');
        copyMenuItem.setText("Copy");
        editMenu.add(copyMenuItem);

        pasteMenuItem.setMnemonic('p');
        pasteMenuItem.setText("Paste");
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setMnemonic('d');
        deleteMenuItem.setText("Delete");
        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        contentsMenuItem.setMnemonic('c');
        contentsMenuItem.setText("Contents");
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 707, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        final ExecutorService service;

        service = Executors.newFixedThreadPool(1);

        productManager a = new productManager(ck_key, cs_key, log, Level.INFO, "config.properties", jTextArea1, dbm, jProgressBar1);
        a.setMode(0);
        service.submit(a);
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        final ExecutorService service;

        service = Executors.newFixedThreadPool(1);

        productManager a = new productManager(ck_key, cs_key, log, Level.INFO, "config.properties", jTextArea1, dbm, jProgressBar1);
        a.setMode(1);
        service.submit(a);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        final ExecutorService service;
        service = Executors.newFixedThreadPool(1);
        OrderProcessor order = new OrderProcessor(ck_key, cs_key, log, Level.INFO, "config.properties", jTextArea1, dbm, jProgressBar1);
        //a.setMode(2);
        service.submit(order);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void cutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMenuItemActionPerformed
        String g = "SEMILAC LAKIER 105 7ML";
        String h = "SEMILAC LAKIER HYBRYDOWY 160 I`M NOT SURE - 7ML";
        if (h.contains(g)) {
            System.out.println("jest");
        } else {
            System.out.println("brak");
        }

    }//GEN-LAST:event_cutMenuItemActionPerformed

    private void jmUpdeteBulkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmUpdeteBulkActionPerformed
        final ExecutorService service;
        service = Executors.newFixedThreadPool(1);
        productManager a = new productManager(ck_key, cs_key, log, Level.INFO, "config.properties", jTextArea1, dbm, jProgressBar1);
        a.setMode(3);
        service.submit(a);
    }//GEN-LAST:event_jmUpdeteBulkActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WeeEx.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        try {
            if (!args[0].isEmpty()) {
                java.awt.EventQueue.invokeLater(() -> {
                    new WeeEx(args[0]).setVisible(true);
                });

            } else {
                java.awt.EventQueue.invokeLater(() -> {
                    new WeeEx("0").setVisible(true);
                });
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            java.awt.EventQueue.invokeLater(() -> {
                new WeeEx("0").setVisible(true);
            });
        }
        //</editor-fold>

        /* Create and display the form */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JMenuItem jmUpdeteBulk;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    // End of variables declaration//GEN-END:variables

}