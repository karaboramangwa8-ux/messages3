package com.mycompany.messages3;
 
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Student
 */


public class Messages3 { 

    static final String SENDER = "Developer";
    static final String FILE = "messages.json";

    // the five arrays from the assignment
    static ArrayList<String> sentMessages = new ArrayList<>();
    static ArrayList<String> disregardedMessages = new ArrayList<>();
    static ArrayList<String> storedMessages = new ArrayList<>();
    static ArrayList<String> messageHash = new ArrayList<>();
    static ArrayList<String> messageID = new ArrayList<>();

    // these line up with messageID and messageHash (sent + stored, same order)
    static ArrayList<String> allMessages = new ArrayList<>();
    static ArrayList<String> allRecipients = new ArrayList<>();
    static ArrayList<String> allFlags = new ArrayList<>();

    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        loadStoredMessages();
        while (true) {
            System.out.println("\n1) Send message");
            System.out.println("2) Show sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Choose: ");
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1": addMessage(); break;
                case "2": showSent(); break;
                case "3": System.exit(0); break;
                case "4": storedMenu(); break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    // ---------- adding messages ----------
    static void addMessage() {
        System.out.print("Recipient: ");
        String recipient = in.nextLine().trim();
        System.out.print("Message: ");
        String msg = in.nextLine().trim();
        System.out.print("Send, Store or Disregard? (S/T/D): ");
        String f = in.nextLine().trim().toUpperCase();
        String flag = f.equals("S") ? "Sent" : f.equals("T") ? "Stored" : "Disregard";
        String id = String.valueOf(System.nanoTime()).substring(0, 10);
        record(recipient, msg, flag, id, null);
        if (flag.equals("Stored")) saveStoredMessages();
    }

    static void record(String recipient, String msg, String flag, String id, String hash) {
        if (flag.equals("Disregard")) {
            disregardedMessages.add(msg);
            return;
        }
        if (hash == null) hash = makeHash(id, allMessages.size() + 1, msg);
        allMessages.add(msg);
        allRecipients.add(recipient);
        allFlags.add(flag);
        messageID.add(id);
        messageHash.add(hash);
        if (flag.equals("Sent")) sentMessages.add(msg);
        else storedMessages.add(msg);
    }

    // swap in your Part 1 hash logic if it's different
    static String makeHash(String id, int num, String msg) {
        String[] words = msg.split("\\s+");
        String first = words[0].replaceAll("[^A-Za-z0-9]", "");
        String last = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        return (id.substring(0, 2) + ":" + num + ":" + first + last).toUpperCase();
    }

    static void showSent() {
        for (int i = 0; i < allMessages.size(); i++) {
            if (allFlags.get(i).equals("Sent")) {
                System.out.println(allRecipients.get(i) + ": " + allMessages.get(i));
            }
        }
    }

    // ---------- Stored Messages menu ----------
    static void storedMenu() {
        System.out.println("\na) Sender and recipient of all stored messages");
        System.out.println("b) Longest stored message");
        System.out.println("c) Search by message ID");
        System.out.println("d) Search by recipient");
        System.out.println("e) Delete by message hash");
        System.out.println("f) Report");
        System.out.print("Choose: ");
        String opt = in.nextLine().trim().toLowerCase();
        switch (opt) {
            case "a": displaySenderAndRecipient(); break;
            case "b": System.out.println(longestStored()); break;
            case "c":
                System.out.print("Message ID: ");
                String[] r = searchByID(in.nextLine().trim());
                System.out.println(r == null ? "Not found." : r[0] + ", " + r[1]);
                break;
            case "d":
                System.out.print("Recipient: ");
                for (String m : searchByRecipient(in.nextLine().trim())) System.out.println(m);
                break;
            case "e":
                System.out.print("Message hash: ");
                System.out.println(deleteByHash(in.nextLine().trim()));
                break;
            case "f": report(); break;
            default: System.out.println("Invalid option.");
        }
    }

    // a
    static void displaySenderAndRecipient() {
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages.");
            return;
        }
        for (int i = 0; i < allMessages.size(); i++) {
            if (allFlags.get(i).equals("Stored")) {
                System.out.println("Sender: " + SENDER + " | Recipient: " + allRecipients.get(i));
            }
        }
    }

    // b (looks at sent + stored so the test data check works too)
    static String longestStored() {
        String longest = "";
        for (int i = 0; i < allMessages.size(); i++) {
            if (allFlags.get(i).equals("Stored") && allMessages.get(i).length() > longest.length()) {
                longest = allMessages.get(i);
            }
        }
        return longest;
    }

    static String longestMessage() {
        String longest = "";
        for (String m : allMessages) {
            if (m.length() > longest.length()) longest = m;
        }
        return longest;
    }

    // c: returns {recipient, message}
    static String[] searchByID(String id) {
        int i = messageID.indexOf(id);
        if (i == -1) return null;
        return new String[]{allRecipients.get(i), allMessages.get(i)};
    }

    // d
    static ArrayList<String> searchByRecipient(String recipient) {
        ArrayList<String> found = new ArrayList<>();
        for (int i = 0; i < allMessages.size(); i++) {
            if (allFlags.get(i).equals("Stored") && allRecipients.get(i).equals(recipient)) {
                found.add(allMessages.get(i));
            }
        }
        return found;
    }

    // e
    static String deleteByHash(String hash) {
        int i = messageHash.indexOf(hash);
        if (i == -1) return "Hash not found.";
        String msg = allMessages.get(i);
        String flag = allFlags.get(i);
        if (flag.equals("Sent")) sentMessages.remove(msg);
        else storedMessages.remove(msg);
        allMessages.remove(i);
        allRecipients.remove(i);
        allFlags.remove(i);
        messageID.remove(i);
        messageHash.remove(i);
        if (flag.equals("Stored")) saveStoredMessages();
        return "Message: \"" + msg + "\" successfully deleted.";
    }

    // f
    static void report() {
        for (int i = 0; i < allMessages.size(); i++) {
            if (allFlags.get(i).equals("Stored")) {
                System.out.println("Message Hash: " + messageHash.get(i));
                System.out.println("Message ID: " + messageID.get(i));
                System.out.println("Sender: " + SENDER);
                System.out.println("Recipient: " + allRecipients.get(i));
                System.out.println("Message: " + allMessages.get(i));
                System.out.println("-----");
            }
        }
    }

    // ---------- JSON (org.json) ----------
    static void saveStoredMessages() {
        JSONArray arr = new JSONArray();
        for (int i = 0; i < allMessages.size(); i++) {
            if (allFlags.get(i).equals("Stored")) {
                JSONObject o = new JSONObject();
                o.put("id", messageID.get(i));
                o.put("hash", messageHash.get(i));
                o.put("recipient", allRecipients.get(i));
                o.put("message", allMessages.get(i));
                o.put("flag", "Stored");
                arr.put(o);
            }
        }
        try (FileWriter fw = new FileWriter(FILE)) {
            fw.write(arr.toString(2));
        } catch (IOException e) {
            System.out.println("Could not save file.");
        }
    }

    static void loadStoredMessages() {
        try {
            String text = new String(Files.readAllBytes(Paths.get(FILE)));
            JSONArray arr = new JSONArray(text);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                record(o.getString("recipient"), o.getString("message"), "Stored",
                        o.getString("id"), o.getString("hash"));
            }
        } catch (IOException e) {
            // no file yet
        }
    }
}

// ---------- unit tests (JUnit 5) ----------

class MessagesAppTest {

    static final String M1 = "Did you get the cake?";
    static final String M2 = "Where are you? You are late! I have asked you to be on time.";
    static final String M4 = "It is dinner time!";
    static final String M5 = "Ok, I am leaving without you.";

    @BeforeEach
    void setUp() {
        Messages3.sentMessages.clear();
        Messages3.disregardedMessages.clear();
        Messages3.storedMessages.clear();
        Messages3.messageHash.clear();
        Messages3.messageID.clear();
        Messages3.allMessages.clear();
        Messages3.allRecipients.clear();
        Messages3.allFlags.clear();

        Messages3.record("+27834557896", M1, "Sent", "1000000001", null);
        Messages3.record("+27838884567", M2, "Stored", "1000000002", null);
        Messages3.record("+27834484567", "Yohoooo, I am at your gate.", "Disregard", "1000000003", null);
        Messages3.record("0838884567", M4, "Sent", "1000000004", null);
        Messages3.record("+27838884567", M5, "Stored", "1000000005", null);
    }

    @Test
    void sentMessagesArrayCorrectlyPopulated() {
        assertEquals(Arrays.asList(M1, M4), Messages3.sentMessages);
    }

    @Test
    void displayLongestMessage() {
        assertEquals(M2, Messages3.longestMessage());
    }

    @Test
    void searchForMessageID() {
        String[] r = Messages3.searchByID("1000000004");
        assertEquals("0838884567", r[0]);
        assertEquals(M4, r[1]);
    }

    @Test
    void searchAllMessagesForRecipient() {
        assertEquals(Arrays.asList(M2, M5), Messages3.searchByRecipient("+27838884567"));
    }

    
    void deleteMessageUsingHash() {
        String hash = Messages3.messageHash.get(1); // message 2
        assertEquals("Message: \"" + M2 + "\" successfully deleted.", Messages3.deleteByHash(hash));
    }
}