import { EllipsisVerticalIcon } from "@heroicons/react/24/outline"
import { useTranslation } from "next-export-i18n"
import Link from "next/link"

interface Props {
  isOnSale: boolean
}

const ProductDropdown = ({ isOnSale }: Props) => {
  const { t } = useTranslation()

  return (
    <div className="dropdown">
      <label
        tabIndex={0}
        className="btn bg-white  hover:bg-slate-200 hover:border-slate-300 rounded-r-xl rounded-l-none border border-slate-300 py-2 px-2"
      >
        <EllipsisVerticalIcon className="w-6 h-6 text-black" />
      </label>
      {isOnSale ? (
        <ul
          tabIndex={0}
          className="dropdown-content menu mt-2 border border-gray-300 text-sm shadow bg-base-100 rounded-md"
        >
          <li>
            <Link href={""}>
              <span
                suppressHydrationWarning
                className="text-xs whitespace-nowrap hover:bg-gray-600 rounded-t-md hover:text-white transition-none py-1.5"
              >
                {t("product.updateSell")}
              </span>
            </Link>
          </li>
          <li>
            <Link href={""}>
              <span
                suppressHydrationWarning
                className="text-xs whitespace-nowrap hover:bg-gray-600 rounded-b-md hover:text-white transition-none py-2 pr-12"
              >
                {t("product.deleteSell")}
              </span>
            </Link>
          </li>
        </ul>
      ) : (
        <ul
          tabIndex={0}
          className="dropdown-content menu mt-2 border border-gray-300 text-sm shadow bg-base-100 rounded-md"
        >
          <li>
            <Link href={""}>
              <span
                suppressHydrationWarning
                className="text-xs whitespace-nowrap hover:bg-gray-600 rounded-t-md hover:text-white transition-none py-1.5"
              >
                {t("product.sell")}
              </span>
            </Link>
          </li>
          <li>
            <Link href={""}>
              <span
                suppressHydrationWarning
                className="text-xs whitespace-nowrap hover:bg-gray-600 rounded-b-md hover:text-white transition-none py-2 pr-12"
              >
                {t("product.delete")}
              </span>
            </Link>
          </li>
        </ul>
      )}
    </div>
  )
}

export default ProductDropdown
