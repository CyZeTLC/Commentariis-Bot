package eu.cyzetlc.commentariis.service.entities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class Embed {
    private final EmbedBuilder builder;

    private Embed(EmbedBuilder builder) {
        this.builder = builder;
    }

    /**
     * This function adds an image to the embed.
     *
     * @param url The url of the image.
     * @return The Embed object.
     */
    public Embed addImage(String url) {
        this.builder.setImage(url);
        return this;
    }

    /**
     * `this.builder.setThumbnail(url);`
     *
     * @param url The URL of the image to use as the thumbnail.
     * @return The Embed object.
     */
    public Embed addThumbnail(String url) {
        this.builder.setThumbnail(url);
        return this;
    }

    /**
     * This function sets the author of the book.
     *
     * @param author The author of the book.
     */
    public void setAuthor(String author) {
        this.builder.setAuthor(author);
    }

    /**
     * Sets the footer of the embed
     *
     * @param footer The footer of the embed.
     */
    public void setFooter(String footer) {
        this.builder.setFooter(footer);
    }

    /**
     * It appends a description to the embed
     *
     * @param description The description of the embed.
     * @return The Embed object
     */
    public Embed appendDescriptions(String description) {
        this.builder.appendDescription(description);
        return this;
    }

    /**
     * It adds a field to the embed
     *
     * @param title The title of the field.
     * @param content The content of the field. This is a string, and it may not contain any markup.
     * @return The Embed object.
     */
    public Embed addField(String title, String content) {
        this.builder.addField(title, content, false);
        return this;
    }

    /**
     * Adds a field to the embed
     *
     * @param title The title of the field.
     * @param content The text of the field.
     * @param inline If true, the field will be displayed inline.
     * @return The Embed object.
     */
    public Embed addField(String title, String content, boolean inline) {
        this.builder.addField(title, content, inline);
        return this;
    }

    public Embed addBlankField(boolean inline) {
        this.builder.addBlankField(inline);
        return this;
    }

    /**
     * It returns the message embed
     *
     * @return A MessageEmbed object
     */
    public MessageEmbed build() {
        return this.builder.build();
    }

    /**
     * It creates an embed object with the title, content and color you specify
     *
     * @param title The title of the embed
     * @param content The content of the embed
     * @param color The color of the embed.
     * @return An Embed object
     */
    public static Embed getEmbed(String title, String content, Color color) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(color);
        builder.setTitle(title);
        builder.setDescription(content);
        builder.setFooter("Bot by CyZeTLC × Tom C.", "https://avatars.githubusercontent.com/u/57322153?v=4");
        return new Embed(builder);
    }
}
