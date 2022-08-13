package eu.cyzetlc.commentariis.service.message;

import eu.cyzetlc.commentariis.Commentarii;
import eu.cyzetlc.commentariis.service.json.JSONObject;
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
    private final LinkedList<String> languageKeys = new LinkedList<>(Arrays.asList("de", "en"));
    private final LinkedHashMap<String, String> prefixKeys = new LinkedHashMap<>();
    private final LinkedHashMap<Long, String> guildLanguages = new LinkedHashMap<>();
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

    public void applyPrefix(String key, String prefixKey) {
        this.prefixKeys.put(key, prefixKey);
    }

    public void applyLanguage(long guildId, String languageKey) {
        if (this.languageKeys.contains(languageKey)) {
            Commentarii.getInstance().getQueryHandler().createBuilder(
                    "UPDATE settings SET language = ? WHERE guild_id = ?"
            ).addParameters(Arrays.asList(languageKey, guildId)).executeUpdateAsync();
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
