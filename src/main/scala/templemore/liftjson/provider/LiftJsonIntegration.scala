package templemore.liftjson.provider

import net.liftweb.json._
import io.Source
import java.io.{InputStream, OutputStream, OutputStreamWriter}

private[provider] trait LiftJsonIntegration {

  protected def convertToJson(value: AnyRef, entityStream: OutputStream) = {
    val jsonAst = Extraction.decompose(value)(DefaultFormats)
    Printer.compact(render(jsonAst), new OutputStreamWriter(entityStream))
  }

  protected def convertFromJson(classType: Class[AnyRef], entityStream: InputStream) = {
    def parseAndExtract(json: String, classType: Class[_]): AnyRef =
      parse(json).extract(DefaultFormats, Manifest.classType(classType))

    val buf = new scala.collection.mutable.StringBuilder()
    Source.createBufferedSource(entityStream).getLines().foreach(buf.append)
    classType.cast(parseAndExtract(buf.toString(), classType))
  }
}