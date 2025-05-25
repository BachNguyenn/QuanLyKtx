package dao;

import java.util.List;

public interface Searchable<T> {
    List<T> searchByName(String name);
}