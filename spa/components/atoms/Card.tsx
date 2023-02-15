import { HeartIcon } from "@heroicons/react/24/outline"
import { BaseSyntheticEvent } from "react"
import Link from "next/link"
import Skeleton from "react-loading-skeleton"
import { useTranslation } from "next-export-i18n"
import Nft from "../../types/Nft"
import { useImageUrl } from "../../services/images"
import { useSellorderUrl } from "../../services/sellorders"
import { useUserUrl } from "../../services/users"
import useSession from "../../hooks/useSession"
import { fetcher, getResourceUrl } from "../../services/endpoints"
import ErrorBox from "./ErrorBox"

interface Props {
  nft: Nft
  mutateFavs?: () => void
  isFaved?: boolean
}

const Card = ({ nft, mutateFavs, isFaved }: Props) => {
  const { t } = useTranslation()
  const { userId, accessToken } = useSession()
  const {
    img: image,
    loading: loadingImage,
    errors: errorsImage,
    mutate: mutateImage,
  } = useImageUrl(nft?.image?.toString())
  const {
    sellorder,
    loading: loadingSellorder,
    errors: errorsSellorder,
    mutate: mutateSellorder,
  } = useSellorderUrl(nft?.sellorder?.toString())
  const {
    user: owner,
    loading: loadingOwner,
    errors: errorsOwner,
    mutate: mutateOwner,
  } = useUserUrl(nft?.owner?.toString())
  const handleFavNft = async (ev: BaseSyntheticEvent) => {
    ev.preventDefault()
    fetcher(`/users/${userId}/favorites/${nft.id}`, {
      headers: {
        "Content-Type": "application/json; charset=UTF-8",
        Authorization: `Bearer ${accessToken}`,
      },
      method: isFaved ? "DELETE" : "PUT",
    }).then(mutateFavs)
  }

  const reloadCard = () => {
    mutateImage()
    mutateSellorder()
    mutateOwner()
  }

  return errorsImage || errorsSellorder || errorsOwner ? (
    <div className="flex items-center justify-center w-80 h-80 rounded-lg group shadow-sm hover:shadow-xl max-w-xs border-slate-300 border">
      <ErrorBox errorMessage={t("errors.errorLoadingCard")} retryAction={reloadCard} />
    </div>
  ) : (
    <Link
      href={`/product?id=${nft.id}`}
      className="hover:underline decoration-2 decoration-cyan-500 underline-offset-4"
    >
      <a>
        <div className="relative rounded-lg group shadow-sm hover:shadow-xl w-full max-w-xs cursor-pointer">
          {loadingImage ? (
            <Skeleton className="aspect-[4/3] w-80 h-60 rounded-t-lg object-center object-cover group-hover:opacity-80 border-slate-300 border-x border-t" />
          ) : (
            <img
              src={`data:image/jpg;base64,${image?.image.toString()}`}
              alt=""
              className="aspect-[4/3] w-80 h-60 rounded-t-lg object-center object-cover group-hover:opacity-80 border-slate-300 border-x border-t"
            />
          )}

          {nft.sellorder !== undefined && loadingSellorder ? (
            <div className="pt-4 px-4 border-x border-slate-300 space-x-8 w-full h-full flex justify-between">
              <Skeleton className="w-32 h-7" />
              <Skeleton className="w-14 h-7" />
            </div>
          ) : (
            <div className="pt-4 px-4 flex items-center justify-between text-base font-medium text-gray-900 border-x border-slate-300 space-x-8">
              <h3 className="max-w-[20ch] truncate text-lg">{`${nft.nftName} #${nft.nftId}`}</h3>
              {sellorder !== undefined ? (
                <div className="flex items-center">
                  <img className="h-8" src={getResourceUrl("/product/eth.svg")} alt="Eth icon" />
                  <span>{sellorder.price}</span>
                </div>
              ) : (
                <div className="flex h-8 items-center">
                  <span suppressHydrationWarning className="min-w-max">{t("explore:notOnSale")}</span>
                </div>
              )}
            </div>
          )}

          {nft.sellorder !== undefined && (loadingOwner || loadingSellorder) ? (
            <div className="pt-1 pb-3 px-4 rounded-b-lg text-slate-500 border-x border-b border-gray-300 w-full h-full flex justify-between">
              <Skeleton className="w-24 h-4" />
              <Skeleton className="w-24 h-4" />
            </div>
          ) : (
            <p className="flex justify-between pt-1 pb-4 px-4 text-sm rounded-b-lg text-slate-500 border-x border-b border-gray-300">
              <span className="max-w-[40ch] inline-block truncate">{owner?.username}</span>
              {nft.sellorder !== undefined && (
                <span className="min-w-max">{t(`categories.${sellorder?.category}`)}</span>
              )}
            </p>
          )}

          {userId !== null && isFaved !== undefined && (
            <div className="absolute top-4 right-4 hidden group-hover:block bg-white p-2 rounded-full cursor-pointer border shadow border-gray-300">
              <button type="submit" className="flex">
                {isFaved ? (
                  <HeartIcon
                    className="h-6 w-6 text-cyan-700 fill-current hover:fill-transparent"
                    onClick={handleFavNft}
                  />
                ) : (
                  <HeartIcon
                    className="h-6 w-6 text-cyan-700 hover:fill-current"
                    onClick={handleFavNft}
                  />
                )}
              </button>
            </div>
          )}
        </div>
      </a>
    </Link>
  )
}

export default Card
