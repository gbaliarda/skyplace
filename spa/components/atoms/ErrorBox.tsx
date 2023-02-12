import { FaceFrownIcon } from "@heroicons/react/24/outline"
import { useTranslation } from "next-export-i18n"
import { BaseSyntheticEvent } from "react"

interface Props {
  errorMessage: string
  retryAction?: () => void
}

const ErrorBox = ({ errorMessage, retryAction }: Props) => {
  const { t } = useTranslation()

  const handleRetry = (ev: BaseSyntheticEvent) => {
    ev.preventDefault()
    if (retryAction !== undefined) retryAction()
  }

  return (
    <div className="flex flex-col gap-2 items-center">
      <FaceFrownIcon className="w-8 h-8" />
      <span suppressHydrationWarning className="text-md text-center px-4">
        {errorMessage}
      </span>
      {retryAction !== undefined && (
        <button
          suppressHydrationWarning
          className="btn normal-case font-light w-32 bg-cyan-600 hover:bg-cyan-800 text-white px-6 rounded-md border-0"
          onClick={handleRetry}
        >
          {t("common.retry")}
        </button>
      )}
    </div>
  )
}

export default ErrorBox
