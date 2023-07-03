import { useCallback, useEffect, useState } from "react"
import { useRouter } from "next/router"
import queryString from "query-string"
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
  const router = useRouter()
  const [page, setPage] = useState<number>()
  const defaultURL = {
    baseUrl: `${api}/users/${userId}/purchases?page=${page}`,
  } as PurchasesURL
  const [url, setUrl] = useState<PurchasesURL>(defaultURL)
  const { purchases, links, totalPages, loading, errors, refetchData } = usePurchases(url)

  useEffect(() => {
    if (!router.isReady) return

    if (router.query.page)
      setPage(
        Array.isArray(router.query.page)
          ? parseInt(router.query.page[0])
          : parseInt(router.query.page),
      )
  }, [router.isReady, router.query.page])

  const updatePage = (pageNumber: string) => {
    const queryParams = new URLSearchParams(queryString.stringify(router.query))

    setPage(parseInt(pageNumber))
    queryParams.set("page", pageNumber)

    const newURL = `${router.pathname}?${queryParams.toString()}`

    router.push(newURL)
  }

  const buildPurchasesUrlFromUrl = (_url: string) => {
    const urlAux = new URL(_url)
    const params = urlAux.searchParams

    const purchasesUrl: PurchasesURL = {
      baseUrl: `${urlAux.origin}${urlAux.pathname}?page=${params.get("page")}`,
    }

    return purchasesUrl
  }

  const updateUrl = useCallback(
    (_url: string) => {
      const params = new URL(_url).searchParams
      const pageNumber = params.get("page") ?? "1"
      updatePage(pageNumber)

      const purchasesUrl = buildPurchasesUrlFromUrl(_url)
      setUrl(purchasesUrl)
    },
    [url],
  )

  useEffect(() => {
    if (router.isReady && page === undefined && router.query.page === undefined) setPage(1)
    setUrl({
      ...url,
      baseUrl: `${api}/users/${userId}/purchases?page=${page}`,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page])

  if (errors)
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
