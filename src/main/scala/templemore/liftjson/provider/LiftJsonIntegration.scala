package templemore.liftjson.provider

import net.liftweb.json._
import io.Source
import java.io.{InputStream, OutputStream, OutputStreamWriter}

private[provider] trait LiftJsonIntegration {

  protected def convertToJson(value: AnyRef, entityStream: OutputStream) = {
    val jsonAst = Extraction.decompose(value)(DefaultFormats)
    Printer.compact(render(jsonAst), new OutputStreamWriter(entityStream))
  }

  protected def convertFromJson(classType: Class[AnyRef],
                                entityStream: InputStream,
                                transformerClass: Option[Class[JsonASTTransformer]]) = {
    def extract(jsonAST: JValue, classType: Class[_]): AnyRef =
      jsonAST.extract(DefaultFormats, Manifest.classType(classType))

    val transform = transformIfPossible(transformerClass)_

    val buf = new scala.collection.mutable.StringBuilder()
    Source.createBufferedSource(entityStream).getLines().foreach(buf.append)

    val jsonAST = transform(parse(buf.toString()))
    classType.cast(extract(jsonAST, classType))
  }

  private def transformIfPossible(transformerClass: Option[Class[JsonASTTransformer]])
                                 (jsonAST: JValue): JValue = {
    val transformer = transformerClass.map(_.newInstance)
    transformer.map(_.transform(jsonAST)).getOrElse(jsonAST)
  }
}