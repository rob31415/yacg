package yacg.util

import com.jme3.math.Vector3f
import yacg.jme.Terrain

object coordConversion {

  def threeDToImage(loc: Vector3f): (Int, Int) =
    {
      val x = Terrain.patch_size + (loc.x / Terrain.scale_z)
      val y = Terrain.patch_size + (loc.z / Terrain.scale_x)
      return (x.toInt, y.toInt)
    }

  def threeDToImage(ix: Float, iy: Float): (Int, Int) =
    {
      return threeDToImage(new Vector3f(ix, 0, iy))
    }

  def ImageToThreeD(ix: Float, iy: Float): Vector3f =
    {
      val x = (ix - Terrain.patch_size) * Terrain.scale_z
      val z = (iy - Terrain.patch_size) * Terrain.scale_x * -1
      return new Vector3f(x, 0, z)
    }

}
