import { useTranslation } from "next-export-i18n"
import Link from "next/link"
import Buyorder from "../../types/Buyorder"
import { useSellorderUrl } from "../../services/sellorders"
import { useNftUrl } from "../../services/nfts"
import { useImageUrl } from "../../services/images"
import { useUserUrl } from "../../services/users"
import useSession from "../../hooks/useSession"

interface Props {
  buyorder: Buyorder
  status: string | undefined
}

export default function ProfileBuyorderCard({ buyorder, status }: Props) {
  const { t } = useTranslation()
  const { userId } = useSession()

  const {
    user: bidder,
    loading: loadingBidder,
    error: errorBidder,
  } = useUserUrl(buyorder.offeredBy.toString())
  const {
    sellorder,
    loading: loadingSellorder,
    error: errorSellorder,
  } = useSellorderUrl(buyorder.sellorder.toString())
  const { nft, loading: loadingNft, error: errorNft } = useNftUrl(sellorder?.nft.toString())
  const { img, loading: loadingImage, error: errorImage } = useImageUrl(nft?.image.toString())

  if (errorBidder) return <h1>Error loading Owner of nft</h1>
  if (loadingBidder) return <h1>Loading Owner of nftr...</h1>

  if (errorSellorder) return <h1>Error loading Sellorder of buyorder</h1>
  if (loadingSellorder) return <h1>Loading sellorder of buyorder...</h1>

  if (errorNft) return <h1>Error loading Nft of buyorder</h1>
  if (loadingNft) return <h1>Loading Nft of buyorder...</h1>

  if (errorImage) return <h1>Error loading Image of nft</h1>
  if (loadingImage) return <h1>Loading Image of nftr...</h1>

  const imageSrc = `data:image/jpg;base64,${img?.image.toString()}`
  const nftName = `${nft?.nftName} #${nft?.nftId}`
  const userIsBidder = bidder?.id === userId

  return (
    <Link href={`/product/${nft?.id}`} className="flex inset-0 absolute w-full gap-2">
      <div className="border-jacarta-100 rounded-2.5xl h-32 relative flex items-center border bg-white p-4 transition-shadow hover:shadow-lg z-0 cursor-pointer">
        <div className="flex flex-row items-center ml-4">
          <figure className="mr-5">
            <img
              src={imageSrc}
              className="w-[6rem] h-[6rem] rounded-lg aspect-square object-cover border border-gray-300"
              alt="avatar 2"
              loading="lazy"
            />
          </figure>

          <div className="max-w-[34rem]">
            <h3 className="font-display text-jacarta-700 mb-1 text-base font-semibold truncate">
              {nftName}
            </h3>
            <div suppressHydrationWarning className="text-jacarta-500 block text-sm">
              {!userIsBidder
                ? t("buyorders.forSale", { bidder: bidder?.username, amount: buyorder.amount })
                : t("buyorders.bidded", { amount: buyorder.amount })}
            </div>
            <div suppressHydrationWarning className="text-sm pt-3">
              {/*
                <c:when test="${param.isMySale == true}">
                  <spring:message code="buyoffer.pendingDate" arguments="${param.offerDate}" />
                </c:when>
                */}
              {status === "NEW" ? t("buyorders.pending") : t("buyorders.accepted")}
            </div>
          </div>
        </div>
        {/* Modals
      <c:if
        test="${param.isAdmin == true || (param.isMySale == false && param.isOwner != null && param.isOwner == true)}">
        <c:choose>
          <c:when test="${param.status == 'NEW'}">
            <button type="submit" onClick="openDeleteOfferModal(${param.sellOrderId}, ${param.buyerId})"
                    className="px-5 py-2 rounded-md text-white transition duration-300 shadow-md hover:shadow-xl bg-red-500 hover:bg-red-900 z-10 absolute right-8">
              <spring:message code="buyoffer.delete" />
            </button>
          </c:when>
          <c:otherwise>
            <button
              onClick="openConfirmBuyOfferModal('${param.buyerUsername}', '${param.buyerWallet}', '${param.sellerUsername}', '${param.sellerWallet}', '${param.nftName}', '${param.nftContractAddr}', ${param.price}, ${param.nftCollectionId}, ${param.productId}, ${param.sellOrderId}, ${param.buyerId})"
              className="px-5 py-2 rounded-md text-white transition duration-300 shadow-md hover:shadow-xl bg-cyan-600 hover:bg-cyan-800 z-10 absolute right-8">
              <spring:message code="buyoffer.confirm" />
            </button>
          </c:otherwise>
        </c:choose>
      </c:if>
      */}
      </div>
    </Link>
  )
}
