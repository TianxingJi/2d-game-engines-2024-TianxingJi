package engine.systems;

import engine.gameobjects.GameObject;
import engine.gameobjects.TransformComponent;
import engine.gameobjects.PhysicsComponent;
import engine.support.Vec2d;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class SaveAndLoadSystem {

    private static final Logger logger = Logger.getLogger(SaveAndLoadSystem.class.getName());
    private double rescaleOriginal;
    private double rescaleNew;

    public SaveAndLoadSystem(double rescaleOriginal){
        this.rescaleOriginal = rescaleOriginal;
    }

    // Save the game state to an XML file
    public void saveGame(String filePath, List<GameObject> gameObjects) {
        try {
            // Create XML document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Root element
            Element rootElement = doc.createElement("GameState");
            doc.appendChild(rootElement);

            // Save each game object's TransformComponent position
            for (GameObject gameObject : gameObjects) {
                Element objectElement = doc.createElement("GameObject");

                // Save object ID
                objectElement.setAttribute("id", gameObject.getId());

                // Retrieve the TransformComponent to get position
                TransformComponent transform = gameObject.getComponent(TransformComponent.class);
                if (transform != null) {
                    // Save position
                    Element positionElement = doc.createElement("Position");
                    Vec2d pos = transform.getPosition();
                    positionElement.setAttribute("x", String.valueOf(pos.x));
                    positionElement.setAttribute("y", String.valueOf(pos.y));
                    objectElement.appendChild(positionElement);
                }

                rootElement.appendChild(objectElement);
            }

            // Write the content into an XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

            logger.info("Game saved to " + filePath);

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    // Load the game state from an XML file

    // Load the game state from an XML file
    public void loadGame(String filePath, List<GameObject> gameObjects, double rescaleNew) {
        try {
            // Parse XML file
            this.rescaleNew = rescaleNew;
            double rescaleChange = rescaleNew / rescaleOriginal;
            File xmlFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Load each GameObject data from XML
            NodeList objectList = doc.getElementsByTagName("GameObject");
            for (int i = 0; i < objectList.getLength(); i++) {
                Element objectElement = (Element) objectList.item(i);

                // Get object ID from XML
                String id = objectElement.getAttribute("id");

                // Load position from TransformComponent
                Vec2d position = null;
                Element positionElement = (Element) objectElement.getElementsByTagName("Position").item(0);
                if (positionElement != null) {
                    double x = Double.parseDouble(positionElement.getAttribute("x"));
                    double y = Double.parseDouble(positionElement.getAttribute("y"));
                    position = new Vec2d(x * rescaleChange, y * rescaleChange);
                }

                // Find the GameObject in the list and update its TransformComponent position
                for (GameObject gameObject : gameObjects) {
                    if (gameObject.getId().equals(id)) {
                        TransformComponent transform = gameObject.getComponent(TransformComponent.class);
                        if (transform != null && position != null) {
                            transform.setPosition(position); // Update position
                        }

                        // If has physics component
                        PhysicsComponent physics = gameObject.getComponent(PhysicsComponent.class);
                        if (physics != null) {
                            physics.setVelocity(new Vec2d(0, 0)); // set the speed to zero
                        }

                        break;
                    }
                }
            }

            logger.info("Game loaded from " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
