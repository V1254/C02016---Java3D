import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Light;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Example3D extends JFrame {

    public static void main(String[] args) {
        new Example3D();
    }

    public Example3D(){
        // JFRame initialisations
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration() );
        cp.add("Center", c);
        BranchGroup scene = initSceneGraph();
        SimpleUniverse u = new SimpleUniverse(c);
        u.getViewingPlatform().setNominalViewingTransform();
        u.addBranchGraph(scene);

        // Create a viewing platform
        TransformGroup cameraTG = u.getViewingPlatform().
                getViewPlatformTransform();
        // Starting postion of the viewing platform
        Vector3f translate = new Vector3f();
        Transform3D T3D = new Transform3D();
        // Reposition the intial camera position
        translate.set(7.0f, 2.0f, 15.0f);
        T3D.rotY(Math.PI/9);
        T3D.setTranslation(translate);
        cameraTG.setTransform(T3D);
        setSize(512,512);
        setVisible(true);
    }

    public BranchGroup initSceneGraph(){
        // Create scenegraph here.
        BranchGroup objRoot = new BranchGroup();

        // Create the main transform group
        TransformGroup mainTG = new TransformGroup();
        mainTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        mainTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        mainTG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        mainTG.setCapability(BranchGroup.ALLOW_CHILDREN_READ);

        // create our objects
        Universe universe = new Universe();
        universe.initSolarEntities();

        List<SolarBody> bodies = universe.getBodies();

        /**
         * Lighting from the sun, this will be magenta.
         */
        PointLight sunLight = getLighting(bodies,"sun");

        // big boy sun
        Star sun = (Star) getSolarBodyByName(bodies,"sun");

        /**
         * Lighting from the moon, this will be set to white
         */
        Satellite moon = (Satellite) getSolarBodyByName(bodies,"moon");
        PointLight moonLight = getLighting(bodies,"moon");
        moon.getAxisTransformGroup().addChild(moonLight);



        // light to illuminate the scene
        PointLight spotLight = new PointLight();
        spotLight.setColor(new Color3f(Color.WHITE));
        spotLight.setPosition(new Point3f(12.0f, 12.0f, 12.0f));
        spotLight.setInfluencingBounds(new BoundingSphere(new Point3d(12.0f, 12.0f, 12.0f), 20.0f));
        mainTG.addChild(spotLight);


        Transform3D retrogradeRotation = new Transform3D();
        retrogradeRotation.rotZ(Math.PI);
        TransformGroup retrogradeTG = new TransformGroup(retrogradeRotation);

        // Make edge relations between the scene graph nodes
        objRoot.addChild(mainTG);


        mainTG.addChild(retrogradeTG);

        // fetch venus and uranus to negative rotation
        Planet venus = (Planet) getSolarBodyByName(bodies,"venus");
        Planet uranus = (Planet) getSolarBodyByName(bodies,"uranus");

        retrogradeTG.addChild(uranus.getOrbitTransformGroup());
        retrogradeTG.addChild(venus.getOrbitTransformGroup());

        mainTG.addChild(sunLight);
        mainTG.addChild(sun.getAxisTransformGroup());
        sun.getAxisTransformGroup().addChild(sun.getSphere());

        // fetch the rest of the planets
        Planet mercury = (Planet) getSolarBodyByName(bodies,"mercury");
        Planet earth = (Planet) getSolarBodyByName(bodies,"earth");
        Planet mars = (Planet) getSolarBodyByName(bodies,"mars");
        Planet jupiter = (Planet) getSolarBodyByName(bodies,"jupiter");
        Planet saturn = (Planet) getSolarBodyByName(bodies,"saturn");
        Planet neptune = (Planet) getSolarBodyByName(bodies,"neptune");

        // add each body to the main transform group.
        Stream.of(mercury,earth,mars,jupiter,saturn,neptune,sun).forEach(b -> mainTG.addChild(b.getOrbitTransformGroup()));

//        // mercury connections
//        mercury.getOrbitTransformGroup().addChild(mercury.getMainTransformGroup());
//        mercury.getMainTransformGroup().addChild(mercury.getAxisTransformGroup());
//        mercury.getAxisTransformGroup().addChild(mercury.getSphere());
//
//        // venus connections
//        venus.getOrbitTransformGroup().addChild(venus.getMainTransformGroup());
//        venus.getMainTransformGroup().addChild(venus.getAxisTransformGroup());
//        venus.getAxisTransformGroup().addChild(venus.getSphere());
//
//        // earth + moon connections
//        earth.getOrbitTransformGroup().addChild(earth.getMainTransformGroup());
//        earth.getMainTransformGroup().addChild(earth.getAxisTransformGroup());
//        earth.getAxisTransformGroup().addChild(earth.getSphere());
//        earth.getAxisTransformGroup().addChild(moon.getOrbitTransformGroup());
//        moon.getOrbitTransformGroup().addChild(moon.getMainTransformGroup());
//        moon.getMainTransformGroup().addChild(moon.getAxisTransformGroup());
//        moon.getAxisTransformGroup().addChild(moon.getSphere());
//
//        // mars connections
//        mars.getOrbitTransformGroup().addChild(mars.getMainTransformGroup());
//        mars.getMainTransformGroup().addChild(mars.getAxisTransformGroup());
//        mars.getAxisTransformGroup().addChild(mars.getSphere());
//
//        // jupiter connections
//        jupiter.getOrbitTransformGroup().addChild(jupiter.getMainTransformGroup());
//        jupiter.getMainTransformGroup().addChild(jupiter.getAxisTransformGroup());
//        jupiter.getAxisTransformGroup().addChild(jupiter.getSphere());
//
//        // saturn connections
//        saturn.getOrbitTransformGroup().addChild(saturn.getMainTransformGroup());
//        saturn.getMainTransformGroup().addChild(saturn.getAxisTransformGroup());
//        saturn.getAxisTransformGroup().addChild(saturn.getSphere());
//
//        // uranus connections
//        uranus.getOrbitTransformGroup().addChild(uranus.getMainTransformGroup());
//        uranus.getMainTransformGroup().addChild(uranus.getAxisTransformGroup());
//        uranus.getAxisTransformGroup().addChild(uranus.getSphere());
//
//        // neptune connections
//        neptune.getOrbitTransformGroup().addChild(neptune.getMainTransformGroup());
//        neptune.getMainTransformGroup().addChild(neptune.getAxisTransformGroup());
//        neptune.getAxisTransformGroup().addChild(neptune.getSphere());

        // Connections between the transform groups.

        Supplier<Stream<SolarBody>> bodySupplier = () -> Stream.of(mercury,earth,moon,venus,mars,jupiter,saturn,uranus,neptune);

//        Stream<SolarBody> planetStream = Stream.of(mercury,earth,moon,venus,mars,jupiter,saturn,uranus,neptune);

        bodySupplier.get().forEach(b -> {
            b.getOrbitTransformGroup().addChild(b.getMainTransformGroup());
            b.getMainTransformGroup().addChild(b.getAxisTransformGroup());
            b.getAxisTransformGroup().addChild(b.getSphere());
        });

        // connection of earth to moon.
        earth.getAxisTransformGroup().addChild(moon.getOrbitTransformGroup());


        // orbits around the sun + (moon orbit around earth)
//        mercury.getOrbitTransformGroup().addChild(mercury.getOrbitRotator());
//        venus.getOrbitTransformGroup().addChild(venus.getOrbitRotator());
//        earth.getOrbitTransformGroup().addChild(earth.getOrbitRotator());
//        moon.getOrbitTransformGroup().addChild(moon.getOrbitRotator());
//        mars.getOrbitTransformGroup().addChild(mars.getOrbitRotator());
//        jupiter.getOrbitTransformGroup().addChild(jupiter.getOrbitRotator());
//        saturn.getOrbitTransformGroup().addChild(saturn.getOrbitRotator());
//        uranus.getOrbitTransformGroup().addChild(uranus.getOrbitRotator());
//        neptune.getOrbitTransformGroup().addChild(neptune.getOrbitRotator());

        bodySupplier.get().forEach(body -> body.getOrbitTransformGroup().addChild(body.getOrbitRotator()));


        // orbits around each axis.
//        sun.getAxisTransformGroup().addChild(sun.getAxisRotator());
//        mercury.getAxisTransformGroup().addChild(mercury.getAxisRotator());
//        venus.getAxisTransformGroup().addChild(venus.getAxisRotator());
//        earth.getAxisTransformGroup().addChild(earth.getAxisRotator());
//        moon.getAxisTransformGroup().addChild(moon.getAxisRotator());
//        mars.getAxisTransformGroup().addChild(mars.getAxisRotator());
//        jupiter.getAxisTransformGroup().addChild(jupiter.getAxisRotator());
//        saturn.getAxisTransformGroup().addChild(saturn.getAxisRotator());
//        uranus.getAxisTransformGroup().addChild(uranus.getAxisRotator());
//        neptune.getAxisTransformGroup().addChild(neptune.getAxisRotator());

        bodySupplier.get().forEach(body -> body.getAxisTransformGroup().addChild(body.getAxisRotator()));
        sun.getAxisTransformGroup().addChild(sun.getAxisRotator());


        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 1000);
        // mouse behaviors
        // Create the rotate behavior node
        MouseRotate behavior = new MouseRotate();
        behavior.setTransformGroup(mainTG);
        objRoot.addChild(behavior);
        behavior.setSchedulingBounds(bounds);

        // Create the zoom behavior node
        MouseWheelZoom behavior2 = new MouseWheelZoom();
        behavior2.setTransformGroup(mainTG);
        objRoot.addChild(behavior2);
        behavior2.setSchedulingBounds(bounds);

        // Create the translate behavior node
        MouseTranslate behavior3 = new MouseTranslate();
        behavior3.setTransformGroup(mainTG);
        objRoot.addChild(behavior3);
        behavior3.setSchedulingBounds(bounds);

        objRoot.compile();
        return objRoot;

    }


    /**
     * Produce a pointlight node (light that is reflected off the other nodes) based on the _name based in.
     * @param bodies - List of all the solar entities to search
     * @param _name - the name to match against.
     * @return - Orange Pointlight reflected off the planets from the sun, white reflected off the earth from the moons light
     */
    private PointLight getLighting(List<SolarBody> bodies, String _name) {
        SolarBody namedBody = getSolarBodyByName(bodies,_name);

        PointLight light = new PointLight();
        light.setColor(new Color3f(Color.WHITE));

        if(namedBody instanceof Star){
            // sun lighting
            light.setColor(new Color3f(Color.PINK));
            light.setPosition(0.0f,0.0f,0.0f);
            light.setInfluencingBounds(new BoundingSphere(new Point3d(0,0,0),1000));
            light.setCapability(Light.ALLOW_COLOR_WRITE);
        } else if( namedBody instanceof Satellite){
            // moon lighting
            Vector3f position = new Vector3f(namedBody.getPosition());
            Point3d bounds = new Point3d(position);
            light.setPosition(position.getX(),position.getY(),position.getZ() + .8f);
            light.setInfluencingBounds(new BoundingSphere(bounds,namedBody.getRadius() + 3f));
        }
        return light;
    }

    private SolarBody getSolarBodyByName(List<SolarBody> bodies, String _name){
        return bodies.stream().filter(s -> s.getBodyName().equalsIgnoreCase(_name)).findAny().orElse(null);
    }


}
