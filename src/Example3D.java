import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.universe.SimpleUniverse;


import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Light;
import javax.media.j3d.PointLight;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleArray;
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

        // createPyramid our universe object.
        universe = new Universe();

        // initialise the solar entities.
        universe.initSolarEntities();

    // =================================================================================================================
    // ||                                             Lighting                                                        ||
    // =================================================================================================================

        // Fetch the sun and moon so we can add lighting.
        Star sun = (Star) universe.getSolarBodyByName("sun");
        Satellite moon = (Satellite) universe.getSolarBodyByName("moon");

        // saturn ring
        // way too lazy to do other rings.
        Ring ring = new Ring(1f,.1f,"saturnRings");


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

            // add rings if planet is saturn
            if(planet.getBodyName().equalsIgnoreCase("saturn")){
                planet.getAxisTransformGroup().addChild(ring.getShape());
            }
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

        mainTransformGroup.addChild(createPyramid());

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


    /**
     * Creates a pink pyramid who's orbit line will intersect with the sun.
     * @return
     */

    private BranchGroup createPyramid(){

        BranchGroup root = new BranchGroup();

        // The translation/scaling for the pyramid
        Transform3D translation = new Transform3D();
        translation.setScale(new Vector3d( .75f,.75f,.75f));
        translation.setTranslation(new Vector3d(0,0,12));
        TransformGroup translateGroup = new TransformGroup(translation);

        // an orbit for the pyramid, which will collide with the sun.
        Transform3D orbit = new Transform3D();
        orbit.setTranslation(new Vector3d(12,0,0));
        orbit.mul(new Transform3D());
        TransformGroup orbitTranslation  = new TransformGroup(orbit);

        // rotating around a point.
        TransformGroup rotationCenter = new TransformGroup();
        rotationCenter.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        RotationInterpolator orbitRotator = new RotationInterpolator(new Alpha(-1, 30000),
                rotationCenter, new Transform3D(), 0.0f, (float) Math.PI * 2);
        orbitRotator.setSchedulingBounds(new BoundingSphere(new Point3d(0,0,0),1000));
        rotationCenter.addChild(orbitRotator);


        // create our pyramid and apply a purple color to it.
        GeometryArray geometry = getArray();
        Appearance purpleApp = new Appearance();
        Color3f color3f = new Color3f(Color.PINK);
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(color3f);
        purpleApp.setColoringAttributes(ca);
        Shape3D customShape = new Shape3D(geometry,purpleApp);

        // add to the shape group
        translateGroup.addChild(customShape);

        root.addChild(orbitTranslation);
        orbitTranslation.addChild(rotationCenter);
        rotationCenter.addChild(translateGroup);


        return root;
    }


    // creates a GemoetryArray holding the co-ordinates for six triangles which compose to form a pyramid.
    private GeometryArray getArray() {
        TriangleArray pyramidArray = new TriangleArray(18,TriangleArray.COORDINATES);
        Point3f north = new Point3f(0.0f, 0.0f, -1.0f); // north
        Point3f east = new Point3f(1.0f, 0.0f, 0.0f); // east
        Point3f wwest = new Point3f(-1.0f, 0.0f, 0.0f); // west
        Point3f south = new Point3f(0.0f, 0.0f, 1.0f); // south
        Point3f top = new Point3f(0.0f, 0.9f, 0.0f); // top

        // add the co-ordinates
        pyramidArray.setCoordinate(0, east);
        pyramidArray.setCoordinate(1, top);
        pyramidArray.setCoordinate(2, south);


        pyramidArray.setCoordinate(3, south);
        pyramidArray.setCoordinate(4, top);
        pyramidArray.setCoordinate(5, wwest);

        pyramidArray.setCoordinate(6, wwest);
        pyramidArray.setCoordinate(7, top);
        pyramidArray.setCoordinate(8, north);

        pyramidArray.setCoordinate(9, north);
        pyramidArray.setCoordinate(10, top);
        pyramidArray.setCoordinate(11, east);

        pyramidArray.setCoordinate(12, north);
        pyramidArray.setCoordinate(13, south);
        pyramidArray.setCoordinate(14, wwest);

        pyramidArray.setCoordinate(15, wwest);
        pyramidArray.setCoordinate(16, north);
        pyramidArray.setCoordinate(17, south);

        GeometryInfo geometryInfo = new GeometryInfo(pyramidArray);
        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(geometryInfo);
        return geometryInfo.getGeometryArray();
    }

    // TODO: add some collision stuff


}
