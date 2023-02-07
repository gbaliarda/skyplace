import { useTranslation } from "next-export-i18n"
import { Bars3BottomLeftIcon } from "@heroicons/react/24/outline"
import Skeleton from "react-loading-skeleton"

interface Props {
  description?: string
}

const ProductDescription = ({ description }: Props) => {
  const { t } = useTranslation()

  return (
    <div className=" border-gray-200 rounded-2xl border pb-4 flex-col justify-between mb-8 bg-slate-50">
      <div className="flex text-xl px-4  py-2 border-b bg-white rounded-t-2xl items-center">
        <Bars3BottomLeftIcon className="w-6 h-6" />
        <span suppressHydrationWarning className="pl-2 font-semibold">
          {t("product.description")}
        </span>
      </div>
      {description === undefined ? (
        <div className="px-4 pt-4">
          <Skeleton count={4} />
        </div>
      ) : (
        <p className="mb-4 px-4 pt-4 break-words line-clamp-18">{description}</p>
      )}
    </div>
  )
}

export default ProductDescription
