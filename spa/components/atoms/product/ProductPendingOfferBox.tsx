import { useTranslation } from "next-export-i18n"
import { TagIcon } from "@heroicons/react/24/outline"
import Link from "next/link"
import Buyorder from "../../../types/Buyorder"
import { useUserUrl } from "../../../services/users"
import Spinner from "../Spinner"
import { getResourceUrl } from "../../../services/endpoints"

interface Props {
  pendingBuyOrder: Buyorder
}

const ProductPendingOfferBox = ({ pendingBuyOrder }: Props) => {
  const { t } = useTranslation()
  const { user, loading } = useUserUrl(pendingBuyOrder.offeredBy.toString())

  return (
    <div className=" border-gray-200 rounded-2xl border flex-col justify-between mb-8 bg-slate-50">
      <div className="flex text-xl px-4 py-2 border-b bg-white rounded-t-2xl items-center">
        <TagIcon className="w-6 h-6" />
        <span suppressHydrationWarning className="pl-2 font-semibold">
          {t("product.pendingOffer")}
        </span>
      </div>
      <div className="flex items-center justify-between py-3 px-4">
        <div suppressHydrationWarning className="font-semibold flex items-center">
          <img
            className="w-10 h-10 rounded-full mr-3"
            src={getResourceUrl("/profile/profile_picture.png")}
            alt=""
          />
          {t("product.offerBy").toLowerCase()}
          {loading ? (
            <Spinner />
          ) : (
            <Link href={`/profile?id=${user!!.id}`}>
              <a className="ml-1 text-cyan-600 hover:underline hover:text-cyan-800 cursor-pointer">
                {user!!.username}
              </a>
            </Link>
          )}
        </div>
        <div className="flex items-center">
          <img className="h-8 -ml-1" src={getResourceUrl("/product/eth.svg")} alt="Eth icon" />
          <span className="text-slate-700">
            {pendingBuyOrder.amount.toLocaleString("en-US", {
              maximumFractionDigits: 20,
            })}
          </span>
        </div>
      </div>
    </div>
  )
}

export default ProductPendingOfferBox
