package eu.cyzetlc.commentarii.service.message;

import eu.cyzetlc.commentarii.Commentarii;
import eu.cyzetlc.commentarii.service.json.JSONObject;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

@Getter
public class MessageHandler {
    // It's creating a list of languages that the bot supports.
    private final LinkedList<String> languageKeys = new LinkedList<>(Arrays.asList("de", "en"));
    // It's a map that stores the prefix of each language.
    private final LinkedHashMap<String, String> prefixKeys = new LinkedHashMap<>();
    // It's a map that stores the language of each guild.
    private final LinkedHashMap<Long, String> guildLanguages = new LinkedHashMap<>();
    // It's a reference to the messages object in the config.json file.
    private final JSONObject obj;

    public MessageHandler() {
        this.obj = Commentarii.getInstance().getConfig().getObject().getJSONObject("messages");

        try {
            CachedRowSet rs = Commentarii.getInstance().getQueryHandler().createBuilder(
                    "SELECT guild_id,language FROM settings"
            ).executeQuerySync();

            while (rs.next()) {
                Guild guild = Commentarii.getInstance().getJda().getGuildById(rs.getLong("guild_id"));
                if (guild != null) {
                    this.guildLanguages.put(guild.getIdLong(), rs.getString("language"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function takes a key and a prefix key, and adds the prefix key to the prefixKeys map.
     *
     * @param key The key to be prefixed.
     * @param prefixKey The key to be used as a prefix.
     */
    public void applyPrefix(String key, String prefixKey) {
        this.prefixKeys.put(key, prefixKey);
    }

    /**
     * It updates the database with the new language key and updates the cache
     *
     * @param guildId The ID of the guild to apply the language to.
     * @param languageKey The language key to apply to the guild.
     */
    public void applyLanguage(long guildId, String languageKey) {
        if (this.languageKeys.contains(languageKey)) {
            Commentarii.getInstance().getQueryHandler().createBuilder(
                    "UPDATE settings SET language = ? WHERE guild_id = ?"
            ).addParameters(Arrays.asList(languageKey, guildId)).executeUpdateAsync();
            this.guildLanguages.put(guildId, languageKey);
        }
    }

    /**
     * If the guild has a language set, return the message in that language, otherwise return the message in the default
     * language
     *
     * @param guildId The ID of the guild you want to get the message for.
     * @param key The key of the message you want to get.
     * @return The message for the guild
     */
    public String getMessageForGuild(long guildId, String key, String... args) {
        if (this.guildLanguages.containsKey(guildId) && !this.guildLanguages.get(guildId).equals("de")) {
            return this.getStaticMessage(this.guildLanguages.get(guildId) + "." + key, args);
        } else {
            return this.getStaticMessage(key, args);
        }
    }

    /**
     * If the key exists, replace the placeholders with the arguments and return the message. If the key doesn't exist,
     * return a message saying that the key wasn't found
     *
     * @param key The key of the message you want to get.
     * @return A string
     */
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
