package templemore.liftjson.provider.jsr311

import org.specs2.Specification


class Jsr311StatusAdapterSpec extends Specification { def is =

  "Specification for the Jsr-311 Status Adapter"                     ^
                                                                     endp^
  "A jsr-311 status adapter should"                                  ^
    "capture the response code for a bad request"                    ! badRequestCode^
    "capture the reason phrase for a bad request"                    ! badRequestPhrase^
    "capture the response code for an internal server error"         ! internalErrorCode^
    "caprtue the reason phrase for an internal server error"         ! internalErrorPhrase^
                                                                     end

  def badRequestCode = {
    Jsr311StatusAdapter.badRequest.statusCode must_== 400
  }

  def badRequestPhrase = {
    Jsr311StatusAdapter.badRequest.reasonPhrase must_== "Bad Request"
  }

  def internalErrorCode = {
    Jsr311StatusAdapter.internalServerError.statusCode must_== 500
  }

  def internalErrorPhrase = {
    Jsr311StatusAdapter.internalServerError.reasonPhrase must_== "Internal Server Error"
  }
}