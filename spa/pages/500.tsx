import { useTranslation } from "next-export-i18n"
import ErrorPage from "../components/molecules/ErrorPage"

export default function Custom500() {
  const { t } = useTranslation()

  return (
    <ErrorPage errorCode={500} errorTitle={t("500.internalError")} errorDetail={t("500.detail")} />
  )
}
