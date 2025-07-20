package com.library.utils;
import java.util.Properties;

import com.library.model.Location;
import com.library.model.User; // Add this import

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class HibernateUtil {
    
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        try {
            if (sessionFactory == null) {
                Configuration configuration = new Configuration();
                Properties properties = new Properties();
                properties.put(Environment.JAKARTA_JDBC_DRIVER, "org.postgresql.Driver");
                properties.put(Environment.JAKARTA_JDBC_URL, "jdbc:postgresql://localhost:5432/auca_library_db");
                properties.put(Environment.JAKARTA_JDBC_USER, "postgres");
                properties.put(Environment.JAKARTA_JDBC_PASSWORD, "Mhthodol@2022%");
                properties.put(Environment.SHOW_SQL, "true");
                properties.put(Environment.HBM2DDL_AUTO, "create");

                configuration.setProperties(properties);

                configuration.addAnnotatedClass(Location.class);
                configuration.addAnnotatedClass(User.class); // Register User entity

                sessionFactory = configuration.buildSessionFactory();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return sessionFactory;
    }
}