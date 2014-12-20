package yacg.igo
import com.jme3.math.Vector3f
import com.jme3.bullet.control.RigidBodyControl
import yacg.igo._
import yacg.events._
import com.jme3.math.Vector2f
import com.jme3.terrain.geomipmap.TerrainGrid
import com.jme3.asset.AssetManager
import yacg.util.Logger
import yacg.igog.Igog
import yacg.jme.Terrain
import yacg.jme.Jme_interface

// @TODO: maybe derive from a PhysicsCollisionObject and let bullet take care of collisions - find out how that can work for an npc that walks on top of terrain
class Npc(name: String, file_name: String, jme_interface: Jme_interface)
  extends Mobile(name, file_name, jme_interface.asset_mgr, jme_interface.enqueue)
  with Logger
  {

  typeName = "npc"

}

