package org.example.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IPLStatsService {

    private String[][] fixtures;
    private String[][] pointsTable;
    private String team;
    public IPLStatsService(String team) {
        this.team = team;
        String fixturesCsvFile = "fixtures.csv";
        this.fixtures = csvtoString2d(fixturesCsvFile);
        String ptsTableCsvFile = "ptsTable.csv";
        this.pointsTable = csvtoString2d(ptsTableCsvFile);
    }

    public void findStats() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM", Locale.ENGLISH);
        String date = LocalDate.now().format(formatter);
        int i=0;
        for (String[] fixture : fixtures) {
            if (fixture[0].equals(date)) {
                break;
            }
            i++;
        }
        int[] prob = new int[2];
        int bestRank = bestRank(fixtures, pointsTable, team, i, new ArrayList<>());
        probabilityToQualify(fixtures, pointsTable, team, i, new ArrayList<>(), prob);
        System.out.println("Best Rank:" + bestRank);
        System.out.println("Prob to qualify" + ((double)(prob[1])/(double)prob[0]));
    }

    private String[][] csvtoString2d(String fixturesCsvFile) {
        String line;
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fixturesCsvFile))) {
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                rows.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[][] dataArray = new String[rows.size()][];
        dataArray = rows.toArray(dataArray);
        return dataArray;
    }

    private int bestRank(String[][] fixtures, String[][] pointsTable, String team, int index, List<String> path) {
        if (index == fixtures.length) {
            Arrays.sort(pointsTable, (a, b) -> {
                if (a[0].equals(team) && Integer.parseInt(a[2])==Integer.parseInt(b[2])) {
                    return -1;
                }
                if (b[0].equals(team) && Integer.parseInt(a[2])==Integer.parseInt(b[2])) {
                    return 1;
                }
                return Integer.parseInt(b[2])-Integer.parseInt(a[2]);
            });
            int i=1;
            for (String[] teamInPtsTable : pointsTable) {
                if (teamInPtsTable[0].equals(team)) {
                    return i;
                }
                i++;
            }
        }
        String[] match = fixtures[index];
        String[][] newPointsTable = null;
        if (match[1].equals(team)) {
            newPointsTable = updatePointsTable(team, match[2], Arrays.stream(pointsTable)
                                         .map(String[]::clone)
                                         .toArray(String[][]::new));
            String matchResult = team + "win" + match[2];
            path.add(matchResult);
            int x =  bestRank(fixtures, newPointsTable, team, index+1, path);
            path.remove(path.size() - 1);
            return x;
        } else if (match[2].equals(team)) {
            newPointsTable = updatePointsTable(team, match[1], Arrays.stream(pointsTable)
                                         .map(String[]::clone)
                                         .toArray(String[][]::new));
            String matchResult = team + "win" + match[1];
            path.add(matchResult);
            int y =  bestRank(fixtures, newPointsTable, team, index+1, path);
            path.remove(path.size() - 1);
            return y;

        } else {
            newPointsTable = updatePointsTable(match[1], match[2], Arrays.stream(pointsTable)
                    .map(String[]::clone)
                    .toArray(String[][]::new));
            String matchResult = match[1] + "win" + match[2];
            path.add(matchResult);
            int a = bestRank(fixtures, newPointsTable, team, index+1, path);
            path.remove(path.size() - 1);

            newPointsTable = updatePointsTable(match[2], match[1], Arrays.stream(pointsTable)
                    .map(String[]::clone)
                    .toArray(String[][]::new));
            matchResult = match[2] + "win" + match[1];
            path.add(matchResult);
            int b = bestRank(fixtures, newPointsTable, team, index+1, path);
            path.remove(path.size() - 1);
            return Math.min(a,b);
        }
    }

    private void probabilityToQualify(String[][] fixtures, String[][] pointsTable, String team, int index, List<String> path, int[] prob) {
        if (index == fixtures.length) {
            prob[0]+=1;
            Arrays.sort(pointsTable, (a, b) -> {
                if (a[0].equals(team) && Integer.parseInt(a[2])==Integer.parseInt(b[2])) {
                    return -1;
                }
                if (b[0].equals(team) && Integer.parseInt(a[2])==Integer.parseInt(b[2])) {
                    return 1;
                }
                return Integer.parseInt(b[2])-Integer.parseInt(a[2]);
            });
            int i=1;
            for (String[] teamInPtsTable : pointsTable) {
                if (teamInPtsTable[0].equals(team)) {
                    if (i<=4) {
                        prob[1]+=1;
                    }
                    return;
                }
                i++;
            }
        }
        String[] match = fixtures[index];
        String[][] newPointsTable = null;
        newPointsTable = updatePointsTable(match[1], match[2], Arrays.stream(pointsTable)
                .map(String[]::clone)
                .toArray(String[][]::new));
        String matchResult = match[1] + "win" + match[2];
        path.add(matchResult);
        probabilityToQualify(fixtures, newPointsTable, team, index+1, path, prob);
        path.remove(path.size() - 1);

        newPointsTable = updatePointsTable(match[2], match[1], Arrays.stream(pointsTable)
                .map(String[]::clone)
                .toArray(String[][]::new));
        matchResult = match[2] + "win" + match[1];
        path.add(matchResult);
        probabilityToQualify(fixtures, newPointsTable, team, index+1, path, prob);
        path.remove(path.size() - 1);
    }

    private String[][] updatePointsTable(String winner, String loser, String[][] pointsTable) {
        for (String[] team : pointsTable) {
            if(team[0].equals(winner)) {
                team[1] = ((Integer)(Integer.parseInt(team[1]) + 1)).toString();
                team[2] = ((Integer)(Integer.parseInt(team[2]) + 1)).toString();
            }
            else if(team[0].equals(loser)) {
                team[1] = ((Integer)(Integer.parseInt(team[1]) + 1)).toString();
            }
        }
        return pointsTable;
    }

}
