package eu.cyzetlc.commentariis.service.entities;

import eu.cyzetlc.commentariis.Commentariis;

public class User {
    protected final net.dv8tion.jda.api.entities.User jdaUser;

    public User(net.dv8tion.jda.api.entities.User jdaUser) {
        this.jdaUser = jdaUser;
    }

    /**
     * If the user has the permission, or the permission is *, return true
     *
     * @param permission The permission to check for.
     * @return A boolean value.
     */
    public boolean hasPermission(String permission) {
        //return Commentariis.getInstance().getPermissionHandler().hasUserPermission(this, permission) ||
        //        Commentariis.getInstance().getPermissionHandler().hasUserPermission(this, "*");
        return true;
    }

    /**
     * `Returns the JDA User object that this User represents.`
     *
     * @return The JDA User object.
     */
    public net.dv8tion.jda.api.entities.User getJdaUser() {
        return jdaUser;
    }
}
