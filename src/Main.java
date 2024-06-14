import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main extends JFrame {
    private JPanel MainPanel;
    private JButton stopBtn;
    private JButton playBtn;
    private JButton button2;
    private JButton forwBtn;
    private JButton backBtn;
    private JPanel transportPanel;
    private JButton zoomBtn;
    private JButton upBtn;
    private JButton leftBtn;
    private JButton downBtn;
    private JButton rightBtn;
    private JPanel zoomPanel;
    private JButton windowBtn;
    private JSlider wheelSlider;
    private JButton scrubBtn;
    private JPanel wheelPanel;
    private JButton wheelLeft;
    private JButton wheelRight;
    static Receiver midiReceiver;
    private int lastWheelValue;
    private final int sliderCenter = 512;



    public Main() {
        setSize(700, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Transport");
        setContentPane(MainPanel);
        setVisible(true);

        // Configurar receiver midi
        try {
            // Obtener el puerto MIDI virtual (LoopMIDI en Windows)
            MidiDevice loopbackDevice = null;
            MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
            for (MidiDevice.Info info : infos) {
                if (info.getName().equals("santiport")) { // Cambiar por el nombre de tu puerto MIDI virtual
                    loopbackDevice = MidiSystem.getMidiDevice(info);
                    break;
                }
            }

            // Abrir el dispositivo MIDI virtual y obtener el receptor
            if (loopbackDevice != null) {
                loopbackDevice.open();
                midiReceiver = loopbackDevice.getReceiver();
            } else {
                throw new RuntimeException("No se encontró el dispositivo MIDI virtual.");
            }

        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        stopBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMidiNoteOn(0x5D, 127,0); // A#1 en el canal MIDI 0
                sendMidiNoteOff(0x5E,0); // Apagar la nota después de un tiempo
            }
        });

        playBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMidiNoteOn(0x5E, 127,0); // A#1 en el canal MIDI 0
                sendMidiNoteOff(0x5E,0); // Apagar la nota después de un tiempo
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMidiNoteOn(0x5F, 127,0);
                sendMidiNoteOff(0x5D,0);
                sendMidiNoteOn(0x5E, 127,0);
            }
        });
        // Manejar el click del botón
        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                sendMidiNoteOn(0x5B, 127, 0); // Enviar NOTE_ON cuando se presiona el botón

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                sendMidiNoteOff(0x5B, 0); // Enviar NOTE_OFF cuando se suelta el botón
                sendMidiNoteOn(0x5D, 127,0); // A#1 en el canal MIDI 0
                sendMidiNoteOff(0x5E,0); // Apagar la nota después de un tiempo
            }
        });
        forwBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                sendMidiNoteOn(0x5C, 127, 0); // Enviar NOTE_ON cuando se presiona el botón

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                sendMidiNoteOff(0x5C, 0); // Enviar NOTE_OFF cuando se suelta el botón
                sendMidiNoteOn(0x5D, 127,0); // A#1 en el canal MIDI 0
                sendMidiNoteOff(0x5E,0); // Apagar la nota después de un tiempo
            }
        });



        zoomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMidiNoteOn(0x64, 127,0);
                sendMidiNoteOff(0x64,0);
            }
        });
        leftBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMidiNoteOn(0x62, 127,0);
                sendMidiNoteOff(0x62,0);
            }
        });
        rightBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMidiNoteOn(0x63, 127,0);
                sendMidiNoteOff(0x63,0);
            }
        });
        upBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMidiNoteOn(0x60, 127,0);
                sendMidiNoteOff(0x60,0);
            }
        });
        downBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMidiNoteOn(0x61, 127,0);
                sendMidiNoteOff(0x61,0);
            }
        });
        windowBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMidiNoteOn(0x4E, 127,0);
                sendMidiNoteOff(0x4E,0);
            }
        });
        configureButton(windowBtn);
        configureButton(scrubBtn);
        configureButton(downBtn);
        configureButton(leftBtn);
        configureButton(rightBtn);
        configureButton(upBtn);
        configureButton(forwBtn);
        configureButton(playBtn);
        configureButton(stopBtn);
        configureButton(scrubBtn);
        configureButton(zoomBtn);
        configureButton(button2);
        configureButton(backBtn);




        lastWheelValue = sliderCenter;

        // Configurar el slider como una rueda
        wheelSlider.setMinimum(0);
        wheelSlider.setMaximum(1023);
        wheelSlider.setValue(sliderCenter);

        wheelSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int currentValue = wheelSlider.getValue();
                if (currentValue > sliderCenter) {
                    sendMidiControlChange(60, 1, 0); // Sentido horario, controller 60, valor 1, canal 0
                } else if (currentValue < sliderCenter) {
                    sendMidiControlChange(60, 65, 0); // Sentido antihorario, controller 60, valor 65, canal 0
                }
                wheelSlider.setValue(sliderCenter); // Reajustar el slider al centro
            }
        });
        scrubBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMidiNoteOn(0x65, 127,0);
                sendMidiNoteOff(0x65,0);
            }
        });
    }

    // CONFIGURAR ESTILO DE BOTONES //
    private void configureButton(JButton button) {
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
    }

    // ------------------------ METODOS PARA ENVIAR MENSAJES MIDI --------------//
    private void sendMidiMessage(int command, int value) {
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.CONTROL_CHANGE, 0, command, value);
            midiReceiver.send(message, -1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }
    // Metodo para enviar control change

    private void sendMidiControlChange(int controller, int value, int channel) {
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.CONTROL_CHANGE, channel, controller, value);
            midiReceiver.send(message, -1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }
    // Método para enviar mensaje de nota MIDI ON
    public static void sendMidiNoteOn(int note, int velocity, int channel) {
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.NOTE_ON, channel, note, velocity);
            midiReceiver.send(message, -1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    // Método para enviar mensaje de nota MIDI OFF (apagar la nota)
    public static void sendMidiNoteOff(int note, int channel) {
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.NOTE_OFF, channel, note, 0); // Velocidad 0 para apagar la nota
            midiReceiver.send(message, -1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main mainFrame = new Main();
                MasterFader masterFader = new MasterFader();
            }
        });
    }
}
