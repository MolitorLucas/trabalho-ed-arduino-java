package br.edu.fema.utils;

public interface ISensor<T> {

    void start();
    T createFromData(String data);

    void showData(T sensorDevice);
}
