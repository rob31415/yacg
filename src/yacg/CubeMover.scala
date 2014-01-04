package yacg

import com.jme3.scene.Geometry
import akka.actor._
import com.jme3.app.SimpleApplication
import java.util.concurrent.Callable


case object Go

class float_wrap(var tpf: Float)	// wrap byval with a byref

class CubeMover(boxie: Geometry, app: SimpleApplication, fw: float_wrap) extends Actor {
  
    // var tpf: Float = 1.0f		//no clean way to access this from another thread other than via receive
  
    def receive = {
      case Go =>
        println("Hello World!")
        while(true)
        {
        	Thread sleep((fw.tpf * 1000) toLong)
        	Thread `yield`	//yield is a scala keyword, so it needs to be exkaped

        	//this generates the exception
		    /*
		     * uncaught exception thrown in thread LWJGL Renderer Thread,5,main
		     * illegalstateexception: scene graph is not properly updated for rendering.
		     * state was changed after rottnode.updategeometricstate() call.
		     * make shure you do not modify the scene from another thread!
		     * problem spatial name: root node
		     * 
		     */
        	//boxie.move(0.016f * 10.0f, 0, 0)	

        	// this works. problem now is the missing tpf
        	val callable = new Callable[Unit]() {
        		def call() {
        			boxie.move(10 * fw.tpf, 0, 0)
        		}
            }
        	app.enqueue(callable)
        	//println("tpf=" + fw.tpf)        	
        	
        }
    }

}
