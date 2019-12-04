package com.postalioassign;

import okhttp3.*;

import java.io.IOException;

public class Part1 {


    public static void main(String[] args) throws Exception {

        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/w/api.php?action=parse&section=0&prop=text&format=json&page=Cincinnati")
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            int counter = 0;
            int lastIndex = 0;

            String text = response.body().string();
            String target = "Cincinnati";

            while(lastIndex != -1){
                lastIndex = text.indexOf(target,lastIndex);

                if(lastIndex != -1){
                    counter++;
                    lastIndex += target.length();
                }
            }

            System.out.printf("%s occurred %d times\n", target, counter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
