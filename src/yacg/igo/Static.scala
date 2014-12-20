package yacg.igo

import yacg.events.Event
import com.jme3.asset.AssetManager

// @TODO: rename to stationary
class Static(_name: String, _file_name: String, assetManager: AssetManager) extends Igo(_name, _file_name, assetManager) {
  typeName = "static"

  def handle(event: Event): Boolean = {true}
  override def run = {}
  def stop = {}
}