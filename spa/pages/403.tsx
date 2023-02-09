import { useTranslation } from "next-export-i18n"
import ErrorPage from "../components/molecules/ErrorPage"

export default function Custom403() {
  const { t } = useTranslation()
  return (
    <ErrorPage
      errorCode={403}
      errorTitle={t("403.forbidden")}
      errorDetail={t("403.noPermission")}
    />
  )
}
