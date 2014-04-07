package com.elvisoliveira.yellowpages.ui;

import com.elvisoliveira.yellowpages.beans.contactbean;
import com.elvisoliveira.yellowpages.webservice.telelistas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import org.jsoup.nodes.Document;

public class mainWindow {

    private static JScrollPane contactsListing;
    private static JTable table;
    private static DefaultTableModel contactsTable;
    private static JTextField searchInput;
    private static JButton searchButton;
    private static List<String> strings;

    private static final JFrame window = new JFrame("YellowPages");
    private static final JPanel panel = new JPanel();

    // format the loading image with it's full path
    static final String ajaxLoader = String.format(
            // define the path of the image
            "%s/src/main/java/%s/ajax-loader.gif",
            // return the project production directory
            System.getProperty("user.dir").replace(".", "§").replace(".", "/").replace("§", "."),
            // return the package as a directory
            new mainWindow().getClass().getPackage().getName().replace(".", "/")
    );

    public static void setContacts(String name) throws IOException {

        // layout configuration        
        panel.setLayout(new MigLayout());

        // serach field
        searchInput = new JTextField();
        // position the serach field in MigLayout
        panel.add(searchInput, "growx, growy, split 2");

        // search button
        searchButton = new JButton();
        searchButton.setText("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // set the loading screen, while the SwingWorker class is working
                contactsListing.setViewportView(new JLabel("loading... ", new ImageIcon(ajaxLoader), JLabel.CENTER));
                // get the inputted name
                final String name = searchInput.getText();
                // set the SwingWorker class as a buffer streammer
                SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
                    @Override // required implementation
                    public Void doInBackground() throws IOException {
                        // this will be executed in background
                        changeContacts(name);
                        // return anything
                        return null;
                    }
                };
                // make the swingWorker work
                swingWorker.execute();
            }
        });
        // position the searchButton in MigLayout
        panel.add(searchButton, "wrap");

        // instantiate the widget that will list the contacts
        contactsListing = new JScrollPane();
        // in the beginning of the execution
        // the name is not set, give the user instructions to search
        contactsListing.setViewportView(new JLabel("Search a contact", JLabel.CENTER));
        // set the widget dimentions, required to define layout properties
        contactsListing.setPreferredSize(new Dimension(400, 500));
        // position the scroll panel in MigLayout
        panel.add(contactsListing);

        // window configuration
        window.add(panel);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
        window.setResizable(false);

    }

    @SuppressWarnings("empty-statement")
    public static void changeContacts(String name) throws IOException {
        // make the request and return the Document
        Document document = telelistas.generateDocument(name);
        // from the requested Document, verify how many contacts were returned
        Integer total = telelistas.totalContacts(document);
        // if the total of returned contacts are zero, feedback one messange
        if (total.equals(0)) {
            contactsListing.setViewportView(new JLabel("None contacts with this name", JLabel.CENTER));
        } // if the total of returned contacts are greater than zero, return them
        else {
            List<contactbean> contactsList = telelistas.telelistas(document);

            table = new JTable();
            
            contactsTable = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            contactsTable.addColumn("Nome");
            contactsTable.addColumn("Endereço");

            for (contactbean object : contactsList) {
                contactsTable.addRow(new Object[]{object.getName(), object.getAddress().trim()});
            }

            table.setModel(contactsTable);

            contactsListing.setViewportView(table);
        }

    }

}
