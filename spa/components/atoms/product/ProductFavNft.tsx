import { HeartIcon } from "@heroicons/react/24/outline"
import { BaseSyntheticEvent, useState } from "react"
import { fetcher } from "../../../services/endpoints"
import useSession from "../../../hooks/useSession"

interface Props {
  isFaved?: boolean
  nftId: number
  mutateFavs: () => void
  amountFavourites?: number
}

const ProductFavNft = ({ isFaved, nftId, mutateFavs, amountFavourites = 0 }: Props) => {
  const { userId, accessToken } = useSession()
  const [amountFavs, setAmountFavs] = useState(amountFavourites)

  const handleFavNft = (ev: BaseSyntheticEvent) => {
    ev.preventDefault()
    fetcher(`/users/${userId}/favorites/${nftId}`, {
      headers: {
        "Content-Type": "application/json; charset=UTF-8",
        Authorization: `Bearer ${accessToken}`,
      },
      method: isFaved ? "DELETE" : "PUT",
    }).then(() => {
      mutateFavs()
      setAmountFavs(amountFavs + (isFaved ? -1 : 1))
    })
  }

  return (
    <div className="flex items-center py-2 px-2 space-x-1 rounded-l-xl rounded-r-none border border-slate-300 bg-white">
      <HeartIcon
        className={`h-6 w-6 text-cyan-700 cursor-pointer ${
          isFaved ? "fill-current hover:fill-transparent" : "hover:fill-current"
        }`}
        onClick={handleFavNft}
      />
      <span className="pl-1 text-base text-black font-normal">{amountFavs}</span>
    </div>
  )
}

export default ProductFavNft
