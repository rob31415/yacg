package yacg.persistence
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.neo4j.cypher.javacompat.ExecutionEngine
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Node
import org.neo4j.server.WrappingNeoServerBootstrapper
import org.neo4j.server.configuration.ServerConfigurator
import org.neo4j.server.configuration.{Configurator => serverConfig}
import org.neo4j.kernel.EmbeddedGraphDatabase
import scala.collection.JavaConversions._
import yacg.util.Logger
import org.neo4j.server.configuration.{Configurator => serverConfig}

/*
 * cypher snippets
 * 
 * 
 * all nodes: start n=node(*) return n;
 * node with attr value: start n=node(*) where n.type="npc" return n;
 * nodes that have attr: start n=node(*) where has(n.someprop) return n;
   CREATE (a { type : 'static', x:-350, y:470, modelfile:'house1.blend' }) RETURN a
 * START n=node(9999) delete n
 * MATCH (n) RETURN n LIMIT 100
 * START n=node(9999) return n
 * delete property: start n=node(30524) set n.modelfile=n.file_name, n.file_name=null / or delete n.name
 * 
 * start n=node(*) where has(n.type) return n
 */


/*
 * 		      neo4j 2.0    1.9
 * write			ok     ok
 * neoclipse	    nok	   ok
 * embedded server	nok	   ok, but no logging
 * os server		ok	   ok
 * 
 */
object Db extends Logger {
  var graph_db: Option[GraphDatabaseService] = None
  var engine: ExecutionEngine = _
  var srv: WrappingNeoServerBootstrapper = _
  
  def init(db_path: String) {
    init_db(db_path)
    init_db_server(db_path)
  }

  def init_db(db_path: String) {
    graph_db = Some(new GraphDatabaseFactory().
      newEmbeddedDatabaseBuilder(db_path + "graph.db/")
      .loadPropertiesFromFile(db_path + "neo4j.properties")
      .newGraphDatabase())

    engine = new ExecutionEngine(graph_db.get)
    
    log_debug("opened db at " + db_path)
  }

  def init_db_server(db_path: String) {
    val configurator = new ServerConfigurator(graph_db.get.asInstanceOf[EmbeddedGraphDatabase])
    configurator.configuration().setProperty(serverConfig.WEBSERVER_ADDRESS_PROPERTY_KEY, "127.0.0.1")
    configurator.configuration().setProperty(serverConfig.WEBSERVER_PORT_PROPERTY_KEY, 7474)
    configurator.configuration().setProperty(serverConfig.DEFAULT_DATABASE_LOCATION_PROPERTY_KEY, db_path)
    // configurator.configuration().setProperty(Configurator.RRDB_LOCATION_PROPERTY_KEY, db_path)

    srv = new WrappingNeoServerBootstrapper(graph_db.get.asInstanceOf[EmbeddedGraphDatabase], configurator)
    if(yacg.Configurator.startServer) srv.start
  }

  def shutdown() {
    if(yacg.Configurator.startServer) srv.stop

    if (graph_db.isDefined) {
      log_debug("shutdown");
      graph_db.get.shutdown()
      log_debug("shutdown ok");
    } 
    else log_debug("shutdown failed. no graph-db instance.");

    graph_db = None
  }

  // query must return a column named "result".
  // given lambda is executed for every row.
  def execute[T](query: String, lambda: (T) => (Unit)) {
    log_debug(query)

    if (graph_db.isDefined) {

      try {
        val tx = graph_db.get.beginTx()
        val result = engine.execute(query)
        //log_debug(result.dumpToString())

        var it = result.columnAs("result")
        while (it.hasNext) {
          lambda(it.next.asInstanceOf[T])
        }

        tx.success
        tx.finish
        //tx.close()

      } catch {
        case e: Exception => { log_error("", e) }
      }

    }
  }

  def execute(query: String) {

    if (graph_db.isDefined) {

      try {
    	log_debug(query)
    	val tx = graph_db.get.beginTx()
        val result = engine.execute(query)
        log_debug(result.getQueryStatistics().toString())
        tx.success()
        // log_debug(result.dumpToString())
        tx.finish()
        //tx.close()
        log_debug("done")
      } catch {
        case e: Exception => { log_error("", e) }
      }
    }

    /*
    Db.execute("start npc=node(*) match (npc)-[:home]->(location) where npc.type='npc' and npc.name='frank' and location.type='location' return location as result", (node: org.neo4j.graphdb.Node) => {
      val ret_val = (node.getProperty("x").toString().toFloat, node.getProperty("y").toString().toFloat)
      log_debug("xy=" + ret_val._1 + " " + ret_val._2)
    })
      */

  }
  
  def dump: String = {
    var retVal: String = ""
      
    if (graph_db.isDefined) {

      try {
    	val tx = graph_db.get.beginTx()
        val result = engine.execute("START n=node(*) RETURN n;")	//cypher 2.0 MATCH (n) RETURN n LIMIT 100
        retVal = result.dumpToString()
        tx.success()
        tx.finish()	
      } catch {
        case e: Exception => { log_error("", e) }
      }
    }

    retVal
  }

  def update_node(name: String, node_type: String, v1_key: String, v1_val: String, v2_key: String, v2_val: String) {

    if (graph_db.isDefined) {

      val tx = graph_db.get.beginTx()
      try {
        graph_db.get.getAllNodes().foreach(node => {
          if (node.hasProperty("name") &&
            node.hasProperty("type") &&
            node.hasProperty(v1_key) &&
            node.hasProperty(v2_key) &&
            node.getProperty("type") == node_type &&
            node.getProperty("name") == name) {

            node.setProperty(v1_key, v1_val)
            node.setProperty(v2_key, v2_val)

            tx.success()

            log_debug("set " + name + " to xy " + v1_val + " " + v2_val)
          }
        })
      } catch {
        case e: Exception => { log_error("", e) }
      } finally {
        tx.finish()
        //tx.close()
       }

    }

  }

}