package yacg.audio

import com.jme3.audio.AudioNode
import com.jme3.asset.AssetManager
import com.jme3.scene.Node
import yacg.Configurator

object Audio {

  var assetManager: AssetManager = _
  var rootNode: Node = _

  def init(assetManager: AssetManager, rootNode: Node) {
    this.rootNode = rootNode
    this.assetManager = assetManager

    if (Configurator.playAudio) {
      play("yacg_incidental_music_1", false)
      play("wind", false)
    }

  }

  def play(name: String, loop: Boolean) {
    val audioNode: AudioNode = new AudioNode(assetManager, "Sounds/" + name + ".ogg", false);
    audioNode.setLooping(loop);
    audioNode.setPositional(false);
    audioNode.setVolume(0.05f);
    rootNode.attachChild(audioNode);
    audioNode.play();
  }
}