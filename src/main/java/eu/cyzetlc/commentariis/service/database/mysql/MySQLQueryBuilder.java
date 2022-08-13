package eu.cyzetlc.commentariis.service.database.mysql;

import eu.cyzetlc.commentariis.service.database.IMySQLExtension;
import lombok.Getter;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Getter
public class MySQLQueryBuilder {
    private final ExecutorService executorService;
    private final Connection connection;
    private final IMySQLExtension extension;

    private RowSetFactory factory;
    private LinkedList<Object> params;
    private String query;

    public MySQLQueryBuilder(IMySQLExtension extension) {
        this.executorService = Executors.newFixedThreadPool(15);
        this.params = new LinkedList<>();
        this.extension = extension;
        this.connection = extension.getNewConnection();
        try {
            this.factory = RowSetProvider.newFactory();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CachedRowSet executeQuerySync() {
        return this.executeQueryOrUpdateSync(false);
    }

    public CachedRowSet executeUpdateSync() {
        return this.executeQueryOrUpdateSync(true);
    }

    public void executeQueryAsync(Consumer<CachedRowSet> callback) {
        this.executeQueryOrUpdateAsync(callback, false);
    }

    public void executeUpdateAsync() {
        this.executeQueryOrUpdateAsync(null, true);
    }

    private void executeQueryOrUpdateAsync(Consumer<CachedRowSet> callback, boolean useUpdateStatement) {
        this.executorService.execute(() -> {
            CachedRowSet rs = MySQLQueryBuilder.this.executeQueryOrUpdateSync(useUpdateStatement);
            if (callback != null) {
                callback.accept(rs);
            }
        });
    }

    private CachedRowSet executeQueryOrUpdateSync(boolean useUpdateStatement) {
        PreparedStatement statement = null;
        ResultSet rs = null;
        CachedRowSet var5;

        try {
            statement = this.connection.prepareStatement(this.query);

            for (int i = 0; i < this.params.size(); ++i) {
                statement.setObject(i + 1, this.params.get(i));
            }

            CachedRowSet crs;

            if (useUpdateStatement) {
                statement.executeUpdate();
                this.closeItems(null, statement);
                return null;
            }

            rs = statement.executeQuery();
            if (rs == null) {
                return null;
            }

            crs = factory.createCachedRowSet();
            crs.populate(rs);
            this.closeItems(rs, statement);
            var5 = crs;
        } catch (SQLException var17) {
            this.printDebugInformation();
            var17.printStackTrace();
            return null;
        } finally {
            try {
                this.closeItems(rs, statement);
            } catch (SQLException var16) {
                var16.printStackTrace();
            }

        }

        return var5;
    }

    private void closeItems(ResultSet rs, PreparedStatement statement) throws SQLException {
        if (rs != null) {
            rs.close();
        }

        if (this.connection != null) {
            this.connection.close();
        }

        if (statement != null) {
            statement.close();
        }

        this.extension.closeConnection(this.connection);
    }

    public MySQLQueryBuilder printDebugInformation() {
        System.out.println("-----------------------------");
        System.out.println("Query - Debug");
        System.out.println("Query: " + this.query);
        System.out.println(" ");
        System.out.println("Parameters: ");

        for (Object param : this.params) {
            System.out.println("- " + param);
        }

        System.out.println("-----------------------------");
        return this;
    }

    public MySQLQueryBuilder setQuery(String qry) {
        this.query = qry;
        return this;
    }

    public MySQLQueryBuilder addParameter(Object obj) {
        this.params.add(obj);
        return this;
    }

    public MySQLQueryBuilder addParameters(Collection<Object> objects) {
        this.params.addAll(objects);
        return this;
    }
}
