package yacg.audio

import com.jme3.audio.AudioNode
import com.jme3.asset.AssetManager
import com.jme3.scene.Node
import yacg.Configurator
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{ Success, Failure }
import yacg.util.Logger

object Audio extends Logger {

  var assetManager: AssetManager = _
  var rootNode: Node = _

  def init(assetManager: AssetManager, rootNode: Node) {
    this.rootNode = rootNode
    this.assetManager = assetManager

    if (Configurator.playAudio) {
      //play("yacg_incidental_music_1", false)
      play("wind", false)
      future {
        while (true) {
          val rnd = 1000 + (Math.abs(scala.util.Random.nextInt().toDouble / Integer.MAX_VALUE) * 10000).toInt
         this.log_debug("playing audio in " + rnd)
          Thread.sleep(rnd)
          play("yacg_incidental_music_1", false)
        }
      }
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