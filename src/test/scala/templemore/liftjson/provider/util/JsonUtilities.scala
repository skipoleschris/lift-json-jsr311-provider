package templemore.liftjson.provider.util


trait JsonUtilities {

  protected def compact(s: String) =
    s.replaceAll("""\s:\s""", ":")
     .replaceAll("""\n""", "")
     .replaceAll("""\{[\s]+""", "{")
     .replaceAll(""",[\s]+""", ",")
     .replaceAll("""[\s]+\[[\s]+""", "[")
     .replaceAll("""[\s]+\]""", "]")
     .replaceAll("""[\s]+\}""", "}")
}