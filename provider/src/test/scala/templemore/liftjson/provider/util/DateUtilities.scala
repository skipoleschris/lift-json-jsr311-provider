package templemore.liftjson.provider.util

import java.util.{Date, Calendar, TimeZone}
import java.text.SimpleDateFormat

trait DateUtilities {

  protected def makeDate(day: Int, month: Int, year: Int, timezone: TimeZone = TimeZone.getDefault) = {
    val cal = Calendar.getInstance()
    cal.setTimeZone(timezone)
    cal.set(year, month - 1, day, 0, 0, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.getTime
  }

  protected def asIsoString(date: Date) = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    sdf.format(date)
  }
}