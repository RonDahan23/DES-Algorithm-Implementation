import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

public class main {
    private JFrame frame;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JTextField keyField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            main guiApp = new main();
            guiApp.createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        frame = new JFrame("Enhanced DES Encryption GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(780, 330);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Create components
        inputTextArea = new JTextArea(10, 40);
        outputTextArea = new JTextArea(10, 40);
        keyField = new JTextField(20);
        JButton encryptButton = new JButton("Encrypt");
        JButton decryptButton = new JButton("Decrypt");
        JButton copyOutputButton = new JButton("Copy Output");

        // Add action listeners
        encryptButton.addActionListener(new EncryptButtonListener());
        decryptButton.addActionListener(new DecryptButtonListener());
        copyOutputButton.addActionListener(new CopyOutputButtonListener());

        // Create panels
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Input Text:"), BorderLayout.NORTH);
        inputPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(new JLabel("Output Text:"), BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);

        JPanel keyPanel = new JPanel();
        keyPanel.add(new JLabel("Enter Key:"));
        keyPanel.add(keyField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        controlPanel.add(keyPanel);
        controlPanel.add(buttonPanel);

        JPanel outputButtonPanel = new JPanel();
        outputButtonPanel.add(copyOutputButton);

        // Add panels to the frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(outputPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.WEST);
        frame.add(outputButtonPanel, BorderLayout.SOUTH);

        // Display the frame
        frame.setVisible(true);
    }

    DESImplementation a = new DESImplementation();

    private class EncryptButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Perform encryption here
            String inputText = inputTextArea.getText();
            String key = keyField.getText();
            String encryptedText = a.Encrypt(inputText, key);
            outputTextArea.setText(encryptedText);
        }
    }

    private class DecryptButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Perform decryption here
            String inputText = inputTextArea.getText();
            String key = keyField.getText();
            String decryptedText = a.Decrypt(inputText, key);
            outputTextArea.setText(decryptedText);
        }
    }

    private class CopyOutputButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String decryptedText = outputTextArea.getText();
            if (!decryptedText.isEmpty()) {
                StringSelection selection = new StringSelection(decryptedText);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        }
    }
}
