import java.sql.*;
import java.util.*;

public class AuctionSystem {

    // 3 user inputs method
    public static boolean placeBid(int itemId, double bidAmount, String bidderName) throws SQLException {
        //Auction item open or sold
        String checkStatusQuery = "SELECT status, current_bid FROM auction_items WHERE item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkStatusQuery)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                if ("sold".equals(status)) {
                    return false; // Item sold
                }
            }
        }

        // counting number of user bids
        String countBidsQuery = "SELECT COUNT(*) AS bid_count FROM bids WHERE item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(countBidsQuery)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int bidCount = rs.getInt("bid_count");
            if (bidCount >= 3) {
                return false;
            }

            // Inserting bid values to bid table
            String insertBidQuery = "INSERT INTO bids (item_id, bid_amount, bidder_name) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertBidQuery)) {
                insertStmt.setInt(1, itemId);
                insertStmt.setDouble(2, bidAmount);
                insertStmt.setString(3, bidderName);
                insertStmt.executeUpdate();
            }

            // Update current bid 
            String updateBidQuery = "UPDATE auction_items SET current_bid = (SELECT MAX(bid_amount) FROM bids WHERE item_id = ?) WHERE item_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateBidQuery)) {
                updateStmt.setInt(1, itemId);
                updateStmt.setInt(2, itemId);
                updateStmt.executeUpdate();
            }

            // declaring the winner
            if (bidCount == 2) {
                String selectWinnerQuery = "SELECT bidder_name FROM bids WHERE item_id = ? AND bid_amount = (SELECT MAX(bid_amount) FROM bids WHERE item_id = ?)";
                try (PreparedStatement selectStmt = conn.prepareStatement(selectWinnerQuery)) {
                    selectStmt.setInt(1, itemId);
                    selectStmt.setInt(2, itemId);
                    ResultSet rsWinner = selectStmt.executeQuery();
                    if (rsWinner.next()) {
                        String winner = rsWinner.getString("bidder_name");
                        String updateWinnerQuery = "UPDATE auction_items SET winner = ?, status = 'sold' WHERE item_id = ?";
                        try (PreparedStatement updateWinnerStmt = conn.prepareStatement(updateWinnerQuery)) {
                            updateWinnerStmt.setString(1, winner);
                            updateWinnerStmt.setInt(2, itemId);
                            updateWinnerStmt.executeUpdate();
                        }
                    }
                }
            }

            return true; // bid successful
        }
    }

   
    public static List<String> getAllAuctionItems() throws SQLException {
        List<String> auctionItems = new ArrayList<>();
        String query = "SELECT item_id, item_name, item_description, current_bid, status, winner FROM auction_items";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                String itemName = rs.getString("item_name");
                double currentBid = rs.getDouble("current_bid");
                String status = rs.getString("status");
                String winner = rs.getString("winner");

                String itemDetails = itemId + " - " + itemName + " - $" + currentBid + " - Status: " + status;
                if ("sold".equals(status)) {
                    itemDetails += " - Winner: " + (winner != null ? winner : "N/A");
                }
                auctionItems.add(itemDetails);
            }
        }
        return auctionItems;
    }

   
    public static List<String> getAllBidsForItem(int itemId) throws SQLException {
        List<String> bids = new ArrayList<>();
        String query = "SELECT bid_amount, bidder_name FROM bids WHERE item_id = ? ORDER BY bid_amount DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                double bidAmount = rs.getDouble("bid_amount");
                String bidderName = rs.getString("bidder_name");
                bids.add(bidderName + " bid $" + bidAmount);
            }
        }
        return bids;
    }
}
