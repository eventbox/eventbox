package eventbox

import java.util.concurrent.TimeUnit
import scala.concurrent.{ExecutionContext, Future}

object Timing {

	def time[R](name:String, unit:TimeUnit=TimeUnit.MILLISECONDS)(block: => R): R = {
		val t0 = System.nanoTime()
		val result = block    // call-by-name
		val t1 = System.nanoTime()

		println(s"${name}: ${unit.convert(t1-t0, TimeUnit.NANOSECONDS)} ${unit.toString}")
		result
	}

	def timeF[R](name:String, unit:TimeUnit=TimeUnit.MILLISECONDS)(block: => Future[R])(implicit ec:ExecutionContext): Future[R] = {
		val t0 = System.nanoTime()
		block.map { result =>
			val t1 = System.nanoTime()
			println(s"${name}: ${unit.convert(t1-t0, TimeUnit.NANOSECONDS)} ${unit.toString}")
			result
		}
	}
}
