import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Material;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;

/**
 * Superclass for all solar entities in the universe.
 */
public abstract class SolarBody {

    protected String bodyName;
    protected float radius;

    protected Primitive sphere;
    protected Appearance appearance;

    protected TransformGroup mainTransformGroup;
    protected TransformGroup axisTransformGroup;
    protected TransformGroup orbitTransformGroup;

    protected RotationInterpolator orbitRotator;
    protected RotationInterpolator axisRotator;

    protected Vector3d scaling;
    protected Vector3d position;

    protected Double rotationAngle;
    protected long rotationDuration;
    protected Long orbitDuration;

    private static final int primitiveFlags = Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS;
    private static final String texturePath = "src/resources//", extension = ".png";

    /**
     * Instantiate an instance of this class.
     * @param _name - the name of the entity. (case Insenstive to searches).
     * @param _radius - radius of the entity.
     * @param _scaling - scaling for the transform group
     * @param _position - translation for the transform group.
     * @param _rotationAngle - the angle to rotate at, can be null.
     * @param _rotationDuration - time taken for a entity to rotate within its own axis.
     * @param _orbitDuration - time taken for entity to orbit another entity. (null for entities that do not have an orbit).
     */
    public SolarBody(String _name, float _radius, Vector3d _scaling, Vector3d _position, Double _rotationAngle, long _rotationDuration, Long _orbitDuration) {
        this.bodyName = _name;
        this.radius = _radius;
        this.scaling = _scaling;
        this.position = _position;
        this.rotationAngle = _rotationAngle;
        this.rotationDuration = _rotationDuration;
        this.orbitDuration = _orbitDuration;
        initAppearance();
        this.sphere = new Sphere(radius, primitiveFlags, 50,appearance);
    }

    public SolarBody(String _name, float _radius, Vector3d _scaling, Vector3d _position,long _rotationDuration, Long _orbitDuration){
        this(_name,_radius,_scaling,_position,null,_rotationDuration,_orbitDuration);
    }

    /**
     * Create our appearance object using the texture based on the entityName.
     */
    protected void initAppearance() {
        // Load the texture based on the name.
        String pathToTexture = texturePath + bodyName.toLowerCase() + extension;
        Texture texture = new TextureLoader(pathToTexture, null).getTexture();

        // attribute and material
        TextureAttributes attributes = new TextureAttributes();
        attributes.setTextureMode(TextureAttributes.MODULATE);

        Material material = new Material();
        material.setSpecularColor(new Color3f(Color.WHITE));
        material.setDiffuseColor(new Color3f(Color.WHITE));

        appearance = new Appearance();
        appearance.setTexture(texture);
        appearance.setTextureAttributes(attributes);

        // better to just override this method in subclass i think.
        if(!(this instanceof Star)){
            appearance.setMaterial(material);
        }
    }

    /**
     * Initialise the scaled  and translated transformGroup for this entity.
     */
    public void createScaledTransformGroup() {

        if (scaling != null && position != null) {
            // Scale and translate using the set values
            Transform3D transform = new Transform3D();
            transform.setScale(scaling);

            // rotate only if the planet needs it
            if (rotationAngle != null)
                transform.rotZ(rotationAngle);

            transform.setTranslation(position);
            mainTransformGroup = new TransformGroup(transform);
        }

    }


    /**
     * Initialise RotationInterpolator and transform group for the solar body (rotation around their own axis).
     */
    protected void createRotationTransformGroup() {
        axisTransformGroup = new TransformGroup();
        axisTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Transform3D yAxis = new Transform3D();

        Alpha alpha = new Alpha(-1, rotationDuration);
        axisRotator = new RotationInterpolator(alpha, axisTransformGroup, yAxis, 0, (float) (2 * Math.PI));

        Point3d pos;


        if(position == null){
            pos = new Point3d(0, 0, 0);
        } else {
            pos = new Point3d(position.getX(), position.getY(), position.getZ());
        }
        BoundingSphere bounds = new BoundingSphere(new BoundingSphere(pos, radius));
        axisRotator.setSchedulingBounds(bounds);
    }

    /**
     * Initialise OrbitInterpolator and transform group for the solar body.
     */
    public void createOrbitTransform() {
        if(orbitDuration == null){
            return;
        }

        orbitTransformGroup = new TransformGroup();
        orbitTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Transform3D orbitYAxis = new Transform3D();

        orbitRotator = new RotationInterpolator(new Alpha(-1, orbitDuration), orbitTransformGroup, orbitYAxis, 0, (float) (2 * Math.PI));
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000);
        orbitRotator.setSchedulingBounds(bounds);
    }

    /**
     * Getters From here.
     */

    public String getBodyName() {
        return bodyName;
    }

    public float getRadius() {
        return radius;
    }

    public Primitive getSphere() {
        return sphere;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public TransformGroup getMainTransformGroup() {
        return mainTransformGroup;
    }

    public TransformGroup getAxisTransformGroup() {
        return axisTransformGroup;
    }

    public TransformGroup getOrbitTransformGroup() {
        return orbitTransformGroup;
    }

    public Vector3d getScaling() {
        return scaling;
    }

    public Vector3d getPosition() {
        return position;
    }

    public Double getRotationAngle() {
        return rotationAngle;
    }

    public long getRotationDuration() {
        return rotationDuration;
    }

    public long getOrbitDuration() {
        return orbitDuration;
    }

    public static int getPrimitiveFlags() {
        return primitiveFlags;
    }

    public static String getTexturePath() {
        return texturePath;
    }

    public static String getExtension() {
        return extension;
    }

    public RotationInterpolator getOrbitRotator() {
        return orbitRotator;
    }

    public RotationInterpolator getAxisRotator() {
        return axisRotator;
    }
}
