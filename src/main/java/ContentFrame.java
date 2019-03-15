import javax.swing.*;
import java.awt.*;

public class ContentFrame extends JFrame {
    private WikipediaPageData wikipediaPageData;

    private final static int windowWidth = 1200;
    private final static int windowHeight = 800;
    private final static String windowTitle = "Content";

    private final JLabel entryTitleLabel = new JLabel("Entry title:");
    private JLabel entryTitleText;
    private final JPanel entryTitlePanel = new JPanel();

    private final JPanel entryContentPanel = new JPanel();
    private final JLabel contentLabel = new JLabel("Entry content:");
    private JTextArea contentArea;


    public ContentFrame(WikipediaPageData wikipediaPageData) {
        this.wikipediaPageData = wikipediaPageData;
        entryTitleText = new JLabel(wikipediaPageData.getTitle());
        contentArea = new JTextArea(wikipediaPageData.getContent());
        contentArea.setColumns(80);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setTitle(windowTitle);
        this.setSize(windowWidth, windowHeight);
        this.setLocationRelativeTo(null);
        createComponents();
    }

    private void createComponents() {
        entryTitlePanel.add(entryTitleLabel);
        entryTitlePanel.add(entryTitleText);

        entryContentPanel.add(contentLabel);
        entryContentPanel.add(contentArea);


        this.add(entryTitlePanel, BorderLayout.NORTH);
        this.add(entryContentPanel, BorderLayout.CENTER);

        //make sure the JFrame is visible
        this.setVisible(true);

    }


}
