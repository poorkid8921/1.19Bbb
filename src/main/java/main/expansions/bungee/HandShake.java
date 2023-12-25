/*
 * This file is part of BungeeGuard, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package main.expansions.bungee;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class HandShake {
    private static final Gson GSON = new Gson();
    private static final Type PROPERTY_LIST_TYPE = new TypeToken<List<JsonObject>>() {
    }.getType();

    public static HandShake decodeAndVerify(String handshake) {
        try {
            return decodeAndVerify0(handshake);
        } catch (Exception e) {
            new Exception("Failed to decode handshake", e).printStackTrace();
            return null;
        }
    }

    private static HandShake decodeAndVerify0(String handshake) {
        if (handshake.length() > 2500)
            return null;

        String[] split = handshake.split("\00");
        if (split.length != 3 && split.length != 4)
            return null;

        String serverHostname = split[0];
        String socketAddressHostname = split[1];
        UUID uniqueId = UUID.fromString(split[2].replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));

        if (split.length == 3)
            return null;

        List<JsonObject> properties = new LinkedList<>(GSON.fromJson(split[3], PROPERTY_LIST_TYPE));
        if (properties.isEmpty())
            return null;

        String bungeeGuardToken = null;
        for (Iterator<JsonObject> iterator = properties.iterator(); iterator.hasNext(); ) {
            JsonObject property = iterator.next();
            if (property.get("name").getAsString().equals("bungeeguard-token")) {
                if (bungeeGuardToken != null)
                    return null;

                bungeeGuardToken = property.get("value").getAsString();
                iterator.remove();
            }
        }

        if (bungeeGuardToken == null)
            return null;

        if (!bungeeGuardToken.equals("3tKYsShqn6RHx7ogptDlD2ufehq6Kac330zSMb8wDqOB48n60jge88kStbaqt5d4"))
            return null;

        return new Success(serverHostname, socketAddressHostname, uniqueId, GSON.toJson(properties, PROPERTY_LIST_TYPE));
    }

    public static final class Success extends HandShake {
        private final String serverHostname;
        private final String socketAddressHostname;
        private final UUID uniqueId;
        private final String propertiesJson;

        Success(String serverHostname, String socketAddressHostname, UUID uniqueId, String propertiesJson) {
            this.serverHostname = serverHostname;
            this.socketAddressHostname = socketAddressHostname;
            this.uniqueId = uniqueId;
            this.propertiesJson = propertiesJson;
        }

        public String serverHostname() {
            return this.serverHostname;
        }

        public String socketAddressHostname() {
            return this.socketAddressHostname;
        }

        public UUID uniqueId() {
            return this.uniqueId;
        }

        public String propertiesJson() {
            return this.propertiesJson;
        }
    }
}