import org.jdom2.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

public class ClassFrame extends JFrame {
    private final static int windowWidth = 800;
    private final static int windowHeight = 600;
    private final static String windowTitle = "Edit class";

    private final JLabel entryLabel = new JLabel("Element name:");
    private final JLabel entryNotFoundLabel = new JLabel("Entry not found in Wikipedia");
    private JLabel entryName;
    private List<JLabel> countryLabels;
    private List<JTextField> translationTextFields;

    private List<JPanel> panels;
    private JPanel mainPanel = new JPanel();
    private JPanel northPanel = new JPanel();
    private List<JButton> scrapButtons;
    private JFrame contentFrame;
    private Element owlClass;


    private final JButton saveChangesButton = new JButton("Save changes");

    public ClassFrame(Element owlClass) {
        this.owlClass = owlClass;
        entryNotFoundLabel.setVisible(false);
        this.setTitle(windowTitle);
        this.setSize(windowWidth, windowHeight);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        entryName = new JLabel(OntologyXMLParser.getClassName(owlClass));
        northPanel.add(entryLabel);
        northPanel.add(entryName);
        countryLabels = new LinkedList<>();
        translationTextFields = new LinkedList<>();
        panels = new LinkedList<>();
        scrapButtons = new LinkedList<>();
        List<OWLClassLabel> owlClassLabels = OntologyXMLParser.getOWLClassLabels(owlClass);

        for (String country : OntologyXMLParser.countries) {

            JLabel label = new JLabel(country);
            countryLabels.add(label);
            mainPanel.add(label);

            JTextField translation = new JTextField(OntologyXMLParser.getClassLabel(country, owlClassLabels));
            translation.setColumns(30);
            translationTextFields.add(translation);
            mainPanel.add(translation);
            JButton scrapButton = new JButton("Scrap");
            scrapButtons.add(scrapButton);
            mainPanel.add(scrapButton);
            scrapButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {

                    if (contentFrame != null) {
                        contentFrame.dispatchEvent(new WindowEvent(contentFrame, WindowEvent.WINDOW_CLOSING));
                    }

                    String entry = translation.getText();
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

        this.add(northPanel, BorderLayout.NORTH);
        this.add(mainPanel,BorderLayout.CENTER);
        this.add(entryNotFoundLabel, BorderLayout.WEST);
        this.add(saveChangesButton, BorderLayout.SOUTH);


        setSaveChangesButtonListener();
        this.setVisible(true);
    }

    private void setSaveChangesButtonListener() {
        saveChangesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {

                if (!translationTextFields.get(0).getText().equals("")) {
                    OntologyXMLParser.setNewValue(owlClass, countryLabels.get(0).getText(), translationTextFields.get(0).getText());
                }
                if (!translationTextFields.get(1).getText().equals("")) {
                    OntologyXMLParser.setNewValue(owlClass, countryLabels.get(1).getText(), translationTextFields.get(1).getText());
                }
            }
        });
    }




}
