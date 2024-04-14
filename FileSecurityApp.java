import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileSecurityApp extends JFrame{
    // Create JFrame object
    JFrame frame;

    // Create JMenuBar, JMenu, and JMenuItem objects for the menu bar
    JMenuBar menuBar = new JMenuBar();
    JMenu file = new JMenu("File");
    JMenuItem fileItem = new JMenuItem("Exit");
    JMenu help = new JMenu("Help");
    JMenuItem helpItem = new JMenuItem("Help");
    JMenu about = new JMenu("About");
    JMenuItem aboutItem = new JMenuItem("About");

    // Create JPanel object
    JPanel panel = new JPanel();
    
    // Create JButton objects for various buttons
    JButton browseButton = new JButton("Browse Files:");
    JButton encryptButton = new JButton("ENCRYPT");
    JButton decryptButton = new JButton("DECRYPT");
    JButton cancelButton = new JButton("CANCEL");
    JButton browseKeyButton = new JButton("Open Key File:");

    // Create JLabel objects for various labels
    JLabel titleLabel = new JLabel("File Security Application");
    JLabel keyLabel = new JLabel("Key:");
    JLabel keyFileLabel = new JLabel("Use Key File?");

    // Create JCheckBox object for the key file checkbox
    JCheckBox keyFileCheckBox = new JCheckBox();

    // Create JTextField objects for various text fields
    JTextField keyField = new JTextField();
    JTextField fileField = new JTextField();
    JTextField keyFileField = new JTextField();

    // Create ImageIcon object for the application logo
    ImageIcon image = new ImageIcon("logo.png");

    // Create Font objects for buttons
    Font buttonsFont = new Font("Consolas", Font.PLAIN, 12);

    // Create Color objects for background, button text, and button backgrounds
    Color backgroundColor = new Color(0x272822); 
    Color lightButtonTextColor = new Color(0xEEEEEE);
    Color darkButtonTextColor = new Color(0x000000);
    Color browseButtonBgColor = new Color(0xD4E1F5);
    Color encryptButtonBgColor = new Color(0x7EA6E0);
    Color decryptButtonBgColor = new Color(0xE76D33);
    Color cancelButtonBgColor = new Color(0x808080);

    public FileSecurityApp(){
        setFrame(); // Set up the frame
        setPanel(); // Set up the panel

        // Add ActionListener to fileItem
        fileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            // Exit the application
            System.exit(0);
            }
        });
        // Add ActionListener to helpItem
        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            // Display a help message using JOptionPane
            JOptionPane.showMessageDialog(null, "This is a simple file security application that allows you to encrypt and decrypt files using a key.\n\nTo encrypt a file, click on the 'Browse Files' button to select a file, enter a key, and click on the 'ENCRYPT' button.\n\nTo decrypt a file, click on the 'Browse Files' button to select an encrypted file, enter the key used to encrypt the file, and click on the 'DECRYPT' button.\n\nThe encrypted file will be saved in a folder with the key, which can be used to decrypt the file again.", "Help", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        // Add ActionListener to aboutItem
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "ADET4: File Security Application\nVersion 1.0\nDeveloped by Daniela M. Cantillo\nBSIT 3a", "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Check if the keyFileCheckBox is selected
        keyFileCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(keyFileCheckBox.isSelected()){
                    keyFileField.setVisible(true);
                    browseKeyButton.setVisible(true);
                    keyField.setEditable(false);
                    keyField.setText("");
                } else{
                    keyFileField.setVisible(false);
                    browseKeyButton.setVisible(false);
                    keyField.setEditable(true);
                    keyFileField.setText("");
                }
            }
        });
        
        // Allows the user to select a key file
        browseKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a JFileChooser object
                JFileChooser fileChooser = new JFileChooser();

                // Enable drag and drop functionality
                fileChooser.setDragEnabled(true);

                // Hide the control buttons
                fileChooser.setControlButtonsAreShown(false);

                // Set the dialog title
                fileChooser.setDialogTitle("Choose a key file");

                // Set the file filter to only accept .txt files
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    public boolean accept(java.io.File f) {
                        return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
                    }
                    public String getDescription() {
                        return "Text Files (*.key)";
                    }
                });

                // Allow only single file selection
                fileChooser.setMultiSelectionEnabled(false);

                // Disable file view
                fileChooser.setFileView(null);

                // Show the open dialog and wait for user selection
                fileChooser.showOpenDialog(null);

                // Set the selected file path to the keyFileField text field
                keyFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        // Allows the user to select a file to encrypt or decrypt
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.showOpenDialog(null);
                fileField.setText(fileChooser.getSelectedFile().getAbsolutePath());

                fileChooser.setDragEnabled(true);
                fileChooser.setControlButtonsAreShown(false);
                fileChooser.setDialogTitle("Choose a file to encrypt or decrypt");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setFileView(null);
            }
        });
        
        // Limit the keyField text field to 16 characters
        keyField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (keyField.getText().length() >= 16) // limit to 16 characters
                    e.consume();
            }
        });
        
        // Add ActionListener to the encryptButton
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the values from the text fields
                String key = keyField.getText();
                String file = fileField.getText();
                String keyFile = keyFileField.getText();

                // Check if any of the fields are empty
                if((key.isEmpty() & keyFile.isEmpty()) || file.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                } else{
                    // Call the encryptFile() method
                    encryptFile();
                }

                // Clear the text fields
                keyField.setText("");
                fileField.setText("");
                keyFileField.setText("");
            }
        });
        // Add ActionListener to the decryptButton
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            // Get the values from the text fields
            String key = keyField.getText();
            String file = fileField.getText();
            String keyFile = keyFileField.getText();

            // Check if any of the fields are empty
            if((key.isEmpty() & keyFile.isEmpty()) || file.isEmpty()){
                // Display an error message if any field is empty
                JOptionPane.showMessageDialog(null, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            } else{
                // Call the decryptFile() method if all fields are filled
                decryptFile();
            }

            // Clear the text fields
            keyField.setText("");
            fileField.setText("");
            keyFileField.setText("");
            }
        });
        // Add ActionListener to the cancelButton
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the text fields
                keyField.setText("");
                fileField.setText("");
                keyFileField.setText("");
            }
        });
    }
    
    // Method to set up the frame
    private void setFrame(){
        // Set the frame properties
        this.setTitle("ADET4: File Security App by Daniela M. Cantillo");
        this.setIconImage(image.getImage());
        this.setResizable(false);
        this.setSize(480, 320);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add the components to the frame
        this.add(panel);
        this.add(menuBar);
        this.add(titleLabel);
        this.add(keyLabel);
        this.add(keyField);
        this.add(fileField);
        this.add(browseButton);
        this.add(encryptButton);
        this.add(decryptButton);
        this.add(cancelButton);
        this.add(keyFileLabel);
        this.add(keyFileCheckBox);
        this.add(keyFileField);
        this.add(browseKeyButton);

        // Set the menu bar
        this.setJMenuBar(menuBar);
        this.setVisible(true);
    }

    // Method to set up the panel
    private void setPanel(){
        // Set the panel properties
        panel.setLayout(null);
        panel.setBounds(0, 0, 480, 280);
        panel.setBackground(backgroundColor);
        
        // Add the components to the panel
        panel.add(titleLabel);
        panel.add(browseButton);
        panel.add(fileField);
        panel.add(keyLabel);
        panel.add(keyField);
        panel.add(encryptButton);
        panel.add(decryptButton);
        panel.add(cancelButton);
        panel.add(keyFileLabel);
        panel.add(keyFileCheckBox);
        panel.add(keyFileField);
        panel.add(browseKeyButton);

        // Add menu items to the menu bar
        menuBar.add(file);
        file.add(fileItem);
        menuBar.add(help);
        help.add(helpItem);
        menuBar.add(about);
        about.add(aboutItem);
        
        // Set the properties of menuBar
        menuBar.setBackground(lightButtonTextColor);
        menuBar.setForeground(darkButtonTextColor);
        menuBar.setBorder(BorderFactory.createEmptyBorder());
        menuBar.setBorder(BorderFactory.createEmptyBorder(4, 5, 0, 5));

        // Set the properties of the menu items
        file.setBackground(lightButtonTextColor);
        file.setForeground(darkButtonTextColor);
        file.setBorder(BorderFactory.createEmptyBorder());
        file.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        file.setFont(buttonsFont);

        fileItem.setBackground(lightButtonTextColor);
        fileItem.setForeground(darkButtonTextColor);
        fileItem.setFont(buttonsFont);

        help.setBackground(lightButtonTextColor);
        help.setForeground(darkButtonTextColor);
        help.setBorder(BorderFactory.createEmptyBorder());
        help.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        help.setFont(buttonsFont);

        helpItem.setBackground(lightButtonTextColor);
        helpItem.setForeground(darkButtonTextColor);
        helpItem.setFont(buttonsFont);

        about.setBackground(lightButtonTextColor);
        about.setForeground(darkButtonTextColor);
        about.setBorder(BorderFactory.createEmptyBorder());
        about.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        about.setFont(buttonsFont);

        aboutItem.setBackground(lightButtonTextColor);
        aboutItem.setForeground(darkButtonTextColor);
        aboutItem.setFont(buttonsFont);

        // Set the properties of the titleLabel
        titleLabel.setBounds(55, 30, 340, 30);
        titleLabel.setForeground(lightButtonTextColor);
        titleLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        titleLabel.setHorizontalAlignment(titleLabel.CENTER);

        // Set the properties of the buttons
        browseButton.setBounds(55, 90, 100, 25);
        browseButton.setBackground(browseButtonBgColor);
        browseButton.setForeground(darkButtonTextColor);
        browseButton.setFont(buttonsFont);
        browseButton.setFocusable(false);
        browseButton.setBorder(BorderFactory.createEmptyBorder()); 
        browseButton.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); 
        browseButton.setHorizontalAlignment(browseButton.CENTER); 
        browseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        browseKeyButton.setBounds(55, 150, 100, 25);
        browseKeyButton.setBackground(browseButtonBgColor);
        browseKeyButton.setForeground(darkButtonTextColor);
        browseKeyButton.setFont(buttonsFont);
        browseKeyButton.setFocusable(false);
        browseKeyButton.setBorder(BorderFactory.createEmptyBorder()); 
        browseKeyButton.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); 
        browseKeyButton.setHorizontalAlignment(browseButton.CENTER); 
        browseKeyButton.setVisible(false);
        browseKeyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        encryptButton.setBounds(55, 200, 102, 30);
        encryptButton.setBackground(encryptButtonBgColor);
        encryptButton.setForeground(darkButtonTextColor);
        encryptButton.setFont(buttonsFont);
        encryptButton.setFocusable(false);
        encryptButton.setBorder(BorderFactory.createEmptyBorder()); 
        encryptButton.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0)); 
        encryptButton.setHorizontalAlignment(encryptButton.CENTER); 
        encryptButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        decryptButton.setBounds(188, 200, 100, 30);
        decryptButton.setBackground(decryptButtonBgColor);
        decryptButton.setForeground(darkButtonTextColor);
        decryptButton.setFont(buttonsFont);
        decryptButton.setFocusable(false);
        decryptButton.setBorder(BorderFactory.createEmptyBorder()); 
        decryptButton.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0)); 
        decryptButton.setHorizontalAlignment(decryptButton.CENTER); 
        decryptButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cancelButton.setBounds(315, 200, 100, 30);
        cancelButton.setBackground(cancelButtonBgColor);
        cancelButton.setForeground(darkButtonTextColor);
        cancelButton.setFont(buttonsFont);
        cancelButton.setFocusable(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder()); 
        cancelButton.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0)); 
        cancelButton.setHorizontalAlignment(cancelButton.CENTER); 
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Set the properties of the text fields
        fileField.setBounds(165, 90, 250, 25);
        fileField.setBackground(lightButtonTextColor);
        fileField.setForeground(darkButtonTextColor);
        fileField.setFont(buttonsFont);
        fileField.setEditable(false);
        fileField.setBorder(BorderFactory.createEmptyBorder());
        fileField.setBorder(BorderFactory.createEmptyBorder(5, 10, 2, 10));
        fileField.setHorizontalAlignment(fileField.LEFT);

        keyField.setBounds(165, 120, 130, 25);
        keyField.setBackground(lightButtonTextColor);
        keyField.setForeground(darkButtonTextColor);
        keyField.setFont(buttonsFont);
        keyField.setEditable(true);
        keyField.setBorder(BorderFactory.createEmptyBorder());
        keyField.setBorder(BorderFactory.createEmptyBorder(5, 10, 2, 10));
        keyField.setHorizontalAlignment(keyField.LEFT);

        keyFileField.setBounds(165, 150, 250, 25);
        keyFileField.setBackground(lightButtonTextColor);
        keyFileField.setForeground(darkButtonTextColor);
        keyFileField.setFont(buttonsFont);
        keyFileField.setEditable(false);
        keyFileField.setBorder(BorderFactory.createEmptyBorder());
        keyFileField.setBorder(BorderFactory.createEmptyBorder(5, 10, 2, 10));
        keyFileField.setHorizontalAlignment(keyFileField.LEFT);
        keyFileField.setVisible(false);

        // Set the properties of the labels
        keyLabel.setBounds(55, 125, 100, 25);
        keyLabel.setForeground(lightButtonTextColor);
        keyLabel.setFont(buttonsFont);
        keyLabel.setBorder(BorderFactory.createEmptyBorder()); 
        keyLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 
        keyLabel.setHorizontalAlignment(keyLabel.RIGHT);

        keyFileLabel.setBounds(305, 120, 100, 25);
        keyFileLabel.setForeground(lightButtonTextColor);
        keyFileLabel.setFont(buttonsFont);
        keyFileLabel.setBorder(BorderFactory.createEmptyBorder()); 
        keyFileLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 
        keyFileLabel.setHorizontalAlignment(keyLabel.LEFT);

        // Set the properties of the keyFileCheckBox
        keyFileCheckBox.setBounds(398, 120, 20, 25);
        keyFileCheckBox.setBackground(backgroundColor);
        keyFileCheckBox.setForeground(darkButtonTextColor);
        keyFileCheckBox.setFont(buttonsFont);
        keyFileCheckBox.setFocusable(false);
        keyFileCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

    // Method to pad the key with spaces if its length is less than 16
    private String padKey(String key) {
        if (key.length() < 16) { // pad the key with spaces if its length is less than 16
            int paddingLength = 16 - key.length();
            StringBuilder paddedKey = new StringBuilder(key);
            for (int i = 0; i < paddingLength; i++) {
            paddedKey.append(" "); // add a space to the key
            }
            key = paddedKey.toString(); // convert the StringBuilder to a String
        }
        return key; // return the padded key
    }

    // Method to encrypt the file
    private void encryptFile(){
        // Get the key and file path from the text fields
        String key = keyField.getText();
        String file = fileField.getText();

        key = padKey(key); // Pad the key if its length is less than 16

        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES"); // Create a secret key using the provided key
            Cipher cipher = Cipher.getInstance("AES"); // Create a cipher instance for AES encryption
            cipher.init(Cipher.ENCRYPT_MODE, secretKey); // Initialize the cipher in encryption mode with the secret key
            byte[] encryptedFile = Files.readAllBytes(Paths.get(file));  // Read all bytes from the file
            byte[] encrypted = cipher.doFinal(encryptedFile); // Encrypt the file using the cipher

            Path filePath = Paths.get(file); // Get the file name from the file path
            String fileName = filePath.getFileName().toString(); // Convert the file path to a string

            try {
                // Create a folder to save encrypted files
                String folder = file + "_encrypted";
                Files.createDirectories(Paths.get(folder));
                String encryptedString = folder + "/" + fileName + ".tws"; // Create a file path for the encrypted file
                Files.write(Paths.get(encryptedString), encrypted, StandardOpenOption.CREATE); // Write the encrypted bytes to the encrypted file
                String keyFile = folder + "/" + fileName + ".key"; // Create a file path for the key file

                // Write the key bytes to the key file
                Files.write(Paths.get(keyFile), key.getBytes(), StandardOpenOption.CREATE);

                // Show a success message with the path of the encrypted file
                JOptionPane.showMessageDialog(null, "File Encrypted Successfully!\nEncrypted file saved as:\n" + encryptedString, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                // Show an error message if an exception occurs during file creation or writing
                JOptionPane.showMessageDialog(null, "An error occurred while creating or writing the file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch(Exception e){
            // Show an error message if an exception occurs during encryption
            JOptionPane.showMessageDialog(null, "An error occurred while encrypting the file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to decrypt the file
    private void decryptFile(){
        // Get the key and file path from the text fields
        String key = null;
        String file = fileField.getText();

        // Check if the key file checkbox is selected
        if (keyFileCheckBox.isSelected()){
            String keyFile = keyFileField.getText();
            try {
                key = new String(Files.readAllBytes(Paths.get(keyFile))); // Read the key from the key file
                } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An error occurred while reading the key file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            key = keyField.getText(); // Use the key from the key field
        }

        key = padKey(key); // Pad the key if its length is less than 16

        try{
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES"); // Create a secret key from the key bytes
            Cipher cipher = Cipher.getInstance("AES"); // Create a cipher instance for AES encryption
            cipher.init(Cipher.DECRYPT_MODE, secretKey); // Initialize the cipher in decryption mode with the secret key
            byte[] encryptedFile = Files.readAllBytes(Paths.get(file)); // Read the encrypted file bytes
            byte[] decrypted = cipher.doFinal(encryptedFile); // Decrypt the file bytes

            try {
                // Remove the .tws extension from the file name
                String decryptedString = file.replace(".tws", "");
                // Write the decrypted bytes to a new file
                Files.write(Paths.get(decryptedString), decrypted, StandardOpenOption.CREATE);

                // Show a success message with the path of the decrypted file
                JOptionPane.showMessageDialog(null, "File Decrypted Successfully!\nDecrypted file saved as:\n" + decryptedString, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "An error occurred while creating the folder or writing the decrypted file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, "An error occurred while decrypting the file", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    // Main method
    public static void main(String[] args) {
        new FileSecurityApp();
    }
}