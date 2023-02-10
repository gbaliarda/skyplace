import { EllipsisVerticalIcon } from "@heroicons/react/24/outline"
import { useTranslation } from "next-export-i18n"
import Link from "next/link"
import { useRouter } from "next/router"
import Swal from "sweetalert2"
import Nft from "../../../types/Nft"
import { useSellorderUrl } from "../../../services/sellorders"
import { fetcher } from "../../../services/endpoints"
import useSession from "../../../hooks/useSession"
import { useNft } from "../../../services/nfts"
import { useUserUrl } from "../../../services/users"

interface Props {
  nft: Nft
}

const ProductDropdown = ({ nft }: Props) => {
  const { t } = useTranslation()
  const router = useRouter()
  const { accessToken } = useSession()
  const { mutate } = useNft(nft.id)
  const { sellorder } = useSellorderUrl(nft.sellorder?.toString())
  const { user } = useUserUrl(nft.owner.toString())

  const isOnSale = nft.sellorder !== undefined

  const handleDeleteSellorder = async () => {
    try {
      const { isConfirmed } = await Swal.fire({
        title: t("product.deleteSellOrderConfirm"),
        text: t("product.deleteSellOrderInfo"),
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: t("product.removeOffer"),
        cancelButtonText: t("product.cancel"),
      })
      if (isConfirmed) {
        await fetcher(`/sellorders/${sellorder!!.id}`, {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        })
        Swal.fire({ title: t("product.deleteSellOrderSuccess"), icon: "success" })
        mutate()
      }
    } catch (e: any) {
      console.log(e.name, e.message)
      Swal.fire({ title: t("product.deleteSellOrderError"), text: e.message, icon: "error" })
    }
  }

  const handleDeleteNft = async () => {
    try {
      const { isConfirmed } = await Swal.fire({
        title: t("product.deleteNftConfirm"),
        text: t("product.deleteNftInfo"),
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: t("product.removeOffer"),
        cancelButtonText: t("product.cancel"),
      })
      if (isConfirmed) {
        await fetcher(`/nfts/${nft.id}`, {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        })
        await Swal.fire({ title: t("product.deleteNftSuccess"), icon: "success" })
        router.replace(`/profile/${user!!.id}`)
      }
    } catch (e: any) {
      console.log(e.name, e.message)
      Swal.fire({ title: t("product.deleteNftError"), text: e.message, icon: "error" })
    }
  }

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
            <Link
              href={`/sell/${nft.id}?update=${sellorder?.id}&category=${sellorder?.category}&price=${sellorder?.price}`}
            >
              <span
                suppressHydrationWarning
                className="text-xs whitespace-nowrap hover:bg-gray-600 rounded-t-md hover:text-white transition-none py-2"
              >
                {t("product.updateSell")}
              </span>
            </Link>
          </li>
          <li>
            <button
              onClick={handleDeleteSellorder}
              suppressHydrationWarning
              className="text-xs whitespace-nowrap hover:bg-gray-600 rounded-b-md hover:text-white transition-none py-2 pr-12"
            >
              {t("product.deleteSell")}
            </button>
          </li>
        </ul>
      ) : (
        <ul
          tabIndex={0}
          className="dropdown-content menu mt-2 border border-gray-300 text-sm shadow bg-base-100 rounded-md"
        >
          <li>
            <Link href={`/sell/${nft.id}`}>
              <span
                suppressHydrationWarning
                className="text-xs whitespace-nowrap hover:bg-gray-600 rounded-t-md hover:text-white transition-none py-2"
              >
                {t("product.sell")}
              </span>
            </Link>
          </li>
          <li>
            <button
              onClick={handleDeleteNft}
              suppressHydrationWarning
              className="text-xs whitespace-nowrap hover:bg-gray-600 rounded-b-md hover:text-white transition-none py-2 pr-12"
            >
              {t("product.delete")}
            </button>
          </li>
        </ul>
      )}
    </div>
  )
}

export default ProductDropdown
