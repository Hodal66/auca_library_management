package com.library.dao;
import com.library.utils.HibernateUtil;
import org.hibernate.Session;


public class Connection {
    

    public Session getSession(){

        return HibernateUtil.getSessionFactory().openSession();
    }
}
