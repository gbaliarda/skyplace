import { useState } from "react"
import { ArrowSmallDownIcon } from "@heroicons/react/24/outline"
import { FilterType } from "../../types/Filters"
import { Chain } from "../../types/Nft"

interface Props {
  changeValueFilters: (filterType: FilterType, toAdd: boolean, category: Chain) => void
}

const ChainFilter = ({ changeValueFilters }: Props) => {
  const [isClosed, setIsClosed] = useState(false)

  return (
    <div className={isClosed ? "text-gray-500" : ""}>
      <button
        className="flex cursor-pointer justify-between items-center p-5 w-full font-medium text-left border border-x-0 border-gray-200"
        onClick={() => setIsClosed(!isClosed)}
      >
        <span>Chain</span>
        <ArrowSmallDownIcon
          className={isClosed ? "w-6 h-6 shrink-0" : "w-6 h-6 shrink-0 rotate-180"}
        />
      </button>
      <div className={isClosed ? "hidden" : ""}>
        <div className="px-5 py-2 space-y-2 flex flex-col">
          {Object.keys(Chain).map((chain) => (
            <div key={chain}>
              <input
                type="checkbox"
                className="w-5 h-5 border-gray-300 rounded mr-2 cursor-pointer"
                value={chain}
                onChange={(e) =>
                  changeValueFilters(
                    FilterType.CHAIN,
                    e.target.checked,
                    Chain[chain as keyof typeof Chain],
                  )
                }
              />
              <label>{chain}</label>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default ChainFilter
