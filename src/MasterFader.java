import javax.imageio.ImageIO;
import javax.sound.midi.ShortMessage;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class MasterFader extends JFrame {
    private JPanel masterSectionPanel;
    //private JLabel backgroundLabel;
    private JSlider faderSlider;
    private JSlider sliderPanMaster;

    public MasterFader(){
        setContentPane(masterSectionPanel);
        setSize(220,640);
        setResizable(false);
        setTitle("Master Fader");
        setLocationRelativeTo(null);
        setVisible(true);

        faderSlider.setOrientation(SwingConstants.VERTICAL);
        faderSlider.setPreferredSize(new Dimension(180,500));
        faderSlider.setUI(new CustomSliderUI(faderSlider));
        faderSlider.setMinimum(-8192);  // Valor mínimo del pitch wheel
        faderSlider.setMaximum(7089);   // Valor máximo del pitch wheel
        faderSlider.setOpaque(false);

        sliderPanMaster.setSize(90,70);

        faderSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Al presionar el slider, enviar el mensaje Note On
                Main.sendMidiNoteOn(0x68, 127, 1); // Asegúrate de ajustar el canal y la nota según tu configuración
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Al soltar el slider, enviar el mensaje Note Off
                Main.sendMidiNoteOff(0x68, 1); // Asegúrate de ajustar el canal y la nota según tu configuración
            }
        });

        faderSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = faderSlider.getValue();
                sendPitchWheelMessage(value);
            }
        });


    }

    // Clase para personalizar la apariencia del slider
    private class CustomSliderUI extends BasicSliderUI {
        private BufferedImage knobImage;

        public CustomSliderUI(JSlider b) {
            super(b);
            try {
                // Cargar la imagen del indicador del fader
                URL imageURL = getClass().getResource("/assets/INDICADORfader.png");
                knobImage = ImageIO.read(imageURL);
                BufferedImage originalImage = ImageIO.read(imageURL);
                int scaledWidth = 65; // Ancho deseado de la imagen (ajústalo según tus necesidades)
                int scaledHeight = 103; // Alto deseado de la imagen (ajústalo según tus necesidades)
                knobImage = resizeImage(originalImage, scaledWidth, scaledHeight);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        // Método para redimensionar la imagen
        private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
            Image tmp = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();

            return resizedImage;
        }
        @Override
        public void paintThumb(Graphics g) {
            // Dibujar la imagen en lugar del indicador estándar
            Rectangle knobBounds = thumbRect;
            g.drawImage(knobImage, knobBounds.x, knobBounds.y, knobBounds.width, knobBounds.height, null);
        }

        @Override
        protected Dimension getThumbSize() {
            // Tamaño de la imagen del fader
            return new Dimension(knobImage.getWidth(), knobImage.getHeight());
        }
    }

    // Método para enviar mensaje de pitch wheel
    private void sendPitchWheelMessage(int value) {
        try {
            ShortMessage pitchMessage = new ShortMessage();
            int channel = 8;  // Canal 9 en MIDI (0 a 15)

            // Ajustar el rango del pitch wheel MIDI (-8192 a 7089)
            int pitchWheelRange = 7089 - (-8192);
            int pitchWheelValue = value - (-8192);

            // Convertir el valor de pitch wheel a los bytes de mensaje MIDI
            int pitchWheelIntValue = (int) (pitchWheelValue / (double) pitchWheelRange * 16383);
            int msb = (pitchWheelIntValue >> 7) & 0x7F;
            int lsb = pitchWheelIntValue & 0x7F;
            pitchMessage.setMessage(ShortMessage.PITCH_BEND, channel, lsb, msb);

            // Enviar el mensaje de pitch wheel al receptor MIDI
            Main.midiReceiver.send(pitchMessage, -1);  // Asegúrate de tener una instancia de Receiver
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
