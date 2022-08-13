package eu.cyzetlc.commentariis.service.message;

import eu.cyzetlc.commentariis.Commentarii;
import eu.cyzetlc.commentariis.service.json.JSONObject;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

@Getter
public class MessageHandler {
    private final LinkedList<String> languageKeys = new LinkedList<>(Arrays.asList("de", "en"));
    private final LinkedHashMap<String, String> prefixKeys = new LinkedHashMap<>();
    private final LinkedHashMap<Long, String> guildLanguages = new LinkedHashMap<>();
    private final JSONObject obj;

    public MessageHandler() {
        this.obj = Commentarii.getInstance().getConfig().getObject().getJSONObject("messages");
    }

    public void applyPrefix(String key, String prefixKey) {
        this.prefixKeys.put(key, prefixKey);
    }

    public void applyLanguage(long guildId, String languageKey) {
        if (this.languageKeys.contains(languageKey)) {
            this.guildLanguages.put(guildId, languageKey);
        }
    }

    public String getMessageForGuild(long guildId, String key, String... args) {
        if (this.guildLanguages.containsKey(guildId) && !this.guildLanguages.get(guildId).equals("de")) {
            return this.getStaticMessage(this.guildLanguages.get(guildId) + "." + key, args);
        } else {
            return this.getStaticMessage(key, args);
        }
    }

    public String getStaticMessage(@NotNull String key, String... args) {
        String firstKey = key.split("\\.")[0];
        String message = "Not Found: " + key;

        if (this.obj.has(key)) {
            String content = this.obj.getString(key);
            for (int i = 0; i < args.length; i++) {
                content = content.replace("{" + i + "}", args[i]);
            }
            message = content;

            if (this.prefixKeys.containsKey(firstKey) && !key.equals(this.prefixKeys.get(firstKey))) {
                message = content.replace("%prefix%", this.getStaticMessage(this.prefixKeys.get(firstKey)));
            }
        }
        return message;
    }
}
