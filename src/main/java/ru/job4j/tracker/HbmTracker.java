package ru.job4j.tracker;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import ru.job4j.tracker.model.Item;

import java.util.List;

public class HbmTracker implements Store, AutoCloseable {
    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();



    @Override
    public Item add(Item item) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.save(item);
        session.getTransaction().commit();
        session.close();
        return item;
    }

    /*
    Для реализации replace и delete используйте HQL синтаксис,
    для возврата boolean будете использовать
    возвращаемое значение метода executeUpdate
     */

    @Override
    public boolean replace(int id, Item item) {
        Session session = sf.openSession();
        session.beginTransaction();
        item.setId(id);

        Query query = session.createQuery("update Item i set "
                        + "i.name = :name, "
                        + "i.created = :created, "
                        + "i.description = :description "
                        + "where i.id = :id")
                .setParameter("name", item.getName())
                .setParameter("created", item.getCreated())
                .setParameter("description", item.getDescription())
                .setParameter("id", id);
        int result = query.executeUpdate();

        session.getTransaction().commit();
        session.close();
        return result != 0;
    }

    @Override
    public boolean delete(int id) {
        Session session = sf.openSession();
        session.beginTransaction();

        Query query = session.createQuery("delete from Item where id = :id")
                .setParameter("id", id);
        int result = query.executeUpdate();

        session.getTransaction().commit();
        session.close();
        return result != 0;
    }

    @Override
    public List<Item> findAll() {
        Session session = sf.openSession();
        session.beginTransaction();
        List result = session.createQuery("from Item").list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    @Override
    public List<Item> findByName(String key) {
        Session session = sf.openSession();
        session.beginTransaction();
        List result = session.createQuery("from Item i where i.name = :key").setParameter("key", key).list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    @Override
    public Item findById(int id) {
        Session session = sf.openSession();
        session.beginTransaction();
        Item result = session.get(Item.class, id);
        session.getTransaction().commit();
        session.close();
        return result;
    }

    @Override
    public void close() throws Exception {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    public static void main(String[] args) {
        HbmTracker hbm = new HbmTracker();
        hbm.delete(6);
    }
}