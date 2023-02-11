import { useTranslation } from "next-export-i18n"
import Link from "next/link"
import Navbar from "./Navbar"

interface Props {
  errorCode: number
  errorTitle: string
  errorDetail: string
}

const ErrorPage = ({ errorCode, errorTitle, errorDetail }: Props) => {
  const { t } = useTranslation()

  return (
    <div className="mx-5">
      <div className="h-screen flex flex-col">
        <div className="flex flex-col sm:flex-row justify-center items-center flex-grow sm:divide-x h-">
          <h1 className="text-8xl text-cyan-500 pr-5 text-bold">{errorCode}</h1>
          <div className="flex flex-col justify-start pl-5 gap-y-1">
            <h2 suppressHydrationWarning className="text-5xl text-bold font-bold">
              {errorTitle}
            </h2>
            <p suppressHydrationWarning className="text-xl text-bold font-light">
              {errorDetail}
            </p>
            <Link href="/">
              <a>
                <button
                  suppressHydrationWarning
                  type="button"
                  className="w-fit shadow-md mt-3 py-2 px-6 rounded-md transition duration-300 bg-cyan-600 text-white"
                >
                  {t("errors.goBack")}
                </button>
              </a>
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ErrorPage
