package yacg.igog

import yacg.util.Logger
import scala.collection.mutable
import yacg.igo.Npc
import yacg.igo.Scene_graph_interface
import com.jme3.math.Vector3f
import com.jme3.math.Vector2f
import yacg.jme.Jme_interface
import yacg.jme.Terrain
import scala.collection.mutable._
import com.jme3.bullet.control.RigidBodyControl
import yacg.igo.Igo
import yacg.persistence.Db

object Igog extends Logger {
  type location_type = HashMap[String, (Float, Float)]

  def init(db_path: String) {
    Db.init(db_path)
  }

  def shutdown {
    Db shutdown
  }

  def get_all_npcs_names: List[String] = {
    var ret_val: List[String] = Nil
    val query = "start result=node(*) where result.type = 'npc' return result"

    Db.execute(query, (node: org.neo4j.graphdb.Node) => {
      ret_val = ret_val :+ node.getProperty("name").asInstanceOf[String]
      log_debug("npc=" + node.getProperty("name"))
    })

    ret_val
  }

  def get_location(id: String, location_name: String): (Float, Float) = {
    var ret_val = (0f, 0f)

    val query = "start npc=node(*) match (npc)-[:" + location_name + "]->(location) where npc.type='npc' and npc.name='" + id + "' and location.type='location' return location as result"

    Db.execute(query, (node: org.neo4j.graphdb.Node) => {
      ret_val = (node.getProperty("x").toString().toFloat, node.getProperty("y").toString().toFloat)
      log_debug("xy=" + ret_val._1 + " " + ret_val._2)
    })

    ret_val
  }

  def create_igos(jme_interface: Jme_interface) {

    List(("npc", "start_x", "start_y", true), ("static", "x", "y", false)).foreach(element => {

      val query = "start result=node(*) where result.type = '" + element._1 + "' return result"

      Db.execute(query, (node: org.neo4j.graphdb.Node) => {

        val name = if (element._4) node.getProperty("name").asInstanceOf[String] else ""
        val file_name = node.getProperty("modelfile").asInstanceOf[String]
        val x = node.getProperty(element._2).toString().toFloat
        val y = node.getProperty(element._3).toString().toFloat

        log_debug("yxz=" + x + " " + Terrain.get_mesh_height(x, y) + " " + y + " - " + file_name)

        //val npc = new Npc(name, file_name, jme_interface)
        var igo: Igo = Igo.createInstance(element._1, name, file_name, jme_interface)

        igo.geo.setLocalTranslation(x, Terrain.get_mesh_height(x, y), y)
        //npc.geo.addControl(new RigidBodyControl(1.0f))
        //jme_interface.bullet_app_state.getPhysicsSpace().add(npc.geo)
        jme_interface.root_node.attachChild(igo.geo)
        igo run

      })
    })

  }

  def get_locations: location_type = {
    var ret_val = new location_type()
    val query = "start result=node(*) where result.type = 'location' return result"

    Db.execute(query, (node: org.neo4j.graphdb.Node) => {
      val name = node.getProperty("name").asInstanceOf[String]
      val x = node.getProperty("x").toString().toFloat
      val y = node.getProperty("y").toString().toFloat

      ret_val.put(name, (x, y))
    })

    ret_val
  }

  def update_location(id: String, x: Float, y: Float) {
    Db.execute("start result=node(*) where has(result.name) and has(result.type) and result.name = '" + id.replace("'", "\\'") + "' and result.type='location' set result.x='" + x + "', result.y='" + y + "' return result".toString())
    //Db.update_node(id, "location", "x", x.toString, "y", y.toString)
  }

  def update_npc(id: String, x: Float, y: Float) {
    Db.execute("start result=node(*) where has(result.name) and has(result.type) and result.name = '" + id.replace("'", "\\'") + "' and result.type='npc' set result.start_x='" + x + "', result.start_y='" + y + "' return result".toString())
    //Db.update_node(id, "npc", "start_x", x.toString, "start_y", y.toString)
  }

  def update_static(id: Long, x: Float, y: Float) {
    Db.execute("start result=node(" + id.toString() + ") where has(result.type) and result.type='static' set result.x='" + x + "', result.y='" + y + "' return result".toString())
    //Db.update_node(id, "npc", "start_x", x.toString, "start_y", y.toString)
  }

  def create_static_models(jme_interface: Jme_interface): scala.collection.mutable.LinkedList[Igo] = {
    val retval = new scala.collection.mutable.LinkedList[Igo]
    val query = "start result=node(*) where has(result.type) and result.type = 'static' return result"

    Db.execute(query, (node: org.neo4j.graphdb.Node) => {
      val file_name = node.getProperty("file_name").asInstanceOf[String]
      val x = node.getProperty("x").toString().toFloat
      val y = node.getProperty("y").toString().toFloat

      log_debug("yxz=" + x + " " + Terrain.get_mesh_height(x, y) + " " + y + " - " + file_name)

      val geo = jme_interface.asset_mgr.loadModel("Models/" + file_name) //.j3o
      geo.setUserData("type", "static")
      geo.setLocalTranslation(x, Terrain.get_mesh_height(x, y), y)
      geo.setLocalScale(new Vector3f(24, 24, 24))
      geo.addControl(new RigidBodyControl(1.0f))
      jme_interface.bullet_app_state.getPhysicsSpace().add(geo)
      jme_interface.root_node.attachChild(geo)
    })

    retval
    /*
    val geo = assetManager.loadModel("Models/house1.blend")
    geo.setLocalScale(new Vector3f(24, 24, 24))
    geo.setLocalTranslation(new Vector3f(-350, 60, 470))
    geo.addControl(new RigidBodyControl(1.0f))
    bulletAppState.getPhysicsSpace().add(geo)
    rootNode.attachChild(geo)

    -271 to -181 by 5 map { x =>
      {
        463 to 580 by 5 map { z =>
          {
            create_shrub(x, (22 * 3) + 7, z)
          }
        }
      }
    }

    1464 to 3369 by 50 map { x =>
      {
        2169 to 3360 by 50 map { z =>
          {
            //create_shrub(x, 10, z)
          }
        }
      }
    }

    def create_shrub(x: Float, y: Float, z: Float) {
      val geo2 = assetManager.loadModel("Models/shrub1.blend")
      geo2.setLocalScale(new Vector3f(4, 2, 4))
      geo2.setLocalTranslation(new Vector3f(x + -15 + util.Random.nextInt(30), y, z + -15 + util.Random.nextInt(30)))
      geo2.rotate(180f * FastMath.DEG_TO_RAD, util.Random.nextInt(130) * FastMath.DEG_TO_RAD, 0f) //.setLocalRotation(new Quarternion())
      //geo2.addControl(new RigidBodyControl(1.0f))
      //bulletAppState.getPhysicsSpace().add(geo2)
      rootNode.attachChild(geo2)
    }
*/
  }

}