package templemore.liftjson.provider

import net.liftweb.json._
import io.Source
import java.io.{InputStream, OutputStream, OutputStreamWriter}

trait LiftJsonIntegration {

  protected def config: ProviderConfig

  protected def convertToJson(value: AnyRef,
                              entityStream: OutputStream,
                              transformerClass: Option[Class[_ <: JsonASTTransformer]]): Unit = {
    val transform = createTransform(transformerClass)_

    val jsonAst = transform(Extraction.decompose(value)(DefaultFormats))
    Printer.compact(render(jsonAst), new OutputStreamWriter(entityStream))
  }

  protected def convertFromJson(classType: Class[AnyRef],
                                entityStream: InputStream,
                                transformerClass: Option[Class[_ <: JsonASTTransformer]]): Either[MappingError, AnyRef] = {
    def extract(jsonAST: JValue, classType: Class[_]): AnyRef =
      jsonAST.extract(DefaultFormats, Manifest.classType(classType))

    val transform = createTransform(transformerClass)_

    val buf = new scala.collection.mutable.StringBuilder()
    Source.createBufferedSource(entityStream).getLines().foreach(buf.append)

    try {
      val jsonAST = transform(parse(buf.toString()))
      Right(classType.cast(extract(jsonAST, classType)))
    }
    catch {
      case e: MappingException => Left(MappingError(e))
    }
  }

  private def createTransform(transformerClass: Option[Class[_ <: JsonASTTransformer]])
                             (jsonAST: JValue): JValue = {
    val transformer = transformerClass.map(config.transformerFactory.transformer(_))
    transformer.map(_.transform(jsonAST)).getOrElse(jsonAST)
  }
}