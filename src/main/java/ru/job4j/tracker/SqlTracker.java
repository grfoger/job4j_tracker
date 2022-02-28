package ru.job4j.tracker;

import ru.job4j.tracker.model.Item;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SqlTracker implements Store, AutoCloseable {

    private Connection cn;

    public SqlTracker() {
    }

    public SqlTracker(Connection connection) {
        this.cn = connection;
    }

    public void init() {
        try (InputStream in = SqlTracker.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Item getItemFromResult (ResultSet resultSet) throws SQLException {
            return new Item(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getTimestamp("created").toLocalDateTime()
            );
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }

    @Override
    public Item add(Item item) {
        try (PreparedStatement statement = cn.prepareStatement("insert into items(name, created) values (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, item.getName());
            statement.setTimestamp(2, Timestamp.valueOf(item.getCreated()));
            statement.execute();
            try (ResultSet getId = statement.getGeneratedKeys()) {
                if (getId.next()) {
                    item.setId(getId.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public boolean replace(int id, Item item) {
        boolean isReplaced = false;
        try (PreparedStatement statement = cn.prepareStatement("update items set name = ?, created = ? where id = ?;")) {
            statement.setString(1, item.getName());
            statement.setTimestamp(2, Timestamp.valueOf(item.getCreated()));
            statement.setInt(3, id);
            item.setId(id);
            isReplaced = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isReplaced;
    }

    @Override
    public boolean delete(int id) {
        boolean isDeleted = false;
        try (PreparedStatement statement = cn.prepareStatement("delete from items where id = ?;")) {
            statement.setInt(1, id);
            isDeleted = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    @Override
    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        try (PreparedStatement statement = cn.prepareStatement("select * from items;")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(getItemFromResult(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public List<Item> findByName(String key) {
        List<Item> items = new ArrayList<>();
        try (PreparedStatement statement = cn.prepareStatement("select * from items where name like ?;")) {
            statement.setString(1, key);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(getItemFromResult(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public Item findById(int id) {
        Item itemById = null;
        try (PreparedStatement statement = cn.prepareStatement("select * from items where id = ?;")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    itemById = getItemFromResult(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemById;
    }

    public static void main(String[] args) {
        SqlTracker tracker = new SqlTracker();
        tracker.init();
        tracker.replace(2, new Item("ppp"));
        System.out.println(tracker.findByName("one").toString());
    }
}