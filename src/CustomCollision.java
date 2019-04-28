
import com.sun.j3d.utils.geometry.Primitive;

import javax.media.j3d.Behavior;
import javax.media.j3d.Bounds;
import javax.media.j3d.PointLight;
import javax.media.j3d.Switch;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.vecmath.Color3f;
import java.awt.*;
import java.util.Enumeration;

/**
 * Collision involving the sun and pyramid.
 */

public class CustomCollision extends Behavior {

    private Primitive originalSun;
    private Primitive pinkSun;
    private Switch aSwitch;
    private WakeupCriterion[] criterion;
    private PointLight sunLight;


    public CustomCollision(Primitive _orgSun, Primitive _newSun, Switch _switch, PointLight _sunLight, Bounds bounds) {
        this.originalSun = _orgSun;
        this.pinkSun = _newSun;
        this.aSwitch = _switch;
        this.sunLight = _sunLight;
        setSchedulingBounds(bounds);
    }

    /**
     * Set up conditions criteria[0] = normal sun and criteria[1] = pink sun.
     */

    @Override
    public void initialize() {
        criterion = new WakeupCriterion[2];
        System.out.println("This was called????");
        criterion[0] = new WakeupOnCollisionEntry(originalSun);
        criterion[1] = new WakeupOnCollisionEntry(pinkSun);
        wakeupOn(criterion[0]);

    }

    /**
     * Switch the current active sun and change light accordingly.
     *
     * @param enumeration
     */
    @Override
    public void processStimulus(Enumeration enumeration) {
        if (aSwitch.getWhichChild() == 0) {
            // swap to pink sun and change lighting to magenta
            aSwitch.setWhichChild(1);
            this.sunLight.setColor(new Color3f(Color.MAGENTA));
            wakeupOn(criterion[1]);
        } else {
            aSwitch.setWhichChild(0);
            this.sunLight.setColor(new Color3f(new Color(224, 186, 168)));
            wakeupOn(criterion[0]);
        }


    }
}
