package br.edu.fema;

import br.edu.fema.sensorarduino.model.SensorArduino;
import br.edu.fema.sensorarduino.service.SensorArduinoService;
import br.edu.fema.utils.ISensor;
import br.edu.fema.utils.SerialHelper;

public class AppArduinoJavaMain {

    public static void main (String[] args){

        ISensor<SensorArduino> sensorArduinoService = new SensorArduinoService(new SerialHelper("COM4", 9600));
        sensorArduinoService.start();
    }
}
