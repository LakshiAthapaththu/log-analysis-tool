package org.example;

import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        //readTextLogFile();
        //readCSVLogFile();
        seperate();
    }

    public static void seperate () throws IOException {
        Scanner sc1 = new Scanner(new File("/Users/lakshikaathapaththu/Desktop/correlation-logs-issue/program/output/request_log.csv"));
        Scanner sc2 = new Scanner(new File("/Users/lakshikaathapaththu/Desktop/correlation-logs-issue/program/output/response_log.csv"));

        List<String> array_responses = new ArrayList<>();
        List<String> array_requests = new ArrayList<>();
        while (sc1.hasNext())  //returns a boolean value
        {
            array_requests.add(sc1.next().trim());
        }

        while (sc2.hasNext())  //returns a boolean value
        {
            array_responses.add(sc2.next().trim());
        }

        List<String> request_response = new ArrayList<>();
        List<String> requestsOnly = new ArrayList<>();
        for (int i = 0; i < array_requests.size(); i++) {
            if(array_responses.contains(array_requests.get(i))){
                request_response.add(array_requests.get(i));
            } else {
                requestsOnly.add(array_requests.get(i));
            }
        }

        List<String> response_request = new ArrayList<>();
        List<String> responseOnly = new ArrayList<>();
        for (int i = 0; i < array_responses.size(); i++) {
            if(array_requests.contains(array_responses.get(i))){
                response_request.add(array_responses.get(i));
            } else {
                responseOnly.add(array_responses.get(i));
            }
        }

        System.out.println("request-response-pairs: "+request_response.size());
        System.out.println("request-response-pairs: "+response_request.size());

        System.out.println("requests only: "+ requestsOnly.size());
        System.out.println("responses only: " + responseOnly.size());

        System.out.println("total requests: "+array_requests.size());
        System.out.println("total responses: "+array_responses.size());
        sc1.close();  //closes the scanner
        sc2.close();

        CSVWriter writer1 = new CSVWriter(new FileWriter("/Users/lakshikaathapaththu/Desktop/correlation-logs-issue/program/output/request_response.csv"));
        request_response.forEach((value) -> {
            String line[] = {value};
            writer1.writeNext(line);
        });

        CSVWriter writer2 = new CSVWriter(new FileWriter("/Users/lakshikaathapaththu/Desktop/correlation-logs-issue/program/output/request_only.csv"));
        requestsOnly.forEach((value) -> {
            String line[] = {value};
            writer2.writeNext(line);
        });

        CSVWriter writer3 = new CSVWriter(new FileWriter("/Users/lakshikaathapaththu/Desktop/correlation-logs-issue/program/output/response_only.csv"));
        responseOnly.forEach((value) -> {
            String line[] = {value};
            writer3.writeNext(line);
        });

        writer1.flush();
        writer2.flush();
        writer3.flush();
    }

    public static void readTextLogFile () throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader("/Users/lakshikaathapaththu/Desktop/correlation-logs-issue/program/input/logs_06_02_01.txt"));
        String line = reader.readLine();

        int totalWebsubLogs = 0;
        int okResponses = 0;
        int error502 = 0;
        int error503 = 0;
        int totalReq = 0;
        int totalRes = 0;
        int timeouts = 0;

        List<String> req = new ArrayList<>();
        List<String> res = new ArrayList<>();

        List<String> req_dups = new ArrayList<>();
        List<String> res_dups = new ArrayList<>();

        while (line != null) {

            String trimLine = line.trim();

            if(trimLine.contains("LOGINS|/hub|completed|200|OK")) {
                okResponses++;
            }

            if(trimLine.contains("completed|503|Service Temporarily Unavailable")) {
                error503++;
            }

            if(trimLine.contains("|completed|502|Bad Gateway")) {
                error502++;
            }

            if(trimLine.contains("LOGINS|/hub|failed|null")){
                timeouts++;
            }

            if (trimLine.contains("HTTP-Out-Request")) {
                totalWebsubLogs++;
                totalReq++;
                String[] parts = trimLine.split("\\|");
                String log = parts[1].replace(": iam-cloud-correlation :","").trim();
                if(!req.contains(log)) {
                    req.add(log);
                }else {
                    req_dups.add(log);
                }
            }

            if (trimLine.contains("HTTP-Out-Response")) {
                totalWebsubLogs++;
                totalRes++;
                String[] parts = trimLine.split("\\|");
                String log = parts[1].replace(": iam-cloud-correlation :","").trim();
                if(!res.contains(log)) {
                    res.add(log);
                }else {
                    res_dups.add(log);
                }
            }
            line = reader.readLine();
        }

        System.out.println("total logs for event publishing: "+totalWebsubLogs);

        System.out.println("total requests: "+totalReq);
        System.out.println("total responses: "+totalRes);

        System.out.println("total unique request logs: "+req.size());
        System.out.println("total unique response logs: "+res.size());

        System.out.println("total req duplicates logs: "+req_dups.size());
        System.out.println("total res duplicates logs: "+res_dups.size());

        System.out.println("total OK responses: "+okResponses);
        System.out.println("total 503 responses: "+error503);
        System.out.println("total 502 responses: "+error502);
        System.out.println("total timeout responses: "+timeouts);

        CSVWriter writer1 = new CSVWriter(new FileWriter("/Users/lakshikaathapaththu/Desktop/correlation-logs-issue/program/output/request_log.csv"));
        req.forEach((value) -> {
            String line1[] = {value};
            writer1.writeNext(line1);
        });

        CSVWriter writer2 = new CSVWriter(new FileWriter("/Users/lakshikaathapaththu/Desktop/correlation-logs-issue/program/output/response_log.csv"));
        res.forEach((value) -> {
            String line2[] = {value};
            writer2.writeNext(line2);
        });

        writer1.flush();
        writer2.flush();
    }

    public static void readCSVLogFile () throws IOException {

        Scanner sc1 = new Scanner(new File("/Users/lakshikaathapaththu/Desktop/correlation-logs-issue/program/input/request_response_from_LA.csv"));

        int totalWebsubLogs = 0;
        int okResponses = 0;
        int error502 = 0;
        int error503 = 0;
        int totalReq = 0;
        int totalRes = 0;
        int timeouts = 0;

        List<String> req = new ArrayList<>();
        List<String> res = new ArrayList<>();

        List<String> req_dups = new ArrayList<>();
        List<String> res_dups = new ArrayList<>();

        while (sc1.hasNext()) {

            String trimLine = sc1.nextLine();

            if(trimLine.contains("LOGINS|/hub|completed|200|OK")) {
                okResponses++;
            }

            if(trimLine.contains("completed|503|Service Temporarily Unavailable")) {
                error503++;
            }

            if(trimLine.contains("|completed|502|Bad Gateway")) {
                error502++;
            }

            if(trimLine.contains("LOGINS|/hub|failed|null")){
                timeouts++;
            }

            if (trimLine.contains("HTTP-Out-Request")) {
                totalWebsubLogs++;
                totalReq++;
                String[] parts = trimLine.split("\\|");
                String log = parts[1].replace(": iam-cloud-correlation :","").trim();
                if(!req.contains(log)) {
                    req.add(log);
                }else {
                    req_dups.add(log);
                }
            }

            if (trimLine.contains("HTTP-Out-Response")) {
                totalWebsubLogs++;
                totalRes++;
                String[] parts = trimLine.split("\\|");
                String log = parts[1].replace(": iam-cloud-correlation :","").trim();
                if(!res.contains(log)) {
                    res.add(log);
                }else {
                    res_dups.add(log);
                }
            }
        }

        System.out.println("total logs for event publishing: "+totalWebsubLogs);

        System.out.println("total requests: "+totalReq);
        System.out.println("total responses: "+totalRes);

        System.out.println("total unique request logs: "+req.size());
        System.out.println("total unique response logs: "+res.size());

        System.out.println("total req duplicates logs: "+req_dups.size());
        System.out.println("total res duplicates logs: "+res_dups.size());

        System.out.println("total OK responses: "+okResponses);
        System.out.println("total 503 responses: "+error503);
        System.out.println("total 502 responses: "+error502);
        System.out.println("total timeout responses: "+timeouts);

        CSVWriter writer1 = new CSVWriter(new FileWriter("/Users/lakshikaathapaththu/Desktop/correlation-logs-issue/program/output/request_log.csv"));
        req.forEach((value) -> {
            String line1[] = {value};
            writer1.writeNext(line1);
        });

        CSVWriter writer2 = new CSVWriter(new FileWriter("/Users/lakshikaathapaththu/Desktop/correlation-logs-issue/program/output/response_log.csv"));
        res.forEach((value) -> {
            String line2[] = {value};
            writer2.writeNext(line2);
        });

        writer1.flush();
        writer2.flush();
    }

}