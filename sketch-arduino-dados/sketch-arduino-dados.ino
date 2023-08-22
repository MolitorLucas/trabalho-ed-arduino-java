const String dispositivos[] = {"1de950a9", "7db92f89", "12e3f04d"};

const String sensores[] = {"Temperatura", "Umidade", "Luminosidade"};

float sortearValor(String sensor){
    if(sensor == "Temperatura")
      return (float)random(1000, 3500)/100.00;
    else if (sensor == "Umidade")
      return (float)random(1000,10000)/100.0;
    else if (sensor == "Luminosidade")
      return random(100,1000);
  }


String sortearPosicao(String arr[]){
  return arr[random(0,3)];
}

void enviarDados(String dispositivo, String sensor, float valor) {
  Serial.println(dispositivo + "|" + sensor + ":" + valor);
}

void setup() {
  Serial.begin(9600);
  pinMode(13, OUTPUT);
}

void loop() {
  String sensorSelecionado = sortearPosicao(sensores);
  enviarDados(sortearPosicao(dispositivos), sensorSelecionado, sortearValor(sensorSelecionado));
  delay(5000);
}
