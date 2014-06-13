package yacg.jme

import com.jme3.util.SkyFactory
import com.jme3.renderer.queue.RenderQueue.Bucket
import com.jme3.scene.Spatial

object Sky {
  def init(jme_interface: Jme_interface) {
    val sky = SkyFactory.createSky(jme_interface.asset_mgr, "Textures/sky/skydome2.bmp", true)
    jme_interface.root_node.attachChild(sky)
    sky.setQueueBucket(Bucket.Sky)
    sky.setCullHint(Spatial.CullHint.Never)
    //this.viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f))
  }

}