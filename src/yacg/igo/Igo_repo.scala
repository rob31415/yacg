package yacg.igo

import com.jme3.scene._
import com.jme3.math.Vector3f
import com.jme3.scene.Mesh
import com.jme3.scene.Geometry
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.bullet.BulletAppState
import com.jme3.asset._
import java.util.concurrent.Future
import java.util.concurrent.Callable
import yacg.Npc_bill_dauterive

object Igo_repo {

  //bäh, igitt! there must be another way!
  var rootNode: Node = _
  var bulletAppState: BulletAppState = _
  var assetmgr: AssetManager = _
  var enqueue: (Callable[Unit]) => Future[Unit] = _
  var npc_1: Npc_bill_dauterive = _

  def init(root_node: Node, bulletappstate: BulletAppState, assetmgr: AssetManager, enqueue: (Callable[Unit]) => Future[Unit]) {
    this.rootNode = root_node
    this.bulletAppState = bulletappstate
    this.assetmgr = assetmgr
    this.enqueue = enqueue
  }

  def get_igo_by_id(id: Int): Igo =
    {
      if (npc_1 == null) {
        npc_1 = new Npc_bill_dauterive(1, enqueue)
        bulletAppState.getPhysicsSpace().add(npc_1.geo)
        rootNode.attachChild(npc_1.geo)
      }
      npc_1
    }

  def createGeometryFromMesh(mesh: Mesh, name: String, loc: Vector3f, color: ColorRGBA): Geometry =
    {
      var geom = new Geometry(name, mesh)
      var mat = new Material(assetmgr, "Common/MatDefs/Misc/Unshaded.j3md")
      mat.setColor("Color", color)
      geom.setMaterial(mat)
      geom.setLocalTranslation(loc)
      geom;
    }

}