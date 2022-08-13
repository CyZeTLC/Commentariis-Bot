package eu.cyzetlc.commentariis.service.database;

import java.sql.Connection;

public interface IMySQLExtension {
    Connection getNewConnection();

    void closeConnection(Connection connection);

    void stop();
}
