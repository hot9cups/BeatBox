import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;
import java.util.*;  //for ArrayList really...
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.File;

public class BeatBox {
    JPanel mainPanel;
    ArrayList<JCheckBox> checkboxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame theFrame;
    JPanel buttonPanel;
    JLabel tempoLabel;
    JPanel tempoPanel;
    
    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat",
        "Open Hi-Hat","Acoustic Snare", "Crash Cymbal", "Hand Clap",
        "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga",
        "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo",
        "Open Hi Conga"};
    int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};

    public static void main(String[] args){
        new BeatBox().buildGUI();
    }

    public void buildGUI(){
        theFrame = new JFrame("Cyber BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        background.setBackground(Color.ORANGE);

        checkboxList = new ArrayList<JCheckBox>();
        //Box buttonBox = new Box(BoxLayout.Y_AXIS);
        GridLayout buttonBox = new GridLayout(8,1);
        buttonBox.setVgap(1);
        buttonBox.setHgap(2);
        buttonPanel = new JPanel(buttonBox);
        buttonPanel.setBackground(Color.WHITE);

        JButton start = new JButton("Start");
        start.setBackground(Color.GREEN.darker());
        start.setForeground(Color.WHITE);
        start.addActionListener(new MyStartListener());
        start.setFont(new Font("Serif", Font.BOLD, 28));
        buttonPanel.add(start);

        JButton stop = new JButton("Stop");
        stop.setBackground(Color.RED);
        stop.setForeground(Color.WHITE);
        stop.addActionListener(new MyStopListener());
        stop.setFont(new Font("Serif", Font.BOLD, 32));
        buttonPanel.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        upTempo.setForeground(Color.WHITE);
        upTempo.setBackground(Color.BLACK);
        upTempo.setFont(new Font("Serif", Font.BOLD, 28));
        buttonPanel.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        downTempo.setForeground(Color.WHITE);
        downTempo.setBackground(Color.BLACK);
        downTempo.setFont(new Font("Serif", Font.BOLD, 28));
        buttonPanel.add(downTempo);

        JButton randomNotes = new JButton("Random Notes");
        randomNotes.addActionListener(new MyRandomNotesListener());
        randomNotes.setForeground(Color.BLACK);
        randomNotes.setBackground(Color.WHITE);
        randomNotes.setFont(new Font("Serif", Font.BOLD, 28));
        buttonPanel.add(randomNotes);

        JButton selectAll = new JButton("Select All");
        selectAll.addActionListener(new MySelectAllListener());
        selectAll.setForeground(Color.BLACK);
        selectAll.setBackground(Color.WHITE);
        selectAll.setFont(new Font("Serif", Font.BOLD, 28));
        buttonPanel.add(selectAll);

        JButton saveBeat = new JButton("Save Beat");
        saveBeat.addActionListener(new MySendListener());
        saveBeat.setForeground(Color.PINK);
        saveBeat.setBackground(Color.magenta);
        saveBeat.setFont(new Font("Serif", Font.BOLD, 28));
        buttonPanel.add(saveBeat);

        JButton loadBeat = new JButton("load Beat");
        loadBeat.addActionListener(new MyReadInListener());
        loadBeat.setForeground(Color.PINK);
        loadBeat.setBackground(Color.magenta);
        loadBeat.setFont(new Font("Serif", Font.BOLD, 28));
        buttonPanel.add(loadBeat);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++){
            Label label = new Label(instrumentNames[i]);
            label.setFont(new Font("Serif", Font.BOLD, 28));
            nameBox.add(label);
        }

        background.add(BorderLayout.EAST, buttonPanel);
        background.add(BorderLayout.WEST, nameBox);

        GridLayout grid = new GridLayout(16,16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        mainPanel.setBackground(Color.ORANGE.darker());
        background.add(BorderLayout.CENTER, mainPanel);

        for (int i = 0; i < 256; i++){
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            c.setAlignmentX(10);
            c.setAlignmentY(10);
            c.setBackground(Color.YELLOW);
            c.setForeground(Color.PINK);
            checkboxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();
        
        
        JButton reset = new JButton("Reset All");
        reset.addActionListener(new MyResetListener());
        reset.setFont(new Font("Arial", Font.BOLD, 20));
        reset.setBackground(new Color(192, 0, 255)); //Violet
        reset.setForeground(Color.WHITE);

        GridLayout tempoGrid = new GridLayout(1,3);
        tempoGrid.setHgap(20);
        float tempoFactor = sequencer.getTempoFactor();
        tempoLabel = new JLabel("Current Tempo : " + tempoFactor);
        tempoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        tempoPanel = new JPanel(tempoGrid);
        tempoPanel.add(reset);
        tempoPanel.add(tempoLabel);
        JLabel creator = new JLabel("Yours Truly, Ayush");
        creator.setBackground(Color.WHITE); //Violet
        creator.setForeground(new Color(192, 0, 255));
        
        tempoPanel.add(creator);
        creator.setFont(new Font("Arial", Font.BOLD, 20));
        background.add(BorderLayout.SOUTH, tempoPanel);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        theFrame.pack();
        theFrame.getContentPane().add(background);
        //theFrame.setBounds(0, 0, (int)height, (int)width);
        theFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        //theFrame.setUndecorated(true);
        theFrame.setVisible(true);
        //System.out.println(theFrame.getSize());
        //theFrame.setLocation(dim.width/2-theFrame.getSize().width/2, 0);
        //theFrame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
    }

    public void setUpMidi(){
        try{
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e){e.printStackTrace();}
    }

    public void buildTrackAndStart(){
        int[] trackList = null;
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < 16; i++){
            trackList = new int[16];
            int key = instruments[i];
            for (int j = 0; j < 16; j++){
                JCheckBox jc = (JCheckBox)checkboxList.get(j + (16*i));
                if (jc.isSelected()){
                    trackList[j] = key;
                } else {
                    trackList[j] = 0;
                }
            }
            makeTracks(trackList);
            track.add(makeEvent(176,1,127,0,16));
        }
        track.add(makeEvent(192,9,1,0,15));
        try{
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch(Exception e) {e.printStackTrace();}
    }

    public class MyStartListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor + 0.1));
            tempoLabel.setText(String.format("Current Tempo : %.3f",(tempoFactor + 0.1)));
            tempoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        }
    }
    public class MyDownTempoListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor - 0.1));
            tempoLabel.setText(String.format("Current Tempo : %.3f",(tempoFactor - 0.1)));
            tempoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        }
    }

    public class MyRandomNotesListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            for (JCheckBox c: checkboxList)
                c.setSelected(false);
            for (JCheckBox c: checkboxList)
                if (Math.random() > 0.5)
                    c.setSelected(true);
            //buildTrackAndStart();
        }
    }

    public class MySelectAllListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            for (JCheckBox c: checkboxList)
                c.setSelected(true);
        }
    }

    public class MyResetListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            for (JCheckBox c: checkboxList)
                c.setSelected(false);
            sequencer.setTempoFactor(1.0f);
            tempoLabel.setText(String.format("Current Tempo : %.3f",sequencer.getTempoFactor()));
            tempoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        }
    }

    public class MySendListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            boolean[] checkboxState = new boolean[256];

            for(int i = 0; i < 256; i++){
                JCheckBox check = (JCheckBox) checkboxList.get(i);
                if (check.isSelected()){
                    checkboxState[i] = true;
                }
            }
            
            try{
                FileOutputStream fileStream = new FileOutputStream(new File("Checkbox.ser"));
                ObjectOutputStream os = new ObjectOutputStream(fileStream);
                os.writeObject(checkboxState);
            } catch(Exception ex){ex.printStackTrace();}
        }
    }

    public class MyReadInListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            boolean [] checkboxState = null;
            try{
                FileInputStream fileIn = new FileInputStream(new File("Checkbox.ser"));
                ObjectInputStream is = new ObjectInputStream(fileIn);
                checkboxState = (boolean[]) is.readObject();
            } catch(Exception ex){ex.printStackTrace();}

            for(int i = 0; i < 256; i++){
                JCheckBox check = (JCheckBox) checkboxList.get(i);
                if (checkboxState[i]){
                    check.setSelected(true);
                }
                else{
                    check.setSelected(false);
                }
            }
            sequencer.stop();
            buildTrackAndStart();
        }
    }

    public void makeTracks(int [] list){
        for (int i = 0; i < 16; i++){
            int key = list[i];
            if (key != 0){
                track.add(makeEvent(144,9,key,100,i));
                track.add(makeEvent(128,9,key,100,i+1));
            }
        }
    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick){
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch(Exception e) {e.printStackTrace(); }
        return event;
        }
}