import { useTranslation } from "next-export-i18n"
import ErrorPage from "../components/molecules/ErrorPage"

export default function Custom400() {
  const { t } = useTranslation()

  return (
    <ErrorPage
      errorCode={400}
      errorTitle={t("400.badRequest")}
      errorDetail={t("400.requestInfo")}
    />
  )
}
