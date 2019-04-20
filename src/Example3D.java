import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;


import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Light;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.List;

public class Example3D extends JFrame {

    private Universe universe;

    public static void main(String[] args) {
        new Example3D();
    }

    public Example3D() {
        // Frame window initialisations.
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        cp.add("Center", c);
        BranchGroup scene = initSceneGraph();
        SimpleUniverse u = new SimpleUniverse(c);
        u.getViewingPlatform().setNominalViewingTransform();
        u.addBranchGraph(scene);

        TransformGroup cameraTG = u.getViewingPlatform().getViewPlatformTransform();
        Vector3f translate = new Vector3f();
        Transform3D T3D = new Transform3D();
        translate.set(7.0f, 2.0f, 15.0f);
        T3D.rotY(Math.PI / 9);
        T3D.setTranslation(translate);
        cameraTG.setTransform(T3D);
        setSize(512, 512);
        setVisible(true);
    }

    public BranchGroup initSceneGraph() {
        // Create scenegraph here.
        BranchGroup sceneRoot = new BranchGroup();

        // Create the main transform group
        TransformGroup mainTransformGroup = new TransformGroup();

        //add the relationship.
        sceneRoot.addChild(mainTransformGroup);

        // set the different capabilities.
        mainTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        mainTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        mainTransformGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        mainTransformGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);

        // create our universe object.
        universe = new Universe();

        // initialise the solar entities.
        universe.initSolarEntities();

    // =================================================================================================================
    // ||                                             Lighting                                                        ||
    // =================================================================================================================

        // Fetch the sun and moon so we can add lighting.
        Star sun = (Star) universe.getSolarBodyByName("sun");
        Satellite moon = (Satellite) universe.getSolarBodyByName("moon");

        // Lighting from the sun, this will be #e0baa8.
        mainTransformGroup.addChild(getLighting("sun"));

        // add the Lighting from the moon, this will be set to white
        moon.getAxisTransformGroup().addChild(getLighting("moon"));


        // light to illuminate the scene as i am blind.
        PointLight spotLight = new PointLight();
        spotLight.setColor(new Color3f(Color.WHITE));
        spotLight.setPosition(new Point3f(12.0f, 12.0f, 12.0f));
        spotLight.setInfluencingBounds(new BoundingSphere(new Point3d(12.0f, 12.0f, 12.0f), 22.0f));

        // add to the spotlight to the tg.
        mainTransformGroup.addChild(spotLight);


    // =================================================================================================================
    // ||                                             Node Connections                                                ||
    // =================================================================================================================

        // Transform group to handle the opposite orbit of Venus and Uranus.
        Transform3D retrogradeRotation = new Transform3D();
        retrogradeRotation.rotZ(Math.PI);
        TransformGroup retrogradeTransformGroup = new TransformGroup(retrogradeRotation);

        // add to the main group.
        mainTransformGroup.addChild(retrogradeTransformGroup);

        // fetch the retrograde planets.
        Planet venus = (Planet) universe.getSolarBodyByName("venus");
        Planet uranus = (Planet) universe.getSolarBodyByName("uranus");

        // add their orbits to the retrograde transform group.
        retrogradeTransformGroup.addChild(uranus.getOrbitTransformGroup());
        retrogradeTransformGroup.addChild(venus.getOrbitTransformGroup());


        mainTransformGroup.addChild(sun.getAxisTransformGroup());
        sun.getAxisTransformGroup().addChild(sun.getSphere());

        // fetch the planets
        List<SolarBody> planets = universe.getSolarByType(Planet.class);

        // remove uranus and venus for now as we have already added their orbits to the retrograde transform group.
        planets.remove(uranus);
        planets.remove(venus);

        // add each planet orbit to the mainTransform group
        planets.forEach(b -> mainTransformGroup.addChild(b.getOrbitTransformGroup()));

        // add the sun to the transform group as well.
        mainTransformGroup.addChild(sun.getOrbitTransformGroup());

        // add the moon,uranus and venus to the planet list so we can operate on them all at once.
        planets.add(moon);
        planets.add(uranus);
        planets.add(venus);


        // add the connections for each transform group
        planets.forEach(planet -> {
            planet.getOrbitTransformGroup().addChild(planet.getMainTransformGroup());
            planet.getMainTransformGroup().addChild(planet.getAxisTransformGroup());
            planet.getAxisTransformGroup().addChild(planet.getSphere());
        });

        // moon orbit around the earth.
        Planet earth = (Planet) universe.getSolarBodyByName("earth");
        earth.getAxisTransformGroup().addChild(moon.getOrbitTransformGroup());

        // add the rotations to the transform groups.
        planets.forEach(planet -> {
            planet.getOrbitTransformGroup().addChild(planet.getOrbitRotator());
            planet.getAxisTransformGroup().addChild(planet.getAxisRotator());
        });

        sun.getAxisTransformGroup().addChild(sun.getAxisRotator());
    // =================================================================================================================
    // ||                                              Mouse Behaviours                                               ||
    // =================================================================================================================
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000);

        // mouse behaviors
        // Create the rotate behavior node
        MouseRotate behavior = new MouseRotate();
        behavior.setTransformGroup(mainTransformGroup);
        sceneRoot.addChild(behavior);
        behavior.setSchedulingBounds(bounds);

        // Create the zoom behavior node
        MouseWheelZoom behavior2 = new MouseWheelZoom();
        behavior2.setTransformGroup(mainTransformGroup);
        sceneRoot.addChild(behavior2);
        behavior2.setSchedulingBounds(bounds);

        // Create the translate behavior node
        MouseTranslate behavior3 = new MouseTranslate();
        behavior3.setTransformGroup(mainTransformGroup);
        sceneRoot.addChild(behavior3);
        behavior3.setSchedulingBounds(bounds);

        sceneRoot.compile();
        return sceneRoot;

    }


    /**
     * Produce a pointlight node (light that is reflected off the other nodes) based on the _name based in.
     *
     * @param _name - the name of the node to generate the lighting for.
     * @return - Orange Pointlight reflected off the planets from the sun, white reflected off the earth from the moons light
     */
    private PointLight getLighting(String _name) {
        SolarBody namedBody = universe.getSolarBodyByName(_name);
        PointLight light = new PointLight();
        if (namedBody instanceof Star) {
            // sun lighting
            light.setColor(new Color3f(new Color(224, 186, 168)));
            light.setPosition(0.0f, 0.0f, 0.0f);
            light.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000));
            light.setCapability(Light.ALLOW_COLOR_WRITE);
        } else if (namedBody instanceof Satellite) {
            // moon lighting
            light.setColor(new Color3f(Color.WHITE));
            Vector3f position = new Vector3f(namedBody.getPosition());
            Point3d bounds = new Point3d(position);
            light.setPosition(position.getX(), position.getY(), position.getZ() + .8f);
            light.setInfluencingBounds(new BoundingSphere(bounds, namedBody.getRadius() + 3f));
        }
        return light;
    }


}
