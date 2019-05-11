import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.image.TextureLoader;


import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.vecmath.Color3f;
import java.awt.*;

/**
 * Represents the rings of a planet.
 */
public class Ring {

    private float radius;
    private float height;
    private String name;
    private int primFlags = Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS;
    private String textureDirectory = "resources//", extension = ".png";
    private Cylinder shape;
    private Appearance appearance;

    Ring(float radius, float height, String _ringName){
        this.radius = radius;
        this.height = height;
        this.name = _ringName;
        initAppearance();
        this.shape = new Cylinder(radius,height,appearance);
    }

    void initAppearance(){
        String path = textureDirectory + this.name + extension;
        Texture texture = new TextureLoader(path,null).getTexture();

        TextureAttributes att = new TextureAttributes();
        att.setTextureMode(TextureAttributes.MODULATE);

        Material m = new Material();
        m.setSpecularColor(new Color3f(Color.WHITE));
        m.setDiffuseColor(new Color3f(Color.WHITE));


        appearance = new Appearance();
        appearance.setMaterial(m);
        appearance.setTexture(texture);
        appearance.setTextureAttributes(att);
    }
    public Cylinder getShape() {
        return shape;
    }

}
