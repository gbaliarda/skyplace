import { useCallback, useEffect, useState } from "react"
import { useRouter } from "next/router"
import { useTranslation } from "next-export-i18n"
import queryString from "query-string"
import { BuyordersURL, useUserBuyorders } from "../../../../services/users"
import ProfileBuysTab from "../ProfileBuysTab"
import Loader from "../../../atoms/Loader"
import ErrorBox from "../../../atoms/ErrorBox"
import { api } from "../../../../services/endpoints"

interface Props {
  userId: number | undefined
}

export default function BuyordersTab({ userId }: Props) {
  const { t } = useTranslation()
  const router = useRouter()
  const [page, setPage] = useState<number>()
  const [status, setStatus] = useState("ALL")
  const defaultURL = {
    baseUrl: `${api}/users/${userId}/buyorders?page=${page}`,
    status,
  } as BuyordersURL
  const [url, setUrl] = useState<BuyordersURL>(defaultURL)
  const { buyorders, links, totalPages, loading, error, refetchData } = useUserBuyorders(url)

  useEffect(() => {
    if (!router.isReady) return

    if (router.query.page)
      setPage(
        Array.isArray(router.query.page)
          ? parseInt(router.query.page[0])
          : parseInt(router.query.page),
      )
  }, [router.isReady, router.query.page])

  useEffect(() => {
    setStatus(
      Array.isArray(router.query.status) ? router.query.status[0] : router.query.status ?? "ALL",
    )
  }, [router.query.status])

  const updatePage = (pageNumber: string) => {
    const queryParams = new URLSearchParams(queryString.stringify(router.query))

    setPage(parseInt(pageNumber))
    queryParams.set("page", pageNumber)

    const newURL = `${router.pathname}?${queryParams.toString()}`

    router.push(newURL)
  }

  const buildBuyordersUrlFromUrl = (_url: string) => {
    const urlAux = new URL(_url)
    const params = urlAux.searchParams
    const statusParam = params.get("status")

    const buyordersUrl: BuyordersURL = {
      baseUrl: `${urlAux.origin}${urlAux.pathname}?page=${params.get("page")}`,
      status: statusParam != null ? statusParam : status,
    }

    return buyordersUrl
  }

  const applyStatus = (_status: string) => {
    const queryParams = new URLSearchParams(queryString.stringify(router.query))

    queryParams.set("status", _status)
    queryParams.set("page", "1")

    const newURL = `${router.pathname}?${queryParams.toString()}`

    router.push(newURL)
  }

  const updateUrl = useCallback(
    (_url: string) => {
      const params = new URL(_url).searchParams
      const pageNumber = params.get("page") ?? "1"
      updatePage(pageNumber)

      const buyordersUrl = buildBuyordersUrlFromUrl(_url)
      setUrl(buyordersUrl)
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [url],
  )

  const refetchBuyorders = () => {
    refetchData(defaultURL)
  }

  useEffect(() => {
    if (router.isReady && page === undefined && router.query.page === undefined) setPage(1)
    setUrl({
      baseUrl: `${api}/users/${userId}/buyorders?page=${page}`,
      status,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [status, page])

  if (error)
    return (
      <ErrorBox errorMessage={t("errors.errorLoadingTab")} retryAction={() => refetchData(url)} />
    )
  if (loading || buyorders === undefined)
    return <Loader className="grow flex items-center justify-center mb-32" />

  return (
    <ProfileBuysTab
      updateUrl={updateUrl}
      links={links ?? undefined}
      amountPages={totalPages}
      status={status}
      setStatus={applyStatus}
      buyorders={buyorders}
      updateBuyorders={refetchBuyorders}
    />
  )
}
