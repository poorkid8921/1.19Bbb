package bab.bbb.utils;

import bab.bbb.Bbb;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VPNChecker {
    static boolean works1 = true;
    static boolean works2 = false;
    static boolean works3 = false;
    static boolean works4 = false;
    static boolean works5 = false;
    static boolean works6 = false;

    public static void checkPlayerAsync(Player p, String ip, String apikey) {
        Bukkit.getScheduler().runTaskAsynchronously(Bbb.getInstance(), () ->
        {
            String result = "ERROR";
            try {
                URL myURL = new URL("http://v2.api.iphub.info/ip/" + ip);
                HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("X-Key", apikey);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.connect();

                BufferedReader br;
                if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299)
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                else
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                result = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            final String finalResult = result;
            Bukkit.getScheduler().runTask(Bbb.getInstance(), () ->
            {
                if (finalResult.equals("ERROR")) {
                    if (works1) {
                        VPNChecker.checkPlayerAsync(p, p.getAddress().getAddress().getHostAddress(), "MjA0ODU6bzE0SmZESFJPWjdLYTR6MkxUWEtLWDM1dkkzMlhKMjY=");
                        works2 = true;
                        works1 = false;
                    }

                    if (works2) {
                        VPNChecker.checkPlayerAsync(p, p.getAddress().getAddress().getHostAddress(), "MjA0ODY6dWQ1akFUMWR6NXhmZ0FaWHJZVENqUmp6UXNGbFJwcXY=");
                        works3 = true;
                        works2 = false;
                    }

                    if (works3) {
                        VPNChecker.checkPlayerAsync(p, p.getAddress().getAddress().getHostAddress(), "MjA0ODg6NHJIeGpTT3JJTzI4ajBwTlo1eUF3dngyVHhtdmNvT0Q=");
                        works4 = true;
                        works3 = false;
                    }

                    if (works4) {
                        VPNChecker.checkPlayerAsync(p, p.getAddress().getAddress().getHostAddress(), "MjA0OTA6MUV3cFk1SVZsV1RZdHZiYVp3dFRaTWVzRk44NmdSdzM=");
                        works5 = true;
                        works4 = false;
                    }

                    if (works5) {
                        VPNChecker.checkPlayerAsync(p, p.getAddress().getAddress().getHostAddress(), "MjA0ODk6QXEydVQycjJqQm82ZUlNdWlyR1g4RG9WWXF4czBtdEY=");
                        works6 = true;
                        works5 = false;
                    }

                    if (works6) {
                        VPNChecker.checkPlayerAsync(p, p.getAddress().getAddress().getHostAddress(), "MjA0ODc6MTY5dXQ2OXRNOFUzZlpuRFRTSVNUbG5teTlEaXBlR2M=");
                        works1 = true;
                        works6 = false;
                    }
                } else {
                    try {
                        final JSONObject obj2 = (JSONObject) new JSONParser().parse(finalResult);
                        long severity2 = (long) obj2.get("block");

                        if (severity2 == 1)
                            p.kickPlayer(Methods.parseText("&7Proxies aren't &callowed"));
                    } catch (ParseException eee) {
                        if (works1) {
                            VPNChecker.checkPlayerAsync(p, p.getAddress().getAddress().getHostAddress(), "MjA0ODU6bzE0SmZESFJPWjdLYTR6MkxUWEtLWDM1dkkzMlhKMjY=");
                            works2 = true;
                            works1 = false;
                        }

                        if (works2) {
                            VPNChecker.checkPlayerAsync(p, p.getAddress().getAddress().getHostAddress(), "MjA0ODY6dWQ1akFUMWR6NXhmZ0FaWHJZVENqUmp6UXNGbFJwcXY=");
                            works3 = true;
                            works2 = false;
                        }

                        if (works3) {
                            VPNChecker.checkPlayerAsync(p, p.getAddress().getAddress().getHostAddress(), "MjA0ODg6NHJIeGpTT3JJTzI4ajBwTlo1eUF3dngyVHhtdmNvT0Q=");
                            works4 = true;
                            works3 = false;
                        }

                        if (works4) {
                            VPNChecker.checkPlayerAsync(p, p.getAddress().getAddress().getHostAddress(), "MjA0OTA6MUV3cFk1SVZsV1RZdHZiYVp3dFRaTWVzRk44NmdSdzM=");
                            works5 = true;
                            works4 = false;
                        }

                        if (works5) {
                            VPNChecker.checkPlayerAsync(p, p.getAddress().getAddress().getHostAddress(), "MjA0ODk6QXEydVQycjJqQm82ZUlNdWlyR1g4RG9WWXF4czBtdEY=");
                            works6 = true;
                            works5 = false;
                        }

                        if (works6) {
                            VPNChecker.checkPlayerAsync(p, p.getAddress().getAddress().getHostAddress(), "MjA0ODc6MTY5dXQ2OXRNOFUzZlpuRFRTSVNUbG5teTlEaXBlR2M=");
                            works1 = true;
                            works6 = false;
                        }
                    }
                }
            });
        });
    }
}