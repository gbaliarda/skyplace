export function epochToIntlDate(epoch: number) {
  let locale = navigator.language.substring(0, 2)
  if (locale !== "es") locale = "en"
  const formatter = new Intl.RelativeTimeFormat(locale)
  const diffInSeconds = Math.trunc((new Date(epoch * 1000).getTime() - new Date().getTime()) / 1000)
  let output
  switch (true) {
    case Math.abs(diffInSeconds) > 60 * 60 * 24:
      const diffInDays = Math.trunc(diffInSeconds / (60 * 60 * 24))
      output = formatter.format(diffInDays, "days")
      break
    case Math.abs(diffInSeconds) > 60 * 60:
      const diffInHours = Math.trunc(diffInSeconds / (60 * 60))
      output = formatter.format(diffInHours, "hours")
      break
    case Math.abs(diffInSeconds) > 60:
      const diffInMinutes = Math.trunc(diffInSeconds / 60)
      output = formatter.format(diffInMinutes, "minutes")
      break
    default:
      output = formatter.format(diffInSeconds, "seconds")
      break
  }
  return output
}
