package br.edu.fema.sensorarduino.model;

public class SensorArduino {

    private String idDevice;

    private String idSensor;

    private Float valueDevice;

    public String getIdDevice(){
        return idDevice;
    }

    public String getIdSensor(){
        return idSensor;
    }

    public Float getValueDevice(){
        return valueDevice;
    }

    public void setIdDevice(String idDevice){
        this.idDevice = idDevice;
    }

    public void setIdSensor(String idSensor){
        this.idSensor = idSensor;
    }

    public void setValueDevice(String valueDevice){
        this.valueDevice = Float.parseFloat(valueDevice);
    }

    public SensorArduino(String idDevice, String idSensor, String valueDevice) {
        this.idDevice = idDevice;
        this.idSensor = idSensor;
        this.valueDevice = Float.parseFloat(valueDevice);
    }
}
