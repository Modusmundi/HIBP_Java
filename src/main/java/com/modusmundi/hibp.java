package main.java.com.modusmundi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class hibp {
    public static void main(String[] args) throws IOException {

        // Ingest a string provided by the user.
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String readString;
        while (true) {
            readString = reader.readLine();
            if (readString.isEmpty()){
                System.out.println("Enter a password to check against HIBP.");
            }
            else break;
        }

        // Hash the string.
        String hashedString = encrypt(readString);
        readString = "";

        // Put the first five and last five characters of the hash into two
        // separate variables for ease of reference later.
        String front = frontRange(hashedString);
        String back = backRange(hashedString);

        //URL Connection Properties
        String pwnURL = "https://api.pwnedpasswords.com/range/" + front;
        String pwnAgent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
        String pwnAccept = "application/vnd.haveibeenpwned.v2+json";

        List<String> resultList = new ArrayList<String>();
        boolean resultResponse = false;

        try {

            URL url = new URL(pwnURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", pwnAgent);
            conn.setRequestProperty("Accept", pwnAccept);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                resultList.add(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        for(String listItem : resultList){
            if(listItem.toLowerCase().contains(back)){
                resultResponse = true;
            }
        }

        if (resultResponse == true){
            System.out.println("You have been pwned!");
            System.out.println("Please, change your password.");
        }
        else{
            System.out.println("The password you entered has not been pwned!");
        }
    }

    public static String encrypt(String input) {

        try {
            // getInstance() method is called with algorithm SHA-1
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = digest.digest(input.getBytes());

            // Convert byte md to number
            BigInteger number = new BigInteger(1, messageDigest);

            // Decimal number to hex
            String hashtext = number.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            hashtext.toString();

            // return the HashText
            return hashtext;
        }

        // Catch for bad algo
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String frontRange(String input){
        // Returns front 5 characters of SHA-1 hash
        return input.substring(0,5);
    }

    public static String backRange(String input){
        // Returns back 5 characters of SHA-1 hash
        return input.substring(input.length() - 35);
    }
}


