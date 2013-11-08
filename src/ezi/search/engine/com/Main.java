package ezi.search.engine.com;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main implements ActionListener {

	private static JFrame frame;
	private JTextField entry;
	private JTextArea display;
	private JList<String> resultDisplay;
	private DefaultListModel<String> listModel;
	private Font font;
	private JButton searchButton, loadData, documentsButton, keywordsButton,
			docsAfterStemmingButton, queryExtensionButton;
	private JCheckBox stemmingBox, docsTwoBox;
	private JFileChooser fc;
	private File documents, keywords;

	private FileProcessor fileProcessor;
	private CosCalculator cosCalculator;

	private boolean stemmingFlag, docsTwoFlag;

	private Color panelsColor;

	private Main() {

		font = new Font("Verdana", Font.PLAIN, 12);
		display = new JTextArea(30, 50);
		display.setFont(font);
		display.setEditable(false);
		listModel = new DefaultListModel<String>();
		resultDisplay = new JList<String>();
		display.add(resultDisplay);
		searchButton = createButton("Search", 70, 40);
		queryExtensionButton = createButton("QueryExtension", 120, 40);
		loadData = createButton("Load Data", 100, 40);
		documentsButton = createButton("Documents", 140, 30);
		keywordsButton = createButton("Keywords", 140, 30);
		docsAfterStemmingButton = createButton("Docs After Stemm", 200, 30);
		stemmingBox = new JCheckBox("Stemming");
		stemmingBox.setSelected(true);
		docsTwoBox = new JCheckBox("Documents 2");
		docsTwoBox.setSelected(false);

		searchButton.addActionListener(this);
		loadData.addActionListener(this);
		documentsButton.addActionListener(this);
		keywordsButton.addActionListener(this);
		docsAfterStemmingButton.addActionListener(this);
		queryExtensionButton.addActionListener(this);
		stemmingBox.addActionListener(this);
		docsTwoBox.addActionListener(this);

		entry = new JTextField();
		entry.setPreferredSize(new Dimension(300, 24));
		stemmingFlag = true;
		docsTwoFlag = false;

		panelsColor = new Color(62, 70, 81);

	}

	private JButton createButton(String text, int width, int height) {
		final JButton button = new JButton(text);
		button.setPreferredSize(new Dimension(width, height));
		return button;
	}

	private JComponent createNorthPanel() {
		final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.setBackground(panelsColor);
		panel.add(entry);
		panel.add(searchButton);
		panel.add(queryExtensionButton);

		return panel;
	}

	private JComponent createCenterPanel() {
		JPanel middlePanel = new JPanel();
		middlePanel.setBackground(Color.WHITE);
		// resultDisplay.setFont(font);
		// resultDisplay.setFixedCellHeight(50);

		JScrollPane scroll = new JScrollPane(display,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// scroll.setMinimumSize(new Dimension(640, 480));
		// scroll.setPreferredSize(new Dimension(640, 480));
		// scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		middlePanel.add(scroll);

		return middlePanel;
	}

	private JComponent createSouthPanel() {
		final JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		final JPanel goPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		final JPanel stemmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		buttonPanel.setBackground(panelsColor);
		goPanel.setBackground(panelsColor);
		stemmPanel.setBackground(panelsColor);

		buttonPanel.add(documentsButton);
		buttonPanel.add(keywordsButton);
		goPanel.add(stemmingBox);
		goPanel.add(docsTwoBox);
		goPanel.add(loadData);
		stemmPanel.add(docsAfterStemmingButton);

		southPanel.add(buttonPanel);
		southPanel.add(goPanel);
		southPanel.add(stemmPanel);

		return southPanel;
	}

	private JComponent createMainPanel() {
		final JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(createNorthPanel(), BorderLayout.NORTH);
		mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
		mainPanel.add(createSouthPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == searchButton) {
			display.setText("");
			fileProcessor.processQuery(entry.getText());
			String query;

			if (stemmingFlag) {
				query = fileProcessor.getQueryStemmed();
			} else {
				query = fileProcessor.getQueryTokenized();
			}
			Map<String, Double> result = cosCalculator.calculateCos(query);
			for (Entry<String, Double> entry : result.entrySet()) {
				String key = entry.getKey();
				Double value = entry.getValue();
				display.setText(display.getText() + value + "\t" + " " + key
						+ "\n");
				// listModel.addElement(value + "<br/>" + key);

			}
			resultDisplay.setModel(listModel);
		} else if (e.getSource() == queryExtensionButton) {
			display.setText("");
			String autoQuery = fileProcessor.autocompleteQuery(entry.getText());
			fileProcessor.processQuery(autoQuery);
			String query;

			if (stemmingFlag) {
				query = fileProcessor.getQueryStemmed();
			} else {
				query = fileProcessor.getQueryTokenized();
			}
			Map<String, Double> result = cosCalculator.calculateCos(query);
			for (Entry<String, Double> entry : result.entrySet()) {
				String key = entry.getKey();
				Double value = entry.getValue();
				display.setText(display.getText() + value + "\t" + " " + key
						+ "\n");
				// listModel.addElement(value + "<br/>" + key);

			}
		} else if (e.getSource() == documentsButton) {
			fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				documents = fc.getSelectedFile();
				documentsButton.setText(documents.getName());
			}
		} else if (e.getSource() == keywordsButton) {
			fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				keywords = fc.getSelectedFile();
				keywordsButton.setText(keywords.getName());
			}
		} else if (e.getSource() == loadData) {
			fileProcessor = new FileProcessor();
			fileProcessor.processFiles(documents, keywords, docsTwoFlag);
			if (stemmingFlag) {
				cosCalculator = new CosCalculator(
						fileProcessor.getDocsBeforeStemming(),
						fileProcessor.getDocsAfterStemming(),
						fileProcessor.getKeywordsAfterStemming());
			} else {
				cosCalculator = new CosCalculator(
						fileProcessor.getDocsBeforeStemming(),
						fileProcessor.getDocsAfterTokenization(),
						fileProcessor.getKeywordsBeforeStemming());
			}
			cosCalculator.calculateTfIdf();
		} else if (e.getSource() == docsAfterStemmingButton) {
			display.setText("");
			for (String current : fileProcessor.getDocsAfterStemming()) {
				display.setText(display.getText() + current + "\n\n" + "");
			}
		} else if (e.getSource() == stemmingBox) {
			if (stemmingBox.isSelected()) {
				stemmingFlag = true;
			} else {
				stemmingFlag = false;
			}
		} else if (e.getSource() == docsTwoBox) {
			if (docsTwoBox.isSelected()) {
				docsTwoFlag = true;
			} else {
				docsTwoFlag = false;
			}
		}
	}

	private static void createAndShowGUI() {
		final Main instance = new Main();

		frame = new JFrame(Main.class.getName());
		frame.getContentPane().add(instance.createMainPanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 730);
		frame.setVisible(true);
		frame.setTitle("EZI Search Engine");

	}

	/**
	 * Command line entry point.
	 * 
	 * @param args
	 *            Command line arguments.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
