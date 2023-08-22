package br.edu.fema.sensorarduino.service;

import br.edu.fema.sensorarduino.model.SensorArduino;
import br.edu.fema.utils.ISensor;
import br.edu.fema.utils.SerialHelper;

public class SensorArduinoService implements ISensor<SensorArduino> {

    private final SerialHelper sh;

    public SensorArduinoService(SerialHelper sh){
        this.sh = sh;
    }
    public void start() {
        sh.init();

        sh.sendKeyboard(true);
        sh.setPrefixCommands("OK", "ERROR");
        sh.delayEventListener(10000);

        sh.setOnEventListener((String data) -> {
            showData(createFromData(data));
        });
    }

    public SensorArduino createFromData(String data) {
        String idSensor = data.split("|")[0];
        String deviceValue = data.split("|")[1];
        String idDevice = deviceValue.split(":")[0];
        String value = deviceValue.split(":")[1];

        return new SensorArduino(idSensor, idDevice, value);
    }

    public void showData(SensorArduino sensor){
        System.out.println(sensor.getIdDevice() + "|" + sensor.getIdSensor() + ": ");
    }
}
