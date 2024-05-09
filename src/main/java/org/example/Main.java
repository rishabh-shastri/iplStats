package org.example;

import org.example.service.IPLStatsService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("Enter the team you want to find stats of");
            String team = bufferedReader.readLine();

            IPLStatsService iplStatsService = new IPLStatsService(team);
            iplStatsService.findStats();
        }
    }
}