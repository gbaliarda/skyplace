import { useTranslation } from "next-export-i18n"
import { ListBulletIcon } from "@heroicons/react/24/outline"
import Skeleton from "react-loading-skeleton"

interface Props {
  loading: boolean
  contractAddr?: string
  category?: string
  chain?: string
  tokenId?: number
}

const ProductDetails = ({ loading, contractAddr, category, chain, tokenId }: Props) => {
  const { t } = useTranslation()

  return (
    <div className=" border-gray-200 rounded-2xl border pb-4 flex-col justify-between mb-8 bg-slate-50">
      <div className="flex text-xl px-4  py-2 border-b bg-white rounded-t-2xl items-center">
        <ListBulletIcon className="w-6 h-6" />
        <span suppressHydrationWarning className="pl-2 font-semibold">
          {t("product.details")}
        </span>
      </div>
      {loading ? (
        <div className="px-4 pt-4">
          <Skeleton count={5} />
        </div>
      ) : (
        <div className="flex flex-col gap-2 px-4 pt-4">
          <div className="flex justify-between">
            <p suppressHydrationWarning className="font-medium">
              {t("product.contractAddress")}
            </p>
            <p className="w-3/5 break-words max-h-20 overflow-hidden text-right font-light">
              {contractAddr}
            </p>
          </div>
          <div className="flex justify-between">
            <p className="font-medium">Token ID</p>
            <p className="w-3/5 overflow-hidden text-right font-light">{tokenId}</p>
          </div>
          {category !== undefined && (
            <div className="flex justify-between">
              <p suppressHydrationWarning className="font-medium">
                {t("product.category")}
              </p>
              <p className="font-light">{t(`categories.${category}`)}</p>
            </div>
          )}
          <div className="flex justify-between">
            <p className="font-medium">Blockchain</p>
            <p className="font-light">{chain}</p>
          </div>
        </div>
      )}
    </div>
  )
}

export default ProductDetails
