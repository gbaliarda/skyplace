import { BaseSyntheticEvent, useState, useRef } from "react"
import { useTranslation } from "next-export-i18n"
import { TagIcon } from "@heroicons/react/24/outline"
import Swal from "sweetalert2"
import { sendJson, getResourceUrl } from "../../../services/endpoints"
import useSession from "../../../hooks/useSession"
import Sellorder from "../../../types/Sellorder"
import { useBuyOrders } from "../../../services/sellorders"
import { useCryptoPrice } from "../../../hooks/useCryptoPrice"
import { BuyordersURL } from "../../../services/users"

const ProductBuyOfferBox = ({ sellOrder }: { sellOrder: Sellorder }) => {
  const { t } = useTranslation()
  const { accessToken } = useSession()
  const priceRef = useRef<HTMLInputElement>(null)
  const [offer, setOffer] = useState(0)
  const [loading, setLoading] = useState(false)
  const url = { baseUrl: sellOrder.buyorders.toString() } as BuyordersURL
  const { refetchData } = useBuyOrders(url)

  const { price: ethPriceUsd } = useCryptoPrice("ethereum")

  const handleMakeOffer = async () => {
    if (!accessToken) return Swal.fire({ title: t("errors.notLoggedIn"), icon: "error" })

    setLoading(true)
    try {
      await sendJson("POST", `/sellorders/${sellOrder.id}/buyorders`, { price: offer }, accessToken)
      priceRef.current!!.value = "0"
      setOffer(0)
      refetchData(url) // FIXME: this will only work if the user is on the first page of offers
    } catch (e: any) {
      console.log(e.name, e.message)
      Swal.fire({ title: t("product.makeOfferError"), text: e.message, icon: "error" })
    }
    setLoading(false)
  }

  const updateOfferValue = (ev: BaseSyntheticEvent) => {
    const price = Number.parseFloat(ev.target.value)
    setOffer(Number.isNaN(price) ? 0 : price)
  }

  return (
    <div className=" border-gray-200 rounded-2xl border pb-4 flex-col justify-between mb-8 bg-slate-50">
      <div className="flex text-xl px-4  py-2 border-b bg-white rounded-t-2xl items-center">
        <TagIcon className="w-6 h-6" />
        <span suppressHydrationWarning className="pl-2 font-semibold">
          {t("product.buy")}
        </span>
      </div>
      <div className="mb-4 px-4 pt-4">
        <div>
          <label className="flex mb-4 items-center">
            <span suppressHydrationWarning className="text-[1.1rem] pr-2">
              {t("product.yourOffer")}
            </span>
            <img className="h-8 -ml-1" src={getResourceUrl("/product/eth.svg")} alt="Eth icon" />
            <input
              ref={priceRef}
              type="number"
              className="rounded-lg border-slate-300"
              min="0.000000000000000001"
              step="0.000000000000000001"
              defaultValue={offer}
              onChange={updateOfferValue}
            />
          </label>
          <button
            suppressHydrationWarning
            className="btn normal-case w-48 bg-cyan-600 hover:bg-cyan-800 text-white px-6 rounded-md border-0 disabled:bg-gray-100 disabled:text-gray-500"
            onClick={handleMakeOffer}
            disabled={loading}
          >
            {t("product.makeOffer")}
          </button>
          {ethPriceUsd && (
            <span id="offerDisplay" className="ml-4 text-slate-500 text-base">
              ~ {offer * ethPriceUsd} USD
            </span>
          )}
        </div>
      </div>
    </div>
  )
}

export default ProductBuyOfferBox
