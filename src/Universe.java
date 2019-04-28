import javax.vecmath.Vector3d;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class Universe {
    private List<SolarBody> bodies;

    /**
     * Initialises our Solar system objects.
     */
    public void initSolarEntities() {
        // Initialise the  planets
        Planet mercury = new Planet("Mercury", .5f, new Vector3d(.38, .38, .38), new Vector3d(0, 0, -2), 20000, 8800L);
        Planet venus = new Planet("Venus", .5f, new Vector3d(0.95, 0.95, 0.95), new Vector3d(1, 0., -3), 40000, 22470L);
        Planet earth = new Planet("Earth", .25f, new Vector3d(1, 1, 1), new Vector3d(2, 0, -4), Math.PI / 2, 12500, 36500L);
        Planet mars = new Planet("Mars", .5f, new Vector3d(0.53, 0.53, 0.53), new Vector3d(3, 0, -5), 13000, 68700L);
        Planet jupiter = new Planet("Jupiter", .5f, new Vector3d(2.0, 2.0, 2.0), new Vector3d(5.0, 0.0, -7.0), 8000, 43320L);
        Planet saturn = new Planet("Saturn", .5f, new Vector3d(2.0, 2.0, 2.0), new Vector3d(6.0, 0.0, -9.5), Math.PI / 5, 9000, 107600L);
        Planet uranus = new Planet("Uranus", .5f, new Vector3d(2.0, 2.0, 2.0), new Vector3d(7.6, 0.0, -12.0), 11000, 30700L);
        Planet neptune = new Planet("Neptune", .5f, new Vector3d(2.0, 2.0, 2.0), new Vector3d(8.4, 0.0, -14.5), 10000, 60200L);


        // initialise the sun and moon.
        Star sun = new Star("Sun", 1, 64800);
        Satellite moon = new Satellite("Moon", .2f, new Vector3d(0.27, 0.27, 0.27), new Vector3d(0.0f, 0.0f, -0.6f), 64800, 2700);

        // save the above objects.
        bodies = Arrays.asList(mercury, venus, earth, mars, jupiter, saturn, uranus, neptune, sun, moon);

        // initialise all the transformations.
        bodies.forEach(b -> {
            b.createScaledTransformGroup();
            b.createRotationTransformGroup();
            b.createOrbitTransform();
        });
    }

    public Star  createAltSun(){
        Star sun = new Star("altSun", 1, 64800);
        sun.createScaledTransformGroup();
        sun.createRotationTransformGroup();
        sun.createOrbitTransform();
        return sun;
    }

    public List<SolarBody> getBodies() {
        return bodies;
    }

    public SolarBody getSolarBodyByName(String _name) {
        return bodies.stream().filter(s -> s.getBodyName().equalsIgnoreCase(_name)).findAny().orElse(null);
    }

    /**
     * Returns a list where each item is of the class passed in.
     *
     * @param subclass - the class extending from SolarBody to match against.
     * @return
     */
    public List<SolarBody> getSolarByType(Class<? extends SolarBody> subclass) {
        return bodies.stream().filter(b -> b.getClass().equals(subclass)).collect(Collectors.toList());
    }


}
