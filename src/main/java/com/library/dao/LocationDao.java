package com.library.dao;

import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.library.model.Location;

public class LocationDao {

    Connection connection = new Connection();

    public String saveLocation(Location location) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = connection.getSession();
            transaction = session.beginTransaction();

            session.persist(location);

            transaction.commit();
            return "Location saved Successfully";
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return "Error saving location: " + e.getMessage();
        } finally {
            if (session != null) session.close();
        }
    }

    public Location getLocationById(UUID id) {
        Session session = null;
        try {
            session = connection.getSession();
            return session.get(Location.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public void deleteAllLocations() {
    Session session = connection.getSession();
    Transaction tx = session.beginTransaction();
    session.createQuery("DELETE FROM Location").executeUpdate();
    tx.commit();
    session.close();
}
}