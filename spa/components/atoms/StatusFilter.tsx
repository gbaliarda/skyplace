import { useState } from "react"
import { ArrowSmallDownIcon } from "@heroicons/react/24/outline"
import { useTranslation } from "next-export-i18n"
import { FilterType, SaleStatus } from "../../types/Filters"

interface Props {
  changeValueFilters: (filterType: FilterType, toAdd: boolean, saleStatus: SaleStatus) => void,
  status: SaleStatus[] | undefined
}

const StatusFilter = ({ changeValueFilters, status }: Props) => {
  const { t } = useTranslation()
  const [isClosed, setIsClosed] = useState(false)

  return (
    <div className={isClosed ? "text-gray-500" : ""}>
      <button
        className="flex cursor-pointer justify-between items-center p-5 w-full font-medium text-left border border-x-0 border-gray-200"
        onClick={() => setIsClosed(!isClosed)}
      >
        <span suppressHydrationWarning>{t("explore.status")}</span>
        <ArrowSmallDownIcon
          className={isClosed ? "w-6 h-6 shrink-0" : "w-6 h-6 shrink-0 rotate-180"}
        />
      </button>
      <div className={isClosed ? "hidden" : ""}>
        <div className="px-5 py-2 space-y-2 flex flex-col">
          <div>
            <input
              type="checkbox"
              className="w-5 h-5 border-gray-300 rounded mr-2 cursor-pointer"
              value="onSale"
              checked={status !== undefined && status.includes(SaleStatus.ONSALE)}
              onChange={(e) =>
                changeValueFilters(FilterType.STATUS, e.target.checked, SaleStatus.ONSALE)
              }
            />
            <label suppressHydrationWarning>{t("explore.onSale")}</label>
          </div>
          <div>
            <input
              type="checkbox"
              className="w-5 h-5 border-gray-300 rounded mr-2 cursor-pointer"
              value="notSale"
              checked={status !== undefined && status.includes(SaleStatus.NOTSALE)}
              onChange={(e) =>
                changeValueFilters(FilterType.STATUS, e.target.checked, SaleStatus.NOTSALE)
              }
            />
            <label suppressHydrationWarning>{t("explore.notOnSale")}</label>
          </div>
        </div>
      </div>
    </div>
  )
}

export default StatusFilter
