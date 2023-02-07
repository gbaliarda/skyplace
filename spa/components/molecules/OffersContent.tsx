import { useEffect, useState } from "react"
import { useTranslation } from "next-export-i18n"
import { QueueListIcon, ArrowPathIcon } from "@heroicons/react/24/outline"
import BuyorderCard from "../atoms/BuyorderCard"
import { useBuyOrders } from "../../services/sellorders"
import BuyOrdersPaginator from "../atoms/BuyOrdersPaginator"
import Spinner from "../atoms/Spinner"
import ErrorBox from "../atoms/ErrorBox"
import User from "../../types/User"

interface Props {
  buyOrdersUrl: string | undefined
  owner: User
}

const OffersContent = ({ buyOrdersUrl, owner }: Props) => {
  const { t } = useTranslation()
  const { mutate } = useBuyOrders(buyOrdersUrl)

  return (
    <div className="border-gray-200 rounded-2xl border pb-4 flex-col justify-between mb-8 bg-slate-50">
      <div className="flex text-xl px-4 justify-between py-2 border-b bg-white rounded-t-2xl items-center">
        <div className="flex">
          <QueueListIcon className="h-6 w-6" />
          <span suppressHydrationWarning className="pl-2 font-semibold">
            {t("product.offers")}
          </span>
        </div>
        <ArrowPathIcon className="h-6 w-6 cursor-pointer" onClick={() => mutate()} />
      </div>
      <div>
        <div className="flex flex-col px-4 pt-4 divide-y">
          <Content owner={owner} buyOrdersUrl={buyOrdersUrl} />
        </div>
      </div>
    </div>
  )
}

const Content = ({ buyOrdersUrl, owner }: Props) => {
  const { t } = useTranslation()
  const [page, setPage] = useState(1)
  const { buyorders, loading, error, mutate } = useBuyOrders(buyOrdersUrl, page)
  const [pageSize, setPageSize] = useState(0)

  useEffect(() => {
    if (pageSize === 0 && buyorders !== undefined && buyorders?.totalPages > 0)
      setPageSize(buyorders?.buyorders.length)
  }, [buyorders])

  if (buyOrdersUrl === undefined) return <span suppressHydrationWarning>{t("product.noSale")}</span>
  if (loading)
    return (
      <div suppressHydrationWarning className="flex flex-col gap-2 items-center text-lg">
        {t("product.loadingOffers")}
        <Spinner />
      </div>
    )
  if (error) return <ErrorBox errorMessage={t("errors.errorLoadingOffers")} retryAction={mutate} />
  if (!buyorders || buyorders.buyorders.length === 0)
    return <span suppressHydrationWarning>{t("product.noOffers")}</span>

  return (
    <>
      {buyorders.buyorders.map((value, index) => {
        return (
          <BuyorderCard
            buyorder={value}
            owner={owner}
            index={index + (page - 1) * pageSize}
            key={value.offeredBy.toString()}
            mutate={mutate}
          />
        )
      })}
      <BuyOrdersPaginator amountPages={buyorders.totalPages} page={page} setPage={setPage} />
    </>
  )
}

export default OffersContent
