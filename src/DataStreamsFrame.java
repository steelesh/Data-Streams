import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static java.nio.file.StandardOpenOption.CREATE;

public class DataStreamsFrame extends JFrame {
    JPanel mainPnl;
    JPanel searchPnl;
    JPanel resPnl;
    JPanel ctrlPnl;
    JButton quitBtn;
    JButton loadBtn;
    JButton searchBtn;
    JButton clearBtn;
    JTextArea origArea;
    JScrollPane scroller;
    JTextArea filtArea;
    JScrollPane scroller1;
    JTextField searchField;
    JLabel title;
    JFileChooser chooser;
    File workingDirectory = new File(System.getProperty("user.dir"));
    File selectedTxtFile;
    ArrayList<String> filt = new ArrayList<>();
    String rec;
    int line = 0;
    DataStreamsFrame(){
        mainPnl = new JPanel();
        setSize(1125, 875);
        mainPnl.setLayout(new BorderLayout());
        setTitle("Data Streams");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        createSearchPnl();
        mainPnl.add(searchPnl, BorderLayout.NORTH);
        createResultsPnl();
        mainPnl.add(resPnl, BorderLayout.CENTER);
        createCtrlPnl();
        mainPnl.add(ctrlPnl, BorderLayout.SOUTH);
        add(mainPnl);
        setVisible(true);
    }
    private void createSearchPnl(){
        searchPnl = new JPanel();
        title = new JLabel("Search file by String:");
        searchField = new JTextField(30);
        searchPnl.add(title);
        searchPnl.add(searchField);
    }
    private void createResultsPnl(){
        resPnl = new JPanel();
        origArea = new JTextArea(20, 45);
        origArea.setEditable(false);
        scroller = new JScrollPane(origArea);
        filtArea = new JTextArea(20, 45);
        filtArea.setEditable(false);
        scroller1 = new JScrollPane(filtArea);
        resPnl.add(scroller);
        resPnl.add(scroller1);
    }
    private void createCtrlPnl(){
        ctrlPnl = new JPanel();
        ctrlPnl.setLayout(new GridLayout(1, 4));
        loadBtn = new JButton("Load");
        loadBtn.addActionListener((ActionEvent ae) ->{
            chooser = new JFileChooser();
            chooser.setCurrentDirectory(workingDirectory);
            chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "text"));
            int result = chooser.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION){
                selectedTxtFile = chooser.getSelectedFile();
                loadBtn.setText(selectedTxtFile.getName());
                Path file = selectedTxtFile.toPath();
                InputStream in = null;
                try {
                    in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                line = 0;
                while(true){
                    try {
                        if (!reader.ready())
                            break;
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    try{
                        rec = reader.readLine();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    line++;
                    filt.add(rec);
                    origArea.append(rec + "\n");
                }
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JOptionPane.showMessageDialog(null, "File loaded!", "Success!", JOptionPane.INFORMATION_MESSAGE);
            }
            else if(result == JFileChooser.CANCEL_OPTION){
                selectedTxtFile = null;
            }
        });
        searchBtn = new JButton("Search");
        searchBtn.addActionListener((ActionEvent ae) ->{
            String inputTxt = searchField.getText();
            if(selectedTxtFile == null){
                JOptionPane.showMessageDialog(null, "Must load file first!", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            else if(inputTxt.isEmpty()){
                JOptionPane.showMessageDialog(null, "Cannot search for empty string!", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            else{
                filt.stream()
                        .filter(s -> s.contains(searchField.getText()))
                        .forEach(s -> filtArea.append("Word \"" + searchField.getText() + "\" found!\n\n" + s + "\n\n\n"));
            }
        });
        clearBtn = new JButton("Clear");
        clearBtn.addActionListener((ActionEvent ae) ->{
            selectedTxtFile = null;
            loadBtn.setText("Load");
            origArea.setText(null);
            filtArea.setText(null);
            searchField.setText(null);
            JOptionPane.showMessageDialog(null, "Cleared program!", "Success!", JOptionPane.INFORMATION_MESSAGE);
        });
        quitBtn = new JButton("Quit");
        quitBtn.addActionListener((ActionEvent ae) -> System.exit(0));
        ctrlPnl.add(loadBtn);
        ctrlPnl.add(searchBtn);
        ctrlPnl.add(clearBtn);
        ctrlPnl.add(quitBtn);
    }
}