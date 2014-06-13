package yacg.jme

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.TextureKey;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainGridTileLoader;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap._;
import com.jme3.texture.Texture;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class ImageTileLoada(var assetManager: AssetManager, val namer: Namer) extends TerrainGridTileLoader {
  var patchSize = 0;
  var quadSize = 0;
  var heightScale = 0.1f;

  def setHeightScale(heightScale: Float) {
    this.heightScale = heightScale;
  }

  def getHeightMapAt(location: Vector3f): HeightMap = {

    val x = location.x.toInt;
    val z = location.z.toInt;

    var heightmap: AbstractHeightMap = null;

    var name: String = null;
    name = namer.getName(x, z);

    val texture: Texture = assetManager.loadTexture(new TextureKey(name));
    heightmap = new ImageBasedHeightMap(texture.getImage());
    heightmap.setHeightScale(1);
    heightmap.load();
    heightmap.smooth(1.0f, 2);
    return heightmap;
  }

  def setSize(size: Integer) {
    this.patchSize = size - 1;
  }

  def getTerrainQuadAt(location: Vector3f): TerrainQuad = {
    val heightMapAt = getHeightMapAt(location);
    val q = new TerrainQuad("Quad" + location, patchSize, quadSize, if (heightMapAt == null) null else heightMapAt.getHeightMap());
    return q;
  }

  def setPatchSize(patchSize: Int): Unit = {
    this.patchSize = patchSize;
  }

  def setQuadSize(quadSize: Int): Unit = {
    this.quadSize = quadSize;
  }

  def write(ex: JmeExporter) {}

  def read(im: JmeImporter) {}
}
