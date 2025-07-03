# Auction Bidding System (Java Swing + MySQL)
A simple desktop-based auction system built using Java Swing GUI and MySQL database. Users can view items up for auction, place bids, and see bidding history. The system limits each item to 3 bids and automatically marks the item as sold after the third bid, declaring the highest bidder as the winner.

# Features
View all auction items with status (available/sold)
Place bids with name and amount
Maximum of 3 bids per item
Winner automatically declared on 3rd bid
Stores bids and items in MySQL database
Real-time bid history shown in the UI

# Technologies Used
Language	Java 8+
UI	Java Swing (JFrame, JTextArea, JButton, etc.)
Database	MySQL
JDBC Driver	MySQL Connector/J

# Project Structure
AuctionSystem/
│
├── AuctionGUI.java        # Main UI class
├── AuctionSystem.java     # Business logic and database queries
├── DBConnection.java      # MySQL DB connection helper
├── auction_system.sql     # SQL script to create tables
└── README.md

# Database Setup
1. Open MySQL and create a new database:

CREATE DATABASE auction_system;

2. Run the following SQL script to create necessary tables:
USE auction_system;

CREATE TABLE auction_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL,
    item_description TEXT,
    current_bid DOUBLE DEFAULT 0.0,
    status VARCHAR(20) DEFAULT 'open',
    winner VARCHAR(255)
);

CREATE TABLE bids (
    bid_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    bid_amount DOUBLE,
    bidder_name VARCHAR(255),
    FOREIGN KEY (item_id) REFERENCES auction_items(item_id)
);

3. Insert sample data (optional):

INSERT INTO auction_items (item_name, item_description) VALUES
('Vintage Clock', 'A beautiful antique wall clock.'),
('Mountain Bike', 'A rugged bike suitable for off-road trails.'),
('Laptop', '15-inch high-performance laptop.');

# Configuration
Make sure to update your DBConnection.java file with your own DB credentials:

private static final String DB_URL = "jdbc:mysql://localhost:3306/auction_system";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "root";

# Ensure MySQL server is running.
# How to Run

# Compile all Java files:
javac AuctionGUI.java AuctionSystem.java DBConnection.java

# Run the main GUI:
java AuctionGUI

# Logic Summary
The combo box shows all auction items.
Selecting an item shows its bid history.
You can place a bid if:
The item is open (not sold)
It has received less than 3 bids
Your bid is higher than current bid
After the third bid, the item is marked "sold" and the highest bidder is set as the winner.
