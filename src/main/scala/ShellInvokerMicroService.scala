import akka.actor.ActorSystem
import akka.event.{LoggingAdapter, Logging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.IOException
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.math._
import spray.json.DefaultJsonProtocol
import java.io.BufferedReader
import java.io.InputStreamReader
import scala.sys.process._

trait Service {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  val logger: LoggingAdapter

  val routes = {
    logRequestResult("shell-invoker-microservice") {
      pathPrefix(config.getString("services.kickoff.name")) {
        (get & path(Segment.repeat(4, separator = Slash))) { params  =>
              val cliCmd = s"${config.getString("services.kickoff.command")} ${params(0)} ${params(1)} ${params(2)} ${params(3)} ; "
  	      logger.info(cliCmd)
	      val p = Process(cliCmd)
              val exitVal = p.!
	      logger.debug(s"$exitVal")
              complete {
	  	s"$exitVal"
	      }
        }
      }
    }
  }
}

object ShellInvokerMicroService extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  val bindingFuture = Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
