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

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, IrcException {
        Configuration.Builder<PircBotX> builder = new Configuration.Builder<>();
        builder.setAutoNickChange(true);
        builder.setAutoReconnect(true);
        builder.setLogin("REDACTED");
        builder.setServerPassword("REDACTED");
        builder.setName("REDACTED");
        builder.setServer("REDACTED", REDACTED);
        builder.addAutoJoinChannel("REDACTED");
        builder.addListener(new BotListener());
        PircBotX bot = new PircBotX(builder.buildConfiguration());
        bot.startBot();
    }

}
