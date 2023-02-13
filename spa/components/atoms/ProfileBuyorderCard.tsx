import { useTranslation } from "next-export-i18n"
import Skeleton from "react-loading-skeleton"
import Link from "next/link"
import Buyorder from "../../types/Buyorder"
import { useSellorderUrl } from "../../services/sellorders"
import { useNftUrl } from "../../services/nfts"
import { useImageUrl } from "../../services/images"
import { useUserUrl } from "../../services/users"
import useSession from "../../hooks/useSession"
import { getResourceUrl } from "../../services/endpoints"
import ErrorBox from "./ErrorBox"

interface Props {
  buyorder: Buyorder
}

export default function ProfileBuyorderCard({ buyorder }: Props) {
  const { t } = useTranslation()
  const { userId } = useSession()

  const {
    user: bidder,
    loading: loadingBidder,
    errors: errorsBidder,
    mutate: mutateBidder,
  } = useUserUrl(buyorder.offeredBy.toString())
  const {
    sellorder,
    loading: loadingSellorder,
    errors: errorsSellorder,
    mutate: mutateSellorder,
  } = useSellorderUrl(buyorder.sellorder.toString())
  const {
    nft,
    loading: loadingNft,
    errors: errorsNft,
    mutate: mutateNft,
  } = useNftUrl(sellorder?.nft.toString())
  const {
    img,
    loading: loadingImage,
    errors: errorsImage,
    mutate: mutateImage,
  } = useImageUrl(nft?.image.toString())

  const reloadContent = () => {
    mutateBidder()
    mutateImage()
    mutateNft()
    mutateSellorder()
  }

  const imageSrc =
    img !== undefined ? getResourceUrl(`data:image/jpg;base64,${img.image.toString()}`) : undefined
  const nftName = `${nft?.nftName} #${nft?.nftId}`
  const userIsBidder = bidder?.id === userId

  return (
    <Link href={`/product/${nft?.id}`} className="flex inset-0 absolute w-full gap-2">
      <a>
        <div className="border-jacarta-100 rounded-2.5xl h-32 relative flex items-center border bg-white p-4 transition-shadow hover:shadow-lg z-0 cursor-pointer">
          <div className="flex flex-row items-center ml-4">
            <figure className="mr-5">
              {loadingImage ? (
                <Skeleton className="w-[6rem] h-[6rem] rounded-lg" />
              ) : errorsImage ? (
                <div className="w-[6rem] h-[6rem] flex justify-center items-center rounded-lg border border-gray-300">
                  <ErrorBox errorMessage={""} />
                </div>
              ) : (
                <img
                  src={imageSrc}
                  className="w-[6rem] h-[6rem] rounded-lg aspect-square object-cover border border-gray-300"
                  alt="avatar 2"
                  loading="lazy"
                />
              )}
            </figure>

            {errorsBidder || errorsSellorder || errorsNft ? (
              <div>
                <ErrorBox
                  errorMessage={t("errors.errorLoadingBuyOrderCard")}
                  retryAction={reloadContent}
                />
              </div>
            ) : (
              <div className="max-w-[34rem]">
                {loadingSellorder || loadingNft ? (
                  <Skeleton className="w-36 h-5 mb-1" />
                ) : (
                  <h3 className="font-display text-jacarta-700 mb-1 text-base font-semibold truncate">
                    {nftName}
                  </h3>
                )}
                {loadingBidder ? (
                  <Skeleton className="w-48" />
                ) : (
                  <div suppressHydrationWarning className="text-jacarta-500 block text-sm">
                    {!userIsBidder ? (
                      <p>
                        <Link href={`/profile/${bidder?.id}`}>
                          <a className="text-cyan-600 hover:text-cyan-800 hover:underline cursor-pointer">
                            {bidder?.username}
                          </a>
                        </Link>
                        {t("buyorders.forSale", {
                          bidder: bidder?.username,
                          amount: buyorder.amount,
                        })}
                      </p>
                    ) : (
                      t("buyorders.bidded", { amount: buyorder.amount })
                    )}
                  </div>
                )}
                <p suppressHydrationWarning className="text-sm pt-3 text-cyan-600">
                  {buyorder.status === "NEW" && (
                    <span className="text-slate-500">{t("buyorders.pending")}</span>
                  )}
                  {buyorder.status === "PENDING" && (
                    <span className="text-yellow-600">{t("buyorders.accepted")}</span>
                  )}
                </p>
              </div>
            )}
          </div>
        </div>
      </a>
    </Link>
  )
}
