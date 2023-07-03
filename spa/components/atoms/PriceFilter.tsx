import { useState, useRef } from "react"
import { ArrowSmallDownIcon } from "@heroicons/react/24/outline"
import { useTranslation } from "next-export-i18n"

interface Props {
  applyPriceFilter: (minPrice: number, maxPrice: number) => void
  minPrice: number | undefined
  maxPrice: number | undefined
}

const PriceFilter = ({ applyPriceFilter, minPrice, maxPrice }: Props) => {
  const { t } = useTranslation()
  const [isClosed, setIsClosed] = useState(false)
  const filterMinPriceRef = useRef<HTMLInputElement>(null)
  const filterMaxPriceRef = useRef<HTMLInputElement>(null)

  return (
    <div className={isClosed ? "text-gray-500" : ""}>
      <button
        className="flex cursor-pointer justify-between items-center p-5 w-full font-medium text-left border border-x-0 border-gray-200"
        onClick={() => setIsClosed(!isClosed)}
      >
        <span suppressHydrationWarning>{t("explore.price")}</span>
        <ArrowSmallDownIcon
          className={isClosed ? "w-6 h-6 shrink-0" : "w-6 h-6 shrink-0 rotate-180"}
        />
      </button>
      <div className={isClosed ? "hidden" : ""}>
        <div className="flex items-end">
          <div className="px-2 w-full group">
            <label className="font-mono font-bold w-1/3 text-[11px] text-gray-300 bg-white relative px-1  top-2 left-3 group-focus-within:text-black ">
              Min
            </label>
            <input
              type="number"
              min="0"
              step=".000000000000000001"
              className="h-8 text-10  bg-gray-50 border py-55-rem border-gray-300 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
              placeholder="Min"
              defaultValue={minPrice}
              ref={filterMinPriceRef}
            />
          </div>
          <span suppressHydrationWarning className="pb-1">
            {t("explore.to")}
          </span>
          <div className="px-2 w-full group">
            <label className="font-mono font-bold w-1/3 text-[11px]  text-gray-300  bg-white relative px-1  top-2 left-3 group-focus-within:text-black ">
              Max
            </label>
            <input
              type="number"
              min="0"
              step=".000000000000000001"
              className="h-8 text-10  bg-gray-50 border py-55-rem border-gray-300 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
              placeholder="Max"
              defaultValue={maxPrice}
              ref={filterMaxPriceRef}
            />
          </div>
        </div>
        <button
          suppressHydrationWarning
          type="submit"
          className="rounded-lg flex px-4 py-1 mx-auto mt-4 cursor-pointer text-white bg-cyan-600 hover:bg-cyan-700"
          onClick={() =>
            applyPriceFilter(
              parseInt(filterMinPriceRef.current?.value ?? "0"),
              parseInt(filterMaxPriceRef.current?.value ?? "0"),
            )
          }
        >
          {t("explore.apply")}
        </button>
      </div>
    </div>
  )
}

export default PriceFilter
