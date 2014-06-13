package yacg.jme

import com.jme3.post.FilterPostProcessor
import com.jme3.post.filters.FogFilter
import com.jme3.renderer.ViewPort
import com.jme3.math.ColorRGBA
import com.jme3.asset.AssetManager
import com.jme3.scene.Node

object Fog {
  def init(asset_mgr: AssetManager, view_port: ViewPort, node: Node) {

    val fpp = new FilterPostProcessor(asset_mgr);
    val fog = new FogFilter();
    fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
    fog.setFogDistance(5000);
    fog.setFogDensity(1.0f);
    fpp.addFilter(fog);
    view_port.addProcessor(fpp);

  }

  /*   
val fs = display.getRenderer().createFogState();
           fs.setDensity(0.5f);
           fs.setEnabled(true);
           fs.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
           fs.setEnd(1000);
           fs.setStart(300);
           fs.setDensityFunction(FogState.DF_LINEAR);
           fs.setApplyFunction(FogState.AF_PER_VERTEX);
           rootNode.setRenderState(fs);    
  }
  * 
  */
}