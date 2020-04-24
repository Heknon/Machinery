package me.oriharel.machinery.inventory;

public interface Datable<T> {
    T getStoredData();
    Datable<T> setStoredData(T data);
}
