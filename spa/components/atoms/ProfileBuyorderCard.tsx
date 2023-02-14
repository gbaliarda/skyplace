import { useTranslation } from "next-export-i18n"
import Skeleton from "react-loading-skeleton"
import Link from "next/link"
import Swal from "sweetalert2"
import { useRouter } from "next/router"
import Buyorder from "../../types/Buyorder"
import { useSellorderUrl } from "../../services/sellorders"
import { useNftUrl } from "../../services/nfts"
import { useImageUrl } from "../../services/images"
import { useUserUrl } from "../../services/users"
import useSession from "../../hooks/useSession"
import ErrorBox from "./ErrorBox"
import ConfirmTransactionModal from "./product/ConfirmTransactionModal"
import { fetcher, sendJson } from "../../services/endpoints"

interface Props {
  buyorder: Buyorder
  updateBuyorders: () => void
}

export default function ProfileBuyorderCard({ buyorder, updateBuyorders }: Props) {
  const { t } = useTranslation()
  const { userId, accessToken } = useSession()
  const router = useRouter()

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
  const { user: seller, mutate: mutateSeller } = useUserUrl(nft?.owner.toString())
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
    mutateSeller()
    mutateSellorder()
  }

  const handleConfirmPendingOffer = async (txHash: string) => {
    // do not handle the error here, it will be handled in ConfirmTransactionModal
    await sendJson(
      "POST",
      `/sellorders/${sellorder?.id}/buyorders/${bidder?.id}`,
      { txHash },
      accessToken!!,
    )
    await router.push(`/api/product?id=${nft?.id}`)
  }

  const handleRemoveOffer = async (e: any) => {
    e.preventDefault()
    e.stopPropagation()
    try {
      if (userId === bidder?.id) {
        const { isConfirmed } = await Swal.fire({
          title: t("product.confirmRemoveOffer"),
          text: t("product.deleteNftInfo"),
          icon: "warning",
          showCancelButton: true,
        })
        if (!isConfirmed) return
      }
      await fetcher(`/sellorders/${sellorder!!.id}/buyorders/${bidder?.id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${accessToken}` },
      })
      await updateBuyorders()
      Swal.fire({
        title: t("buyorders.offerDeleted"),
        text: t("buyorders.offerDeletedText"),
        icon: "success",
      })
    } catch (errs: any) {
      console.log(errs)
      Swal.fire({ title: t("errors.removeOffer"), text: errs[0].message, icon: "error" })
    }
  }

  const imageSrc = img !== undefined ? `data:image/jpg;base64,${img.image.toString()}` : undefined
  const nftName = `${nft?.nftName} #${nft?.nftId}`
  const userIsBidder = bidder?.id === userId

  const ignoreLink = (e: any) => {
    e.stopPropagation()
  }

  return (
    <>
      <ConfirmTransactionModal
        userPendingBuyOrder={bidder}
        owner={seller}
        product={nft}
        price={buyorder.amount}
        handleConfirm={handleConfirmPendingOffer}
      />
      <Link href={`/product?id=${nft?.id}`} className="flex inset-0 absolute w-full gap-2">
        <a>
          <div className="border-jacarta-100 rounded-2.5xl h-32 relative flex items-center border bg-white p-4 transition-shadow hover:shadow-lg z-0 cursor-pointer">
            <div className="flex flex-row grow items-center ml-4">
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
                <div className="grow">
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
                          <Link href={`/profile?id=${bidder?.id}`}>
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
                  <div className="flex flex-row justify-between items-center w-full">
                    <p suppressHydrationWarning className="text-sm pt-3 text-cyan-600">
                      {buyorder.status === "NEW" && (
                        <span className="text-slate-500">{t("buyorders.pending")}</span>
                      )}
                      {buyorder.status === "PENDING" && (
                        <span className="text-yellow-600">{t("buyorders.accepted")}</span>
                      )}
                    </p>
                    {buyorder.status === "PENDING" && (
                      /* eslint-disable */
                      <label
                        className="btn normal-case border-0 px-4 py-2 bg-cyan-600 rounded-lg flex text-white w-max"
                        suppressHydrationWarning
                        htmlFor="my-modal"
                        onClick={ignoreLink}
                      >
                        {t("buyorders.confirmOffer")}
                      </label>
                    )}
                    {buyorder.status === "NEW" && (
                      <label
                        className="btn normal-case border-0 px-4 py-2 bg-red-600 rounded-lg flex text-white w-max"
                        suppressHydrationWarning
                        onClick={handleRemoveOffer}
                      >
                        {t("buyorders.deleteOffer")}
                      </label>
                    )}
                  </div>
                </div>
              )}
            </div>
          </div>
        </a>
      </Link>
    </>
  )
}
