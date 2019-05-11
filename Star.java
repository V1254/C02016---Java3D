import javax.vecmath.Vector3d;

public class Star extends SolarBody {


    /**
     * Instantiate an instance of a planet with all values being utilised.
     *
     * @param _name             - the name of the entity. (case Insensitive to searches).
     * @param _radius           - radius of the entity.
     * @param _scaling          - scaling for the transform group
     * @param _position         - translation for the transform group.
     * @param _rotationAngle    - the angle to rotate at, can be null.
     * @param _rotationDuration - time taken for a entity to rotate within its own axis.
     * @param _orbitDuration    - time taken for entity to orbit another entity. (null for entities that do not have an orbit).
     */

    public Star(String _name, float _radius, Vector3d _scaling, Vector3d _position, Double _rotationAngle, long _rotationDuration, Long _orbitDuration) {
        super(_name, _radius, _scaling, _position, _rotationAngle, _rotationDuration, _orbitDuration);
    }

    /**
     * Lazy Constructor, instantiate a star with null values for : scaling, position, rotationAngle and orbitDuration.
     *
     * @param _name             - the name to assign to this star(case Insensitive to searches).
     * @param _radius           - the radius of this star.
     * @param _rotationDuration - time taken for a entity to rotate within its own axis.
     */
    public Star(String _name, float _radius, long _rotationDuration) {
        super(_name, _radius, null, null, null, _rotationDuration, null);
    }
}
