import { useState } from "react"
import { ArrowSmallDownIcon } from "@heroicons/react/24/outline"
import { useTranslation } from "next-export-i18n"
import { FilterType } from "../../types/Filters"
import { Category } from "../../types/Nft"

interface Props {
  changeValueFilters: (filterType: FilterType, toAdd: boolean, category: Category) => void
  categories: Category[] | undefined
}

const CategoryFilter = ({ changeValueFilters, categories }: Props) => {
  const { t } = useTranslation()
  const [isClosed, setIsClosed] = useState(false)

  return (
    <div className={isClosed ? "text-gray-500" : ""}>
      <button
        className="flex cursor-pointer justify-between items-center p-5 w-full font-medium text-left border border-x-0 border-gray-200"
        onClick={() => setIsClosed(!isClosed)}
      >
        <span suppressHydrationWarning>{t("explore.category")}</span>
        <ArrowSmallDownIcon
          className={isClosed ? "w-6 h-6 shrink-0" : "w-6 h-6 shrink-0 rotate-180"}
        />
      </button>
      <div className={isClosed ? "hidden" : ""}>
        <div className="px-5 py-2 space-y-2 flex flex-col">
          {Object.keys(Category).map((category) => (
            <div key={category}>
              <input
                type="checkbox"
                className="w-5 h-5 border-gray-300 rounded mr-2 cursor-pointer"
                value={category}
                checked={categories !== undefined && categories.includes(category as Category)}
                onChange={(e) =>
                  changeValueFilters(
                    FilterType.CATEGORY,
                    e.target.checked,
                    Category[category as keyof typeof Category],
                  )
                }
              />
              <label suppressHydrationWarning>{t(`categories.${category}`)}</label>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default CategoryFilter
