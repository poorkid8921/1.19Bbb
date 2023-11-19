package main.utils;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;

public class DiscordWebhook {
    private final String url;
    private final List<EmbedObject> embeds = new ArrayList<>();
    private String username;
    private String avatarUrl;

    public DiscordWebhook(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void addEmbed(EmbedObject embed) {
        this.embeds.add(embed);
    }

    public void execute() throws IOException {
        JSONObject json = new JSONObject();

        json.put("content", null);
        json.put("username", this.username);
        json.put("avatar_url", this.avatarUrl);
        json.put("tts", false);

        List<JSONObject> embedObjects = new ArrayList<>();

        for (EmbedObject embed : this.embeds) {
            JSONObject jsonEmbed = new JSONObject();

            jsonEmbed.put("title", embed.getTitle());
            jsonEmbed.put("description", null);
            jsonEmbed.put("url", null);

            if (embed.getColor() != null) {
                Color color = embed.getColor();
                int rgb = color.getRed();
                rgb = (rgb << 8) + color.getGreen();
                rgb = (rgb << 8) + color.getBlue();

                jsonEmbed.put("color", rgb);
            }

            EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
            List<EmbedObject.Field> fields = embed.getFields();

            if (thumbnail != null) {
                JSONObject jsonThumbnail = new JSONObject();

                jsonThumbnail.put("url", thumbnail.getUrl());
                jsonEmbed.put("thumbnail", jsonThumbnail);
            }

            List<JSONObject> jsonFields = new ArrayList<>();
            for (EmbedObject.Field field : fields) {
                JSONObject jsonField = new JSONObject();

                jsonField.put("name", field.getName());
                jsonField.put("value", field.getValue());
                jsonField.put("inline", field.isInline());

                jsonFields.add(jsonField);
            }

            jsonEmbed.put("fields", jsonFields.toArray());
            embedObjects.add(jsonEmbed);
        }

        json.put("embeds", embedObjects.toArray());

        URL url = new URL(this.url);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Webhook");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        OutputStream stream = connection.getOutputStream();
        stream.write(json.toString().getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();

        connection.getInputStream().close();
        connection.disconnect();
    }

    public static class EmbedObject {
        private final List<Field> fields = new ArrayList<>();
        private String title;
        private Color color;
        private Thumbnail thumbnail;

        public String getTitle() {
            return title;
        }

        public EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public Color getColor() {
            return color;
        }

        public EmbedObject setColor(Color color) {
            this.color = color;
            return this;
        }

        public Thumbnail getThumbnail() {
            return thumbnail;
        }

        public EmbedObject setThumbnail(String url) {
            this.thumbnail = new Thumbnail(url);
            return this;
        }

        public List<Field> getFields() {
            return fields;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        private static class Thumbnail {
            private final String url;

            private Thumbnail(String url) {
                this.url = url;
            }

            private String getUrl() {
                return url;
            }
        }

        private static class Field {
            private final String name;
            private final String value;
            private final boolean inline;

            private Field(String name, String value, boolean inline) {
                this.name = name;
                this.value = value;
                this.inline = inline;
            }

            private String getName() {
                return name;
            }

            private String getValue() {
                return value;
            }

            private boolean isInline() {
                return inline;
            }
        }
    }

    private static class JSONObject {
        private final HashMap<String, Object> map = new HashMap<>();

        void put(String key, Object value) {
            if (value != null) {
                map.put(key, value);
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            builder.append("{");

            int i = 0;
            for (Map.Entry<String, Object> entry : entrySet) {
                Object val = entry.getValue();
                builder.append(quote(entry.getKey())).append(":");

                if (val instanceof String) {
                    builder.append(quote(String.valueOf(val)));
                } else if (val instanceof Integer) {
                    builder.append(Integer.valueOf(String.valueOf(val)));
                } else if (val instanceof Boolean) {
                    builder.append(val);
                } else if (val instanceof JSONObject) {
                    builder.append(val);
                } else if (val.getClass().isArray()) {
                    builder.append("[");
                    int len = Array.getLength(val);
                    for (int j = 0; j < len; j++) {
                        builder.append(Array.get(val, j).toString()).append(j != len - 1 ? "," : "");
                    }
                    builder.append("]");
                }

                builder.append(++i == entrySet.size() ? "}" : ",");
            }

            return builder.toString();
        }

        private String quote(String string) {
            return "\"" + string + "\"";
        }
    }
}