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
import yacg.igog.Igog
import yacg.util.Logger
import scala.collection.mutable
import yacg.jme.Jme_interface
import yacg.igo._

/*
 * there are 3 different "storage locations" here:
 * the ingameobjectgraph (igog), the scenegraph (sg), and the igo
 * 
 * the igog is an interface to the database - it represents persisted igo-s. at runtime, you can't find an igo-instance via igog.
 * a spatial is a member of a igo.
 * the spatial is put in the sg.
 * to get the igo via id from the sg, the igo is referenced via id from it's member spatial.
 * 
 * here's an ascii-art of the three "things" to drive home the point, that a spatial needs
 * a ref to it's parent/container-npc, for the npc to be retrieved via the sg:
 * 
 * igog --- node1-id
 *     \--- node2-id
 *     
 * igo <-- npc
 *          \--- spatial    
 *                \--- id    
 * 
 * sg
 *  \--- spatial
 *           \--- id
 */
object Scene_graph_interface extends Logger {

  var jme_interface: Jme_interface = _

  def init(jme_interface: Jme_interface) {
    this.jme_interface = jme_interface
    Igog.create_igos(jme_interface)
  }

  def shutdown() {
    getIgosByType("npc") foreach (igo => igo.stop)
    jme_interface.root_node.detachAllChildren
  }

  def reinit() {
    shutdown
    init(jme_interface)
  }

  def refresh() {
    log_debug("refreshing SceneGraph")
    getIgosByType("npc") foreach (igo => igo.geo.setLocalTranslation(igo.geo.getLocalTranslation().x, yacg.jme.Terrain.get_mesh_height(igo.geo.getLocalTranslation().x, igo.geo.getLocalTranslation().z) , igo.geo.getLocalTranslation().z))
    getIgosByType("static") foreach (igo => igo.geo.setLocalTranslation(igo.geo.getLocalTranslation().x, yacg.jme.Terrain.get_mesh_height(igo.geo.getLocalTranslation().x, igo.geo.getLocalTranslation().z) , igo.geo.getLocalTranslation().z))
    //@TODO: put npcs in certain terrain tile on top of terrain (manipulate their transform)
  }

  def createGeometryFromMesh(mesh: Mesh, name: String, loc: Vector3f, color: ColorRGBA): Geometry =
    {
      var geom = new Geometry(name, mesh)
      var mat = new Material(jme_interface.asset_mgr, "Common/MatDefs/Misc/Unshaded.j3md")
      mat.setColor("Color", color)
      geom.setMaterial(mat)
      geom.setLocalTranslation(loc)
      geom
    }

  def get_igo_by_id(id: String): Option[Igo] = {
    var ret_val: Option[Npc] = None

    log_debug("get_igo_by_id " + id)

    val visitor = new SceneGraphVisitor() {
      override def visit(spatial: Spatial) {
        if (spatial.getUserData("id") != null) {
          log_debug("id=" + spatial.getUserData("id").toString)
          if (spatial.getUserData("id").toString == id) ret_val = Some(spatial.getUserData("igo_ref").asInstanceOf[Npc])
        }
      }
    }

    log_debug("get_igo_by_id before traversal")
    jme_interface.root_node.breadthFirstTraversal(visitor)
    log_debug("get_igo_by_id after traversal")

    ret_val
  }

  def set_tpf_on_all_npcs(tpf: Float) {
    getIgosByType("npc") foreach { igo => igo.fw.tpf = tpf }
  }

  // it would be nice to specify the type and then to get a list of elements of that type as to not force users to cast. (generics?)
  def getIgosByType(typeName: String = ""): scala.collection.mutable.ListBuffer[Igo] = {
    var retVal = new scala.collection.mutable.ListBuffer[Igo]

    val visitor = new SceneGraphVisitor() {
      override def visit(spatial: Spatial) {
        val candidate: Igo = spatial.getUserData("igo_ref")
        if (candidate != null) {
          if (typeName == "" || typeName == "igo" || candidate.typeName == typeName) retVal += candidate
        }
      }
    }

    jme_interface.root_node.breadthFirstTraversal(visitor)

    retVal
  }

  def dump_current_locations: String = {
    var retVal = "npcs\n"
    getIgosByType("npc") foreach { igo => retVal += igo.asInstanceOf[Npc].name + ":" + igo.geo.getLocalTranslation().toString() + "\n" }
    retVal += "statics\n"
    getIgosByType("static") foreach { igo => retVal += igo.asInstanceOf[Static].file_name + ":" + igo.geo.getLocalTranslation().toString() + "\n" }
    retVal
  }

}








