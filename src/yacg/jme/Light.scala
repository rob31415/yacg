package yacg.jme

import com.jme3.light.DirectionalLight
import com.jme3.math.Vector3f
import com.jme3.math.ColorRGBA
import com.jme3.light.AmbientLight
import com.jme3.scene.Node

object Light {
  def init(root_node: Node): DirectionalLight = {

    val light = new DirectionalLight()
    light.setDirection((new Vector3f(-0.5f, -0.05f, -0.2f).normalizeLocal))
    light.setColor(ColorRGBA.White.mult(0.7f)) //new ColorRGBA(200 / 255, 150 / 255, 50 / 255, 1))
    root_node.addLight(light)

    val light3 = new DirectionalLight()
    light3.setDirection((new Vector3f(0.5f, -0.05f, 0.2f).normalizeLocal))
    light3.setColor(ColorRGBA.White.mult(0.7f)) //new ColorRGBA(200 / 255, 150 / 255, 50 / 255, 1))
    root_node.addLight(light3)

    val light2 = new AmbientLight()
    light2.setColor(ColorRGBA.White.mult(1.6f))
    root_node.addLight(light2)

    //initShadow(light)
    light
  }

}