import org.jdom2.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.List;

public class ClassTableFrame extends JFrame {

    private final static int windowWidth = 1200;
    private final static int windowHeight = 800;
    private final static String windowTitle = "Ontology List";

    private JTable table;
    List<String> classLabels;
    private JScrollPane scrollPane;
    private DefaultTableModel model;
    private List<Element> classes;
    private ClassFrame classFrame;


    public ClassTableFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(windowTitle);
        this.setSize(windowWidth, windowHeight);
        this.setLocationRelativeTo(null);

        table = new JTable();

        model = new DefaultTableModel(
                new String[] {
                        "Name", "pl", "en"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        table.setModel(model);


        classes = OntologyXMLParser.getAllOWLClasses();
        for (Element owlClass : classes) {
            List<OWLClassLabel> owlClassLabels = OntologyXMLParser.getOWLClassLabels(owlClass);
            model.addRow(
                    new Object[] {
                            OntologyXMLParser.getClassName(owlClass), OntologyXMLParser.getClassLabel(OntologyXMLParser.countries[0], owlClassLabels), OntologyXMLParser.getClassLabel(OntologyXMLParser.countries[1], owlClassLabels)
                    }
            );
        }

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    if (classFrame != null) {
                        classFrame.dispatchEvent(new WindowEvent(classFrame, WindowEvent.WINDOW_CLOSING));
                    }

                   Element owlClass = classes.get(row);
                   classFrame = new ClassFrame(owlClass);
                }

            }
            public void onceClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 1) {
                    model.fireTableDataChanged();
                }
            }
        });

        scrollPane = new JScrollPane(table);
        scrollPane.setViewportView(table);
        this.add(scrollPane);
        this.setVisible(true);
    }
}
