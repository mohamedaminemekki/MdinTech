package org.example.mdintech.service;

import java.util.List;

public interface Iservice<T> {
    boolean save(T obj);
    void update(T obj);
    void delete(T obj);
    T findById(int id);
    List<T> findAll();
}
