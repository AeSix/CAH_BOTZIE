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
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotListener extends ListenerAdapter<PircBotX> {

    private Map<String, Boolean> allowed = new HashMap<String, Boolean>();
    private Map<String, GameSession> sessions = new HashMap<String, GameSession>();
    private ScoreHandler scores = new ScoreHandler();

    @Override
    public void onInvite(InviteEvent<PircBotX> event) throws Exception {
        event.getBot().sendIRC().joinChannel(event.getChannel());
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        String message = event.getMessage().trim();
        message = Colors.removeFormattingAndColors(message);
        message = message.replace('\u200B' + "", "");

        if (event.getUser().getNick().equalsIgnoreCase("TheHumanity")) {
            if (message.contains("!join")) {
                if (!allowed.containsKey(event.getChannel().getName()) || allowed.get(event.getChannel().getName())) {
                    GameSession session = new GameSession();
                    event.getBot().sendIRC().message(event.getChannel().getName(), "!join");

                    sessions.put(event.getChannel().getName(), session);
                }
            } else if (message.contains("Scores: ")) {
                GameSession session = sessions.get(event.getChannel().getName());
                if (session != null) {
                    String[] parts = message.substring("Scores: ".length()).split(",");
                    for (String scorePair : parts) {
                        String[] scoreParts = scorePair.split(":");
                        if (scoreParts.length == 2) {
                            session.setScore(scoreParts[0].trim(), Integer.parseInt(scoreParts[1].trim()));
                        }
                    }
                }
            } else if (message.contains(event.getBot().getNick())) {
                if (message.contains("is picking a winner")) {
                    GameSession session = sessions.get(event.getChannel().getName());
                    if (session != null) {
                        int winner = session.pickWinner();
                        event.getBot().sendIRC().message(event.getChannel().getName(), "!p " + winner);
                    }
                } else if (message.contains("is the card czar")) {
                    GameSession session = sessions.get(event.getChannel().getName());
                    if (session != null) {
                        session.setPicking(true);
                        event.getBot().sendIRC().message(event.getChannel().getName(), Colors.RED + "Remember! I'm a bot that likes random numbers!");
                    }
                } else if (message.contains("has left the game")) {
                    sessions.put(event.getChannel().getName(), null);
                }
            }

            GameSession session = sessions.get(event.getChannel().getName());
            if (session == null) return;

            try {
                int n = Integer.parseInt(message.charAt(0) + "");
                session.addChoice(n, event.getMessage());
            } catch (NumberFormatException ignored) {
            }

            if (!session.isPicking() && message.contains("<BLANK>")) {
                List<Integer> cards = session.pick(count("<BLANK>", message));
                StringBuilder command = new StringBuilder("!p ");
                for (Integer i : cards) {
                    command.append(i).append(" ");
                }
                event.getBot().sendIRC().message(event.getChannel().getName(), command.toString().trim());
            }

            if (message.equalsIgnoreCase("The game has ended.")) {
                GameSession session1 = sessions.remove(event.getChannel().getName());
                if (session1 != null) {
                    session1.saveScores(scores);
                    event.getBot().sendIRC().message(event.getChannel().getName(), "Scores saved!");
                } else
                    event.getBot().sendIRC().message(event.getChannel().getName(), "Scores NOT saved: You didn't include me :(");
            }
        } else if (event.getUser().getNick().equalsIgnoreCase("turt2live") || event.getChannel().getVoices().contains(event.getUser()) || event.getChannel().getOps().contains(event.getUser())) {
            if (message.equalsIgnoreCase("~leave")) {
                event.getBot().sendIRC().message(event.getChannel().getName(), "!leave");
                sessions.put(event.getChannel().getName(), null);
            } else if (message.startsWith("~p")) {
                event.getBot().sendIRC().message(event.getChannel().getName(), "!" + message.substring(1));
            } else if (message.equalsIgnoreCase("~join")) {
                GameSession session = new GameSession();
                event.getBot().sendIRC().message(event.getChannel().getName(), "!join");

                sessions.put(event.getChannel().getName(), session);
            } else if (message.equalsIgnoreCase("~toggle") && event.getChannel().getOps().contains(event.getUser())) {
                allowed.put(event.getChannel().getName(), allowed.containsKey(event.getChannel().getName()) && !allowed.get(event.getChannel().getName()));
                event.getBot().sendIRC().message(event.getChannel().getName(), "Now " + (allowed.get(event.getChannel().getName()) ? "PLAYING" : "NOT PLAYING"));
            }
            // TODO: OP TOGGLE
            // TODO: NOT JUST TURT2LIVE
        }

        if (message.equalsIgnoreCase("~top")) {
            event.getBot().sendIRC().message(event.getChannel().getName(), scores.getTop());
        }
    }

    @Override
    public void onNotice(NoticeEvent<PircBotX> event) throws Exception {
        if (event.getUser().getNick().equalsIgnoreCase("TheHumanity")) {
            if (event.getMessage().startsWith("1.") && sessions.size() > 0) {
                GameSession session = sessions.entrySet().iterator().next().getValue();

                String[] parts = event.getMessage().split(Colors.BOLD);
                if (parts.length > 1) {
                    for (int i = 1; i < parts.length; i++) {
                        String card = parts[i].split(Colors.NORMAL)[0];
                        session.addCard(i, card);
                    }
                }
            }
        }
    }

    private int count(String findStr, String str) {
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {

            lastIndex = str.indexOf(findStr, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += findStr.length();
            }
        }
        return count;
    }
}
