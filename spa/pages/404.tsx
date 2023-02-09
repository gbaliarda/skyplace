import { useTranslation } from "next-export-i18n"
import ErrorPage from "../components/molecules/ErrorPage"

export default function Custom404() {
  const { t } = useTranslation()

  return (
    <ErrorPage errorCode={404} errorTitle={t("404.pageNotFound")} errorDetail={t("404.check")} />
  )
}
