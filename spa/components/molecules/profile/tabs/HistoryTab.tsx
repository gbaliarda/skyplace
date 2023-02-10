import { useCallback, useState } from "react"
import { useTranslation } from "next-export-i18n"
import HistoryItem from "../../../atoms/HistoryItem"
import { PurchasesURL, usePurchases } from "../../../../services/purchases"
import Paginator from "../../Paginator"
import ErrorBox from "../../../atoms/ErrorBox"
import Loader from "../../../atoms/Loader"
import { api } from "../../../../services/endpoints"

interface Props {
  userId: number
}

export default function HistoryTab({ userId }: Props) {
  const { t } = useTranslation()
  const defaultURL = {
    baseUrl: `${api}/users/${userId}/purchases?page=1`,
  } as PurchasesURL
  const [url, setUrl] = useState<PurchasesURL>(defaultURL)
  const { purchases, links, totalPages, loading, error, refetchData } = usePurchases(url)
  const updateUrl = useCallback(
    (_url: string) => {
      setUrl({
        ...url,
        baseUrl: _url,
      })
    },
    [url],
  )

  if (error)
    return (
      <ErrorBox errorMessage={t("errors.errorLoadingTab")} retryAction={() => refetchData(url)} />
    )
  if (loading || purchases === undefined)
    return <Loader className="grow flex items-center justify-center mb-32" />

  return (
    <>
      {links && <Paginator links={links} updateUrl={updateUrl} amountPages={totalPages} />}
      <div className="flex flex-col gap-2 w-3/4 max-w-4xl self-center">
        {purchases.length > 0 ? (
          purchases.map((purchase) => {
            return <HistoryItem userId={userId ?? 0} purchase={purchase} key={purchase.id} />
          })
        ) : (
          <span suppressHydrationWarning className="text-center text-slay-700">
            {t("profile.noActivity")}
          </span>
        )}
      </div>
    </>
  )
}
