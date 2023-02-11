import { useTranslation } from "next-export-i18n"
import Skeleton from "react-loading-skeleton"
import { TagIcon, FaceFrownIcon } from "@heroicons/react/24/outline"
import Link from "next/link"
import Swal from "sweetalert2"
import Buyorder from "../../types/Buyorder"
import { BuyordersURL, useUserUrl } from "../../services/users"
import { fetcher, getResourceUrl } from "../../services/endpoints"
import { useSellorderUrl, usePendingBuyOrder } from "../../services/sellorders"
import useSession from "../../hooks/useSession"
import User from "../../types/User"

interface Props {
  buyorder: Buyorder
  index: number
  url: BuyordersURL
  refetchData: (url: BuyordersURL) => void
  owner?: User
}

const BuyorderCard = ({ buyorder, owner, index, url, refetchData }: Props) => {
  const { t } = useTranslation()
  const { user: offerer, loading, errors } = useUserUrl(buyorder.offeredBy.toString())
  const {
    sellorder,
    loading: sellOrderLoading,
    errors: sellOrderError,
  } = useSellorderUrl(buyorder.sellorder.toString())
  const { accessToken, userId } = useSession()
  const { pendingBuyOrder, mutate: mutatePendingBuyOrder } = usePendingBuyOrder(sellorder?.id)
  const { user: userPendingBuyOrder } = useUserUrl(pendingBuyOrder?.offeredBy?.toString())

  if (errors || sellOrderError) {
    return (
      <div suppressHydrationWarning className="flex gap-1 items-center my-2">
        <FaceFrownIcon className="w-6 h-6" />
        {t("product.errorLoadingBuyOrder")}
      </div>
    )
  }

  if (loading || !offerer || sellOrderLoading) {
    return (
      <div className="flex items-center justify-between py-3">
        <div className="flex flex-col">
          <div className="flex gap-1">
            <div className="w-6 h-6 -mt-1 mb-1">
              <Skeleton circle height="100%" />
            </div>
            <h2 className="font-semibold w-32">
              <Skeleton />
            </h2>
          </div>
          <p className="text-sm break-words line-clamp-3 w-48">
            <Skeleton />
          </p>
        </div>
        <div className="flex gap-1">
          <div className="w-6 h-6 -mt-1 mb-1">
            <Skeleton circle height="100%" />
          </div>
          <h2 className="font-semibold w-16">
            <Skeleton />
          </h2>
        </div>
      </div>
    )
  }

  const offererUrl = `/profile/${offerer.id}`
  const isUserOfferer = userId === offerer.id
  const isUserOwner = userId === owner?.id

  const handleRemoveOffer = async () => {
    try {
      await fetcher(`/sellorders/${sellorder!!.id}/buyorders/${offerer.id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${accessToken}` },
      })
      refetchData(url)
    } catch (errs: any) {
      Swal.fire({ title: t("errors.removeOffer"), text: errs[0].message, icon: "error" })
    }
  }

  const handleAcceptOffer = async () => {
    try {
      await fetcher(`/sellorders/${sellorder!!.id}/buyorders/${offerer.id}`, {
        method: "PUT",
        headers: { Authorization: `Bearer ${accessToken}` },
      })
      refetchData(url)
      mutatePendingBuyOrder()
    } catch (errs: any) {
      Swal.fire({ title: t("errors.acceptOffer"), text: errs[0].message, icon: "error" })
    }
  }

  return (
    <div className="flex items-center justify-between py-3">
      <div className="flex flex-col">
        <div className="flex gap-1">
          <TagIcon className="h-6 w-6" />
          <p suppressHydrationWarning className="font-semibold">
            {t("product.offer")}
            <span>#{index + 1}</span>
          </p>
        </div>
        <p suppressHydrationWarning className="text-sm break-words line-clamp-3 w-80">
          {t("product.by")}
          &nbsp;
          <Link href={offererUrl} className="text-cyan-600 hover:underline hover:text-cyan-800">
            <a className="text-cyan-600 hover:underline hover:text-cyan-800 cursor-pointer">
              {offerer?.username}
            </a>
          </Link>
        </p>
      </div>
      <div className="flex flex-col gap-1 items-end">
        <div className="flex">
          <img className="h-6 -ml-1" src={getResourceUrl("/product/eth.svg")} alt="Eth icon" />
          <span className="text-slate-700">{buyorder.amount}</span>
        </div>
        {isUserOfferer && userPendingBuyOrder?.id !== userId && (
          <div className="flex gap-2">
            <button
              suppressHydrationWarning
              onClick={handleRemoveOffer}
              className="btn btn-xs normal-case text-black font-medium  border rounded-md bg-white border-slate-400 hover:border-slate-600 hover:bg-slate-600 hover:text-white"
            >
              {t("product.removeOffer")}
            </button>
          </div>
        )}
        {isUserOwner && !pendingBuyOrder && (
          <div className="flex gap-2">
            <button
              suppressHydrationWarning
              onClick={handleAcceptOffer}
              className="py-0.5 px-3 text-sm border rounded-lg bg-cyan-600 text-white border-cyan-600 transition duration-300 hover:bg-cyan-900 hover:border-cyan-900 hover:shadow-md"
            >
              {t("product.accept")}
            </button>
            <button
              suppressHydrationWarning
              onClick={handleRemoveOffer}
              className="py-0.5 px-3 text-sm border rounded-lg bg-white border-slate-400 transition duration-300 hover:border-slate-600 hover:bg-slate-600 hover:text-white hover:shadow-md"
            >
              {t("product.reject")}
            </button>
          </div>
        )}
      </div>
    </div>
  )
}

export default BuyorderCard
