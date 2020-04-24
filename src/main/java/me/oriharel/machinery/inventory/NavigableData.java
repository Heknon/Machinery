package me.oriharel.machinery.inventory;

public interface NavigableData<T> {
    T getStoredData();
    NavigableData<T> setStoredData(T data);
}
