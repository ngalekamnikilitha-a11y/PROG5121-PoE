/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chatsystem;

/**
 *
 * @author ngale
 */

import java.util.Scanner;
import java.util.regex.Pattern;

public class ChatSystem {

   
    private static String savedUsername = "";
    private static String savedPassword = "";

    
    private static final int MAX_CAPACITY = 100; 
    private static int currentSize = 0; 

    private static String[] sentMessages = new String[MAX_CAPACITY];
    private static String[] disregardedMessages = new String[MAX_CAPACITY];
    private static String[] storedMessages = new String[MAX_CAPACITY]; 
    private static String[] messageHash = new String[MAX_CAPACITY];
    private static String[] messageID = new String[MAX_CAPACITY];
    private static String[] senders = new String[MAX_CAPACITY];
    private static String[] recipients = new String[MAX_CAPACITY];

    
    static class Message {
        private String messageID;
        private String recipientCell;
        private String messageBody;

        public Message(String messageID, String recipientCell, String messageBody) {
            this.messageID = messageID;
            this.recipientCell = recipientCell;
            this.messageBody = messageBody;
        }

        public boolean checkMessageID() {
            return this.messageID != null && this.messageID.length() <= 10;
        }

        public String checkRecipientCell() {
            if (this.recipientCell == null || this.recipientCell.isEmpty() || 
                !this.recipientCell.matches("[0-9]+") || this.recipientCell.length() > 10) {
                return "Incorrect number.";
            }
            return "Valid";
        }

        public String createMessageHash(int currentMessageNumber) {
            String prefix = (this.messageID.length() >= 2) ? this.messageID.substring(0, 2) : (this.messageID + "0");
            String[] words = this.messageBody.trim().split("\\s+");
            String trackingWords = (words.length > 0) ? (words[0] + words[words.length - 1]) : "";
            return (prefix + ":" + currentMessageNumber + ":" + trackingWords).toUpperCase();
        }

        public String askUserAction(Scanner scanner) {
            System.out.println("\nWhat would you like to do with this message?");
            System.out.print("Type A to Send: ");
            System.out.print("Type B to Store: ");
            System.out.print("Type C to Disregard: ");
            return scanner.nextLine().trim().toUpperCase();
        }
    }

  
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        System.out.println("*********************************");
        System.out.println("Welcome to QuickChat Portal.");
        System.out.println("*********************************");

      
        createAccount(input);
        loginPrompt(input);
        
        
        populateSampleData();

       
        int maxMessages = 0;
        while (true) {
            try {
                System.out.print("\nHow many new messages do you wish to enter? ");
                maxMessages = Integer.parseInt(input.nextLine().trim());
                if (maxMessages > 0) break;
                System.out.println("Please enter a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer number.");
            }
        }

        int sessionInputCounter = 0;
        boolean running = true;

        
        while (running) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1) Send / Process Messages");
            System.out.println("2) Show Recently Sent Messages (Array Trace)");
            System.out.println("3) Show Disregarded Messages Log");
            System.out.println("4) Stored Messages Operations Sub-Menu");
            System.out.println("5) Exit Application");
            System.out.print("Please select an option (1-5): ");

            String choice = input.nextLine().trim();

            switch (choice) {
                case "1":
                    if (sessionInputCounter >= maxMessages) {
                        System.out.println("\n[Limit Reached] You cannot enter more than " + maxMessages + " messages this session.");
                        break;
                    }
                    if (currentSize >= MAX_CAPACITY) {
                        System.out.println("\n[System Error] Storage Arrays Full!");
                        break;
                    }

                    composeAndProcessMessage(input, sessionInputCounter);
                    sessionInputCounter++;
                    break;

                case "2":
                    displaySentMessagesLog();
                    break;

                case "3":
                    displayDisregardedMessagesLog();
                    break;

                case "4":
                    handleStoredMessagesMenu(input);
                    break;

                case "5":
                    System.out.println("\nExiting QuickChat Portal. Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("\nInvalid option. Please enter a value between 1 and 5.");
            }
        }
        input.close();
    }

  
    private static void createAccount(Scanner input) {
        while (true) {
            System.out.println("\n[Registration] Enter username (1-4 letters followed by '_' and at least 1 more character): ");
            String username = input.nextLine().trim();
            if (isUsernameValid(username)) {
                savedUsername = username;
                System.out.println("Username successfully captured.");
                break;
            }
            System.out.println("Username format mismatch. Ensure pattern: 1-4 letters + '_' + at least one character.");
        }

        while (true) {
            System.out.println("Enter password (at least 8 chars, uppercase, digit, special character): ");
            String password = input.nextLine();
            if (isPasswordValid(password)) {
                savedPassword = password;
                System.out.println("Password successfully captured.");
                break;
            }
            System.out.println("Password pattern format mismatch. Verify constraints and try again.");
        }

        while (true) {
            System.out.println("Enter South African phone number (10 digits starting with 0): ");
            String phoneNumber = input.nextLine().trim();
            if (isPhoneNumberValid(phoneNumber)) {
                System.out.println("Phone number verified safe.");
                break;
            }
            System.out.println("Invalid SA phone number structure. Must start with 0 followed by 6, 7, or 8 up to 10 digits.");
        }
        System.out.println("\nAccount successfully initialized for User: " + savedUsername);
    }

    private static void loginPrompt(Scanner input) {
        System.out.println("\n---------------------------------");
        System.out.println("Security Authentication System Check");
        System.out.println("---------------------------------");
        while (true) {
            System.out.print("Enter Username: ");
            String userAttempt = input.nextLine().trim();
            System.out.print("Enter Password: ");
            String passAttempt = input.nextLine();

            if (userAttempt.equals(savedUsername) && passAttempt.equals(savedPassword)) {
                System.out.println("\nAccess Granted. Access tokens issued successfully.");
                break;
            }
            System.out.println("Credentials mismatch. Authorization denied. Try again.");
        }
    }

    
    private static void composeAndProcessMessage(Scanner input, int sessionCounter) {
        String id;
        while (true) {
            System.out.print("Enter Message ID (Max 10 characters): ");
            id = input.nextLine().trim();
            Message checkObj = new Message(id, "", "");
            if (checkObj.checkMessageID()) break;
            System.out.println("Invalid ID. Ensure characters length stays below 10.");
        }

        String cell = "";
        while (true) {
            System.out.print("Enter Recipient Cell: ");
            cell = input.nextLine().trim();
            Message dynamicCheck = new Message(id, cell, "");
            if (dynamicCheck.checkRecipientCell().equals("Incorrect number.")) {
                System.out.println("Incorrect number format. (Numbers only, max 10 digits starting validly).");
            } else {
                break;
            }
        }

        System.out.print("Enter Message Text Body: ");
        String body = input.nextLine();

        
        Message activeMsg = new Message(id, cell, body);
        String action = activeMsg.askUserAction(input);

        String calculatedHash = activeMsg.createMessageHash(currentSize);

     
        messageID[currentSize] = id;
        messageHash[currentSize] = calculatedHash;
        senders[currentSize] = savedUsername; 
        recipients[currentSize] = cell;
        storedMessages[currentSize] = body;

        if (action.equals("A")) {
            sentMessages[currentSize] = "SENT: " + body;
            disregardedMessages[currentSize] = "N/A";
            System.out.println("Action Processed: Message successfully sent out! Hash code: " + calculatedHash);
            currentSize++;
        } else if (action.equals("B")) {
            sentMessages[currentSize] = "N/A";
            disregardedMessages[currentSize] = "N/A";
            System.out.println("Action Processed: Saved into Stored Storage space! Hash code: " + calculatedHash);
            currentSize++;
        } else {
            
            sentMessages[currentSize] = "N/A";
            disregardedMessages[currentSize] = "DISREGARDED: " + body;
            System.out.println("Action Processed: Item flagged directly to Disregarded registers.");
            currentSize++;
        }
    }

    
    private static void displaySentMessagesLog() {
        System.out.println("\n--- Recently Sent Messages Log ---");
        boolean logged = false;
        for(int i=0; i<currentSize; i++) {
            if(!sentMessages[i].equals("N/A")) {
                System.out.println("[" + messageID[i] + "] to Recipient: " + recipients[i] + " -> " + sentMessages[i]);
                logged = true;
            }
        }
        if(!logged) System.out.println("No outgoing records recorded during active lifecycle execution logs.");
    }

    private static void displayDisregardedMessagesLog() {
        System.out.println("\n--- Disregarded Messages Registry Logs ---");
        boolean logged = false;
        for(int i=0; i<currentSize; i++) {
            if(!disregardedMessages[i].equals("N/A")) {
                System.out.println("[" + messageID[i] + "] Hash Ref: " + messageHash[i] + " -> " + disregardedMessages[i]);
                logged = true;
            }
        }
        if(!logged) System.out.println("No records flagged inside internal trash buffers.");
    }

    
    private static void handleStoredMessagesMenu(Scanner input) {
        boolean inSubMenu = true;
        while (inSubMenu) {
            System.out.println("\n--- STORED MESSAGES SUB-MENU ---");
            System.out.println("a. Display the sender and recipient of all stored messages.");
            System.out.println("b. Display the longest stored message.");
            System.out.println("c. Search for a message ID and display the corresponding recipient and message.");
            System.out.println("d. Search for all the messages stored for a particular recipient.");
            System.out.println("e. Delete a message using the message hash.");
            System.out.println("f. Display a report that lists the full details of all the stored messages.");
            System.out.println("g. Return to Main Menu");
            System.out.print("Select a sub-option (a-g): ");

            String option = input.nextLine().toLowerCase().trim();

            switch (option) {
                case "a": displaySendersAndRecipients(); break;
                case "b": displayLongestMessage(); break;
                case "c": searchByMessageID(input); break;
                case "d": searchByRecipient(input); break;
                case "e": deleteMessageByHash(input); break;
                case "f": displayFullReport(); break;
                case "g": inSubMenu = false; break;
                default: System.out.println("Invalid option configuration choice. Select keys a through g.");
            }
        }
    }

    
    private static void displaySendersAndRecipients() {
        System.out.println("\n--- Senders and Recipients Tracking Matrix ---");
        if (currentSize == 0) { System.out.println("No current tracked elements located."); return; }
        for (int i = 0; i < currentSize; i++) {
            System.out.println("Msg [" + messageID[i] + "] -> Sender Account: " + senders[i] + " | Destination Cell: " + recipients[i]);
        }
    }

    private static void displayLongestMessage() {
        System.out.println("\n--- Analysis: Longest Stored Record Message ---");
        if (currentSize == 0) { System.out.println("Nothing stored available to measure structural lengths."); return; }
        int maxIndex = 0;
        for (int i = 1; i < currentSize; i++) {
            if (storedMessages[i].length() > storedMessages[maxIndex].length()) maxIndex = i;
        }
        System.out.println("Longest Entry Found at ID [" + messageID[maxIndex] + "] (" + storedMessages[maxIndex].length() + " Characters Long):");
        System.out.println("\"" + storedMessages[maxIndex] + "\"");
    }

    private static void searchByMessageID(Scanner input) {
        System.out.print("\nEnter target ID value to look up: ");
        String searchTarget = input.nextLine().trim();
        boolean found = false;
        for (int i = 0; i < currentSize; i++) {
            if (messageID[i].equalsIgnoreCase(searchTarget)) {
                System.out.println("\n[Record Found]");
                System.out.println("Recipient: " + recipients[i]);
                System.out.println("Body Message context: " + storedMessages[i]);
                found = true;
                break;
            }
        }
        if (!found) System.out.println("Search mismatch. ID value not registered.");
    }

    private static void searchByRecipient(Scanner input) {
        System.out.print("\nEnter recipient target cell address: ");
        String targetCell = input.nextLine().trim();
        boolean found = false;
        for (int i = 0; i < currentSize; i++) {
            if (recipients[i].equals(targetCell)) {
                System.out.println("-> ID (" + messageID[i] + ") [Hash: " + messageHash[i] + "] Content: " + storedMessages[i]);
                found = true;
            }
        }
        if (!found) System.out.println("No matching cell records identified in system arrays.");
    }

    private static void deleteMessageByHash(Scanner input) {
        System.out.print("\nEnter unique Message Hash targeted for absolute wiping: ");
        String targetHash = input.nextLine().trim();
        int matchedIdx = -1;
        for (int i = 0; i < currentSize; i++) {
            if (messageHash[i].equalsIgnoreCase(targetHash)) {
                matchedIdx = i;
                break;
            }
        }
        if (matchedIdx == -1) {
            System.out.println("Target array deletion failure: Hash signature not identified.");
            return;
        }

     
        for (int i = matchedIdx; i < currentSize - 1; i++) {
            sentMessages[i] = sentMessages[i + 1];
            disregardedMessages[i] = disregardedMessages[i + 1];
            storedMessages[i] = storedMessages[i + 1];
            messageHash[i] = messageHash[i + 1];
            messageID[i] = messageID[i + 1];
            senders[i] = senders[i + 1];
            recipients[i] = recipients[i + 1];
        }
        
        sentMessages[currentSize - 1] = null;
        disregardedMessages[currentSize - 1] = null;
        storedMessages[currentSize - 1] = null;
        messageHash[currentSize - 1] = null;
        messageID[currentSize - 1] = null;
        senders[currentSize - 1] = null;
        recipients[currentSize - 1] = null;

        currentSize--;
        System.out.println("System Operation Cleared. Array records safely shifted down.");
    }

    private static void displayFullReport() {
        System.out.println("\n==========================================================================");
        System.out.println("                     COMPLETE SYSTEM PARALLEL ARRAY REPORT                ");
        System.out.println("==========================================================================");
        if (currentSize == 0) System.out.println("                 *** Inventory Buffer Empty *** ");
        for (int i = 0; i < currentSize; i++) {
            System.out.printf("Record Slot #%d -> [ID: %s] | [Hash: %s]\n", (i+1), messageID[i], messageHash[i]);
            System.out.printf("   From User: %s  | To Recipient Cell: %s\n", senders[i], recipients[i]);
            System.out.printf("   Message Payload Context String: %s\n", storedMessages[i]);
            System.out.println("--------------------------------------------------------------------------");
        }
    }
    
    public static boolean isUsernameValid(String username) {
        return Pattern.matches("^[a-zA-Z]{1,4}_[^_]+$", username);
    }

    public static boolean isPasswordValid(String password) {
        return Pattern.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", password);
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        return Pattern.matches("^0(6|7|8)\\d{8}$", phoneNumber);
    }

    
    private static void populateSampleData() {
        String[] sampleIDs = {"M101", "M102"};
        String[] sampleHashes = {"M1:0:SAMPLE", "M1:1:LOGS"};
        String[] sampleRecipients = {"0821112222", "0734445555"};
        String[] sampleBodies = {"Initial sample array text package setup.", "Short body data."};

        for (int i = 0; i < sampleIDs.length; i++) {
            messageID[currentSize] = sampleIDs[i];
            messageHash[currentSize] = sampleHashes[i];
            senders[currentSize] = "SystemAdmin";
            recipients[currentSize] = sampleRecipients[i];
            storedMessages[currentSize] = sampleBodies[i];
            sentMessages[currentSize] = "N/A";
            disregardedMessages[currentSize] = "N/A";
            currentSize++;
        }
    }
}
