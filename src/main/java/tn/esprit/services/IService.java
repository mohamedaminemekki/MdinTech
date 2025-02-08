package tn.esprit.services;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {

    List<T> readList() throws SQLException;
    void add(T t) throws SQLException;
    void update(T t) throws SQLException;

    void addP(T t) throws SQLException;
}
