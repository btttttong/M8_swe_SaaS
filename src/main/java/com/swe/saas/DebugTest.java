package com.swe.saas;

public class DebugTest {
    public static void main(String[] args) {
        try {
            java.net.URL url = new java.net.URL("http://127.0.0.1:9090/api/github/activity/test");
            java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println("✅ Response Code: " + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}