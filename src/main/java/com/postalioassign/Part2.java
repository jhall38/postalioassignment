package com.postalioassign;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

import okhttp3.*;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.validator.routines.InetAddressValidator;



public class Part2 {
    public static void main(String[] args) throws Exception {

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://www.sec.gov/dera/data/Public-EDGAR-log-file-data/2017/Qtr2/log20170630.zip").build();
            Response response = client.newCall(request).execute();
            inputStream = response.body().byteStream();
            outputStream = new FileOutputStream(new File("data.zip"));
            int totalCount = inputStream.available();
            byte[] buffer = new byte[2 * 1024];
            int len;
            int readLen = 0;
            while ((len = inputStream.read(buffer)) != -1 ) {
                outputStream.write(buffer, 0, len);
                readLen += len;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String source = "data.zip";
        String destination = "data";
        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        BufferedReader csvReader = new BufferedReader(new FileReader("data/log20170630.csv"));
        HashSet<String> ips = new HashSet<String>();
        HashMap<String, Integer> codes = new HashMap<String, Integer>();
        double totalSize = 0.0;
        String row = csvReader.readLine(); //skip first line with headers
        InetAddressValidator validator = InetAddressValidator.getInstance();
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            ips.add(data[0]);
            if (codes.containsKey(data[7])) {
                codes.put(data[7], codes.get(data[7])+1);
            } else {
                try {
                    Double.parseDouble(data[7]);
                    codes.put(data[7], 1);
                } catch(Exception e) {
                    //continue
                }
            }
            try {
                totalSize += Double.parseDouble(data[8]);
            } catch(Exception e) {
                //continue
            }

        }
        csvReader.close();

        System.out.printf("Total Ips: %d\nCodes: %s\nTotal Size (bytes): %f\n", ips.size(), codes, totalSize);
    }
}
