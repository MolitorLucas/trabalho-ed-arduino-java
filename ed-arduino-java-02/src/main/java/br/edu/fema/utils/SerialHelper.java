package br.edu.fema.utils;

import gnu.io.*;

import java.io.*;
import java.util.*;

public final class SerialHelper {

    public static final String EXIT_COMMAND = "exit";

    public SerialHelper(String commPort, int baudRate) {
        this.commPort = commPort;
        this.baudRate = baudRate;
    }

    private static final int TIMEOUT = 10000; // 2 segundos

    private final String commPort;
    private final int baudRate;
    private OnEventListener onEventListener;
    private Scanner keyboardScanner;
    private boolean terminateOnExit = false;
    private List<String> prefixes = new ArrayList<>();
    private long delayedMillis = -1;
    private long lastMillisEventListener;

    private SerialPort serialPort;
    private BufferedReader br;
    private OutputStream os;
    private DataOutputStream dout;

    public SerialPort init() {
        CommPortIdentifier portId = this.find(this.commPort).orElseThrow(IllegalArgumentException::new);

        try {
            serialPort = this.connect(portId, this.baudRate);
            br = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            os = serialPort.getOutputStream();
        } catch (Exception e) {
            return null;
        }

        return serialPort;
    }

    private Optional<CommPortIdentifier> find(String commPort) {
        final Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();

        while (portEnum.hasMoreElements()) {
            final CommPortIdentifier portIdentifier = (CommPortIdentifier) portEnum.nextElement();

            if (portIdentifier.getName().equals(commPort)) {
                return Optional.of(portIdentifier);
            }
        }

        return Optional.empty();
    }

    public void sendKeyboard(boolean terminateOnExit) {
        this.terminateOnExit = terminateOnExit;

        new Thread(new Runnable() {
            @Override
            public void run() {
                SerialHelper.this.keyboardScanner = new Scanner(System.in);

                String keyboardData = SerialHelper.this.keyboardScanner.nextLine();

                while (!EXIT_COMMAND.equals(keyboardData)) {
                    try {
                        os.write(keyboardData.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("Comando enviado...: " + keyboardData);
                    keyboardData = SerialHelper.this.keyboardScanner.nextLine();
                }

                if (SerialHelper.this.terminateOnExit) {
                    System.exit(0);
                }
            }
        }).start();
    }

    public void setPrefixCommands(String... prefixes) {
        this.prefixes.clear();
        if (prefixes.length > 0) {
            this.prefixes.addAll(Arrays.asList(prefixes));
        }
    }

    private boolean hasPrefix(String data) {
        for (String prefix : prefixes) {
            if (data.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

    public void delayEventListener(long delayedMillis) {
        if (delayedMillis > 1000) {
            this.delayedMillis = delayedMillis;
        }
    }

    public interface OnEventListener {
        void process(String data);
    }

    public void setOnEventListener(OnEventListener onEventListener) {
        Objects.requireNonNull(onEventListener);
        this.onEventListener = onEventListener;
    }

    private SerialPort connect(CommPortIdentifier portId, int baudRate) throws PortInUseException, UnsupportedCommOperationException, TooManyListenersException {
        serialPort = (SerialPort) portId.open(this.getClass().getName(), TIMEOUT);

        // 8N1
        serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        serialPort.disableReceiveTimeout();
        serialPort.enableReceiveThreshold(1);
        serialPort.notifyOnDataAvailable(true);

        serialPort.addEventListener(new SerialPortEventListener() {
            @Override
            public void serialEvent(SerialPortEvent event) {
                if (Objects.isNull(SerialHelper.this.onEventListener)) {
                    return;
                }

                if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                    try {
                        final String data = br.readLine();
                        final boolean delayedEvent = delayedMillis == -1 || (System.currentTimeMillis() - lastMillisEventListener) > delayedMillis;

                        if (delayedEvent) {
                            lastMillisEventListener = System.currentTimeMillis();
                        }

                        if (hasPrefix(data) || delayedEvent) {
                            SerialHelper.this.onEventListener.process(data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return serialPort;
    }
}
