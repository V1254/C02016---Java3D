import javax.vecmath.Vector3d;

public class Planet extends SolarBody {

    /**
     * Instantiate an instance of a planet
     *
     * @param _name             - the name of the entity. (case Insenstive to searches).
     * @param _radius           - radius of the entity.
     * @param _scaling          - scaling for the transform group
     * @param _position         - translation for the transform group.
     * @param _rotationAngle    - the angle to rotate at, can be null.
     * @param _rotationDuration - time taken for a entity to rotate within its own axis.
     * @param _orbitDuration    - time taken for entity to orbit another entity. (null for entities that do not have an orbit).
     */
    public Planet(String _name, float _radius, Vector3d _scaling, Vector3d _position, Double _rotationAngle, long _rotationDuration, Long _orbitDuration) {
        super(_name, _radius, _scaling, _position, _rotationAngle, _rotationDuration, _orbitDuration);
    }

    public Planet(String _name, float _radius, Vector3d _scaling, Vector3d _position,long _rotationDuration, Long _orbitDuration){
        super(_name,_radius,_scaling,_position,null,_rotationDuration,_orbitDuration);
    }

    public Planet(String _name, float _radius, Vector3d _scaling, Vector3d _position,long _rotationDuration){
        this(_name,_radius,_scaling,_position,_rotationDuration,null);
    }
}
