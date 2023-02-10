import { useCallback, useEffect, useState } from "react"
import { useTranslation } from "next-export-i18n"
import parse from "parse-link-header"
import Skeleton from "react-loading-skeleton"
import { QueueListIcon, ArrowPathIcon } from "@heroicons/react/24/outline"
import BuyorderCard from "../atoms/BuyorderCard"
import { useBuyOrders } from "../../services/sellorders"
import BuyOrdersPaginator from "../atoms/BuyOrdersPaginator"
import Spinner from "../atoms/Spinner"
import ErrorBox from "../atoms/ErrorBox"
import User from "../../types/User"
import Buyorder from "../../types/Buyorder"
import { BuyordersURL } from "../../services/users"

interface Props {
  buyOrdersUrl: string | undefined
  loadingData: boolean
  owner?: User
}

const OffersContent = ({ buyOrdersUrl, loadingData, owner }: Props) => {
  const defaultURL = {
    baseUrl: buyOrdersUrl ? `${buyOrdersUrl}?page=1` : "",
  } as BuyordersURL

  const [url, setUrl] = useState(defaultURL)
  const { buyorders, totalPages, links, loading, error, refetchData } = useBuyOrders(url)
  const { t } = useTranslation()
  const updateUrl = useCallback(
    (_url: string) => {
      setUrl({
        ...url,
        baseUrl: _url,
      })
    },
    [url],
  )

  useEffect(() => {
    if (!buyOrdersUrl) return
    updateUrl(buyOrdersUrl)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [buyOrdersUrl])

  return (
    <div className="border-gray-200 rounded-2xl border pb-4 flex-col justify-between mb-8 bg-slate-50">
      <div className="flex text-xl px-4 justify-between py-2 border-b bg-white rounded-t-2xl items-center">
        <div className="flex">
          <QueueListIcon className="h-6 w-6" />
          <span suppressHydrationWarning className="pl-2 font-semibold">
            {t("product.offers")}
          </span>
        </div>
        {!loadingData && !loading && (
          <ArrowPathIcon className="h-6 w-6 cursor-pointer" onClick={() => refetchData(url)} />
        )}
      </div>
      <div>
        <div className="flex flex-col px-4 pt-4 divide-y">
          {loadingData ? (
            <Skeleton count={5} className="h-8 my-2" />
          ) : (
            <Content
              owner={owner}
              buyOrdersUrl={buyOrdersUrl}
              buyorders={buyorders}
              totalPages={totalPages}
              refetchData={refetchData}
              error={error}
              url={url}
              updateUrl={updateUrl}
              loading={loading}
              links={links ?? undefined}
            />
          )}
        </div>
      </div>
    </div>
  )
}

interface ContentProps {
  buyOrdersUrl?: string
  buyorders?: Buyorder[]
  totalPages: number
  refetchData: (_url: BuyordersURL) => void
  error: any
  url: BuyordersURL
  updateUrl: (url: string) => void
  loading: boolean
  links?: parse.Links
  owner?: User
}

const Content = ({
  buyOrdersUrl,
  buyorders,
  totalPages,
  refetchData,
  error,
  url,
  updateUrl,
  loading,
  links,
  owner,
}: ContentProps) => {
  const { t } = useTranslation()
  const page = parseInt(
    new URL(
      links !== undefined && links.self !== undefined ? links.self.url : "javascript:void(0)",
    ).searchParams.get("page") ?? "0",
  )

  if (buyOrdersUrl === undefined) return <span suppressHydrationWarning>{t("product.noSale")}</span>
  if (loading)
    return (
      <div suppressHydrationWarning className="flex flex-col gap-2 items-center text-lg">
        {t("product.loadingOffers")}
        <Spinner />
      </div>
    )
  if (error)
    return (
      <ErrorBox
        errorMessage={t("errors.errorLoadingOffers")}
        retryAction={() => refetchData(url)}
      />
    )
  if (!loading && (!buyorders || buyorders.length === 0))
    return <span suppressHydrationWarning>{t("product.noOffers")}</span>

  return (
    <>
      {buyorders?.map((value, index) => {
        return (
          <BuyorderCard
            buyorder={value}
            owner={owner}
            index={index + (page - 1) * 5}
            key={value.offeredBy.toString()}
            url={url}
            refetchData={refetchData}
          />
        )
      })}
      {links !== undefined && (
        <BuyOrdersPaginator
          amountPages={totalPages}
          page={page}
          updateUrl={updateUrl}
          links={links}
        />
      )}
    </>
  )
}

export default OffersContent
