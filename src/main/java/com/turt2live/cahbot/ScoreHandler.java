/*******************************************************************************
 * Copyright (C) 2014 Travis Ralston (turt2live)
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.turt2live.cahbot;

import org.pircbotx.Colors;

import java.io.*;
import java.util.*;

public class ScoreHandler {

    public void incrementScore(String player, int amount) throws IOException {
        File target = new File("scores.txt");
        if(!target.exists())target.createNewFile();

        Map<String, Integer> scores = new HashMap<String, Integer>();
        BufferedReader reader = new BufferedReader(new FileReader(target));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                String[] parts = line.split(" = ", 2);
                scores.put(parts[0], Integer.parseInt(parts[1]));
            }
        }
        reader.close();

        if (scores.containsKey(player)) amount += scores.get(player);
        scores.put(player, amount);

        BufferedWriter writer = new BufferedWriter(new FileWriter(target, false));
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            writer.write(entry.getKey() + " = " + entry.getValue());
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    public String getTop() {
        File target = new File("scores.txt");
        if (!target.exists()) return "No top scores!";

        Map<String, Integer> scores = new TreeMap<String, Integer>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(target));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0) {
                    String[] parts = line.split(" = ", 2);
                    scores.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return Colors.RED + "Error reading scores: " + e.getMessage();
        }

        scores = sortByComparator(scores);

        StringBuilder builder = new StringBuilder();

        int c = 0;
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (c == 5) break;

            String username = entry.getKey();
            username = username.charAt(0) + "" + ('\u200B') + username.substring(1);

            builder.append(username).append(": ").append(entry.getValue());
            if (c != 4 && c != scores.size() - 1) builder.append(",");
            builder.append(" ");

            c++;
        }

        if (scores.size() == 0) return "No top scores!";
        return builder.toString().trim();
    }

    private Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // sort list based on comparator
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        // put sorted list into map again
        //LinkedHashMap make sure order in which keys were inserted
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

}
