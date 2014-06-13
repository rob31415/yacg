package yacg.jme

import com.jme3.light.DirectionalLight
import com.jme3.shadow.DirectionalLightShadowRenderer
import com.jme3.shadow.DirectionalLightShadowFilter
import com.jme3.post.FilterPostProcessor
import com.jme3.renderer.queue.RenderQueue.ShadowMode

object Shadow {

  //@TODO: shadow, y u no work !?
  def init(sun: DirectionalLight) {
    /*
    val shadowmap_size = 256

    val dlsr = new DirectionalLightShadowRenderer(assetManager, shadowmap_size, 3);
    dlsr.setLight(sun);
//    viewPort.addProcessor(dlsr);

    val dlsf = new DirectionalLightShadowFilter(assetManager, shadowmap_size, 3);
    dlsf.setLight(sun);
    dlsf.setEnabled(true);

    val fpp = new FilterPostProcessor(assetManager);
    fpp.addFilter(dlsf);
    viewPort.addProcessor(fpp);
*/

    /*
    val pssm = new PssmShadowRenderer(assetManager, 4096, 3);
    pssm.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
    pssm.setFilterMode(FilterMode.PCF4);
    viewPort.addProcessor(pssm);
*/

//    Terrain.terrain.setShadowMode(ShadowMode.CastAndReceive)
//    rootNode.setShadowMode(ShadowMode.Receive)
  }
}