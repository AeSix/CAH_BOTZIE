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

import java.io.IOException;
import java.util.*;

public class GameSession {

    private List<String> cards = new ArrayList<String>();
    private List<String> choices = new ArrayList<String>();
    private Map<String, Integer> scores = new HashMap<String, Integer>();
    private boolean picking = false;
    private Random random = new Random();

    public int pickWinner() {
        picking = false;
        return random.nextInt(choices.size()) + 1;
    }

    public void addChoice(int n, String message) {
        if (n == 1) choices.clear();
        choices.add(message);
    }

    public void setPicking(boolean b) {
        this.picking = b;
    }

    public boolean isPicking() {
        return this.picking;
    }

    public List<Integer> pick(int count) {
        List<Integer> values = new ArrayList<Integer>();
        while (values.size() != count) {
            int n = random.nextInt(cards.size()) + 1;
            if (!values.contains(n)) values.add(n);
        }
        return values;
    }

    public void addCard(int n, String card) {
        if (n == 1) cards.clear();
        cards.add(card);
    }

    public void saveScores(ScoreHandler scores) throws IOException {
        for (Map.Entry<String, Integer> score : this.scores.entrySet()) {
            scores.incrementScore(score.getKey(), score.getValue());
        }
    }

    public void setScore(String player, int score) {
        scores.put(player, score);
    }
}
