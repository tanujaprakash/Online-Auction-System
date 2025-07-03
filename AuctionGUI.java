import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;

public class AuctionGUI {
    private JFrame frame;
    private JTextArea textArea;
    private JTextField bidAmountField;
    private JTextField bidderNameField;
    private JButton btnPlaceBid;
    private JComboBox<String> itemComboBox;
    private JTextArea bidHistoryArea;
    private int selectedItemId = -1;

    // Main method 
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                AuctionGUI window = new AuctionGUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Constructor to initialize 
    public AuctionGUI() {
        initialize();
    }

    // Method to initialize the GUI components
    private void initialize() {
        frame = new JFrame("Auction System");
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        // display auction items and current bids
        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);

        // buttons
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        //text field to enter amount 
        JLabel lblBidAmount = new JLabel("Bid Amount ($):");
        panel.add(lblBidAmount);
        bidAmountField = new JTextField();
        panel.add(bidAmountField);

        // text field to enter bidder name
        JLabel lblBidderName = new JLabel("Your Name:");
        panel.add(lblBidderName);
        bidderNameField = new JTextField();
        panel.add(bidderNameField);

       
        btnPlaceBid = new JButton("Place Bid");
        panel.add(btnPlaceBid);

        // ComboBox for selecting an auction item
        JLabel lblItemSelect = new JLabel("Select Item:");
        panel.add(lblItemSelect);

        itemComboBox = new JComboBox<>();
        panel.add(itemComboBox);

        // Panel for displaying bid history in a scrollable text area
        bidHistoryArea = new JTextArea(6, 30);
        bidHistoryArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(bidHistoryArea), BorderLayout.EAST);

        // Load auction items into combo box and text area
        loadAuctionItems();

        // Action to place a bid
        btnPlaceBid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bidderName = bidderNameField.getText();
                String bidAmountStr = bidAmountField.getText();

                if (selectedItemId != -1) {
                    try {
                        double bidAmount = Double.parseDouble(bidAmountStr);
                        boolean success = AuctionSystem.placeBid(selectedItemId, bidAmount, bidderName);
                        if (success) {
                            JOptionPane.showMessageDialog(frame, "Your bid has been placed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadAuctionItems(); // Reloading auction items after placing a bid
                        } else {
                            JOptionPane.showMessageDialog(frame, "Bid must be higher than the current bid or item already has 3 bids.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Error placing bid: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(frame, "Invalid bid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an auction item to place a bid.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //selecting item from combo box
        itemComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) itemComboBox.getSelectedItem();
                if (selectedItem != null && !selectedItem.isEmpty()) {
                    // separate column for selected item to get the ID and name
                    String[] parts = selectedItem.split(" - ");
                    if (parts.length > 0) {
                        selectedItemId = Integer.parseInt(parts[0]);
                        displayBidHistory(selectedItemId);
                    }
                }
            }
        });
    }

    // Load auction items from the database into the combo box
    private void loadAuctionItems() {
        try {
            List<String> auctionItems = AuctionSystem.getAllAuctionItems();
            StringBuilder displayText = new StringBuilder();

            itemComboBox.removeAllItems();
            for (String itemDetails : auctionItems) {
                itemComboBox.addItem(itemDetails);
                displayText.append(itemDetails).append("\n-----------------------------\n");
            }

            textArea.setText(displayText.toString());

            if (!auctionItems.isEmpty()) {
                itemComboBox.setSelectedIndex(0); // Selecting first item as default
            }
        } catch (SQLException e) {
            textArea.setText("Error loading auction items: " + e.getMessage());
        }
    }

   
    private void displayBidHistory(int itemId) {
        try {
            List<String> bids = AuctionSystem.getAllBidsForItem(itemId);
            StringBuilder bidHistory = new StringBuilder();

            if (bids.isEmpty()) {
                bidHistory.append("No bids placed yet.");
            } else {
                for (String bid : bids) {
                    bidHistory.append(bid).append("\n");
                }
            }

            bidHistoryArea.setText(bidHistory.toString());
        } catch (SQLException e) {
            bidHistoryArea.setText("Error loading bid history: " + e.getMessage());
        }
    }
}
