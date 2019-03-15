import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.List;


public class MainFrame extends JFrame {
    private final static int windowWidth = 300;
    private final static int windowHeight = 250;
    private final static String windowTitle = "Wikipedia entries scraper";

    private final JLabel entryNotFoundLabel = new JLabel("Entry not found in Wikipedia");
    private final JPanel countryPanel = new JPanel();
    private final JLabel countryLabel = new JLabel("Country:");
    private final JComboBox countriesComboBox = new JComboBox(OntologyXMLParser.countries);

    private final JPanel entryPanel = new JPanel();
    private final JLabel entryLabel = new JLabel("Entry:");
    private final JTextField entryField = new JTextField();

    private final JButton scrapButton = new JButton("Scrap Article Content");

    private JFrame contentFrame;


    public MainFrame() {
        //make sure the program exits when the frame closes
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(windowTitle);
        this.setSize(windowWidth, windowHeight);
        this.setLocationRelativeTo(null);
        entryNotFoundLabel.setVisible(false);
        createComponents();
    }

    private void createComponents() {
        countryPanel.add(countryLabel);
        countryPanel.add(countriesComboBox);

        entryField.setColumns(20);
        entryPanel.add(entryLabel);
        entryPanel.add(entryField);
        entryPanel.add(entryNotFoundLabel);

        setScrapButtonActionListener();

        this.add(countryPanel, BorderLayout.NORTH);
        this.add(entryPanel, BorderLayout.CENTER);
        this.add(scrapButton, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    private void setScrapButtonActionListener() {
        scrapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {

                if (contentFrame != null) {
                    contentFrame.dispatchEvent(new WindowEvent(contentFrame, WindowEvent.WINDOW_CLOSING));
                }

                String country = countriesComboBox.getSelectedItem().toString();
                String entry = entryField.getText();
                if (entry.equals("")) {
                    return;
                }

                WikipediaClient.scrapArticleContent(country, WikipediaClient.entryToWikipediaFormat(entry));
                if (!WikipediaClient.hasPagesData()) {
                    entryNotFoundLabel.setVisible(true);
                    return;
                }
                entryNotFoundLabel.setVisible(false);
                List<WikipediaPageData> pageDataList = WikipediaClient.getPagesData();
                WikipediaPageData wikipediaPageData = pageDataList.iterator().next();

                contentFrame = new ContentFrame(wikipediaPageData);

            }
        });
    }


}
